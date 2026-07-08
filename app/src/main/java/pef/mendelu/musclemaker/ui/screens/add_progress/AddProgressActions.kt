package pef.mendelu.musclemaker.ui.screens.add_progress

import android.net.Uri

sealed interface AddProgressAction {
    data class OnWeightChange(val weight: String) : AddProgressAction
    data class OnPhotosSelected(val uris: List<Uri>) : AddProgressAction
    data object OnSaveClick : AddProgressAction
    data object OnAddPhotoClick : AddProgressAction
    data class OnPermissionResult(val isGranted: Boolean) : AddProgressAction
}