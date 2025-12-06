package com.sudokuwhatsapp.game.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudokuwhatsapp.game.data.local.AllowedContact
import com.sudokuwhatsapp.game.data.local.FilteredMessage
import com.sudokuwhatsapp.game.data.models.Difficulty
import com.sudokuwhatsapp.game.data.models.SudokuBoard
import com.sudokuwhatsapp.game.data.models.SudokuCell
import com.sudokuwhatsapp.game.ui.theme.SudokuWhatsAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Debug & Testing") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
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
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Development Tests",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Check Logcat for test results",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                // Phase 2 Tests
                DebugTestButton(
                    title = "Test Phase 2 - Models",
                    description = "Tests all data models (SudokuCell, Board, Difficulty, etc.)",
                    onClick = { testPhase2DataModels() }
                )

                // Phase 3 Tests - Placeholder
                DebugTestButton(
                    title = "Test Phase 3 - Generator",
                    description = "Tests Sudoku puzzle generation (Coming Soon)",
                    onClick = { testPhase3Generator() },
                    enabled = false
                )

                // Phase 6 Tests - Placeholder
                DebugTestButton(
                    title = "Test Phase 6 - Notifications",
                    description = "Tests WhatsApp notification filtering (Coming Soon)",
                    onClick = { testPhase6Notifications() },
                    enabled = false
                )
            }
        }
    }
}

@Composable
private fun DebugTestButton(
    title: String,
    description: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Test Phase 2 data models
 * Verifies that all data models work correctly
 */
private fun testPhase2DataModels() {
    Log.d("Phase2Test", "=== Starting Phase 2 Data Models Test ===")

    // 1. Test SudokuCell
    val cell = SudokuCell(value = 5, isGiven = true, row = 0, col = 0)
    Log.d("Phase2Test", "Cell created: $cell")
    Log.d("Phase2Test", "Cell isEmpty: ${cell.isEmpty()}, getBox: ${cell.getBox()}")

    // 2. Test all Difficulty levels
    Log.d("Phase2Test", "--- Testing Difficulty Levels ---")
    Difficulty.entries.forEach {
        Log.d("Phase2Test", "Difficulty: ${it.hebrewName}, givens: ${it.givens}")
    }

    // 3. Test AllowedContact
    val contact = AllowedContact(id = 1, displayName = "אבא", identifier = "אבא")
    Log.d("Phase2Test", "Contact: $contact")

    // 4. Test FilteredMessage
    val message = FilteredMessage(
        id = 1,
        senderName = "אבא",
        content = "שלום, מה שלומך?",
        timestamp = System.currentTimeMillis(),
        isFromGroup = false,
        groupName = null,
        isRead = false
    )
    Log.d("Phase2Test", "Message: $message")

    // 5. Test SudokuBoard
    val emptyBoard = SudokuBoard(
        cells = List(9) { row ->
            List(9) { col ->
                SudokuCell(value = 0, isGiven = false, row = row, col = col)
            }
        },
        difficulty = Difficulty.MEDIUM,
        startTimeMillis = System.currentTimeMillis(),
        isPaused = false
    )
    Log.d("Phase2Test", "Board created with ${emptyBoard.cells.size}x${emptyBoard.cells[0].size} cells")
    Log.d("Phase2Test", "Board isFilled: ${emptyBoard.isFilled()}, hasErrors: ${emptyBoard.hasErrors()}")
    Log.d("Phase2Test", "Board difficulty: ${emptyBoard.difficulty.hebrewName}")

    // Test helper methods
    val firstRow = emptyBoard.getRow(0)
    Log.d("Phase2Test", "First row has ${firstRow.size} cells")
    val firstCol = emptyBoard.getColumn(0)
    Log.d("Phase2Test", "First column has ${firstCol.size} cells")
    val firstBox = emptyBoard.getBox(0)
    Log.d("Phase2Test", "First box has ${firstBox.size} cells")

    Log.d("Phase2Test", "=== Phase 2 Data Models Test Complete ===")
}

/**
 * Test Phase 3 puzzle generator
 * Placeholder for future implementation
 */
private fun testPhase3Generator() {
    Log.d("Phase3Test", "=== Phase 3 Generator Test - Coming Soon ===")
}

/**
 * Test Phase 6 notification filtering
 * Placeholder for future implementation
 */
private fun testPhase6Notifications() {
    Log.d("Phase6Test", "=== Phase 6 Notifications Test - Coming Soon ===")
}

@Preview(showBackground = true)
@Composable
fun DebugScreenPreview() {
    SudokuWhatsAppTheme {
        DebugScreen()
    }
}
