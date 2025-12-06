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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sudokuwhatsapp.game.data.local.AllowedContact
import com.sudokuwhatsapp.game.data.local.FilteredMessage
import com.sudokuwhatsapp.game.data.models.Difficulty
import com.sudokuwhatsapp.game.data.models.SudokuBoard
import com.sudokuwhatsapp.game.data.models.SudokuCell
import com.sudokuwhatsapp.game.game.GameValidator
import com.sudokuwhatsapp.game.game.SudokuGenerator
import com.sudokuwhatsapp.game.game.SudokuSolver
import com.sudokuwhatsapp.game.ui.components.NumberPad
import com.sudokuwhatsapp.game.ui.components.SudokuGrid
import com.sudokuwhatsapp.game.ui.theme.SudokuWhatsAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showPhase4UI by remember { mutableStateOf(false) }

    if (showPhase4UI) {
        Phase4UITest(onNavigateBack = { showPhase4UI = false })
    } else {
        DebugMenuScreen(
            onNavigateBack = onNavigateBack,
            onShowPhase4UI = { showPhase4UI = true },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebugMenuScreen(
    onNavigateBack: () -> Unit,
    onShowPhase4UI: () -> Unit,
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

                // Phase 3 Tests
                DebugTestButton(
                    title = "Test Phase 3 - Generator",
                    description = "Tests Sudoku puzzle generation, solver, and validator",
                    onClick = { testPhase3Generator() },
                    enabled = true
                )

                // Phase 4 Tests
                DebugTestButton(
                    title = "Test Phase 4 - UI Components",
                    description = "Interactive Sudoku grid and number pad",
                    onClick = onShowPhase4UI,
                    enabled = true
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
 * Test Phase 3 puzzle generator, solver, and validator
 * Tests all game logic components
 */
private fun testPhase3Generator() {
    Log.d("Phase3Test", "=== Starting Phase 3 Generator Test ===")

    // 1. Test puzzle generation for each difficulty
    Log.d("Phase3Test", "--- Testing Puzzle Generation ---")
    Difficulty.entries.forEach { difficulty ->
        try {
            val board = SudokuGenerator.generate(difficulty)
            val givenCount = board.cells.flatten().count { it.isGiven }
            Log.d("Phase3Test", "${difficulty.hebrewName}: $givenCount givens (expected: ${difficulty.givens})")

            // Print the board visually
            Log.d("Phase3Test", "Board for ${difficulty.hebrewName}:")
            board.cells.forEach { row ->
                val rowStr = row.joinToString(" ") {
                    if (it.value == 0) "." else it.value.toString()
                }
                Log.d("Phase3Test", rowStr)
            }
            Log.d("Phase3Test", "---")
        } catch (e: Exception) {
            Log.e("Phase3Test", "Error generating ${difficulty.hebrewName}: ${e.message}")
        }
    }

    // 2. Test the solver
    Log.d("Phase3Test", "--- Testing Solver ---")
    try {
        val board = SudokuGenerator.generate(Difficulty.EASY)
        Log.d("Phase3Test", "Generated EASY puzzle for solving test")

        val solved = SudokuSolver.solve(board.cells)
        val isSolved = solved?.flatten()?.all { it.value != 0 } == true
        Log.d("Phase3Test", "Solver works: $isSolved")

        if (solved != null && isSolved) {
            Log.d("Phase3Test", "Solved board:")
            solved.forEach { row ->
                val rowStr = row.joinToString(" ") { it.value.toString() }
                Log.d("Phase3Test", rowStr)
            }
        }
    } catch (e: Exception) {
        Log.e("Phase3Test", "Error testing solver: ${e.message}")
    }

    // 3. Test validation
    Log.d("Phase3Test", "--- Testing Validator ---")
    try {
        val testBoard = SudokuGenerator.generate(Difficulty.EASY)
        val isComplete = GameValidator.isComplete(testBoard)
        Log.d("Phase3Test", "Empty board complete: $isComplete (should be false)")

        // Test if generated board has errors
        val withErrors = GameValidator.findErrors(testBoard)
        val hasErrors = withErrors.hasErrors()
        Log.d("Phase3Test", "Generated board has errors: $hasErrors (should be false)")

        // Test solver + validator together
        val solved = SudokuSolver.solve(testBoard.cells)
        if (solved != null) {
            val solvedBoard = testBoard.copy(cells = solved)
            val solvedIsComplete = GameValidator.isComplete(solvedBoard)
            Log.d("Phase3Test", "Solved board complete: $solvedIsComplete (should be true)")
        }
    } catch (e: Exception) {
        Log.e("Phase3Test", "Error testing validator: ${e.message}")
    }

    // 4. Test unique solution verification
    Log.d("Phase3Test", "--- Testing Unique Solution ---")
    try {
        val board = SudokuGenerator.generate(Difficulty.MEDIUM)
        val hasUnique = SudokuSolver.hasUniqueSolution(board.cells)
        Log.d("Phase3Test", "Generated puzzle has unique solution: $hasUnique (should be true)")
    } catch (e: Exception) {
        Log.e("Phase3Test", "Error testing unique solution: ${e.message}")
    }

    Log.d("Phase3Test", "=== Phase 3 Generator Test Complete ===")
}

/**
 * Test Phase 6 notification filtering
 * Placeholder for future implementation
 */
private fun testPhase6Notifications() {
    Log.d("Phase6Test", "=== Phase 6 Notifications Test - Coming Soon ===")
}

/**
 * Phase 4 UI Test - Interactive Sudoku Grid and Number Pad
 * Tests visual components with user interaction
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Phase4UITest(
    onNavigateBack: () -> Unit = {}
) {
    // Generate a MEDIUM difficulty board
    val board = remember { SudokuGenerator.generate(Difficulty.MEDIUM) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phase 4 - UI Test") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Info text
            Text(
                text = "Tap cells to select. Tap numbers to see logs.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Sudoku Grid
            SudokuGrid(
                board = board,
                selectedCell = selectedCell,
                onCellClick = { row, col ->
                    selectedCell = Pair(row, col)
                    Log.d("Phase4Test", "Cell selected: row=$row, col=$col")
                }
            )

            // Number Pad
            NumberPad(
                onNumberClick = { number ->
                    Log.d("Phase4Test", "Number $number clicked")
                    if (selectedCell != null) {
                        Log.d("Phase4Test", "Would place $number at ${selectedCell!!.first},${selectedCell!!.second}")
                    }
                },
                onClearClick = {
                    Log.d("Phase4Test", "Clear clicked")
                    if (selectedCell != null) {
                        Log.d("Phase4Test", "Would clear cell at ${selectedCell!!.first},${selectedCell!!.second}")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DebugScreenPreview() {
    SudokuWhatsAppTheme {
        DebugScreen()
    }
}
