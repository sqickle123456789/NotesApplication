package com.sqickle.spacenotes.ui.editnote.components

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun ColorPickerDialog(
    initialColor: Int,
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var currentColor by remember { mutableIntStateOf(initialColor) }
    val hsv = remember { FloatArray(3) }.apply {
        Color.RGBToHSV(
            Color.red(initialColor),
            Color.green(initialColor),
            Color.blue(initialColor),
            this
        )
    }
    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var saturation by remember { mutableFloatStateOf(hsv[1]) }
    var value by remember { mutableFloatStateOf(hsv[2]) }

    val brush = Brush.sweepGradient(
        colors = listOf(
            ComposeColor.Red,
            ComposeColor.Yellow,
            ComposeColor.Green,
            ComposeColor.Cyan,
            ComposeColor.Blue,
            ComposeColor.Magenta,
            ComposeColor.Red
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select color") },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(ComposeColor(currentColor))
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(brush = brush)
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                val size = size
                                val x = change.position.x.coerceIn(0f, size.width.toFloat())
                                val y = change.position.y.coerceIn(0f, size.height.toFloat())

                                hue = (x / size.width) * 360f
                                saturation = (y / size.height).coerceIn(0f, 1f)

                                currentColor = Color.HSVToColor(
                                    floatArrayOf(hue, saturation, value)
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .border(2.dp, ComposeColor.White, CircleShape)
                            .background(ComposeColor(currentColor), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Brightness:", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = value,
                    onValueChange = {
                        value = it
                        currentColor = Color.HSVToColor(
                            floatArrayOf(hue, saturation, value)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onColorSelected(currentColor)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}