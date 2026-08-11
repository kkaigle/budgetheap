package com.koreykaigle.budgetapp.ui.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.koreykaigle.budgetapp.data.AccountKind
import com.koreykaigle.budgetapp.ui.common.CustomFieldsEditor
import com.koreykaigle.budgetapp.ui.common.FrequencyPicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditorSheet(
    initial: AccountDraft,
    onDismiss: () -> Unit,
    onSave: (AccountDraft) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var draft by remember(initial) { mutableStateOf(initial) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(if (initial.id == null) "Add account" else "Edit account", style = MaterialTheme.typography.titleLarge)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AccountKind.entries.forEach { kind ->
                    FilterChip(
                        selected = draft.kind == kind,
                        onClick = { draft = draft.copy(kind = kind) },
                        label = { Text(if (kind == AccountKind.ASSET) "Asset" else "Liability") }
                    )
                }
            }

            OutlinedTextField(
                value = draft.name,
                onValueChange = { draft = draft.copy(name = it) },
                label = { Text("Name (e.g. \"Roth IRA\", \"Car Loan\")") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = draft.label,
                onValueChange = { draft = draft.copy(label = it) },
                label = { Text("Type / label (optional, e.g. \"Retirement\", \"Savings\", \"Debt\")") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = draft.balance,
                onValueChange = { v -> if (v.matches(Regex("^\\d*\\.?\\d{0,2}$"))) draft = draft.copy(balance = v) },
                label = { Text("Current balance") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = { Text("$") }
            )

            OutlinedTextField(
                value = draft.contributionAmount,
                onValueChange = { v -> if (v.matches(Regex("^\\d*\\.?\\d{0,2}$"))) draft = draft.copy(contributionAmount = v) },
                label = { Text("Recurring contribution (optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = { Text("$") }
            )

            FrequencyPicker(
                selected = draft.contributionFrequency,
                onSelect = { draft = draft.copy(contributionFrequency = it) }
            )

            OutlinedTextField(
                value = draft.interestRatePct,
                onValueChange = { v -> if (v.matches(Regex("^\\d*\\.?\\d{0,2}$"))) draft = draft.copy(interestRatePct = v) },
                label = { Text("Interest / growth rate % (optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                suffix = { Text("%") }
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onDelete != null) {
                    TextButton(onClick = onDelete) { Text("Delete") }
                }
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                Button(
                    onClick = { onSave(draft) },
                    enabled = draft.name.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) { Text("Save") }
            }
        }
    }
}
