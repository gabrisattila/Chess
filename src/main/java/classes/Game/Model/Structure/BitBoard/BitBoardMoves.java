package classes.Game.Model.Structure.BitBoard;

import classes.Game.I18N.Location;

import java.util.ArrayList;

import static classes.Game.I18N.VARS.MUTABLE.whiteDown;
import static classes.Game.I18N.VARS.MUTABLE.whiteToPlay;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoards.collectPieceBoardsToOneList;
import static classes.Game.Model.Structure.BitBoard.BitBoards.mergeFullBitBoard;

public class BitBoardMoves {

    public static String possibleMoves(boolean maxNeeded, long whitePawn, long whiteBishop, long whiteKnight, long whiteRook, long whiteKing, long whiteQueen, long blackPawn, long blackBishop, long blackKnight, long blackRook, long blackKing, long blackQueen) {
        return "";
    }

    //region Methods With BitBoards

    public static void moveAPieceOnBoard(long bitBoardOfPiece, long bitBoardOfSecondPiece,
                                         int startIndexOfStepper, int endIndexOfStepper,
                                         int startIndexOfSecondPiece, int endIndexOfSecondPiece,
                                         boolean isItEmPassant, boolean isItEmPassantAuthorization,
                                         boolean isItCastle, boolean isItPawnGotIn){
        long helperBoard = 1L << startIndexOfStepper;
        bitBoardOfPiece &= ~helperBoard;
        helperBoard = 1L << endIndexOfStepper;
        bitBoardOfPiece |= helperBoard;
        if (bitBoardOfSecondPiece != 0){
            if (isItCastle){
                helperBoard = 1L << startIndexOfSecondPiece;
                bitBoardOfSecondPiece &= ~helperBoard;
                helperBoard = 1L << endIndexOfSecondPiece;
                bitBoardOfSecondPiece |= helperBoard;
            } else if (isItEmPassant) {
                //TODO Megvcsinálni az emPassant esetet.
            }else {
                helperBoard = 1L << startIndexOfSecondPiece;
                bitBoardOfSecondPiece &= ~helperBoard;
            }
        }
    }


    /**
     * @param theBitBoardOfPawns If whiteToPlay this is whitePawn else it's blackPawn
     * @return the possible moves move doc string
     */
    public String pawnMoves(long theBitBoardOfPawns, String emPassantChance){

        HITTABLE_BY_BLACK = whitePawn | whiteKnight | whiteBishop | whiteRook | whiteQueen;
        HITTABLE_BY_WHITE = blackPawn | blackKnight | blackBishop | blackRook | blackQueen;
        EMPTY = ~mergeFullBitBoard(collectPieceBoardsToOneList());

        StringBuilder moves = new StringBuilder();

        long pawnMoves;
        if (whiteToPlay){
            //Előre sima 1
            pawnMoves = (whiteDown ? theBitBoardOfPawns << 8 : theBitBoardOfPawns >> 8) & EMPTY;
            pawnMoveListMaker(pawnMoves, moves, 1, 0);
//            Előre sima 2
            pawnMoves = (whiteDown ? theBitBoardOfPawns << 16 : theBitBoardOfPawns >> 16) & EMPTY;
            pawnMoveListMaker(pawnMoves, moves, 2, 0);
            //Jobbra üt
            pawnMoves = (whiteDown ? (theBitBoardOfPawns << 7 & ~COL_A) : (theBitBoardOfPawns >> 9 & ~COL_H)) & HITTABLE_BY_BLACK;
            pawnMoveListMaker(pawnMoves, moves, 1, whiteDown ? -1 : 1);
            //Balra üt
            pawnMoves = (whiteDown ? (whitePawn << 9 & ~COL_H) : (whitePawn >> 7 & ~COL_A)) & HITTABLE_BY_BLACK;
            pawnMoveListMaker(pawnMoves, moves, 1, 1);
        }else {
            //Előre sima 1
//            pawnMoves |= (whiteDown ? blackPawn >> 8 : blackPawn << 8) & EMPTY;
            //Előre sima 2
//            pawnMoves |= (whiteDown ? blackPawn >> 16 : blackPawn << 16) & EMPTY;
            //Jobbra üt
//            pawnMoves |= (whiteDown ? (blackPawn >> 9 & ~COL_A) : (blackPawn << 7 & ~COL_H)) & HITTABLE_BY_WHITE;
            //Balra üt
//            pawnMoves |= (whiteDown ? (blackPawn >> 7 & ~COL_H) : (blackPawn << 9 & ~COL_A)) & HITTABLE_BY_WHITE;
        }

        return "";
    }

    public String knightMoves(boolean forWhite, int indexOfASinglePawn, long bitBoardOfPawns){
        //TODO returns the possible end indexes of the knight which stands on the given index
        return null;
    }

    public String bishopMoves(boolean forWhite, int indexOfASinglePawn, long bitBoardOfPawns){
        //TODO returns the possible end indexes of the bishop which stands on the given index
        return null;
    }

    public String rookMoves(boolean forWhite, int indexOfASinglePawn, long bitBoardOfPawns){
        //TODO returns the possible end indexes of the rook which stands on the given index
        return null;
    }

    public String queenMoves(boolean forWhite, int indexOfASinglePawn, long bitBoardOfPawns){
        //TODO returns the possible end indexes of the queen which stands on the given index
        return null;
    }

    public String kingMoves(boolean forWhite, int indexOfASinglePawn, long bitBoardOfPawns){
        //TODO returns the possible end indexes of the king which stands on the given index
        return null;
    }

    private void pawnMoveListMaker(long pawnMoves, StringBuilder moves, int addToFirst, int addToSecond){
        for (int i = Long.numberOfLeadingZeros(pawnMoves); i < 64 - Long.numberOfLeadingZeros(pawnMoves); i++) {
            if ((pawnMoves >> i & 1) == 1){
                moves.append((i / 8) + addToFirst);
                moves.append((i % 8) + addToSecond);
                moves.append(i / 8);
                moves.append(i % 8);
            }
        }
    }

    //endregion

}
