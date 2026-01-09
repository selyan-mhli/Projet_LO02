package model;

import strategy.Strategy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe representant un joueur du jeu Jest.
 * 
 * <p>Cette classe utilise le patron Strategy pour definir le comportement
 * du joueur. Tous les joueurs (humains et IA) utilisent une strategie :</p>
 * 
 * <h2>Strategies disponibles :</h2>
 * <ul>
 *   <li>{@link strategy.HumanStrategy} - Joueur humain (delegue a la vue)</li>
 *   <li>{@link strategy.Strategy1} - IA conservatrice</li>
 *   <li>{@link strategy.Strategy2} - IA bluff</li>
 * </ul>
 * 
 * <h2>Gestion du score :</h2>
 * <p>Le score final d un joueur est compose de :</p>
 * <ul>
 *   <li>baseScore : score calcule sur les cartes du Jest</li>
 *   <li>trophyBonus : bonus des trophees gagnes</li>
 *   <li>finalScore = baseScore + trophyBonus</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Strategy
 * @see Jest
 */
public class Player implements Serializable {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /** Nom du joueur. */
    private String name;
    
    /** Jest du joueur (cartes collectees). */
    private Jest jest;
    
    /** Strategie du joueur (humain ou IA). */
    private Strategy strategy;
    
    /** Main courante du joueur. */
    private List<Card> hand;
    
    /** Score de base calcule sur les cartes. */
    private int baseScore;
    
    /** Bonus provenant des trophees. */
    private int trophyBonus;

    /**
     * Constructeur d un joueur.
     * 
     * @param name     le nom du joueur
     * @param strategy la strategie du joueur
     */
    public Player(String name, Strategy strategy) {
        this.name = name;
        this.strategy = strategy;
        this.jest = new Jest();
        this.hand = new ArrayList<>();
        this.baseScore = 0;
        this.trophyBonus = 0;
    }

    /**
     * Recoit des cartes dans la main.
     * 
     * @param cards les cartes a recevoir
     */
    public void receiveCards(List<Card> cards) {
        hand.addAll(cards);
    }

    /**
     * Cree une offre pour ce tour en utilisant la strategie.
     * 
     * @param game la partie en cours
     * @return l offre creee (ou null si geree par la vue)
     */
    public Offer makeOffer(Game game) {
        return strategy.chooseOffer(this, hand, game);
    }

    /**
     * Choisit une carte parmi les offres disponibles en utilisant la strategie.
     * 
     * @param offers les offres disponibles
     * @param game   la partie en cours
     * @return la carte choisie (ou null si geree par la vue)
     */
    public Card chooseFrom(List<Offer> offers, Game game) {
        return strategy.chooseTake(this, offers, game);
    }

    /**
     * Indique si ce joueur est un humain.
     * 
     * @return true si humain, false si IA
     */
    public boolean isHuman() {
        return strategy.isHuman();
    }
    
    /**
     * Retourne la strategie du joueur.
     * 
     * @return la strategie
     */
    public Strategy getStrategy() {
        return strategy;
    }

    /**
     * Retourne le Jest du joueur.
     * 
     * @return le Jest
     */
    public Jest getJest() { 
        return jest; 
    }
    
    /**
     * Retourne le nom du joueur.
     * 
     * @return le nom
     */
    public String getName() { 
        return name; 
    }
    
    /**
     * Retourne la main courante.
     * 
     * @return la liste des cartes en main
     */
    public List<Card> getHand() { 
        return hand; 
    }

    /**
     * Vide la main du joueur.
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Retourne le score de base.
     * 
     * @return le score de base
     */
    public int getBaseScore() {
        return baseScore;
    }

    /**
     * Definit le score de base.
     * 
     * @param baseScore le nouveau score de base
     */
    public void setBaseScore(int baseScore) {
        this.baseScore = baseScore;
    }

    /**
     * Retourne le bonus des trophees.
     * 
     * @return le bonus
     */
    public int getTrophyBonus() {
        return trophyBonus;
    }

    /**
     * Ajoute un bonus de trophee.
     * 
     * @param bonus le bonus a ajouter
     */
    public void addTrophyBonus(int bonus) {
        this.trophyBonus += bonus;
    }

    /**
     * Calcule et retourne le score final.
     * 
     * @return baseScore + trophyBonus
     */
    public int getFinalScore() {
        return baseScore + trophyBonus;
    }

    /**
     * Retourne une representation textuelle du joueur.
     * 
     * @return nom et scores du joueur
     */
    @Override
    public String toString() {
        return name + " (base=" + baseScore + ", trophees=" + trophyBonus + ", total=" + getFinalScore() + ")";
    }
}