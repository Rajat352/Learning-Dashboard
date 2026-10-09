package com.example.learningdashboard.ui.screens.courses

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningdashboard.R
import com.example.learningdashboard.ui.screens.courses.action.CoursesScreenAction
import com.example.learningdashboard.ui.screens.courses.state.CourseUiState
import com.example.learningdashboard.ui.screens.courses.state.CoursesScreenState

@Composable
fun CoursesScreen(
    modifier: Modifier = Modifier,
    onContinue: (Long) -> Unit
) {
    val viewModel: CoursesScreenViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CoursesScreen(
        state = state,
        onAction = viewModel::onAction,
        onContinue = onContinue,
        modifier = modifier
    )
}

@Composable
fun CoursesScreen(
    state: CoursesScreenState,
    onAction: (CoursesScreenAction) -> Unit,
    onContinue: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.courses_title),
                style = MaterialTheme.typography.headlineMedium
            )
            TextButton(
                onClick = { onAction(CoursesScreenAction.Refresh) },
                enabled = !state.isLoading
            ) {
                Text(stringResource(R.string.refresh))
            }
        }

        when {
            state.courses.isNotEmpty() -> {
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
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.courses, key = { it.id }) { course ->
                        CourseCard(course = course, onContinue = { onContinue(course.id) })
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
                        text = state.error?.asString() ?: stringResource(R.string.courses_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (state.error != null) {
                            MaterialTheme.colorScheme.error
                        } else MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { onAction(CoursesScreenAction.Refresh) }) {
                        Text(stringResource(if (state.error != null) R.string.retry else R.string.refresh))
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseCard(
    course: CourseUiState,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = course.title, style = MaterialTheme.typography.titleLarge)
            Text(
                text = course.instructor,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = pluralStringResource(R.plurals.course_lessons, course.lessonCount, course.lessonCount),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.course_progress, course.progress),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            LinearProgressIndicator(
                progress = { course.progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = onContinue, modifier = Modifier.align(Alignment.End)) {
                Text(stringResource(R.string.continue_course))
            }
        }
    }
}
