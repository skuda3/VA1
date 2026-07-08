package pef.mendelu.musclemaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import pef.mendelu.musclemaker.datastore.SettingsDataStore
import pef.mendelu.musclemaker.navigation.NavGraph
import pef.mendelu.musclemaker.navigation.Destinations
import pef.mendelu.musclemaker.ui.theme.MuscleMakerTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        enableEdgeToEdge()
        setContent {

                val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
                    Destinations.Main.route
                } else {
                    Destinations.Login.route
                }

                MuscleMakerTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        NavGraph(startDestination = startDestination)

                }
            }
        }
    }
}