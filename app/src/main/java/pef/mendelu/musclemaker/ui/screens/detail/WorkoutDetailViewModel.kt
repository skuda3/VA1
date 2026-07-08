package pef.mendelu.musclemaker.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pef.mendelu.musclemaker.database.IWorkoutRepository
import pef.mendelu.musclemaker.model.Exercise
import pef.mendelu.musclemaker.model.Workout
import javax.inject.Inject

sealed interface WorkoutDetailEffect {
    data object NavigateUp : WorkoutDetailEffect
    data class NavigateToAddExercise(val workoutId: Long) : WorkoutDetailEffect
    data class NavigateToEditExercise(val workoutId: Long, val exerciseId: Long) : WorkoutDetailEffect
}

@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    private val repository: IWorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<WorkoutDetailEffect>()
    val effect = _effect.receiveAsFlow()

    private val workoutId: Long = savedStateHandle.get<Long>("workoutId") ?: -1L

    init {
        viewModelScope.launch {
            combine(
                repository.getWorkoutById(workoutId),
                repository.getExercisesForWorkout(workoutId)
            ) { workout, exercises ->
                _uiState.update {
                    it.copy(
                        workout = workout,
                        exercises = exercises
                    )
                }
            }.collect()
        }
    }

    fun onAction(action: WorkoutDetailAction) {
        when (action) {
            is WorkoutDetailAction.OnToggleWorkoutComplete -> toggleWorkoutCompleted()
            is WorkoutDetailAction.OnDeleteWorkout -> deleteWorkout()
            is WorkoutDetailAction.OnEditNameClick -> onEditNameClick()
            is WorkoutDetailAction.OnEditNameDialogDismiss -> onEditNameDialogDismiss()
            is WorkoutDetailAction.OnEditNameChange -> onEditNameChange(action.newName)
            is WorkoutDetailAction.OnSaveWorkoutName -> saveWorkoutName()
            is WorkoutDetailAction.OnToggleExerciseDone -> toggleExerciseDone(action.exercise)
            is WorkoutDetailAction.OnAddExerciseClick -> onAddExerciseClick()
            is WorkoutDetailAction.OnEditExerciseClick -> onEditExerciseClick(action.exercise)
        }
    }

    private fun toggleWorkoutCompleted() {
        viewModelScope.launch {
            _uiState.value.workout?.let { workout ->
                repository.updateWorkout(workout.copy(isCompleted = !workout.isCompleted))
            }
        }
    }

    private fun deleteWorkout() {
        viewModelScope.launch {
            _uiState.value.workout?.let {
                repository.deleteWorkout(it)
                _effect.send(WorkoutDetailEffect.NavigateUp)
            }
        }
    }

    private fun onEditNameClick() {
        _uiState.update { it.copy(
            showEditNameDialog = true,
            editNameFieldValue = it.workout?.name ?: ""
        )}
    }

    private fun onEditNameDialogDismiss() {
        _uiState.update { it.copy(showEditNameDialog = false) }
    }

    private fun onEditNameChange(newName: String) {
        _uiState.update { it.copy(editNameFieldValue = newName) }
    }

    private fun saveWorkoutName() {
        val newName = _uiState.value.editNameFieldValue
        if (newName.isNotBlank()) {
            viewModelScope.launch {
                _uiState.value.workout?.let {
                    repository.updateWorkout(it.copy(name = newName))
                }
                onEditNameDialogDismiss()
            }
        }
    }

    private fun toggleExerciseDone(exercise: Exercise) {
        viewModelScope.launch {
            val updatedExercise = exercise.copy(isDone = !exercise.isDone)
            repository.upsertExercise(updatedExercise)
            if (updatedExercise.isDone) {
                triggerConfettiAnimation()
            }
        }
    }

    private fun triggerConfettiAnimation() {
        viewModelScope.launch {
            _uiState.update { it.copy(showConfetti = true) }
            kotlinx.coroutines.delay(2000)
            _uiState.update { it.copy(showConfetti = false) }
        }
    }

    private fun onAddExerciseClick() {
        viewModelScope.launch {
            _effect.send(WorkoutDetailEffect.NavigateToAddExercise(workoutId))
        }
    }

    private fun onEditExerciseClick(exercise: Exercise) {
        viewModelScope.launch {
            _effect.send(WorkoutDetailEffect.NavigateToEditExercise(workoutId, exercise.id))
        }
    }
}