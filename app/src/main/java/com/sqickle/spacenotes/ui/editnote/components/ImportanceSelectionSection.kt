package com.sqickle.spacenotes.ui.editnote.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sqickle.spacenotes.data.model.Importance

data class ImportanceOptionColors(
    val text: String,
    val containerColor: androidx.compose.ui.graphics.Color,
    val contentColor: androidx.compose.ui.graphics.Color,
)

@Composable
fun ImportanceSelectionSection(
    currentImportance: Importance,
    onImportanceSelected: (Importance) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Importance:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Importance.entries.forEach { importance ->
                    ImportanceOption(
                        importance = importance,
                        isSelected = currentImportance == importance,
                        onSelected = { onImportanceSelected(importance) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportanceOption(
    importance: Importance,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = when (importance) {
        Importance.HIGH -> ImportanceOptionColors(
            text = Importance.HIGH.getEmojiName(),
            containerColor = if (isSelected) colorScheme.errorContainer
            else colorScheme.surfaceVariant,
            contentColor = if (isSelected) colorScheme.onErrorContainer
            else colorScheme.onSurfaceVariant
        )

        Importance.NORMAL -> ImportanceOptionColors(
            text = Importance.NORMAL.getEmojiName(),
            containerColor = if (isSelected) colorScheme.primaryContainer
            else colorScheme.surfaceVariant,
            contentColor = if (isSelected) colorScheme.onPrimaryContainer
            else colorScheme.onSurfaceVariant
        )

        Importance.LOW -> ImportanceOptionColors(
            text = Importance.LOW.getEmojiName(),
            containerColor = if (isSelected) colorScheme.tertiaryContainer
            else colorScheme.surfaceVariant,
            contentColor = if (isSelected) colorScheme.onTertiaryContainer
            else colorScheme.onSurfaceVariant
        )
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = colors.containerColor,
        contentColor = colors.contentColor,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) colorScheme.primary
            else colorScheme.outline
        ),
        onClick = onSelected,
        modifier = modifier
            .wrapContentSize()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = colors.text,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 16.dp
            )
        )
    }
}