package model;

/**
 * Classe representant la carte Joker unique du jeu Jest.
 * 
 * <p>Le Joker est une carte speciale avec des regles de scoring complexes
 * dependant du nombre de Coeurs dans le Jest du joueur.</p>
 * 
 * <h2>Regles de scoring du Joker :</h2>
 * <table border="1">
 *   <tr><th>Coeurs dans le Jest</th><th>Effet du Joker</th></tr>
 *   <tr><td>0 Coeur</td><td>+4 points bonus</td></tr>
 *   <tr><td>1, 2 ou 3 Coeurs</td><td>Le Joker ne vaut rien, chaque Coeur reduit le Jest</td></tr>
 *   <tr><td>4 Coeurs</td><td>Chaque Coeur augmente le Jest de sa valeur faciale</td></tr>
 * </table>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see Card
 * @see model.rules.Rule1
 */
public class JokerCard extends Card {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur du Joker.
     * 
     * <p>Le Joker est unique et possede la couleur et le rang speciaux JOKER.</p>
     */
    public JokerCard() {
        super(Suits.JOKER, Rank.JOKER);
    }

    /**
     * Accepte un visiteur pour le calcul du score.
     * 
     * @param visitor le visiteur calculant le score
     * @return 0 (le score reel est calcule au niveau du Jest)
     */
    @Override
    public int acceptScore(ScoreVisitor visitor) {
        return visitor.score(this);
    }

    /**
     * Retourne une representation textuelle du Joker.
     * 
     * @return "Joker ★"
     */
    @Override
    public String toString() { 
        return "Joker ★"; 
    }
}