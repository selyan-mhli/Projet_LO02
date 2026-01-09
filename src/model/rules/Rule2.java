package model.rules;

import model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Variante : Trophees inverses.
 * 
 * <p>Dans cette variante, les trophees sont gagnes par le joueur
 * qui les merite le moins (ex: Highest va au joueur avec le moins).</p>
 * 
 * @author Projet LO02 - UTT
 * @version 2.0
 * @since 1.0
 */
public class Rule2 implements RuleSet {
    private static final long serialVersionUID = 1L;

    @Override
    public ScoreVisitor scoreVisitor() {
        return new Rule1().scoreVisitor();
    }

    @Override
    public List<TrophyCard> trophies() {
        return new ArrayList<>();
    }

    @Override
    public int numberOfTrophies(int playerCount) {
        return playerCount == 4 ? 1 : 2;
    }
}