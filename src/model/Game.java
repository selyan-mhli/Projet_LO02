package model;

import model.extension.BMCard;
import model.trophy.TrophyFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe principale representant une partie de Jest.
 * 
 * <p>Cette classe gere l ensemble de la logique metier d une partie :</p>
 * <ul>
 *   <li>Gestion des joueurs et du deck</li>
 *   <li>Distribution des cartes et des trophees</li>
 *   <li>Deroulement des tours</li>
 *   <li>Calcul des scores et determination du gagnant</li>
 * </ul>
 * 
 * <h2>Cycle de vie d une partie :</h2>
 * <ol>
 *   <li>Creation de la partie (new Game())</li>
 *   <li>Ajout des joueurs (addPlayer)</li>
 *   <li>Configuration des regles (setRuleSet)</li>
 *   <li>Initialisation du deck</li>
 *   <li>Demarrage (start) - tire les trophees</li>
 *   <li>Tours de jeu (dealRound, endTurn)</li>
 *   <li>Fin de partie (winner)</li>
 * </ol>
 * 
 * <h2>Gestion des trophees selon les regles officielles :</h2>
 * <p>Les trophees sont tires au debut de la partie. La carte tiree
 * determine le type de trophee selon la bande orange sur la carte.</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Player
 * @see Deck
 * @see TrophyCard
 */
