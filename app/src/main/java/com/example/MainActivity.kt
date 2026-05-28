package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color

enum class CoreTool {
    CALENDAR, NOTES, CALCULATOR
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        val repository = NoteRepository(database.noteDao())
        
        setContent {
            MyApplicationTheme(darkTheme = false) {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(repository)
                )
                
                var currentTool by remember { mutableStateOf(CoreTool.CALENDAR) }
                var showAboutDialog by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Organiseur") },
                            actions = {
                                IconButton(onClick = { showAboutDialog = true }) {
                                    Icon(Icons.Default.Info, contentDescription = "A propos")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            )
                        )
                    },
                    bottomBar = {
                        // Wrapping inside windowInsetsPadding to fully ensure edge-to-edge support is covered.
                        NavigationBar(
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendrier") },
                                label = { Text("Calendrier") },
                                selected = currentTool == CoreTool.CALENDAR,
                                onClick = { currentTool = CoreTool.CALENDAR }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Notes, contentDescription = "Notes") },
                                label = { Text("Notes") },
                                selected = currentTool == CoreTool.NOTES,
                                onClick = { currentTool = CoreTool.NOTES }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculatrice") },
                                label = { Text("Calculatrice") },
                                selected = currentTool == CoreTool.CALCULATOR,
                                onClick = { currentTool = CoreTool.CALCULATOR }
                            )
                        }
                    }
                ) { innerPadding ->
                    when (currentTool) {
                        CoreTool.CALENDAR -> CalendarScreen(modifier = Modifier.padding(innerPadding))
                        CoreTool.NOTES -> NotesScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                        CoreTool.CALCULATOR -> CalculatorScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                    }
                }

                if (showAboutDialog) {
                    AlertDialog(
                        onDismissRequest = { showAboutDialog = false },
                        title = { Text("À propos") },
                        text = {
                            Text("Cette application a été créée par l'ingénieux et talentueux développeur BOKO ETIENNE, de nationalité béninoise. Une création exceptionnelle alliant productivité, design et efficacité.")
                        },
                        confirmButton = {
                            TextButton(onClick = { showAboutDialog = false }) {
                                Text("Fermer")
                            }
                        }
                    )
                }
            }
        }
    }
}
