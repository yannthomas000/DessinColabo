package com.uqac.android.dessincolabo

import android.util.Log
import com.uqac.android.dessincolabo.data.PaquetTraitDirect
import com.uqac.android.dessincolabo.network.SupabaseManager
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.broadcast
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

class GestionnaireRealtime(
    private val tableauId: String,
    private val nomTableau: String,
    private val monUtilisateurId: String,
    private val scope: CoroutineScope,
    private val onPaquetRecu: (PaquetTraitDirect) -> Unit
) {
    private var canal: RealtimeChannel? = null

    suspend fun connecter() {
        val nomCanal = "tableau_$tableauId"
        Log.d("RealtimeTableau", "1. Initialisation du canal $nomCanal...")

        // Démarrer explicitement la connexion WebSocket globale si elle n'est pas ouverte
        SupabaseManager.client.realtime.connect()
        Log.d("RealtimeTableau", "2. WebSocket global connecté")

        val nouveauCanal = SupabaseManager.client.channel(nomCanal) {
            broadcast {
                acknowledgeBroadcasts = false
                receiveOwnBroadcasts = false
            }
        }

        // Écouter les paquets entrants avant de souscrire
        nouveauCanal.broadcastFlow<PaquetTraitDirect>(event = "trait_direct")
            .onEach { paquet ->
                Log.d(
                    "RealtimeTableau",
                    "REÇU sur '$nomTableau' -> trait=${paquet.traitId}, auteur=${paquet.auteurId}, points=${paquet.points.size}"
                )
                if (paquet.auteurId != monUtilisateurId) {
                    withContext(Dispatchers.Main) {
                        onPaquetRecu(paquet)
                    }
                }
            }
            .launchIn(scope)

        Log.d("RealtimeTableau", "3. Souscription au canal $nomCanal...")
        nouveauCanal.subscribe()
        canal = nouveauCanal
        Log.i("RealtimeTableau", "4. CONNECTÉ au tableau '$nomTableau' (canal=$nomCanal)")
    }

    suspend fun envoyerPaquet(paquet: PaquetTraitDirect) {
        val c = canal
        if (c == null) {
            Log.w("RealtimeTableau", "ENVOI ANNULÉ sur '$nomTableau' : canal pas encore initialisé")
            return
        }
        try {
            c.broadcast(
                event = "trait_direct",
                message = paquet
            )
            Log.d(
                "RealtimeTableau",
                "ENVOYÉ sur '$nomTableau' (id=$tableauId) -> ${paquet.points.size} points"
            )
        } catch (e: Exception) {
            Log.e("RealtimeTableau", "ERREUR D'ENVOI sur '$nomTableau'", e)
        }
    }

    suspend fun deconnecter() {
        Log.d("RealtimeTableau", "Déconnexion du tableau '$nomTableau'")
        canal?.unsubscribe()
        canal = null
    }
}