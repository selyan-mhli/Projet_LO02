package model;

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
        if (c != null) {
            c.markTakenFromOffer(owner, false);
        }
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
        if (c != null) {
            c.markTakenFromOffer(owner, true);
        }
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
}
