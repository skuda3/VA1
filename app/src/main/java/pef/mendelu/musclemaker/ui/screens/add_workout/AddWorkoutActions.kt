package pef.mendelu.musclemaker.ui.screens.add_workout

sealed interface AddWorkoutAction {
    data class OnNameChange(val name: String) : AddWorkoutAction
    data object OnSaveClick : AddWorkoutAction
}