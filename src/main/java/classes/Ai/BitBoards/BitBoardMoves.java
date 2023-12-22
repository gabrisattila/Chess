package classes.Ai.BitBoards;

import classes.Game.I18N.Location;
import lombok.*;

import java.util.Arrays;

import static classes.Ai.BitBoards.BBVars.*;
import static classes.Ai.BitBoards.BitBoards.*;
import static classes.Game.I18N.VARS.FINALS.abc;
import static classes.Game.I18N.VARS.FINALS.pieceImagesForLog;
import static classes.Game.I18N.VARS.MUTABLE.*;

@Getter
@Setter
public class BitBoardMoves {

    //region Base Possibilities

    public static long pawnSimpleSteps(int forWhite, int from){
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
        } else { // for black pawns
            if ((whiteDown ? bitBoard >> 8 : bitBoard << 8) != 0){
                possibility |= (whiteDown ? bitBoard >> 8 : bitBoard << 8);
            }
            if ((whiteDown ? bitBoard >> 16 : bitBoard << 16) != 0 && (bitBoard & ROW_7) != 0) {
                possibility |= (whiteDown ? bitBoard >> 16 : bitBoard << 16);
            }
        }
        return possibility;
    }

    private static long pawnAttacks(int forWhite, int from){
        long possibility = 0L;
        long bitBoard = 0L;
        bitBoard = setBit(bitBoard, from);

        if (forWhite == 1){
            if ((whiteDown ? (bitBoard << 7 & ~COL_A) : (bitBoard >> 9 & ~COL_H)) != 0) {
                possibility |= (whiteDown ? (bitBoard << 7 & ~COL_A) : (bitBoard >> 9 & ~COL_H));
            }
            if ((whiteDown ? (bitBoard << 9 & ~COL_H) : (bitBoard >> 7 & ~COL_A)) != 0) {
                possibility |= (whiteDown ? (bitBoard << 9 & ~COL_H) : (bitBoard >> 7 & ~COL_A));
            }
        } else {

            if ((whiteDown ? (bitBoard >> 7 & ~COL_H) : (bitBoard << 9 & ~COL_A)) != 0) {
                possibility |= (whiteDown ? (bitBoard >> 7 & ~COL_H) : (bitBoard << 9 & ~COL_A));
            }
            if ((whiteDown ? (bitBoard >> 9 & ~COL_A) : (bitBoard << 7 & ~COL_H)) != 0) {
                possibility |= (whiteDown ? (bitBoard >> 9 & ~COL_A) : (bitBoard << 7 & ~COL_H));
            }
        }
        return possibility;
    }

    public static long knightPossibilities(int from){
        return removeAB_OR_GH_Cols(from, (from >= 18 ? (KNIGHT_SPAN << (from - 18)) : (KNIGHT_SPAN >> (18 - from))));
    }

    public static long bishopPossibilities(int from, boolean forBase){
        return diagonalAndVerticalMoves(from, forBase);
    }

    public static long rookPossibilities(int from, boolean forBase){
        return horizontalAndVerticalMoves(from, forBase);
    }

    public static long queenPossibilities(int from, boolean forBase){
        return diagonalAndVerticalMoves(from, forBase) | horizontalAndVerticalMoves(from, forBase);
    }

    public static long kingPossibilities(int from){
        return removeAB_OR_GH_Cols(from, (from >= 10 ? KING_SPAN << (from - 10) : KING_SPAN >> (10 - from)));
    }


    private static long diagonalAndVerticalMoves(int from, boolean forBase) {
        long binaryFrom = 1L << from;
        int rowIndex = from / 8, colIndex = (from % 8);
        if (forBase){
            return DiagonalMasks8[rowIndex + colIndex] ^ binaryFrom | AntiDiagonalMasks8[rowIndex + 7 - colIndex] ^ binaryFrom;
        } else {
            long firstPart = ((OCCUPIED & DiagonalMasks8[rowIndex + colIndex]) - (2 * binaryFrom)) ^
                    Long.reverse(Long.reverse(OCCUPIED & DiagonalMasks8[rowIndex + colIndex]) - (2 * Long.reverse(binaryFrom)))
                    & DiagonalMasks8[rowIndex + colIndex];
            long secondPart = ((OCCUPIED & AntiDiagonalMasks8[rowIndex + 7 - colIndex]) - (2 * binaryFrom)) ^
                    Long.reverse(Long.reverse(OCCUPIED & AntiDiagonalMasks8[rowIndex + 7 - colIndex]) - (2 * Long.reverse(binaryFrom)))
                    & AntiDiagonalMasks8[rowIndex + 7 - colIndex];
            return DiagonalMasks8[rowIndex + colIndex] & firstPart | AntiDiagonalMasks8[rowIndex + 7 - colIndex] & secondPart;
        }
    }

    private static long horizontalAndVerticalMoves(int from, boolean forBase){
        long binaryFrom = 1L << from;
        int rowIndex = from / 8, colIndex = (from % 8);
        if (forBase){
            return RowMasks8[rowIndex] ^ binaryFrom | ColMasks8[colIndex] ^ binaryFrom;
        }else {
            long firstPart = ((OCCUPIED & RowMasks8[rowIndex]) - (2 * binaryFrom)) ^
                    Long.reverse(Long.reverse(OCCUPIED & RowMasks8[rowIndex]) - (2 * Long.reverse(binaryFrom)));
            long secondPart = ((OCCUPIED & ColMasks8[colIndex]) - (2 * binaryFrom)) ^
                    Long.reverse(Long.reverse(OCCUPIED & ColMasks8[colIndex]) - (2 * Long.reverse(binaryFrom)));
            return firstPart & RowMasks8[rowIndex] | secondPart & ColMasks8[colIndex];
        }
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
                pawnSimpleStepTable[i][j] = pawnSimpleSteps(i, j);
                pawnAttackTable[i][j] = pawnAttacks(i, j);
                if (i == 0){
                    knightPossibilityTable[j] = knightPossibilities(j);
                    bishopPossibilityTable[j] = bishopPossibilities(j, true);
                    rookPossibilityTable[j] = rookPossibilities(j, true);
                    queenPossibilityTable[j] = queenPossibilities(j, true);
                    kingPossibilityTable[j] = kingPossibilities(j);
                }
            }
        }
        basePossibilities[0] = pawnSimpleStepTable[1];
        basePossibilities[1] = knightPossibilityTable;
        basePossibilities[2] = bishopPossibilityTable;
        basePossibilities[3] = rookPossibilityTable;
        basePossibilities[4] = queenPossibilityTable;
        basePossibilities[5] = kingPossibilityTable;
        basePossibilities[6] = pawnSimpleStepTable[0];
        basePossibilities[7] = knightPossibilityTable;
        basePossibilities[8] = bishopPossibilityTable;
        basePossibilities[9] = rookPossibilityTable;
        basePossibilities[10] = queenPossibilityTable;
        basePossibilities[11] = kingPossibilityTable;

    }

    public static long getPawnBoard(boolean forWhite){
        return bitBoards[forWhite ? wPawnI : bPawnI];
    }

    public static long getKnightBoard(boolean forWhite){
        return bitBoards[forWhite ? wKnightI : bKnightI];
    }

    public static long getBishopBoard(boolean forWhite){
        return bitBoards[forWhite ? wBishopI : bBishopI];
    }

    public static long getRookBoard(boolean forWhite){
        return bitBoards[forWhite ? wRookI : bRookI];
    }

    public static long getQueenBoard(boolean forWhite){
        return bitBoards[forWhite ? wQueenI : bQueenI];
    }

    public static long getKingBoard(boolean forWhite){
        return bitBoards[forWhite ? wKingI : bKingI];
    }

    public static int getSide(boolean forWhite){
        return forWhite ? 1 : 0;
    }

    //endregion

    public static boolean isSquareAttacked(boolean attackerColor, int squareIndex){

        return
                decideWhetherIsAttackedByPawn(attackerColor, squareIndex) ||
                (knightPossibilityTable[squareIndex] & getKnightBoard(attackerColor)) != 0 ||
                (bishopPossibilities(squareIndex, false) & getBishopBoard(attackerColor)) != 0 ||
                (rookPossibilities(squareIndex, false) & getRookBoard(attackerColor)) != 0 ||
                (queenPossibilities(squareIndex, false) & getQueenBoard(attackerColor)) != 0 ||
                (kingPossibilityTable[squareIndex] & getKingBoard(attackerColor)) != 0;
    }

    private static boolean decideWhetherIsAttackedByPawn(boolean attackerColor, int squareIndex){
        boolean attackedByPawn = false;
        long pawnBoardCopy = bitBoards[attackerColor ? wPawnI : bPawnI];
        int i;
        while (pawnBoardCopy != 0){
            i = 63 - Long.numberOfLeadingZeros(pawnBoardCopy);
            if ((pawnAttackTable[getSide(attackerColor)][i] & 1L << squareIndex) != 0)
                attackedByPawn = true;
            pawnBoardCopy = removeBit(pawnBoardCopy, i);
        }
        return attackedByPawn;
    }

    /*
          binary move bits

    0000 0000 0000 0000 0011 1111    source square       Because we can represent 63 squares in 6 bits
    0000 0000 0000 1111 1100 0000    target square       ------------------- || ----------------------
    0000 0000 1111 0000 0000 0000    piece               Because we can represent 12 piece whitePawn (0000) - blackKing 1111
    0000 1111 0000 0000 0000 0000    promoted piece      ----------------------- || ----------------------
    0001 0000 0000 0000 0000 0000    capture flag        Is move was capture
    0100 0000 0000 0000 0000 0000    emPassant flag      Is move was emPassant
    1000 0000 0000 0000 0000 0000    castling flag       Is move was castling

    Decoding helpers in order:
    0x3f
    0xfc0
    0xf000
    0xf0000
    0x100000
    0x200000
    0x400000
    0x800000
*/

    //region Create And Decode Move

    private static Integer createMove(int from, int to, int what, int promotion,
                                  boolean wasCapture, boolean wasEmPassant, boolean wasCastling){

        return
                (from) |
                (to << 6) |
                (what << 12) |
                (promotion << 16) |
                (wasCapture ? 1 << 20 : 0) |
                (wasEmPassant ? 1 << 22 : 0) |
                (wasCastling ? 1 << 23 : 0);
    }

    public static int getFrom(int move){
        return move & 0x3f;
    }

    public static int getTo(int move){
        return (move & 0xfc0) >> 6;
    }

    public static int getWhat(int move){
        return (move & 0xf000) >> 12;
    }

    private static int getPromotion(int move){
        return (move & 0xf0000) >> 16;
    }

    private static boolean isCapture(int move){
        return (move & 0x100000) != 0;
    }

    public static boolean isEmPassant(int move){
        return (move & 0x400000) != 0;
    }

    public static boolean isCastling(int move){
        return (move & 0x800000) != 0;
    }

    public static void addMove(int move){
        movesInATurn[moveCount] = move;
        moveCount++;
    }

    public static int[] generateMoves(boolean forWhite){

        HITTABLE_BY_BLACK = getPawnBoard(true) | getKnightBoard(true) | getBishopBoard(true) | getRookBoard(true) | getQueenBoard(true);
        HITTABLE_BY_WHITE = getPawnBoard(false) | getKnightBoard(false) | getBishopBoard(false) | getRookBoard(false) | getQueenBoard(false);
        OCCUPIED = HITTABLE_BY_BLACK | HITTABLE_BY_WHITE | getKingBoard(true) | getKingBoard(false);
        EMPTY = ~OCCUPIED;

        int moveCount = 0;
        int[] moves = new int[256];

        int from, to;
        long currentBitBoardCopy, possibility = 0;
        long shouldBePartOfMove = 0;
        for (int piece : pieceIndexes) {
            if (forWhite == (piece <= wKingI)){
                currentBitBoardCopy = bitBoards[piece];
                while (currentBitBoardCopy != 0) {
                    from = 63 - Long.numberOfLeadingZeros(currentBitBoardCopy);
                    switch (piece) {
                        case wPawnI -> {
                            possibility = pawnSimpleStepTable[1][from];
                        }
                        case bPawnI -> {
                            possibility = pawnSimpleStepTable[0][from];
                        }
                        case wKnightI -> {
                            possibility = knightPossibilityTable[from];
                            shouldBePartOfMove = shouldBePartOf(true);
                        }
                        case bKnightI -> {
                            possibility = knightPossibilityTable[from];
                            shouldBePartOfMove = shouldBePartOf(false);
                        }
                        case wBishopI -> {
                            possibility = bishopPossibilities(from, false);
                            shouldBePartOfMove = shouldBePartOf(true);
                        }
                        case bBishopI -> {
                            possibility = bishopPossibilities(from, false);
                            shouldBePartOfMove = shouldBePartOf(false);
                        }
                        case wRookI -> {
                            possibility = rookPossibilities(from, false);
                            shouldBePartOfMove = shouldBePartOf(true);
                        }
                        case bRookI -> {
                            possibility = rookPossibilities(from, false);
                            shouldBePartOfMove = shouldBePartOf(false);
                        }
                        case wQueenI -> {
                            possibility = queenPossibilities(from, false);
                            shouldBePartOfMove = shouldBePartOf(true);
                        }
                        case bQueenI -> {
                            possibility = queenPossibilities(from, false);
                            shouldBePartOfMove = shouldBePartOf(false);
                        }
                        case wKingI -> {
                            possibility = kingPossibilityTable[from];
                            shouldBePartOfMove = shouldBePartOf(true);
                            possibility = calcKingPossibility(piece, possibility);
                        }
                        case bKingI -> {
                            possibility = kingPossibilityTable[from];
                            shouldBePartOfMove = shouldBePartOf(false);
                            possibility = calcKingPossibility(piece, possibility);
                        }
                    }
                    while (possibility != 0) {
                        to = Long.numberOfTrailingZeros(possibility);

                        //Calc what should be part of pawn range
                        if (piece == wPawnI || piece == bPawnI) {
                            //Simple 1 forward
                            if (8 == Math.abs(from - to)) {
                                shouldBePartOfMove = EMPTY;
                            }
                            //2 forward
                            else if (16 == Math.abs(from - to)) {
                                int minus = whiteDown ? (forWhite ? 8 : -8) : (forWhite ? -8 : 8);
                                shouldBePartOfMove = EMPTY & (piece == wPawnI ? (ROW_4 | ROW_3) : (ROW_5 | ROW_6)) & 1L << (to - minus);
                            }
                            //Hit and if there's emPassant possibility, combine it
                            else {
                                shouldBePartOfMove = (piece == wPawnI ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK);
                                if (bbEmPassant != -1) {
                                    if (piece == wPawnI && (1L << bbEmPassant & ROW_6) != 0) { // wh
                                        shouldBePartOfMove |= 1L << bbEmPassant;
                                    } else if (piece == bPawnI && (1L << bbEmPassant & ROW_3) != 0) {
                                        shouldBePartOfMove |= 1L << bbEmPassant;
                                    }
                                }
                            }
                        }

                        if ((1L << to & shouldBePartOfMove) != 0) {

                            //Handling pawn promotion
                            int promotion = 0;
                            if (piece == wPawnI && (1L << to & ROW_8) != 0) {
                                promotion = wQueenI;
                            } else if (piece == bPawnI && (1L << to & ROW_1) != 0) {
                                promotion = bQueenI;
                            }

                            //Add new move to move list
                            moves[moveCount] =
                                    createMove(from, to, piece, promotion,
                                            (1L << to & (piece <= wKingI ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK)) != 0,
                                            (1L << to & EMPTY & 1L << bbEmPassant) != 0 && (piece == wPawnI || piece == bPawnI),
                                            (piece == wKingI || piece == bKingI) && 2 == Math.abs(from - to)
                                    );
                            moveCount++;
                        }
                        possibility = removeBit(possibility, to);
                    }
                    currentBitBoardCopy = removeBit(currentBitBoardCopy, from);
                }
            }
        }
        return Arrays.stream(moves).filter(m -> getFrom(m) != getTo(m)).toArray();
    }

    private static long shouldBePartOf(boolean forWhite){
        return (forWhite ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK) | EMPTY;
    }

    public static long calcKingPossibility(int piece, long possibility){
        if ((castle & (piece == wKingI ? wK : bK)) == 0)
            possibility &= ~(1L << (63 - Long.numberOfLeadingZeros(bitBoards[piece]) - (whiteDown ? 2 : -2)));

        if ((castle & (piece == wKingI ? wQ : bQ)) == 0)
            possibility &= ~(1L << (63 - Long.numberOfLeadingZeros(bitBoards[piece]) - (whiteDown ? -2 : 2)));

        return possibility;
    }

    //endregion

    //region Make and Undo Moves

    public static void copyPosition(){
        bitBoardsCopy.push(Arrays.copyOf(bitBoards, bitBoards.length));
        castleCopy.push(castle);
        bbEmPassantCopy.push(bbEmPassant);
        whiteToPlayCopy.push(whiteToPlay);
    }

    public static void undoMove(){
        bitBoards = bitBoardsCopy.pop();
        castle = castleCopy.pop();
        bbEmPassant = bbEmPassantCopy.pop();
        whiteToPlay = whiteToPlayCopy.pop();
    }

    /**
     * @param move the move we want to make
     * @return true if after the move our king isn't in check - it's a legal move
     */
    public static boolean makeMove(int move){
        int what = getWhat(move);
        if ((what <= wKingI == whiteToPlay)){
            int from = getFrom(move);
            int to = getTo(move);
            int promotion = getPromotion(move);
            boolean capture = isCapture(move);
            boolean emPassant = isEmPassant(move);
            boolean castling = isCastling(move);

            bitBoards[what] = removeBit(bitBoards[what], from);
            bitBoards[what] = setBit(bitBoards[what], to);

            //move is capture
            if (capture) {
                for (int
                     piece = whiteToPlay ? bPawnI : wPawnI;
                     piece <= (whiteToPlay ? bKingI : wKingI);
                     piece++)
                {
                    if (getBit(bitBoards[piece], to) != 0) {
                        bitBoards[piece] = removeBit(bitBoards[piece], to);
                        break;
                    }
                }
            }

            //move is pawn promotion
            if (promotion != 0){
                bitBoards[what] = removeBit(bitBoards[what], to);
                bitBoards[promotion] = setBit(bitBoards[promotion], to);
            }

            //move is emPassant action
            if (emPassant) {
                bitBoards[whiteToPlay ? bPawnI : wPawnI] =
                        removeBit(getPawnBoard(!whiteToPlay), to + (whiteDown ? (whiteToPlay ? -8 : 8) : (whiteToPlay ? 8 : -8)));
            }

            //move is castling action
            if (castling) {
                int rookFrom = -1;
                int rookTo = -1;
                if (to == 1){ // whiteDown wKC
                    rookFrom = 0;
                    rookTo = 2;
                    castle &= wK;
                } else if (to == 5) { // whiteDown wQC
                    rookFrom = 7;
                    rookTo = 4;
                    castle &= wQ;
                } else if (to == 57) { // whiteDown bKC
                    rookFrom = 56;
                    rookTo = 58;
                    castle &= bK;
                } else if (to == 61) { // whiteDown bQC
                    rookFrom = 63;
                    rookTo = 60;
                    castle &= bQ;
                } else if (to == 62) { // !whiteDown wKC
                    rookFrom = 63;
                    rookTo = 61;
                    castle &= wK;
                } else if (to == 58) { // !whiteDown wQC
                    rookFrom = 56;
                    rookTo = 59;
                    castle &= wQ;
                } else if (to == 6) { // !whiteDown bKC
                    rookFrom = 7;
                    rookTo = 5;
                    castle &= bK;
                } else if (to == 2) { // !whiteDown bQC
                    rookFrom = 0;
                    rookTo = 3;
                    castle &= bQ;
                }
                bitBoards[whiteToPlay ? wRookI : bRookI] = removeBit(getRookBoard(whiteToPlay), rookFrom);
                bitBoards[whiteToPlay ? wRookI : bRookI] = setBit(getRookBoard(whiteToPlay), rookTo);
            }

            if (what == wPawnI){
                if (16 == Math.abs(from - to) && (
                        (getPawnBoard(false) & (1L << to - 1 & ~(whiteDown ? COL_A : COL_H))) != 0 ||
                                (getPawnBoard(false) & (1L << to + 1 & ~(whiteDown ? COL_H : COL_A))) != 0)
                ){
                    bbEmPassant = whiteDown ? to - 8 : to + 8;
                }
            } else if (what == bPawnI) {
                if (16 == Math.abs(from - to) && (
                        (getPawnBoard(true) & (1L << to - 1 & ~(whiteDown ? COL_A : COL_H))) != 0 ||
                                (getPawnBoard(true) & (1L << to + 1 & ~(whiteDown ? COL_H : COL_A))) != 0)
                ){
                    bbEmPassant = whiteDown ? to + 8 : to - 8;
                }
            }

            if (what == wKingI){
                castle &= wK;
                castle &= wQ;
            } else if (what == bKingI) {
                castle &= bK;
                castle &= bQ;
            } else if (what == wRookI) {
                if (whiteDown){
                    if (from == 0)
                        castle &= wK;
                    else
                        castle &= wQ;
                }else {
                    if (from == 63)
                        castle &= wK;
                    else
                        castle &= wQ;
                }
            } else if (what == bRookI) {
                if (whiteDown){
                    if (from == 56)
                        castle &= bK;
                    else
                        castle &= bQ;
                }else {
                    if (from == 0)
                        castle &= bK;
                    else
                        castle &= bQ;
                }
            }

            whiteToPlay = !whiteToPlay;

            if (getKingBoard(!whiteToPlay) != 0 &&
                            isSquareAttacked(whiteToPlay, 63 - Long.numberOfLeadingZeros(getKingBoard(!whiteToPlay)))
            ){
                undoMove();
                return false;
            }else
                return true;
        }
        return false;
    }

    private static Location bitBoardIndexToLocation(int index){
        int i = (index / 8) + 1, j = 7 - index % 8;
        return new Location(i, j);
    }

    //endregion

    //region Print move

    public static String moveToString(int move){
        String m = "";
        m += getWhat(move) <= wKingI ? "White " : "Black ";
        m += pieceImagesForLog[getWhat(move)];
        m += " goes from: ";
        m += intToSquare(getFrom(move));
        m += " to: ";
        m += intToSquare(getTo(move));
        m += ".";
        m += getPromotion(move) == 0 ? "" : " And become " + pieceImagesForLog[getPromotion(move)];
        m += isCapture(move) ? " Caused capture. " : "";
        m += isEmPassant(move) ? " It was em-passant move." : "";
        m += isCastling(move) ? " It was castle. " : "";
        m += "\n";
        return m;
    }

    private static String intToSquare(int move){
        int i = move / 8;
        int j = 7 - (move % 8);
        i++;
        String m = "";
        m += abc.get(j);
        m += i;
        return m;
    }

    public static String intToBitString(int i){
        StringBuilder s = new StringBuilder();
        for (int j = 31; j >= 0; j--) {
            if ((1 << j & i) != 0)
                s.append('1');
            else
                s.append('0');
            if (j % 4 == 0)
                s.append('\n');
        }
        return s.toString();
    }

    public static String moveListToString(int[] movesInATurn){
        StringBuilder moveList = new StringBuilder();

        int index = 0;
        for (int move : movesInATurn) {
            if (getFrom(move) != getTo(move)){
                moveList.append(index).append(". move in list: ").append(moveToString(move));
                moveList.append("---------------------\n");
                index++;
            }
        }

        return moveList.toString();
    }

    //endregion

}
