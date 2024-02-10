package controller;

import javax.management.AttributeNotFoundException;

import model.Settings;
import model.playerModels.BotBase;
import model.playerModels.BotBase.BotType;
import view.PanelBase;

public class TwoBotGame extends SimulationBase
{
    /** initialization constructor */
    public TwoBotGame() 
    {
        super(GameType.TwoBots, 
            Settings.instance.botType1 != BotType.BruteFroceLearning, 
            Settings.instance.botType2 != BotType.BruteFroceLearning);
    }

    public void initGame(PanelBase panel) 
        throws AttributeNotFoundException, InterruptedException
    {
        this._player1 = BotBase.create(Settings.instance.botType1, this.__treeDatapath1);
        this._player2 = BotBase.create(Settings.instance.botType2, this.__treeDatapath2);
        super.initGame(panel);
    }

    /** inform player 1 it's time for his move */
    @Override
    protected void _triggerMove1() 
        throws AttributeNotFoundException, InterruptedException
    {
        this.makeMovePlayer1();
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