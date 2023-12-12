package classes.Game.Model.Structure.BitBoard;

import java.util.ArrayList;
import java.util.Arrays;

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

        String moves = pawnMoves(maxNeeded, maxNeeded ? wG : bG, emPassantChance, wG, bG);
        moves += knightMoves(maxNeeded, wH, bH);
        moves += bishopMoves(maxNeeded, wF, bF);
        moves += rookMoves(maxNeeded, wB, bB);
        moves += queenMoves(maxNeeded, wV, bV);
//        moves += kingMoves();
        int moveCount = moves.split("_").length;
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
    public static String pawnMoves(boolean forWhite, long theBitBoardOfPawns, int emPassantChance, long wG, long bG){

        long emPassantPossibility = 1L << emPassantChance;

        StringBuilder moves = new StringBuilder();

        long pawnMoves;
        int plusToGetOrigin;
        long shouldBeInThatPart;

        if (forWhite){
            //Előre sima 1
            shouldBeInThatPart = EMPTY;
            pawnMoves = (whiteDown ? theBitBoardOfPawns << 8 : theBitBoardOfPawns >> 8) & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? -8 : 8;
            moveDocStringPawn(true, moves, pawnMoves, plusToGetOrigin, bG);

//          Előre sima 2
            shouldBeInThatPart = EMPTY & ROW_4;
            pawnMoves = (whiteDown ? theBitBoardOfPawns << 16 : theBitBoardOfPawns >> 16) & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? -16 : 16;
            moveDocStringPawn(true, moves, pawnMoves, plusToGetOrigin, bG);

            //Jobbra üt
            shouldBeInThatPart = HITTABLE_BY_BLACK & emPassantPossibility;

            pawnMoves = (whiteDown ? (theBitBoardOfPawns << 7 & ~COL_A) : (theBitBoardOfPawns >> 9 & ~COL_H))
                        & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? -7 : 9;
            moveDocStringPawn(true, moves, pawnMoves, plusToGetOrigin, bG);

            //Balra üt
            pawnMoves = (whiteDown ? (whitePawn << 9 & ~COL_H) : (whitePawn >> 7 & ~COL_A))
                        & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? -9 : 7;
            moveDocStringPawn(true, moves, pawnMoves, plusToGetOrigin, bG);

        }else {
            //Előre sima 1
            shouldBeInThatPart = EMPTY;
            pawnMoves = (whiteDown ? theBitBoardOfPawns >> 8 : theBitBoardOfPawns << 8) & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? 8 : -8;
            moveDocStringPawn(false, moves, pawnMoves, plusToGetOrigin, wG);

            //Előre sima 2
            shouldBeInThatPart = EMPTY & ROW_5;
            pawnMoves = (whiteDown ? theBitBoardOfPawns >> 16 : theBitBoardOfPawns << 16) & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? 16 : -16;
            moveDocStringPawn(false, moves, pawnMoves, plusToGetOrigin, wG);

            //Jobbra üt
            shouldBeInThatPart = HITTABLE_BY_BLACK & emPassantPossibility;
            pawnMoves = (whiteDown ? (theBitBoardOfPawns >> 7 & ~COL_A) : (theBitBoardOfPawns << 9 & ~COL_H))
                        & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? 7 : -9;
            moveDocStringPawn(false, moves, pawnMoves, plusToGetOrigin, wG);

            //Balra üt
            pawnMoves = (whiteDown ? (whitePawn >> 9 & ~COL_H) : (whitePawn << 7 & ~COL_A))
                        & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? 9 : -7;
            moveDocStringPawn(false, moves, pawnMoves, plusToGetOrigin, wG);
        }

        return moves.toString();
    }

    public static String knightMoves(boolean forWhite, long wH, long bH){
        String type = forWhite ? "H" : "h";
        return moveDocStringTisztek(type, wH, bH);
    }

    public static String bishopMoves(boolean forWhite, long wF, long bF){
        return slidingPieceMoves(F.toString(forWhite), wF, bF);
    }

    public static String rookMoves(boolean forWhite, long wB, long bB){
        return slidingPieceMoves(B.toString(forWhite), wB, bB);
    }

    public static String queenMoves(boolean forWhite, long wV, long bV){
        return slidingPieceMoves(V.toString(forWhite), wV, bV);
    }

    public static String kingMoves(boolean forWhite, int indexOfASinglePawn, long bitBoardOfPawns){
        //TODO returns the possible end indexes of the king which stands on the given index
        return null;
    }

    public static String slidingPieceMoves(String type, long w, long b){
        return moveDocStringTisztek(type, w, b);
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
        long binaryStart = 1L << start;
        int rowIndex = start / 8, colIndex = (start % 8);
        long firstPart = ((OCCUPIED & DiagonalMasks8[rowIndex + colIndex]) - (2 * binaryStart)) ^
                Long.reverse(Long.reverse(OCCUPIED & DiagonalMasks8[rowIndex + colIndex]) - (2 * Long.reverse(binaryStart)));
        long secondPart = ((OCCUPIED & AntiDiagonalMasks8[rowIndex + 7 - colIndex]) - (2 * binaryStart)) ^
                Long.reverse(Long.reverse(OCCUPIED & AntiDiagonalMasks8[rowIndex + 7 - colIndex]) - (2 * Long.reverse(binaryStart)));
        return firstPart & DiagonalMasks8[rowIndex + colIndex] | secondPart & AntiDiagonalMasks8[rowIndex + 7 - colIndex];
    }

    private static void moveDocStringPawn(boolean forWhite, StringBuilder moves, long used, int difference, long enemyPawn){
        long possibility = used & -used;

        String type = forWhite ? "G" : "g";
        while (possibility != 0)
        {
            int index = Long.numberOfTrailingZeros(possibility);
            moves.append(type);
            moves.append('-');
            moves.append(index + difference);
            moves.append('-');
            moves.append(index);
            appendEmPassantAutIfThereWas(type, moves, index + difference, index, enemyPawn);
            moves.append('_');
            used &= ~possibility;
            possibility = used & -used;
        }
    }

    private static String moveDocStringTisztek(String type, long w, long b){
        StringBuilder moves = new StringBuilder();

        boolean forWhite = Character.isUpperCase(type.charAt(0));
        long used = forWhite ? w : b;

        long i = used & -used;
        long possibility;
        while (i != 0){
            int startLoc = 63 - Long.numberOfLeadingZeros(i);

            possibility = movingPossibilities(type, startLoc);
            long j = possibility & -possibility;

            while (j != 0)
            {
                int endLoc = Long.numberOfTrailingZeros(j);
                moves.append(type);
                moves.append('-');
                moves.append(startLoc);
                moves.append('-');
                moves.append(endLoc);
                moves.append(rookMoveNote(type, startLoc));
                moves.append('_');
                possibility &= ~j;
                j = possibility & -possibility;
            }
            used &= ~i;
            i = used & -used;
        }

        return moves.toString();
    }

    private static long movingPossibilities(String type, int startLoc){
        long possibility = 0L;
        boolean forWhite = Character.isUpperCase(type.charAt(0));
        if ("H".equals(type) || "h".equals(type)){
            possibility = startLoc >= 18 ? (KNIGHT_SPAN << (startLoc - 18)) : (KNIGHT_SPAN >> (18 - startLoc));
            if ((1L << startLoc & COL_GH) != 0){
                possibility &= ~COL_AB;
            } else if ((1L << startLoc & COL_AB) != 0) {
                possibility &= ~COL_GH;
            }
        }else {
            switch (type.charAt(0)){
                case 'F', 'f' -> possibility |= diagonalAndAntiDiagonalMoves(startLoc);
                case 'B', 'b' -> possibility |= horizontalAndVerticalMoves(startLoc);
                default -> possibility |= diagonalAndAntiDiagonalMoves(startLoc) | horizontalAndVerticalMoves(startLoc);
            }
        }
        possibility &= (forWhite ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK) | EMPTY;
        return possibility;
    }

    private static String rookMoveNote(String type, int startLoc){
        if (("B".equals(type) || "b".equals(type)) && Arrays.stream(corners).anyMatch(c -> c == startLoc)){
            if ((1L << startLoc & KING_SIDE) != 0){
                return Character.isUpperCase(type.charAt(0)) ? "-K" : "-k";
            } else if ((1L << startLoc & QUEEN_SIDE) != 0) {
                return Character.isUpperCase(type.charAt(0)) ? "-V" : "-v";
            }
        }
        return "";
    }

    private static void doPawnPromotionChangesIfThereWere(String type, int endIndex, boolean forWhite,
                                                          long pawnBoard, long queenBoard){
        if (itIsPawnPromotion(type, endIndex, forWhite)){
            changeTypeToQueen(endIndex, pawnBoard, queenBoard);
        }
    }

    private static void appendEmPassantAutIfThereWas(String type, StringBuilder moves,
                                                     int startIndex, int endIndex, long enemyPawns){
        if ("G".equals(type) || "g".equals(type)){
            if (Math.abs(startIndex - endIndex) == 16){
                long plusSidePossibility = 1L << (endIndex + 1) & enemyPawns & (whiteDown ? ~COL_H : ~COL_A);
                long minusSidePossibility = 1L << (endIndex - 1) & enemyPawns & (whiteDown ? ~COL_A : ~COL_H);
                if (plusSidePossibility != 0 || minusSidePossibility != 0){
                    moves.append('-');
                    moves.append(
                            whiteDown ? (Character.isUpperCase(type.charAt(0)) ? (endIndex - 8) : (endIndex + 8)) :
                                        (Character.isUpperCase(type.charAt(0)) ? (endIndex + 8) : (endIndex - 8))
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
