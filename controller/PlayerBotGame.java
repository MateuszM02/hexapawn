package controller;

import javax.management.AttributeNotFoundException;

import model.Settings;
import model.playerModels.*;
import model.playerModels.BotBase.BotType;
import view.PanelBase;

public class PlayerBotGame extends SimulationBase
{
    /** initialization constructor */
    public PlayerBotGame() 
    {
        super(GameType.PlayerBot, true, 
            Settings.instance.botType2 != BotType.BruteFroceLearning);
    }

    public void initGame(PanelBase panel) 
        throws AttributeNotFoundException, InterruptedException 
    {
        this._player1 = new Human(this.__treeDatapath1);
        this._player2 = BotBase.create(Settings.instance.botType2, this.__treeDatapath2);
        super.initGame(panel);
    }

    /** inform player 1 it's time for his move */
    @Override
    protected void _triggerMove1()
    {
        // do nothing - humans make move when they click buttons
    }

    /** inform player 2 it's time for his move 
     * @throws AttributeNotFoundException 
     * @throws InterruptedException */
    @Override
    protected void _triggerMove2() 
        throws AttributeNotFoundException, InterruptedException
    {
        this.makeMovePlayer2();
    }
}