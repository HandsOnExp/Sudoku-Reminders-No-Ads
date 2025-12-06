package com.sudokuwhatsapp.game

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudokuwhatsapp.game.data.local.AllowedContact
import com.sudokuwhatsapp.game.data.local.FilteredMessage
import com.sudokuwhatsapp.game.data.models.Difficulty
import com.sudokuwhatsapp.game.data.models.SudokuBoard
import com.sudokuwhatsapp.game.data.models.SudokuCell
import com.sudokuwhatsapp.game.ui.theme.SudokuWhatsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SudokuWhatsAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HelloSudokuScreen(
                        modifier = Modifier.padding(innerPadding),
                        onTestPhase2Click = { testPhase2DataModels() }
                    )
                }
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
}

@Composable
fun HelloSudokuScreen(
    modifier: Modifier = Modifier,
    onTestPhase2Click: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
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
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = stringResource(id = R.string.welcome_message),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            Button(
                onClick = onTestPhase2Click,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text(
                    text = "Test Phase 2",
                    fontSize = 18.sp
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
