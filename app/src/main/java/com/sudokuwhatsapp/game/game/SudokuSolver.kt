package com.sudokuwhatsapp.game.game

import com.sudokuwhatsapp.game.data.models.SudokuCell

/**
 * Sudoku solver using backtracking algorithm
 * Provides validation and solving capabilities for Sudoku puzzles
 */
object SudokuSolver {

    /**
     * Checks if placing a number at a specific position is valid
     * Validates against row, column, and 3x3 box constraints
     *
     * @param cells The current board state (9x9 grid)
     * @param row Row index (0-8)
     * @param col Column index (0-8)
     * @param num Number to place (1-9)
     * @return true if placement is valid, false otherwise
     */
    fun isValidPlacement(
        cells: List<List<SudokuCell>>,
        row: Int,
        col: Int,
        num: Int
    ): Boolean {
        // Check row
        for (c in 0..8) {
            if (cells[row][c].value == num) {
                return false
            }
        }

        // Check column
        for (r in 0..8) {
            if (cells[r][col].value == num) {
                return false
            }
        }

        // Check 3x3 box
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if (cells[r][c].value == num) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Solves a Sudoku puzzle using backtracking algorithm
     *
     * @param cells The puzzle to solve (9x9 grid)
     * @return Solved board, or null if no solution exists
     */
    fun solve(cells: List<List<SudokuCell>>): List<List<SudokuCell>>? {
        // Create a mutable copy for solving
        val workingGrid = cells.map { row ->
            row.map { it.copy() }.toMutableList()
        }.toMutableList()

        return if (solveRecursive(workingGrid)) {
            workingGrid.map { it.toList() }
        } else {
            null
        }
    }

    /**
     * Recursive backtracking solver
     * Modifies the grid in-place
     */
    private fun solveRecursive(grid: MutableList<MutableList<SudokuCell>>): Boolean {
        // Find next empty cell
        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col].value == 0) {
                    // Try numbers 1-9
                    for (num in 1..9) {
                        if (isValidPlacement(grid, row, col, num)) {
                            // Place number
                            grid[row][col] = grid[row][col].copy(value = num)

                            // Recursively solve
                            if (solveRecursive(grid)) {
                                return true
                            }

                            // Backtrack
                            grid[row][col] = grid[row][col].copy(value = 0)
                        }
                    }
                    // No valid number found, backtrack
                    return false
                }
            }
        }
        // All cells filled successfully
        return true
    }

    /**
     * Checks if a puzzle has exactly one unique solution
     *
     * @param cells The puzzle to check
     * @return true if exactly one solution exists, false otherwise
     */
    fun hasUniqueSolution(cells: List<List<SudokuCell>>): Boolean {
        val workingGrid = cells.map { row ->
            row.map { it.copy() }.toMutableList()
        }.toMutableList()

        val solutions = mutableListOf<List<List<SudokuCell>>>()
        countSolutions(workingGrid, solutions, maxSolutions = 2)

        return solutions.size == 1
    }

    /**
     * Counts solutions up to a maximum number
     * Used for unique solution verification
     */
    private fun countSolutions(
        grid: MutableList<MutableList<SudokuCell>>,
        solutions: MutableList<List<List<SudokuCell>>>,
        maxSolutions: Int
    ) {
        if (solutions.size >= maxSolutions) {
            return
        }

        // Find next empty cell
        var emptyRow = -1
        var emptyCol = -1
        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col].value == 0) {
                    emptyRow = row
                    emptyCol = col
                    break
                }
            }
            if (emptyRow != -1) break
        }

        // If no empty cell, we found a solution
        if (emptyRow == -1) {
            solutions.add(grid.map { it.map { cell -> cell.copy() } })
            return
        }

        // Try numbers 1-9
        for (num in 1..9) {
            if (isValidPlacement(grid, emptyRow, emptyCol, num)) {
                grid[emptyRow][emptyCol] = grid[emptyRow][emptyCol].copy(value = num)
                countSolutions(grid, solutions, maxSolutions)
                grid[emptyRow][emptyCol] = grid[emptyRow][emptyCol].copy(value = 0)
            }
        }
    }
}
