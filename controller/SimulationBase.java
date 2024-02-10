package controller;

import java.util.concurrent.TimeUnit;

import javax.management.AttributeNotFoundException;
import javax.swing.SwingWorker;

import model.*;
import model.playerModels.*;
import view.*;

/**
 * Base class for simulating hexapawn games.
 * Since there can be many type of games (player vs bot, 2 players, 2 bots)
 * this class is abstract and its extensions are those types of games
 */
public abstract class SimulationBase 
{
    // 1. variable declarations -------------------------------------------------------------------

    protected final String __treeDatapath1;
    protected final String __treeDatapath2;

    protected PlayerBase _player1;
    protected PlayerBase _player2;

    private PanelBase __panel;
    private Board __currentState;
    public ClickModel clickModel;

    /** tells whether player 1 makes next move */
    private boolean __nextMovePlayer1;

    /** possible types of game */
    public enum GameType 
    {
        TwoBots, PlayerBot, BotPlayer, TwoPlayers
    };

    /** type of currently played game */
    public final GameType type;

    // 2. constructor -----------------------------------------------------------------------------

    public SimulationBase(GameType type, boolean tree1Random, boolean tree2Random)
    {
        this.__treeDatapath1 = Paths.getTreePath(tree1Random);
        this.__treeDatapath2 = Paths.getTreePath(tree2Random);
        
        this.__currentState = new Board();
        this.clickModel = new ClickModel();
        this.__nextMovePlayer1 = true;
        this.type = type;
    }

    // 3. getters ---------------------------------------------------------------------------------

    public Board getBoard() 
    {
        return this.__currentState;
    }

    public int getBoardSize() 
    {
        return this.__currentState.Size;
    }

    public boolean getNextMovePlayer1()
    {
        return this.__nextMovePlayer1;
    }

    // 4. create fresh board of given type --------------------------------------------------------

    public SimulationBase reinitialize()
    {
        switch(this.type)
        {
            case BotPlayer:
                return new BotPlayerGame();
            case PlayerBot:
                return new PlayerBotGame();
            case TwoBots:
                return new TwoBotGame();
            case TwoPlayers:
                return new TwoPlayerGame();
            default:
                return null;
        }
    }

    // 5. Swing worker for bot movement delay -----------------------------------------------------

    /**
     * Make player movement after given break time, repaint board and tell opponent to make move.
     * @param withDelay time to wait before move (for bots only)
     */
    private void __makeMoveWithDelay(boolean withDelay) 
    {
        new SwingWorker<Void, Void>() 
        {
            /** sleep for specified amount before bot move */
            @Override
            protected Void doInBackground() throws InterruptedException 
            {
                if (withDelay) // if it is bot move, delay
                    TimeUnit.MILLISECONDS.sleep(Settings.instance.milisecondsBeforeBotMove);
                return null;
            }

            /** after the sleep is done, make the bot move and update the game state */
            @Override
            protected void done() 
            {
                try 
                {
                    if (__nextMovePlayer1) // player 1 move
                    {
                        __currentState = _player1.makeMove(clickModel);
                        _player2.receiveMove(__currentState);
                    } 
                    else // player 2 move
                    {
                        __currentState = _player2.makeMove(clickModel);
                        _player1.receiveMove(__currentState);
                    }

                    __nextMovePlayer1 = !__nextMovePlayer1; // change player to make next move
                    __panel.repaintPanel(); // update board view
                    int winner = __currentState.getWinner(
                            _player1, _player2, _player1.getRound());

                    if (winner == 0) // tell your opponent it's his turn
                    {
                        if (__nextMovePlayer1)
                            _triggerMove1();
                        else
                            _triggerMove2();
                    } else
                        __endGame(winner);
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // 6. methods handling player movements -------------------------------------------------------

    /**
     * called after player 1 made 2 clicks forming valid move
     * 
     * @throws AttributeNotFoundException decision tree error
     * @throws InterruptedException some error happened during bot sleeping
     */
    public void makeMovePlayer1()
            throws AttributeNotFoundException, InterruptedException 
    {
        this.__makeMoveWithDelay(this._player1 instanceof BotBase);
    }

    /**
     * called after player 2 made 2 clicks forming valid move
     * 
     * @throws AttributeNotFoundException decision tree error
     * @throws InterruptedException
     */
    public void makeMovePlayer2()
            throws AttributeNotFoundException, InterruptedException 
    {
        this.__makeMoveWithDelay(this._player2 instanceof BotBase);
    }

    /**
     * initialize decision trees for players and tell player 1 to make move
     * 
     * @throws InterruptedException
     * @throws AttributeNotFoundException
     */
    public void initGame(PanelBase panel)
            throws AttributeNotFoundException, InterruptedException 
    {
        this.__panel = panel;
        this.__panel.repaintPanel(); // update board view
        this._triggerMove1(); // player 1 always starts
    }

    /** after game ends, return to main menu */
    private void __endGame(int winner) throws InterruptedException 
    {
        new SwingWorker<Void, Void>() 
        {
            @Override
            protected Void doInBackground() throws Exception 
            {
                TimeUnit.MILLISECONDS.sleep(Settings.instance.milisecondsBeforeBotMove);
                return null;
            }

            @Override
            protected void done() // interrupt game thread and return to main menu
            {
                __callLost(winner);
                ((GameView)__panel).afterGameAction(); 
            }
        }.execute();
    }

    // 7. tell player who lost to improve his decision tree (only brute-force learning bot) -------

    private void __callLost(int winner)
    {
        switch (winner) 
        {
            case Board.Player1: // player 1 won, call player 2
                try 
                {
                    BruteforceLearningBot temp = (BruteforceLearningBot)_player2;
                    temp.lost();
                }
                catch(Exception e){ }
                break;
            case Board.Player2: // player 2 won, call player 1
                try 
                {
                    BruteforceLearningBot temp = (BruteforceLearningBot)_player1;
                    temp.lost();
                }
                catch(Exception e){ }
                break;
            default:
                try 
                {
                    throw new IllegalArgumentException("Received wrong player number!");
                } 
                catch (Exception e) 
                {
                    e.getStackTrace();
                };
        };
    }

    // 7. abstract method declarations ------------------------------------------------------------

    /**
     * inform player 1 it's time for his move
     * 
     * @throws AttributeNotFoundException
     * @throws InterruptedException
     */
    protected abstract void _triggerMove1() 
        throws AttributeNotFoundException, InterruptedException;

    /**
     * inform player 2 it's time for his move
     * 
     * @throws AttributeNotFoundException
     * @throws InterruptedException
     */
    protected abstract void _triggerMove2() 
        throws AttributeNotFoundException, InterruptedException;
}