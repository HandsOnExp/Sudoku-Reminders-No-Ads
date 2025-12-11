package com.sudokuwhatsapp.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudokuwhatsapp.game.data.models.Difficulty
import com.sudokuwhatsapp.game.ui.screens.GameScreen
import com.sudokuwhatsapp.game.ui.screens.SettingsScreen
import com.sudokuwhatsapp.game.ui.theme.SudokuWhatsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SudokuWhatsAppTheme {
                SudokuApp()
            }
        }
    }
}

@Composable
fun SudokuApp() {
    var showGameScreen by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }

    when {
        showSettingsScreen -> {
            SettingsScreen(
                onNavigateBack = { showSettingsScreen = false }
            )
        }
        showGameScreen -> {
            GameScreen(
                difficulty = selectedDifficulty,
                onNavigateBack = { showGameScreen = false }
            )
        }
        else -> {
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                HelloSudokuScreen(
                    modifier = Modifier.padding(innerPadding),
                    onTitleClick = { showDifficultyDialog = true },
                    onSettingsClick = { showSettingsScreen = true }
                )

                // Difficulty selection dialog
                if (showDifficultyDialog) {
                    DifficultySelectionDialog(
                        onDismiss = { showDifficultyDialog = false },
                        onDifficultySelected = { difficulty ->
                            selectedDifficulty = difficulty
                            showDifficultyDialog = false
                            showGameScreen = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HelloSudokuScreen(
    modifier: Modifier = Modifier,
    onTitleClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.hello_sudoku),
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = stringResource(id = R.string.welcome_message),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Start game button
                Button(
                    onClick = onTitleClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text(
                        text = "התחל משחק",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Settings button
                Button(
                    onClick = onSettingsClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "הגדרות"
                        )
                        Text(
                            text = "הגדרות",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog for selecting game difficulty level
 */
@Composable
fun DifficultySelectionDialog(
    onDismiss: () -> Unit,
    onDifficultySelected: (Difficulty) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "בחר רמת קושי",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Group difficulties into rows
                    // Row 1: BEGINNER, EASY, MEDIUM (RTL display order)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DifficultyButton(
                            difficulty = Difficulty.BEGINNER,
                            onClick = { onDifficultySelected(Difficulty.BEGINNER) },
                            modifier = Modifier.weight(1f)
                        )
                        DifficultyButton(
                            difficulty = Difficulty.EASY,
                            onClick = { onDifficultySelected(Difficulty.EASY) },
                            modifier = Modifier.weight(1f)
                        )
                        DifficultyButton(
                            difficulty = Difficulty.MEDIUM,
                            onClick = { onDifficultySelected(Difficulty.MEDIUM) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: HARD, EXPERT, EXTREME (RTL display order)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DifficultyButton(
                            difficulty = Difficulty.HARD,
                            onClick = { onDifficultySelected(Difficulty.HARD) },
                            modifier = Modifier.weight(1f)
                        )
                        DifficultyButton(
                            difficulty = Difficulty.EXPERT,
                            onClick = { onDifficultySelected(Difficulty.EXPERT) },
                            modifier = Modifier.weight(1f)
                        )
                        DifficultyButton(
                            difficulty = Difficulty.EXTREME,
                            onClick = { onDifficultySelected(Difficulty.EXTREME) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Info text about difficulty
                    Text(
                        text = "מספר התאים המלאים:\n" +
                               "מתחיל: 45 • קל: 40 • בינוני: 35\n" +
                               "קשה: 30 • מומחה: 25 • קיצוני: 20",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("ביטול")
                }
            }
        )
    }
}

/**
 * Individual difficulty button
 */
@Composable
private fun DifficultyButton(
    difficulty: Difficulty,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(70.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            Text(
                text = difficulty.hebrewName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = difficulty.givens.toString(),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, locale = "iw")
@Composable
fun HelloSudokuScreenPreview() {
    SudokuWhatsAppTheme {
        HelloSudokuScreen()
    }
}
