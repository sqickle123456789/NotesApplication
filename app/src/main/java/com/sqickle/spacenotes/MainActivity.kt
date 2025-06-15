package com.sqickle.spacenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sqickle.spacenotes.ui.createnote.CreateNoteScreen
import com.sqickle.spacenotes.ui.editnote.EditNoteScreen
import com.sqickle.spacenotes.ui.noteslist.NotesListScreen
import com.sqickle.spacenotes.ui.theme.SpaceNotesTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SpaceNotesTheme {
                AppNavigation()
            }
        }
    }
}


@Serializable
object NotesList

@Serializable
data class EditNote(val noteId: String)

@Serializable
object CreateNote

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = NotesList) {
        composable<NotesList> {
            NotesListScreen(
                onNoteClick = { noteId ->
                    navController.navigate(EditNote(noteId))
                },
                onCreateNote = {
                    navController.navigate(CreateNote)
                }
            )
        }

        composable<EditNote> { backStackEntry ->
            val note: EditNote = backStackEntry.toRoute()
            EditNoteScreen(
                noteId = note.noteId,
                onBack = { navController.popBackStack() }
            )
        }

        composable<CreateNote> {
            CreateNoteScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}