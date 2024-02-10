## About Game
Hexapawn is a board game played on 3x3 chess-like board.<br>
Top row contains 3 black pawns, middle is empty, and bottom has 3 white pawns.<br>
Pawns move like in chess. The winner is a player who gets his pawn to last row first<br>
or the player who made last move if the game ended in stalemate.

## Getting Started

To run game you need to open terminal in this folder, then run following command:<br>
<b>java Main</b><br>
You will see a window with menubar and buttons for selecting game mode.<br>
If you have compile errors, try to recompile it using:<br>
<b>javac Main.java controller/\*.java model/\*.java model/playerModels/\*.java view/\*.java</b>

## Folder Structure

Entire project has following subfolders:
- <b>controller</b>: files responsible for game mechanics like starting game with default board,<br> 
telling players to make move, making moves and updating board after moves.
- <b>data</b>: files where user settings and progress like full and improved decision trees are stored.<br>
If removed, they will be restored with default values next time you play.
- <b>model</b>: files include Board model with its functions, 3 helping files - ClickModel, Paths, Settings<br>
and subfolder playerModels with human and bot models with movement functions,
- <b>view</b>: contains files creating GUI of application - its window, main menu view and game view.<br>
It has also all button click actions like picking game mode or clicking board to make move.
