package com.event.notes.data

interface UiState {
    object Idle: UiState
    object Loading: UiState
    data class Success(val notes: List<Note>): UiState
    data class Error(val message: String): UiState
}