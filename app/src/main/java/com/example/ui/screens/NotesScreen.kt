package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.MainViewModel
import com.example.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val notes by viewModel.notes.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    
    var tempTitle by remember { mutableStateOf("") }
    var tempContent by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                tempTitle = ""
                tempContent = ""
                showDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            Text(
                text = "Bloc-notes",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
            )

            if (notes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucune note pour l'instant", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(note = note, onDelete = { viewModel.deleteNote(note.id) })
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nouvelle Note") },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempTitle,
                        onValueChange = { tempTitle = it },
                        label = { Text("Titre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempContent,
                        onValueChange = { tempContent = it },
                        label = { Text("Contenu") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tempTitle.isNotBlank() || tempContent.isNotBlank()) {
                        viewModel.addNote(tempTitle, tempContent)
                    }
                    showDialog = false
                }) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun NoteCard(note: Note, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (note.title.isNotBlank()) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha=0.8f)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}
