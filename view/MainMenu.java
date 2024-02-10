package view;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.naming.InvalidNameException;
import javax.swing.JButton;

import controller.*;
import model.*;

public class MainMenu extends PanelBase 
{
    // 1. variable declarations -------------------------------------------------------------------

    private JButton __twoBotsButton;
    private JButton __playerBotButton;
    private JButton __botPlayerButton;
    private JButton __twoPlayersButton;

    private final String __twoBotsText = "Watch 2 bots play";
    private final String __playerBotText = "Player vs bot";
    private final String __botPlayerText = "Bot vs player";
    private final String __twoPlayersText = "2 player game";
    
    // 2. constructor -----------------------------------------------------------------------------

    public MainMenu(MyWindow window) 
    {
        super(window, 4, 1);
        // initialize elements
        this.__twoBotsButton = new JButton(this.__twoBotsText);
        this.__botPlayerButton = new JButton(this.__botPlayerText);
        this.__playerBotButton = new JButton(this.__playerBotText);
        this.__twoPlayersButton = new JButton(this.__twoPlayersText);

        this.__twoBotsButton.setName(this.__twoBotsText);
        this.__botPlayerButton.setName(this.__botPlayerText);
        this.__playerBotButton.setName(this.__playerBotText);
        this.__twoPlayersButton.setName(this.__twoPlayersText);

        this.repaintPanel();

        this.__twoBotsButton.addActionListener(this);
        this.__botPlayerButton.addActionListener(this);
        this.__playerBotButton.addActionListener(this);
        this.__twoPlayersButton.addActionListener(this);

        // add elements to panel
        this.add(this.__twoBotsButton);
        this.add(this.__botPlayerButton);
        this.add(this.__playerBotButton);
        this.add(this.__twoPlayersButton);
    }

    // 3. player picked game type to play ---------------------------------------------------------

    @Override
    protected void _buttonClicked(ActionEvent e) 
        throws InvalidNameException, InvalidAttributeValueException, 
        IOException, AttributeNotFoundException, InterruptedException
    {
        Object source = e.getSource();
        String name = ((JButton)source).getName();
        this.play(name);
    }

    // 4. play a game -----------------------------------------------------------------------------

    private void play(String name) 
        throws InvalidAttributeValueException, InvalidNameException, 
        IOException, AttributeNotFoundException, InterruptedException
    {
        SimulationBase newGame;

        switch(name)
        {
            case __twoBotsText:
                newGame = new TwoBotGame();
                break;
            case __botPlayerText:
                newGame = new BotPlayerGame(); 
                break;
            case __playerBotText: 
                newGame = new PlayerBotGame();
                break;
            case __twoPlayersText: 
                newGame = new TwoPlayerGame();
                break;
            default: 
                throw new InvalidNameException(
                    "Given button should not call this action!");
        }
        PanelBase gamePanel = new GameView(newGame, this._window);
        this._window.changeWindowPanel(gamePanel);
    }

    @Override
    public void repaintPanel() 
    { 
        this.__twoBotsButton.setBackground(Settings.instance.mainMenuButtonColor);
        this.__botPlayerButton.setBackground(Settings.instance.mainMenuButtonColor);
        this.__playerBotButton.setBackground(Settings.instance.mainMenuButtonColor);
        this.__twoPlayersButton.setBackground(Settings.instance.mainMenuButtonColor);
    }
}
