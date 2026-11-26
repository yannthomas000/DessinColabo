package com.uqac.android.dessincolabo.data

import androidx.compose.ui.graphics.Color

data class TableauApercu(
    val id: String,
    val nom: String,
    val derniereModif: String,
    val couleursUtilisateurs: List<Color>,
    val couleurTraitApercu: Color
)