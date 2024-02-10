package view;

import java.awt.*;
import java.awt.event.*;
import java.io.InvalidClassException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

import model.*;
import model.playerModels.BotBase.BotType;

public class MyWindow extends JFrame
{
    // 1. variable declarations -------------------------------------------------------------------

    private final int __windowWidth = 600;
    private final int __windowHeight = 600;

    private PanelBase __panel;
    private JMenuBar __menubar;
    private JMenu __gameSettings;

    // sliders
    private final JLabel __sliderChangeGamesNumberTitle = new JLabel("Games to be played");
    private JSlider __sliderChangeGamesNumber;
    private final JLabel __sliderChangeBotDelayTitle = new JLabel("Time delay before bot move (ms)");
    private JSlider __sliderChangeBotDelay;

    // game settings for player 1
    private JMenu __gameSettingsBot1;
    private ButtonGroup __botTypeGroup1;
    private JRadioButton __gameSettingRandom1;

    // game settings for player 2
    private JMenu __gameSettingsBot2;
    private ButtonGroup __botTypeGroup2;
    private JRadioButton __gameSettingRandom2;
    private JRadioButton __gameSettingBruteforceLearning2; // only player 2

    // game settings texts
    private final String __gameSettingRandomText1 = "random white";
    private final String __gameSettingRandomText2 = "random black";
    private final String __gameSettingBruteForceLearningText2 = "brute-force learning black";

    // visual settings
    private JMenu __visualSettings;
    private JMenuItem __visualSettingMainMenuButtonColor;
    private JMenuItem __visualSettingBoardButtonColor1;
    private JMenuItem __visualSettingBoardButtonColor2;

    // visual settings texts
    private final String __visualSettingMainMenuText = "Change color of main menu buttons";
    private final String __visualSettingBoardText1 = "Change color 1 of board";
    private final String __visualSettingBoardText2 = "Change color 2 of board";
    
    // go back to main menu
    private JButton __quit; 
    public JLabel __gameNumber;

    // help
    private JMenu __help;
    private JMenuItem __howToPlay;
    private JMenuItem __aboutBots;

    // help texts
    String __howToPlayMessage = "<html>Hexapawn is a 3x3 version of chess using only pawns.<br>" +
    "Pawns move like in regular chess - one square forward if it is empty or<br>"+
    "diagonally forward if it is occupied by an opponent.<br>"+
    "The winner is the first person to get his pawn to the last row or<br> the last person "+
    "to make a move if current player can't make any valid move.</html>";
    String __aboutBotsMessage = "<html>You can select following types of bots:<br>"+
    "1. random - always makes random moves out of all possible.<br>"+
    "2. brute-force learning - like random, BUT if a move ends in opponent winning<br>"+
    "that move gets removed from list of possible moves.<br>"+
    "Only second bot can be of this type as hexapawn is a game where second player has winning strategy.<br>"+
    "<br>You can change bot type in \"Game settings\".</html>";

    // 2. constructor -----------------------------------------------------------------------------

