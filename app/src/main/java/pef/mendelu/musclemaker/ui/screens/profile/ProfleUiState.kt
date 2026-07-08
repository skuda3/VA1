package pef.mendelu.musclemaker.ui.screens.profile

import android.net.Uri

data class ProfileUiState(
    val email: String? = null,
    val profilePictureUrl: Uri? = null,
    val appVersion: String = "",
    val selectedLanguage: String = "en",
    val isDeleteDialogVisible: Boolean = false
)