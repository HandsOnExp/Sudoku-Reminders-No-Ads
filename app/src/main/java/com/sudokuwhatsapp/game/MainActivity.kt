package com.sudokuwhatsapp.game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.NotificationManagerCompat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudokuwhatsapp.game.data.models.Difficulty
import com.sudokuwhatsapp.game.ui.screens.DebugScreen
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
    var showDebugScreen by remember { mutableStateOf(false) }
    var showGameScreen by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }

    when {
        showDebugScreen -> {
            DebugScreen(
                onNavigateBack = { showDebugScreen = false }
            )
        }
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
                modifier = Modifier.fillMaxSize(),
                floatingActionButton = {
                    // Debug FAB - visible in development
                    FloatingActionButton(
                        onClick = { showDebugScreen = true },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Debug & Testing",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            ) { innerPadding ->
                HelloSudokuScreen(
                    modifier = Modifier.padding(innerPadding),
                    onTitleLongPress = { showDebugScreen = true },
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HelloSudokuScreen(
    modifier: Modifier = Modifier,
    onTitleLongPress: () -> Unit = {},
    onTitleClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isNotificationEnabled = remember {
        mutableStateOf(isNotificationListenerEnabled(context))
    }

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
                    modifier = Modifier.combinedClickable(
                        onClick = onTitleClick,
                        onLongClick = onTitleLongPress
                    )
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

                // Notification Permission Button
                if (!isNotificationEnabled.value) {
                    Button(
                        onClick = {
                            openNotificationListenerSettings(context)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.medium
                            )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "🔔 אפשר הודעות WhatsApp",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "נדרש לפעילות המשחק",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "✓ גישה להודעות מופעלת",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings button
                Button(
                    onClick = onSettingsClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                        Text(
                            text = "הגדרות",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Tap title to play • Long press for debug • Tap 🔧 for tests",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 24.dp)
                )
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
                    // Row 1: MEDIUM, EASY, BEGINNER (RTL order)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DifficultyButton(
                            difficulty = Difficulty.MEDIUM,
                            onClick = { onDifficultySelected(Difficulty.MEDIUM) },
                            modifier = Modifier.weight(1f)
                        )
                        DifficultyButton(
                            difficulty = Difficulty.EASY,
                            onClick = { onDifficultySelected(Difficulty.EASY) },
                            modifier = Modifier.weight(1f)
                        )
                        DifficultyButton(
                            difficulty = Difficulty.BEGINNER,
                            onClick = { onDifficultySelected(Difficulty.BEGINNER) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: EXTREME, EXPERT, HARD (RTL order)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DifficultyButton(
                            difficulty = Difficulty.EXTREME,
                            onClick = { onDifficultySelected(Difficulty.EXTREME) },
                            modifier = Modifier.weight(1f)
                        )
                        DifficultyButton(
                            difficulty = Difficulty.EXPERT,
                            onClick = { onDifficultySelected(Difficulty.EXPERT) },
                            modifier = Modifier.weight(1f)
                        )
                        DifficultyButton(
                            difficulty = Difficulty.HARD,
                            onClick = { onDifficultySelected(Difficulty.HARD) },
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
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                text = difficulty.hebrewName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
            Text(
                text = difficulty.givens.toString(),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
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

/**
 * Helper functions for notification listener permission
 */

/**
 * Check if notification listener permission is granted
 */
fun isNotificationListenerEnabled(context: Context): Boolean {
    val packageName = context.packageName
    val flat = Settings.Secure.getString(
        context.contentResolver,
        "enabled_notification_listeners"
    )
    return flat != null && flat.contains(packageName)
}

/**
 * Open system settings to grant notification listener permission
 */
fun openNotificationListenerSettings(context: Context) {
    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    context.startActivity(intent)
}
