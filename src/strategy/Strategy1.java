package strategy;

import model.*;
import java.util.Comparator;
import java.util.List;

/**
 * Strategie IA conservatrice : privilegie les cartes visibles.
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
 * @see AIStrategy
 */
public class Strategy1 extends AIStrategy {
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
}