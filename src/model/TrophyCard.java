package model;

import java.io.Serializable;

/**
 * Classe representant une carte trophee du jeu Jest.
 * 
 * <p>Les trophees sont des cartes speciales tirees au debut de la partie
 * et attribuees aux joueurs remplissant certaines conditions en fin de partie.</p>
 * 
 * <h2>Types de trophees (selon les regles officielles) :</h2>
 * 
 * <h3>Trophees de valeur :</h3>
 * <ul>
 *   <li><b>Highest [Couleur]</b> : Joueur avec la plus haute valeur dans une couleur</li>
 *   <li><b>Lowest [Couleur]</b> : Joueur avec la plus basse valeur dans une couleur</li>
 * </ul>
 * 
 * <h3>Trophees de majorite :</h3>
 * <ul>
 *   <li><b>Majority</b> : Joueur avec le plus de cartes d une couleur donnee</li>
 * </ul>
 * 
 * <h3>Trophees speciaux :</h3>
 * <ul>
 *   <li><b>Joker</b> : Joueur possedant le Joker</li>
 *   <li><b>Best Jest</b> : Joueur avec le meilleur score Jest</li>
 *   <li><b>Best Jest, No Joke</b> : Meilleur Jest parmi ceux sans Joker</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see TrophyCondition
 * @see TrophyEffect
 */
public class TrophyCard extends Card implements Serializable {
    
    /** Numero de version pour la serialisation. */
    private static final long serialVersionUID = 1L;

    /** Nom du trophee pour l affichage. */
    private String name;
    
    /** Condition d obtention du trophee. */
    private TrophyCondition condition;
    
    /** Effet applique au gagnant du trophee. */
    private TrophyEffect effect;
    
    /** Carte originale ayant determine le trophee (optionnel). */
    private Card originalCard;

    /**
     * Constructeur d une carte trophee.
     * 
     * @param name      le nom du trophee
     * @param condition la condition d obtention
     * @param effect    l effet applique au gagnant
     */
    public TrophyCard(String name, TrophyCondition condition, TrophyEffect effect) {
        super(Suits.JOKER, Rank.JOKER);
        this.name = name;
        this.condition = condition;
        this.effect = effect;
        this.originalCard = null;
    }
    
    /**
     * Constructeur d une carte trophee avec carte originale.
     * 
     * @param name         le nom du trophee
     * @param condition    la condition d obtention
     * @param effect       l effet applique au gagnant
     * @param originalCard la carte originale ayant determine le trophee
     */
    public TrophyCard(String name, TrophyCondition condition, TrophyEffect effect, Card originalCard) {
        super(Suits.JOKER, Rank.JOKER);
        this.name = name;
        this.condition = condition;
        this.effect = effect;
        this.originalCard = originalCard;
    }

    /**
     * Verifie si un joueur remplit la condition pour gagner ce trophee.
     * 
     * @param player le joueur a tester
     * @param game   la partie en cours
     * @return true si le joueur remplit la condition
     */
    public boolean isWonBy(Player player, Game game) {
        return condition.test(player, game);
    }

    /**
     * Applique l effet du trophee au joueur gagnant.
     * 
     * @param player le joueur gagnant le trophee
     * @param game   la partie en cours
     */
    public void applyTo(Player player, Game game) {
        effect.apply(player, game);
    }

    /**
     * Retourne le nom du trophee.
     * 
     * @return le nom du trophee
     */
    public String getName() { 
        return name; 
    }
    
    /**
     * Retourne la carte originale ayant determine le trophee.
     * 
     * @return la carte originale, ou null si non applicable
     */
    public Card getOriginalCard() {
        return originalCard;
    }
    
    /**
     * Definit la carte originale ayant determine le trophee.
     * 
     * @param card la carte originale
     */
    public void setOriginalCard(Card card) {
        this.originalCard = card;
    }

    /**
     * Un trophee n a pas de score propre.
     * 
     * @param visitor le visiteur (ignore)
     * @return toujours 0
     */
    @Override
    public int acceptScore(ScoreVisitor visitor) {
        return 0;
    }

    /**
     * Un trophee n est jamais considere comme un Joker.
     * 
     * @return toujours false
     */
    @Override
    public boolean isJoker() {
        return false;
    }

    /**
     * Retourne une representation textuelle du trophee.
     * 
     * @return le nom du trophee avec l emoji trophee
     */
    @Override
    public String toString() {
        return "TROPHY " + name;
    }
}