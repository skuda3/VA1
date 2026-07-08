package pef.mendelu.musclemaker.ui.screens.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed interface LoginEffect {
    data class LaunchGoogleSignIn(val signInIntent: Intent) : LoginEffect
    data object NavigateToMain : LoginEffect
    data class ShowAuthError(val message: String) : LoginEffect
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnGoogleSignInClick -> onGoogleSignInClick()
            is LoginAction.OnGoogleSignInResult -> onGoogleSignInResult(action.result)
        }
    }

    private fun onGoogleSignInClick() {
        viewModelScope.launch {
            _effect.send(LoginEffect.LaunchGoogleSignIn(googleSignInClient.signInIntent))
        }
    }

    private fun onGoogleSignInResult(result: androidx.activity.result.ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            return
        }
        val data = result.data ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val credential = GoogleAuthProvider.getCredential(
                    GoogleSignIn.getSignedInAccountFromIntent(data).await().idToken,
                    null
                )
                auth.signInWithCredential(credential).await()
                _effect.send(LoginEffect.NavigateToMain)

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _effect.send(LoginEffect.ShowAuthError(e.message ?: "Neznámá chyba při přihlašování"))
            }
        }
    }
}