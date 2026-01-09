package model.extension;

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
    
    /** Joueur destinataire de la carte BM. */
    private Player target;

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
     * Definit le joueur destinataire de la carte BM.
     */
    public void setTarget(Player target) {
        this.target = target;
    }
    
    /**
     * Retourne le joueur destinataire de la carte BM.
     */
    public Player getTarget() {
        return target;
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
}