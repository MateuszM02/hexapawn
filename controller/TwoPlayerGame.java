package controller;

import javax.management.AttributeNotFoundException;

import model.playerModels.Human;
import view.PanelBase;

public class TwoPlayerGame extends SimulationBase 
{
    /** initialization constructor */
    public TwoPlayerGame() 
    {
        super(GameType.TwoPlayers, true, true); // both players have full decision tree
    }
    
    public void initGame(PanelBase panel) 
        throws AttributeNotFoundException, InterruptedException 
    {
        this._player1 = new Human(this.__treeDatapath1);
        this._player2 = new Human(this.__treeDatapath2);
        super.initGame(panel);
    }

    /** inform player 1 it's time for his move */
    @Override
    protected void _triggerMove1()
    {
        // do nothing - humans make move when they click buttons
    }

    /** inform player 2 it's time for his move */
    @Override
    protected void _triggerMove2()
    {
        // do nothing - humans make move when they click buttons
    }
}