package com.event.notes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewNote(viewmodel: NoteViewmodel = hiltViewModel(), navController: NavController){
    val title by viewmodel.title.collectAsState()
    val content by viewmodel.content.collectAsState()
    val isPinned by viewmodel.isPinned.collectAsState()

    BackHandler { //saves the note on back gesture
        viewmodel.saveNote(title, content, isPinned)
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewmodel.saveNote(title, content, isPinned)
                            navController.popBackStack()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "back")
                    }
                },
                actions = {
                    IconButton(onClick = {viewmodel.updatePinned()}) {
                        Icon(
                            imageVector = if(isPinned)Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "pin note"
                        )
                    }
                    IconButton(onClick = {
                        viewmodel.deleteNote()
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
 ) {padding ->
        Column(modifier = Modifier.padding(padding)) {
                TextField(
                    value = title,
                    onValueChange = {viewmodel.updateNoteTitle(it)},
                    placeholder = { Text("Title") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent, //remove text field line
                        focusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface, //without it characters became invisible
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            Spacer(Modifier.height(5.dp))
            TextField(
                value = content,
                onValueChange = {viewmodel.updateNoteContent(it)},
                placeholder = {Text("Note")},
                modifier = Modifier.fillMaxWidth().weight(1f), //text remaining space, so clickable anywhere after title
                textStyle = TextStyle(fontSize = 15.sp,),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}