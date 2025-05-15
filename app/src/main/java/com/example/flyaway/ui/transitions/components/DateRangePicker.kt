package com.example.flyaway.ui.transitions.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun DateRangePicker(
    startDate: String,
    endDate: String,
    onDatesSelected: (String, String) -> Unit
) {
    var start by remember { mutableStateOf(startDate) }
    var end by remember { mutableStateOf(endDate) }

    Column {
        OutlinedTextField(
            value = start,
            onValueChange = {
                start = it
                onDatesSelected(start, end)
            },
            label = { Text("Start Date (yyyy-MM-dd)") }
        )
        OutlinedTextField(
            value = end,
            onValueChange = {
                end = it
                onDatesSelected(start, end)
            },
            label = { Text("End Date (yyyy-MM-dd)") }
        )
    }
}
