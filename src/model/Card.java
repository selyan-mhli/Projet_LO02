package model;

import java.io.Serializable;

/**
 * Classe abstraite representant une carte du jeu Jest.
 * 
 * <p>Cette classe est la classe mere de toutes les cartes du jeu.
 * Elle definit les proprietes communes (couleur, rang) et le comportement
 * polymorphique via le patron Visitor pour le calcul des scores.</p>
 * 
 * <h2>Hierarchie des cartes :</h2>
 * <pre>
 * Card (abstraite)
 * +-- SuitCard (cartes normales : As, 2, 3, 4, ... 8)
 * +-- JokerCard (carte Joker unique)
 * +-- TrophyCard (cartes trophees)
 * </pre>
 * 
 * <h2>Patron Visitor :</h2>
 * <p>Le calcul du score utilise le patron Visitor via la methode
 * {@link #acceptScore(ScoreVisitor)}. Cela permet de modifier le calcul
 * du score sans modifier les classes de cartes.</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see SuitCard
 * @see JokerCard
 * @see TrophyCard
 * @see ScoreVisitor
 */
public abstract class Card implements Serializable {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /** Couleur de la carte. Pour le Joker, suit = Suits.JOKER */
    protected Suits suit;
    
    /** Rang de la carte. Pour le Joker, rank = Rank.JOKER */
    protected Rank rank;
    
    /** Joueur proprietaire de l offre d ou provient la carte prise. */
    private transient Player lastOfferOwner;
    
    /** Indique si la carte prise provenait de la partie cachee de l offre. */
    private transient boolean lastTakenHidden;

    /**
     * Constructeur de la classe Card.
     * 
     * @param suit la couleur de la carte
     * @param rank le rang de la carte
     * @throws IllegalArgumentException si suit ou rank est null
     */
    public Card(Suits suit, Rank rank) {
        if (suit == null || rank == null) {
            throw new IllegalArgumentException("La couleur et le rang ne peuvent pas etre null");
        }
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Retourne la couleur de la carte.
     * 
     * @return la couleur (Suits) de la carte
     */
    public Suits getSuit() { 
        return suit; 
    }
    
    /**
     * Retourne le rang de la carte.
     * 
     * @return le rang (Rank) de la carte
     */
    public Rank getRank() { 
        return rank; 
    }
    
    /**
     * Marque la derniere offre dont provient cette carte.
     * 
     * @param owner  proprietaire de l offre
     * @param hidden true si la carte etait cachee
     */
    public void markTakenFromOffer(Player owner, boolean hidden) {
        this.lastOfferOwner = owner;
        this.lastTakenHidden = hidden;
    }

    /**
     * Retourne le proprietaire de l offre dont provient la carte.
     * 
     * @return joueur proprietaire, ou null si inconnu
     */
    public Player getLastOfferOwner() {
        return lastOfferOwner;
    }

    /**
     * Indique si la derniere prise provenait de la carte cachee.
     * 
     * @return true si cachee, false sinon
     */
    public boolean wasTakenHidden() {
        return lastOfferOwner != null && lastTakenHidden;
    }

    /**
     * Verifie si cette carte est le Joker.
     * 
     * @return true si la carte est le Joker, false sinon
     */
    public boolean isJoker() {
        return rank == Rank.JOKER;
    }

    /**
     * Accepte un visiteur pour le calcul du score.
     * 
     * <p>Cette methode implemente le patron Visitor.
     * Chaque sous-classe doit appeler la methode appropriee du visiteur.</p>
     * 
     * @param visitor le visiteur calculant le score
     * @return le score de cette carte selon les regles du visiteur
     */
    public abstract int acceptScore(ScoreVisitor visitor);

    /**
     * Retourne une representation textuelle de la carte.
     * 
     * @return une chaine decrivant la carte (ex: "As de Piques")
     */
    @Override
    public String toString() {
        if (isJoker()) {
            return "Joker ★";
        }
        return rank.getDisplayName() + " de " + suit.getName();
    }
}
