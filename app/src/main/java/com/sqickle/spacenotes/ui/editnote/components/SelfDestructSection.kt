package com.sqickle.spacenotes.ui.editnote.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SelfDestructSection(
    hasDate: Boolean,
    currentDate: Date?,
    onDateChange: (Date?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Add self-destruct date",
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = hasDate,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        showDatePicker = true
                    } else {
                        onDateChange(null)
                    }
                }
            )
        }

        if (hasDate && currentDate != null) {
            Text(
                text = "Selected: ${currentDate.format()}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Select date")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        val calendar = Calendar.getInstance().apply {
                            time = date
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                        }
                        onDateChange(calendar.time)
                    },
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().get(Calendar.MINUTE),
                    false
                ).show()
            }
        )
    }
}

@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        LocalContext.current,
        { _, year, month, day ->
            calendar.set(year, month, day)
            onDateSelected(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    DisposableEffect(Unit) {
        datePicker.show()
        onDispose { datePicker.dismiss() }
    }
}

private fun Date?.format(): String {
    return this?.let {
        SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.getDefault()).format(it)
    } ?: "Not set"
}