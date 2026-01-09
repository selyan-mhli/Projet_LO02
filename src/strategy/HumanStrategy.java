package strategy;

import model.*;
import java.util.List;

/**
 * Strategie pour un joueur humain.
 * 
 * <p>Cette strategie delegue les decisions a l interface utilisateur.
 * Elle ne prend pas de decisions automatiques mais retourne null,
 * ce qui signale au controleur de demander l input a la vue.</p>
 * 
 * <p>Cette classe permet d unifier l architecture en traitant tous
 * les joueurs (humains et IA) avec le meme patron Strategy.</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 2.0
 * @see Strategy
 * @see controller.GameEngine
 */
public class HumanStrategy implements Strategy {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur par defaut.
     */
    public HumanStrategy() {
    }

    /**
     * Cree une offre - delegue a la vue.
     * 
     * <p>Retourne null pour signaler au GameEngine que l offre
     * doit etre demandee via l interface utilisateur.</p>
     * 
     * @param player le joueur humain
     * @param hand   la main du joueur
     * @param game   la partie en cours
     * @return null (gere par la vue dans GameEngine)
     */
    @Override
    public Offer chooseOffer(Player player, List<Card> hand, Game game) {
        return null;
    }
    
    /**
     * Choisit une carte - delegue a la vue.
     * 
     * <p>Retourne null pour signaler au GameEngine que le choix
     * doit etre demande via l interface utilisateur.</p>
     * 
     * @param player le joueur humain
     * @param offers les offres disponibles
     * @param game   la partie en cours
     * @return null (gere par la vue dans GameEngine)
     */
    @Override
    public Card chooseTake(Player player, List<Offer> offers, Game game) {
        return null;
    }

    /**
     * Indique si cette strategie est pour un joueur humain.
     * 
     * @return toujours true
     */
    @Override
    public boolean isHuman() {
        return true;
    }
}
