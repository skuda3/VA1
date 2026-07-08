package pef.mendelu.musclemaker.ui.screens.detail

import pef.mendelu.musclemaker.model.Exercise
import pef.mendelu.musclemaker.model.Workout

data class WorkoutDetailUiState(
    val workout: Workout? = null,
    val exercises: List<Exercise> = emptyList(),
    val showEditNameDialog: Boolean = false,
    val editNameFieldValue: String = "",
    val showConfetti: Boolean = false
)