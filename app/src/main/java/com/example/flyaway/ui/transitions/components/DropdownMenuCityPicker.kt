package com.example.flyaway.ui.transitions.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun DropdownMenuCityPicker(
    selectedCity: String,
    onCitySelected: (String) -> Unit
) {
    val cities = mapOf("Barcelona" to "BCN", "Paris" to "PAR", "London" to "LON")
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = cities.entries.firstOrNull { it.value == selectedCity }?.key ?: "Select city")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            cities.forEach { (name, code) ->
                DropdownMenuItem(onClick = {
                    onCitySelected(code)
                    expanded = false
                }, text = { Text(name) })
            }
        }
    }
}
