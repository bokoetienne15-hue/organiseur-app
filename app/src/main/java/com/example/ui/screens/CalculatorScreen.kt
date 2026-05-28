package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel

@Composable
fun CalculatorScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val calcText by viewModel.calcText.collectAsState()

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Display Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = if (calcText.isEmpty()) "0" else calcText,
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    maxLines = 2
                )
            }

            // Button Grid
            val buttons = listOf(
                listOf("C", "(", ")", "/"),
                listOf("7", "8", "9", "*"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf(".", "0", "DEL", "=")
            )

            buttons.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { btn ->
                        CalculatorButton(
                            text = btn,
                            onClick = { viewModel.onCalcKeyPress(btn) },
                            modifier = Modifier.weight(1f),
                            color = when (btn) {
                                "C", "DEL" -> MaterialTheme.colorScheme.errorContainer
                                "=" -> MaterialTheme.colorScheme.tertiaryContainer
                                "/", "*", "-", "+" -> MaterialTheme.colorScheme.surfaceVariant
                                "(", ")" -> MaterialTheme.colorScheme.surfaceVariant
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            textColor = when (btn) {
                                "C", "DEL" -> MaterialTheme.colorScheme.onErrorContainer
                                "=" -> MaterialTheme.colorScheme.onTertiaryContainer
                                "/", "*", "-", "+" -> MaterialTheme.colorScheme.onSurfaceVariant
                                "(", ")" -> MaterialTheme.colorScheme.onSurfaceVariant
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color,
    textColor: Color
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
