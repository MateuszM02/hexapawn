package model.playerModels;

import javax.management.AttributeNotFoundException;

import model.*;

public class Human extends PlayerBase
{
    public Human(String datapath)
    {
        super(datapath);
    }

    @Override
    public Board makeMove(ClickModel clickModel) throws AttributeNotFoundException 
    {
        // create new state
        Board currentBoard = this._currentMovesTree.clone();
        Board newBoard = this._currentMovesTree.createNewState(
            clickModel.getFromRow(), clickModel.getFromCol(), 
            clickModel.getToRow(), clickModel.getToCol());

        // return it as a move
        int index = currentBoard.getNextStatesIndex(newBoard);
        this._currentMovesTree = newBoard;
        this._movesHistory.add(index);

        clickModel.reset(); // make sure next click counts as first
        return this._currentMovesTree;
    }
}