package pef.mendelu.musclemaker.ui.screens.main

import pef.mendelu.musclemaker.model.Workout

data class MainUiState(
    val workouts: List<Workout> = emptyList()
)