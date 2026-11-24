package com.uqac.android.dessincolabo.ui.ecrans

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modèle de données temporaire pour les placeholders
data class TableauApercu(
    val id: String,
    val nom: String,
    val derniereModif: String,
    val couleursUtilisateurs: List<Color>,
    val couleurTraitApercu: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranAccueil(
    onDeconnexion: () -> Unit
) {
    // Données factices pour visualiser le rendu
    val tableauxPlaceholders = remember {
        listOf(
            TableauApercu(
                id = "1",
                nom = "Architecture App Mobile",
                derniereModif = "Modifié il y a 5 min",
                couleursUtilisateurs = listOf(Color(0xFF4F46E5), Color(0xFF059669), Color(0xFFD97706), Color(0xFFDC2626), Color(0xFF7C3AED)),
                couleurTraitApercu = Color(0xFF4F46E5)
            ),
            TableauApercu(
                id = "2",
                nom = "Brainstorming UI/UX",
                derniereModif = "Modifié hier",
                couleursUtilisateurs = listOf(Color(0xFF0284C7), Color(0xFFDB2777)),
                couleurTraitApercu = Color(0xFFDB2777)
            ),
            TableauApercu(
                id = "3",
                nom = "Schéma Base de Données",
                derniereModif = "Modifié il y a 3 jours",
                couleursUtilisateurs = listOf(Color(0xFF16A34A)),
                couleurTraitApercu = Color(0xFF16A34A)
            ),
            TableauApercu(
                id = "4",
                nom = "Notes Réunion Équipe",
                derniereModif = "Modifié le 18 nov.",
                couleursUtilisateurs = listOf(Color(0xFFEA580C), Color(0xFF2563EB), Color(0xFF0D9488)),
                couleurTraitApercu = Color(0xFFEA580C)
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mes Tableaux",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onDeconnexion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Se déconnecter"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Action de création à brancher plus tard */ },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nouveau tableau") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 12.dp,
                bottom = paddingValues.calculateBottomPadding() + 88.dp, // Espace pour ne pas masquer la dernière carte sous le FAB
                start = 16.dp,
                end = 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(tableauxPlaceholders, key = { it.id }) { tableau ->
                CarteTableau(tableau = tableau)
            }
        }
    }
}

@Composable
fun CarteTableau(
    tableau: TableauApercu,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { /* Ouverture du tableau en grand à venir */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // 1. Zone miniature du tableau (Canvas placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color(0xFFF8FAFC))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Quadrillage léger façon tableau blanc
                    val step = 20.dp.toPx()
                    for (x in 0..size.width.toInt() step step.toInt()) {
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(x.toFloat(), 0f),
                            end = Offset(x.toFloat(), size.height),
                            strokeWidth = 1f
                        )
                    }
                    for (y in 0..size.height.toInt() step step.toInt()) {
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(0f, y.toFloat()),
                            end = Offset(size.width, y.toFloat()),
                            strokeWidth = 1f
                        )
                    }

                    // Tracé d'aperçu simulé
                    val path = Path().apply {
                        moveTo(size.width * 0.18f, size.height * 0.65f)
                        quadraticTo(
                            size.width * 0.42f, size.height * 0.15f,
                            size.width * 0.60f, size.height * 0.55f
                        )
                        quadraticTo(
                            size.width * 0.75f, size.height * 0.85f,
                            size.width * 0.85f, size.height * 0.30f
                        )
                    }
                    drawPath(
                        path = path,
                        color = tableau.couleurTraitApercu,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // 2. Informations du tableau + Icônes des collaborateurs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = tableau.nom,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = tableau.derniereModif,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Rangée d'icônes des utilisateurs ayant accès au tableau
                GroupeAvatarsUtilisateurs(couleurs = tableau.couleursUtilisateurs)
            }
        }
    }
}

@Composable
fun GroupeAvatarsUtilisateurs(
    couleurs: List<Color>,
    maxVisibles: Int = 3
) {
    val visibles = couleurs.take(maxVisibles)
    val surplus = couleurs.size - maxVisibles

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((-8).dp) // Chevauchement propre des icônes
    ) {
        visibles.forEach { couleur ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(couleur)
                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Collaborateur",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        if (surplus > 0) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Text(
                    text = "+$surplus",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}