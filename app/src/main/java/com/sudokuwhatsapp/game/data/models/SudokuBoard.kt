package com.sudokuwhatsapp.game.data.models

/**
 * Represents the complete Sudoku game board state
 *
 * @param cells 9x9 grid of SudokuCell objects representing the board
 * @param difficulty The difficulty level of this game
 * @param startTimeMillis Timestamp when the game started (for timer)
 * @param isPaused True if the game is currently paused
 */
data class SudokuBoard(
    val cells: List<List<SudokuCell>>,
    val difficulty: Difficulty,
    val startTimeMillis: Long = System.currentTimeMillis(),
    val isPaused: Boolean = false
) {
    init {
        require(cells.size == 9) { "Board must have exactly 9 rows" }
        require(cells.all { it.size == 9 }) { "Each row must have exactly 9 columns" }
    }

    /**
     * Get a specific cell by row and column
     */
    fun getCell(row: Int, col: Int): SudokuCell {
        require(row in 0..8 && col in 0..8) { "Invalid cell position: ($row, $col)" }
        return cells[row][col]
    }

    /**
     * Get all cells in a specific row
     */
    fun getRow(rowIndex: Int): List<SudokuCell> {
        require(rowIndex in 0..8) { "Invalid row index: $rowIndex" }
        return cells[rowIndex]
    }

    /**
     * Get all cells in a specific column
     */
    fun getColumn(colIndex: Int): List<SudokuCell> {
        require(colIndex in 0..8) { "Invalid column index: $colIndex" }
        return cells.map { it[colIndex] }
    }

    /**
     * Get all cells in a specific 3x3 box (0-8)
     */
    fun getBox(boxIndex: Int): List<SudokuCell> {
        require(boxIndex in 0..8) { "Invalid box index: $boxIndex" }
        val boxRow = (boxIndex / 3) * 3
        val boxCol = (boxIndex % 3) * 3
        return buildList {
            for (r in boxRow until boxRow + 3) {
                for (c in boxCol until boxCol + 3) {
                    add(cells[r][c])
                }
            }
        }
    }

    /**
     * Check if the board is completely filled
     */
    fun isFilled(): Boolean = cells.all { row -> row.all { !it.isEmpty() } }

    /**
     * Check if the board has any errors
     */
    fun hasErrors(): Boolean = cells.any { row -> row.any { it.isError } }

    /**
     * Check if the puzzle is solved (filled and no errors)
     */
    fun isSolved(): Boolean = isFilled() && !hasErrors()

    /**
     * Get the elapsed time in milliseconds (accounting for pause state)
     */
    fun getElapsedTimeMillis(): Long {
        return if (isPaused) {
            0L
        } else {
            System.currentTimeMillis() - startTimeMillis
        }
    }

    companion object {
        /**
         * Create an empty 9x9 board
         */
        fun createEmpty(difficulty: Difficulty = Difficulty.MEDIUM): SudokuBoard {
            val cells = List(9) { row ->
                List(9) { col ->
                    SudokuCell(row = row, col = col)
                }
            }
            return SudokuBoard(
                cells = cells,
                difficulty = difficulty
            )
        }
    }
}
