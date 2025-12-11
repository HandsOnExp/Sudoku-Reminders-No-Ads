# Sudoku - Reminders, No Ads

A clean, ad-free Android Sudoku game with customizable reminder system, designed for Hebrew-speaking users.

## Overview

This app provides a distraction-free Sudoku experience with customizable reminders to help you take breaks during gameplay. Perfect for those who want to enjoy Sudoku without advertisements or interruptions.

## Features

### ✅ Complete Sudoku Game
- Full 9x9 Sudoku grid with clean, modern UI
- Six difficulty levels: Beginner (45 givens), Easy (40), Medium (35), Hard (30), Expert (25), Extreme (20)
- Smart number highlighting - tap a cell to highlight all occurrences of that number
- Real-time error detection and validation
- Mistake counter (max 3 mistakes per game)
- Game timer
- Pause/Resume functionality
- RTL (Right-to-Left) support for Hebrew interface

### ✅ Customizable Reminder System
- Create multiple custom reminders with personalized messages
- Set reminder intervals (20-45 minutes)
- Queue system - multiple reminders stack and display one at a time
- Duplicate detection - same reminder won't appear twice in queue
- Counter showing "תזכורת 1 מתוך 3" when multiple reminders are queued
- Enable/disable individual reminders
- Persistent settings storage

### ✅ Modern UI Design
- Material 3 design with clean, polished buttons
- Solid color buttons with large rounded corners
- No intrusive borders or frames
- Intuitive number pad with remaining count display
- Hebrew language support throughout

## Technical Specifications

- **Package Name**: `com.sudokuwhatsapp.game`
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 34
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3

## Dependencies

- Jetpack Compose BOM
- Compose Material 3
- Navigation Compose
- DataStore Preferences (for settings persistence)
- Kotlin Coroutines & Flow
- ViewModel & Lifecycle components
- Gson (for JSON serialization)

## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an Android device or emulator (API 26+)

## Project Structure

```
com.sudokuwhatsapp.game/
├── data/
│   ├── models/         # Data models (SudokuBoard, CustomReminder, etc.)
│   └── repository/     # Settings repository
├── game/               # Sudoku game logic (generator, validator, ViewModel)
├── reminders/          # Reminder manager with queue system
├── ui/
│   ├── components/     # Reusable UI components (SudokuGrid, NumberPad)
│   ├── screens/        # Compose screens (GameScreen, SettingsScreen)
│   └── theme/          # App theming and colors
└── MainActivity.kt     # Main entry point
```

## Game Features

### Difficulty Levels
- **מתחיל (Beginner)**: 45 pre-filled cells - perfect for learning
- **קל (Easy)**: 40 pre-filled cells - gentle challenge
- **בינוני (Medium)**: 35 pre-filled cells - balanced gameplay
- **קשה (Hard)**: 30 pre-filled cells - requires strategy
- **מומחה (Expert)**: 25 pre-filled cells - for experienced players
- **קיצוני (Extreme)**: 20 pre-filled cells - ultimate challenge

### Game Rules
- Fill the 9x9 grid with numbers 1-9
- Each row must contain all digits 1-9 without repetition
- Each column must contain all digits 1-9 without repetition
- Each 3x3 box must contain all digits 1-9 without repetition
- Three mistakes allowed per game

### Reminder Examples
- "הפסקה לשתייה" (Drink water break)
- "מתיחה קלה" (Light stretch)
- "הפסקת עיניים" (Eye break)
- "זמן לקום ולהתנועע" (Time to stand and move)

## Recent Updates

### Version 1.0
- Implemented complete Sudoku game with six difficulty levels
- Added customizable reminder system with queue management
- Implemented number highlighting feature
- Modernized UI with Material 3 design
- Fixed RTL ordering for Hebrew interface
- Added game timer and pause functionality
- Removed WhatsApp integration (simplified to focus on core Sudoku experience)

## Why No Ads?

This app is built for players who value a clean, uninterrupted gaming experience. No advertisements, no tracking, just pure Sudoku enjoyment.

## License

[To be determined]

## Author

HandsOnExp
