package com.sudokuwhatsapp.game.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudokuwhatsapp.game.data.models.Difficulty
import com.sudokuwhatsapp.game.data.models.SudokuBoard
import com.sudokuwhatsapp.game.data.models.SudokuCell
import com.sudokuwhatsapp.game.ui.theme.SudokuErrorBackground
import com.sudokuwhatsapp.game.ui.theme.SudokuGivenNumber
import com.sudokuwhatsapp.game.ui.theme.SudokuGridDark
import com.sudokuwhatsapp.game.ui.theme.SudokuGridLight
import com.sudokuwhatsapp.game.ui.theme.SudokuLightBlue
import com.sudokuwhatsapp.game.ui.theme.SudokuUserNumber
import com.sudokuwhatsapp.game.ui.theme.SudokuWhatsAppTheme
import com.sudokuwhatsapp.game.ui.theme.SudokuWrongFlash

/**
 * Sudoku grid component displaying a 9x9 board
 *
 * @param board The current game board state
 * @param selectedCell Currently selected cell coordinates (row, col), null if none selected
 * @param onCellClick Callback when a cell is clicked
 * @param modifier Modifier for the grid
 */
@Composable
fun SudokuGrid(
    board: SudokuBoard,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit,
    wrongFlash: Pair<Int, Int>? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
    ) {
        // Background layer with grid lines
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            val width = size.width
            val height = size.height
            val cellSize = width / 9

            // Draw all grid lines
            for (i in 0..9) {
                val offset = i * cellSize
                // Determine line thickness - thick borders every 3 cells
                val strokeWidth = if (i % 3 == 0) 4.dp.toPx() else 1.dp.toPx()
                val color = if (i % 3 == 0) SudokuGridDark else SudokuGridLight

                // Vertical lines
                drawLine(
                    color = color,
                    start = Offset(offset, 0f),
                    end = Offset(offset, height),
                    strokeWidth = strokeWidth
                )

                // Horizontal lines
                drawLine(
                    color = color,
                    start = Offset(0f, offset),
                    end = Offset(width, offset),
                    strokeWidth = strokeWidth
                )
            }
        }

        // Cells layer with content
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            for (row in 0..8) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    for (col in 0..8) {
                        val cell = board.cells[row][col]
                        SudokuGridCell(
                            cell = cell,
                            isSelected = selectedCell == Pair(row, col),
                            isWrongFlash = wrongFlash == Pair(row, col),
                            onClick = { onCellClick(row, col) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual cell in the Sudoku grid
 */
@Composable
private fun SudokuGridCell(
    cell: SudokuCell,
    isSelected: Boolean,
    isWrongFlash: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine background color
    val backgroundColor = when {
        isWrongFlash -> SudokuWrongFlash
        cell.isError -> SudokuErrorBackground
        isSelected -> SudokuLightBlue
        else -> Color.Transparent
    }

    // Determine text color and weight
    val textColor = if (cell.isGiven) SudokuGivenNumber else SudokuUserNumber
    val fontWeight = if (cell.isGiven) FontWeight.Bold else FontWeight.Normal

    Box(
        modifier = modifier
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (cell.value != 0) {
            Text(
                text = cell.value.toString(),
                fontSize = 20.sp,
                fontWeight = fontWeight,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SudokuGridPreview() {
    // Create a sample board for preview
    val cells = List(9) { row ->
        List(9) { col ->
            SudokuCell(
                value = if ((row + col) % 2 == 0) (row + 1) else 0,
                isGiven = (row + col) % 2 == 0,
                row = row,
                col = col,
                isError = row == 0 && col == 0
            )
        }
    }

    val board = SudokuBoard(
        cells = cells,
        difficulty = Difficulty.MEDIUM
    )

    SudokuWhatsAppTheme {
        SudokuGrid(
            board = board,
            selectedCell = Pair(4, 4),
            onCellClick = { _, _ -> }
        )
    }
}
