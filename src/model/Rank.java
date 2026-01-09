package model;

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
}