package pef.mendelu.musclemaker.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pef.mendelu.musclemaker.R
import pef.mendelu.musclemaker.model.Workout
import pef.mendelu.musclemaker.ui.theme.AppDarkBackground
import pef.mendelu.musclemaker.ui.theme.AppRed
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.flow.collectLatest
import pef.mendelu.musclemaker.navigation.Destinations

@Composable
fun MainScreen(navController: NavController) {
    val viewModel: MainViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is MainEffect.NavigateToAddWorkout -> {
                    navController.navigate(Destinations.AddWorkout.route)
                }
                is MainEffect.NavigateToWorkoutDetail -> {
                    navController.navigate(Destinations.WorkoutDetail.createRoute(effect.workoutId))
                }
                is MainEffect.Navigate -> {
                    navController.navigate(effect.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = { MainTopAppBar() },
        bottomBar = {
            MainBottomNavBar(
                navController = navController,
                onNavClick = { route -> viewModel.onAction(MainAction.OnBottomNavClick(route)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAction(MainAction.OnAddWorkoutClick) },
                containerColor = AppRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.workout_add))
            }
        },
        containerColor = AppDarkBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.workouts) { workout ->
                WorkoutListItem(
                    workout = workout,
                    onClick = { viewModel.onAction(MainAction.OnWorkoutClick(workout)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppDarkBackground,
            titleContentColor = AppRed
        )
    )
}

@Composable
fun MainBottomNavBar(navController: NavController, onNavClick: (String) -> Unit) {
    val navItems = listOf(
        Destinations.Main to Icons.Filled.FitnessCenter,
        Destinations.Progress to Icons.Filled.Timeline,
        Destinations.Profile to Icons.Filled.AccountCircle
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = AppDarkBackground,
    ) {
        val navItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = AppRed,
            selectedTextColor = AppRed,
            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Gray,
            indicatorColor = AppDarkBackground
        )

        navItems.forEach { (screen, icon) ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = { onNavClick(screen.route) },
                label = { Text(stringResource(id = getNavItemLabel(screen))) },
                icon = { Icon(icon, contentDescription = stringResource(id = getNavItemLabel(screen))) },
                colors = navItemColors
            )
        }
    }
}

@Composable
private fun getNavItemLabel(screen: Destinations): Int {
    return when (screen) {
        is Destinations.Main -> R.string.nav_bar_workouts
        is Destinations.Progress -> R.string.nav_bar_progress
        is Destinations.Profile -> R.string.nav_bar_profile
        else -> R.string.app_name
    }
}


@Composable
fun WorkoutListItem(workout: Workout, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = stringResource(R.string.workout_completed),
                tint = if (workout.isCompleted) AppRed else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = workout.name,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (workout.hasVideo) {
                    Icon(
                        imageVector = Icons.Filled.PlayCircleOutline,
                        contentDescription = stringResource(R.string.workout_video_available),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}
