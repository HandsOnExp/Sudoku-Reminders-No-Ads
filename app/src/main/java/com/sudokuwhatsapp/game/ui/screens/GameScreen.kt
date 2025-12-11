package com.sudokuwhatsapp.game.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sudokuwhatsapp.game.data.models.Difficulty
import com.sudokuwhatsapp.game.game.GameViewModel
import com.sudokuwhatsapp.game.reminders.ReminderManager
import com.sudokuwhatsapp.game.ui.components.NumberPad
import com.sudokuwhatsapp.game.ui.components.SudokuGrid
import com.sudokuwhatsapp.game.ui.theme.SudokuWhatsAppTheme

/**
 * Main game screen with full Sudoku interaction
 *
 * @param difficulty The difficulty level to start
 * @param onNavigateBack Callback to navigate back
 * @param viewModel The game view model
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    difficulty: Difficulty = Difficulty.MEDIUM,
    onNavigateBack: () -> Unit = {},
    viewModel: GameViewModel = viewModel()
) {
    // Reminder manager
    val context = LocalContext.current
    val reminderManager: ReminderManager = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as android.app.Application
        )
    )

    val currentReminder by reminderManager.currentReminder.collectAsState()
    val reminderQueue by reminderManager.reminderQueue.collectAsState()

    // Exit confirmation dialog state
    var showExitDialog by remember { mutableStateOf(false) }

    // Start/stop reminders with game lifecycle
    DisposableEffect(Unit) {
        reminderManager.startReminders()
        onDispose {
            reminderManager.stopAllReminders()
        }
    }

    // Collect state from ViewModel
    val board by viewModel.board.collectAsState()
    val selectedCell by viewModel.selectedCell.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val isGameWon by viewModel.isGameWon.collectAsState()
    val mistakes by viewModel.mistakes.collectAsState()
    val isGameOver by viewModel.isGameOver.collectAsState()
    val wrongFlash by viewModel.wrongNumberFlash.collectAsState()

    // Start new game if board is null
    if (board == null) {
        viewModel.startNewGame(difficulty)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = board?.difficulty?.hebrewName ?: "",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = formatTime(elapsedSeconds),
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isPaused) {
                                viewModel.resumeGame()
                            } else {
                                viewModel.pauseGame()
                            }
                        }
                    ) {
                        if (isPaused) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Resume"
                            )
                        } else {
                            Text(
                                text = "⏸",
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Pause overlay or game content
                if (isPaused && !isGameWon && !isGameOver) {
                    PausedOverlay()
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Mistakes counter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "טעויות: $mistakes/${GameViewModel.MAX_MISTAKES}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (mistakes >= 2) MaterialTheme.colorScheme.error
                                       else MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Sudoku Grid
                        board?.let { currentBoard ->
                            // Get the number in the selected cell for highlighting
                            val highlightNumber = selectedCell?.let { (row, col) ->
                                currentBoard.cells[row][col].value
                            } ?: 0

                            SudokuGrid(
                                board = currentBoard,
                                selectedCell = selectedCell,
                                wrongFlash = wrongFlash,
                                highlightNumber = highlightNumber,
                                onCellClick = { row, col ->
                                    viewModel.selectCell(row, col)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Number Pad
                        val remainingNumbers = remember(board) {
                            viewModel.getRemainingNumbers()
                        }
                        NumberPad(
                            onNumberClick = { number ->
                                viewModel.inputNumber(number)
                            },
                            onClearClick = {
                                viewModel.clearCell()
                            },
                            remainingNumbers = remainingNumbers
                        )
                    }
                }
            }
        }

        // Win dialog
        if (isGameWon) {
            WinDialog(
                time = formatTime(elapsedSeconds),
                difficulty = board?.difficulty?.hebrewName ?: "",
                onDismiss = onNavigateBack,
                onNewGame = {
                    board?.difficulty?.let { viewModel.startNewGame(it) }
                }
            )
        }

        // Game Over dialog
        if (isGameOver) {
            GameOverDialog(
                mistakes = mistakes,
                onDismiss = onNavigateBack,
                onNewGame = {
                    board?.difficulty?.let { viewModel.startNewGame(it) }
                }
            )
        }

        // Exit confirmation dialog
        if (showExitDialog) {
            ExitConfirmationDialog(
                time = formatTime(elapsedSeconds),
                difficulty = board?.difficulty?.hebrewName ?: "",
                onContinue = { showExitDialog = false },
                onNewGame = {
                    showExitDialog = false
                    onNavigateBack()
                }
            )
        }

        // Reminder dialog
        currentReminder?.let { reminder ->
            ReminderDialog(
                message = reminder.message,
                currentPosition = 1,
                totalCount = reminderQueue.size,
                onDismiss = { reminderManager.dismissReminder() }
            )
        }
    }
}

/**
 * Paused overlay shown when game is paused
 */
@Composable
private fun PausedOverlay() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "המשחק מושהה",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "לחץ על ▶️ כדי להמשיך",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

/**
 * Win congratulations dialog
 */
@Composable
private fun WinDialog(
    time: String,
    difficulty: String,
    onDismiss: () -> Unit,
    onNewGame: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🎉 מזל טוב! 🎉",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "סיימת את הסודוקו!",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "רמת קושי: $difficulty",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "זמן: $time",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onNewGame) {
                Text("משחק חדש")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("חזור")
            }
        }
    )
}

/**
 * Game Over dialog when player makes too many mistakes
 */
@Composable
private fun GameOverDialog(
    mistakes: Int,
    onDismiss: () -> Unit,
    onNewGame: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "😞 המשחק נגמר",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "עשית $mistakes טעויות",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "נסה שוב!",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onNewGame) {
                Text("משחק חדש")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("חזור")
            }
        }
    )
}

/**
 * Reminder dialog shown at configured intervals
 */
@Composable
private fun ReminderDialog(
    message: String,
    currentPosition: Int,
    totalCount: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⏰ תזכורת",
                    style = MaterialTheme.typography.headlineSmall
                )

                // Show counter if multiple reminders in queue
                if (totalCount > 1) {
                    Text(
                        text = "תזכורת $currentPosition מתוך $totalCount",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        text = {
            Text(
                text = message,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("הבנתי")
            }
        }
    )
}

/**
 * Exit confirmation dialog with Continue/New Game options
 */
@Composable
private fun ExitConfirmationDialog(
    time: String,
    difficulty: String,
    onContinue: () -> Unit,
    onNewGame: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onContinue,
        title = {
            Text(
                text = "Classic Sudoku",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Continue button with time and difficulty
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "המשך",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = time,
                                fontSize = 16.sp
                            )
                            Text(
                                text = difficulty,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // New Game button
                Button(
                    onClick = onNewGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = "משחק חדש",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

/**
 * Format seconds into MM:SS format
 */
private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@Preview(showBackground = true, locale = "iw")
@Composable
fun GameScreenPreview() {
    SudokuWhatsAppTheme {
        GameScreen(difficulty = Difficulty.EASY)
    }
}
