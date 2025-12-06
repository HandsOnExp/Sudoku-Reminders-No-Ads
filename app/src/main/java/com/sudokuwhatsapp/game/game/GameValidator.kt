package com.sudokuwhatsapp.game.game

import com.sudokuwhatsapp.game.data.models.SudokuBoard
import com.sudokuwhatsapp.game.data.models.SudokuCell

/**
 * Validates Sudoku game state and identifies errors
 * Checks for conflicts in rows, columns, and 3x3 boxes
 */
object GameValidator {

    /**
     * Finds all errors in the current board state
     * Marks conflicting cells with isError = true
     *
     * @param board The current game board
     * @return Updated board with error flags set
     */
    fun findErrors(board: SudokuBoard): SudokuBoard {
        val cells = board.cells
        val newCells = cells.map { row ->
            row.map { cell ->
                if (cell.value == 0) {
                    // Empty cells have no errors
                    cell.copy(isError = false)
                } else {
                    // Check if this cell conflicts with others
                    val hasError = hasConflict(cells, cell.row, cell.col, cell.value)
                    cell.copy(isError = hasError)
                }
            }
        }

        return board.copy(cells = newCells)
    }

    /**
     * Checks if a cell has conflicts with other cells
     *
     * @param cells The board state
     * @param row Row of the cell to check
     * @param col Column of the cell to check
     * @param value Value of the cell
     * @return true if there's a conflict, false otherwise
     */
    private fun hasConflict(
        cells: List<List<SudokuCell>>,
        row: Int,
        col: Int,
        value: Int
    ): Boolean {
        // Check row for duplicates (excluding this cell)
        for (c in 0..8) {
            if (c != col && cells[row][c].value == value) {
                return true
            }
        }

        // Check column for duplicates (excluding this cell)
        for (r in 0..8) {
            if (r != row && cells[r][col].value == value) {
                return true
            }
        }

        // Check 3x3 box for duplicates (excluding this cell)
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if ((r != row || c != col) && cells[r][c].value == value) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Checks if the board is complete and valid
     * A board is complete when all cells are filled with no errors
     *
     * @param board The game board to check
     * @return true if board is completely and correctly filled, false otherwise
     */
    fun isComplete(board: SudokuBoard): Boolean {
        // Check if all cells are filled
        if (!board.isFilled()) {
            return false
        }

        // Check if there are any errors
        if (board.hasErrors()) {
            return false
        }

        // Double-check by validating the entire board
        for (row in 0..8) {
            for (col in 0..8) {
                val cell = board.cells[row][col]
                if (hasConflict(board.cells, row, col, cell.value)) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Validates a move before it's made
     * Used to prevent invalid placements during gameplay
     *
     * @param board Current board state
     * @param row Row where number will be placed
     * @param col Column where number will be placed
     * @param num Number to place
     * @return true if placement is valid, false otherwise
     */
    fun isValidMove(board: SudokuBoard, row: Int, col: Int, num: Int): Boolean {
        // Can't place on a given cell
        if (board.cells[row][col].isGiven) {
            return false
        }

        // Number must be 1-9 or 0 (to clear)
        if (num !in 0..9) {
            return false
        }

        // If clearing the cell, always valid
        if (num == 0) {
            return true
        }

        // Check if this number conflicts with existing numbers
        return SudokuSolver.isValidPlacement(board.cells, row, col, num)
    }

    /**
     * Gets all possible valid numbers for a specific cell
     *
     * @param board Current board state
     * @param row Row of the cell
     * @param col Column of the cell
     * @return Set of valid numbers (1-9) that can be placed in this cell
     */
    fun getPossibleNumbers(board: SudokuBoard, row: Int, col: Int): Set<Int> {
        // If cell is given, no numbers can be placed
        if (board.cells[row][col].isGiven) {
            return emptySet()
        }

        // Try each number 1-9
        return (1..9).filter { num ->
            SudokuSolver.isValidPlacement(board.cells, row, col, num)
        }.toSet()
    }
}
