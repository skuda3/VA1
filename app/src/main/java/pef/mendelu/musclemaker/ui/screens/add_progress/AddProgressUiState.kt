package pef.mendelu.musclemaker.ui.screens.add_progress

import android.net.Uri

data class AddProgressUiState(
    val weight: String = "",
    val selectedPhotoUris: List<Uri> = emptyList()
)