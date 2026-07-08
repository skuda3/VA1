package pef.mendelu.musclemaker.ui.screens.add_progress

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pef.mendelu.musclemaker.database.IWorkoutRepository
import pef.mendelu.musclemaker.model.ProgressPhoto
import pef.mendelu.musclemaker.model.WeightEntry
import pef.mendelu.musclemaker.ui.screens.add_progress.AddProgressAction
import pef.mendelu.musclemaker.ui.screens.add_progress.AddProgressUiState
import java.time.LocalDate
import javax.inject.Inject

sealed interface AddProgressEffect {
    data object NavigateUp : AddProgressEffect
    data object LaunchPhotoPicker : AddProgressEffect
    data object LaunchPermissionRequest : AddProgressEffect
    data class ShowSnackbar(val message: Int) : AddProgressEffect
}

@HiltViewModel
class AddProgressViewModel @Inject constructor(
    private val repository: IWorkoutRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val date: LocalDate = LocalDate.ofEpochDay(savedStateHandle.get<Long>("date") ?: 0L)

    private val _uiState = MutableStateFlow(AddProgressUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<AddProgressEffect>()
    val effect = _effect.receiveAsFlow()

    fun onAction(action: AddProgressAction) {
        when (action) {
            is AddProgressAction.OnWeightChange -> onWeightChange(action.weight)
            is AddProgressAction.OnPhotosSelected -> onPhotosSelected(action.uris)
            is AddProgressAction.OnSaveClick -> save()
            is AddProgressAction.OnPermissionResult -> onPermissionResult(action.isGranted)
            is AddProgressAction.OnAddPhotoClick -> onAddPhotoClick()
        }
    }

    private fun onWeightChange(newWeight: String) {
        if (newWeight.matches(Regex("^\\d*\\.?\\d*\$"))) {
            _uiState.update { it.copy(weight = newWeight) }
        }
    }

    private fun onPhotosSelected(uris: List<Uri>) {
        viewModelScope.launch {
            uris.forEach { uri ->
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)
            }
            _uiState.update { it.copy(selectedPhotoUris = it.selectedPhotoUris + uris) }
        }
    }

    private fun save() {
        viewModelScope.launch {
            _uiState.value.weight.toFloatOrNull()?.let {
                repository.upsertWeight(WeightEntry(date = date, weight = it))
            }
            _uiState.value.selectedPhotoUris.forEach { uri ->
                repository.insertPhoto(ProgressPhoto(date = date, imageUri = uri.toString()))
            }
            _effect.send(AddProgressEffect.NavigateUp)
        }
    }

    private fun onAddPhotoClick() {
        viewModelScope.launch {
            _effect.send(AddProgressEffect.LaunchPermissionRequest)
        }
    }

    private fun onPermissionResult(isGranted: Boolean) {
        viewModelScope.launch {
            if (isGranted) {
                _effect.send(AddProgressEffect.LaunchPhotoPicker)
            } else {
                _effect.send(AddProgressEffect.ShowSnackbar(pef.mendelu.musclemaker.R.string.permission_denied_message))
            }
        }
    }
}