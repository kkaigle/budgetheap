package com.koreykaigle.budgetapp.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.koreykaigle.budgetapp.data.Frequency
import com.koreykaigle.budgetapp.data.entity.Category

/** Editable draft shared by income, expense, and deduction entries -- their forms
 *  are structurally identical, so one editor UI serves all three. */
data class LineItemDraft(
    val id: Long? = null,
    val name: String = "",
    val amount: String = "",
    val categoryId: Long? = null,
    val frequency: Frequency? = null,
    val notes: String = "",
    val customFields: List<Pair<String, String>> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineItemEditorSheet(
    title: String,
    categories: List<Category>,
    initial: LineItemDraft,
    onDismiss: () -> Unit,
    onSave: (LineItemDraft, newCategoryName: String?) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var draft by remember(initial) { mutableStateOf(initial) }
    var pendingNewCategory by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(title, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = draft.name,
                onValueChange = { draft = draft.copy(name = it) },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = draft.amount,
                onValueChange = { value -> if (value.matches(Regex("^\\d*\\.?\\d{0,2}$"))) draft = draft.copy(amount = value) },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = { Text("$") }
            )

            CategoryPicker(
                categories = categories,
                selectedCategoryId = draft.categoryId,
                onSelectExisting = { draft = draft.copy(categoryId = it); pendingNewCategory = null },
                onCreateNew = { newName -> pendingNewCategory = newName; draft = draft.copy(categoryId = null) }
            )

            FrequencyPicker(
                selected = draft.frequency,
                onSelect = { draft = draft.copy(frequency = it) }
            )

            OutlinedTextField(
                value = draft.notes,
                onValueChange = { draft = draft.copy(notes = it) },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Divider()

            CustomFieldsEditor(
                fields = draft.customFields,
                onChange = { draft = draft.copy(customFields = it) }
            )

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onDelete != null) {
                    TextButton(onClick = onDelete) { Text("Delete") }
                }
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                Button(
                    onClick = { onSave(draft, pendingNewCategory) },
                    enabled = draft.name.isNotBlank() && draft.amount.toDoubleOrNull() != null,
                    modifier = Modifier.weight(1f)
                ) { Text("Save") }
            }
        }
    }
}
