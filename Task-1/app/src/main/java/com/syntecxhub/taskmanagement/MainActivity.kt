package com.syntecxhub.taskmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.room.Room
import com.syntecxhub.taskmanagement.data.local.TaskDatabase
import com.syntecxhub.taskmanagement.data.repository.TaskRepositoryImpl
import com.syntecxhub.taskmanagement.domain.usecase.AddTaskUseCase
import com.syntecxhub.taskmanagement.domain.usecase.DeleteTaskUseCase
import com.syntecxhub.taskmanagement.domain.usecase.GetTasksUseCase
import com.syntecxhub.taskmanagement.presentation.ui.screens.TaskListScreen
import com.syntecxhub.taskmanagement.presentation.ui.theme.TaskManagementTheme
import com.syntecxhub.taskmanagement.presentation.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Simple Manual Dependency Injection for the task
        val db = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java,
            "tasks_db"
        ).build()
        
        val repository = TaskRepositoryImpl(db.dao)
        val viewModel = TaskViewModel(
            getTasksUseCase = GetTasksUseCase(repository),
            addTaskUseCase = AddTaskUseCase(repository),
            deleteTaskUseCase = DeleteTaskUseCase(repository)
        )

        setContent {
            TaskManagementTheme {
                Surface {
                    TaskListScreen(viewModel = viewModel)
                }
            }
        }
    }
}
