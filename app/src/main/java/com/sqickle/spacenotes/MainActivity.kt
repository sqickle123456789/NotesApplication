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
import com.sqickle.spacenotes.ui.createnote.CreateNoteScreen
import com.sqickle.spacenotes.ui.editnote.EditNoteScreen
import com.sqickle.spacenotes.ui.noteslist.NotesListScreen
import com.sqickle.spacenotes.ui.theme.SpaceNotesTheme
import dagger.hilt.android.AndroidEntryPoint

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


@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "notesList") {
        composable("notesList") {
            NotesListScreen(
                onNoteClick = { noteId ->
                    navController.navigate("editNote/$noteId")
                },
                onCreateNote = {
                    navController.navigate("createNote")
                }
            )
        }
        composable("editNote/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            EditNoteScreen(
                noteId = noteId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("createNote") {
            CreateNoteScreen(
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}
