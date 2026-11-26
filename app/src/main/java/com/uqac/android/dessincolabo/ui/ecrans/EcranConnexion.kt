package com.uqac.android.dessincolabo.ui.ecrans

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.uqac.android.dessincolabo.network.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

@Composable
fun EcranConnexion(
    onConnexionReussie: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }
    var estEnModeInscription by remember { mutableStateOf(false) }
    var chargement by remember { mutableStateOf(false) }
    var messageErreur by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (estEnModeInscription) "Créer un compte" else "Tableau Collaboratif",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Adresse courriel") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = motDePasse,
            onValueChange = { motDePasse = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        messageErreur?.let { erreur ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = erreur,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                chargement = true
                messageErreur = null
                coroutineScope.launch {
                    try {
                        if (estEnModeInscription) {
                            SupabaseManager.client.auth.signUpWith(Email) {
                                this.email = email.trim()
                                this.password = motDePasse
                            }
                        } else {
                            SupabaseManager.client.auth.signInWith(Email) {
                                this.email = email.trim()
                                this.password = motDePasse
                            }
                        }
                        onConnexionReussie()
                    } catch (e: Exception) {
                        messageErreur = "Erreur : ${e.localizedMessage ?: "Vérifie tes identifiants"}"
                    } finally {
                        chargement = false
                    }
                }
            },
            enabled = !chargement && email.isNotBlank() && motDePasse.length >= 6,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (chargement) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (estEnModeInscription) "S'inscrire" else "Se connecter")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                estEnModeInscription = !estEnModeInscription
                messageErreur = null
            }
        ) {
            Text(
                if (estEnModeInscription) "Déjà un compte ? Se connecter"
                else "Pas encore de compte ? S'inscrire"
            )
        }
    }
}