package com.sudokuwhatsapp.game.data.models

/**
 * Represents a single cell in the Sudoku grid
 *
 * @param value Current value in the cell (0 = empty, 1-9 for numbers)
 * @param isGiven True if this is a pre-filled cell that cannot be edited
 * @param row Row position in the grid (0-8)
 * @param col Column position in the grid (0-8)
 * @param isError True if this cell conflicts with another cell in the same row/column/box
 * @param notes Set of pencil marks/notes (1-9) for this cell
 */
data class SudokuCell(
    val value: Int = 0,
    val isGiven: Boolean = false,
    val row: Int,
    val col: Int,
    val isError: Boolean = false,
    val notes: Set<Int> = emptySet()
) {
    init {
        require(value in 0..9) { "Cell value must be between 0 and 9" }
        require(row in 0..8) { "Row must be between 0 and 8" }
        require(col in 0..8) { "Column must be between 0 and 8" }
        require(notes.all { it in 1..9 }) { "Notes must contain values between 1 and 9" }
    }

    /**
     * Returns true if the cell is empty (value = 0)
     */
    fun isEmpty(): Boolean = value == 0

    /**
     * Returns the 3x3 box number (0-8) this cell belongs to
     */
    fun getBox(): Int = (row / 3) * 3 + (col / 3)
}
