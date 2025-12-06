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
 * @param modifier Modifier for the number pad
 */
@Composable
fun NumberPad(
    onNumberClick: (Int) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
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
 * Individual number button (1-9)
 */
@Composable
private fun NumberButton(
    number: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),  // Minimum 48dp touch target + padding
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Text(
            text = number.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
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
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
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
            onClearClick = {}
        )
    }
}