public class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Liste des joueurs de la partie. */
    private List<Player> players;
    
    /** Deck de cartes. */
    private Deck deck;
    
    /** Regles utilisees pour cette partie. */
    private RuleSet ruleSet;
    
    /** Numero du tour actuel. */
    private int currentRound;
    
    /** Cartes trophees de la partie. */
    private List<TrophyCard> trophies;
    
    /** Cartes non choisies pour le tour suivant. */
    private List<Card> carryOverCards;
    
    /** Extension BM activee. */
    private boolean extensionBM;
    
    /** Extension cartes supplementaires activee. */
    private boolean extensionCards;

    /** Carte BM en attente d application a la fin de partie. */
    private transient BMCard pendingBmCard;

    /** Dernier joueur ayant recu une carte BM. */
    private transient Player lastBmTarget;

    /** Carte BM appliquee lors du dernier calcul de score. */
    private transient BMCard lastAppliedBmCard;

    /** Variation de score causee par la derniere carte BM. */
    private transient int lastBmDelta;

    /**
     * Constructeur d une nouvelle partie.
     */
    public Game() {
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.trophies = new ArrayList<>();
        this.currentRound = 0;
        this.carryOverCards = new ArrayList<>();
        this.extensionBM = false;
        this.extensionCards = false;
        this.pendingBmCard = null;
        this.lastBmTarget = null;
        this.lastAppliedBmCard = null;
        this.lastBmDelta = 0;
    }

    /**
     * Demarre la partie.
     * <p>Melange le deck et tire les trophees.</p>
     */
    public void start() {
        deck.shuffle();
        dealInitialTrophies();
        currentRound = 1;
    }

    /**
     * Tire les cartes trophees du debut de partie.
     */
    private void dealInitialTrophies() {
        int numTrophies = ruleSet != null
                ? ruleSet.numberOfTrophies(players.size())
                : (players.size() == 4 ? 1 : 2);

        trophies.clear();

        for (int i = 0; i < numTrophies; i++) {
            Card card = deck.draw();
            if (card == null) continue;

            TrophyCard trophy = TrophyFactory.createFromCard(card);
            trophies.add(trophy);
        }
    }

    /**
     * Distribue les cartes pour un tour.
     */
    public void dealRound() {
        if (players.isEmpty()) return;

        int needed = players.size() * 2;

        List<Card> pool = new ArrayList<>(carryOverCards);
        carryOverCards.clear();

        int missing = needed - pool.size();
        for (int i = 0; i < missing && !deck.isEmpty(); i++) {
            pool.add(deck.draw());
        }

        if (pool.size() < needed) return;

        Collections.shuffle(pool);

        for (Player player : players) {
            List<Card> hand = new ArrayList<>();
            hand.add(pool.remove(0));
            hand.add(pool.remove(0));
            player.receiveCards(hand);
        }
    }

    /**
     * Ajoute une carte au pool pour le prochain tour.
     * 
     * @param card la carte non choisie
     */
    public void addCarryOverCard(Card card) {
        if (card != null) carryOverCards.add(card);
    }

    /**
     * Verifie si la partie est terminee.
     * 
     * @return true si plus assez de cartes pour un tour
     */
    public boolean isGameOver() {
        int needed = players.size() * 2;
        int available = deck.size() + carryOverCards.size();
        return available < needed;
    }

    /**
     * Calcule les scores et determine le gagnant.
     * 
     * @return le joueur gagnant
     */
    public Player winner() {
        lastBmTarget = null;
        lastAppliedBmCard = null;
        lastBmDelta = 0;

        // 1) Calcul du score de base
        ScoreVisitor visitor = ruleSet.scoreVisitor();
        for (Player p : players) {
            int base = visitor.score(p.getJest());
            p.setBaseScore(base);
        }

        // 2) Attribution des trophees
        for (TrophyCard trophy : trophies) {
            Player winner = determineTrophyWinner(trophy);
            if (winner != null) {
                winner.getJest().addTrophy(trophy);
                trophy.applyTo(winner, this);
            }
        }

        if (extensionBM) {
            applyPendingBmCard();
        }

        // 3) Determination du gagnant
        return players.stream()
                .max(Comparator.comparingInt(Player::getFinalScore))
                .orElse(null);
    }

    /**
     * Determine le gagnant d un trophee.
     */
    private Player determineTrophyWinner(TrophyCard trophy) {
        List<Player> candidates = players.stream()
                .filter(p -> trophy.isWonBy(p, this))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) return null;
        return candidates.get(0);
    }

    /**
     * Applique la carte BM au joueur choisi par le joueur avec le plus petit Jest.
     */
    private void applyPendingBmCard() {
        if (pendingBmCard == null) {
            return;
        }

        // Utilise le target défini dans la carte BM
        Player target = pendingBmCard.getTarget();

        if (target == null) {
            pendingBmCard = null;
            return;
        }

        int before = target.getFinalScore();
        int after = pendingBmCard.applyEffect(before);
        int delta = after - before;

        if (delta != 0) {
            target.addTrophyBonus(delta);
        }

        lastBmTarget = target;
        lastAppliedBmCard = pendingBmCard;
        lastBmDelta = delta;
        pendingBmCard = null;
    }

    /**
     * Affiche les trophees disponibles.
     */
    public void displayAvailableTrophies() {
        System.out.println();
        System.out.println("==== Trophées pour cette partie ====");

        if (trophies == null || trophies.isEmpty()) {
            System.out.println("  Aucun trophée pour ces règles.");
            return;
        }

        for (TrophyCard trophy : trophies) {
            System.out.println("  Trophée " + trophy.getName());
        }
    }

    // Getters et setters
    public List<Player> getPlayers() { return players; }
    public void addPlayer(Player player) { players.add(player); }
    public Deck getDeck() { return deck; }
    public int getCurrentRound() { return currentRound; }
    public void endTurn() { currentRound++; }
    public RuleSet getRuleSet() { return ruleSet; }
    public void setRuleSet(RuleSet ruleSet) { this.ruleSet = ruleSet; }
    public List<TrophyCard> getTrophies() { return trophies; }
    
    public void setExtensionBM(boolean enabled) { this.extensionBM = enabled; }
    public boolean isExtensionBM() { return extensionBM; }
    public void setExtensionCards(boolean enabled) { this.extensionCards = enabled; }
    public boolean isExtensionCards() { return extensionCards; }

    public void setPendingBmCard(BMCard card) { this.pendingBmCard = card; }
    public Player getLastBmTarget() { return lastBmTarget; }
    public BMCard getLastAppliedBmCard() { return lastAppliedBmCard; }
    public int getLastBmDelta() { return lastBmDelta; }
}
