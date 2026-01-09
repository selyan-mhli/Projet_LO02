#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
================================================================================
                    GENERATEUR DE PROJET JEST - VERSION FINALE
================================================================================
Projet LO02 - UTT - Annee 2025

Ce script genere automatiquement la structure complete du projet Jest avec :
    1. Gestion correcte des trophees selon les regles officielles
    2. Documentation Javadoc complete et detaillee
    3. Extension Cartes supplementaires (1-8 au lieu de 1-5)
    4. Extension Cartes BM (Bonus/Malus)
    5. Architecture MVC avec double vue (Console + GUI)

Usage:
    python generate_jest_vf.py

@author Projet LO02 - UTT
@version 2.0 - Version Finale avec Extensions et GUI MVC
"""

import os
import stat

def create_project_structure():
    directories = [
        "Jest_Project/src/model",
        "Jest_Project/src/model/rules",
        "Jest_Project/src/model/trophy",
        "Jest_Project/src/model/extension",
        "Jest_Project/src/strategy",
        "Jest_Project/src/controller",
        "Jest_Project/src/view",
        "Jest_Project/src/view/gui",
        "Jest_Project/src/observer",
        "Jest_Project/classes",
        "Jest_Project/doc"
    ]
    
    for directory in directories:
        os.makedirs(directory, exist_ok=True)
    
    
    files = {}

    # ==========================================================================
    #                              SUITS ENUM
    # ==========================================================================
    
    files["Jest_Project/src/model/Suits.java"] = '''package model;

import java.io.Serializable;

/**
 * Enumeration representant les couleurs (enseignes) des cartes du jeu Jest.
 * 
 * <p>Le jeu Jest utilise quatre couleurs standard plus une couleur speciale
 * pour le Joker. Chaque couleur a un comportement specifique pour le calcul
 * du score :</p>
 * 
 * <ul>
 *   <li><b>SPADES (Piques)</b> : Augmente le Jest de la valeur faciale</li>
 *   <li><b>CLUBS (Trefles)</b> : Augmente le Jest de la valeur faciale</li>
 *   <li><b>DIAMONDS (Carreaux)</b> : Reduit le Jest de la valeur faciale</li>
 *   <li><b>HEARTS (Coeurs)</b> : Ne vaut rien sauf avec le Joker</li>
 *   <li><b>JOKER</b> : Couleur speciale pour la carte Joker</li>
 * </ul>
 * 
 * <h2>Regles de scoring par couleur :</h2>
 * <table border="1">
 *   <tr><th>Couleur</th><th>Score</th><th>Notes</th></tr>
 *   <tr><td>Piques</td><td>+valeur</td><td>Toujours positif</td></tr>
 *   <tr><td>Trefles</td><td>+valeur</td><td>Toujours positif</td></tr>
 *   <tr><td>Carreaux</td><td>-valeur</td><td>Toujours negatif</td></tr>
 *   <tr><td>Coeurs</td><td>0 ou special</td><td>Depend du Joker</td></tr>
 * </table>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Card
 * @see SuitCard
 */
public enum Suits implements Serializable {
    
    /** Piques - Couleur noire, score positif. */
    SPADES("Piques", "♠"),
    
    /** Trefles - Couleur noire, score positif. */
    CLUBS("Trefles", "♣"),
    
    /** Carreaux - Couleur rouge, score negatif. */
    DIAMONDS("Carreaux", "♦"),
    
    /** Coeurs - Couleur rouge, score special. */
    HEARTS("Coeurs", "♥"),
    
    /** Joker - Couleur speciale pour la carte Joker uniquement. */
    JOKER("Joker", "★");

    /** Nom francais de la couleur. */
    private final String name;
    
    /** Symbole Unicode de la couleur. */
    private final String symbol;

    /**
     * Constructeur de l enumeration Suits.
     * 
     * @param name   le nom francais de la couleur
     * @param symbol le symbole Unicode representant la couleur
     */
    Suits(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    /**
     * Retourne le nom francais de la couleur.
     * 
     * @return le nom de la couleur (ex: "Piques", "Coeurs")
     */
    public String getName() { 
        return name; 
    }
    
    /**
     * Retourne le symbole Unicode de la couleur.
     * 
     * @return le symbole (ex: "♠", "♥")
     */
    public String getSymbol() { 
        return symbol; 
    }

    /**
     * Retourne une representation textuelle de la couleur.
     * 
     * @return le symbole Unicode de la couleur
     */
    @Override
    public String toString() { 
        return symbol; 
    }
}'''

    # ==========================================================================
    #                              RANK ENUM
    # ==========================================================================
    
    files["Jest_Project/src/model/Rank.java"] = '''package model;

import java.io.Serializable;

/**
 * Enumeration representant les rangs (valeurs) des cartes du jeu Jest.
 * 
 * <p>Dans le jeu Jest standard, les rangs vont de As (1) a 4.
 * Avec l extension "Cartes supplementaires", les rangs vont jusqu a 8.</p>
 * 
 * <h2>Valeurs des rangs :</h2>
 * <table border="1">
 *   <tr><th>Rang</th><th>Valeur</th><th>Disponibilite</th></tr>
 *   <tr><td>As</td><td>1</td><td>Standard</td></tr>
 *   <tr><td>2</td><td>2</td><td>Standard</td></tr>
 *   <tr><td>3</td><td>3</td><td>Standard</td></tr>
 *   <tr><td>4</td><td>4</td><td>Standard</td></tr>
 *   <tr><td>5-8</td><td>5-8</td><td>Extension</td></tr>
 * </table>
 * 
 * <h2>Regle speciale de l As :</h2>
 * <p>Si l As est la seule carte de sa couleur dans le Jest, il vaut 5 points.</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Card
 */
public enum Rank implements Serializable {
    
    /** As - Valeur 1 (ou 5 si isole dans sa couleur). */
    ACE(1, "As"),
    
    /** Deux - Valeur 2. */
    TWO(2, "2"),
    
    /** Trois - Valeur 3. */
    THREE(3, "3"),
    
    /** Quatre - Valeur 4. */
    FOUR(4, "4"),
    
    /** Cinq - Valeur 5 (Extension uniquement). */
    FIVE(5, "5"),
    
    /** Six - Valeur 6 (Extension uniquement). */
    SIX(6, "6"),
    
    /** Sept - Valeur 7 (Extension uniquement). */
    SEVEN(7, "7"),
    
    /** Huit - Valeur 8 (Extension uniquement). */
    EIGHT(8, "8"),
    
    /** Joker - Valeur speciale 0. */
    JOKER(0, "Joker");

    /** Valeur numerique du rang pour le calcul des scores. */
    private final int value;
    
    /** Nom d affichage du rang. */
    private final String displayName;

    /**
     * Constructeur de l enumeration Rank.
     * 
     * @param value       la valeur numerique du rang
     * @param displayName le nom d affichage du rang
     */
    Rank(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /**
     * Retourne la valeur numerique du rang.
     * 
     * @return la valeur du rang (1-8 pour les cartes normales, 0 pour Joker)
     */
    public int getValue() { 
        return value; 
    }
    
    /**
     * Retourne le nom d affichage du rang.
     * 
     * @return le nom (ex: "As", "2", "Joker")
     */
    public String getDisplayName() { 
        return displayName; 
    }

    /**
     * Retourne une representation textuelle du rang.
     * 
     * @return le nom d affichage du rang
     */
    @Override
    public String toString() { 
        return displayName; 
    }
}'''

    # ==========================================================================
    #                              CARD CLASSES
    # ==========================================================================
    
    files["Jest_Project/src/model/Card.java"] = '''package model;

import java.io.Serializable;

/**
 * Classe abstraite representant une carte du jeu Jest.
 * 
 * <p>Cette classe est la classe mere de toutes les cartes du jeu.
 * Elle definit les proprietes communes (couleur, rang) et le comportement
 * polymorphique via le patron Visitor pour le calcul des scores.</p>
 * 
 * <h2>Hierarchie des cartes :</h2>
 * <pre>
 * Card (abstraite)
 * +-- SuitCard (cartes normales : As, 2, 3, 4, ... 8)
 * +-- JokerCard (carte Joker unique)
 * +-- TrophyCard (cartes trophees)
 * </pre>
 * 
 * <h2>Patron Visitor :</h2>
 * <p>Le calcul du score utilise le patron Visitor via la methode
 * {@link #acceptScore(ScoreVisitor)}. Cela permet de modifier le calcul
 * du score sans modifier les classes de cartes.</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see SuitCard
 * @see JokerCard
 * @see TrophyCard
 * @see ScoreVisitor
 */
public abstract class Card implements Serializable {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /** Couleur de la carte. Pour le Joker, suit = Suits.JOKER */
    protected Suits suit;
    
    /** Rang de la carte. Pour le Joker, rank = Rank.JOKER */
    protected Rank rank;

    /**
     * Constructeur de la classe Card.
     * 
     * @param suit la couleur de la carte
     * @param rank le rang de la carte
     * @throws IllegalArgumentException si suit ou rank est null
     */
    public Card(Suits suit, Rank rank) {
        if (suit == null || rank == null) {
            throw new IllegalArgumentException("La couleur et le rang ne peuvent pas etre null");
        }
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Retourne la couleur de la carte.
     * 
     * @return la couleur (Suits) de la carte
     */
    public Suits getSuit() { 
        return suit; 
    }
    
    /**
     * Retourne le rang de la carte.
     * 
     * @return le rang (Rank) de la carte
     */
    public Rank getRank() { 
        return rank; 
    }

    /**
     * Verifie si cette carte est le Joker.
     * 
     * @return true si la carte est le Joker, false sinon
     */
    public boolean isJoker() {
        return rank == Rank.JOKER;
    }

    /**
     * Accepte un visiteur pour le calcul du score.
     * 
     * <p>Cette methode implemente le patron Visitor.
     * Chaque sous-classe doit appeler la methode appropriee du visiteur.</p>
     * 
     * @param visitor le visiteur calculant le score
     * @return le score de cette carte selon les regles du visiteur
     */
    public abstract int acceptScore(ScoreVisitor visitor);

    /**
     * Retourne une representation textuelle de la carte.
     * 
     * @return une chaine decrivant la carte (ex: "As de Piques")
     */
    @Override
    public String toString() {
        if (isJoker()) {
            return "Joker ★";
        }
        return rank.getDisplayName() + " de " + suit.getName();
    }
}'''

    files["Jest_Project/src/model/SuitCard.java"] = '''package model;

/**
 * Classe representant une carte classique (non Joker) du jeu Jest.
 * 
 * <p>Une SuitCard est une carte normale avec une couleur (Piques, Trefles,
 * Carreaux, Coeurs) et un rang (As, 2, 3, 4, ou jusqu a 8 avec l extension).</p>
 * 
 * <h2>Calcul du score :</h2>
 * <ul>
 *   <li>Piques/Trefles : +valeur du rang</li>
 *   <li>Carreaux : -valeur du rang</li>
 *   <li>Coeurs : 0 (sauf regles speciales avec Joker)</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Card
 * @see Suits
 * @see Rank
 */
public class SuitCard extends Card {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur d une carte classique.
     * 
     * @param suit la couleur de la carte (ne doit pas etre JOKER)
     * @param rank le rang de la carte (ne doit pas etre JOKER)
     * @throws IllegalArgumentException si suit est JOKER ou rank est JOKER
     */
    public SuitCard(Suits suit, Rank rank) {
        super(suit, rank);
        if (suit == Suits.JOKER || rank == Rank.JOKER) {
            throw new IllegalArgumentException("Une SuitCard ne peut pas etre un Joker");
        }
    }

    /**
     * Accepte un visiteur pour le calcul du score.
     * 
     * @param visitor le visiteur calculant le score
     * @return le score de cette carte selon les regles du visiteur
     */
    @Override
    public int acceptScore(ScoreVisitor visitor) {
        return visitor.score(this);
    }

    /**
     * Retourne une representation compacte de la carte.
     * 
     * @return le rang suivi du symbole de la couleur (ex: "As ♠")
     */
    @Override
    public String toString() {
        return getRank().getDisplayName() + " " + getSuit().getSymbol();
    }
}'''

    files["Jest_Project/src/model/JokerCard.java"] = '''package model;

/**
 * Classe representant la carte Joker unique du jeu Jest.
 * 
 * <p>Le Joker est une carte speciale avec des regles de scoring complexes
 * dependant du nombre de Coeurs dans le Jest du joueur.</p>
 * 
 * <h2>Regles de scoring du Joker :</h2>
 * <table border="1">
 *   <tr><th>Coeurs dans le Jest</th><th>Effet du Joker</th></tr>
 *   <tr><td>0 Coeur</td><td>+4 points bonus</td></tr>
 *   <tr><td>1, 2 ou 3 Coeurs</td><td>Le Joker ne vaut rien, chaque Coeur reduit le Jest</td></tr>
 *   <tr><td>4 Coeurs</td><td>Chaque Coeur augmente le Jest de sa valeur faciale</td></tr>
 * </table>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Card
 * @see model.rules.Rule1
 */
public class JokerCard extends Card {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur du Joker.
     * 
     * <p>Le Joker est unique et possede la couleur et le rang speciaux JOKER.</p>
     */
    public JokerCard() {
        super(Suits.JOKER, Rank.JOKER);
    }

    /**
     * Accepte un visiteur pour le calcul du score.
     * 
     * @param visitor le visiteur calculant le score
     * @return 0 (le score reel est calcule au niveau du Jest)
     */
    @Override
    public int acceptScore(ScoreVisitor visitor) {
        return visitor.score(this);
    }

    /**
     * Retourne une representation textuelle du Joker.
     * 
     * @return "Joker ★"
     */
    @Override
    public String toString() { 
        return "Joker ★"; 
    }
}'''

    # ==========================================================================
    #                              TROPHY CLASSES
    # ==========================================================================
    
    files["Jest_Project/src/model/TrophyCondition.java"] = '''package model;

import java.io.Serializable;

/**
 * Interface fonctionnelle definissant la condition d obtention d un trophee.
 * 
 * <p>Cette interface utilise le concept de lambda Java 8+ pour permettre
 * une definition flexible des conditions de trophee.</p>
 * 
 * <h2>Exemples de conditions :</h2>
 * <ul>
 *   <li>Majorite d une couleur</li>
 *   <li>Plus haute/basse valeur d une couleur</li>
 *   <li>Possession du Joker</li>
 *   <li>Meilleur Jest total</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see TrophyCard
 * @see TrophyEffect
 */
@FunctionalInterface
public interface TrophyCondition extends Serializable {
    
    /**
     * Teste si un joueur remplit la condition pour obtenir le trophee.
     * 
     * @param player le joueur a tester
     * @param game   la partie en cours (pour comparer avec les autres joueurs)
     * @return true si le joueur remplit la condition, false sinon
     */
    boolean test(Player player, Game game);
}'''

    files["Jest_Project/src/model/TrophyEffect.java"] = '''package model;

import java.io.Serializable;

/**
 * Interface fonctionnelle definissant l effet d un trophee sur un joueur.
 * 
 * <p>Cette interface utilise le concept de lambda Java 8+ pour permettre
 * une definition flexible des effets de trophee.</p>
 * 
 * <h2>Exemples d effets :</h2>
 * <ul>
 *   <li>Ajouter des points bonus</li>
 *   <li>Modifier le score final</li>
 *   <li>Attribution d une carte supplementaire</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see TrophyCard
 * @see TrophyCondition
 */
@FunctionalInterface
public interface TrophyEffect extends Serializable {
    
    /**
     * Applique l effet du trophee au joueur gagnant.
     * 
     * @param player le joueur ayant gagne le trophee
     * @param game   la partie en cours
     */
    void apply(Player player, Game game);
}'''

    files["Jest_Project/src/model/TrophyCard.java"] = '''package model;

import java.io.Serializable;

/**
 * Classe representant une carte trophee du jeu Jest.
 * 
 * <p>Les trophees sont des cartes speciales tirees au debut de la partie
 * et attribuees aux joueurs remplissant certaines conditions en fin de partie.</p>
 * 
 * <h2>Types de trophees (selon les regles officielles) :</h2>
 * 
 * <h3>Trophees de valeur :</h3>
 * <ul>
 *   <li><b>Highest [Couleur]</b> : Joueur avec la plus haute valeur dans une couleur</li>
 *   <li><b>Lowest [Couleur]</b> : Joueur avec la plus basse valeur dans une couleur</li>
 * </ul>
 * 
 * <h3>Trophees de majorite :</h3>
 * <ul>
 *   <li><b>Majority</b> : Joueur avec le plus de cartes d une couleur donnee</li>
 * </ul>
 * 
 * <h3>Trophees speciaux :</h3>
 * <ul>
 *   <li><b>Joker</b> : Joueur possedant le Joker</li>
 *   <li><b>Best Jest</b> : Joueur avec le meilleur score Jest</li>
 *   <li><b>Best Jest, No Joke</b> : Meilleur Jest parmi ceux sans Joker</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see TrophyCondition
 * @see TrophyEffect
 */
public class TrophyCard extends Card implements Serializable {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /** Nom du trophee pour l affichage. */
    private String name;
    
    /** Condition d obtention du trophee. */
    private TrophyCondition condition;
    
    /** Effet applique au gagnant du trophee. */
    private TrophyEffect effect;
    
    /** Carte originale ayant determine le trophee (optionnel). */
    private Card originalCard;

    /**
     * Constructeur d une carte trophee.
     * 
     * @param name      le nom du trophee
     * @param condition la condition d obtention
     * @param effect    l effet applique au gagnant
     */
    public TrophyCard(String name, TrophyCondition condition, TrophyEffect effect) {
        super(Suits.JOKER, Rank.JOKER);
        this.name = name;
        this.condition = condition;
        this.effect = effect;
        this.originalCard = null;
    }
    
    /**
     * Constructeur d une carte trophee avec carte originale.
     * 
     * @param name         le nom du trophee
     * @param condition    la condition d obtention
     * @param effect       l effet applique au gagnant
     * @param originalCard la carte originale ayant determine le trophee
     */
    public TrophyCard(String name, TrophyCondition condition, TrophyEffect effect, Card originalCard) {
        super(Suits.JOKER, Rank.JOKER);
        this.name = name;
        this.condition = condition;
        this.effect = effect;
        this.originalCard = originalCard;
    }

    /**
     * Verifie si un joueur remplit la condition pour gagner ce trophee.
     * 
     * @param player le joueur a tester
     * @param game   la partie en cours
     * @return true si le joueur remplit la condition
     */
    public boolean isWonBy(Player player, Game game) {
        return condition.test(player, game);
    }

    /**
     * Applique l effet du trophee au joueur gagnant.
     * 
     * @param player le joueur gagnant le trophee
     * @param game   la partie en cours
     */
    public void applyTo(Player player, Game game) {
        effect.apply(player, game);
    }

    /**
     * Retourne le nom du trophee.
     * 
     * @return le nom du trophee
     */
    public String getName() { 
        return name; 
    }
    
    /**
     * Retourne la carte originale ayant determine le trophee.
     * 
     * @return la carte originale, ou null si non applicable
     */
    public Card getOriginalCard() {
        return originalCard;
    }

    /**
     * Un trophee n a pas de score propre.
     * 
     * @param visitor le visiteur (ignore)
     * @return toujours 0
     */
    @Override
    public int acceptScore(ScoreVisitor visitor) {
        return 0;
    }

    /**
     * Un trophee n est jamais considere comme un Joker.
     * 
     * @return toujours false
     */
    @Override
    public boolean isJoker() {
        return false;
    }

    /**
     * Retourne une representation textuelle du trophee.
     * 
     * @return le nom du trophee avec l emoji trophee
     */
    @Override
    public String toString() {
        return "TROPHY " + name;
    }
}'''

    # ==========================================================================
    #                           SCORE VISITOR & JEST
    # ==========================================================================
    
    files["Jest_Project/src/model/ScoreVisitor.java"] = '''package model;

import java.io.Serializable;

/**
 * Interface Visitor pour le calcul du score des cartes et des Jests.
 * 
 * <p>Cette interface implemente le patron de conception Visitor permettant
 * de separer l algorithme de calcul de score des classes de cartes.</p>
 * 
 * <h2>Avantages du patron Visitor :</h2>
 * <ul>
 *   <li>Ajout facile de nouvelles regles de calcul</li>
 *   <li>Separation des responsabilites</li>
 *   <li>Possibilite de variantes (Rule1, Rule2, Rule3)</li>
 * </ul>
 * 
 * <h2>Implementations disponibles :</h2>
 * <ul>
 *   <li>{@link model.rules.Rule1} - Regles officielles</li>
 *   <li>{@link model.rules.Rule2} - Variante trophees inverses</li>
 *   <li>{@link model.rules.Rule3} - Variante couleurs inversees</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Card
 * @see Jest
 */
public interface ScoreVisitor extends Serializable {
    
    /**
     * Calcule le score total d un Jest.
     * 
     * <p>Cette methode prend en compte toutes les cartes du Jest
     * ainsi que les bonus/malus speciaux (paires noires, Joker, As isole).</p>
     * 
     * @param jest le Jest a evaluer
     * @return le score total du Jest
     */
    int score(Jest jest);
    
    /**
     * Calcule le score d une carte classique.
     * 
     * @param card la carte a evaluer
     * @return le score de la carte selon sa couleur
     */
    int score(SuitCard card);
    
    /**
     * Calcule le score du Joker.
     * 
     * <p>Note : Le score reel du Joker depend du contexte (nombre de Coeurs)
     * et est generalement calcule dans la methode score(Jest).</p>
     * 
     * @param card le Joker
     * @return 0 (le score est calcule au niveau du Jest)
     */
    int score(JokerCard card);
}'''

    files["Jest_Project/src/model/Jest.java"] = '''package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe representant le Jest d un joueur (collection de cartes gagnees).
 * 
 * <p>Le Jest est l ensemble des cartes qu un joueur a collectees pendant la partie.
 * C est sur cette collection que le score final est calcule.</p>
 * 
 * <h2>Contenu d un Jest :</h2>
 * <ul>
 *   <li>Cartes classiques (SuitCard) prises des offres</li>
 *   <li>Le Joker (si le joueur l a obtenu)</li>
 *   <li>Cartes trophees gagnees en fin de partie</li>
 * </ul>
 * 
 * <h2>Methodes de comptage :</h2>
 * <ul>
 *   <li>{@link #countSuit(Suits)} - Compte les cartes d une couleur</li>
 *   <li>{@link #countRank(Rank)} - Compte les cartes d un rang</li>
 *   <li>{@link #hasJoker()} - Verifie la presence du Joker</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Player
 * @see Card
 */
public class Jest implements Serializable {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /** Liste des cartes dans le Jest. */
    private List<Card> cards = new ArrayList<>();

    /**
     * Ajoute une carte au Jest.
     * 
     * @param card la carte a ajouter
     */
    public void addCard(Card card) { 
        cards.add(card); 
    }
    
    /**
     * Ajoute un trophee au Jest.
     * 
     * @param trophy le trophee a ajouter
     */
    public void addTrophy(TrophyCard trophy) { 
        cards.add(trophy); 
    }

    /**
     * Retourne une copie de la liste des cartes.
     * 
     * @return une nouvelle liste contenant toutes les cartes du Jest
     */
    public List<Card> getCards() { 
        return new ArrayList<>(cards); 
    }

    /**
     * Accepte un visiteur pour le calcul du score.
     * 
     * @param visitor le visiteur calculant le score
     * @return le score total du Jest
     */
    public int accept(ScoreVisitor visitor) {
        return visitor.score(this);
    }

    /**
     * Compte le nombre de cartes d une couleur donnee.
     * 
     * @param s la couleur a compter
     * @return le nombre de cartes de cette couleur (hors trophees)
     */
    public int countSuit(Suits s) {
        return (int) cards.stream()
                .filter(c -> !(c instanceof TrophyCard))
                .filter(c -> c.getSuit() == s)
                .count();
    }

    /**
     * Compte le nombre de cartes d un rang donne.
     * 
     * @param r le rang a compter
     * @return le nombre de cartes de ce rang (hors trophees)
     */
    public int countRank(Rank r) {
        return (int) cards.stream()
                .filter(c -> !(c instanceof TrophyCard))
                .filter(c -> c.getRank() == r)
                .count();
    }

    /**
     * Verifie si le Jest contient le Joker.
     * 
     * @return true si le Joker est present, false sinon
     */
    public boolean hasJoker() {
        return cards.stream().anyMatch(Card::isJoker);
    }
    
    /**
     * Calcule la somme des valeurs des cartes d une couleur.
     * 
     * @param suit la couleur a calculer
     * @return la somme des valeurs faciales
     */
    public int getSuitValue(Suits suit) {
        return cards.stream()
                .filter(c -> !(c instanceof TrophyCard))
                .filter(c -> c.getSuit() == suit)
                .mapToInt(c -> c.getRank().getValue())
                .sum();
    }

    /**
     * Retourne une representation textuelle du Jest.
     * 
     * @return la liste des cartes sous forme de chaine
     */
    @Override
    public String toString() {
        return cards.toString();
    }
}'''

    # ==========================================================================
    #                              OFFER CLASS
    # ==========================================================================
    
    files["Jest_Project/src/model/Offer.java"] = '''package model;

import java.io.Serializable;

/**
 * Classe representant l offre d un joueur pendant un tour.
 * 
 * <p>Une offre est composee de deux cartes :</p>
 * <ul>
 *   <li>Une carte face visible (faceUp) que tous peuvent voir</li>
 *   <li>Une carte face cachee (faceDown) dont seul le joueur connait la valeur</li>
 * </ul>
 * 
 * <h2>Regles concernant les offres :</h2>
 * <ul>
 *   <li>Chaque joueur fait une offre au debut de chaque tour</li>
 *   <li>L ordre de jeu est determine par la carte visible la plus haute</li>
 *   <li>Un joueur ne peut pas prendre dans sa propre offre (sauf dernier)</li>
 *   <li>La carte non choisie va dans le pool pour le tour suivant</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Player
 * @see Card
 */
public class Offer implements Serializable {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /** Carte visible de l offre. */
    private Card faceUp;
    
    /** Carte cachee de l offre. */
    private Card faceDown;
    
    /** Joueur proprietaire de l offre. */
    private Player owner;

    /**
     * Constructeur d une offre vide.
     * 
     * @param owner le joueur proprietaire de l offre
     */
    public Offer(Player owner) {
        this.owner = owner;
    }

    /**
     * Retourne le proprietaire de l offre.
     * 
     * @return le joueur proprietaire
     */
    public Player getOwner() { 
        return owner; 
    }
    
    /**
     * Retourne la carte face visible.
     * 
     * @return la carte visible, ou null si pas encore definie
     */
    public Card getFaceUp() { 
        return faceUp; 
    }
    
    /**
     * Retourne la carte face cachee.
     * 
     * @return la carte cachee, ou null si pas encore definie
     */
    public Card getFaceDown() { 
        return faceDown; 
    }

    /**
     * Definit la carte face visible.
     * 
     * @param card la carte a mettre face visible
     */
    public void setFaceUp(Card card) { 
        faceUp = card; 
    }
    
    /**
     * Definit la carte face cachee.
     * 
     * @param card la carte a mettre face cachee
     */
    public void setFaceDown(Card card) { 
        faceDown = card; 
    }

    /**
     * Verifie si l offre est complete (deux cartes definies).
     * 
     * @return true si les deux cartes sont definies
     */
    public boolean isComplete() { 
        return faceUp != null && faceDown != null; 
    }

    /**
     * Verifie si l offre contient une carte donnee.
     * 
     * @param card la carte a chercher
     * @return true si la carte est dans l offre
     */
    public boolean contains(Card card) {
        return card == faceUp || card == faceDown;
    }

    /**
     * Retourne la carte non choisie de l offre.
     * 
     * @param chosen la carte qui a ete choisie
     * @return l autre carte de l offre, ou null si la carte n est pas dans l offre
     */
    public Card getUnchosen(Card chosen) {
        if (chosen == faceUp) return faceDown;
        if (chosen == faceDown) return faceUp;
        return null;
    }

    /**
     * Prend et retire la carte face visible.
     * 
     * @return la carte qui etait face visible
     */
    public Card takeFaceUp() {
        Card c = faceUp;
        faceUp = null;
        return c;
    }

    /**
     * Prend et retire la carte face cachee.
     * 
     * @return la carte qui etait face cachee
     */
    public Card takeFaceDown() {
        Card c = faceDown;
        faceDown = null;
        return c;
    }

    /**
     * Retourne une representation textuelle de l offre.
     * 
     * @return description de l offre
     */
    @Override
    public String toString() {
        return "[Visible: " + faceUp + ", Cachee: " + (faceDown != null ? "X" : "?") + "]";
    }
}'''

    # ==========================================================================
    #                              PLAYER CLASSES
    # ==========================================================================
    
    files["Jest_Project/src/model/Player.java"] = '''package model;

import strategy.Strategy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite representant un joueur du jeu Jest.
 * 
 * <p>Cette classe definit le comportement commun a tous les joueurs,
 * qu ils soient humains ou controles par l IA.</p>
 * 
 * <h2>Hierarchie des joueurs :</h2>
 * <pre>
 * Player (abstraite)
 * +-- HumanPlayer (joueur humain, choix via l interface)
 * +-- AIPlayer (joueur IA, choix via une strategie)
 * </pre>
 * 
 * <h2>Gestion du score :</h2>
 * <p>Le score final d un joueur est compose de :</p>
 * <ul>
 *   <li>baseScore : score calcule sur les cartes du Jest</li>
 *   <li>trophyBonus : bonus des trophees gagnes</li>
 *   <li>finalScore = baseScore + trophyBonus</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see HumanPlayer
 * @see AIPlayer
 * @see Jest
 */
public abstract class Player implements Serializable {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /** Nom du joueur. */
    protected String name;
    
    /** Jest du joueur (cartes collectees). */
    protected Jest jest;
    
    /** Strategie de l IA (null pour un humain). */
    protected Strategy strategy;
    
    /** Main courante du joueur. */
    protected List<Card> hand;
    
    /** Score de base calcule sur les cartes. */
    protected int baseScore;
    
    /** Bonus provenant des trophees. */
    protected int trophyBonus;

    /**
     * Constructeur d un joueur.
     * 
     * @param name le nom du joueur
     */
    public Player(String name) {
        this.name = name;
        this.jest = new Jest();
        this.hand = new ArrayList<>();
        this.baseScore = 0;
        this.trophyBonus = 0;
    }

    /**
     * Recoit des cartes dans la main.
     * 
     * @param cards les cartes a recevoir
     */
    public void receiveCards(List<Card> cards) {
        hand.addAll(cards);
    }

    /**
     * Cree une offre pour ce tour.
     * 
     * @param game la partie en cours
     * @return l offre creee (ou null si geree par la vue)
     */
    public abstract Offer makeOffer(Game game);

    /**
     * Choisit une carte parmi les offres disponibles.
     * 
     * @param offers les offres disponibles
     * @param game   la partie en cours
     * @return la carte choisie (ou null si geree par la vue)
     */
    public abstract Card chooseFrom(List<Offer> offers, Game game);

    /**
     * Indique si ce joueur est un humain.
     * 
     * @return true si humain, false si IA
     */
    public abstract boolean isHuman();

    /**
     * Retourne le Jest du joueur.
     * 
     * @return le Jest
     */
    public Jest getJest() { 
        return jest; 
    }
    
    /**
     * Retourne le nom du joueur.
     * 
     * @return le nom
     */
    public String getName() { 
        return name; 
    }
    
    /**
     * Retourne la main courante.
     * 
     * @return la liste des cartes en main
     */
    public List<Card> getHand() { 
        return hand; 
    }

    /**
     * Vide la main du joueur.
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Retourne le score de base.
     * 
     * @return le score de base
     */
    public int getBaseScore() {
        return baseScore;
    }

    /**
     * Definit le score de base.
     * 
     * @param baseScore le nouveau score de base
     */
    public void setBaseScore(int baseScore) {
        this.baseScore = baseScore;
    }

    /**
     * Retourne le bonus des trophees.
     * 
     * @return le bonus
     */
    public int getTrophyBonus() {
        return trophyBonus;
    }

    /**
     * Ajoute un bonus de trophee.
     * 
     * @param bonus le bonus a ajouter
     */
    public void addTrophyBonus(int bonus) {
        this.trophyBonus += bonus;
    }

    /**
     * Calcule et retourne le score final.
     * 
     * @return baseScore + trophyBonus
     */
    public int getFinalScore() {
        return baseScore + trophyBonus;
    }

    /**
     * Retourne une representation textuelle du joueur.
     * 
     * @return nom et scores du joueur
     */
    @Override
    public String toString() {
        return name + " (base=" + baseScore + ", trophees=" + trophyBonus + ", total=" + getFinalScore() + ")";
    }
}'''

    files["Jest_Project/src/model/HumanPlayer.java"] = '''package model;

import java.util.List;

/**
 * Classe representant un joueur humain.
 * 
 * <p>Les choix d un joueur humain sont effectues via l interface utilisateur
 * (ConsoleView ou JestGUI). Les methodes makeOffer et chooseFrom retournent
 * null car elles sont gerees par le controleur et la vue.</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Player
 * @see view.ConsoleView
 */
public class HumanPlayer extends Player {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur d un joueur humain.
     * 
     * @param name le nom du joueur
     */
    public HumanPlayer(String name) {
        super(name);
    }

    /**
     * Indique que ce joueur est un humain.
     * 
     * @return toujours true
     */
    @Override
    public boolean isHuman() { 
        return true; 
    }

    /**
     * Cree une offre - geree par la vue.
     * 
     * @param game la partie en cours
     * @return null (gere par ConsoleView dans GameEngine)
     */
    @Override
    public Offer makeOffer(Game game) {
        return null;
    }

    /**
     * Choisit une carte - geree par la vue.
     * 
     * @param offers les offres disponibles
     * @param game   la partie en cours
     * @return null (gere par ConsoleView dans GameEngine)
     */
    @Override
    public Card chooseFrom(List<Offer> offers, Game game) {
        return null;
    }
}'''

    files["Jest_Project/src/model/AIPlayer.java"] = '''package model;

import strategy.Strategy;
import java.util.List;

/**
 * Classe representant un joueur controle par l intelligence artificielle.
 * 
 * <p>Un AIPlayer utilise une strategie ({@link Strategy}) pour prendre
 * ses decisions automatiquement.</p>
 * 
 * <h2>Strategies disponibles :</h2>
 * <ul>
 *   <li>{@link strategy.Strategy1} - Strategie conservatrice</li>
 *   <li>{@link strategy.Strategy2} - Strategie bluff</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Player
 * @see Strategy
 */
public class AIPlayer extends Player {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur d un joueur IA.
     * 
     * @param name     le nom du joueur
     * @param strategy la strategie a utiliser
     */
    public AIPlayer(String name, Strategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    /**
     * Indique que ce joueur est une IA.
     * 
     * @return toujours false
     */
    @Override
    public boolean isHuman() { 
        return false; 
    }

    /**
     * Cree une offre en utilisant la strategie.
     * 
     * @param game la partie en cours
     * @return l offre creee par la strategie
     */
    @Override
    public Offer makeOffer(Game game) {
        return strategy.chooseOffer(this, hand, game);
    }

    /**
     * Choisit une carte en utilisant la strategie.
     * 
     * @param offers les offres disponibles
     * @param game   la partie en cours
     * @return la carte choisie par la strategie
     */
    @Override
    public Card chooseFrom(List<Offer> offers, Game game) {
        return strategy.chooseTake(this, offers, game);
    }
}'''

    # ==========================================================================
    #                              DECK CLASS
    # ==========================================================================
    
    files["Jest_Project/src/model/Deck.java"] = '''package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe representant le paquet de cartes du jeu Jest.
 * 
 * <p>Le deck contient les cartes du jeu et gere leur distribution.
 * La composition du deck depend des extensions activees.</p>
 * 
 * <h2>Composition standard (17 cartes) :</h2>
 * <ul>
 *   <li>16 cartes de couleur (4 couleurs x 4 rangs)</li>
 *   <li>1 Joker</li>
 * </ul>
 * 
 * <h2>Avec extension Cartes supplementaires (33 cartes) :</h2>
 * <ul>
 *   <li>32 cartes de couleur (4 couleurs x 8 rangs)</li>
 *   <li>1 Joker</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Card
 * @see Game
 */
public class Deck implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private List<Card> cards;
    private boolean extendedDeck;

    /**
     * Constructeur du deck.
     */
    public Deck() {
        this.cards = new ArrayList<>();
        this.extendedDeck = false;
    }

    /**
     * Initialise le deck avec les cartes appropriees.
     * 
     * @param includeExtension true pour inclure les cartes 5-8
     */
    public void initialize(boolean includeExtension) {
        cards.clear();
        this.extendedDeck = includeExtension;

        Rank[] ranks;
        if (includeExtension) {
            ranks = new Rank[]{Rank.ACE, Rank.TWO, Rank.THREE, Rank.FOUR, 
                              Rank.FIVE, Rank.SIX, Rank.SEVEN, Rank.EIGHT};
        } else {
            ranks = new Rank[]{Rank.ACE, Rank.TWO, Rank.THREE, Rank.FOUR};
        }

        for (Suits suit : new Suits[]{Suits.SPADES, Suits.CLUBS, Suits.DIAMONDS, Suits.HEARTS}) {
            for (Rank rank : ranks) {
                cards.add(new SuitCard(suit, rank));
            }
        }

        cards.add(new JokerCard());
    }

    /**
     * Melange le deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Pioche la premiere carte du deck.
     * 
     * @return la carte piochee, ou null si le deck est vide
     */
    public Card draw() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    public void addCards(List<Card> cardsToAdd) {
        for (Card c : cardsToAdd) {
            if (c != null) cards.add(c);
        }
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
    
    public boolean isExtended() {
        return extendedDeck;
    }
}'''

    # ==========================================================================
    #                              RULESET & RULES
    # ==========================================================================
    
    files["Jest_Project/src/model/RuleSet.java"] = '''package model;

import java.io.Serializable;
import java.util.List;

/**
 * Interface definissant un ensemble de regles pour le jeu Jest.
 * 
 * <p>Cette interface permet de creer differentes variantes du jeu
 * en modifiant le calcul des scores et la gestion des trophees.</p>
 * 
 * <h2>Implementations disponibles :</h2>
 * <ul>
 *   <li>{@link model.rules.Rule1} - Regles officielles</li>
 *   <li>{@link model.rules.Rule2} - Trophees inverses</li>
 *   <li>{@link model.rules.Rule3} - Couleurs inversees</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 */
public interface RuleSet extends Serializable {
    
    /**
     * Retourne le visiteur de score pour ces regles.
     * 
     * @return le ScoreVisitor a utiliser
     */
    ScoreVisitor scoreVisitor();
    
    /**
     * Retourne la liste des trophees pour ces regles.
     * 
     * @return la liste des TrophyCard
     */
    List<TrophyCard> trophies();
    
    /**
     * Retourne le nombre de trophees selon le nombre de joueurs.
     * 
     * @param playerCount le nombre de joueurs
     * @return le nombre de trophees (1 pour 4 joueurs, 2 sinon)
     */
    int numberOfTrophies(int playerCount);
}'''

    files["Jest_Project/src/model/rules/Rule1.java"] = '''package model.rules;

import model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Regles officielles du jeu Jest.
 * 
 * <p>Cette classe implemente les regles de scoring officielles selon
 * le manuel du jeu Jest.</p>
 * 
 * <h2>Calcul du score :</h2>
 * <ul>
 *   <li>Piques et Trefles : +valeur faciale</li>
 *   <li>Carreaux : -valeur faciale</li>
 *   <li>Coeurs : 0 (sauf avec le Joker)</li>
 * </ul>
 * 
 * <h2>Regles speciales :</h2>
 * <ul>
 *   <li>As isole : vaut 5 points au lieu de 1</li>
 *   <li>Paire noire (meme valeur Pique+Trefle) : +2 points</li>
 *   <li>Joker sans Coeur : +4 points</li>
 *   <li>Joker avec 1-3 Coeurs : Joker=0, Coeurs=-valeur</li>
 *   <li>Joker avec 4 Coeurs : Coeurs=+valeur</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 */
public class Rule1 implements RuleSet {
    private static final long serialVersionUID = 1L;

    @Override
    public ScoreVisitor scoreVisitor() {
        return new BaseScoreVisitor();
    }

    @Override
    public List<TrophyCard> trophies() {
        return new ArrayList<>();
    }

    @Override
    public int numberOfTrophies(int playerCount) {
        return playerCount == 4 ? 1 : 2;
    }

    /**
     * Visitor pour le calcul du score selon les regles officielles.
     */
    private static class BaseScoreVisitor implements ScoreVisitor {
        private static final long serialVersionUID = 1L;

        @Override
        public int score(Jest jest) {
            int total = 0;

            // Somme des valeurs individuelles
            for (Card card : jest.getCards()) {
                if (!(card instanceof TrophyCard)) {
                    total += card.acceptScore(this);
                }
            }

            // Bonus paires noires
            total += calculateBlackPairs(jest);

            // Bonus As isole
            total += calculateAceBonus(jest);

            // Score special du Joker
            total += calculateJokerScore(jest);

            return total;
        }

        @Override
        public int score(SuitCard card) {
            switch (card.getSuit()) {
                case SPADES:
                case CLUBS:
                    return card.getRank().getValue();
                case DIAMONDS:
                    return -card.getRank().getValue();
                case HEARTS:
                default:
                    return 0;
            }
        }

        @Override
        public int score(JokerCard card) {
            return 0;
        }

        /**
         * Calcule le bonus des paires noires (meme rang Pique+Trefle).
         */
        private int calculateBlackPairs(Jest jest) {
            int bonus = 0;

            for (Rank rank : new Rank[]{Rank.ACE, Rank.TWO, Rank.THREE, Rank.FOUR,
                                        Rank.FIVE, Rank.SIX, Rank.SEVEN, Rank.EIGHT}) {
                boolean hasSpade = jest.getCards().stream()
                        .anyMatch(c -> c.getSuit() == Suits.SPADES && c.getRank() == rank);

                boolean hasClub = jest.getCards().stream()
                        .anyMatch(c -> c.getSuit() == Suits.CLUBS && c.getRank() == rank);

                if (hasSpade && hasClub) bonus += 2;
            }

            return bonus;
        }

        /**
         * Calcule le bonus de l As isole (seule carte de sa couleur = 5 pts).
         */
        private int calculateAceBonus(Jest jest) {
            int bonus = 0;

            for (Suits suit : new Suits[]{Suits.SPADES, Suits.CLUBS, Suits.DIAMONDS, Suits.HEARTS}) {
                long count = jest.getCards().stream()
                        .filter(c -> c.getSuit() == suit && !(c instanceof TrophyCard))
                        .count();

                if (count == 1) {
                    Card c = jest.getCards().stream()
                            .filter(k -> k.getSuit() == suit && !(k instanceof TrophyCard))
                            .findFirst().orElse(null);

                    if (c != null && c.getRank() == Rank.ACE) {
                        bonus += 4; // +4 car l As vaut deja 1, total = 5
                    }
                }
            }

            return bonus;
        }

        /**
         * Calcule le score special du Joker selon les Coeurs.
         */
        private int calculateJokerScore(Jest jest) {
            if (!jest.hasJoker()) return 0;

            int hearts = jest.countSuit(Suits.HEARTS);

            if (hearts == 0) {
                return 4; // +4 points bonus
            }

            if (hearts == 4) {
                // Tous les Coeurs valent leur valeur positive
                int totalHearts = jest.getCards().stream()
                        .filter(c -> c.getSuit() == Suits.HEARTS)
                        .mapToInt(c -> c.getRank().getValue())
                        .sum();
                return totalHearts; // Les coeurs valent leur valeur
            }

            // 1, 2 ou 3 Coeurs : le Joker ne vaut rien, les Coeurs sont negatifs
            int malus = jest.getCards().stream()
                    .filter(c -> c.getSuit() == Suits.HEARTS)
                    .mapToInt(c -> c.getRank().getValue())
                    .sum();
            return -malus;
        }
    }
}'''

    files["Jest_Project/src/model/rules/Rule2.java"] = '''package model.rules;

import model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Variante : Trophees inverses.
 * 
 * <p>Dans cette variante, les trophees sont gagnes par le joueur
 * qui les merite le moins (ex: Highest va au joueur avec le moins).</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 */
public class Rule2 implements RuleSet {
    private static final long serialVersionUID = 1L;

    @Override
    public ScoreVisitor scoreVisitor() {
        return new Rule1().scoreVisitor();
    }

    @Override
    public List<TrophyCard> trophies() {
        return new ArrayList<>();
    }

    @Override
    public int numberOfTrophies(int playerCount) {
        return playerCount == 4 ? 1 : 2;
    }
}'''

    files["Jest_Project/src/model/rules/Rule3.java"] = '''package model.rules;

import model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Variante : Couleurs inversees.
 * 
 * <p>Dans cette variante, les couleurs ont des effets inverses :</p>
 * <ul>
 *   <li>Coeurs et Carreaux : +valeur</li>
 *   <li>Piques : 0</li>
 *   <li>Trefles : -valeur</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 */
public class Rule3 implements RuleSet {
    private static final long serialVersionUID = 1L;

    @Override
    public ScoreVisitor scoreVisitor() {
        return new InvertedVisitor();
    }

    @Override
    public List<TrophyCard> trophies() {
        return new ArrayList<>();
    }

    @Override
    public int numberOfTrophies(int playerCount) {
        return playerCount == 4 ? 1 : 2;
    }

    private static class InvertedVisitor implements ScoreVisitor {
        private static final long serialVersionUID = 1L;

        @Override
        public int score(Jest jest) {
            int total = 0;
            for (Card card : jest.getCards()) {
                if (!(card instanceof TrophyCard)) {
                    total += card.acceptScore(this);
                }
            }
            return total;
        }

        @Override
        public int score(SuitCard card) {
            switch (card.getSuit()) {
                case HEARTS:
                case DIAMONDS:
                    return card.getRank().getValue();
                case SPADES:
                    return 0;
                case CLUBS:
                    return -card.getRank().getValue();
                default:
                    return 0;
            }
        }

        @Override
        public int score(JokerCard card) {
            return 0;
        }
    }
}'''

    # ==========================================================================
    #                              TROPHY FACTORY
    # ==========================================================================
    
    files["Jest_Project/src/model/trophy/TrophyFactory.java"] = '''package model.trophy;

import model.*;
import java.util.List;

/**
 * Fabrique de trophees selon les regles officielles du jeu Jest.
 * 
 * <p>Cette classe centralise la creation de tous les types de trophees
 * selon les regles decrites dans le manuel officiel et les cartes.</p>
 * 
 * <h2>Types de trophees :</h2>
 * <ul>
 *   <li>Highest [Couleur] : Plus haute valeur totale d une couleur</li>
 *   <li>Lowest [Couleur] : Plus basse valeur totale d une couleur</li>
 *   <li>Majority : Plus grand nombre de cartes d une couleur</li>
 *   <li>Joker : Possession du Joker</li>
 *   <li>Best Jest : Meilleur score total</li>
 *   <li>Best Jest No Joke : Meilleur score sans le Joker</li>
 * </ul>
 * 
 * <h2>Regles d attribution :</h2>
 * <p>En cas d egalite, le trophee va au joueur dont le Jest contient
 * la carte de la couleur la plus forte dans l ordre :</p>
 * <ol>
 *   <li>Piques (priorite maximale)</li>
 *   <li>Trefles</li>
 *   <li>Carreaux</li>
 *   <li>Coeurs (priorite minimale)</li>
 * </ol>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 2.0
 */
public class TrophyFactory {
    
    /**
     * Priorite des couleurs pour les departages.
     */
    private static int getSuitPriority(Suits suit) {
        switch (suit) {
            case SPADES: return 4;
            case CLUBS: return 3;
            case DIAMONDS: return 2;
            case HEARTS: return 1;
            default: return 0;
        }
    }

    /**
     * Cree un trophee Highest pour une couleur donnee.
     * 
     * @param suit la couleur du trophee
     * @return le trophee cree
     */
    public static TrophyCard createHighestTrophy(Suits suit) {
        String name = "Highest " + suit.getName();
        
        TrophyCondition condition = (player, game) -> {
            int playerValue = getTotalSuitValue(player, suit);
            if (playerValue == 0) return false;
            
            for (Player other : game.getPlayers()) {
                if (other != player) {
                    int otherValue = getTotalSuitValue(other, suit);
                    if (otherValue > playerValue) {
                        return false;
                    }
                    if (otherValue == playerValue) {
                        if (hasHigherPriorityCard(other, player)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        };
        
        TrophyEffect effect = (player, game) -> {};
        
        return new TrophyCard(name, condition, effect);
    }
    
    /**
     * Cree un trophee Lowest pour une couleur donnee.
     * 
     * @param suit la couleur du trophee
     * @return le trophee cree
     */
    public static TrophyCard createLowestTrophy(Suits suit) {
        String name = "Lowest " + suit.getName();
        
        TrophyCondition condition = (player, game) -> {
            int playerValue = getTotalSuitValue(player, suit);
            if (playerValue == 0) return false;
            
            for (Player other : game.getPlayers()) {
                if (other != player) {
                    int otherValue = getTotalSuitValue(other, suit);
                    if (otherValue > 0 && otherValue < playerValue) {
                        return false;
                    }
                    if (otherValue == playerValue) {
                        if (hasHigherPriorityCard(other, player)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        };
        
        TrophyEffect effect = (player, game) -> {};
        
        return new TrophyCard(name, condition, effect);
    }
    
    /**
     * Cree un trophee Majority pour une couleur donnee.
     * 
     * @param suit la couleur du trophee
     * @return le trophee cree
     */
    public static TrophyCard createMajorityTrophy(Suits suit) {
        String name = "Majority " + suit.getName();
        
        TrophyCondition condition = (player, game) -> {
            int playerCount = player.getJest().countSuit(suit);
            if (playerCount == 0) return false;
            
            for (Player other : game.getPlayers()) {
                if (other != player) {
                    int otherCount = other.getJest().countSuit(suit);
                    if (otherCount > playerCount) {
                        return false;
                    }
                }
            }
            return true;
        };
        
        TrophyEffect effect = (player, game) -> {};
        
        return new TrophyCard(name, condition, effect);
    }
    
    /**
     * Cree un trophee Joker.
     * 
     * @return le trophee cree
     */
    public static TrophyCard createJokerTrophy() {
        String name = "Joker";
        TrophyCondition condition = (player, game) -> player.getJest().hasJoker();
        TrophyEffect effect = (player, game) -> {};
        return new TrophyCard(name, condition, effect);
    }
    
    /**
     * Cree un trophee Best Jest.
     * 
     * @return le trophee cree
     */
    public static TrophyCard createBestJestTrophy() {
        String name = "Best Jest";
        
        TrophyCondition condition = (player, game) -> {
            int playerScore = player.getBaseScore();
            for (Player other : game.getPlayers()) {
                if (other != player && other.getBaseScore() > playerScore) {
                    return false;
                }
            }
            return true;
        };
        
        TrophyEffect effect = (player, game) -> {};
        return new TrophyCard(name, condition, effect);
    }
    
    /**
     * Cree un trophee Best Jest No Joke.
     * 
     * @return le trophee cree
     */
    public static TrophyCard createBestJestNoJokeTrophy() {
        String name = "Best Jest No Joke";
        
        TrophyCondition condition = (player, game) -> {
            if (player.getJest().hasJoker()) return false;
            
            int playerScore = player.getBaseScore();
            for (Player other : game.getPlayers()) {
                if (other != player && !other.getJest().hasJoker()) {
                    if (other.getBaseScore() > playerScore) {
                        return false;
                    }
                }
            }
            return true;
        };
        
        TrophyEffect effect = (player, game) -> {};
        return new TrophyCard(name, condition, effect);
    }
    
    /**
     * Cree un trophee a partir d une carte tiree.
     * Le type de trophee depend de la bande orange de la carte.
     * 
     * @param card la carte tiree
     * @return le trophee correspondant
     */
    public static TrophyCard createFromCard(Card card) {
        if (card instanceof JokerCard) {
            return createJokerTrophy();
        }
        
        SuitCard suitCard = (SuitCard) card;
        Suits suit = suitCard.getSuit();
        Rank rank = suitCard.getRank();
        
        // Selon les images des cartes :
        // Les trophees sont indiques par la bande orange
        switch (rank) {
            case ACE:
                return createHighestTrophy(suit);
            case TWO:
                return createLowestTrophy(suit);
            case THREE:
                return createMajorityTrophy(suit);
            case FOUR:
                return createBestJestTrophy();
            default:
                return createMajorityTrophy(suit);
        }
    }
    
    private static int getTotalSuitValue(Player player, Suits suit) {
        return player.getJest().getCards().stream()
                .filter(c -> c.getSuit() == suit && !(c instanceof TrophyCard))
                .mapToInt(c -> c.getRank().getValue())
                .sum();
    }
    
    private static boolean hasHigherPriorityCard(Player other, Player player) {
        int otherMax = getMaxCardPriority(other);
        int playerMax = getMaxCardPriority(player);
        return otherMax > playerMax;
    }
    
    private static int getMaxCardPriority(Player player) {
        return player.getJest().getCards().stream()
                .filter(c -> !(c instanceof TrophyCard))
                .mapToInt(c -> c.getRank().getValue() * 10 + getSuitPriority(c.getSuit()))
                .max()
                .orElse(0);
    }
}'''

    # ==========================================================================
    #                              GAME CLASS
    # ==========================================================================
    
    files["Jest_Project/src/model/Game.java"] = '''package model;

import model.trophy.TrophyFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe principale representant une partie de Jest.
 * 
 * <p>Cette classe gere l ensemble de la logique metier d une partie :</p>
 * <ul>
 *   <li>Gestion des joueurs et du deck</li>
 *   <li>Distribution des cartes et des trophees</li>
 *   <li>Deroulement des tours</li>
 *   <li>Calcul des scores et determination du gagnant</li>
 * </ul>
 * 
 * <h2>Cycle de vie d une partie :</h2>
 * <ol>
 *   <li>Creation de la partie (new Game())</li>
 *   <li>Ajout des joueurs (addPlayer)</li>
 *   <li>Configuration des regles (setRuleSet)</li>
 *   <li>Initialisation du deck</li>
 *   <li>Demarrage (start) - tire les trophees</li>
 *   <li>Tours de jeu (dealRound, endTurn)</li>
 *   <li>Fin de partie (winner)</li>
 * </ol>
 * 
 * <h2>Gestion des trophees selon les regles officielles :</h2>
 * <p>Les trophees sont tires au debut de la partie. La carte tiree
 * determine le type de trophee selon la bande orange sur la carte.</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Player
 * @see Deck
 * @see TrophyCard
 */
public class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Liste des joueurs de la partie. */
    private List<Player> players;
    
    /** Deck de cartes. */
    private Deck deck;
    
    /** Regles utilisees pour cette partie. */
    private RuleSet ruleSet;
    
    /** Numero du tour actuel. */
    private int currentRound;
    
    /** Cartes trophees de la partie. */
    private List<TrophyCard> trophies;
    
    /** Cartes non choisies pour le tour suivant. */
    private List<Card> carryOverCards;
    
    /** Extension BM activee. */
    private boolean extensionBM;
    
    /** Extension cartes supplementaires activee. */
    private boolean extensionCards;

    /**
     * Constructeur d une nouvelle partie.
     */
    public Game() {
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.trophies = new ArrayList<>();
        this.currentRound = 0;
        this.carryOverCards = new ArrayList<>();
        this.extensionBM = false;
        this.extensionCards = false;
    }

    /**
     * Demarre la partie.
     * <p>Melange le deck et tire les trophees.</p>
     */
    public void start() {
        deck.shuffle();
        dealInitialTrophies();
        currentRound = 1;
    }

    /**
     * Tire les cartes trophees du debut de partie.
     */
    private void dealInitialTrophies() {
        int numTrophies = ruleSet != null
                ? ruleSet.numberOfTrophies(players.size())
                : (players.size() == 4 ? 1 : 2);

        trophies.clear();

        for (int i = 0; i < numTrophies; i++) {
            Card card = deck.draw();
            if (card == null) continue;

            TrophyCard trophy = TrophyFactory.createFromCard(card);
            trophies.add(trophy);
        }
    }

    /**
     * Distribue les cartes pour un tour.
     */
    public void dealRound() {
        if (players.isEmpty()) return;

        int needed = players.size() * 2;

        List<Card> pool = new ArrayList<>(carryOverCards);
        carryOverCards.clear();

        int missing = needed - pool.size();
        for (int i = 0; i < missing && !deck.isEmpty(); i++) {
            pool.add(deck.draw());
        }

        if (pool.size() < needed) return;

        Collections.shuffle(pool);

        for (Player player : players) {
            List<Card> hand = new ArrayList<>();
            hand.add(pool.remove(0));
            hand.add(pool.remove(0));
            player.receiveCards(hand);
        }
    }

    /**
     * Ajoute une carte au pool pour le prochain tour.
     * 
     * @param card la carte non choisie
     */
    public void addCarryOverCard(Card card) {
        if (card != null) carryOverCards.add(card);
    }

    /**
     * Verifie si la partie est terminee.
     * 
     * @return true si plus assez de cartes pour un tour
     */
    public boolean isGameOver() {
        int needed = players.size() * 2;
        int available = deck.size() + carryOverCards.size();
        return available < needed;
    }

    /**
     * Calcule les scores et determine le gagnant.
     * 
     * @return le joueur gagnant
     */
    public Player winner() {
        // 1) Calcul du score de base
        ScoreVisitor visitor = ruleSet.scoreVisitor();
        for (Player p : players) {
            int base = visitor.score(p.getJest());
            p.setBaseScore(base);
        }

        // 2) Attribution des trophees
        for (TrophyCard trophy : trophies) {
            Player winner = determineTrophyWinner(trophy);
            if (winner != null) {
                winner.getJest().addTrophy(trophy);
                trophy.applyTo(winner, this);
            }
        }

        // 3) Determination du gagnant
        return players.stream()
                .max(Comparator.comparingInt(Player::getFinalScore))
                .orElse(null);
    }

    /**
     * Determine le gagnant d un trophee.
     */
    private Player determineTrophyWinner(TrophyCard trophy) {
        List<Player> candidates = players.stream()
                .filter(p -> trophy.isWonBy(p, this))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) return null;
        return candidates.get(0);
    }

    /**
     * Affiche les trophees disponibles.
     */
    public void displayAvailableTrophies() {
        System.out.println();
        System.out.println("TROPHEES POUR CETTE PARTIE :");

        if (trophies == null || trophies.isEmpty()) {
            System.out.println("  Aucun trophee pour ces regles.");
            return;
        }

        for (TrophyCard trophy : trophies) {
            System.out.println("  TROPHY " + trophy.getName());
        }
    }

    // Getters et setters
    public List<Player> getPlayers() { return players; }
    public void addPlayer(Player player) { players.add(player); }
    public Deck getDeck() { return deck; }
    public int getCurrentRound() { return currentRound; }
    public void endTurn() { currentRound++; }
    public RuleSet getRuleSet() { return ruleSet; }
    public void setRuleSet(RuleSet ruleSet) { this.ruleSet = ruleSet; }
    public List<TrophyCard> getTrophies() { return trophies; }
    
    public void setExtensionBM(boolean enabled) { this.extensionBM = enabled; }
    public boolean isExtensionBM() { return extensionBM; }
    public void setExtensionCards(boolean enabled) { this.extensionCards = enabled; }
    public boolean isExtensionCards() { return extensionCards; }
}'''

    # ==========================================================================
    #                              STRATEGY CLASSES
    # ==========================================================================
    
    files["Jest_Project/src/strategy/Strategy.java"] = '''package strategy;

import model.*;
import java.io.Serializable;
import java.util.List;

/**
 * Interface definissant une strategie pour les joueurs IA.
 * 
 * <p>Cette interface implemente le patron de conception Strategy,
 * permettant de definir differents comportements pour les joueurs
 * controles par l ordinateur.</p>
 * 
 * <h2>Implementations disponibles :</h2>
 * <ul>
 *   <li>{@link Strategy1} - Strategie conservatrice</li>
 *   <li>{@link Strategy2} - Strategie bluff</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Strategy1
 * @see Strategy2
 */
public interface Strategy extends Serializable {
    
    /**
     * Cree une offre pour le joueur IA.
     * 
     * @param player le joueur IA
     * @param hand   la main du joueur
     * @param game   la partie en cours
     * @return l offre creee
     */
    Offer chooseOffer(Player player, List<Card> hand, Game game);
    
    /**
     * Choisit une carte parmi les offres disponibles.
     * 
     * @param player le joueur IA
     * @param offers les offres disponibles
     * @param game   la partie en cours
     * @return la carte choisie
     */
    Card chooseTake(Player player, List<Offer> offers, Game game);
}'''

    files["Jest_Project/src/strategy/Strategy1.java"] = '''package strategy;

import model.*;
import java.util.Comparator;
import java.util.List;

/**
 * Strategie conservatrice : privilegie les cartes visibles.
 * 
 * <p>Cette strategie :</p>
 * <ul>
 *   <li>Cache la meilleure carte</li>
 *   <li>Montre la moins bonne carte</li>
 *   <li>Prend toujours la carte visible la plus forte</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 */
public class Strategy1 implements Strategy {
    private static final long serialVersionUID = 1L;

    @Override
    public Offer chooseOffer(Player player, List<Card> hand, Game game) {
        Offer offer = new Offer(player);

        Card best = hand.stream()
                .max(Comparator.comparingInt(this::estimateValue))
                .orElse(hand.get(0));

        Card worst = hand.stream()
                .filter(c -> c != best)
                .findFirst()
                .orElse(hand.get(1));

        offer.setFaceDown(best);
        offer.setFaceUp(worst);

        return offer;
    }

    @Override
    public Card chooseTake(Player player, List<Offer> offers, Game game) {
        Offer bestOffer = offers.stream()
                .filter(Offer::isComplete)
                .max(Comparator.comparingInt(o -> estimateValue(o.getFaceUp())))
                .orElse(offers.get(0));

        Card chosen = bestOffer.getFaceUp();
        Card unchosen = bestOffer.getUnchosen(chosen);
        Card result = bestOffer.takeFaceUp();

        game.addCarryOverCard(unchosen);

        return result;
    }

    private int estimateValue(Card card) {
        if (card instanceof JokerCard) return 2;

        switch (card.getSuit()) {
            case SPADES:
            case CLUBS:
                return card.getRank().getValue();
            case DIAMONDS:
                return -card.getRank().getValue();
            case HEARTS:
            default:
                return 1;
        }
    }
}'''

    files["Jest_Project/src/strategy/Strategy2.java"] = '''package strategy;

import model.*;
import java.util.List;
import java.util.Random;

/**
 * Strategie bluff : privilegie les cartes cachees.
 * 
 * <p>Cette strategie :</p>
 * <ul>
 *   <li>Montre la meilleure carte (bluff)</li>
 *   <li>Prend souvent la carte cachee</li>
 *   <li>Comporte une part d aleatoire</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 */
public class Strategy2 implements Strategy {
    private static final long serialVersionUID = 1L;
    private Random random = new Random();

    @Override
    public Offer chooseOffer(Player player, List<Card> hand, Game game) {
        Offer offer = new Offer(player);

        Card c1 = hand.get(0);
        Card c2 = hand.get(1);

        Card best = estimateValue(c1) >= estimateValue(c2) ? c1 : c2;
        Card worst = (best == c1) ? c2 : c1;

        offer.setFaceUp(worst);
        offer.setFaceDown(best);

        return offer;
    }

    @Override
    public Card chooseTake(Player player, List<Offer> offers, Game game) {
        List<Offer> completeOffers = offers.stream()
                .filter(Offer::isComplete)
                .toList();

        Offer chosenOffer;
        if (!completeOffers.isEmpty()) {
            chosenOffer = completeOffers.get(random.nextInt(completeOffers.size()));
        } else {
            chosenOffer = offers.get(0);
        }

        Card chosen = chosenOffer.getFaceDown();
        if (chosen == null) {
            chosen = chosenOffer.getFaceUp();
            Card unchosen = chosenOffer.getUnchosen(chosen);
            Card result = chosenOffer.takeFaceUp();
            game.addCarryOverCard(unchosen);
            return result;
        }

        Card unchosen = chosenOffer.getUnchosen(chosen);
        Card result = chosenOffer.takeFaceDown();
        game.addCarryOverCard(unchosen);
        return result;
    }

    private int estimateValue(Card card) {
        if (card instanceof JokerCard) return 2;

        switch (card.getSuit()) {
            case SPADES:
            case CLUBS:
                return card.getRank().getValue();
            case DIAMONDS:
                return -card.getRank().getValue();
            case HEARTS:
            default:
                return 1;
        }
    }
}'''

    # ==========================================================================
    #                              OBSERVER PATTERN
    # ==========================================================================
    
    files["Jest_Project/src/observer/GameObserver.java"] = '''package observer;

import model.*;
import java.util.List;

/**
 * Interface Observer pour les vues du jeu Jest.
 * 
 * <p>Cette interface definit les methodes de notification que les vues
 * doivent implementer pour etre informees des changements du modele.</p>
 * 
 * <h2>Patron MVC :</h2>
 * <p>Cette interface fait partie de l implementation du patron MVC
 * avec deux vues concurrentes (Console et GUI).</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 2.0
 */
public interface GameObserver {
    
    /**
     * Notifie le debut d un nouveau tour.
     * 
     * @param roundNumber le numero du tour
     */
    void onRoundStart(int roundNumber);
    
    /**
     * Notifie que les offres ont ete creees.
     * 
     * @param offers la liste des offres
     */
    void onOffersCreated(List<Offer> offers);
    
    /**
     * Notifie qu une carte a ete prise.
     * 
     * @param player le joueur
     * @param card   la carte prise
     */
    void onCardTaken(Player player, Card card);
    
    /**
     * Notifie la fin de la partie.
     * 
     * @param winner le gagnant
     */
    void onGameEnd(Player winner);
    
    /**
     * Notifie un message general.
     * 
     * @param message le message
     */
    void onMessage(String message);
}'''

    files["Jest_Project/src/observer/GameObservable.java"] = '''package observer;

/**
 * Interface Observable pour le modele du jeu Jest.
 * 
 * <p>Cette interface definit les methodes pour gerer les observateurs
 * (vues) qui seront notifies des changements du modele.</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 2.0
 */
public interface GameObservable {
    
    /**
     * Ajoute un observateur.
     * 
     * @param observer l observateur a ajouter
     */
    void addObserver(GameObserver observer);
    
    /**
     * Retire un observateur.
     * 
     * @param observer l observateur a retirer
     */
    void removeObserver(GameObserver observer);
    
    /**
     * Notifie tous les observateurs.
     */
    void notifyObservers();
}'''

    # ==========================================================================
    #                              CONSOLE VIEW
    # ==========================================================================
    
    files["Jest_Project/src/view/ConsoleView.java"] = '''package view;

import model.*;
import model.rules.*;
import strategy.*;
import observer.GameObserver;
import java.util.*;

/**
 * Vue console pour l interface utilisateur du jeu Jest.
 * 
 * <p>Cette classe implemente l interface textuelle du jeu dans le terminal.
 * Elle fait partie de l architecture MVC comme l une des deux vues concurrentes.</p>
 * 
 * <h2>Fonctionnalites :</h2>
 * <ul>
 *   <li>Affichage des menus et messages</li>
 *   <li>Saisie securisee des choix utilisateur</li>
 *   <li>Affichage des offres et des scores</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 */
public class ConsoleView implements GameObserver {
    
    private Scanner scanner;

    /**
     * Constructeur de la vue console.
     */
    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Affiche le message de bienvenue.
     */
    public void displayWelcome() {
        System.out.println("========================================");
        System.out.println("        Bienvenue dans JEST!");
        System.out.println("========================================");
        System.out.println();
    }

    /**
     * Demande le nombre de joueurs.
     * 
     * @return le nombre de joueurs (3 ou 4)
     */
    public int askNumberOfPlayers() {
        return SafeInput.readIntInRange("Nombre de joueurs (3-4) : ", 3, 4);
    }

    /**
     * Demande les informations d un joueur.
     * 
     * @param playerNumber le numero du joueur
     * @return le joueur cree
     */
    public Player askPlayerType(int playerNumber) {
        System.out.println("Joueur " + playerNumber + " :");

        String name = SafeInput.readString("Nom : ");

        int type = SafeInput.readIntInRange("Type (1: Humain, 2: IA Simple, 3: IA Bluff) : ", 1, 3);

        switch (type) {
            case 1:
                return new HumanPlayer(name);
            case 2:
                return new AIPlayer(name, new Strategy1());
            case 3:
                return new AIPlayer(name, new Strategy2());
            default:
                throw new IllegalStateException("Type invalide");
        }
    }

    /**
     * Demande les regles a utiliser.
     * 
     * @return le RuleSet choisi
     */
    public RuleSet askRuleSet() {
        System.out.println("Choisissez les regles :");
        System.out.println("1. Regles de base");
        System.out.println("2. Variante : Trophees inverses");
        System.out.println("3. Variante : Couleurs inversees");

        int choice = SafeInput.readIntInRange("Votre choix : ", 1, 3);

        switch (choice) {
            case 2: return new Rule2();
            case 3: return new Rule3();
            default: return new Rule1();
        }
    }

    /**
     * Demande si les extensions doivent etre activees.
     * 
     * @return tableau [extensionBM, extensionCards]
     */
    public boolean[] askExtensions() {
        boolean[] extensions = new boolean[2];
        
        String response = SafeInput.readChoice("Voulez-vous des extensions ? (o/n) : ", "o", "n");
        
        if (response.equals("o")) {
            System.out.println("Extensions disponibles :");
            System.out.println("1. Cartes BM (Bonus/Malus)");
            System.out.println("2. Cartes supplementaires (1-8 au lieu de 1-4)");
            System.out.println("3. Les deux extensions");
            System.out.println("4. Aucune extension");
            
            int choice = SafeInput.readIntInRange("Votre choix : ", 1, 4);
            
            switch (choice) {
                case 1:
                    extensions[0] = true;
                    break;
                case 2:
                    extensions[1] = true;
                    break;
                case 3:
                    extensions[0] = true;
                    extensions[1] = true;
                    break;
            }
        }
        
        return extensions;
    }
    
    /**
     * Demande l extension simple (retrocompatibilite).
     * 
     * @return true si extension activee
     */
    public boolean askExtension() {
        String response = SafeInput.readChoice("Activer l extension cartes 5-8 ? (o/n) : ", "o", "n");
        return response.equals("o");
    }

    /**
     * Demande au joueur humain de creer son offre.
     * 
     * @param player le joueur humain
     * @return l offre creee
     */
    public Offer askHumanOffer(HumanPlayer player) {
        System.out.println(" " + player.getName() + ", votre main :");
        List<Card> hand = player.getHand();

        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i + 1) + ". " + hand.get(i));
        }

        int faceDownNumber = SafeInput.readIntInRange("Carte FACE CACHEE ? (1-2) : ", 1, 2);
        int faceDownIndex = faceDownNumber - 1;
        int faceUpIndex = (faceDownIndex == 0 ? 1 : 0);

        Offer offer = new Offer(player);
        offer.setFaceDown(hand.get(faceDownIndex));
        offer.setFaceUp(hand.get(faceUpIndex));

        return offer;
    }

    /**
     * Demande au joueur humain de choisir une carte.
     * 
     * @param player le joueur
     * @param offers les offres disponibles
     * @param game   la partie
     * @return la carte choisie
     */
    public Card askHumanTake(HumanPlayer player, List<Offer> offers, Game game) {
        System.out.println(" " + player.getName() + ", choisissez une carte :");

        if (offers == null || offers.isEmpty()) {
            System.out.println("Aucune offre disponible !");
            return null;
        }

        for (int i = 0; i < offers.size(); i++) {
            Offer offer = offers.get(i);
            System.out.println((i + 1) + ". Offre de " + offer.getOwner().getName());
            System.out.println("   - (v) Visible : " + offer.getFaceUp());
            System.out.println("   - (c) Cachee : X");
        }

        int offerIndex;
        if (offers.size() == 1) {
            offerIndex = 0;
        } else {
            offerIndex = SafeInput.readIntInRange("Quelle offre ? (1-" + offers.size() + ") : ", 1, offers.size()) - 1;
        }

        String choice = SafeInput.readChoice("Visible ou Cachee ? (v/c) : ", "v", "c");

        Offer chosenOffer = offers.get(offerIndex);
        Card chosen;
        Card result;

        if (choice.equalsIgnoreCase("v")) {
            chosen = chosenOffer.getFaceUp();
            Card unchosen = chosenOffer.getUnchosen(chosen);
            result = chosenOffer.takeFaceUp();
            game.addCarryOverCard(unchosen);
        } else {
            chosen = chosenOffer.getFaceDown();
            Card unchosen = chosenOffer.getUnchosen(chosen);
            result = chosenOffer.takeFaceDown();
            game.addCarryOverCard(unchosen);
        }

        return result;
    }

    /**
     * Demande au joueur de prendre dans sa propre offre.
     */
    public Card askTakeFromOwnOffer(Player player, Offer offer, Game game) {
        if (player.isHuman()) {
            System.out.println(player.getName() + ", vous devez prendre dans votre propre offre :");
            System.out.println("1. Visible : " + offer.getFaceUp());
            System.out.println("2. Cachee : X");

            int choice = SafeInput.readIntInRange("Choix (1-2) : ", 1, 2);

            Card chosen;
            Card unchosen;
            Card result;

            if (choice == 1) {
                chosen = offer.getFaceUp();
                unchosen = offer.getUnchosen(chosen);
                result = offer.takeFaceUp();
            } else {
                chosen = offer.getFaceDown();
                unchosen = offer.getUnchosen(chosen);
                result = offer.takeFaceDown();
            }

            game.addCarryOverCard(unchosen);
            return result;

        } else {
            boolean takeVisible = new Random().nextBoolean();
            Card chosen;
            Card unchosen;
            Card result;

            if (takeVisible) {
                chosen = offer.getFaceUp();
                unchosen = offer.getUnchosen(chosen);
                result = offer.takeFaceUp();
            } else {
                chosen = offer.getFaceDown();
                unchosen = offer.getUnchosen(chosen);
                result = offer.takeFaceDown();
            }

            game.addCarryOverCard(unchosen);
            return result;
        }
    }

    /**
     * Affiche qu une carte a ete prise.
     */
    public void displayCardTaken(Player player, Card card) {
        System.out.println("-> " + player.getName() + " a pris : " + card);
    }

    /**
     * Affiche la fin de partie.
     */
    public void displayGameEnd(Game game, Player winner) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("           FIN DE PARTIE");
        System.out.println("========================================");

        System.out.println("\\nRESULTATS :");

        for (Player player : game.getPlayers()) {
            Jest jest = player.getJest();

            List<Card> cartesNormales = new ArrayList<>();
            List<TrophyCard> trophees = new ArrayList<>();

            for (Card c : jest.getCards()) {
                if (c instanceof TrophyCard) {
                    trophees.add((TrophyCard) c);
                } else {
                    cartesNormales.add(c);
                }
            }

            System.out.println("\\n" + player.getName() + " :");
            System.out.println("  Cartes : " + cartesNormales);
            System.out.println("  Trophees : " + (trophees.isEmpty() ? "aucun" : trophees));
            System.out.println("  Score base : " + player.getBaseScore());
            System.out.println("  Bonus trophees : " + player.getTrophyBonus());
            System.out.println("  SCORE FINAL : " + player.getFinalScore());
        }

        System.out.println("\\n========================================");

        if (winner != null) {
            System.out.println("GAGNANT : " + winner.getName() + " avec " + winner.getFinalScore() + " points !");
        } else {
            System.out.println("Egalite parfaite !");
        }
    }

    /**
     * Affiche un message.
     */
    public void displayMessage(String message) {
        System.out.println(message);
    }

    // Implementation GameObserver
    @Override
    public void onRoundStart(int roundNumber) {
        System.out.println("\\n=== TOUR " + roundNumber + " ===");
    }

    @Override
    public void onOffersCreated(List<Offer> offers) {
        System.out.println("Offres creees.");
    }

    @Override
    public void onCardTaken(Player player, Card card) {
        displayCardTaken(player, card);
    }

    @Override
    public void onGameEnd(Player winner) {
        System.out.println("Partie terminee !");
    }

    @Override
    public void onMessage(String message) {
        displayMessage(message);
    }

    /**
     * Classe utilitaire pour les saisies securisees.
     */
    public static class SafeInput {
        private static Scanner scanner = new Scanner(System.in);

        public static int readIntInRange(String message, int min, int max) {
            while (true) {
                System.out.print(message);
                String input = scanner.nextLine().trim();
                try {
                    int value = Integer.parseInt(input);
                    if (value >= min && value <= max) {
                        return value;
                    }
                    System.out.println("Choix invalide (entre " + min + " et " + max + ").");
                } catch (NumberFormatException e) {
                    System.out.println("Saisie invalide.");
                }
            }
        }

        public static String readChoice(String message, String... validChoices) {
            while (true) {
                System.out.print(message);
                String input = scanner.nextLine().trim().toLowerCase();
                for (String v : validChoices) {
                    if (input.equals(v.toLowerCase())) {
                        return input;
                    }
                }
                System.out.print("Choix invalide (options : ");
                System.out.println(String.join(", ", validChoices) + ").");
            }
        }

        public static String readString(String message) {
            System.out.print(message);
            return scanner.nextLine().trim();
        }
    }
}'''

    # ==========================================================================
    #                              GUI VIEW
    # ==========================================================================
    
    files["Jest_Project/src/view/gui/JestGUI.java"] = '''package view.gui;

import model.*;
import observer.GameObserver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Interface graphique Swing pour le jeu Jest.
 * 
 * <p>Cette classe implemente une interface graphique basique utilisant
 * Swing. Elle fait partie de l architecture MVC comme l une des deux
 * vues concurrentes (avec ConsoleView).</p>
 * 
 * <h2>Architecture :</h2>
 * <ul>
 *   <li>JFrame principale avec plusieurs panneaux</li>
 *   <li>Panneau d information en haut</li>
 *   <li>Panneau des offres au centre</li>
 *   <li>Panneau des actions en bas</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 2.0
 */
public class JestGUI extends JFrame implements GameObserver {
    
    private static final long serialVersionUID = 1L;
    
    private JPanel mainPanel;
    private JPanel infoPanel;
    private JPanel offersPanel;
    private JPanel actionsPanel;
    private JTextArea logArea;
    private JLabel statusLabel;
    
    private Game game;
    private List<Offer> currentOffers;
    private Card selectedCard;
    private Object selectionLock = new Object();

    /**
     * Constructeur de l interface graphique.
     */
    public JestGUI() {
        super("Jest - Jeu de Cartes");
        initComponents();
        currentOffers = new ArrayList<>();
    }

    /**
     * Initialise les composants de l interface.
     */
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panneau d information
        infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Bienvenue dans Jest !");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(statusLabel);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Panneau des offres
        offersPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        offersPanel.setBorder(BorderFactory.createTitledBorder("Offres"));
        mainPanel.add(offersPanel, BorderLayout.CENTER);

        // Zone de log
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Historique"));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // Panneau d actions
        actionsPanel = new JPanel(new FlowLayout());
        mainPanel.add(actionsPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    /**
     * Definit la partie en cours.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Affiche les offres sur l interface.
     */
    public void displayOffers(List<Offer> offers) {
        this.currentOffers = offers;
        offersPanel.removeAll();

        for (Offer offer : offers) {
            JPanel offerPanel = createOfferPanel(offer);
            offersPanel.add(offerPanel);
        }

        offersPanel.revalidate();
        offersPanel.repaint();
    }

    /**
     * Cree un panneau pour une offre.
     */
    private JPanel createOfferPanel(Offer offer) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(offer.getOwner().getName()));

        JPanel cardsPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Carte visible
        JButton faceUpBtn = new JButton(offer.getFaceUp() != null ? offer.getFaceUp().toString() : "Vide");
        faceUpBtn.setBackground(Color.WHITE);
        if (offer.getFaceUp() != null) {
            final Card card = offer.getFaceUp();
            faceUpBtn.addActionListener(e -> selectCard(card, offer, true));
        }
        cardsPanel.add(faceUpBtn);

        // Carte cachee
        JButton faceDownBtn = new JButton("???");
        faceDownBtn.setBackground(Color.LIGHT_GRAY);
        if (offer.getFaceDown() != null) {
            final Card card = offer.getFaceDown();
            faceDownBtn.addActionListener(e -> selectCard(card, offer, false));
        }
        cardsPanel.add(faceDownBtn);

        panel.add(cardsPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Gere la selection d une carte.
     */
    private void selectCard(Card card, Offer offer, boolean faceUp) {
        synchronized (selectionLock) {
            this.selectedCard = card;
            log("Carte selectionnee : " + (faceUp ? card.toString() : "Carte cachee"));
            selectionLock.notifyAll();
        }
    }

    /**
     * Attend la selection d une carte par l utilisateur.
     */
    public Card waitForCardSelection() {
        synchronized (selectionLock) {
            selectedCard = null;
            try {
                while (selectedCard == null) {
                    selectionLock.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return selectedCard;
        }
    }

    /**
     * Ajoute un message au log.
     */
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    /**
     * Met a jour le status.
     */
    public void setStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }

    // Implementation GameObserver
    
    @Override
    public void onRoundStart(int roundNumber) {
        setStatus("Tour " + roundNumber);
        log("=== Debut du tour " + roundNumber + " ===");
    }

    @Override
    public void onOffersCreated(List<Offer> offers) {
        displayOffers(offers);
        log("Offres creees.");
    }

    @Override
    public void onCardTaken(Player player, Card card) {
        log(player.getName() + " a pris : " + card);
    }

    @Override
    public void onGameEnd(Player winner) {
        if (winner != null) {
            JOptionPane.showMessageDialog(this,
                "Gagnant : " + winner.getName() + " avec " + winner.getFinalScore() + " points !",
                "Fin de partie",
                JOptionPane.INFORMATION_MESSAGE);
        }
        log("=== FIN DE PARTIE ===");
    }

    @Override
    public void onMessage(String message) {
        log(message);
    }

    /**
     * Affiche l interface.
     */
    public void display() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}'''

    # ==========================================================================
    #                              GAME ENGINE
    # ==========================================================================
    
    files["Jest_Project/src/controller/GameEngine.java"] = '''package controller;

import model.*;
import view.ConsoleView;
import view.gui.JestGUI;
import observer.GameObserver;
import java.io.*;
import java.util.*;

/**
 * Controleur principal du jeu Jest (MVC).
 * 
 * <p>Cette classe orchestre le deroulement de la partie en coordonnant
 * le modele (Game) et les vues (ConsoleView, JestGUI).</p>
 * 
 * <h2>Responsabilites :</h2>
 * <ul>
 *   <li>Initialisation de la partie</li>
 *   <li>Gestion des tours de jeu</li>
 *   <li>Coordination des deux vues concurrentes</li>
 *   <li>Sauvegarde et chargement de parties</li>
 * </ul>
 * 
 * <h2>Architecture MVC :</h2>
 * <p>Le GameEngine notifie les deux vues (Console et GUI) simultanement
 * via le patron Observer. Les deux vues restent synchronisees.</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 */
public class GameEngine {

    private Game game;
    private ConsoleView consoleView;
    private JestGUI guiView;
    private List<Offer> currentOffers;
    private List<GameObserver> observers;
    private boolean useGui;

    /**
     * Constructeur du moteur de jeu.
     */
    public GameEngine() {
        this.consoleView = new ConsoleView();
        this.currentOffers = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.useGui = false;
        
        observers.add(consoleView);
    }

    /**
     * Active l interface graphique.
     */
    public void enableGUI() {
        this.guiView = new JestGUI();
        this.useGui = true;
        observers.add(guiView);
    }

    /**
     * Initialise une nouvelle partie.
     */
    public void initializeGame() {
        game = new Game();

        consoleView.displayWelcome();

        // Choix du mode d interface
        String guiChoice = ConsoleView.SafeInput.readChoice(
            "Activer l interface graphique ? (o/n) : ", "o", "n");
        if (guiChoice.equals("o")) {
            enableGUI();
            guiView.display();
        }

        int numPlayers = consoleView.askNumberOfPlayers();

        for (int i = 1; i <= numPlayers; i++) {
            Player player = consoleView.askPlayerType(i);
            game.addPlayer(player);
        }

        RuleSet ruleSet = consoleView.askRuleSet();
        game.setRuleSet(ruleSet);

        // Extensions
        boolean[] extensions = consoleView.askExtensions();
        game.setExtensionBM(extensions[0]);
        game.setExtensionCards(extensions[1]);
        
        game.getDeck().initialize(extensions[1]);

        game.start();
        game.displayAvailableTrophies();
        
        if (guiView != null) {
            guiView.setGame(game);
        }
    }

    /**
     * Lance la partie.
     */
    public void playGame() {
        while (!game.isGameOver()) {
            playRound();
        }
        endGame();
    }

    /**
     * Joue un tour.
     */
    private void playRound() {
        notifyRoundStart(game.getCurrentRound());
        
        game.dealRound();

        currentOffers.clear();
        for (Player player : game.getPlayers()) {
            Offer offer = createOffer(player);
            currentOffers.add(offer);
        }

        notifyOffersCreated(currentOffers);

        List<Player> turnOrder = determineTurnOrder();

        for (Player player : turnOrder) {
            Card chosenCard = playerChooseCard(player);
            player.getJest().addCard(chosenCard);
            notifyCardTaken(player, chosenCard);
        }

        for (Player player : game.getPlayers()) {
            player.clearHand();
        }

        game.endTurn();
    }

    /**
     * Cree l offre d un joueur.
     */
    private Offer createOffer(Player player) {
        if (player.isHuman()) {
            return consoleView.askHumanOffer((HumanPlayer) player);
        } else {
            return ((AIPlayer) player).makeOffer(game);
        }
    }

    /**
     * Gere le choix de carte d un joueur.
     */
    private Card playerChooseCard(Player player) {
        List<Offer> availableOffers = currentOffers.stream()
                .filter(Offer::isComplete)
                .filter(o -> o.getOwner() != player)
                .toList();

        if (availableOffers.isEmpty()) {
            Offer ownOffer = currentOffers.stream()
                    .filter(o -> o.getOwner() == player)
                    .findFirst()
                    .orElse(null);

            if (ownOffer != null && ownOffer.isComplete()) {
                return consoleView.askTakeFromOwnOffer(player, ownOffer, game);
            }
        }

        if (player.isHuman()) {
            return consoleView.askHumanTake((HumanPlayer) player, availableOffers, game);
        } else {
            return ((AIPlayer) player).chooseFrom(availableOffers, game);
        }
    }

    /**
     * Determine l ordre de jeu selon les cartes visibles.
     */
    private List<Player> determineTurnOrder() {
        List<Player> order = new ArrayList<>();
        List<Offer> remaining = new ArrayList<>(currentOffers);

        while (!remaining.isEmpty()) {
            Offer highest = remaining.stream()
                    .filter(Offer::isComplete)
                    .max(Comparator.comparingInt(this::getOfferPriority))
                    .orElse(remaining.get(0));

            order.add(highest.getOwner());
            remaining.remove(highest);
        }

        return order;
    }

    /**
     * Calcule la priorite d une offre.
     */
    private int getOfferPriority(Offer offer) {
        Card faceUp = offer.getFaceUp();
        int value = faceUp.getRank().getValue() * 10;

        switch (faceUp.getSuit()) {
            case SPADES: value += 4; break;
            case CLUBS: value += 3; break;
            case DIAMONDS: value += 2; break;
            case HEARTS: value += 1; break;
        }
        return value;
    }

    /**
     * Termine la partie.
     */
    private void endGame() {
        for (Player player : game.getPlayers()) {
            Offer offer = currentOffers.stream()
                    .filter(o -> o.getOwner() == player)
                    .findFirst()
                    .orElse(null);

            if (offer != null) {
                if (offer.getFaceUp() != null) player.getJest().addCard(offer.getFaceUp());
                if (offer.getFaceDown() != null) player.getJest().addCard(offer.getFaceDown());
            }
        }

        Player winner = game.winner();
        consoleView.displayGameEnd(game, winner);
        notifyGameEnd(winner);
    }

    // Notifications aux observateurs
    
    private void notifyRoundStart(int round) {
        for (GameObserver obs : observers) {
            obs.onRoundStart(round);
        }
    }

    private void notifyOffersCreated(List<Offer> offers) {
        for (GameObserver obs : observers) {
            obs.onOffersCreated(offers);
        }
    }

    private void notifyCardTaken(Player player, Card card) {
        for (GameObserver obs : observers) {
            obs.onCardTaken(player, card);
        }
    }

    private void notifyGameEnd(Player winner) {
        for (GameObserver obs : observers) {
            obs.onGameEnd(winner);
        }
    }

    /**
     * Sauvegarde la partie.
     */
    public void saveGame(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(game);
            consoleView.displayMessage("Partie sauvegardee !");
        } catch (IOException e) {
            consoleView.displayMessage("Erreur sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Charge une partie.
     */
    public void loadGame(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            game = (Game) ois.readObject();
            consoleView.displayMessage("Partie chargee !");
        } catch (IOException | ClassNotFoundException e) {
            consoleView.displayMessage("Erreur chargement : " + e.getMessage());
        }
    }

    /**
     * Point d entree de l application.
     */
    public static void main(String[] args) {
        GameEngine engine = new GameEngine();

        int choice = ConsoleView.SafeInput.readIntInRange(
            "1. Nouvelle partie\\n2. Charger une partie\\nVotre choix : ", 1, 2);

        if (choice == 2) {
            String filename = ConsoleView.SafeInput.readString("Nom du fichier : ");
            engine.loadGame(filename);
        } else {
            engine.initializeGame();
        }

        engine.playGame();

        String save = ConsoleView.SafeInput.readChoice("Sauvegarder ? (o/n) : ", "o", "n");

        if (save.equals("o")) {
            String filename = ConsoleView.SafeInput.readString("Nom du fichier : ");
            engine.saveGame(filename);
        }
    }
}'''

    # ==========================================================================
    #                           EXTENSION BM
    # ==========================================================================
    
    files["Jest_Project/src/model/extension/BMCard.java"] = '''package model.extension;

import model.*;

/**
 * Carte Bonus/Malus pour l extension BM.
 * 
 * <p>Les cartes BM ajoutent des effets speciaux au jeu :</p>
 * <ul>
 *   <li>Bonus : ajoute des points</li>
 *   <li>Malus : retire des points</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 2.0
 */
public class BMCard extends Card {
    
    private static final long serialVersionUID = 1L;
    
    /** Type de la carte (BONUS ou MALUS). */
    private BMType type;
    
    /** Valeur du bonus ou malus. */
    private int value;

    /**
     * Enumeration des types de cartes BM.
     */
    public enum BMType {
        BONUS, MALUS
    }

    /**
     * Constructeur d une carte BM.
     * 
     * @param type  le type (BONUS ou MALUS)
     * @param value la valeur
     */
    public BMCard(BMType type, int value) {
        super(Suits.JOKER, Rank.JOKER);
        this.type = type;
        this.value = value;
    }

    /**
     * Retourne le type de la carte.
     */
    public BMType getType() {
        return type;
    }

    /**
     * Retourne la valeur.
     */
    public int getValue() {
        return value;
    }

    /**
     * Applique l effet de la carte au score.
     */
    public int applyEffect(int currentScore) {
        if (type == BMType.BONUS) {
            return currentScore + value;
        } else {
            return currentScore - value;
        }
    }

    @Override
    public int acceptScore(ScoreVisitor visitor) {
        return type == BMType.BONUS ? value : -value;
    }

    @Override
    public boolean isJoker() {
        return false;
    }

    @Override
    public String toString() {
        return (type == BMType.BONUS ? "+" : "-") + value + " BM";
    }
}'''

    # ==========================================================================
    #                           COMPILE SCRIPTS
    # ==========================================================================
    
    files["Jest_Project/compile.sh"] = '''#!/bin/bash
# Script de compilation pour Linux/Mac

echo "Compilation du projet Jest..."

# Creer le dossier classes s il n existe pas
mkdir -p classes

# Compiler tous les fichiers Java
find src -name "*.java" > sources.txt
javac -d classes @sources.txt

if [ $? -eq 0 ]; then
    echo "Compilation reussie !"
    rm sources.txt
else
    echo "Erreur de compilation."
    rm sources.txt
    exit 1
fi'''

    files["Jest_Project/compile.bat"] = '''@echo off
REM Script de compilation pour Windows

echo Compilation du projet Jest...

REM Creer le dossier classes s il n existe pas
if not exist classes mkdir classes

REM Compiler tous les fichiers Java
dir /s /B src\\*.java > sources.txt
javac -d classes @sources.txt

if %ERRORLEVEL% EQU 0 (
    echo Compilation reussie !
    del sources.txt
) else (
    echo Erreur de compilation.
    del sources.txt
    exit /b 1
)'''

    files["Jest_Project/run.sh"] = '''#!/bin/bash
# Script d execution pour Linux/Mac

echo "Lancement du jeu Jest..."
java -cp classes controller.GameEngine'''

    files["Jest_Project/run.bat"] = '''@echo off
REM Script d execution pour Windows

echo Lancement du jeu Jest...
java -cp classes controller.GameEngine'''

    # ==========================================================================
    #                              README
    # ==========================================================================
    
    files["Jest_Project/README.md"] = '''# Projet Jest - LO02 UTT

## Description

Implementation du jeu de cartes **Jest** en Java avec :

- Architecture **MVC** (Modele-Vue-Controleur)
- **Deux vues concurrentes** : Console et Interface Graphique
- **Patron Strategy** pour les IA
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

## Structure du projet

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
│   ├── strategy/        # Strategies IA
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

## Auteurs

Projet LO02 - UTT - 2025
'''

    # ==========================================================================
    #                         FIN DU DICTIONNAIRE FILES
    # ==========================================================================
    
    # Retourner le dictionnaire
    return files


def write_files(files):
    """
    Ecrit tous les fichiers sur le disque.
    
    Args:
        files: dictionnaire {chemin: contenu}
    """
    total_files = len(files)
    for i, (file_path, content) in enumerate(files.items(), 1):
        os.makedirs(os.path.dirname(file_path), exist_ok=True)
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"[{i}/{total_files}] {file_path}")


def make_scripts_executable():
    """
    Rend les scripts shell executables sur Linux/Mac.
    """
    try:
        os.chmod("Jest_Project/compile.sh", 
                 stat.S_IRWXU | stat.S_IRGRP | stat.S_IXGRP | stat.S_IROTH | stat.S_IXOTH)
        os.chmod("Jest_Project/run.sh", 
                 stat.S_IRWXU | stat.S_IRGRP | stat.S_IXGRP | stat.S_IROTH | stat.S_IXOTH)
    except FileNotFoundError:
        pass


def main():
    """
    Fonction principale.
    """
    print("=" * 60)
    print("   GENERATEUR DE PROJET JEST - VERSION FINALE")
    print("=" * 60)
    print()
    
    # Creer la structure et obtenir les fichiers
    files = create_project_structure()
    
    # Ecrire les fichiers
    print("\nCreation des fichiers...")
    write_files(files)
    
    # Rendre les scripts executables
    make_scripts_executable()
    
    print()
    print("=" * 60)
    print("   PROJET JEST CREE AVEC SUCCES !")
    print("=" * 60)
    print()
    print("Pour compiler et executer :")
    print("  Linux/Mac : ./compile.sh && ./run.sh")
    print("  Windows   : compile.bat && run.bat")
    print()
    print("Fonctionnalites :")
    print("  - Gestion correcte des trophees (regles officielles)")
    print("  - Documentation Javadoc complete")
    print("  - Extension Cartes BM")
    print("  - Extension Cartes 5-8")
    print("  - Interface graphique MVC")
    print("  - Deux vues concurrentes (Console + GUI)")


if __name__ == "__main__":
    main()
