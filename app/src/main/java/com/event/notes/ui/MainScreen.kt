package com.event.notes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.event.notes.data.UiState
import com.event.notes.ui.component.NoteCard

@Composable
fun MainScreen(viewmodel: NoteViewmodel = hiltViewModel(), navController: NavHostController) {

    val uiState by viewmodel.uiState.collectAsState()
    val searchQuery by viewmodel.searchQuery.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {navController.navigate("new_note/0")}) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->

        Column(modifier = Modifier.fillMaxSize().padding(paddingValues))
        {
            TextField(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                value = searchQuery,
                onValueChange = { viewmodel.updateSearchQuery(it) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search"
                    )
                },
                placeholder = {Text("Search notes")},
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            when (val state = uiState) {
                is UiState.Idle -> {}
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    if (state.notes.isEmpty()) {
                        Text("No notes found", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                    else {
                        LazyVerticalStaggeredGrid(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp), // padding between column of card
                            verticalItemSpacing = 10.dp, //padding between row of cards
                            columns = StaggeredGridCells.Fixed(2),
                            contentPadding = PaddingValues(10.dp),
                        ) {
                            item(span = StaggeredGridItemSpan.FullLine) { // stretch across both columns
                                Text(
                                    text = "Pinned",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                                )
                            }
                            items(
                                state.notes.filter { it.isPinned },
                                key = { it.id }
                            ) {
                                NoteCard(
                                    title = it.title,
                                    content = it.description,
                                    noteId = it.id,
                                    navController = navController
                                )
                            }

                            item(span = StaggeredGridItemSpan.FullLine) {
                                Text(
                                    text = "Others",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                                )
                            }
                            items(
                                state.notes.filter { !it.isPinned },
                                key = { it.id }
                            ) {
                                NoteCard(
                                    title = it.title,
                                    content = it.description,
                                    noteId = it.id,
                                    navController = navController
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Text("Error: ${state.message}")
                }
            }
        }
    }
}