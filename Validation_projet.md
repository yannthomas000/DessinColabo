# Dessin Collaboratif — Proposition de projet

## Prénoms et noms des membres de l'équipe

- Mathis LETELLIER
- Yann THOMAS

## Idée du projet

**Nom du projet :** DessinCollabo

**Description :** Dessin Collabo est une application mobile de canevas interactif permettant à plusieurs utilisateurs de collaborer en temps réel. Les utilisateurs pourront interagir sur un même espace de travail pour ajouter du texte, tracer des formes géométriques et dessiner à main levée. L'objectif principal de ce projet est d'affronter la complexité technique de la synchronisation instantanée d'états d'interface entre plusieurs appareils distants, une véritable inconnue technique.

## Technologies explorées

- **Android Studio & Kotlin** : Outils de base pour le développement natif de l'application.
  - **Documentation :** <https://developer.android.com/kotlin>
- **Jetpack Compose (Canvas & PointerInput)** : Pour la création de l'interface utilisateur, la capture des gestes tactiles et le dessin personnalisé.
  - **Documentation :** <https://developer.android.com/jetpack/compose/graphics/draw/modifiers>
- **Firebase Realtime Database** : Backend as a Service utilisé pour la première phase de synchronisation des tracés en temps réel entre les utilisateurs.
  - **Documentation :** <https://firebase.google.com/docs/database/android/start>
- **CRDTs (Conflict-free Replicated Data Types — via une librairie comme Yjs Kotlin ou Automerge)** : La vraie inconnue technique. Transition prévue dans une seconde phase pour gérer la synchronisation sans serveur centralisé de manière robuste et sans conflit.
  - **Documentation :** Lien vers la librairie choisie, par exemple <https://github.com/automerge/automerge>

## Lien vers le dépôt Git

- <https://github.com/yannthomas000/DessinColabo>

## Plan de travail

*Note : Les séances font référence aux rencontres du cours, jusqu'à la présentation à la séance 14.*

| Séance | Tâches à réaliser | Responsables |
|---|---|---|
| **Séance 7** 20 Octobre | Initialisation du projet Git, configuration initiale de l'interface avec Jetpack Compose et implémentation du dessin local sur un Canvas. | Yann |
| **Séance 8** 27 Octobre| Configuration de Firebase Realtime Database. Sérialisation des coordonnées des dessins en local vers Firebase. | Yann |
| **Séance 8** 27 Octobre| Ajout des fonctionnalités d'insertion de texte et de formes géométriques dans le Canvas. |Mathis |
| **Séance 9** 3 Novembre| Réception des données Firebase sur d'autres appareils et dessin automatique. Tests sur téléphones physiques. | Yann |
| **Séance 9** 3 Novembre| Création du menu et navigation. | Mathis |
| **Séance 10** 10 Novembre| Recherche, documentation et tests d'intégration d'une librairie CRDT en Kotlin (exploration de l'inconnue technique). | Mathis et Yann|
| **Séance 11** 17 Novembre| Si concluant : intégration des CRDTs pour remplacer ou compléter Firebase. Sinon : perfectionnement de la synchronisation Firebase (gestion manuelle des conflits). | Yann et Mathis |
| **Séance 12** 24 Novembre| Débogage intensif, tests croisés entre plusieurs appareils. Rédaction approfondie de la documentation (README, Wiki). | Mathis |
| **Séance 13** 1 Décembre| Ajustements finaux, nettoyage du code (nomenclature, commentaires). Version Bêta **Démonstration en direct (évaluation).** | Yann |
| **Séance 14** 8 Décembre| Création des diapositives. **Présentation orale exposant les choix, blocages et apprentissages.** | Mathis |

## Risques identifiés et solutions de repli

1. **Risque :** Si l'intégration ou la compréhension de la librairie CRDT en Kotlin s'avère trop complexe ou incompatible avec notre structure de données.
   - **Solution de repli :** Mettre de côté les CRDTs et conserver Firebase Realtime Database comme architecture finale, en concentrant les efforts techniques sur l'optimisation de la bande passante et une gestion simplifiée des conflits (le dernier trait reçu écrase le précédent).
2. **Risque :** Si l'affichage du Canvas (Jetpack Compose) devient trop lent à cause du nombre élevé de points synchronisés.
   - **Solution de repli :** Implémenter un système d'optimisation des tracés (algorithme de lissage pour réduire le nombre de points) ou limiter le nombre d'objets affichables simultanément.
