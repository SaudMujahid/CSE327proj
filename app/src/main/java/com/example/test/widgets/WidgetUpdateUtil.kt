package com.example.test.widgets

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WidgetUpdateUtil {
    fun refreshAll(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            TodoWidget().updateAll(context)
            CalendarWidget().updateAll(context)
            NotesWidget().updateAll(context)
        }
    }
}
