package pef.mendelu.musclemaker.ui.screens.detail

import pef.mendelu.musclemaker.model.Exercise

sealed interface WorkoutDetailAction {
    data object OnToggleWorkoutComplete : WorkoutDetailAction
    data object OnDeleteWorkout : WorkoutDetailAction
    data object OnEditNameClick : WorkoutDetailAction
    data object OnEditNameDialogDismiss : WorkoutDetailAction
    data class OnEditNameChange(val newName: String) : WorkoutDetailAction
    data object OnSaveWorkoutName : WorkoutDetailAction
    data class OnToggleExerciseDone(val exercise: Exercise) : WorkoutDetailAction
    data object OnAddExerciseClick : WorkoutDetailAction
    data class OnEditExerciseClick(val exercise: Exercise) : WorkoutDetailAction
}