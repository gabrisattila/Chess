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
//        setUpStarterBitBoards();
//        setUpBitBoard(VARS.FINALS.testFens.get("whiteDownOnlyKingsAndRooks"));
//        for (int i = 63; i >= 0; i--) {
//            System.out.print((i < 10 ? " " : "") + i + " ");
//            if (i % 8 == 0){
//                System.out.println();
//            }
//        }
//        System.out.println(BitBoardMoves.possibleMoves(true, -1,
//                whiteSmallCastleEnabled, whiteBigCastleEnabled, blackSmallCastleEnabled, blackBigCastleEnabled,
//                whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
//                blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing));
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