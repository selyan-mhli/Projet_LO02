package model;

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
}