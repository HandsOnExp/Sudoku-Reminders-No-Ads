package com.sudokuwhatsapp.game.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sudokuwhatsapp.game.ui.theme.SudokuWhatsAppTheme

/**
 * Number pad for entering numbers into Sudoku cells
 *
 * @param onNumberClick Callback when a number button (1-9) is clicked
 * @param onClearClick Callback when the clear/erase button is clicked
 * @param remainingNumbers Map of number to remaining count
 * @param modifier Modifier for the number pad
 */
@Composable
fun NumberPad(
    onNumberClick: (Int) -> Unit,
    onClearClick: () -> Unit,
    remainingNumbers: Map<Int, Int> = emptyMap(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // First row: 1-5
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (number in 1..5) {
                NumberButton(
                    number = number,
                    remainingCount = remainingNumbers[number] ?: 0,
                    onClick = { onNumberClick(number) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Second row: 6-9 + Clear
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (number in 6..9) {
                NumberButton(
                    number = number,
                    remainingCount = remainingNumbers[number] ?: 0,
                    onClick = { onNumberClick(number) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Clear/Erase button
            ClearButton(
                onClick = onClearClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Individual number button (1-9) with remaining count
 */
@Composable
private fun NumberButton(
    number: Int,
    remainingCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = remainingCount > 0,
        modifier = modifier.height(66.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = number.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = remainingCount.toString(),
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Clear/Erase button
 */
@Composable
private fun ClearButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(66.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NumberPadPreview() {
    SudokuWhatsAppTheme {
        NumberPad(
            onNumberClick = {},
            onClearClick = {},
            remainingNumbers = mapOf(
                1 to 4, 2 to 3, 3 to 5, 4 to 2, 5 to 1,
                6 to 0, 7 to 6, 8 to 3, 9 to 2
            )
        )
    }
}
