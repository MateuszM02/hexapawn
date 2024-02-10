package model;

import java.awt.*;
import java.io.Serializable;

import model.playerModels.BotBase.BotType;

public class Settings implements Serializable
{
    // 1. variable declarations -------------------------------------------------------------------

    public static Settings instance;

    /** type of bot as player 1 (can't be brute-force learning!) */
    public BotType botType1;
    /** type of bot as player 2 */
    public BotType botType2;
    
    /** default is light green  */
    public Color mainMenuButtonColor;
    /** default is brown */
    public Color boardButton1Color;
    /** default is light yellow */
    public Color boardButton2Color;

    /** number of games to be played before returning to main menu, default is 1 */
    public int gamesLimit;
    /** to make game more visually appealing - bot won't be making moves
     * immediately after opponent, default is 500 (0.5 sec) */
    public int milisecondsBeforeBotMove;

    // 2. constructor -----------------------------------------------------------------------------

    public Settings()
    {
        this.botType1 = BotType.Random;
        this.botType2 = BotType.Random;
        this.mainMenuButtonColor = new Color(200, 240, 100);
        this.boardButton1Color = new Color(133,72,53);
        this.boardButton2Color = new Color(255, 253, 216);
        this.gamesLimit = 1;
        this.milisecondsBeforeBotMove = 500;
    }
}
