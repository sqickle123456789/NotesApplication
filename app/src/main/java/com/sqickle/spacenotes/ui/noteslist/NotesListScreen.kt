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
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sqickle.spacenotes.ui.noteslist.components.LoadingIndicator
import com.sqickle.spacenotes.ui.noteslist.components.SwipeWrapper
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotesListScreen(
    onNoteClick: (String) -> Unit,
    onCreateNote: () -> Unit,
    viewModel: NotesListViewModel = hiltViewModel(),
) {
    val notes by viewModel.notes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uiEvents = viewModel.uiEvents
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        uiEvents.collectLatest { event ->
            when (event) {
                is NotesListViewModel.UiEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                NotesListViewModel.UiEvent.NoteDeleted -> {
                    snackbarHostState.showSnackbar("Note deleted")
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B0D18))
        ) {
            StarryBackground(configuration = LocalConfiguration.current)
        }
        Scaffold(
            floatingActionButton = {
                SunFloatingActionButton(
                    onClick = onCreateNote,
                    isRotating = true
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { padding ->
            when {
                isLoading -> LoadingIndicator()
                else -> LazyColumn(
                    modifier = Modifier.padding(padding)
                ) {
                    items(notes) { note ->
                        SwipeWrapper(
                            onSwipeDelete = { viewModel.deleteNote(note.uid) },
                            content = {
                                NotesListItem(
                                    note = note,
                                    onClick = { onNoteClick(note.uid) },
                                    onDelete = { viewModel.deleteNote(note.uid) }
                                )
                            }
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


@Composable
private fun SunFloatingActionButton(
    onClick: () -> Unit,
    isRotating: Boolean = true,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    FloatingActionButton(
        onClick = onClick,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
        modifier = Modifier
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.tertiary,
                        MaterialTheme.colorScheme.tertiary
                    ),
                    radius = 100f
                ),
                shape = CircleShape
            )
            .size(64.dp)
    ) {
        Icon(
            imageVector = Icons.Default.WbSunny,
            contentDescription = "Sun",
            modifier = Modifier
                .rotate(if (isRotating) rotation else 0f)
                .size(32.dp),
            tint = Color.White
        )
    }
}