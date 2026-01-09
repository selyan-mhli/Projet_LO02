package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe representant le paquet de cartes du jeu Jest.
 * 
 * <p>Le deck contient les cartes du jeu et gere leur distribution.
 * La composition du deck depend des extensions activees.</p>
 * 
 * <h2>Composition standard (17 cartes) :</h2>
 * <ul>
 *   <li>16 cartes de couleur (4 couleurs x 4 rangs)</li>
 *   <li>1 Joker</li>
 * </ul>
 * 
 * <h2>Avec extension Cartes supplementaires (33 cartes) :</h2>
 * <ul>
 *   <li>32 cartes de couleur (4 couleurs x 8 rangs)</li>
 *   <li>1 Joker</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Card
 * @see Game
 */
public class Deck implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private List<Card> cards;
    private boolean extendedDeck;

    /**
     * Constructeur du deck.
     */
    public Deck() {
        this.cards = new ArrayList<>();
        this.extendedDeck = false;
    }

    /**
     * Initialise le deck avec les cartes appropriees.
     * 
     * @param includeExtension true pour inclure les cartes 5-8
     */
    public void initialize(boolean includeExtension) {
        cards.clear();
        this.extendedDeck = includeExtension;

        Rank[] ranks;
        if (includeExtension) {
            ranks = new Rank[]{Rank.ACE, Rank.TWO, Rank.THREE, Rank.FOUR, 
                              Rank.FIVE, Rank.SIX, Rank.SEVEN, Rank.EIGHT};
        } else {
            ranks = new Rank[]{Rank.ACE, Rank.TWO, Rank.THREE, Rank.FOUR};
        }

        for (Suits suit : new Suits[]{Suits.SPADES, Suits.CLUBS, Suits.DIAMONDS, Suits.HEARTS}) {
            for (Rank rank : ranks) {
                cards.add(new SuitCard(suit, rank));
            }
        }

        cards.add(new JokerCard());
    }

    /**
     * Melange le deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Pioche la premiere carte du deck.
     * 
     * @return la carte piochee, ou null si le deck est vide
     */
    public Card draw() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    public void addCards(List<Card> cardsToAdd) {
        for (Card c : cardsToAdd) {
            if (c != null) cards.add(c);
        }
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
    
    public boolean isExtended() {
        return extendedDeck;
    }
}