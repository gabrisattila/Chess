package classes;

import classes.Game.I18N.VARS;
import classes.Game.Model.Structure.BitBoard.BitBoardMoves;
import classes.Game.Model.Structure.BitBoard.BitBoards;

import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoards.*;

public class Main {

    public static void main(String[] args) {
//           exceptionIgnorer();
//        new EDT();
        setUpStarterBitBoards();
        System.out.println(BitBoards.toString(BitBoardMoves.bishopMoves(false, whiteBishop, blackBishop)));
//        for (int i = 0; i < 64; i++) {
//            System.out.println("Ha i = " + i + ":");
//            System.out.println(BitBoards.toString(BitBoardMoves.diagonalAndAntiDiagonalMoves(i)));
//        }
    }
}