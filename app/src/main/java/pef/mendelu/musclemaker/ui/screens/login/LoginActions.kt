package pef.mendelu.musclemaker.ui.screens.login

import androidx.activity.result.ActivityResult

sealed interface LoginAction {
    data object OnGoogleSignInClick : LoginAction
    data class OnGoogleSignInResult(val result: ActivityResult) : LoginAction
}