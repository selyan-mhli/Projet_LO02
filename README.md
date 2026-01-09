# Projet Jest - LO02 UTT

## Description

Implementation du jeu de cartes **Jest** en Java avec :

- Architecture **MVC** (Modele-Vue-Controleur)
- **Deux vues concurrentes** : Console et Interface Graphique
- **Patron Strategy unifie** pour tous les joueurs (humains et IA)
- **Patron Visitor** pour le calcul des scores
- **Patron Observer** pour la synchronisation des vues

## Fonctionnalites

### Regles officielles
- Gestion correcte des trophees selon les cartes (bande orange)
- Calcul du score : Piques/Trefles positifs, Carreaux negatifs, Coeurs speciaux
- Regles du Joker : +4 sans Coeur, negatif avec 1-3 Coeurs, positif avec 4 Coeurs
- Bonus As isole (5 pts) et paires noires (+2 pts)

### Extensions
1. **Cartes BM** : Cartes Bonus/Malus ajoutant des effets speciaux
2. **Cartes 5-8** : Extension du deck de 17 a 33 cartes

### Interface
- **Console** : Interface textuelle complete
- **GUI Swing** : Interface graphique basique
- Les deux vues peuvent fonctionner simultanement

## Architecture

### Patron Strategy Unifie

Tous les joueurs (humains et IA) utilisent le patron Strategy pour une architecture coherente :

```
Strategy (interface)
├── HumanStrategy      # Delegue les decisions a la vue
└── AIStrategy (abstract)
    ├── Strategy1      # IA conservatrice (privilegie cartes visibles)
    └── Strategy2      # IA bluff (privilegie cartes cachees)
```

**Avantages :**
- Architecture homogene et extensible
- Elimination des casts et verifications de type
- Facilite l'ajout de nouvelles strategies
- Respect du principe Open/Closed

### Structure du projet

```
Jest_Project/
├── src/
│   ├── model/           # Modele (cartes, joueurs, partie)
│   │   ├── rules/       # Regles et variantes
│   │   ├── trophy/      # Fabrique de trophees
│   │   └── extension/   # Extensions (BM, cartes 5-8)
│   ├── controller/      # Controleur (GameEngine)
│   ├── view/            # Vues
│   │   └── gui/         # Interface graphique
│   ├── strategy/        # Strategies (humain + IA)
│   └── observer/        # Patron Observer
├── classes/             # Fichiers compiles
├── doc/                 # Documentation
├── compile.sh           # Script compilation Linux/Mac
├── compile.bat          # Script compilation Windows
├── run.sh               # Script execution Linux/Mac
└── run.bat              # Script execution Windows
```

## Compilation et execution

