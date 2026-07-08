package pef.mendelu.musclemaker.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import pef.mendelu.musclemaker.database.IWorkoutRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch


sealed interface ProgressEffect {
    data class Navigate(val route: String) : ProgressEffect
    data class NavigateToAddProgress(val date: LocalDate) : ProgressEffect
}

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val repository: IWorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<ProgressEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            _uiState.map { it.selectedDate }.distinctUntilChanged().flatMapLatest { date ->
                repository.getPhotosForDate(date)
            }.collect { photos ->
                _uiState.update { it.copy(photosForSelectedDate = photos) }
            }
        }

        viewModelScope.launch {
            repository.getAllWeightEntries().collect { weightHistory ->
                _uiState.update { it.copy(weightHistory = weightHistory) }
            }
        }
    }

    fun onAction(action: ProgressAction) {
        when (action) {
            is ProgressAction.OnDateTitleClick -> _uiState.update { it.copy(isDatePickerVisible = true) }
            is ProgressAction.OnDatePickerDismiss -> _uiState.update { it.copy(isDatePickerVisible = false) }
            is ProgressAction.OnDateSelected -> onDateSelected(action.date)
            is ProgressAction.OnAddProgressClick -> onAddProgressClick()
            is ProgressAction.OnBottomNavClick -> onBottomNavClick(action.route)
        }
    }

    private fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, isDatePickerVisible = false) }
    }

    private fun onAddProgressClick() {
        viewModelScope.launch {
            _effect.send(ProgressEffect.NavigateToAddProgress(_uiState.value.selectedDate))
        }
    }

    private fun onBottomNavClick(route: String) {
        viewModelScope.launch {
            _effect.send(ProgressEffect.Navigate(route))
        }
    }
}