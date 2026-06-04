package com.example.test.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import androidx.glance.background
import com.example.test.MainActivity
import com.example.test.data.repository.TaskRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class CalendarWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = com.example.test.data.AppDatabase.getDatabase(context)
        val allTasks = db.taskDao().getAllTasks().first()
        val taskDateSet = allTasks.map { task -> task.date }.toSet()

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val today = cal.get(Calendar.DAY_OF_MONTH)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val firstDay = Calendar.getInstance().apply { set(year, month, 1) }
        val startDow = firstDay.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = firstDay.getActualMaximum(Calendar.DAY_OF_MONTH)
        val monthLabel = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(firstDay.time)
        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        data class DayCell(val day: Int?, val dateStr: String?, val hasTask: Boolean)

        val cells = mutableListOf<DayCell>()
        repeat(startDow) { cells.add(DayCell(null, null, false)) }
        for (d in 1..daysInMonth) {
            val c = Calendar.getInstance().apply { set(year, month, d) }
            val ds = dateFmt.format(c.time)
            cells.add(DayCell(d, ds, taskDateSet.contains(ds)))
        }
        while (cells.size % 7 != 0) cells.add(DayCell(null, null, false))

        provideContent {
            CalendarWidgetContent(
                monthLabel = monthLabel,
                cells = cells.map { cell -> Triple(cell.day, cell.dateStr, cell.hasTask) },
                today = today,
                todayStr = todayStr
            )
        }
    }
}

@Composable
private fun CalendarWidgetContent(
    monthLabel: String,
    cells: List<Triple<Int?, String?, Boolean>>,
    today: Int,
    todayStr: String
) {
    val bgColor     = ColorProvider(Color(0xFF1C1C1E))
    val surfaceColor= ColorProvider(Color(0xFF2C2C2E))
    val onBg        = ColorProvider(Color.White)
    val subText     = ColorProvider(Color(0xFF8E8E93))
    val primaryColor= ColorProvider(Color(0xFF0A84FF))
    val taskDot     = ColorProvider(Color(0xFFFF9F0A))
    val daysOfWeek  = listOf("S","M","T","W","T","F","S")

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bgColor)
            .padding(14.dp)
            .cornerRadius(20.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {

            // Month header
            Text(
                text = monthLabel,
                style = TextStyle(
                    color = onBg,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(GlanceModifier.height(10.dp))

            // Day-of-week header
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                daysOfWeek.forEach { d ->
                    Box(
                        modifier = GlanceModifier.defaultWeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = d,
                            style = TextStyle(
                                color = subText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }

            Spacer(GlanceModifier.height(6.dp))

            // Calendar grid — chunk into rows of 7
            val weeks = cells.chunked(7)
            weeks.forEach { week ->
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    week.forEach { (day, dateStr, hasTask) ->
                        val isToday = day != null && dateStr == todayStr
                        Box(
                            modifier = GlanceModifier
                                .defaultWeight()
                                .padding(2.dp)
                                .background(
                                    when {
                                        isToday -> primaryColor
                                        day != null && hasTask -> ColorProvider(Color(0xFF3A3A3C))
                                        else -> ColorProvider(Color.Transparent)
                                    }
                                )
                                .cornerRadius(8.dp)
                                .padding(vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day?.toString() ?: "",
                                    style = TextStyle(
                                        color = when {
                                            isToday -> ColorProvider(Color.White)
                                            day == null -> ColorProvider(Color.Transparent)
                                            else -> onBg
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center
                                    )
                                )
                                if (day != null && hasTask && !isToday) {
                                    Spacer(GlanceModifier.height(2.dp))
                                    Box(
                                        modifier = GlanceModifier
                                            .size(4.dp)
                                            .background(taskDot)
                                            .cornerRadius(2.dp)
                                    ) {}
                                }
                            }
                        }
                    }
                }
                Spacer(GlanceModifier.height(2.dp))
            }
        }
    }
}

