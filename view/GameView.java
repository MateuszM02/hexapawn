package view;

import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.naming.InvalidNameException;

import controller.*;
import model.*;

public class GameView extends PanelBase 
{
    // 1. variable declarations -------------------------------------------------------------------

    private final Icon __whitePawnIcon;
    private final Icon __blackPawnIcon;
    private int __gameCounter;

    private SimulationBase __game;
    Thread __gameThread;  
    
    private JButton[][] __boardButtons;

    private boolean CONTINUE_GAME = true;
    
    // 2. constructor -----------------------------------------------------------------------------

    /** primary constructor */
    public GameView(SimulationBase game, MyWindow window) throws IOException 
    {
        super(window, game.getBoardSize(), game.getBoardSize());
        this.__whitePawnIcon = Paths.getWhitePawnIcon();
        this.__blackPawnIcon = Paths.getBlackPawnIcon();
        this.__game = game;
        this.__gameCounter = 1;
        this.__gameThread = new Thread(this::startGameThread);
        this.__gameThread.start();

        int boardSize = game.getBoardSize();
        // initialize board view
        this.__boardButtons = new JButton[boardSize][boardSize];
        
        // create board buttons - first row are whites, last row are blacks
        for (int row = 0; row < boardSize; row++)
        {
            for (int col = 0; col < boardSize; col++) 
            {
                this.__boardButtons[row][col] = new JButton();
                this.__boardButtons[row][col].setName(row+";"+col);
                this.__boardButtons[row][col].addActionListener(this);
            }
        }

        // at the start, black pawns should be at top of screen, so they need to be added first 
        for (int row = boardSize - 1; row >= 0; row--) 
        {
            for (int col = boardSize - 1; col >= 0; col--)
            {
                this.add(this.__boardButtons[row][col]);
            }
        }

        this.repaintPanel();
    }

    // 3. start game thread -----------------------------------------------------------------------

    public void startGameThread()
    {
        try 
        {
            this.__game.initGame(this); // start game
            this._window.updateGameCounter(this.__gameCounter);
            while (!Thread.currentThread().isInterrupted()) { } // wait until game is finished
        }
        catch  (AttributeNotFoundException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    // 4. helper methods --------------------------------------------------------------------------

    /** ends game thread, after what you either play new game or get sent back to Main Menu */
    public void afterGameAction()
    {
        // end if played given amount of games 
        afterGameAction(this.__gameCounter >= Settings.instance.gamesLimit); 
    }
    
    /** ends game thread, after what you either play new game or get sent back to Main Menu */
    public void afterGameAction(boolean end)
    {
        this.__gameThread.interrupt(); // always gets called no matter end value
        
        if (end) // go back to main view
        {   
            this.CONTINUE_GAME = false;
            this.__gameCounter = 0;
            this._window.changeWindowPanel(new MainMenu(this._window));
            return;
        }

        // if the game wasn't interrupted by player clicking Quit button, play new game
        if (this.CONTINUE_GAME) 
        {
            this.__gameCounter++;
            this.__gameThread = new Thread(this::startGameThread);
            this.__game = this.__game.reinitialize();
            this.__gameThread.start();
        }
    }

    // 5. player clicked on board -----------------------------------------------------------------

    @Override
    protected void _buttonClicked(ActionEvent e) 
        throws InvalidNameException, InvalidAttributeValueException, 
        AttributeNotFoundException, InterruptedException
    {
        Object source = e.getSource();
        JButton button = (JButton) source;

        // get clicked button's position
        String[] rowcol = button.getName().split(";");
        int row = Integer.parseInt(rowcol[0]);
        int col = Integer.parseInt(rowcol[1]);
        
        // determine game type
        switch(this.__game.type)
        {
            case TwoBots: 
                return; // clicking buttons during 2 bot game should be ignored
            case PlayerBot: 
                __PlayerBotBoardClicked(row, col);
                return;
            case BotPlayer: 
                __BotPlayerBoardClicked(row, col);
                return;
            case TwoPlayers: 
                __TwoPlayerBoardClicked(row, col);
                return;
            default: return;
        }
    }

    // 5. player clicked against bot --------------------------------------------------------------

    private void __PlayerBotBoardClicked(int row, int col)
        throws InvalidAttributeValueException, AttributeNotFoundException, InterruptedException
    {   
        Board currentBoard = this.__game.getBoard();
        if (currentBoard.getWhoseMove() != Board.Player1)
            return; // ignore clicks when bot turn
        this.__game.clickModel.AddClick(currentBoard, row, col);
        if (this.__game.clickModel.isCorrectMove()) // 2 clicks form a valid move, make it
        {    
            if (this.__game.getNextMovePlayer1())
                this.__game.makeMovePlayer1();
        }
    }

    private void __BotPlayerBoardClicked(int row, int col)
        throws InvalidAttributeValueException, AttributeNotFoundException, InterruptedException
    {   
        Board currentBoard = this.__game.getBoard();
        if (currentBoard.getWhoseMove() != Board.Player2)
            return; // ignore clicks when bot turn
        this.__game.clickModel.AddClick(currentBoard, row, col);
        if (this.__game.clickModel.isCorrectMove()) // 2 clicks form a valid move, make it
        {    
            if (!this.__game.getNextMovePlayer1())
                this.__game.makeMovePlayer2();
        }
    }

    // 6. player clicked against another player ---------------------------------------------------

    /**
     * Called when in 2 player game someone clicks one of board buttons
     * @param row row of button clicked
     * @param col column of button clicked
     * @throws InvalidAttributeValueException 
     * @throws InterruptedException 
     * @throws AttributeNotFoundException 
     */
    private void __TwoPlayerBoardClicked(int row, int col) 
        throws InvalidAttributeValueException, AttributeNotFoundException, InterruptedException
    {
        Board currentBoard = this.__game.getBoard();
        this.__game.clickModel.AddClick(currentBoard, row, col);
        if (this.__game.clickModel.isCorrectMove()) // 2 clicks form a valid move, make it
        {    
            if (this.__game.getNextMovePlayer1())
                this.__game.makeMovePlayer1();
            else
                this.__game.makeMovePlayer2();
        }
    }

    // 7. Creates and adds elements to panel ------------------------------------------------------

    @Override
    public void repaintPanel()
    {
        int boardSize = this.__game.getBoardSize();
        for (int row = 0; row < boardSize; row++)
        {
            for (int col = 0; col < boardSize; col++)
            {
                this.__boardButtons[row][col].removeAll();
                if ((row + col) % 2 == 0)
                    this.__boardButtons[row][col].setBackground(
                        Settings.instance.boardButton1Color);
                else
                    this.__boardButtons[row][col].setBackground(
                        Settings.instance.boardButton2Color);

                Icon icon = null;
                switch (this.__game.getBoard().getBoardAt(row, col))
                {
                    case Board.Empty:
                        this.__boardButtons[row][col].setIcon(null);
                        continue;
                    case Board.Player1:
                        icon = this.__whitePawnIcon;
                        break;
                    case Board.Player2:
                        icon = this.__blackPawnIcon;
                        break;
                    default:
                        try 
                        {
                            throw new InvalidAttributeValueException();
                        } 
                        catch (InvalidAttributeValueException e) 
                        {
                            e.printStackTrace();
                        }
                        break;
                }
                this.__boardButtons[row][col].setIcon(icon);
            }
        }
    } 
}
