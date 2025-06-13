package com.sqickle.spacenotes.ui.editnote.components

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color as ComposeColor


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorSelectionSection(
    currentColor: Int,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val defaultColors = listOf(
        Color.RED to "Red",
        Color.BLUE to "Blue",
        Color.GREEN to "Green",
        Color.YELLOW to "Yellow",
        Color.CYAN to "Cyan",
        Color.MAGENTA to "Magenta",
        Color.WHITE to "White"
    )

    var showCustomColorPicker by remember { mutableStateOf(false) }
    var customColor by remember { mutableIntStateOf(currentColor) }

    Column(modifier = modifier) {
        Text(
            text = "Note color:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            defaultColors.forEach { (color, name) ->
                ColorOption(
                    color = color,
                    name = name,
                    isSelected = color == currentColor,
                    onSelected = { onColorSelected(color) }
                )
            }

            CustomColorOption(
                currentColor = currentColor,
                defaultColors = defaultColors.map { it.first },
                onColorSelected = { color ->
                    customColor = color
                    onColorSelected(color)
                },
                onLongPress = { showCustomColorPicker = true }
            )
        }

        if (currentColor !in defaultColors.map { it.first }) {
            Text(
                text = "Custom color:",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(ComposeColor(currentColor), RoundedCornerShape(4.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
            )
        }
    }

    if (showCustomColorPicker) {
        ColorPickerDialog(
            initialColor = customColor,
            onColorSelected = {
                customColor = it
                onColorSelected(it)
            },
            onDismiss = { showCustomColorPicker = false }
        )
    }
}

@Composable
private fun ColorOption(
    color: Int,
    name: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable(onClick = onSelected)
                .background(ComposeColor(color), RoundedCornerShape(4.dp))
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = if (color == Color.WHITE) ComposeColor.Black else ComposeColor.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CustomColorOption(
    currentColor: Int,
    defaultColors: List<Int>,
    onColorSelected: (Int) -> Unit,
    onLongPress: () -> Unit,
) {
    val isSelected = currentColor !in defaultColors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable { onColorSelected(Color.LTGRAY) }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onLongPress() },
                    )
                }
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(ComposeColor.Red, ComposeColor.Green, ComposeColor.Blue)
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = ComposeColor.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Custom color",
                    tint = ComposeColor.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text = "Custom",
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}