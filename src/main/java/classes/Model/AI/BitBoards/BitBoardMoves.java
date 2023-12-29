package classes.Model.AI.BitBoards;

import classes.Model.Game.I18N.Pair;
import lombok.*;

import java.util.*;

import static classes.Model.AI.Ai.AI.waitOnPause;
import static classes.Model.AI.BitBoards.BBVars.*;
import static classes.Model.AI.BitBoards.BitBoards.*;
import static classes.Model.AI.Evaluation.Evaluator.*;
import static classes.Model.Game.I18N.VARS.FINALS.*;
import static classes.Model.Game.I18N.VARS.MUTABLE.*;

@Getter
@Setter
public class BitBoardMoves {

    //region Possibilities

    public static long pawnSimple(int forWhite, int from){
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

    private static long bishopAttacks(int from, boolean forWhite){
        return diagonalAndAntiDiagonal(
                from,
                (forWhite ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK) | getKingBoard(!forWhite),
                (forWhite ? HITTABLE_BY_BLACK : HITTABLE_BY_WHITE) | getKingBoard(forWhite)
        );
    }

    private static long bishopPossibilities(int from, boolean forWhite){
        return diagonalAndAntiDiagonal(
                from,
                forWhite ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK,
                (forWhite ? HITTABLE_BY_BLACK : HITTABLE_BY_WHITE) | getKingBoard(true) | getKingBoard(false)
        );
    }

    private static long getBishopAttacks(boolean forWhite){
        long currentBitBoard = getBishopBoard(forWhite);
        int index;
        long attacks = 0;
        while (currentBitBoard != 0){
            index = getFirstBitIndex(currentBitBoard);
            attacks |= bishopAttacks(index, forWhite);
            currentBitBoard = removeBit(currentBitBoard, index);
        }
        return attacks;
    }

    private static long rookAttacks(int from, boolean forWhite){
        return horizontalAndVertical(
                from,
                (forWhite ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK) | getKingBoard(!forWhite),
                (forWhite ? HITTABLE_BY_BLACK : HITTABLE_BY_WHITE) | getKingBoard(forWhite)
        );
    }

    private static long rookPossibilities(int from, boolean forWhite){
        return horizontalAndVertical(
                from,
                forWhite ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK,
                (forWhite ? HITTABLE_BY_BLACK : HITTABLE_BY_WHITE) | getKingBoard(true) | getKingBoard(false)
        );
    }

    private static long getRookAttacks(boolean forWhite){
        long currentBitBoard = getRookBoard(forWhite);
        int index;
        long attacks = 0L;
        while(currentBitBoard != 0){
            index = getFirstBitIndex(currentBitBoard);
            attacks |= rookAttacks(index, forWhite);
            currentBitBoard = removeBit(currentBitBoard, index);
        }
        return attacks;
    }

    private static long queenAttacks(int from, boolean forWhite){
        return bishopAttacks(from, forWhite) | rookAttacks(from, forWhite);
    }

    private static long queenPossibilities(int from, boolean forWhite){
        return bishopPossibilities(from, forWhite) | rookPossibilities(from, forWhite);
    }

    private static long getQueenAttacks(boolean forWhite){
        long currentBitBoard = getQueenBoard(forWhite);
        int index;
        long attacks = 0L;
        while (currentBitBoard != 0){
            index = getFirstBitIndex(currentBitBoard);
            attacks |= queenAttacks(index, forWhite);
            currentBitBoard = removeBit(currentBitBoard, index);
        }
        return attacks;
    }

    public static long kingPossibilities(int from){
        return removeAB_OR_GH_Cols(from, (from >= 10 ? KING_SPAN << (from - 10) : KING_SPAN >> (10 - from)));
    }

    private static long diagonalAndAntiDiagonal(int from, long hittable, long block) {

        int fromRow = from / 8, fromCol = (from % 8);
        int row, col;
        long possible = 0;

        for (row = fromRow + 1, col = fromCol + 1; row <= 7 && col <= 7; row++, col++) {
            if (breakInsteadOfBlock(row, col, block))
                break;
            
            possible |= (1L << (row * 8 + col));
            
            if (breakInsteadOfBlock(row, col, hittable))
                break;
        }

        for (row = fromRow - 1, col = fromCol + 1; row >= 0 && col <= 7; row--, col++) {
            if (breakInsteadOfBlock(row, col, block))
                break;
            
            possible |= (1L << (row * 8 + col));
            
            if (breakInsteadOfBlock(row, col, hittable))
                break;
        }

        for (row = fromRow + 1, col = fromCol - 1; row <= 7 && col >= 0; row++, col--) {
            if (breakInsteadOfBlock(row, col, block))
                break;
            
            possible |= (1L << (row * 8 + col));
            
            if (breakInsteadOfBlock(row, col, hittable))
                break;
        }

        for (row = fromRow - 1, col = fromCol - 1; row >= 0 && col >= 0; row--, col--) {
            if (breakInsteadOfBlock(row, col, block))
                break;
            
            possible |= (1L << (row * 8 + col));
            
            if (breakInsteadOfBlock(row, col, hittable))
                break;
        }

        return possible;
    }

    private static long horizontalAndVertical(int from, long hittable, long block){

        int fromRow = from / 8, fromCol = from % 8;
        int row, col;
        long possible = 0L;

        for (row = fromRow + 1; row <= 7; row++) {
            if (breakInsteadOfBlock(row, fromCol, block))
                break;

            possible |= (1L << (row * 8 + fromCol));

            if (breakInsteadOfBlock(row, fromCol, hittable))
                break;
        }

        for (row = fromRow - 1; row >= 0; row--) {
            if (breakInsteadOfBlock(row, fromCol, block))
                break;

            possible |= (1L << (row * 8 + fromCol));

            if (breakInsteadOfBlock(row, fromCol, hittable))
                break;
        }

        for (col = fromCol + 1; col <= 7; col++) {
            if (breakInsteadOfBlock(fromRow, col, block))
                break;

            possible |= (1L << (fromRow * 8 + col));

            if (breakInsteadOfBlock(fromRow, col, hittable))
                break;
        }

        for (col = fromCol - 1; col >= 0; col--) {
            if (breakInsteadOfBlock(fromRow, col, block))
                break;

            possible |= (1L << (fromRow * 8 + col));

            if (breakInsteadOfBlock(fromRow, col, hittable))
                break;
        }

        return possible;
    }

    private static boolean breakInsteadOfBlock(int row, int col, long block){
        return (1L << (row * 8 + col) & block) != 0;
    }

    private static long removeAB_OR_GH_Cols(int from, long possibility){
        if ((1L << from & COL_GH) != 0){
            possibility &= ~COL_AB;
        } else if ((1L << from & COL_AB) != 0) {
            possibility &= ~COL_GH;
        }
        return possibility;
    }

    private static long removeA_Or_H_Cols(int from, long possibility){
        if ((1L << from & COL_A) != 0) {
            possibility &= ~COL_H;
        } else if ((1L << from & COL_H) != 0) {
            possibility &= ~COL_A;
        }
        return possibility;
    }

    public static void fillBaseBitBoardPossibilities(){
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 64; j++) {
                pawnSimpleStepTable[i][j] = pawnSimple(i, j);
                pawnAttackTable[i][j] = pawnAttacks(i, j);
                if (i == 0){
                    knightPossibilityTable[j] = knightPossibilities(j);
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
    
    private static void setHittableOccupiedEmpty(){
        HITTABLE_BY_BLACK = getPawnBoard(true) | getKnightBoard(true) | getBishopBoard(true) | getRookBoard(true) | getQueenBoard(true);
        HITTABLE_BY_WHITE = getPawnBoard(false) | getKnightBoard(false) | getBishopBoard(false) | getRookBoard(false) | getQueenBoard(false);
        OCCUPIED = bitBoards[wPawnI] | bitBoards[wKnightI] | bitBoards[wBishopI] | bitBoards[wRookI] | bitBoards[wQueenI] | bitBoards[wKingI] |
                    bitBoards[bPawnI] | bitBoards[bKnightI] | bitBoards[bBishopI] | bitBoards[bRookI] | bitBoards[bQueenI] | bitBoards[bKingI];
        EMPTY = ~OCCUPIED;
    }

    public static boolean isSquareAttacked(boolean attackerColor, int squareIndex){
        setHittableOccupiedEmpty();
        if (squareIndex < 0 || squareIndex > 63)
            return false;
        else
            return
                attackedByPawn(attackerColor, squareIndex) ||
                (knightPossibilityTable[squareIndex] & getKnightBoard(attackerColor)) != 0 ||
                (getBishopAttacks(attackerColor) & 1L << squareIndex) != 0 ||
                (getRookAttacks(attackerColor) & 1L << squareIndex) != 0 ||
                (getQueenAttacks(attackerColor) & 1L << squareIndex) != 0 ||
                attackedByKing(attackerColor, squareIndex);
    }

    private static boolean attackedByPawn(boolean attackerColor, int squareIndex){
        long pawnBoardCopy = bitBoards[attackerColor ? wPawnI : bPawnI];
        int i;
        while (pawnBoardCopy != 0){
            i = getFirstBitIndex(pawnBoardCopy);
            if ((pawnAttackTable[getSide(attackerColor)][i] & 1L << squareIndex) != 0)
                return true;
            pawnBoardCopy = removeBit(pawnBoardCopy, i);
        }
        return false;
    }

    private static boolean attackedByKing(boolean attackerColor, int squareIndex){
        long enemyKingPossibility = (kingPossibilityTable[getFirstBitIndex(getKingBoard(attackerColor))]
                                                ^ 1L << (getFirstBitIndex(getKingBoard(attackerColor)) - 2)
                                                ^ 1L << (getFirstBitIndex(getKingBoard(attackerColor)) + 2));
        enemyKingPossibility = removeAB_OR_GH_Cols(getFirstBitIndex(getKingBoard(attackerColor)), enemyKingPossibility);
        return (enemyKingPossibility & 1L << squareIndex) != 0;
    }

    private static Pair<Integer, Integer> possibilitiesNumInNextTurn(boolean forWhite){
        int moveCount = 0, enemyKingMoveCount = 0, from, to;
        long currentBitBoard, possibilities, shouldBePartOfMove;
        int enemyKingIndex = forWhite ? bKingI : wKingI;
        for (int piece : pieceIndexes) {


            if (forWhite == piece <= wKingI || piece == enemyKingIndex){

                currentBitBoard = bitBoards[piece];

                while (currentBitBoard != 0){

                    from = getFirstBitIndex(currentBitBoard);
                    setHittableOccupiedEmpty();
                    Pair<Long, Long> pAndS = possibilitiesAndShouldBePartOf(piece, from);
                    possibilities = pAndS.getFirst();
                    shouldBePartOfMove = pAndS.getSecond();

                    while (possibilities != 0){
                        to = getFirstBitIndex(possibilities);

                        //Calc what should be part of pawn range
                        if (piece == wPawnI || piece == bPawnI) {
                            //Simple 1 forward
                            if (8 == Math.abs(from - to)) {
                                shouldBePartOfMove = EMPTY;
                            }
                            //2 forward
                            else if (16 == Math.abs(from - to)) {
                                int minus = whiteDown ? (forWhite ? 8 : -8) : (forWhite ? -8 : 8);
                                if ((EMPTY & 1L << (to - minus)) == 0)
                                    break;
                                shouldBePartOfMove = EMPTY & 1L << to;
                            }
                            //Hit and if there's emPassant possibility, combine it
                            else {
                                shouldBePartOfMove = getShouldBePartOfMoveEmPassantAndHit(piece);
                            }
                        }

                        if ((1L << to & shouldBePartOfMove) != 0){
                            if (piece == enemyKingIndex)
                                enemyKingMoveCount++;
                            else
                                moveCount++;
                        }

                        possibilities = removeBit(possibilities, to);
                    }

                    currentBitBoard = removeBit(currentBitBoard, from);
                }
            }
        }
        return new Pair<>(moveCount, enemyKingMoveCount);
    }

    //endregion

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

    public static boolean isCheck(int move){
        return (move & 0x200000) != 0;
    }

    public static boolean isEmPassant(int move){
        return (move & 0x400000) != 0;
    }

    public static boolean isCastling(int move){
        return (move & 0x800000) != 0;
    }

    public static TreeMap<Double, ArrayList<Integer>> generateMoves(boolean forWhite){

        synchronized (pauseFlag){

            waitOnPause();

            double value;
            int move;
            boolean moveIsCheck;

            int from, to;
            long currentBitBoardCopy, possibility;
            long shouldBePartOfMove;

            TreeMap<Double, ArrayList<Integer>> valuedMoves =
                    new TreeMap<>(forWhite ?
                            Comparator.<Double>reverseOrder() :
                            Comparator.<Double>naturalOrder());


            for (int piece : pieceIndexes) {

                if (forWhite == (piece <= wKingI)) {

                    currentBitBoardCopy = bitBoards[piece];

                    while (currentBitBoardCopy != 0) {

                        from = getFirstBitIndex(currentBitBoardCopy);
                        setHittableOccupiedEmpty();
                        Pair<Long, Long> pAndS = possibilitiesAndShouldBePartOf(piece, from);
                        possibility = pAndS.getFirst();
                        shouldBePartOfMove = pAndS.getSecond();

                        while (possibility != 0) {
                            to = getFirstBitIndex(possibility);

                            //Calc what should be part of pawn range
                            if (piece == wPawnI || piece == bPawnI) {
                                //Simple 1 forward
                                if (8 == Math.abs(from - to)) {
                                    shouldBePartOfMove = EMPTY;
                                }
                                //2 forward
                                else if (16 == Math.abs(from - to)) {
                                    int minus = whiteDown ? (forWhite ? 8 : -8) : (forWhite ? -8 : 8);
                                    if ((EMPTY & 1L << (to - minus)) == 0)
                                        break;
                                    shouldBePartOfMove = EMPTY & 1L << to;
                                }
                                //Hit and if there's emPassant possibility, combine it
                                else {
                                    shouldBePartOfMove = getShouldBePartOfMoveEmPassantAndHit(piece);
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

                                move = createMove(
                                        from, to, piece, promotion,
                                        (1L << to & (piece <= wKingI ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK)) != 0,
                                        (1L << to & EMPTY & 1L << bbEmPassant) != 0 && (piece == wPawnI || piece == bPawnI),
                                        (piece == wKingI || piece == bKingI) && 2 == Math.abs(from - to)
                                );

                                copyPosition();

                                if (makeMove(move)) {
                                    Pair<Integer, Integer> moveCountsNextTurn = possibilitiesNumInNextTurn(forWhite);
                                    int possibilitiesAfterMove = moveCountsNextTurn.getFirst();
                                    double enemyKingPossibilitiesAfterMove = 0.1 * moveCountsNextTurn.getSecond();
                                    moveIsCheck = isSquareAttacked(forWhite, getFirstBitIndex(getKingBoard(!forWhite)));
                                    move |= moveIsCheck ? 0x200000 : 0;
                                    value = evaluate(possibilitiesAfterMove, enemyKingPossibilitiesAfterMove, forWhite);
                                    putToMap(valuedMoves, value, move);
                                    undoMove();
                                }
                            }
                            possibility = removeBit(possibility, to);
                        }
                        currentBitBoardCopy = removeBit(currentBitBoardCopy, from);
                    }
                }

            }
            return valuedMoves;
        }
    }

    private static long getShouldBePartOfMoveEmPassantAndHit(int piece) {
        long shouldBePartOfMove;
        shouldBePartOfMove = (piece == wPawnI ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK);
        if (bbEmPassant != -1) {
            if (piece == wPawnI && (1L << bbEmPassant & ROW_6) != 0) {
                shouldBePartOfMove |= 1L << bbEmPassant;
            } else if (piece == bPawnI && (1L << bbEmPassant & ROW_3) != 0) {
                shouldBePartOfMove |= 1L << bbEmPassant;
            }
        }
        return shouldBePartOfMove;
    }

    private static Pair<Long, Long> possibilitiesAndShouldBePartOf(int piece, int from){
        long possibility = 0, shouldBePartOfMove = 0;
        switch (piece) {
            case wPawnI -> possibility = removeA_Or_H_Cols(from, pawnSimpleStepTable[1][from] | pawnAttackTable[1][from]);
            case bPawnI -> possibility = removeA_Or_H_Cols(from, pawnSimpleStepTable[0][from] | pawnAttackTable[0][from]);
            case wKnightI -> {
                possibility = knightPossibilityTable[from];
                shouldBePartOfMove = shouldBePartOf(true);
            }
            case bKnightI -> {
                possibility = knightPossibilityTable[from];
                shouldBePartOfMove = shouldBePartOf(false);
            }
            case wBishopI -> {
                possibility = bishopPossibilities(from, true);
                shouldBePartOfMove = shouldBePartOf(true);
            }
            case bBishopI -> {
                possibility = bishopPossibilities(from, false);
                shouldBePartOfMove = shouldBePartOf(false);
            }
            case wRookI -> {
                possibility = rookPossibilities(from, true);
                shouldBePartOfMove = shouldBePartOf(true);
            }
            case bRookI -> {
                possibility = rookPossibilities(from, false);
                shouldBePartOfMove = shouldBePartOf(false);
            }
            case wQueenI -> {
                possibility = queenPossibilities(from, true);
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

        return new Pair<>(possibility, shouldBePartOfMove);
    }

    private static void putToMap(TreeMap<Double, ArrayList<Integer>> map, double val, int move){
        if (map.containsKey(val)){
            map.get(val).add(move);
        }else {
            ArrayList<Integer> moves = new ArrayList<>();
            moves.add(move);
            map.put(val, moves);
        }
    }

    private static long shouldBePartOf(boolean forWhite){
        return (forWhite ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK) | EMPTY;
    }

    public static long calcKingPossibility(int piece, long possibility){
        int kingLoc = getFirstBitIndex(bitBoards[piece]);
        boolean kingIsInCheck = isSquareAttacked(piece != wKingI, kingLoc);

        int kingsNewPlace;
        int rookNewPlace;
        int rookPlusRoad;
        int rookOrigin;
        kingsNewPlace = kingLoc - (whiteDown ? 2 : -2);

        if (kingIsInCheck){
            possibility &= ~(1L << kingsNewPlace);
        }else {
            if ((castle & (piece == wKingI ? wK : bK)) == 0){
                possibility &= ~(1L << kingsNewPlace);
            } else {
                rookNewPlace = kingLoc - (whiteDown ? 1 : -1);
                rookOrigin = kingLoc - (whiteDown ? 3 : -3);
                if (
                        (1L << kingsNewPlace & EMPTY) == 0 || (1L << rookNewPlace & EMPTY) == 0 ||
                                (1L << rookOrigin & bitBoards[piece == wKingI ? wRookI : bRookI]) == 0 ||
                                isSquareAttacked(piece != wKingI, kingsNewPlace) ||
                                isSquareAttacked(piece != wKingI, rookNewPlace)
                ) {
                    possibility &= ~(1L << kingsNewPlace);
                }
            }
        }

        kingsNewPlace = kingLoc - (whiteDown ? -2 : 2);

        if (kingIsInCheck){
            possibility &= ~(1L << kingsNewPlace);
        }else {
            if ((castle & (piece == wKingI ? wQ : bQ)) == 0) {
                possibility &= ~(1L << kingsNewPlace);
            } else {
                rookNewPlace = kingLoc - (whiteDown ? -1 : 1);
                rookPlusRoad = kingLoc - (whiteDown ? -3 : 3);
                rookOrigin = kingLoc - (whiteDown ? -4 : 4);
                if (
                        (1L << kingsNewPlace & EMPTY) == 0 || (1L << rookNewPlace & EMPTY) == 0 ||
                                (1L << rookPlusRoad & EMPTY) == 0 || (1L << rookOrigin & bitBoards[piece == wKingI ? wRookI : bRookI]) == 0 ||
                                isSquareAttacked(piece != wKingI, kingsNewPlace) ||
                                isSquareAttacked(piece != wKingI, rookNewPlace)
                ) {
                    possibility &= ~(1L << kingsNewPlace);
                }
            }
        }

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
                if (whiteDown){
                    if (to == 1) { // whiteDown wKC
                        rookFrom = 0;
                        rookTo = 2;
                        if ((castle & wK) != 0)
                            castle -= wK;
                    } else if (to == 5) { // whiteDown wQC
                        rookFrom = 7;
                        rookTo = 4;
                        if ((castle & wQ) != 0)
                            castle -= wQ;
                    } else if (to == 57) { // whiteDown bKC
                        rookFrom = 56;
                        rookTo = 58;
                        if ((castle & bK) != 0)
                            castle -= bK;
                    } else if (to == 61) { // whiteDown bQC
                        rookFrom = 63;
                        rookTo = 60;
                        if ((castle & bQ) != 0)
                            castle -= bQ;
                    }
                } else {
                    if (to == 62) { // !whiteDown wKC
                        rookFrom = 63;
                        rookTo = 61;
                        if ((castle & wK) != 0)
                            castle -= wK;
                    } else if (to == 58) { // !whiteDown wQC
                        rookFrom = 56;
                        rookTo = 59;
                        if ((castle & wQ) != 0)
                            castle -= wQ;
                    } else if (to == 6) { // !whiteDown bKC
                        rookFrom = 7;
                        rookTo = 5;
                        if ((castle & bK) != 0)
                            castle -= bK;
                    } else if (to == 2) { // !whiteDown bQC
                        rookFrom = 0;
                        rookTo = 3;
                        if ((castle & bQ) != 0)
                            castle -= bQ;
                    }
                }
                if (rookFrom == -1){
                    bitBoards[what] = removeBit(bitBoards[what], to);
                    bitBoards[what] = setBit(bitBoards[what], from);
                    whiteToPlay = !whiteToPlay;
                    return false;
                }else {
                    bitBoards[whiteToPlay ? wRookI : bRookI] = removeBit(getRookBoard(whiteToPlay), rookFrom);
                    bitBoards[whiteToPlay ? wRookI : bRookI] = setBit(getRookBoard(whiteToPlay), rookTo);
                }
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
                if ((castle & wK) != 0)
                    castle -= wK;
                if ((castle & wQ) != 0)
                    castle -= wQ;
            } else if (what == bKingI) {
                if ((castle & bK) != 0)
                    castle -= bK;
                if ((castle & bQ) != 0)
                    castle -= bQ;
            } else if (what == wRookI) {
                if (whiteDown){
                    if (from == 0 && (castle & wK) != 0)
                        castle -= wK;
                    else if ((castle & wQ) != 0)
                        castle -= wQ;
                }else {
                    if (from == 63 && (castle & wK) != 0)
                        castle -= wK;
                    else if ((castle & wQ) != 0)
                        castle -= wQ;
                }
            } else if (what == bRookI) {
                if (whiteDown){
                    if (from == 56 && (castle & bK) != 0)
                        castle -= bK;
                    else if ((castle & bQ) != 0)
                        castle -= bQ;
                }else {
                    if (from == 0 && (castle & bK) != 0)
                        castle -= bK;
                    else if ((castle & bQ) != 0)
                        castle -= bQ;
                }
            }

            whiteToPlay = !whiteToPlay;

            if (getKingBoard(!whiteToPlay) != 0 &&
                            isSquareAttacked(whiteToPlay, getFirstBitIndex(getKingBoard(!whiteToPlay)))
            ){
                undoMove();
                return false;
            }else
                return true;
        }
        return false;
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
        m += isCheck(move) ? " Caused check. " : "";
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

    public static String moveListToString(Collection<Integer> movesInATurn){
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
