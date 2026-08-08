package com.syntecxhub.taskmanagement.presentation.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.syntecxhub.taskmanagement.domain.model.Priority
import com.syntecxhub.taskmanagement.presentation.state.TaskUiEvent
import com.syntecxhub.taskmanagement.presentation.state.TaskUiState
import com.syntecxhub.taskmanagement.presentation.ui.components.*
import com.syntecxhub.taskmanagement.presentation.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    val state by viewModel.state.collectAsState()
    val haptic = LocalHapticFeedback.current
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(state.celebratedTaskId) {
        val id = state.celebratedTaskId ?: return@LaunchedEffect
        kotlinx.coroutines.delay(2000)
        viewModel.onEvent(TaskUiEvent.CelebrationCompleted(id))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                                    Color.Transparent
                                ),
                                endY = 200f
                            )
                        )
                ) {
                    ElevatedHeader(
                        onStatsClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.onEvent(TaskUiEvent.ShowStatsSheet(true))
                        },
                        scrollBehavior = scrollBehavior
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SearchBarCompact(
                            query = state.searchQuery,
                            onQueryChange = {
                                viewModel.onEvent(TaskUiEvent.SearchQueryChanged(it))
                            }
                        )

                        FilterChipsRow(
                            selectedPriority = state.selectedPriority,
                            showCompleted = state.showCompleted,
                            onPrioritySelected = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.onEvent(TaskUiEvent.PriorityFilterChanged(it))
                            },
                            onCompletionSelected = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.onEvent(TaskUiEvent.CompletionFilterChanged(it))
                            }
                        )
                    }
                }
            },
            floatingActionButton = {
                AddFab(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.onEvent(TaskUiEvent.EditTask(null))
                        viewModel.onEvent(TaskUiEvent.ShowAddTaskSheet(true))
                    }
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    state.isLoading -> {
                        TaskListShimmer()
                    }
                    state.tasks.isEmpty() && state.searchQuery.isBlank() && state.selectedPriority == null && state.showCompleted == null -> {
                        EmptyState(modifier = Modifier.align(Alignment.Center))
                    }
                    state.tasks.isEmpty() -> {
                        SearchEmptyState(
                            modifier = Modifier.align(Alignment.Center),
                            query = state.searchQuery
                        )
                    }
                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 12.dp,
                                bottom = 120.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            item {
                                StatsSummaryCard(
                                    stats = state.stats,
                                    onExpandClick = {
                                        viewModel.onEvent(TaskUiEvent.ShowStatsSheet(true))
                                    },
                                    modifier = Modifier.padding(bottom = 14.dp)
                                )
                            }
                            items(
                                state.tasks,
                                key = { it.id ?: it.hashCode() },
                                contentType = { "task_item" }
                            ) { task ->
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(
                                        animationSpec = tween(
                                            300,
                                            easing = FastOutSlowInEasing
                                        )
                                    ) + slideInVertically(
                                        initialOffsetY = { 40 },
                                        animationSpec = tween(
                                            350,
                                            easing = FastOutSlowInEasing
                                        )
                                    ),
                                    exit = slideOutHorizontally(
                                        targetOffsetX = { -it },
                                        animationSpec = tween(250)
                                    ) + fadeOut(animationSpec = tween(200))
                                ) {
                                    TaskItem(
                                        task = task,
                                        onToggleCompletion = {
                                            viewModel.onEvent(TaskUiEvent.ToggleTaskCompletion(task))
                                        },
                                        onDelete = {
                                            viewModel.onEvent(TaskUiEvent.DeleteTask(task))
                                        },
                                        onEditClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            viewModel.onEvent(TaskUiEvent.EditTask(task))
                                            viewModel.onEvent(TaskUiEvent.ShowAddTaskSheet(true))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                state.error?.let { err ->
                    Snackbar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 80.dp)
                            .padding(horizontal = 16.dp),
                        action = {
                            TextButton(onClick = { viewModel.onEvent(TaskUiEvent.RefreshTasks) }) {
                                Text("Retry")
                            }
                        }
                    ) {
                        Text(err)
                    }
                }
            }
        }

        CelebrationBurst(
            trigger = state.celebratedTaskId != null,
            onAnimationComplete = {}
        )
    }

    if (state.showAddTaskSheet) {
        AddTaskBottomSheet(
            onDismiss = {
                viewModel.onEvent(TaskUiEvent.ShowAddTaskSheet(false))
            },
            onAddTask = { title, desc, prio, due, rem ->
                viewModel.onEvent(
                    TaskUiEvent.AddTask(
                        title = title,
                        description = desc,
                        priority = prio,
                        dueDate = due,
                        reminderEnabled = rem
                    )
                )
            },
            onUpdateTask = { task, title, desc, prio, due, rem ->
                viewModel.onEvent(
                    TaskUiEvent.UpdateTask(
                        task = task,
                        title = title,
                        description = desc,
                        priority = prio,
                        dueDate = due,
                        reminderEnabled = rem
                    )
                )
            },
            editingTask = state.editingTask
        )
    }

    if (state.showStatsSheet) {
        StatsDetailSheet(
            stats = state,
            onDismiss = { viewModel.onEvent(TaskUiEvent.ShowStatsSheet(false)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ElevatedHeader(
    onStatsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "SyncTask",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Your productivity companion",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            IconButton(
                onClick = onStatsClick
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Statistics",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.97f),
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
    )
}

@Composable
private fun SearchBarCompact(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Search tasks...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        textStyle = MaterialTheme.typography.bodyLarge,
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}

@Composable
private fun FilterChipsRow(
    selectedPriority: Priority?,
    showCompleted: Boolean?,
    onPrioritySelected: (Priority?) -> Unit,
    onCompletionSelected: (Boolean?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChoiceChip(
                selected = selectedPriority == null,
                onClick = { onPrioritySelected(null) },
                label = "All",
                icon = Icons.Default.AllInclusive
            )
            Priority.values().forEach { p ->
                FilterChoiceChip(
                    selected = selectedPriority == p,
                    onClick = { onPrioritySelected(p) },
                    label = p.name.lowercase().replaceFirstChar { it.uppercase() },
                    dotColor = when (p) {
                        Priority.HIGH -> Color(0xFFFF1744)
                        Priority.MEDIUM -> Color(0xFFFF9100)
                        Priority.LOW -> Color(0xFF00C853)
                    }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChoiceChip(
                selected = showCompleted == null,
                onClick = { onCompletionSelected(null) },
                label = "Any Status",
                icon = Icons.Default.HorizontalRule
            )
            FilterChoiceChip(
                selected = showCompleted == false,
                onClick = { onCompletionSelected(false) },
                label = "Pending",
                icon = Icons.Default.Pending
            )
            FilterChoiceChip(
                selected = showCompleted == true,
                onClick = { onCompletionSelected(true) },
                label = "Done",
                icon = Icons.Default.CheckCircle
            )
        }
    }
}

@Composable
private fun FilterChoiceChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector? = null,
    dotColor: Color? = null
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        },
        leadingIcon = if (dotColor != null) {
            {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(dotColor)
                )
            }
        } else if (icon != null) {
            {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            }
        } else null,
        shape = RoundedCornerShape(999.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Composable
private fun AddFab(
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val hapticOnClick: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        onClick()
    }

    FloatingActionButton(
        onClick = hapticOnClick,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .size(62.dp)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsDetailSheet(
    stats: TaskUiState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
            Text(
                text = "Your Progress",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                StatsProgressRing(
                    stats = stats.stats,
                    size = 130.dp
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    BigStat(
                        label = "Tasks Done",
                        value = "${stats.stats.completedTasks}",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    BigStat(
                        label = "Remaining",
                        value = "${stats.stats.pendingTasks}",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    BigStat(
                        label = "Total",
                        value = "${stats.stats.totalTasks}",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Priority Breakdown",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                PriorityProgressRow(
                    label = "High Priority",
                    count = stats.stats.highPriorityTasks,
                    total = stats.stats.totalTasks,
                    color = Color(0xFFFF1744),
                    container = Color(0xFFFFEBEE)
                )
                PriorityProgressRow(
                    label = "Medium Priority",
                    count = stats.stats.mediumPriorityTasks,
                    total = stats.stats.totalTasks,
                    color = Color(0xFFFF9100),
                    container = Color(0xFFFFF8E1)
                )
                PriorityProgressRow(
                    label = "Low Priority",
                    count = stats.stats.lowPriorityTasks,
                    total = stats.stats.totalTasks,
                    color = Color(0xFF00C853),
                    container = Color(0xFFE8F5E9)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun TaskListScreenPreview() {
    val mockTasks = listOf(
        com.syntecxhub.taskmanagement.domain.model.Task(
            id = 1,
            title = "Review Master Prompt",
            description = "Check all features are working",
            priority = com.syntecxhub.taskmanagement.domain.model.Priority.HIGH,
            isCompleted = false,
            dueDate = java.time.LocalDateTime.now(),
            createdAt = java.time.LocalDateTime.now()
        ),
        com.syntecxhub.taskmanagement.domain.model.Task(
            id = 2,
            title = "Update README",
            description = "Add project details",
            priority = com.syntecxhub.taskmanagement.domain.model.Priority.MEDIUM,
            isCompleted = true,
            dueDate = java.time.LocalDateTime.now(),
            createdAt = java.time.LocalDateTime.now()
        )
    )

    com.syntecxhub.taskmanagement.presentation.ui.theme.TaskManagementTheme {
        Surface {
            // Since we can't easily mock the ViewModel here without a factory/Hilt
            // I'll just show the components or a mock-up of the screen content
            // For a real preview, we'd need a way to pass mock data to TaskListScreen
        }
    }
}

@Composable
private fun BigStat(
    label: String,
    value: String,
    tint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(tint)
        )
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PriorityProgressRow(
    label: String,
    count: Int,
    total: Int,
    color: Color,
    container: Color
) {
    val progress = if (total > 0) count.toFloat() / total.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "priority_progress"
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$count task${if (count != 1) "s" else ""}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(container)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(color, color.copy(alpha = 0.7f))
                        )
                    )
            )
        }
    }
}
