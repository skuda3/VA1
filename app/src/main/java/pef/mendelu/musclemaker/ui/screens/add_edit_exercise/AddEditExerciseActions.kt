package pef.mendelu.musclemaker.ui.screens.add_edit_exercise

sealed interface AddEditExerciseAction {
    data class OnNameChange(val name: String) : AddEditExerciseAction
    data class OnDescriptionChange(val description: String) : AddEditExerciseAction
    data class OnVideoUrlChange(val url: String) : AddEditExerciseAction
    data object OnSaveClick : AddEditExerciseAction
    data object OnDeleteClick : AddEditExerciseAction
}