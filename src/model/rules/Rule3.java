package model.rules;

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
}