package classes.Ai.BitBoards;

import lombok.*;

import static classes.Ai.BitBoards.BBVars.*;
import static classes.Ai.BitBoards.BitBoards.*;
import static classes.Game.I18N.VARS.MUTABLE.whiteDown;

@Getter
@Setter
public class BitBoardMoves {

    public static long pawnPossibilities(int forWhite, int from){
        long possibility = 0L;
        long bitBoard = 0L;
        bitBoard = setBit(bitBoard, from);

        if (forWhite == 1){ // for white pawns
            if ((whiteDown ? bitBoard << 8 : bitBoard >> 8) != 0) {
                possibility |= (whiteDown ? bitBoard << 8 : bitBoard >> 8);
            }
            if ((whiteDown ? bitBoard << 16 : bitBoard >> 16) != 0 && (bitBoard & ROW_2) != 0) {
                possibility |= (whiteDown ? bitBoard << 16 : bitBoard >> 16);
            }
            if ((whiteDown ? (bitBoard << 7 & ~COL_A) : (bitBoard >> 9 & ~COL_H)) != 0) {
                possibility |= (whiteDown ? (bitBoard << 7 & ~COL_A) : (bitBoard >> 9 & ~COL_H));
            }
            if ((whiteDown ? (bitBoard << 9 & ~COL_H) : (bitBoard >> 7 & ~COL_A)) != 0) {
                possibility |= (whiteDown ? (bitBoard << 9 & ~COL_H) : (bitBoard >> 7 & ~COL_A));
            }
        } else { // for black pawns
            if ((whiteDown ? bitBoard >> 8 : bitBoard << 8) != 0){
                possibility |= (whiteDown ? bitBoard >> 8 : bitBoard << 8);
            }
            if ((whiteDown ? bitBoard >> 16 : bitBoard << 16) != 0 && (bitBoard & ROW_7) != 0) {
                possibility |= (whiteDown ? bitBoard >> 16 : bitBoard << 16) & ROW_7;
            }
            if ((whiteDown ? (bitBoard >> 7 & ~COL_A) : (bitBoard << 9 & ~COL_H)) != 0) {
                possibility |= (whiteDown ? (bitBoard >> 7 & ~COL_A) : (bitBoard << 9 & ~COL_H));
            }
            if ((whiteDown ? (bitBoard >> 9 & ~COL_H) : (bitBoard << 7 & ~COL_A)) != 0) {
                possibility |= (whiteDown ? (bitBoard >> 9 & ~COL_H) : (bitBoard << 7 & ~COL_A));
            }
        }
        return possibility;
    }

    public static long knightPossibilities(int from){
        return removeAB_OR_GH_Cols(from, (from >= 18 ? (KNIGHT_SPAN << (from - 18)) : (KNIGHT_SPAN >> (18 - from))));
    }

    public static long bishopPossibilities(int from){
        return diagonalAndVerticalMoves(from);
    }

    public static long rookPossibilities(int from){
        return horizontalAndVerticalMoves(from);
    }

    public static long queenPossibilities(int from){
        return diagonalAndVerticalMoves(from) | horizontalAndVerticalMoves(from);
    }

    public static long kingPossibilities(int from){
        return removeAB_OR_GH_Cols(from, (from >= 9 ? KING_SPAN << (from - 9) : KING_SPAN >> (9 - from)));
    }


    private static long diagonalAndVerticalMoves(int from){
        long binaryFrom = 1L << from;
        int rowIndex = from / 8, colIndex = (from % 8);
        return DiagonalMasks8[rowIndex + colIndex] ^ binaryFrom | AntiDiagonalMasks8[rowIndex + 7 - colIndex] ^ binaryFrom;
    }

    private static long horizontalAndVerticalMoves(int from){
        long binaryFrom = 1L << from;
        int rowIndex = from / 8, colIndex = (from % 8);
        return RowMasks8[rowIndex] ^ binaryFrom | ColMasks8[colIndex] ^ binaryFrom;
    }

    private static long removeAB_OR_GH_Cols(int from, long possibility){
        if ((1L << from & COL_GH) != 0){
            possibility &= ~COL_AB;
        } else if ((1L << from & COL_AB) != 0) {
            possibility &= ~COL_GH;
        }
        return possibility;
    }

    public static void fillBaseBitBoardPossibilities(){
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 64; j++) {
                pawnPossibilityTable[i][j] = pawnPossibilities(i, j);
                if (i == 0){
                    knightPossibilityTable[j] = knightPossibilities(j);
                    bishopPossibilityTable[j] = bishopPossibilities(j);
                    rookPossibilityTable[j] = rookPossibilities(j);
                    queenPossibilityTable[j] = queenPossibilities(j);
                    kingPossibilityTable[j] = kingPossibilities(j);
                }
            }
        }
        basePossibilities[0] = pawnPossibilityTable[1];
        basePossibilities[1] = knightPossibilityTable;
        basePossibilities[2] = bishopPossibilityTable;
        basePossibilities[3] = rookPossibilityTable;
        basePossibilities[4] = queenPossibilityTable;
        basePossibilities[5] = kingPossibilityTable;
        basePossibilities[6] = pawnPossibilityTable[0];
        basePossibilities[7] = knightPossibilityTable;
        basePossibilities[8] = bishopPossibilityTable;
        basePossibilities[9] = rookPossibilityTable;
        basePossibilities[10] = queenPossibilityTable;
        basePossibilities[11] = kingPossibilityTable;

    }
}
