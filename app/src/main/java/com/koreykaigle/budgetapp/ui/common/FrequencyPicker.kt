package com.koreykaigle.budgetapp.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.koreykaigle.budgetapp.data.Frequency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrequencyPicker(
    selected: Frequency?,
    onSelect: (Frequency?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = selected?.label ?: "One-time / not set",
            onValueChange = {},
            readOnly = true,
            label = { Text("Frequency (optional)") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("One-time / not set") }, onClick = { onSelect(null); expanded = false })
            Frequency.entries.forEach { freq ->
                DropdownMenuItem(text = { Text(freq.label) }, onClick = { onSelect(freq); expanded = false })
            }
        }
    }
}
