package pef.mendelu.musclemaker.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pef.mendelu.musclemaker.database.IWorkoutRepository
import pef.mendelu.musclemaker.model.Workout
import javax.inject.Inject

sealed interface MainEffect {
    data class NavigateToWorkoutDetail(val workoutId: Long) : MainEffect
    data object NavigateToAddWorkout : MainEffect
    data class Navigate(val route: String) : MainEffect
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: IWorkoutRepository
) : ViewModel() {

    val uiState = repository.getAllWorkouts()
        .map { MainUiState(workouts = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = MainUiState()
        )

    private val _effect = Channel<MainEffect>()
    val effect = _effect.receiveAsFlow()

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.OnAddWorkoutClick -> onAddWorkoutClick()
            is MainAction.OnWorkoutClick -> onWorkoutClick(action.workout.id)
            is MainAction.OnBottomNavClick -> onBottomNavClick(action.route)
        }
    }

    private fun onAddWorkoutClick() {
        viewModelScope.launch {
            _effect.send(MainEffect.NavigateToAddWorkout)
        }
    }

    private fun onWorkoutClick(workoutId: Long) {
        viewModelScope.launch {
            _effect.send(MainEffect.NavigateToWorkoutDetail(workoutId))
        }
    }

    private fun onBottomNavClick(route: String) {
        viewModelScope.launch {
            _effect.send(MainEffect.Navigate(route))
        }
    }
}