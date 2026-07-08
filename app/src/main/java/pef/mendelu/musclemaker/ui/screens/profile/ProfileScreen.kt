package pef.mendelu.musclemaker.ui.screens.profile

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import pef.mendelu.musclemaker.R
import pef.mendelu.musclemaker.navigation.Destinations
import pef.mendelu.musclemaker.ui.screens.main.MainBottomNavBar
import pef.mendelu.musclemaker.ui.screens.main.MainTopAppBar
import pef.mendelu.musclemaker.ui.theme.AppDarkBackground
import pef.mendelu.musclemaker.ui.theme.AppRed

@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ProfileEffect.NavigateToLogin -> {
                    navController.navigate(Destinations.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
                is ProfileEffect.RecreateActivity -> {
                    val appLocale = LocaleListCompat.forLanguageTags(effect.languageCode)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                    (context as? Activity)?.recreate()
                }
                is ProfileEffect.Navigate -> {
                    navController.navigate(effect.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                is ProfileEffect.ShowError -> {
                    val message = context.getString(effect.messageResId)
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = { MainTopAppBar() },
        bottomBar = {
            MainBottomNavBar(
                navController = navController,
                onNavClick = { route ->
                    viewModel.onAction(ProfileAction.OnBottomNavClick(route))
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = AppDarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            AsyncImage(
                model = uiState.profilePictureUrl,
                placeholder = painterResource(id = R.drawable.ic_profile_placeholder),
                error = painterResource(id = R.drawable.ic_profile_placeholder),
                contentDescription = stringResource(R.string.profile_screen_profile_photo),
                modifier = Modifier.size(120.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "E-mail",
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = uiState.email ?: stringResource(R.string.profile_screen_not_available),
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            HorizontalDivider(color = Color.Gray)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.profile_screen_current_version, uiState.appVersion),
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            LanguageSwitcher(
                selectedLanguage = uiState.selectedLanguage,
                onLanguageChange = { newLanguageCode ->
                    viewModel.onAction(ProfileAction.OnLanguageChange(newLanguageCode))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { viewModel.onAction(ProfileAction.OnLogoutClick) },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(stringResource(id = R.string.profile_logout), color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { viewModel.onAction(ProfileAction.OnDeleteAccountClick) },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, AppRed)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = AppRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.profile_screen_delete_account), color = AppRed)
            }
        }
        if (uiState.isDeleteDialogVisible) {
            AlertDialog(
                onDismissRequest = { viewModel.onAction(ProfileAction.OnDeleteAccountDismiss) },
                title = { Text(stringResource(R.string.profile_screen_delete_popup1)) },
                text = { Text(stringResource(R.string.profile_screen_delete_popup2)) },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onAction(ProfileAction.OnDeleteAccountConfirm) },
                        colors = ButtonDefaults.buttonColors(containerColor = AppRed)
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onAction(ProfileAction.OnDeleteAccountDismiss) }) {
                        Text(stringResource(R.string.workout_detail_cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun LanguageSwitcher(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val cornerSize = 16.dp

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color.Gray),
        shape = RoundedCornerShape(cornerSize),
        colors = CardDefaults.outlinedCardColors(
            containerColor = AppDarkBackground
        )
    ) {
        Row(Modifier.fillMaxWidth()) {
            // anglictina
            TextButton(
                onClick = { onLanguageChange("en") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(topStart = cornerSize, bottomStart = cornerSize),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (selectedLanguage == "en") AppRed else Color.Transparent,
                    contentColor = if (selectedLanguage == "en") Color.White else Color.Gray
                ),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                if (selectedLanguage == "en") {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text("English")
            }

            HorizontalDivider(modifier = Modifier.width(1.dp).height(48.dp), color = Color.Gray)

            // cestina
            TextButton(
                onClick = { onLanguageChange("cs") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(topEnd = cornerSize, bottomEnd = cornerSize),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (selectedLanguage == "cs") AppRed else Color.Transparent,
                    contentColor = if (selectedLanguage == "cs") Color.White else Color.Gray
                ),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                if (selectedLanguage == "cs") {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text("Čeština")
            }
        }
    }
}