package com.uqac.android.dessincolabo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.uqac.android.dessincolabo.GestionnaireRealtime
import com.uqac.android.dessincolabo.data.PaquetTraitDirect
import com.uqac.android.dessincolabo.network.SupabaseManager
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.*
import java.util.UUID
import kotlin.math.hypot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranTableau(
    tableauId: String,
    nomTableau: String,
    onRetour: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val userId = remember { SupabaseManager.client.auth.currentUserOrNull()?.id ?: UUID.randomUUID().toString() }

    // Mes traits terminés + mon trait en cours de tracé
    val mesTraits = remember { mutableStateListOf<List<Offset>>() }
    val traitEnCours = remember { mutableStateListOf<Offset>() }

    // Traits des autres utilisateurs reçus via Broadcast (clé = traitId)
    val traitsAutres = remember { mutableStateMapOf<String, MutableList<Offset>>() }

    // Tampon d'envoi groupé toutes les 60 ms
    val pointsEnAttente = remember { mutableListOf<List<Float>>() }
    var traitActuelId by remember { mutableStateOf("") }

    // Gestionnaire Realtime pour ce tableau
    val gestionnaire = remember(tableauId) {
        GestionnaireRealtime(
            tableauId = tableauId,
            nomTableau = nomTableau,
            monUtilisateurId = userId,
            scope = scope,
            onPaquetRecu = { paquet ->
                val listePoints = traitsAutres.getOrPut(paquet.traitId) { mutableStateListOf() }
                paquet.points.forEach { coord ->
                    if (coord.size == 2) {
                        listePoints.add(Offset(coord[0], coord[1]))
                    }
                }
            }
        )
    }

    // Connexion au canal et boucle d'envoi régulière
    DisposableEffect(tableauId) {
        val jobConnexion = scope.launch {
            try {
                gestionnaire.connecter()
            } catch (e: Exception) {
                android.util.Log.e("RealtimeTableau", "Échec connexion au tableau $nomTableau", e)
            }
        }

        val jobEnvoi = scope.launch {
            while (isActive) {
                delay(60L)
                if (pointsEnAttente.isNotEmpty() && traitActuelId.isNotEmpty()) {
                    val lot = pointsEnAttente.toList()
                    pointsEnAttente.clear()
                    gestionnaire.envoyerPaquet(
                        PaquetTraitDirect(
                            traitId = traitActuelId,
                            auteurId = userId,
                            couleurHex = "#000000",
                            epaisseur = 8f,
                            points = lot
                        )
                    )
                }
            }
        }

        onDispose {
            jobConnexion.cancel()
            jobEnvoi.cancel()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    gestionnaire.deconnecter()
                } catch (e: Exception) {
                    android.util.Log.e("RealtimeTableau", "Erreur déconnexion", e)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nomTableau, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour à l'accueil"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        mesTraits.clear()
                        traitEnCours.clear()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Effacer mes traits"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Lecture explicite des tailles pour forcer le rafraîchissement du Canvas
            val nombreTraitsLocaux = mesTraits.size
            val nombrePointsEnCours = traitEnCours.size
            val versionTraitsAutres = traitsAutres.values.sumOf { it.size }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                traitActuelId = UUID.randomUUID().toString()
                                traitEnCours.clear()
                                traitEnCours.add(offset)
                                pointsEnAttente.add(listOf(offset.x, offset.y))
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                val pos = change.position
                                val dernier = traitEnCours.lastOrNull()
                                // Ignore les micro-mouvements de moins de 3 pixels
                                if (dernier == null || hypot(pos.x - dernier.x, pos.y - dernier.y) >= 3f) {
                                    traitEnCours.add(pos)
                                    pointsEnAttente.add(listOf(pos.x, pos.y))
                                }
                            },
                            onDragEnd = {
                                if (traitEnCours.isNotEmpty()) {
                                    mesTraits.add(traitEnCours.toList())
                                    traitEnCours.clear()
                                }

                                val restants = pointsEnAttente.toList()
                                pointsEnAttente.clear()
                                val idFinal = traitActuelId
                                traitActuelId = ""

                                if (restants.isNotEmpty() && idFinal.isNotEmpty()) {
                                    scope.launch {
                                        try {
                                            gestionnaire.envoyerPaquet(
                                                PaquetTraitDirect(
                                                    traitId = idFinal,
                                                    auteurId = userId,
                                                    couleurHex = "#000000",
                                                    epaisseur = 8f,
                                                    points = restants,
                                                    estTermine = true
                                                )
                                            )
                                        } catch (_: Exception) { }
                                    }
                                }
                            },
                            onDragCancel = {
                                if (traitEnCours.isNotEmpty()) {
                                    mesTraits.add(traitEnCours.toList())
                                    traitEnCours.clear()
                                }
                                pointsEnAttente.clear()
                                traitActuelId = ""
                            }
                        )
                    }
            ) {
                // Référence aux compteurs d'état Compose pour garantir le redessin
                @Suppress("UNUSED_EXPRESSION")
                nombreTraitsLocaux + nombrePointsEnCours + versionTraitsAutres

                // 1. Dessiner les traits des collaborateurs
                traitsAutres.values.forEach { points ->
                    dessinerTraitLisse(points, Color.Black, 8f)
                }

                // 2. Dessiner mes traits terminés
                mesTraits.forEach { points ->
                    dessinerTraitLisse(points, Color.Black, 8f)
                }

                // 3. Dessiner le trait que je suis en train de tracer
                dessinerTraitLisse(traitEnCours, Color.Black, 8f)
            }
        }
    }
}

private fun DrawScope.dessinerTraitLisse(
    points: List<Offset>,
    couleur: Color,
    epaisseur: Float
) {
    if (points.isEmpty()) return

    if (points.size == 1) {
        drawCircle(
            color = couleur,
            radius = epaisseur / 2f,
            center = points[0]
        )
        return
    }

    val path = Path().apply {
        moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val curr = points[i]
            val midX = (prev.x + curr.x) / 2f
            val midY = (prev.y + curr.y) / 2f
            quadraticTo(prev.x, prev.y, midX, midY)
        }
        lineTo(points.last().x, points.last().y)
    }

    drawPath(
        path = path,
        color = couleur,
        style = Stroke(
            width = epaisseur,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}