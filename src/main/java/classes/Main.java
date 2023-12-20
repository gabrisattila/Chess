package classes;

import classes.Ai.BitBoards.BitBoardMoves;
import classes.Ai.BitBoards.BitBoards;
import classes.Game.I18N.VARS;

import static classes.Ai.BitBoards.BBVars.*;
import static classes.Ai.BitBoards.BitBoardMoves.*;
import static classes.Ai.BitBoards.BitBoards.*;
import static classes.Game.I18N.VARS.MUTABLE.whiteToPlay;

public class Main {

    public static void main(String[] args) {
//        new EDT();
        setUpBitBoard(VARS.FINALS.testFens.get("whiteDownWithOutPawns"));
        fillBaseBitBoardPossibilities();
        generateMoves();
        int move;
        for (int i = 0; i < moveCount; i++) {
            move = movesInATurn[i];
            copyPosition();
            System.out.println(moveToString(move));
            if (makeMove(move)){
                printFullBoard();
            }
            undoMove();
        }
    }

}