    public MyWindow()
    {
        super("hexapawn");
        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
        int screen_width = (int)screen_size.getWidth();
        int screen_height = (int)screen_size.getHeight();

        // get settings from file
        Paths.initSettings();

        // initially we want to show main menu
        this.__panel = new MainMenu(this);
        this.add(this.__panel);
        __initMenuBar();

        this.setSize(__windowWidth, __windowHeight);
        this.setLocation((screen_width - __windowWidth) / 2, (screen_height - __windowHeight) / 2);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        // window close operations like saving settings
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e) 
            {
                __onClosing();
            }
        });
    }

    public void changeWindowPanel(PanelBase newPanel)
    {
        this.remove(this.__panel);
        this.__panel = newPanel;
        this.add(this.__panel);
        this.repaint();
        this.setVisible(true);
        // should not be editable during game
        this.__sliderChangeGamesNumber.setEnabled(!(newPanel instanceof GameView));
        // should only be visible during game
        this.__quit.setVisible(newPanel instanceof GameView);
        this.__gameNumber.setVisible(newPanel instanceof GameView);
        this.updateGameCounter(1);
    }

    private void __initMenuBar()
    {
        // create objects
        this.__menubar = new JMenuBar();
        this.__gameSettings = new JMenu("Game settings");

        // create game number slider
        this.__sliderChangeGamesNumber = 
            new JSlider(1,10,Settings.instance.gamesLimit);
        this.__sliderChangeGamesNumber.setMajorTickSpacing(1); // number of games must be integer
        this.__sliderChangeGamesNumber.setSnapToTicks(true); // slider will move to closest integer
        this.__sliderChangeGamesNumber.setPaintTicks(true); // show ticks on slider
        this.__sliderChangeGamesNumber.setPaintLabels(true); // show numbers on slider

        // create bot delay slider
        this.__sliderChangeBotDelay = 
            new JSlider(0,800, Settings.instance.milisecondsBeforeBotMove);
        this.__sliderChangeBotDelay.setMajorTickSpacing(100); // number of miliseconds must be %100
        this.__sliderChangeBotDelay.setSnapToTicks(true); // slider will move to closest %100 number
        this.__sliderChangeBotDelay.setPaintTicks(true); // show ticks on slider
        this.__sliderChangeBotDelay.setPaintLabels(true); // show numbers on slider

        // bot 1 type pick
        this.__gameSettingsBot1 = new JMenu("White bot type");
        this.__botTypeGroup1 = new ButtonGroup();
        this.__gameSettingRandom1 = new JRadioButton(
            this.__gameSettingRandomText1, Settings.instance.botType1 == BotType.Random);

        // bot 2 type pick
        this.__gameSettingsBot2 = new JMenu("Black bot type");
        this.__botTypeGroup2 = new ButtonGroup();
        this.__gameSettingRandom2 = new JRadioButton(
            this.__gameSettingRandomText2, Settings.instance.botType2 == BotType.Random);
        this.__gameSettingBruteforceLearning2 = new JRadioButton(
            this.__gameSettingBruteForceLearningText2, 
            Settings.instance.botType2 == BotType.BruteFroceLearning);
        
        // visual settings create
        this.__visualSettings = new JMenu("Visual settings");
        this.__visualSettingMainMenuButtonColor = 
            new JMenuItem(this.__visualSettingMainMenuText);
        this.__visualSettingBoardButtonColor1 = 
            new JMenuItem(this.__visualSettingBoardText1);
        this.__visualSettingBoardButtonColor2 = 
            new JMenuItem(this.__visualSettingBoardText2);

        this.__quit = new JButton("Quit");
        this.__gameNumber = new JLabel();
        this.updateGameCounter(1);
        
        // should only be visible during game
        this.__quit.setVisible(false);
        this.__gameNumber.setVisible(false);

        // help create
        this.__help = new JMenu("Help");
        this.__howToPlay = new JMenuItem("How to play");
        this.__aboutBots = new JMenuItem("About bots");

        // add onclicks
        this.__sliderChangeGamesNumber.addChangeListener(this::__sliderGameNumberChange);
        this.__sliderChangeBotDelay.addChangeListener(this::__sliderBotDelayChange);
        this.__gameSettingRandom1.addActionListener(this::__changeBotType);
        this.__gameSettingRandom2.addActionListener(this::__changeBotType);
        this.__gameSettingBruteforceLearning2.addActionListener(this::__changeBotType);

        this.__visualSettingMainMenuButtonColor.addActionListener(this::__changeButtonColor);
        this.__visualSettingBoardButtonColor1.addActionListener(this::__changeButtonColor);
        this.__visualSettingBoardButtonColor2.addActionListener(this::__changeButtonColor);
        this.__quit.addActionListener(this::__quitGame);
        this.__howToPlay.addActionListener(this::__helpAboutGame);
        this.__aboutBots.addActionListener(this::__helpAboutBots);

        this.__gameSettings.add(this.__sliderChangeGamesNumberTitle);
        this.__gameSettings.add(this.__sliderChangeGamesNumber);
        this.__gameSettings.add(this.__sliderChangeBotDelayTitle);
        this.__gameSettings.add(this.__sliderChangeBotDelay);
        // add objects to game settings white bot
        this.__gameSettings.add(this.__gameSettingsBot1); 
        this.__gameSettingsBot1.add(this.__gameSettingRandom1);
        this.__botTypeGroup1.add(this.__gameSettingRandom1);
        
        // add objects to game settings black bot
        this.__gameSettings.add(this.__gameSettingsBot2); 
        this.__gameSettingsBot2.add(this.__gameSettingRandom2);
        this.__gameSettingsBot2.add(this.__gameSettingBruteforceLearning2);
        this.__botTypeGroup2.add(this.__gameSettingRandom2);
        this.__botTypeGroup2.add(this.__gameSettingBruteforceLearning2);

        // add objects to visual settings
        this.__visualSettings.add(this.__visualSettingMainMenuButtonColor);
        this.__visualSettings.add(this.__visualSettingBoardButtonColor1);
        this.__visualSettings.add(this.__visualSettingBoardButtonColor2);

        // add objects to help
        this.__help.add(this.__howToPlay);
        this.__help.add(this.__aboutBots);

        // add objects to menubar
        this.__menubar.add(this.__gameSettings);
        this.__menubar.add(this.__visualSettings);
        this.__menubar.add(this.__quit);
        this.__menubar.add(this.__gameNumber);
        this.__menubar.add(Box.createHorizontalGlue());
        this.__menubar.add(this.__help);
        this.setJMenuBar(this.__menubar);
    }

    // 3. button onclicks -------------------------------------------------------------------------

    /** user changed number of games to be played in a row, update it in settings */
    private void __sliderGameNumberChange(ChangeEvent e)
    {
        Settings.instance.gamesLimit = this.__sliderChangeGamesNumber.getValue();
    }

    /** user changed bot delay, update it in settings */
    private void __sliderBotDelayChange(ChangeEvent e)
    {
        Settings.instance.milisecondsBeforeBotMove = this.__sliderChangeBotDelay.getValue();
    }

    private void __changeBotType(ActionEvent e)
    {
        String buttonText = ((JRadioButton)e.getSource()).getText();
        if (buttonText == this.__gameSettingRandomText1) // white is random bot
        {
            Settings.instance.botType1 = BotType.Random;
        }
        else if (buttonText == this.__gameSettingRandomText2) // black is random bot
        {
            Settings.instance.botType2 = BotType.Random;
        }
        else if (buttonText == this.__gameSettingBruteForceLearningText2) // black is brute-force learning bot
        {
            Settings.instance.botType2 = BotType.BruteFroceLearning;
        }
    }
    
    private void __changeButtonColor(ActionEvent e)
    {
        String buttonText = ((JMenuItem)e.getSource()).getText();
        if (buttonText == this.__visualSettingMainMenuText)
        {
            Settings.instance.mainMenuButtonColor = JColorChooser.showDialog(
            null, "Change button color", Settings.instance.mainMenuButtonColor);
        }
        else if (buttonText == this.__visualSettingBoardText1)
        {
            Settings.instance.boardButton1Color = JColorChooser.showDialog(
            null, "Change button color", Settings.instance.boardButton1Color);
        }
        else if (buttonText == this.__visualSettingBoardText2)
        {
            Settings.instance.boardButton2Color = JColorChooser.showDialog(
            null, "Change button color", Settings.instance.boardButton2Color);
        }
        this.__panel.repaintPanel();
    }

    /** player clicked "quit" option during game */
    private void __quitGame(ActionEvent e)
    {
        if (!(__panel instanceof GameView))
        {
            try 
            {
                throw new InvalidClassException("Quit button should only be visible during games!");
            } 
            catch (InvalidClassException ex) 
            {
                ex.getStackTrace();
            }
        }    
        ((GameView)__panel).afterGameAction(true); // go back to main view
    }

    /** updates text of JLabel responsible for holding info about number of game played 
     * @param gameNumber which number of game it is
    */
    public void updateGameCounter(int gameNumber)
    {
        String newText = String.format(
            "game %d of %d", gameNumber, Settings.instance.gamesLimit);
        this.__gameNumber.setText(newText);
    }

    /** player clicked "how to play" in help section */
    private void __helpAboutGame(ActionEvent e)
    {
        JOptionPane.showMessageDialog(null, this.__howToPlayMessage,
            "How to play Hexapawn", JOptionPane.INFORMATION_MESSAGE);
    }

    private void __helpAboutBots(ActionEvent e)
    {
        JOptionPane.showMessageDialog(null, this.__aboutBotsMessage,
            "About bots", JOptionPane.INFORMATION_MESSAGE);
    }

    private void __onClosing()
    {
        Paths.SaveSettings();
        System.exit(0);
    }
}
