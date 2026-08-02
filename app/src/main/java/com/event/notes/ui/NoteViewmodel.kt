package com.event.notes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.event.notes.data.Note
import com.event.notes.data.NoteDao
import com.event.notes.data.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewmodel @Inject constructor(
    private val dao: NoteDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val noteId: Int = savedStateHandle.get<Int>("noteId") ?: 0 //get id for navigation
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()
    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()
    private val _isPinned = MutableStateFlow(false)
    val isPinned: StateFlow<Boolean> = _isPinned.asStateFlow()
    private var originalTitle: String = ""  //saving the original before editing
    private var originalContent: String = ""
    private var initPinnedStatus: Boolean = false
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<UiState> = combine(dao.getAllNotes(), _searchQuery){notes, query ->
        filteredNotes(notes, query)
    }.map {
            UiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    init {
        if (noteId != 0){
            viewModelScope.launch {
                dao.getNoteById(noteId)?.let {
                    originalTitle = it.title ?:"" //taking snapshot of note title, and content
                    originalContent = it.description
                    initPinnedStatus = it.isPinned

                    _title.value = originalTitle //emit the note to ui,
                    _content.value = originalContent
                    _isPinned.value = initPinnedStatus
                }
            }
        }
    }

    fun filteredNotes(notes: List<Note>, query: String): List<Note>{
        return if (query.isBlank()){
            notes
        }
        else{
            notes.filter {
                it.title?.contains(query, ignoreCase = true) ?: false ||
                        (it.description.contains(query, ignoreCase = true))
            }
        }
    }

    fun updateSearchQuery(query: String) {_searchQuery.value = query}
    fun updateNoteTitle(title: String){
        _title.value = title
    }
    fun updateNoteContent(content: String){
        _content.value = content
    }
    fun updatePinned(){
        _isPinned.value = !_isPinned.value
    }

    fun saveNote(title: String?, content: String, isPinned: Boolean){
        if (title == originalTitle && content == originalContent && isPinned == initPinnedStatus){
            return
        }
        viewModelScope.launch {
           try {
               val note = Note(
                   id = noteId,
                   title = title,
                   description = content,
                   isPinned = isPinned
               )
               dao.saveNote(note)
           }catch (e: Exception){
               UiState.Error(e.message ?: " unknown error")
           }
        }
    }
    fun deleteNote(){
        if(noteId != 0) {
           try {
               viewModelScope.launch {
                   dao.deleteNote(noteId)
               }
           }catch (e: Exception){
               UiState.Error(e.message ?: "dont know what happened!")
           }
        }
    }

}
