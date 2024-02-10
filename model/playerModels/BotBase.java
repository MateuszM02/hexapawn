package model.playerModels;

import javax.management.AttributeNotFoundException;

import model.Board;
import model.ClickModel;

public abstract class BotBase extends PlayerBase
{
    // 1. variable declarations -------------------------------------------------------------------

    /** possible types of bot */
    public enum BotType 
    {
        Random, BruteFroceLearning
    };

    /** type of this bot */
    public final BotType type;

    // 2. constructor -----------------------------------------------------------------------------

    public BotBase(String datapath, BotType botType)
    {
        super(datapath);
        this.type = botType;
    }

    /**
     * Create bot with given type
     * @param type type of bot - random, brute-force learning
     * @param datapath path to decision tree (if this type of bot uses it)
     * @return new instance of bot of given type
     */
    public static BotBase create(BotType type, String datapath)
    {
        switch (type) 
        {
            case Random:
                return new RandomBot(datapath);
            case BruteFroceLearning:
                return new BruteforceLearningBot(datapath);
            default:
                return null;
        }
    }

    // 3. overrides -------------------------------------------------------------------------------

    /** From current board state, pick a move and return new board state 
     * @throws AttributeNotFoundException */
    public abstract Board makeMove(ClickModel __u__);

    /** Behaviour after losing. For learning bot it's removing losing move from decision tree. */
    public abstract void lost();
}
