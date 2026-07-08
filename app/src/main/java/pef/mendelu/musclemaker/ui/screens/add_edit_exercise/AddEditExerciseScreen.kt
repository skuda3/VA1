package pef.mendelu.musclemaker.ui.screens.add_edit_exercise

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import pef.mendelu.musclemaker.R
import pef.mendelu.musclemaker.ui.theme.AppDarkBackground
import pef.mendelu.musclemaker.ui.theme.AppRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExerciseScreen(navController: NavController) {
    val viewModel: AddEditExerciseViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AddEditExerciseEffect.NavigateUp -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = uiState.screenTitle ?: R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (uiState.screenTitle == R.string.edit_exercise) {
                        IconButton(onClick = { viewModel.onAction(AddEditExerciseAction.OnDeleteClick) }) {
                            Icon(Icons.Default.Delete, stringResource(R.string.delete_exercise))
                        }
                    }
                    IconButton(onClick = { viewModel.onAction(AddEditExerciseAction.OnSaveClick) }) {
                        Icon(Icons.Default.Check, stringResource(R.string.save_exercise))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppDarkBackground,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = AppDarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val textFieldColors = TextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = AppRed,
                focusedIndicatorColor = AppRed, unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                errorIndicatorColor = AppRed, errorLabelColor = AppRed, focusedLabelColor = AppRed
            )

            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onAction(AddEditExerciseAction.OnNameChange(it)) },
                label = { Text(stringResource(R.string.exercise_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.nameError,
                colors = textFieldColors
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onAction(AddEditExerciseAction.OnDescriptionChange(it)) },
                label = { Text(stringResource(R.string.exercise_description)) },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                isError = uiState.descriptionError,
                colors = textFieldColors
            )
            OutlinedTextField(
                value = uiState.videoUrl,
                onValueChange = { viewModel.onAction(AddEditExerciseAction.OnVideoUrlChange(it)) },
                label = { Text(stringResource(R.string.exercise_video_url)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )
        }
    }
}