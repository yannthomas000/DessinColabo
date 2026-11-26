package com.uqac.android.dessincolabo.network

import com.uqac.android.dessincolabo.data.PaquetTraitDirect
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.broadcast
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GestionnaireRealtime(
    private val tableauId: String,
    private val monUtilisateurId: String,
    private val scope: CoroutineScope,
    private val onPaquetRecu: (PaquetTraitDirect) -> Unit
) {
    private var canal: RealtimeChannel? = null

    suspend fun connecter() {
        val nouveauCanal = SupabaseManager.client.channel("tableau_$tableauId") {
            broadcast {
                acknowledgeBroadcasts = false
                receiveOwnBroadcasts = false
            }
        }

        nouveauCanal.broadcastFlow<PaquetTraitDirect>(event = "trait_direct")
            .onEach { paquet ->
                if (paquet.auteurId != monUtilisateurId) {
                    onPaquetRecu(paquet)
                }
            }
            .launchIn(scope)

        // Appel sans paramètre dans Supabase 3.x
        nouveauCanal.subscribe()
        canal = nouveauCanal
    }

    suspend fun envoyerPaquet(paquet: PaquetTraitDirect) {
        canal?.broadcast(
            event = "trait_direct",
            message = paquet
        )
    }

    suspend fun deconnecter() {
        canal?.unsubscribe()
        canal = null
    }
}