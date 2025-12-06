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

    // Timer job
    private var timerJob: Job? = null

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
            // Invalid move - don't place the number
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

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
