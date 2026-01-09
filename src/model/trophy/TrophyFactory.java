package model.trophy;

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
        TrophyCondition condition = (player, game) -> {
            // Vérifier si le joueur possède le Joker
            if (!player.getJest().hasJoker()) {
                return false;
            }
            // Vérifier qu'aucun autre joueur ne possède le Joker
            for (Player other : game.getPlayers()) {
                if (other != player && other.getJest().hasJoker()) {
                    return false;
                }
            }
            return true;
        };
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
            TrophyCard trophy = createJokerTrophy();
            trophy.setOriginalCard(card);
            return trophy;
        }
        
        SuitCard suitCard = (SuitCard) card;
        Suits suit = suitCard.getSuit();
        Rank rank = suitCard.getRank();
        
        TrophyCard trophy;
        
        // Selon les images des cartes :
        // Les trophees sont indiques par la bande orange
        switch (rank) {
            case ACE:
                trophy = createHighestTrophy(suit);
                break;
            case TWO:
                trophy = createLowestTrophy(suit);
                break;
            case THREE:
                trophy = createMajorityTrophy(suit);
                break;
            case FOUR:
                trophy = createBestJestTrophy();
                break;
            default:
                trophy = createMajorityTrophy(suit);
                break;
        }
        
        trophy.setOriginalCard(card);
        return trophy;
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
}