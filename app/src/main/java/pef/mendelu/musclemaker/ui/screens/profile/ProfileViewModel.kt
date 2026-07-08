package pef.mendelu.musclemaker.ui.screens.profile

import android.net.Uri
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pef.mendelu.musclemaker.BuildConfig
import pef.mendelu.musclemaker.R
import pef.mendelu.musclemaker.datastore.SettingsDataStore
import javax.inject.Inject

sealed interface ProfileEffect {
    data object NavigateToLogin : ProfileEffect
    data class RecreateActivity(val languageCode: String) : ProfileEffect
    data class Navigate(val route: String) : ProfileEffect
    data class ShowError(@StringRes val messageResId: Int) : ProfileEffect
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<ProfileEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            settingsDataStore.language.collect { lang ->
                val firebaseUser = auth.currentUser
                _uiState.update { currentState ->
                    currentState.copy(
                        email = firebaseUser?.email,
                        profilePictureUrl = firebaseUser?.photoUrl,
                        appVersion = BuildConfig.VERSION_NAME,
                        selectedLanguage = lang
                    )
                }
            }
        }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.OnLanguageChange -> onLanguageSelected(action.languageCode)
            is ProfileAction.OnLogoutClick -> onLogoutClick()
            is ProfileAction.OnDeleteAccountClick -> onShowDeleteDialog()
            is ProfileAction.OnDeleteAccountConfirm -> onDeleteAccount()
            is ProfileAction.OnDeleteAccountDismiss -> onHideDeleteDialog()
            is ProfileAction.OnBottomNavClick -> onBottomNavClick(action.route)
        }
    }

    private fun onBottomNavClick(route: String) {
        viewModelScope.launch {
            _effect.send(ProfileEffect.Navigate(route))
        }
    }

    private fun onLanguageSelected(languageCode: String) {
        viewModelScope.launch {
            settingsDataStore.saveLanguage(languageCode)
            _effect.send(ProfileEffect.RecreateActivity(languageCode))
        }
    }

    private fun onLogoutClick() {
        auth.signOut()
        viewModelScope.launch {
            _effect.send(ProfileEffect.NavigateToLogin)
        }
    }

    private fun onShowDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = true) }
    }

    private fun onHideDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = false) }
    }

    private fun onDeleteAccount() {
        val user = auth.currentUser
        user?.delete()?.addOnCompleteListener { task ->
            viewModelScope.launch {
                if (task.isSuccessful) {
                    _effect.send(ProfileEffect.NavigateToLogin)
                } else {
                    val errorResId = when (task.exception) {
                        is FirebaseAuthRecentLoginRequiredException -> {
                            R.string.profile_screen_error_recent_login
                        }

                        else -> {
                            R.string.profile_screen_error
                        }
                    }
                    _effect.send(ProfileEffect.ShowError(errorResId))
                }
            }
        }
        onHideDeleteDialog()
    }
}