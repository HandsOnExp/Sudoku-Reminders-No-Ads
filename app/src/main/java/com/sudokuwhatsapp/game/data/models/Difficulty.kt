package com.sudokuwhatsapp.game.data.models

/**
 * Difficulty levels for Sudoku game
 * @param givens Number of pre-filled cells (higher = easier)
 * @param hebrewName Display name in Hebrew for the UI
 */
enum class Difficulty(val givens: Int, val hebrewName: String) {
    BEGINNER(45, "מתחיל"),
    EASY(40, "קל"),
    MEDIUM(35, "בינוני"),
    HARD(30, "קשה"),
    EXPERT(25, "מומחה"),
    EXTREME(20, "קיצוני")
}
