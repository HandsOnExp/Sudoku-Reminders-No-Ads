package com.sudokuwhatsapp.game.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sudokuwhatsapp.game.data.models.Difficulty
import com.sudokuwhatsapp.game.data.models.SudokuBoard
import com.sudokuwhatsapp.game.data.models.SudokuCell
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing Sudoku game state
 * Handles game logic, timer, and user interactions
 */
class GameViewModel : ViewModel() {

    // Game state flows
    private val _board = MutableStateFlow<SudokuBoard?>(null)
    val board: StateFlow<SudokuBoard?> = _board.asStateFlow()

    private val _selectedCell = MutableStateFlow<Pair<Int, Int>?>(null)
    val selectedCell: StateFlow<Pair<Int, Int>?> = _selectedCell.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _isGameWon = MutableStateFlow(false)
    val isGameWon: StateFlow<Boolean> = _isGameWon.asStateFlow()

    private val _mistakes = MutableStateFlow(0)
    val mistakes: StateFlow<Int> = _mistakes.asStateFlow()

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()

    private val _wrongNumberFlash = MutableStateFlow<Pair<Int, Int>?>(null)
    val wrongNumberFlash: StateFlow<Pair<Int, Int>?> = _wrongNumberFlash.asStateFlow()

    // Timer job
    private var timerJob: Job? = null

    companion object {
        const val MAX_MISTAKES = 3
    }

    /**
     * Start a new game with the specified difficulty
     */
    fun startNewGame(difficulty: Difficulty) {
        // Cancel existing timer
        timerJob?.cancel()

        // Generate new board
        val newBoard = SudokuGenerator.generate(difficulty)
        _board.value = newBoard

        // Reset state
        _selectedCell.value = null
        _elapsedSeconds.value = 0
        _isPaused.value = false
        _isGameWon.value = false
        _mistakes.value = 0
        _isGameOver.value = false
        _wrongNumberFlash.value = null

        // Start timer
        startTimer()
    }

    /**
     * Select a cell on the board
     */
    fun selectCell(row: Int, col: Int) {
        if (row in 0..8 && col in 0..8) {
            _selectedCell.value = Pair(row, col)
        }
    }

    /**
     * Input a number into the selected cell
     * Only works if cell is not a given number and move is valid
     */
    fun inputNumber(num: Int) {
        val currentBoard = _board.value ?: return
        val selected = _selectedCell.value ?: return
        val (row, col) = selected

        val cell = currentBoard.cells[row][col]

        // Don't allow modifying given numbers
        if (cell.isGiven) {
            return
        }

        // Validate the move before placing
        if (!GameValidator.isValidMove(currentBoard, row, col, num)) {
            // Invalid move - increment mistakes and show flash
            _mistakes.value += 1
            _wrongNumberFlash.value = Pair(row, col)

            // Clear flash after delay
            viewModelScope.launch {
                delay(500)
                _wrongNumberFlash.value = null
            }

            // Check if game over due to mistakes
            if (_mistakes.value >= MAX_MISTAKES) {
                _isGameOver.value = true
                pauseGame()
            }
            return
        }

        // Create new cell with the number
        val newCell = cell.copy(value = num)

        // Update board
        val newCells = currentBoard.cells.map { rowCells ->
            rowCells.map { c ->
                if (c.row == row && c.col == col) newCell else c
            }
        }

        // Validate and mark errors (should be none if validation works)
        val updatedBoard = currentBoard.copy(cells = newCells)
        val validatedBoard = GameValidator.findErrors(updatedBoard)
        _board.value = validatedBoard

        // Check if game is won
        if (GameValidator.isComplete(validatedBoard)) {
            _isGameWon.value = true
            pauseGame()
        }
    }

    /**
     * Clear the selected cell
     * Only works if cell is not a given number
     */
    fun clearCell() {
        val currentBoard = _board.value ?: return
        val selected = _selectedCell.value ?: return
        val (row, col) = selected

        val cell = currentBoard.cells[row][col]

        // Don't allow modifying given numbers
        if (cell.isGiven) {
            return
        }

        // Create new cell with value 0
        val newCell = cell.copy(value = 0, isError = false)

        // Update board
        val newCells = currentBoard.cells.map { rowCells ->
            rowCells.map { c ->
                if (c.row == row && c.col == col) newCell else c
            }
        }

        // Validate and mark errors
        val updatedBoard = currentBoard.copy(cells = newCells)
        val validatedBoard = GameValidator.findErrors(updatedBoard)
        _board.value = validatedBoard
    }

    /**
     * Pause the game
     */
    fun pauseGame() {
        _isPaused.value = true
        timerJob?.cancel()
    }

    /**
     * Resume the game
     */
    fun resumeGame() {
        _isPaused.value = false
        startTimer()
    }

    /**
     * Start the timer coroutine
     * Increments elapsedSeconds every second
     */
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_isPaused.value) {
                    _elapsedSeconds.value += 1
                }
            }
        }
    }

    /**
     * Get remaining count for each number (1-9)
     * Returns a map of number to remaining count
     */
    fun getRemainingNumbers(): Map<Int, Int> {
        val currentBoard = _board.value ?: return emptyMap()
        val solution = currentBoard.solution ?: return emptyMap()

        // Count how many of each number are in the solution
        val totalCounts = mutableMapOf<Int, Int>()
        for (row in solution) {
            for (num in row) {
                totalCounts[num] = (totalCounts[num] ?: 0) + 1
            }
        }

        // Count how many of each number are already placed correctly
        val placedCounts = mutableMapOf<Int, Int>()
        for (row in currentBoard.cells) {
            for (cell in row) {
                if (cell.value != 0 && solution[cell.row][cell.col] == cell.value) {
                    placedCounts[cell.value] = (placedCounts[cell.value] ?: 0) + 1
                }
            }
        }

        // Calculate remaining
        return (1..9).associateWith { num ->
            (totalCounts[num] ?: 0) - (placedCounts[num] ?: 0)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
