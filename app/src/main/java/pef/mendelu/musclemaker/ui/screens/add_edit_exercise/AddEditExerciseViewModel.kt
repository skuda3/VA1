package pef.mendelu.musclemaker.ui.screens.add_edit_exercise

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pef.mendelu.musclemaker.R
import pef.mendelu.musclemaker.database.IWorkoutRepository
import pef.mendelu.musclemaker.model.Exercise
import javax.inject.Inject

sealed interface AddEditExerciseEffect {
    data object NavigateUp : AddEditExerciseEffect
}

@HiltViewModel
class AddEditExerciseViewModel @Inject constructor(
    private val repository: IWorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditExerciseUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<AddEditExerciseEffect>()
    val effect = _effect.receiveAsFlow()

    private val exerciseId: Long = savedStateHandle.get<Long>("exerciseId") ?: -1L
    private val workoutOwnerId: Long = savedStateHandle.get<Long>("workoutId") ?: -1L

    private val isEditing: Boolean
        get() = exerciseId != -1L

    init {
        _uiState.update { it.copy(screenTitle = if (isEditing) R.string.edit_exercise else R.string.add_exercise) }
        if (isEditing) {
            viewModelScope.launch {
                repository.getExerciseById(exerciseId).collect { exercise ->
                    exercise?.let {
                        _uiState.update { currentState ->
                            currentState.copy(
                                name = it.name,
                                description = it.details,
                                videoUrl = it.videoUrl ?: ""
                            )
                        }
                    }
                }
            }
        }
    }

    fun onAction(action: AddEditExerciseAction) {
        when (action) {
            is AddEditExerciseAction.OnNameChange -> onNameChange(action.name)
            is AddEditExerciseAction.OnDescriptionChange -> onDescriptionChange(action.description)
            is AddEditExerciseAction.OnVideoUrlChange -> onVideoUrlChange(action.url)
            is AddEditExerciseAction.OnSaveClick -> save()
            is AddEditExerciseAction.OnDeleteClick -> delete()
        }
    }

    private fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, nameError = false) }
    }

    private fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value, descriptionError = false) }
    }

    private fun onVideoUrlChange(value: String) {
        _uiState.update { it.copy(videoUrl = value) }
    }

    private fun save() {
        if (_uiState.value.name.isBlank()) {
            _uiState.update { it.copy(nameError = true) }
            return
        }
        if (_uiState.value.description.isBlank()) {
            _uiState.update { it.copy(descriptionError = true) }
            return
        }

        viewModelScope.launch {
            val exerciseToSave = Exercise(
                id = if (isEditing) exerciseId else 0,
                workoutOwnerId = workoutOwnerId,
                name = _uiState.value.name,
                details = _uiState.value.description,
                videoUrl = _uiState.value.videoUrl.ifBlank { null }
            )
            repository.upsertExercise(exerciseToSave)

            if (!exerciseToSave.videoUrl.isNullOrBlank()) {
                repository.updateWorkoutHasVideo(workoutOwnerId, true)
            }
            _effect.send(AddEditExerciseEffect.NavigateUp)
        }
    }

    private fun delete() {
        if (isEditing) {
            viewModelScope.launch {
                val exerciseToDelete = Exercise(
                    id = exerciseId,
                    workoutOwnerId = workoutOwnerId,
                    name = _uiState.value.name,
                    details = _uiState.value.description,
                    videoUrl = _uiState.value.videoUrl.ifBlank { null }
                )
                repository.deleteExercise(exerciseToDelete)
                _effect.send(AddEditExerciseEffect.NavigateUp)
            }
        }
    }
}