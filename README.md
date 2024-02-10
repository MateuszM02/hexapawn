## Getting Started

To run game you need to open terminal in this folder, then run following command:
java Main
You will see a window with menubar and buttons for selecting game mode.
If you have compile errors, try to recompile it using:
javac Main.java controller/*.java model/*.java model/playerModels/*.java view/*.java

## Folder Structure

Entire project has following subfolders:
- controller: files responsible for game mechanics like starting game with default board, 
telling players to make move, making moves and updating board after moves.
- data: files where user settings and progress like full and improved decision trees are stored.
If removed, they will be restored with default values next time you play.
- model: files include Board model with its functions, 3 helping files - ClickModel, Paths, Settings and
subfolder playerModels with human and bot models with movement functions,
- view: contains files creating GUI of application - its window, main menu view and game view.
It has also all button click actions like picking game mode or clicking board to make move.