package pef.mendelu.musclemaker.ui.screens.add_edit_exercise

data class AddEditExerciseUiState(
    val screenTitle: Int? = null,
    val name: String = "",
    val description: String = "",
    val videoUrl: String = "",
    val nameError: Boolean = false,
    val descriptionError: Boolean = false
)