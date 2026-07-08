package pef.mendelu.musclemaker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pef.mendelu.musclemaker.ui.screens.login.LoginScreen
import pef.mendelu.musclemaker.ui.screens.main.MainScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import pef.mendelu.musclemaker.ui.screens.detail.WorkoutDetailScreen
import pef.mendelu.musclemaker.ui.screens.add_edit_exercise.AddEditExerciseScreen
import pef.mendelu.musclemaker.ui.screens.add_progress.AddProgressScreen
import pef.mendelu.musclemaker.ui.screens.add_workout.AddWorkoutScreen
import pef.mendelu.musclemaker.ui.screens.profile.ProfileScreen
import pef.mendelu.musclemaker.ui.screens.progress.ProgressScreen

@Composable
fun NavGraph(
    startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destinations.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Destinations.Main.route) {
            MainScreen(navController = navController)
        }
        composable(
            route = Destinations.WorkoutDetail.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
            WorkoutDetailScreen(navController = navController)
        }
        composable(
            route = Destinations.AddEditExercise.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.LongType },
                navArgument("exerciseId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            AddEditExerciseScreen(navController = navController)
        }
        composable(Destinations.AddWorkout.route) {
            AddWorkoutScreen(navController = navController)
        }
        composable(Destinations.Progress.route) {
            ProgressScreen(navController = navController)
        }
        composable(Destinations.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(
            route = Destinations.AddProgress.route,
            arguments = listOf(navArgument("date") { type = NavType.LongType })
        ) {
            AddProgressScreen(navController = navController)
        }
    }
}