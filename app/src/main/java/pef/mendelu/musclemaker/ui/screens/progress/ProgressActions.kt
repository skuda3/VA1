package pef.mendelu.musclemaker.ui.screens.progress

import java.time.LocalDate

sealed interface ProgressAction {
    data object OnDateTitleClick : ProgressAction
    data object OnDatePickerDismiss : ProgressAction
    data class OnDateSelected(val date: LocalDate) : ProgressAction
    data object OnAddProgressClick : ProgressAction
    data class OnBottomNavClick(val route: String) : ProgressAction
}