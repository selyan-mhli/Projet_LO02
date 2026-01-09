package strategy;

import model.*;

/**
 * Classe abstraite de base pour toutes les strategies d intelligence artificielle.
 * 
 * <p>Cette classe fournit une implementation commune pour les strategies IA
 * et definit la methode {@link #estimateValue(Card)} utilisee par les sous-classes
 * pour evaluer la valeur d une carte.</p>
 * 
 * <h2>Strategies IA disponibles :</h2>
 * <ul>
 *   <li>{@link Strategy1} - Strategie conservatrice (privilegie les cartes visibles)</li>
 *   <li>{@link Strategy2} - Strategie bluff (privilegie les cartes cachees)</li>
 * </ul>
 * 
 * <h2>Evaluation des cartes :</h2>
 * <p>La methode {@link #estimateValue(Card)} evalue une carte selon :</p>
 * <ul>
 *   <li>Joker : valeur 2</li>
 *   <li>Piques/Trefles : valeur positive (valeur faciale)</li>
 *   <li>Carreaux : valeur negative (penalite)</li>
 *   <li>Coeurs : valeur 1 (neutre)</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 2.0
 * @see Strategy
 * @see Strategy1
 * @see Strategy2
 */
public abstract class AIStrategy implements Strategy {
    
    private static final long serialVersionUID = 1L;

    /**
     * Indique si cette strategie est pour un joueur humain.
     * 
     * @return toujours false pour les strategies IA
     */
    @Override
    public boolean isHuman() {
        return false;
    }

    /**
     * Estime la valeur d une carte pour la prise de decision.
     * 
     * <p>Cette methode est utilisee par les strategies IA pour evaluer
     * l interet d une carte. Les valeurs positives sont souhaitables,
     * les valeurs negatives sont a eviter.</p>
     * 
     * @param card la carte a evaluer
     * @return la valeur estimee de la carte
     */
    protected int estimateValue(Card card) {
        if (card instanceof JokerCard) {
            return 2;
        }

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
}
