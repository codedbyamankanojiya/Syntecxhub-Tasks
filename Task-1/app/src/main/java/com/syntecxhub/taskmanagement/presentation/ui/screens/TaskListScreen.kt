package com.syntecxhub.taskmanagement.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.syntecxhub.taskmanagement.domain.model.Priority
import com.syntecxhub.taskmanagement.presentation.state.TaskUiEvent
import com.syntecxhub.taskmanagement.presentation.ui.components.AddTaskBottomSheet
import com.syntecxhub.taskmanagement.presentation.ui.components.TaskItem
import com.syntecxhub.taskmanagement.presentation.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    val state by viewModel.state.collectAsState()
    var showAddTaskSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                LargeTopAppBar(
                    title = { Text("SyncTask") },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onEvent(TaskUiEvent.SearchQueryChanged(it)) },
                    onSearch = {},
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text("Search tasks...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) { }
                
                FilterChips(
                    selectedPriority = state.selectedPriority,
                    onPrioritySelected = { viewModel.onEvent(TaskUiEvent.PriorityFilterChanged(it)) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.tasks.isEmpty()) {
                EmptyState(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(state.tasks, key = { it.id ?: it.hashCode() }) { task ->
                        TaskItem(
                            task = task,
                            onToggleCompletion = { viewModel.onEvent(TaskUiEvent.ToggleTaskCompletion(task)) },
                            onDelete = { viewModel.onEvent(TaskUiEvent.DeleteTask(task)) }
                        )
                    }
                }
            }
        }
    }
    
    if (showAddTaskSheet) {
        AddTaskBottomSheet(
            onDismiss = { showAddTaskSheet = false },
            onAddTask = { viewModel.onEvent(TaskUiEvent.AddTask(it)) }
        )
    }
}

@Composable
fun FilterChips(
    selectedPriority: Priority?,
    onPrioritySelected: (Priority?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedPriority == null,
            onClick = { onPrioritySelected(null) },
            label = { Text("All") }
        )
        Priority.values().forEach { priority ->
            FilterChip(
                selected = selectedPriority == priority,
                onClick = { onPrioritySelected(priority) },
                label = { Text(priority.name) }
            )
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No tasks found",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "Tap + to add a new task",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
