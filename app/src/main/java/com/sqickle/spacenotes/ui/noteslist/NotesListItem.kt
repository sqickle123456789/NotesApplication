package com.sqickle.spacenotes.ui.noteslist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sqickle.spacenotes.data.model.Importance
import com.sqickle.spacenotes.data.model.Note
import com.sqickle.spacenotes.ui.theme.errorContainerDark
import com.sqickle.spacenotes.ui.theme.errorDark
import com.sqickle.spacenotes.ui.theme.onSurfaceDark
import com.sqickle.spacenotes.ui.theme.onSurfaceVariantDark
import com.sqickle.spacenotes.ui.theme.primaryContainerDark
import com.sqickle.spacenotes.ui.theme.primaryDark
import com.sqickle.spacenotes.ui.theme.secondaryContainerDark
import com.sqickle.spacenotes.ui.theme.secondaryDark
import com.sqickle.spacenotes.ui.theme.surfaceContainerHighDark
import com.sqickle.spacenotes.ui.theme.tertiaryDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotesListItem(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = surfaceContainerHighDark.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = primaryDark.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = onSurfaceDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete note",
                            tint = errorDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = onSurfaceVariantDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImportanceBadge(importance = note.importance)

                    note.selfDestructDate?.let { date ->
                        Text(
                            text = "Destruct: ${date.formatShort()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = tertiaryDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImportanceBadge(importance: Importance) {
    val (text, color, bgColor) = when (importance) {
        Importance.HIGH -> Triple("!! HIGH !!", errorDark, errorContainerDark)
        Importance.LOW -> Triple("low", secondaryDark, secondaryContainerDark)
        Importance.NORMAL -> Triple("normal", primaryDark, primaryContainerDark)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(
                color = bgColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
private fun Date.formatShort(): String {
    return SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(this)
}