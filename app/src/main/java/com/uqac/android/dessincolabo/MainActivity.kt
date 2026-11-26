package com.uqac.android.dessincolabo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.uqac.android.dessincolabo.ui.EcranTableau
import com.uqac.android.dessincolabo.ui.ecrans.EcranAccueil
import com.uqac.android.dessincolabo.ui.ecrans.EcranConnexion
import com.uqac.android.dessincolabo.ui.ecrans.TableauApercu
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
    var tableauSelectionne by remember { mutableStateOf<TableauApercu?>(null) }

    when (statutSession) {
        is SessionStatus.Initializing -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is SessionStatus.Authenticated -> {
            val tableau = tableauSelectionne
            if (tableau != null) {
                EcranTableau(
                    tableauId = tableau.id,
                    nomTableau = tableau.nom,
                    onRetour = { tableauSelectionne = null }
                )
            } else {
                EcranAccueil(
                    onDeconnexion = {
                        coroutineScope.launch {
                            SupabaseManager.client.auth.signOut()
                        }
                    },
                    onOuvrirTableau = { tableauSelectionne = it }
                )
            }
        }
        else -> {
            EcranConnexion(onConnexionReussie = { })
        }
    }
}