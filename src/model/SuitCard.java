package model;

/**
 * Classe representant une carte classique (non Joker) du jeu Jest.
 * 
 * <p>Une SuitCard est une carte normale avec une couleur (Piques, Trefles,
 * Carreaux, Coeurs) et un rang (As, 2, 3, 4, ou jusqu a 8 avec l extension).</p>
 * 
 * <h2>Calcul du score :</h2>
 * <ul>
 *   <li>Piques/Trefles : +valeur du rang</li>
 *   <li>Carreaux : -valeur du rang</li>
 *   <li>Coeurs : 0 (sauf regles speciales avec Joker)</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Card
 * @see Suits
 * @see Rank
 */
public class SuitCard extends Card {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur d une carte classique.
     * 
     * @param suit la couleur de la carte (ne doit pas etre JOKER)
     * @param rank le rang de la carte (ne doit pas etre JOKER)
     * @throws IllegalArgumentException si suit est JOKER ou rank est JOKER
     */
    public SuitCard(Suits suit, Rank rank) {
        super(suit, rank);
        if (suit == Suits.JOKER || rank == Rank.JOKER) {
            throw new IllegalArgumentException("Une SuitCard ne peut pas etre un Joker");
        }
    }

    /**
     * Accepte un visiteur pour le calcul du score.
     * 
     * @param visitor le visiteur calculant le score
     * @return le score de cette carte selon les regles du visiteur
     */
    @Override
    public int acceptScore(ScoreVisitor visitor) {
        return visitor.score(this);
    }

    /**
     * Retourne une representation compacte de la carte.
     * 
     * @return le rang suivi du symbole de la couleur (ex: "As ♠")
     */
    @Override
    public String toString() {
        return getRank().getDisplayName() + " " + getSuit().getSymbol();
    }
}