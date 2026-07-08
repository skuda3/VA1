package pef.mendelu.musclemaker.ui.screens.profile

sealed interface ProfileAction {
    data class OnLanguageChange(val languageCode: String) : ProfileAction
    data object OnLogoutClick : ProfileAction
    data object OnDeleteAccountClick : ProfileAction
    data object OnDeleteAccountConfirm : ProfileAction
    data object OnDeleteAccountDismiss : ProfileAction
    data class OnBottomNavClick(val route: String) : ProfileAction
}