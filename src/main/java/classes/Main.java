package classes;

import classes.Game.I18N.VARS;
import classes.Game.Model.Logic.EDT;
import classes.Game.Model.Structure.BitBoard.BitBoardMoves;
import classes.Game.Model.Structure.BitBoard.BitBoards;

import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoards.*;

public class Main {

    public static void main(String[] args) {
//           exceptionIgnorer();
        new EDT();
    }

    private static void printBitBoards(long pawn, long rook, long knight, long bishop, long queen, long king) {
        System.out.println(BitBoards.toString(pawn));
        System.out.println(BitBoards.toString(rook));
        System.out.println(BitBoards.toString(knight));
        System.out.println(BitBoards.toString(bishop));
        System.out.println(BitBoards.toString(queen));
        System.out.println(BitBoards.toString(king));
    }
}