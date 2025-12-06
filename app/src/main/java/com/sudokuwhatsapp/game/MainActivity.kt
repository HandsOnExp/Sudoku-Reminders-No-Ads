package com.sudokuwhatsapp.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudokuwhatsapp.game.data.models.Difficulty
import com.sudokuwhatsapp.game.ui.screens.DebugScreen
import com.sudokuwhatsapp.game.ui.screens.GameScreen
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

    when {
        showDebugScreen -> {
            DebugScreen(
                onNavigateBack = { showDebugScreen = false }
            )
        }
        showGameScreen -> {
            GameScreen(
                difficulty = Difficulty.MEDIUM,
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
                    onTitleClick = { showGameScreen = true }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HelloSudokuScreen(
    modifier: Modifier = Modifier,
    onTitleLongPress: () -> Unit = {},
    onTitleClick: () -> Unit = {}
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

@Preview(showBackground = true, locale = "iw")
@Composable
fun HelloSudokuScreenPreview() {
    SudokuWhatsAppTheme {
        HelloSudokuScreen()
    }
}
