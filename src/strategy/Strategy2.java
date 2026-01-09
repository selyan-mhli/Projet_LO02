package strategy;

import model.*;
import java.util.List;
import java.util.Random;

/**
 * Strategie IA bluff : privilegie les cartes cachees.
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
 * @see AIStrategy
 */
public class Strategy2 extends AIStrategy {
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
}