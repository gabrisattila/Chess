package classes.Game.Model.Structure.BitBoard;

import classes.Game.I18N.PieceType;

import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;

import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoards.*;

public class BitBoardMoves {

    public static String possibleMoves(boolean maxNeeded, int emPassantChance,
                                       long wG, long wH, long wF, long wB, long wV, long wK,
                                       long bG, long bH, long bF, long bB, long bV, long bK) {

        HITTABLE_BY_BLACK = wG | wH | wF | wB | wV;
        HITTABLE_BY_WHITE = bG | bH | bF | bB | bV;
        OCCUPIED = mergeFullBitBoard(new ArrayList<>(){{add(wG); add(wH); add(wF); add(wB); add(wV); add(wK);
            add(bG); add(bH); add(bF); add(bB); add(bV); add(bK);}});
        EMPTY = ~OCCUPIED;

        String moves = pawnMoves(maxNeeded, maxNeeded ? wG : bG, emPassantChance, wG, wV, bG, bV);
//        moves += knightMoves();
//        moves += bishopMoves();
//        moves += rookMoves();
//        moves += queenMoves();
//        moves += kingMoves();

        return moves;
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
    public static String pawnMoves(boolean forWhite, long theBitBoardOfPawns, int emPassantChance,
                                   long wG, long wV, long bG, long bV){

        long emPassantPossibility = 1L << emPassantChance;

        StringBuilder moves = new StringBuilder();

        long pawnMoves;
        int plusToOrigin;
        long cantCountThatPart;
        long shouldBeInThatPart;
        if (forWhite){
            //Előre sima 1
            shouldBeInThatPart = EMPTY;
            pawnMoves = (whiteDown ? theBitBoardOfPawns << 8 : theBitBoardOfPawns >> 8) & shouldBeInThatPart;
            plusToOrigin = whiteDown ? 8 : -8;
            moveDocStringCreation(G, moves, plusToOrigin,
                    0, shouldBeInThatPart, true, pawnMoves, wV, wG);

//          Előre sima 2
            pawnMoves = (whiteDown ? theBitBoardOfPawns << 16 : theBitBoardOfPawns >> 16) & shouldBeInThatPart;
            plusToOrigin = whiteDown ? 16 : -16;
            moveDocStringCreation(G, moves, plusToOrigin,
                    0, shouldBeInThatPart, true, pawnMoves, wV, wG);

            //Jobbra üt
            shouldBeInThatPart = HITTABLE_BY_BLACK & emPassantPossibility;

            pawnMoves = (whiteDown ? (theBitBoardOfPawns << 7 & ~COL_A) : (theBitBoardOfPawns >> 9 & ~COL_H))
                        & shouldBeInThatPart;
            plusToOrigin = whiteDown ? 7 : -9;
            cantCountThatPart = whiteDown ? ~COL_A : ~COL_H;
            moveDocStringCreation(G, moves, plusToOrigin,
                    cantCountThatPart, shouldBeInThatPart, true, pawnMoves, wV, wG);

            //Balra üt
            pawnMoves = (whiteDown ? (whitePawn << 9 & ~COL_H) : (whitePawn >> 7 & ~COL_A))
                        & shouldBeInThatPart;
            plusToOrigin = whiteDown ? 9 : -7;
            cantCountThatPart = whiteDown ? ~COL_H : ~COL_A;
            moveDocStringCreation(G, moves, plusToOrigin,
                    cantCountThatPart, shouldBeInThatPart, true, pawnMoves, wV, wG);
        }else {
            //Előre sima 1
            shouldBeInThatPart = EMPTY;
            pawnMoves = (whiteDown ? theBitBoardOfPawns >> 8 : theBitBoardOfPawns << 8) & shouldBeInThatPart;
            plusToOrigin = whiteDown ? -8 : 8;
            moveDocStringCreation(G, moves, plusToOrigin,
                    0, shouldBeInThatPart, false, pawnMoves, bV, bG);

            //Előre sima 2
            pawnMoves = (whiteDown ? theBitBoardOfPawns >> 16 : theBitBoardOfPawns << 16) & EMPTY;
            plusToOrigin = whiteDown ? -16 : 16;
            moveDocStringCreation(G, moves, plusToOrigin,
                    0, shouldBeInThatPart, false, pawnMoves, bV, bG);

            //Jobbra üt
            shouldBeInThatPart = HITTABLE_BY_BLACK & emPassantPossibility;
            pawnMoves = (whiteDown ? (theBitBoardOfPawns >> 7 & ~COL_A) : (theBitBoardOfPawns << 9 & ~COL_H))
                        & shouldBeInThatPart;
            plusToOrigin = whiteDown ? -7 : 9;
            cantCountThatPart = whiteDown ? ~COL_A : ~COL_H;
            moveDocStringCreation(G, moves, plusToOrigin,
                    cantCountThatPart, shouldBeInThatPart, false, pawnMoves, bV, bG);

            //Balra üt
            pawnMoves = (whiteDown ? (whitePawn >> 9 & ~COL_H) : (whitePawn << 7 & ~COL_A))
                        & shouldBeInThatPart;
            plusToOrigin = whiteDown ? -9 : 7;
            cantCountThatPart = whiteDown ? ~COL_H : ~COL_A;
            moveDocStringCreation(G, moves, plusToOrigin,
                    cantCountThatPart, shouldBeInThatPart, false, pawnMoves, bV, bG);
        }

        return moves.toString();
    }

    public static String knightMoves(long theBitBoardOfPawns,
                                     long wG, long wH, long wF, long wB, long wV, long wK,
                                     long bG, long bH, long bF, long bB, long bV, long bK){
        //TODO returns the possible end indexes of the knight which stands on the given index
        return null;
    }

    public static long bishopMoves(boolean forWhite, long wF, long bF){
        StringBuilder moves = new StringBuilder();

        long usedF = forWhite ? wF : bF;
        
        long i = usedF & -usedF;
        long possibility = 0L;
        while (i != 0){
            int iLoc = Long.numberOfLeadingZeros(i);
            possibility = diagonalAndAntiDiagonalMoves(iLoc) & (forWhite ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK);
            usedF &= ~i;
            i = usedF & -usedF;
        }
        /*PieceType type,
          StringBuilder moves,
          int plusToOriginPosition,
          long cantCountThatBoardPart,
          long shouldBeInThatPart,
          boolean forWhite,
          long piecesBitBoard,
          long plusBoardQueenOrRook,
          long enemyPawnBoard*/

//        return moves.toString();
        return possibility;
    }

    public static String rookMoves(boolean forWhite, int indexOfASinglePawn, long bitBoardOfPawns){
        //TODO returns the possible end indexes of the rook which stands on the given index
        return null;
    }

    public static String queenMoves(boolean forWhite, int indexOfASinglePawn, long bitBoardOfPawns){
        //TODO returns the possible end indexes of the queen which stands on the given index
        return null;
    }

    public static String kingMoves(boolean forWhite, int indexOfASinglePawn, long bitBoardOfPawns){
        //TODO returns the possible end indexes of the king which stands on the given index
        return null;
    }

    public static long horizontalAndVerticalMoves(int start){
        long s = 1L << start;
        int rowIndex = start / 8, colIndex = (start % 8);
        long firstPart = ((OCCUPIED & RowMasks8[rowIndex]) - (2 * s)) ^
                Long.reverse(Long.reverse(OCCUPIED & RowMasks8[rowIndex]) - (2 * Long.reverse(s)));
        long secondPart = ((OCCUPIED & ColMasks8[colIndex]) - (2 * s)) ^
                Long.reverse(Long.reverse(OCCUPIED & ColMasks8[colIndex]) - (2 * Long.reverse(s)));
        return firstPart & RowMasks8[rowIndex] | secondPart & ColMasks8[colIndex];
    }

    public static long diagonalAndAntiDiagonalMoves(int start){
        long s = 1L << start;
        int rowIndex = start / 8, colIndex = (start % 8);
        long firstPart = ((OCCUPIED & DiagonalMasks8[rowIndex + colIndex]) - (2 * s)) ^
                Long.reverse(Long.reverse(OCCUPIED & DiagonalMasks8[rowIndex + colIndex]) - (2 * Long.reverse(s)));
        long secondPart = ((OCCUPIED & AntiDiagonalMasks8[rowIndex + 7 - colIndex]) - (2 * s)) ^
                Long.reverse(Long.reverse(OCCUPIED & AntiDiagonalMasks8[rowIndex + 7 - colIndex]) - (2 * Long.reverse(s)));
        return firstPart & DiagonalMasks8[rowIndex + colIndex] | secondPart & AntiDiagonalMasks8[rowIndex + 7 - colIndex];
    }

    private static void moveDocStringCreation(PieceType type, StringBuilder moves,
                                              int plusToOriginPosition,
                                              long cantCountThatBoardPart, long shouldBeInThatPart,
                                              boolean forWhite, long piecesBitBoard,
                                              long plusBoardQueenOrRook, long enemyPawnBoard) {
        for (int i = Long.numberOfLeadingZeros(piecesBitBoard); i < 64 - Long.numberOfLeadingZeros(piecesBitBoard); i++) {
            if (
                    (1L << i & shouldBeInThatPart) == 1 &&
                    (1L << i & cantCountThatBoardPart) != 1 &&
                            (piecesBitBoard << i & 1) == 1
            ){
                appendTypeAndIndexes(moves, type.toString(forWhite), i, i + plusToOriginPosition);
                appendAndDoCastleChangesIfThereWere(moves, type.toString(forWhite), i, i + plusToOriginPosition, plusBoardQueenOrRook);
                appendEmPassantAutIfThereWas(type.toString(forWhite), moves, forWhite,
                                             i, i + plusToOriginPosition, enemyPawnBoard);
                doPawnPromotionChangesIfThereWere(
                        type.toString(forWhite),
                        i + plusToOriginPosition, forWhite,
                        piecesBitBoard, plusBoardQueenOrRook
                );
                moves.append('-');
            }
        }
    }

    private static void appendTypeAndIndexes(StringBuilder moves, String type, int startIndex, int endIndex){
        moves.append(type);
        moves.append(startIndex);
        moves.append(endIndex);
    }

    private static void appendAndDoCastleChangesIfThereWere(StringBuilder moves, String type,
                                                            int startIndex, int endIndex, long rookBoard){
        if (isItCastle(type, startIndex, endIndex)){

        }
        if (itWasRookMove(type)){

        }
    }

    private static void doPawnPromotionChangesIfThereWere(String type, int endIndex, boolean forWhite,
                                                          long pawnBoard, long queenBoard){
        if (itIsPawnPromotion(type, endIndex, forWhite)){
            changeTypeToQueen(endIndex, pawnBoard, queenBoard);
        }
    }

    private static void appendEmPassantAutIfThereWas(String type, StringBuilder moves, boolean forWhite,
                                                     int startIndex, int endIndex, long enemyPawns){
        if ("G".equals(type) || "g".equals(type)){
            if (Math.abs(startIndex - endIndex) == 2){
                long plusSidePossibility = 1L << (endIndex + 1) & enemyPawns & ~COL_A;
                long minusSidePossibility = 1L << (endIndex - 1) & enemyPawns & ~COL_H;
                if (plusSidePossibility == 1 || minusSidePossibility == 1){
                    moves.append(
                            whiteDown ? (forWhite ? (endIndex - 8) : (endIndex + 8)) :
                                        (forWhite ? (endIndex + 8) : (endIndex - 8))
                    );
                }
            }
        }
    }

    private static boolean isItCastle(String type, int startIndex, int endIndex){
        return ("K".equals(type) || "k".equals(type)) && 2 == Math.abs(startIndex - endIndex);
    }

    private static boolean itWasRookMove(String type){
        return "B".equals(type) || "b".equals(type);
    }

    private static boolean itIsPawnPromotion(String type, int endIndex, boolean forWhite){
        if ("G".equals(type) || "g".equals(type)){
            return forWhite ? (1L << endIndex & ROW_8) == 1 : (1L << endIndex & ROW_1) == 1;
        }
        return false;
    }

    private static void changeTypeToQueen(int indexOfPermPawn, long pawnBoard, long queenBoard){
        pawnBoard &= ~(1L << indexOfPermPawn);
        queenBoard |= 1L << indexOfPermPawn;
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
