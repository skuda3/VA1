package pef.mendelu.musclemaker.ui.screens.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.flow.collectLatest
import pef.mendelu.musclemaker.R
import pef.mendelu.musclemaker.model.Exercise
import pef.mendelu.musclemaker.navigation.Destinations
import pef.mendelu.musclemaker.ui.theme.AppDarkBackground
import pef.mendelu.musclemaker.ui.theme.AppRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(navController: NavController) {
    val viewModel: WorkoutDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is WorkoutDetailEffect.NavigateUp -> navController.navigateUp()
                is WorkoutDetailEffect.NavigateToAddExercise -> {
                    navController.navigate(Destinations.AddEditExercise.createRouteForAdd(effect.workoutId))
                }
                is WorkoutDetailEffect.NavigateToEditExercise -> {
                    navController.navigate(Destinations.AddEditExercise.createRouteForEdit(effect.workoutId, effect.exerciseId))
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(uiState.workout?.name ?: stringResource(R.string.workout_detail_loading)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onAction(WorkoutDetailAction.OnEditNameClick) }) {
                            Icon(Icons.Default.Edit, stringResource(R.string.workout_detail_edit_workout_name))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = AppDarkBackground, titleContentColor = Color.White,
                        navigationIconContentColor = Color.White, actionIconContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.onAction(WorkoutDetailAction.OnAddExerciseClick) },
                    containerColor = AppRed
                ) {
                    Icon(Icons.Default.Add, stringResource(R.string.add_exercise), tint = Color.White)
                }
            },
            containerColor = AppDarkBackground
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
            ) {
                items(uiState.exercises) { exercise ->
                    ExerciseListItem(
                        exercise = exercise,
                        onDoneClick = { viewModel.onAction(WorkoutDetailAction.OnToggleExerciseDone(exercise)) },
                        onEditClick = { viewModel.onAction(WorkoutDetailAction.OnEditExerciseClick(exercise)) }
                    )
                }
                item {
                    val currentWorkout = uiState.workout
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { viewModel.onAction(WorkoutDetailAction.OnToggleWorkoutComplete) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentWorkout?.isCompleted == true) Color.Gray else AppRed
                            )
                        ) {
                            Text(if (currentWorkout?.isCompleted == true) stringResource(R.string.workout_detail_incomplete) else stringResource(R.string.workout_detail_complete))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        OutlinedButton(
                            onClick = { viewModel.onAction(WorkoutDetailAction.OnDeleteWorkout) },
                            border = BorderStroke(1.dp, AppRed)
                        ) {
                            Text(stringResource(R.string.workout_detail_delete_workout), color = AppRed)
                        }
                    }
                }
            }
        }

        if (uiState.showEditNameDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onAction(WorkoutDetailAction.OnEditNameDialogDismiss) },
                containerColor = AppDarkBackground,
                titleContentColor = Color.White,
                textContentColor = Color.White,
                title = { Text(stringResource(R.string.workout_detail_edit_name_title)) },
                text = {
                    val textFieldColors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = AppRed,
                        focusedIndicatorColor = AppRed,
                        unfocusedIndicatorColor = Color.Gray,
                        focusedLabelColor = AppRed,
                        unfocusedLabelColor = Color.Gray
                    )

                    OutlinedTextField(
                        value = uiState.editNameFieldValue,
                        onValueChange = { viewModel.onAction(WorkoutDetailAction.OnEditNameChange(it)) },
                        label = { Text(stringResource(R.string.workout_detail_new_name_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onAction(WorkoutDetailAction.OnSaveWorkoutName) },
                        colors = ButtonDefaults.buttonColors(containerColor = AppRed)
                    ) {
                        Text(stringResource(R.string.save))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { viewModel.onAction(WorkoutDetailAction.OnEditNameDialogDismiss) },
                        colors = ButtonDefaults.buttonColors(containerColor = AppRed)
                    ) {
                        Text(stringResource(R.string.workout_detail_cancel))
                    }
                }
            )
        }

        if (uiState.showConfetti) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))
            LottieAnimation(composition = composition, modifier = Modifier.fillMaxSize(), iterations = 1)
        }
    }
}
@Composable
fun ExerciseListItem(
    exercise: Exercise,
    onDoneClick: () -> Unit,
    onEditClick: () -> Unit) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.FitnessCenter,
                    contentDescription = stringResource(R.string.workout_detail_exercise),
                    tint = AppRed,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(exercise.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            AnimatedVisibility(visible = !exercise.isDone) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(16.dp))

                    if (exercise.videoUrl != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exercise.videoUrl))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleOutline,
                                contentDescription = stringResource(R.string.workout_detail_video),
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(exercise.name, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(exercise.details, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onEditClick, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = AppRed)) {
                    Text(stringResource(R.string.workout_detail_edit))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onDoneClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AppRed)) {
                    Text(
                        if (exercise.isDone)
                            stringResource(R.string.workout_detail_redo)
                        else stringResource(R.string.workout_detail_done))
                }
            }
        }
    }
}