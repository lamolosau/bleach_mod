<div align="center">

  <img width="1024" height="304" alt="bleqch_title" src="https://github.com/user-attachments/assets/5c7f5884-2f4a-4b27-990e-be7898131d0d" />

  # Bleach Mod (Fabric 1.20.1)
  
  Un mod Minecraft immersif qui transpose l'univers de Bleach dans le jeu, en introduisant le concept d'énergie spirituelle, la séparation des âmes et la traque des Hollows.
</div>

---

## 🚧 Projets à venir (Roadmap)

### 🧬 Système d'Origines et d'Évolution
* **Nouvelles Factions :** Intégration des origines Quincy, Hollow et Fullbringer avec leurs propres arbres de compétences.
* **Hybridation :** Possibilité de devenir un Vizard (moitié Shinigami, moitié Hollow) sous certaines conditions en jeu.
* **Maîtrise du Reiatsu :** Ajout de compétences actives (Kido, capacités psychiques) consommant la jauge d'énergie spirituelle.

### ⚔️ Arsenal et Zanpakutō
* **Forge et Évolution :** Système de création d'Asauchi et d'évolution personnalisée de l'arme.
* **Éveil :** Mécaniques d'entraînement permettant de débloquer le Shikai, puis le Bankai, modifiant le modèle 3D et les capacités de l'arme.

### 🧟‍♂️ Entités et Fausses Âmes
* **Bestiaire Hollow :** Développement de 4 types de Hollows distincts (en cours de modélisation) avec leurs propres comportements.
* **Gikongan (Fausses Âmes) :** Mécanique permettant d'insérer une âme artificielle dans le corps physique laissé inerte lors de la transformation en Shinigami, lui octroyant une autonomie temporaire.

### 🗺️ Monde et Narration
* **PNJs Interactifs :** Ajout de personnages iconiques (Urahara, Rukia...) apparaissant dans des structures spécifiques ou de manière aléatoire pour proposer des quêtes, du commerce ou des entraînements (ex: entraînement au Bankai chez Urahara).
* **Boss Épiques :** Combats de boss phasés contre des antagonistes majeurs comme Aizen et Yhwach.
* **Dimensions (Projet lointain) :** Création du Hueco Mundo (incluant Las Noches) et de la Soul Society (intégration du Seireitei).

---

## ✅ Fonctionnalités implémentées

### 1. Le Système d'Énergie Spirituelle (Reiatsu & Invisibilité)
* **Mécanique de rendu conditionnel :** Gère l'état spirituel du joueur via l'attribution d'un pouvoir spécifique (`bleach_mod:reiatsu_resource`).
* **Séparation des mondes :** Quand le Reiatsu est actif (forme spirituelle), le rendu 3D du personnage est totalement annulé pour les humains normaux et les créatures classiques. Seuls les esprits (comme les Hollows) peuvent le voir et interagir avec lui.

### 2. Le Badge de Shinigami Remplaçant (Daikōshō)
* **Modélisation et Esthétique :** Intègre un modèle 3D sur mesure avec des textures personnalisées, ajusté pour un rendu immersif en main et dans l'inventaire.
* **Mécanique de Radar :** Tant que l'objet est dans l'inventaire d'un humain, il scanne les environs dans un rayon de 30 blocs. S'il détecte un Hollow, il émet un bip sonore dont le rythme et l'acuité s'intensifient selon la proximité. Il génère également des particules de poussière rougeoyante au-dessus de la tête du monstre pour le localiser dans l'espace.
* **Séparation de l'Âme :** Au clic droit, le badge vérifie le statut du joueur. S'il s'agit d'un humain, le badge lui arrache l'âme (choc sensoriel avec Cécité et Lenteur temporaires), lui octroie le pouvoir de Reiatsu et l'identifie comme Shinigami remplaçant. Un second clic inverse le processus pour réintégrer le corps physique.

### 3. L'Origine Shinigami
* **Intégration Native :** Définit un joueur comme un véritable esprit dès la création de sa partie en s'appuyant sur le système du mod Origins.
* **Adaptation du Badge :** Le Daikōshō réagit intelligemment à cette Origine. L'action de séparation de l'âme échoue logiquement (un vrai Shinigami n'ayant pas de corps physique à quitter) en affichant un message de résonance spirituelle. L'objet conserve uniquement sa fonction de radar.

### 4. L'Écosystème des Entités et le Ciblage Intelligent
* **Catégorisation par Tags :** Mise en place d'une catégorie universelle `bleach_mod:hollows` dans les données du jeu, incluant actuellement le `FishboneEntity`.
* **Évolutivité :** Le radar et les mécaniques du mod ciblent cette étiquette globale plutôt que des monstres spécifiques, permettant d'ajouter facilement de nouvelles créatures sans avoir à recompiler le code Java.
