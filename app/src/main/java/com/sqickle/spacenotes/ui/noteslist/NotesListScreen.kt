package com.sqickle.spacenotes.ui.noteslist

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sqickle.spacenotes.data.model.Note

@Composable
fun NotesListScreen(
    onNoteClick: (String) -> Unit,
    onCreateNote: () -> Unit,
    viewModel: NotesListViewModel = hiltViewModel()
) {
    val notes: List<Note> by viewModel.notes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val configuration = LocalConfiguration.current

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B0D18))
        ) {
            StarryBackground(configuration = configuration)
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = onCreateNote) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Note"
                    )
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(notes) { note ->
                        NotesListItem(
                            note = note,
                            onClick = { onNoteClick(note.uid) },
                            onDelete = { viewModel.deleteNote(note.uid) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StarryBackground(configuration: Configuration) {
    Box(modifier = Modifier.fillMaxSize()) {
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        repeat(100) {
            Star(
                modifier = Modifier
                    .size((1..3).random().dp)
                    .offset(
                        x = (0 until screenWidth.value.toInt()).random().dp,
                        y = (0 until screenHeight.value.toInt()).random().dp
                    )
            )
        }
    }
}

@Composable
private fun Star(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (800..2500).random(),
                delayMillis = (0..1500).random(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = alpha),
                shape = CircleShape
            )
    )
}