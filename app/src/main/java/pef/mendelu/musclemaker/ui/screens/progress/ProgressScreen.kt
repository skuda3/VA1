package pef.mendelu.musclemaker.ui.screens.progress

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.coroutines.flow.collectLatest
import pef.mendelu.musclemaker.R
import pef.mendelu.musclemaker.model.ProgressPhoto
import pef.mendelu.musclemaker.model.WeightEntry
import pef.mendelu.musclemaker.navigation.Destinations
import pef.mendelu.musclemaker.ui.screens.main.MainBottomNavBar
import pef.mendelu.musclemaker.ui.screens.main.MainTopAppBar
import pef.mendelu.musclemaker.ui.theme.AppDarkBackground
import pef.mendelu.musclemaker.ui.theme.AppRed
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(navController: NavController) {
    val viewModel: ProgressViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ProgressEffect.Navigate -> {
                    navController.navigate(effect.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                is ProgressEffect.NavigateToAddProgress -> {
                    navController.navigate(Destinations.AddProgress.createRoute(effect.date))
                }
            }
        }
    }

    Scaffold(
        topBar = { MainTopAppBar() },
        bottomBar = {
            MainBottomNavBar(
                navController = navController,
                onNavClick = { route -> viewModel.onAction(ProgressAction.OnBottomNavClick(route)) }
            )
        },
        containerColor = AppDarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PhotosForDayCard(
                selectedDate = uiState.selectedDate,
                photos = uiState.photosForSelectedDate,
                onAddPhotoClick = { viewModel.onAction(ProgressAction.OnAddProgressClick) },
                onDateTitleClick = { viewModel.onAction(ProgressAction.OnDateTitleClick) }
            )

            WeightChartCard(weightHistory = uiState.weightHistory)
        }

        if (uiState.isDatePickerVisible) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { viewModel.onAction(ProgressAction.OnDatePickerDismiss) },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val newDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                                viewModel.onAction(ProgressAction.OnDateSelected(newDate))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppRed)
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onAction(ProgressAction.OnDatePickerDismiss) }) {
                        Text(stringResource(R.string.workout_detail_cancel), color = AppRed)
                    }
                },
                colors = DatePickerDefaults.colors(containerColor = AppDarkBackground)
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = Color(0xFF2C2C2E), titleContentColor = Color.White,
                        headlineContentColor = Color.White, weekdayContentColor = Color.Gray,
                        currentYearContentColor = AppRed, selectedYearContentColor = Color.White,
                        selectedYearContainerColor = AppRed, selectedDayContainerColor = AppRed,
                        selectedDayContentColor = Color.White, todayContentColor = AppRed,
                        todayDateBorderColor = AppRed, dayContentColor = Color.White,
                        disabledDayContentColor = Color.DarkGray
                    )
                )
            }
        }
    }
}

@Composable
fun PhotosForDayCard(
    selectedDate: LocalDate,
    photos: List<ProgressPhoto>,
    onAddPhotoClick: () -> Unit,
    onDateTitleClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDateTitleClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(
                        R.string.progress_screen_photos_date,
                        selectedDate.format(DateTimeFormatter.ofPattern("d. MMMM uuuu"))
                    ),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.progress_screen_change_date),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (photos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = onAddPhotoClick, colors = ButtonDefaults.buttonColors(containerColor = AppRed)) {
                        Text(stringResource(R.string.progress_screen_add))
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(photos) { photo ->
                        AsyncImage(
                            model = Uri.parse(photo.imageUri),
                            contentDescription = stringResource(R.string.progress_screen_progress_photo),
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeightChartCard(weightHistory: List<WeightEntry>) {
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }
    LaunchedEffect(weightHistory) {
        chartEntryModelProducer.setEntries(
            weightHistory.mapIndexed { index, entry -> entryOf(index.toFloat(), entry.weight) }
        )
    }

    val chartStyle = currentChartStyle.copy(
        axis = currentChartStyle.axis.copy(
            axisLabelColor = Color.White,
            axisLineColor = Color.DarkGray,
            axisTickColor = Color.DarkGray
        ),
        lineChart = currentChartStyle.lineChart.copy(
            lines = listOf(
                LineChart.LineSpec(lineColor = AppRed.toArgb())
            )
        )
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                stringResource(R.string.progress_screen_weight_change),
                color = Color.White,
                fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            if (weightHistory.isNotEmpty()) {
                ProvideChartStyle(chartStyle = chartStyle) {
                    Chart(
                        chart = lineChart(),
                        chartModelProducer = chartEntryModelProducer,
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis(
                            valueFormatter = { value, _ ->
                                weightHistory.getOrNull(value.toInt())?.date?.format(DateTimeFormatter.ofPattern("d.M")) ?: ""
                            }
                        ),
                        modifier = Modifier.height(200.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.progress_screen_weight_no_data), color = Color.Gray)
                }
            }
        }
    }
}
