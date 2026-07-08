package pef.mendelu.musclemaker.ui.screens.add_workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pef.mendelu.musclemaker.database.IWorkoutRepository
import pef.mendelu.musclemaker.model.Workout
import javax.inject.Inject

sealed interface AddWorkoutEffect {
    data object NavigateUp : AddWorkoutEffect
}

@HiltViewModel
class AddWorkoutViewModel @Inject constructor(
    private val repository: IWorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddWorkoutUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<AddWorkoutEffect>()
    val effect = _effect.receiveAsFlow()

    fun onAction(action: AddWorkoutAction) {
        when (action) {
            is AddWorkoutAction.OnNameChange -> onWorkoutNameChange(action.name)
            is AddWorkoutAction.OnSaveClick -> saveWorkout()
        }
    }

    private fun onWorkoutNameChange(newName: String) {
        _uiState.update { it.copy(workoutName = newName, nameError = false) }
    }

    private fun saveWorkout() {
        if (_uiState.value.workoutName.isBlank()) {
            _uiState.update { it.copy(nameError = true) }
            return
        }

        viewModelScope.launch {
            val newWorkout = Workout(
                name = _uiState.value.workoutName,
                isCompleted = false,
                hasVideo = false
            )
            repository.insertWorkout(newWorkout)
            _effect.send(AddWorkoutEffect.NavigateUp)
        }
    }
}