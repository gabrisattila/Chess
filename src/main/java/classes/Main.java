package classes;

import classes.Game.I18N.VARS;
import classes.Game.Model.Structure.BitBoard.BitBoardMoves;
import classes.Game.Model.Structure.BitBoard.BitBoards;

import static classes.Game.I18N.VARS.MUTABLE.whiteToPlay;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoards.*;

public class Main {

    public static void main(String[] args) {
//           exceptionIgnorer();
//        new EDT();
//        setUpStarterBitBoards();
        setUpBitBoard(VARS.FINALS.testFens.get("whiteDownWithOutPawns"));
        for (int i = 63; i >= 0; i--) {
            System.out.print((i < 10 ? " " : "") + i + " ");
            if (i % 8 == 0){
                System.out.println();
            }
        }
        System.out.println(BitBoardMoves.possibleMoves(false, -1,
                whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing));
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