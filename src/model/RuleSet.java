package model;

import java.io.Serializable;
import java.util.List;

/**
 * Interface definissant un ensemble de regles pour le jeu Jest.
 * 
 * <p>Cette interface permet de creer differentes variantes du jeu
 * en modifiant le calcul des scores et la gestion des trophees.</p>
 * 
 * <h2>Implementations disponibles :</h2>
 * <ul>
 *   <li>{@link model.rules.Rule1} - Regles officielles</li>
 *   <li>{@link model.rules.Rule2} - Trophees inverses</li>
 *   <li>{@link model.rules.Rule3} - Couleurs inversees</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 */
public interface RuleSet extends Serializable {
    
    /**
     * Retourne le visiteur de score pour ces regles.
     * 
     * @return le ScoreVisitor a utiliser
     */
    ScoreVisitor scoreVisitor();
    
    /**
     * Retourne la liste des trophees pour ces regles.
     * 
     * @return la liste des TrophyCard
     */
    List<TrophyCard> trophies();
    
    /**
     * Retourne le nombre de trophees selon le nombre de joueurs.
     * 
     * @param playerCount le nombre de joueurs
     * @return le nombre de trophees (1 pour 4 joueurs, 2 sinon)
     */
    int numberOfTrophies(int playerCount);
}