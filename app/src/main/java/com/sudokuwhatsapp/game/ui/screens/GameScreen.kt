package com.sudokuwhatsapp.game.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sudokuwhatsapp.game.data.models.Difficulty
import com.sudokuwhatsapp.game.game.GameViewModel
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
    // Collect state from ViewModel
    val board by viewModel.board.collectAsState()
    val selectedCell by viewModel.selectedCell.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val isGameWon by viewModel.isGameWon.collectAsState()

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
                    IconButton(onClick = onNavigateBack) {
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
                if (isPaused && !isGameWon) {
                    PausedOverlay()
                } else {
                    // Sudoku Grid
                    board?.let { currentBoard ->
                        SudokuGrid(
                            board = currentBoard,
                            selectedCell = selectedCell,
                            onCellClick = { row, col ->
                                viewModel.selectCell(row, col)
                            }
                        )
                    }

                    // Number Pad
                    NumberPad(
                        onNumberClick = { number ->
                            viewModel.inputNumber(number)
                        },
                        onClearClick = {
                            viewModel.clearCell()
                        }
                    )
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
