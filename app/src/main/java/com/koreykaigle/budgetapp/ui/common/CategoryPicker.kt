package com.koreykaigle.budgetapp.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.koreykaigle.budgetapp.data.entity.Category

/**
 * Lets the user pick an existing category or type a brand-new one on the spot --
 * categories are never predetermined, so "create as you go" is the primary flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPicker(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onSelectExisting: (Long?) -> Unit,
    onCreateNew: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var typedText by remember(selectedCategoryId) {
        mutableStateOf(categories.firstOrNull { it.id == selectedCategoryId }?.name ?: "")
    }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = typedText,
            onValueChange = {
                typedText = it
                expanded = true
            },
            label = { Text("Category (optional)") },
            placeholder = { Text("Uncategorized") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        androidx.compose.material3.ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    typedText = ""
                    onSelectExisting(null)
                    expanded = false
                }
            )
            val matches = categories.filter { it.name.contains(typedText, ignoreCase = true) }
            matches.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        typedText = category.name
                        onSelectExisting(category.id)
                        expanded = false
                    }
                )
            }
            val exactMatch = categories.any { it.name.equals(typedText, ignoreCase = true) }
            if (typedText.isNotBlank() && !exactMatch) {
                DropdownMenuItem(
                    text = {
                        Row {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Text("  Create \"$typedText\"")
                        }
                    },
                    onClick = {
                        onCreateNew(typedText.trim())
                        expanded = false
                    }
                )
            }
        }
    }
}
