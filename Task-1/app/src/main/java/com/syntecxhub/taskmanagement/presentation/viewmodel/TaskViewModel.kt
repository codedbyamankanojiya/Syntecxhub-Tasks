package com.syntecxhub.taskmanagement.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntecxhub.taskmanagement.domain.model.Task
import com.syntecxhub.taskmanagement.domain.usecase.AddTaskUseCase
import com.syntecxhub.taskmanagement.domain.usecase.DeleteTaskUseCase
import com.syntecxhub.taskmanagement.domain.usecase.GetTasksUseCase
import com.syntecxhub.taskmanagement.presentation.state.TaskUiEvent
import com.syntecxhub.taskmanagement.presentation.state.TaskUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TaskUiState())
    val state: StateFlow<TaskUiState> = _state.asStateFlow()

    private var getTasksJob: Job? = null

    init {
        loadTasks()
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
                        addTaskUseCase(event.task)
                    } catch (e: Exception) {
                        _state.update { it.copy(error = e.message) }
                    }
                }
            }
            is TaskUiEvent.ToggleTaskCompletion -> {
                viewModelScope.launch {
                    addTaskUseCase(event.task.copy(isCompleted = !event.task.isCompleted))
                }
            }
            is TaskUiEvent.DeleteTask -> {
                viewModelScope.launch {
                    deleteTaskUseCase(event.task)
                }
            }
            TaskUiEvent.RefreshTasks -> loadTasks()
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
}
