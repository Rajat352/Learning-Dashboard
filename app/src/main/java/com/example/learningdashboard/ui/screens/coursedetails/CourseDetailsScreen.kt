package com.example.learningdashboard.ui.screens.coursedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningdashboard.R
import com.example.learningdashboard.ui.screens.coursedetails.action.CourseDetailsScreenAction
import com.example.learningdashboard.ui.screens.coursedetails.state.CourseDetailsScreenState
import com.example.learningdashboard.ui.screens.coursedetails.state.LessonUiState

@Composable
fun CourseDetailsScreen(
    courseId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CourseDetailsScreenViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(courseId) {
        viewModel.onAction(CourseDetailsScreenAction.LoadCourse(courseId))
    }

    CourseDetailsScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun CourseDetailsScreen(
    state: CourseDetailsScreenState,
    onAction: (CourseDetailsScreenAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
            TextButton(
                onClick = { onAction(CourseDetailsScreenAction.Refresh) },
                enabled = !state.isLoading
            ) { Text(stringResource(R.string.refresh)) }
        }

        state.course?.let { course ->
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = course.title, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = stringResource(R.string.course_progress, course.progress),
                    style = MaterialTheme.typography.bodyLarge
                )
                LinearProgressIndicator(
                    progress = { course.progress / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.lessons_completed_count, course.completedLessonCount, course.lessonCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        state.completionError?.let {
            Text(
                text = it.asString(),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }

        when {
            state.isLessonsCached -> {
                if (state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                state.error?.let {
                    Text(
                        text = it.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.lessons.isEmpty()) {
                        item { Text(stringResource(R.string.lessons_empty)) }
                    }
                    items(state.lessons, key = { it.id }) { lesson ->
                        LessonCard(
                            lesson = lesson,
                            isUpdating = lesson.id in state.updatingLessonIds,
                            onComplete = { onAction(CourseDetailsScreenAction.MarkLessonComplete(lesson.id)) }
                        )
                    }
                }
            }
            state.isLoading || !state.isCacheLoaded -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.error?.asString() ?: stringResource(R.string.lessons_unavailable),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = { onAction(CourseDetailsScreenAction.Refresh) }) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonCard(
    lesson: LessonUiState,
    isUpdating: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = lesson.title, style = MaterialTheme.typography.titleMedium)
            if (lesson.completed) {
                Text(
                    text = stringResource(R.string.lesson_completed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = stringResource(R.string.lesson_pending),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onComplete,
                    enabled = !isUpdating,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(if (isUpdating) R.string.lesson_saving else R.string.mark_complete))
                }
            }
        }
    }
}
