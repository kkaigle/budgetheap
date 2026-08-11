package com.koreykaigle.budgetapp.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Free-form "add your own field" list -- the mechanism that lets any entry carry
 * whatever extra attributes a person cares about (vendor, due day, payment method,
 * priority...) without the app dictating a fixed schema.
 */
@Composable
fun CustomFieldsEditor(
    fields: List<Pair<String, String>>,
    onChange: (List<Pair<String, String>>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Custom fields", style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
        fields.forEachIndexed { index, (name, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { newName ->
                        onChange(fields.toMutableList().also { it[index] = newName to value })
                    },
                    label = { Text("Field name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        onChange(fields.toMutableList().also { it[index] = name to newValue })
                    },
                    label = { Text("Value") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                IconButton(onClick = { onChange(fields.toMutableList().also { it.removeAt(index) }) }) {
                    Icon(Icons.Filled.Close, contentDescription = "Remove field")
                }
            }
        }
        OutlinedButton(onClick = { onChange(fields + ("" to "")) }) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text("  Add custom field")
        }
    }
}