### Linux / macOS
```bash
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

### Windows
```batch
compile.bat
run.bat
```

## Regles des trophees

Les trophees sont determines par la bande orange sur les cartes :

| Trophee | Description |
|---------|-------------|
| Highest [Couleur] | Plus haute valeur totale dans une couleur |
| Lowest [Couleur] | Plus basse valeur totale dans une couleur |
| Majority | Plus grand nombre de cartes d une couleur |
| Joker | Possession du Joker |
| Best Jest | Meilleur score total |
| Best Jest No Joke | Meilleur score sans le Joker |

En cas d egalite, departage par la carte de la couleur la plus forte :
Piques > Trefles > Carreaux > Coeurs

## Refactorisation - Patron Strategy Unifie

### Date
6 janvier 2026

### Objectif
Unifier l'architecture en traitant tous les joueurs (humains et IA) avec le patron Strategy, eliminant ainsi la distinction entre `HumanPlayer` et `AIPlayer`.

### Modifications effectuees

#### 1. Nouvelles classes creees

**`strategy/HumanStrategy.java`**
- Implemente `Strategy` pour les joueurs humains
- Retourne `null` pour signaler que les decisions doivent etre prises via la vue
- Methode `isHuman()` retourne `true`

**`strategy/AIStrategy.java`**
- Classe abstraite de base pour toutes les strategies IA
- Fournit la methode `estimateValue(Card)` commune a toutes les IA
- Methode `isHuman()` retourne `false`

#### 2. Classes modifiees

**`strategy/Strategy.java`**
- Ajout de la methode `isHuman()` a l'interface
- Mise a jour de la documentation pour refleter qu'elle s'applique a tous les joueurs

**`strategy/Strategy1.java` et `strategy/Strategy2.java`**
- Changement de `implements Strategy` a `extends AIStrategy`
- Suppression de la methode `estimateValue()` (heritee de `AIStrategy`)

**`model/Player.java`**
- Transformation de classe abstraite en classe concrete
- Ajout du parametre `strategy` au constructeur
- Remplacement des methodes abstraites par des implementations concretes qui deleguent a la strategie
- Ajout de la methode `getStrategy()`

**`controller/GameEngine.java`**
- Simplification de `createOffer()` : appel de `player.makeOffer()`, si `null` on demande a la vue
- Simplification de `playerChooseCard()` : appel de `player.chooseFrom()`, si `null` on demande a la vue
- Suppression de tous les casts `(HumanPlayer)` et `(AIPlayer)`
- Suppression des verifications `instanceof`

**`view/ConsoleView.java` et `view/gui/JestGUI.java`**
- Changement des signatures pour accepter `Player` au lieu de `HumanPlayer`
- Mise a jour de `askPlayerType()` pour creer `new Player(name, new HumanStrategy())` ou `new Player(name, new Strategy1/2())`

#### 3. Classes supprimees

- `model/HumanPlayer.java` - Remplacee par `Player` avec `HumanStrategy`
- `model/AIPlayer.java` - Remplacee par `Player` avec `Strategy1` ou `Strategy2`

### Avantages de la refactorisation

1. **Architecture coherente** : Tous les joueurs utilisent le meme patron
2. **Code plus propre** : Elimination des casts et verifications de type
3. **Extensibilite** : Facilite l'ajout de nouvelles strategies (humaines ou IA)
4. **Maintenabilite** : Moins de duplication de code
5. **Respect des principes SOLID** :
   - Open/Closed : Ouvert a l'extension, ferme a la modification
   - Liskov Substitution : Toutes les strategies sont interchangeables
   - Dependency Inversion : Dependance sur l'abstraction `Strategy`

### Diagramme de classes

```
┌─────────────────┐
│    Strategy     │ (interface)
│  <<interface>>  │
├─────────────────┤
│ +chooseOffer()  │
│ +chooseTake()   │
│ +isHuman()      │
└────────┬────────┘
         │
    ┌────┴────────────────────┐
    │                         │
┌───▼──────────┐      ┌──────▼────────┐
│HumanStrategy │      │  AIStrategy   │ (abstract)
├──────────────┤      ├───────────────┤
│+isHuman():   │      │+isHuman():    │
│  true        │      │  false        │
│+chooseOffer()│      │#estimateValue()│
│  → null      │      └───────┬───────┘
│+chooseTake() │              │
│  → null      │      ┌───────┴────────┐
└──────────────┘      │                │
                  ┌───▼────┐      ┌───▼────┐
                  │Strategy1│      │Strategy2│
                  └─────────┘      └─────────┘

┌─────────────────┐
│     Player      │
├─────────────────┤
│-name: String    │
│-strategy:       │
│  Strategy       │
│-jest: Jest      │
│-hand: List<Card>│
├─────────────────┤
│+makeOffer()     │──┐
│+chooseFrom()    │  │ Delegue a
│+isHuman()       │  │ la strategie
└─────────────────┘  │
         ▲           │
         └───────────┘
```

### Tests effectues

- ✅ Compilation reussie sans erreurs
- ✅ Generation Javadoc complete
- ✅ Architecture coherente et extensible

## Auteurs

Projet LO02 - UTT - 2025
