package com.uqac.android.dessincolabo

import kotlinx.serialization.Serializable

@Serializable
data class PaquetTraitDirect(
    val traitId: String,          // Identifiant unique du trait en cours (UUID)
    val auteurId: String,         // ID de l'utilisateur qui dessine
    val couleurHex: String,       // Ex: "#4F46E5"
    val epaisseur: Float,         // Épaisseur du trait
    val points: List<List<Float>>, // Liste de paires [x, y] (plus compact que des objets)
    val estTermine: Boolean = false
)