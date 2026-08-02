# Notes

A note-taking Android app built with Jetpack Compose and modern Android architecture. Create, edit, search, and pin notes with data persisted locally via Room.


## Features

- **Create & edit notes** — title and content, with a distraction-free editing screen
- **Pin notes** — pinned notes are grouped separately at the top of the list
- **Search** — live filtering across note title and content as you type
- **Persistent storage** — notes are saved locally with Room and survive app restarts
- **Staggered grid layout** — a Google Keep–style grid that sizes each card to its content

## Tech Stack

- **Kotlin** — 100% Kotlin, idiomatic coroutines and Flow throughout
- **Jetpack Compose** — declarative UI, no XML layouts
- **Room** — local persistence with reactive `Flow`-based queries
- **Hilt** — dependency injection for ViewModels and repositories
- **Navigation Compose** — single-activity navigation with type-safe route arguments
- **Kotlin Flow / StateFlow** —  used to get notes stream with live search input; UI state modeled as a sealed interface (`Loading` / `Success` / `Error`)


## Architecture

The app follows a standard MVVM structure:

```
UI (Compose)  →  ViewModel  →  Room DAO  →  SQLite
```

- **UiState** is a sealed interface (`Loading`, `Success(notes)`, `Error(message)`), rendered with an exhaustive `when` in Compose.
- **Search** is implemented by combining Room's live `Flow<List<Note>>` with a `MutableStateFlow<String>` search query via `combine()`, so the note list and search box stay reactive to each other without manual refresh calls.
- **Navigation** uses a single route (`note_screen/{noteId}`) for both creating a new note and editing an existing one — `noteId = 0` signals a new note, any other value loads that note's existing data via `SavedStateHandle`.
- **Editing safety net** — a `BackHandler` ensures notes are saved on the system back gesture, not just the in-app back button.

## Running the project

1. Clone the repo
2. Open in Android Studio
3. Let Gradle sync and build
