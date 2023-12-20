package classes;

import classes.Ai.BitBoards.BitBoards;
import classes.Game.I18N.VARS;

import static classes.Ai.BitBoards.BBVars.*;
import static classes.Ai.BitBoards.BitBoardMoves.*;
import static classes.Ai.BitBoards.BitBoards.*;

public class Main {

    public static void main(String[] args) {
//        new EDT();
        setUpBitBoard(VARS.FINALS.testFens.get("whiteDownOnePawnInTheEdgeOfPawnPromotion"));
        fillBaseBitBoardPossibilities();
        bbEmPassant = 41;
        generateMoves();
        int move;
        for (int i = 0; i < moveCount; i++) {
            move = movesInATurn[i];
            System.out.println(moveToString(move));
            if (makeMove(move)){
                printFullBoard();
            }
            undoMove();
        }

//        for (int i = 63; i >= 0; i--) {
//            System.out.println(BitBoards.toString(pawnPossibilityTable[0][i]));
//        }

//        System.out.println(BitBoards.toString(ROW_2 | ROW_7));
//
    }

}