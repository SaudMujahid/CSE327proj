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

class NotesWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { NotesWidgetContent() }
    }
}

@Composable
private fun NotesWidgetContent() {
    val bgColor     = ColorProvider(Color(0xFF1C1C1E))
    val onBg        = ColorProvider(Color.White)
    val subText     = ColorProvider(Color(0xFF8E8E93))

    // Tile colors
    val allColor    = ColorProvider(Color(0xFF2C2C2E))
    val noteColor   = ColorProvider(Color(0xFF1C3A5E))   // blue tint
    val journalColor= ColorProvider(Color(0xFF3B2A4A))   // purple tint
    val listColor   = ColorProvider(Color(0xFF1A3A2A))   // green tint

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bgColor)
            .padding(14.dp)
            .cornerRadius(20.dp)
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {

            // Title
            Text(
                text = "Notes",
                style = TextStyle(
                    color = onBg,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(GlanceModifier.height(12.dp))

            // 2×2 grid using two rows
            Row(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NotesTile(
                    emoji = "📋",
                    label = "View All",
                    bgColor = allColor,
                    textColor = onBg,
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight().padding(end = 5.dp)
                )
                NotesTile(
                    emoji = "📝",
                    label = "Notes",
                    bgColor = noteColor,
                    textColor = onBg,
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight().padding(start = 5.dp)
                )
            }

            Spacer(GlanceModifier.height(8.dp))

            Row(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NotesTile(
                    emoji = "📔",
                    label = "Journal",
                    bgColor = journalColor,
                    textColor = onBg,
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight().padding(end = 5.dp)
                )
                NotesTile(
                    emoji = "✅",
                    label = "List",
                    bgColor = listColor,
                    textColor = onBg,
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight().padding(start = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun NotesTile(
    emoji: String,
    label: String,
    bgColor: ColorProvider,
    textColor: ColorProvider,
    modifier: GlanceModifier = GlanceModifier
) {
    Box(
        modifier = modifier
            .background(bgColor)
            .cornerRadius(14.dp)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, style = TextStyle(fontSize = 26.sp))
            Spacer(GlanceModifier.height(6.dp))
            Text(
                text = label,
                style = TextStyle(
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

