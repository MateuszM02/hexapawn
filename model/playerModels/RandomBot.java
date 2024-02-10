package model.playerModels;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import model.*;

public class RandomBot extends BotBase 
{
    private Random __random;

    protected RandomBot(String datapath) 
    {
        super(datapath, BotType.Random);
        this.__random = new Random();
    }
        
    @Override
    public Board makeMove(ClickModel __u__)
    {
        try 
        {
            TimeUnit.MILLISECONDS.sleep(500);
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
        int movesCount = this._currentMovesTree.possibleMovesCount();
        int index = this.__random.nextInt(movesCount);
        Board newBoard = this._currentMovesTree.NextState(index);
        this._currentMovesTree = newBoard;
        this._movesHistory.add(index);
        return this._currentMovesTree;
    }

    @Override
    public void lost() { } // random bot doesnt learn anything after losing
}
