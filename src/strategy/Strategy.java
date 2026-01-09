package strategy;

import model.*;
import java.io.Serializable;
import java.util.List;

/**
 * Interface definissant une strategie pour tous les joueurs (humains et IA).
 * 
 * <p>Cette interface implemente le patron de conception Strategy,
 * permettant de definir differents comportements pour tous les joueurs.
 * Elle unifie l architecture en traitant les joueurs humains et IA
 * de maniere homogene.</p>
 * 
 * <h2>Implementations disponibles :</h2>
 * <ul>
 *   <li>{@link HumanStrategy} - Strategie pour joueur humain (delegue a la vue)</li>
 *   <li>{@link Strategy1} - Strategie IA conservatrice</li>
 *   <li>{@link Strategy2} - Strategie IA bluff</li>
 * </ul>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 * @see HumanStrategy
 * @see AIStrategy
 * @see Strategy1
 * @see Strategy2
 */
public interface Strategy extends Serializable {
    
    /**
     * Cree une offre pour le joueur.
     * 
     * @param player le joueur
     * @param hand   la main du joueur
     * @param game   la partie en cours
     * @return l offre creee (ou null si geree par la vue)
     */
    Offer chooseOffer(Player player, List<Card> hand, Game game);
    
    /**
     * Choisit une carte parmi les offres disponibles.
     * 
     * @param player le joueur
     * @param offers les offres disponibles
     * @param game   la partie en cours
     * @return la carte choisie (ou null si geree par la vue)
     */
    Card chooseTake(Player player, List<Offer> offers, Game game);
    
    /**
     * Indique si cette strategie est pour un joueur humain.
     * 
     * @return true si humain, false si IA
     */
    boolean isHuman();
}