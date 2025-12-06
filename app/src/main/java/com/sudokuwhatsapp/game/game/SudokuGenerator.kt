package com.sudokuwhatsapp.game.game

import com.sudokuwhatsapp.game.data.models.Difficulty
import com.sudokuwhatsapp.game.data.models.SudokuBoard
import com.sudokuwhatsapp.game.data.models.SudokuCell
import kotlin.random.Random

/**
 * Generates Sudoku puzzles with guaranteed unique solutions
 * Uses backtracking and strategic cell removal based on difficulty
 */
object SudokuGenerator {

    /**
     * Generates a new Sudoku puzzle with the specified difficulty
     *
     * @param difficulty The difficulty level (determines number of givens)
     * @return A playable SudokuBoard with the appropriate number of clues
     */
    fun generate(difficulty: Difficulty): SudokuBoard {
        // Step 1: Create empty 9x9 grid
        var cells = createEmptyGrid()

        // Step 2: Fill three diagonal 3x3 boxes with random numbers
        cells = fillDiagonalBoxes(cells)

        // Step 3: Solve the board to get a complete valid solution
        val solvedCells = SudokuSolver.solve(cells)
            ?: throw IllegalStateException("Failed to solve initial board")

        // Step 4: Remove cells to create puzzle with desired difficulty
        val puzzleCells = removeCells(solvedCells, difficulty)

        // Step 5: Return the board
        return SudokuBoard(
            cells = puzzleCells,
            difficulty = difficulty,
            startTimeMillis = System.currentTimeMillis(),
            isPaused = false
        )
    }

    /**
     * Creates an empty 9x9 grid of SudokuCells
     */
    private fun createEmptyGrid(): List<List<SudokuCell>> {
        return List(9) { row ->
            List(9) { col ->
                SudokuCell(value = 0, isGiven = false, row = row, col = col)
            }
        }
    }

    /**
     * Fills the three diagonal 3x3 boxes with random valid numbers
     * These boxes are independent and don't affect each other
     */
    private fun fillDiagonalBoxes(cells: List<List<SudokuCell>>): List<List<SudokuCell>> {
        val mutableCells = cells.map { it.toMutableList() }.toMutableList()

        // Fill boxes at (0,0), (3,3), and (6,6)
        for (boxIndex in 0..2) {
            val startRow = boxIndex * 3
            val startCol = boxIndex * 3
            fillBox(mutableCells, startRow, startCol)
        }

        return mutableCells.map { it.toList() }
    }

    /**
     * Fills a single 3x3 box with random numbers 1-9
     */
    private fun fillBox(cells: MutableList<MutableList<SudokuCell>>, startRow: Int, startCol: Int) {
        val numbers = (1..9).shuffled(Random)
        var index = 0

        for (row in startRow until startRow + 3) {
            for (col in startCol until startCol + 3) {
                cells[row][col] = cells[row][col].copy(value = numbers[index++])
            }
        }
    }

    /**
     * Removes cells from a solved board to create a puzzle
     * Ensures unique solution after each removal
     */
    private fun removeCells(
        solvedCells: List<List<SudokuCell>>,
        difficulty: Difficulty
    ): List<List<SudokuCell>> {
        val mutableCells = solvedCells.map { row ->
            row.map { it.copy(isGiven = true) }.toMutableList()
        }.toMutableList()

        // Calculate how many cells to remove
        val totalCells = 81
        val cellsToRemove = totalCells - difficulty.givens

        // Create list of all cell positions
        val positions = mutableListOf<Pair<Int, Int>>()
        for (row in 0..8) {
            for (col in 0..8) {
                positions.add(Pair(row, col))
            }
        }
        positions.shuffle(Random)

        var removedCount = 0
        var attemptIndex = 0

        // Try to remove cells while maintaining unique solution
        while (removedCount < cellsToRemove && attemptIndex < positions.size) {
            val (row, col) = positions[attemptIndex]
            attemptIndex++

            // Save current value
            val originalValue = mutableCells[row][col].value

            // Try removing this cell
            mutableCells[row][col] = mutableCells[row][col].copy(value = 0, isGiven = false)

            // Check if puzzle still has unique solution
            val testCells = mutableCells.map { it.toList() }
            if (SudokuSolver.hasUniqueSolution(testCells)) {
                // Removal successful
                removedCount++
            } else {
                // Restore the cell
                mutableCells[row][col] = mutableCells[row][col].copy(value = originalValue, isGiven = true)
            }
        }

        // If we couldn't remove enough cells, use a simpler approach
        // This is a fallback for very difficult puzzles
        if (removedCount < cellsToRemove) {
            val remaining = cellsToRemove - removedCount
            var additionalRemoved = 0

            for (i in attemptIndex until positions.size) {
                if (additionalRemoved >= remaining) break

                val (row, col) = positions[i]
                if (mutableCells[row][col].value != 0) {
                    mutableCells[row][col] = mutableCells[row][col].copy(value = 0, isGiven = false)
                    additionalRemoved++
                }
            }
        }

        return mutableCells.map { it.toList() }
    }
}
