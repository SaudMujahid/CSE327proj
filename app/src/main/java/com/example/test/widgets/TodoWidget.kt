package com.example.test.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import androidx.glance.background
import com.example.test.MainActivity
import com.example.test.data.models.Task
import com.example.test.data.repository.TaskRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class TodoWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = com.example.test.data.AppDatabase.getDatabase(context)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val allTasks = db.taskDao().getAllTasks().first()
        val todayTasks = allTasks
            .filter { task -> task.date == today }
            .sortedBy { task -> task.isChecked }

        provideContent {
            TodoWidgetContent(tasks = todayTasks)
        }
    }
}

@Composable
private fun TodoWidgetContent(tasks: List<Task>) {
    val bgColor = ColorProvider(Color(0xFF1C1C1E))
    val surfaceColor = ColorProvider(Color(0xFF2C2C2E))
    val primaryColor = ColorProvider(Color(0xFF0A84FF))
    val onBg = ColorProvider(Color.White)
    val subText = ColorProvider(Color(0xFF8E8E93))
    val errorColor = ColorProvider(Color(0xFFFF453A))
    val successColor = ColorProvider(Color(0xFF30D158))

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
            .cornerRadius(20.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {

            // Header row
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today",
                    style = TextStyle(
                        color = onBg,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
                val pending = tasks.count { !it.isChecked }
                if (pending > 0) {
                    Box(
                        modifier = GlanceModifier
                            .background(errorColor)
                            .cornerRadius(12.dp)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$pending left",
                            style = TextStyle(
                                color = ColorProvider(Color.White),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                } else if (tasks.isNotEmpty()) {
                    Box(
                        modifier = GlanceModifier
                            .background(successColor)
                            .cornerRadius(12.dp)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "All done!",
                            style = TextStyle(
                                color = ColorProvider(Color.White),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            Spacer(GlanceModifier.height(12.dp))

            if (tasks.isEmpty()) {
                // Empty state
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎉",
                            style = TextStyle(fontSize = 28.sp)
                        )
                        Spacer(GlanceModifier.height(6.dp))
                        Text(
                            text = "Nothing scheduled today",
                            style = TextStyle(
                                color = subText,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            } else {
                // Task list — show up to 4
                val visible = tasks.take(4)
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    visible.forEach { task ->
                        TaskRow(task = task, surfaceColor = surfaceColor, onBg = onBg, subText = subText, primaryColor = primaryColor)
                        Spacer(GlanceModifier.height(6.dp))
                    }
                    if (tasks.size > 4) {
                        Text(
                            text = "+${tasks.size - 4} more tasks",
                            style = TextStyle(
                                color = primaryColor,
                                fontSize = 12.sp
                            ),
                            modifier = GlanceModifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(
    task: Task,
    surfaceColor: ColorProvider,
    onBg: ColorProvider,
    subText: ColorProvider,
    primaryColor: ColorProvider
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(surfaceColor)
            .cornerRadius(10.dp)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox indicator
        Box(
            modifier = GlanceModifier
                .size(16.dp)
                .background(
                    if (task.isChecked)
                        ColorProvider(Color(0xFF30D158))
                    else
                        ColorProvider(Color(0xFF3A3A3C))
                )
                .cornerRadius(8.dp)
        ) {}

        Spacer(GlanceModifier.width(10.dp))

        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = task.title,
                style = TextStyle(
                    color = if (task.isChecked) subText else onBg,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
            if (task.category.isNotBlank()) {
                Text(
                    text = task.category,
                    style = TextStyle(
                        color = subText,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}