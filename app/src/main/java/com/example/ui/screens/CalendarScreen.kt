package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import java.text.SimpleDateFormat
import java.util.*

data class SimpleDate(val year: Int, val month: Int, val day: Int)

fun getToday(): SimpleDate {
    val cal = Calendar.getInstance()
    return SimpleDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
}

@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    var displayMonth by remember { 
        mutableStateOf(Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }) 
    }
    var selectedDate by remember { mutableStateOf(getToday()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Simple Header
        Text(
            text = "Calendrier",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Month Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { 
                    displayMonth = (displayMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) } 
                }) {
                    Text("< Retour", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                
                val monthYearStr = SimpleDateFormat("MMMM yyyy", Locale.FRANCE).format(displayMonth.time)
                Text(
                    text = monthYearStr.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                TextButton(onClick = { 
                    displayMonth = (displayMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) } 
                }) {
                    Text("Suivant >", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Days of week
            Row(modifier = Modifier.fillMaxWidth()) {
                val days = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")
                days.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid
            val daysInMonth = displayMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
            val dayOfWeek = displayMonth.get(Calendar.DAY_OF_WEEK)
            val firstDayOfMonth = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Empty slots for previous month
                items(firstDayOfMonth - 1) {
                    Spacer(modifier = Modifier.padding(8.dp))
                }
                
                // Days
                val today = getToday()
                items(daysInMonth) { day ->
                    val displayYear = displayMonth.get(Calendar.YEAR)
                    val displayMonthValue = displayMonth.get(Calendar.MONTH)
                    val currentDate = SimpleDate(displayYear, displayMonthValue, day + 1)
                    
                    val isSelected = currentDate == selectedDate
                    val isToday = currentDate == today
                    
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isToday -> MaterialTheme.colorScheme.tertiary
                                    else -> Color.Transparent
                                }
                            )
                            .clickable { selectedDate = currentDate },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (day + 1).toString(),
                            color = if (isSelected || isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
