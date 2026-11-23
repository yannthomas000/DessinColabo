package com.uqac.android.dessincolabo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uqac.android.dessincolabo.ui.ecrans.EcranConnexion
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ApplicationTableau()
                }
            }
        }
    }
}

@Composable
fun ApplicationTableau() {
    val statutSession by SupabaseManager.client.auth.sessionStatus.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    when (statutSession) {
        is SessionStatus.Initializing -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is SessionStatus.Authenticated -> {
            EcranTableauPlaceholder(
                onDeconnexion = {
                    coroutineScope.launch {
                        SupabaseManager.client.auth.signOut()
                    }
                }
            )
        }
        else -> {
            EcranConnexion(
                onConnexionReussie = {
                }
            )
        }
    }
}

@Composable
fun EcranTableauPlaceholder(onDeconnexion: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Connecté au tableau collaboratif !", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onDeconnexion) {
            Text("Se déconnecter")
        }
    }
}