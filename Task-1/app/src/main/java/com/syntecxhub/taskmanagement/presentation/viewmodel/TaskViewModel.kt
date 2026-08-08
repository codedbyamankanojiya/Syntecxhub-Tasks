package com.syntecxhub.taskmanagement.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntecxhub.taskmanagement.data.manager.ReminderManager
import com.syntecxhub.taskmanagement.domain.model.Priority
import com.syntecxhub.taskmanagement.domain.model.Task
import com.syntecxhub.taskmanagement.domain.usecase.AddTaskUseCase
import com.syntecxhub.taskmanagement.domain.usecase.DeleteTaskUseCase
import com.syntecxhub.taskmanagement.domain.usecase.GetTasksUseCase
import com.syntecxhub.taskmanagement.domain.usecase.GetTaskStatsUseCase
import com.syntecxhub.taskmanagement.domain.usecase.ToggleTaskCompletionUseCase
import com.syntecxhub.taskmanagement.domain.usecase.UpdateTaskUseCase
import com.syntecxhub.taskmanagement.presentation.state.TaskUiEvent
import com.syntecxhub.taskmanagement.presentation.state.TaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val getTaskStatsUseCase: GetTaskStatsUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val reminderManager: ReminderManager
) : ViewModel() {

    private val _state = MutableStateFlow(TaskUiState())
    val state: StateFlow<TaskUiState> = _state.asStateFlow()

    private var getTasksJob: Job? = null
    private var getStatsJob: Job? = null

    init {
        loadTasks()
        loadStats()
    }

    fun onEvent(event: TaskUiEvent) {
        when (event) {
            is TaskUiEvent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                loadTasks()
            }
            is TaskUiEvent.PriorityFilterChanged -> {
                _state.update { it.copy(selectedPriority = event.priority) }
                loadTasks()
            }
            is TaskUiEvent.CompletionFilterChanged -> {
                _state.update { it.copy(showCompleted = event.showCompleted) }
                loadTasks()
            }
            is TaskUiEvent.AddTask -> {
                viewModelScope.launch {
                    try {
                        val task = Task(
                            title = event.title,
                            description = event.description,
                            priority = event.priority,
                            dueDate = event.dueDate,
                            reminderEnabled = event.reminderEnabled,
                            isCompleted = false,
                            createdAt = LocalDateTime.now()
                        )
                        val newId = addTaskUseCase(task).toInt()
                        if (newId > 0 && task.reminderEnabled) {
                            reminderManager.scheduleReminder(task.copy(id = newId))
                        }
                        _state.update { it.copy(showAddTaskSheet = false) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is TaskUiEvent.UpdateTask -> {
                viewModelScope.launch {
                    try {
                        val updated = event.task.copy(
                            title = event.title,
                            description = event.description,
                            priority = event.priority,
                            dueDate = event.dueDate,
                            reminderEnabled = event.reminderEnabled
                        )
                        updateTaskUseCase(updated)
                        reminderManager.scheduleReminder(updated)
                        _state.update { it.copy(editingTask = null, showAddTaskSheet = false) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is TaskUiEvent.ToggleTaskCompletion -> {
                viewModelScope.launch {
                    val updated = toggleTaskCompletionUseCase(event.task)
                    reminderManager.scheduleReminder(updated)
                    if (updated.isCompleted && updated.priority == Priority.HIGH) {
                        _state.update { it.copy(celebratedTaskId = updated.id) }
                    }
                }
            }
            is TaskUiEvent.DeleteTask -> {
                viewModelScope.launch {
                    reminderManager.cancelReminder(event.task.id)
                    deleteTaskUseCase(event.task)
                }
            }
            is TaskUiEvent.EditTask -> {
                _state.update { it.copy(editingTask = event.task, showAddTaskSheet = event.task != null) }
            }
            is TaskUiEvent.ShowAddTaskSheet -> {
                _state.update { it.copy(showAddTaskSheet = event.show, editingTask = if (!event.show) null else it.editingTask) }
            }
            is TaskUiEvent.ShowStatsSheet -> {
                _state.update { it.copy(showStatsSheet = event.show) }
            }
            is TaskUiEvent.CelebrationCompleted -> {
                _state.update { it.copy(celebratedTaskId = null) }
            }
            is TaskUiEvent.NotificationPermissionResult -> {
                _state.update { it.copy(notificationPermissionGranted = event.granted) }
            }
            TaskUiEvent.RefreshTasks -> {
                loadTasks()
                loadStats()
            }
        }
    }

    private fun loadTasks() {
        getTasksJob?.cancel()
        getTasksJob = getTasksUseCase(
            query = _state.value.searchQuery,
            priority = _state.value.selectedPriority,
            showCompleted = _state.value.showCompleted
        ).onStart {
            _state.update { it.copy(isLoading = true) }
        }.onEach { tasks ->
            _state.update { it.copy(tasks = tasks, isLoading = false, error = null) }
        }.catch { e ->
            _state.update { it.copy(isLoading = false, error = e.message) }
        }.launchIn(viewModelScope)
    }

    private fun loadStats() {
        getStatsJob?.cancel()
        getStatsJob = getTaskStatsUseCase()
            .onEach { stats ->
                _state.update { it.copy(stats = stats) }
            }.catch { }
            .launchIn(viewModelScope)
    }
}
