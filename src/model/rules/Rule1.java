package model.rules;

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
}