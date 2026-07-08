package pef.mendelu.musclemaker.ui.screens.add_workout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
fun AddWorkoutScreen(navController: NavController) {
    val viewModel: AddWorkoutViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AddWorkoutEffect.NavigateUp -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_workout_new_workout)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onAction(AddWorkoutAction.OnSaveClick) }) {
                        Icon(Icons.Default.Check, contentDescription = stringResource(R.string.add_workout_save_workout))
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
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = uiState.workoutName,
                onValueChange = { viewModel.onAction(AddWorkoutAction.OnNameChange(it)) },
                label = { Text(stringResource(id = R.string.add_workout_workout_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.nameError,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = AppRed,
                    focusedIndicatorColor = AppRed,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                    errorIndicatorColor = AppRed,
                    errorLabelColor = AppRed,
                    focusedLabelColor = AppRed
                )
            )
            if (uiState.nameError) {
                Text(
                    text = stringResource(id = R.string.add_workout_workout_name_cannot_be_empty),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
    }
}