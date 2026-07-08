package pef.mendelu.musclemaker.navigation

import java.time.LocalDate

sealed class Destinations(val route: String) {
    data object Login : Destinations("login_screen")
    data object Main : Destinations("main_screen")
    data object AddWorkout : Destinations("add_workout_screen")
    data object WorkoutDetail : Destinations("workout_detail_screen/{workoutId}") {
        fun createRoute(workoutId: Long) = "workout_detail_screen/$workoutId"
    }
    data object AddEditExercise : Destinations("add_edit_exercise_screen/{workoutId}?exerciseId={exerciseId}") {
        fun createRouteForAdd(workoutId: Long) = "add_edit_exercise_screen/$workoutId"
        fun createRouteForEdit(workoutId: Long, exerciseId: Long) = "add_edit_exercise_screen/$workoutId?exerciseId=$exerciseId"
    }
    data object Progress : Destinations("progress_screen")
    data object Profile : Destinations("profile_screen")
    data object AddProgress : Destinations("add_progress_screen/{date}") {
        fun createRoute(date: LocalDate) = "add_progress_screen/${date.toEpochDay()}"
    }
}