# Sudoku WhatsApp Integration

An Android Sudoku game with WhatsApp notification integration, designed for Hebrew-speaking users.

## Overview

This app allows users to play Sudoku while receiving filtered WhatsApp notifications from specific contacts and prayer groups, ensuring they don't miss important messages while enjoying the game.

## Features (Planned)

### Phase 1: Project Setup ✅
- Android project with Jetpack Compose and Material 3
- Kotlin-based architecture
- RTL (Right-to-Left) support for Hebrew
- Basic project structure

### Phase 2: Notification Listener (Upcoming)
- WhatsApp notification monitoring
- Real-time message filtering
- Notification display during gameplay

### Phase 3: Contact Management (Upcoming)
- Room database for storing allowed contacts
- Settings screen for managing contacts
- Add/remove contacts and prayer groups

### Phase 4: Sudoku Game (Upcoming)
- Full 9x9 Sudoku grid
- Multiple difficulty levels
- Game controls and timer
- Hebrew UI support

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
- Room Database
- DataStore Preferences
- Kotlin Coroutines
- ViewModel & Lifecycle components

## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an Android device or emulator (API 26+)

## Required Permissions

- `BIND_NOTIFICATION_LISTENER_SERVICE` - For monitoring WhatsApp notifications
- `POST_NOTIFICATIONS` - For displaying notifications (Android 13+)

## Project Structure

```
com.sudokuwhatsapp.game/
├── data/
│   ├── local/          # Room database entities and DAOs
│   └── repository/     # Data repositories
├── service/            # Background services (NotificationListener)
├── ui/
│   ├── screens/        # Compose screens
│   └── theme/          # App theming and colors
└── MainActivity.kt     # Main entry point
```

## Current Status

**Phase 1 Complete**: Basic project setup with RTL support and initial "Hello Sudoku" screen.

## Future Development

- Implement NotificationListenerService for WhatsApp monitoring
- Build contact management system with Room database
- Create complete Sudoku game logic and UI
- Add game state persistence
- Implement notification overlay during gameplay

## License

[To be determined]

## Author

HandsOnExp
