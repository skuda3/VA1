package pef.mendelu.musclemaker.ui.screens.main

import pef.mendelu.musclemaker.model.Workout

sealed interface MainAction {
    data object OnAddWorkoutClick : MainAction
    data class OnWorkoutClick(val workout: Workout) : MainAction
    data class OnBottomNavClick(val route: String) : MainAction
}