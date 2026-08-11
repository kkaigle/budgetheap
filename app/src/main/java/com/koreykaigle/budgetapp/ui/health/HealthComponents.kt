package com.koreykaigle.budgetapp.ui.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.koreykaigle.budgetapp.util.formatCurrencyCompact

@Composable
fun KpiCard(title: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
fun FlowRow(label: String, amount: Double, emphasize: Boolean = false, tint: Color? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            formatCurrencyCompact(amount),
            style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
            color = tint ?: MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun HorizontalBarRow(label: String, amount: Double, maxAmount: Double, barColor: Color) {
    val fraction = if (maxAmount > 0) (amount / maxAmount).toFloat().coerceIn(0f, 1f) else 0f
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(formatCurrencyCompact(amount), style = MaterialTheme.typography.bodyMedium)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(barColor.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}
