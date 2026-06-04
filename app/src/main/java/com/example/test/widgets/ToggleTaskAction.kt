package com.example.test.widgets

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.example.test.data.AppDatabase
import com.example.test.data.models.Task

class ToggleTaskAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val taskId = parameters[TaskIdKey] ?: return
        val db = AppDatabase.getDatabase(context)
        val taskDao = db.taskDao()
        
        val task = taskDao.getTaskById(taskId)
        if (task != null) {
            taskDao.updateTask(task.copy(isChecked = !task.isChecked))
            
            // Update all related widgets
            TodoWidget().updateAll(context)
            CalendarWidget().updateAll(context)
        }
    }

    companion object {
        val TaskIdKey = ActionParameters.Key<Long>("taskId")
    }
}
