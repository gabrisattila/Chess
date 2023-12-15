package classes.Game.Model.Structure.BitBoard;

import classes.Game.I18N.PieceType;

import java.util.*;

import static classes.Ai.FenConverter.charToPieceType;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoards.*;

public class BitBoardMoves {


    //region Methods With BitBoards

    public static ArrayList<String> possibleMoves(boolean maxNeeded, int emPassantChance,
                                       boolean whiteKingCastleEnabled, boolean whiteQueenCastleEnabled,
                                       boolean blackKingCastleEnabled, boolean blackQueenCastleEnabled,
                                       long wG, long wN, long wB, long wR, long wQ, long wK,
                                       long bP, long bN, long bB, long bR, long bQ, long bK) {

        HITTABLE_BY_BLACK = wG | wN | wB | wR | wQ;
        HITTABLE_BY_WHITE = bP | bN | bB | bR | bQ;
        OCCUPIED = mergeFullBitBoard(new ArrayList<>(){{add(wG); add(wN); add(wB); add(wR); add(wQ); add(wK);
            add(bP); add(bN); add(bB); add(bR); add(bQ); add(bK);}});
        EMPTY = ~OCCUPIED;

        String moves = pawnMoves(
                maxNeeded, emPassantChance, wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
        moves += knightMoves(maxNeeded, wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
        moves += bishopMoves(maxNeeded, wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
        moves += rookMoves(maxNeeded, wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
        moves += queenMoves(maxNeeded, wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
        moves += kingMoves(
                maxNeeded, whiteKingCastleEnabled, whiteQueenCastleEnabled,
                blackKingCastleEnabled, blackQueenCastleEnabled,
                wG, wN, wB, wR, wQ, wK, bP, bN, bB, bR, bQ, bK, OCCUPIED);
        String[] moveList = moves.split("_");
        TreeMap<Double, Set<String>> finalMoveMap = new TreeMap<>(maxNeeded ?
                                                                    Comparator.<Double>naturalOrder() :
                                                                    Comparator.<Double>reverseOrder());
        String[] moveAndPoint;
        double point;
        for (String move : moveList) {
            moveAndPoint = move.split("/");
            move = moveAndPoint[0];
            point = Double.parseDouble(moveAndPoint[1]);
            putToPossibilityMap(finalMoveMap, point, move);
        }
        
        ArrayList<String> finalMoves = new ArrayList<>();
        for (double d : finalMoveMap.keySet()) {
            finalMoves.addAll(finalMoveMap.get(d));
        }
        
        return finalMoves;
    }

    public static long moveAPieceOnBoard(String move, long boardToMoveOn, String typeOfBoard){

        String[] moveParts = move.split("-");

        String type = moveParts[0];
        int start = Integer.parseInt(moveParts[1]);
        int end = Integer.parseInt(moveParts[2]);
        if ((boardToMoveOn >> start & 1) == 1){
            boardToMoveOn &= ~(1L << start);
            boardToMoveOn |= (1L << end);
        }else {
            boardToMoveOn &= ~(1L << end);
        }

        //Pawn Promotion Action
        if (("P".equals(type) && (1L << end & ROW_8) != 0)){
            if ("P".equals(typeOfBoard)){
                boardToMoveOn &= ~1L << end;
            } else if ("Q".equals(typeOfBoard)) {
                boardToMoveOn |= 1L << end;
            }
        }
        if (("p".equals(type) && (1L << end & ROW_1) != 0)){
            if ("p".equals(typeOfBoard)){
                boardToMoveOn &= ~1L << end;
            } else if ("q".equals(typeOfBoard)) {
                boardToMoveOn |= 1L << end;
            }
        }

        //EmPassant Action
        if (("P".equals(type) || "p".equals(type)) && (Math.abs(start - end) == 7 || Math.abs(start - end) == 9)){
            if ("P".equals(type) && "p".equals(typeOfBoard)){
                if ((1L << end & boardToMoveOn) == 0){ //EmPassant with white
                    if (whiteDown){
                        boardToMoveOn &= ~(1L << (end - 8));
                    }else {
                        boardToMoveOn &= ~(1L << (end + 8));
                    }
                }
            } else if ("p".equals(type) && "P".equals(typeOfBoard)) {
                if ((1L << end & boardToMoveOn) == 0){ //EmPassant with black
                    if (whiteDown){
                        boardToMoveOn &= ~(1L << (end + 8));
                    }else {
                        boardToMoveOn &= ~(1L << (end - 8));
                    }
                }
            }
        }

        //Castle Action
        if (("K".equals(type) || "k".equals(type)) && Math.abs(start - end) == 2){
            int[] kingCastleIndexes = indexesInCastle(whiteDown, "K".equals(type), true);
            int[] queenCastleIndexes = indexesInCastle(whiteDown, "K".equals(type), false);
            if (("K".equals(type) && "R".equals(typeOfBoard)) || ("k".equals(type) && "r".equals(typeOfBoard))){
                boardToMoveOn &= ~(1L << (end == kingCastleIndexes[0] ? kingCastleIndexes : queenCastleIndexes)[2]);
                boardToMoveOn |= 1L << (end == kingCastleIndexes[0] ? kingCastleIndexes : queenCastleIndexes)[1];
            }
        }

        return boardToMoveOn;
    }

    public static long unsafeFor(boolean forWhite,
                                 long wG, long wN, long wB, long wR, long wQ, long wK,
                                 long bP, long bN, long bB, long bR, long bQ, long bK){
        long unsafe = 0L;
        if (forWhite){
            unsafe |= (whiteDown ? (bP >> 7 & ~COL_A) : (bP << 9 & ~COL_H));
            unsafe |= (whiteDown ? (bP >> 9 & ~COL_H) : (bP << 7 & ~COL_A));
        } else {
            unsafe |= whiteDown ? (wG << 7 & ~COL_A) : (wG >> 9 & ~COL_H);
            unsafe |= whiteDown ? (wG << 9 & ~COL_H) : (wG >> 7 & ~COL_A);
        }
        unsafe |= movePossibilities(forWhite ? "n" : "N", wN, bN);
        unsafe |= movePossibilities(forWhite ? "b" : "B", wB, bB);
        unsafe |= movePossibilities(forWhite ? "r" : "R", wR, bR);
        unsafe |= movePossibilities(forWhite ? "q" : "Q", wQ, bQ);
        unsafe |= movePossibilities(forWhite ? "k" : "K", wK, bK);
        
        return unsafe;
    }

    private static void putToPossibilityMap(Map<Double, Set<String>> map, double point, String move){
        if (map.containsKey(point)){
            map.get(point).add(move);
        }else {
            map.put(point, new HashSet<>());
            map.get(point).add(move);
        }
    }

    public static String pawnMoves(boolean forWhite, int emPassantChance,
                                   long wG, long wN, long wB, long wR, long wQ, long wK,
                                   long bP, long bN, long bB, long bR, long bQ, long bK){

        long emPassantPossibility = 1L << emPassantChance;
        long used = forWhite ? wG : bP;
        
        StringBuilder moves = new StringBuilder();

        long pawnMoves;
        int plusToGetOrigin;
        long shouldBeInThatPart;

        if (forWhite){
            //Előre sima 1
            shouldBeInThatPart = EMPTY;
            pawnMoves = (whiteDown ? used << 8 : used >> 8) & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? -8 : 8;
            moveDocStringPawn(true, moves, pawnMoves, plusToGetOrigin, bP,  wN,  wB,  wR,  wQ,  wK, bN,  bB,  bR,  bQ,  bK);

//          Előre sima 2
            shouldBeInThatPart = EMPTY & ROW_4;
            pawnMoves = (whiteDown ? used << 16 : used >> 16) & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? -16 : 16;
            moveDocStringPawn(true, moves, pawnMoves, plusToGetOrigin, bP,  wN,  wB,  wR,  wQ,  wK, bN,  bB,  bR,  bQ,  bK);

            //Jobbra üt
            shouldBeInThatPart = HITTABLE_BY_BLACK & emPassantPossibility;

            pawnMoves = (whiteDown ? (used << 7 & ~COL_A) : (used >> 9 & ~COL_H))
                        & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? -7 : 9;
            moveDocStringPawn(true, moves, pawnMoves, plusToGetOrigin, bP,  wN,  wB,  wR,  wQ,  wK, bN,  bB,  bR,  bQ,  bK);

            //Balra üt
            pawnMoves = (whiteDown ? (used << 9 & ~COL_H) : (used >> 7 & ~COL_A))
                        & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? -9 : 7;
            moveDocStringPawn(true, moves, pawnMoves, plusToGetOrigin, bP,  wN,  wB,  wR,  wQ,  wK, bN,  bB,  bR,  bQ,  bK);

        }else {
            //Előre sima 1
            shouldBeInThatPart = EMPTY;
            pawnMoves = (whiteDown ? used >> 8 : used << 8) & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? 8 : -8;
            moveDocStringPawn(false, moves, pawnMoves, plusToGetOrigin, wG,  wN,  wB,  wR,  wQ,  wK, bN,  bB,  bR,  bQ,  bK);

            //Előre sima 2
            shouldBeInThatPart = EMPTY & ROW_5;
            pawnMoves = (whiteDown ? used >> 16 : used << 16) & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? 16 : -16;
            moveDocStringPawn(false, moves, pawnMoves, plusToGetOrigin, wG,  wN,  wB,  wR,  wQ,  wK, bN,  bB,  bR,  bQ,  bK);

            //Jobbra üt
            shouldBeInThatPart = HITTABLE_BY_BLACK & emPassantPossibility;
            pawnMoves = (whiteDown ? (used >> 7 & ~COL_A) : (used << 9 & ~COL_H))
                        & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? 7 : -9;
            moveDocStringPawn(false, moves, pawnMoves, plusToGetOrigin, wG,  wN,  wB,  wR,  wQ,  wK, bN,  bB,  bR,  bQ,  bK);

            //Balra üt
            pawnMoves = (whiteDown ? (used >> 9 & ~COL_H) : (used << 7 & ~COL_A))
                        & shouldBeInThatPart;
            plusToGetOrigin = whiteDown ? 9 : -7;
            moveDocStringPawn(false, moves, pawnMoves, plusToGetOrigin, wG,  wN,  wB,  wR,  wQ,  wK, bN,  bB,  bR,  bQ,  bK);
        }

        return moves.toString();
    }

    public static String knightMoves(boolean forWhite,
                                     long wG, long wN, long wB, long wR, long wQ, long wK,
                                     long bP, long bN, long bB, long bR, long bQ, long bK){
        String type = forWhite ? "N" : "n";
        return moveDocStringExceptPawn(type, wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK, 0);
    }

    public static String bishopMoves(boolean forWhite,
                                     long wG, long wN, long wB, long wR, long wQ, long wK,
                                     long bP, long bN, long bB, long bR, long bQ, long bK){
        return slidingPieceMoves(B.toString(forWhite), wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
    }

    public static String rookMoves(boolean forWhite,
                                   long wG, long wN, long wB, long wR, long wQ, long wK,
                                   long bP, long bN, long bB, long bR, long bQ, long bK){
        return slidingPieceMoves(R.toString(forWhite), wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
    }

    public static String queenMoves(boolean forWhite,
                                    long wG, long wN, long wB, long wR, long wQ, long wK,
                                    long bP, long bN, long bB, long bR, long bQ, long bK){
        return slidingPieceMoves(Q.toString(forWhite), wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
    }

    public static String kingMoves(boolean forWhite,
                                   boolean wKC, boolean wQC, boolean bKC, boolean bQC,
                                   long wG, long wN, long wB, long wR, long wQ, long wK,
                                   long bP, long bN, long bB, long bR, long bQ, long bK,
                                   long occ){
        String type = forWhite ? "K" : "k";
        StringBuilder moves = new StringBuilder();
        long unsafe = unsafeFor(forWhite, wG, wN, wB, wR, wQ, wK, bP, bN, bB, bR, bQ, bK);
        moves.append(moveDocStringExceptPawn(type, wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK, unsafe));
        castle( type, moves,
                wKC,  wQC,  bKC,  bQC,
                wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK, occ);
        return moves.toString();
    }

    public static String slidingPieceMoves(String type,
                                           long wG, long wN, long wB, long wR, long wQ, long wK,
                                           long bP, long bN, long bB, long bR, long bQ, long bK){
        return moveDocStringExceptPawn(type, wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK, 0);
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

    private static void moveDocStringPawn(boolean forWhite, StringBuilder moves, long used, int difference, long enemyPawn,
                                          long wN, long wB, long wR, long wQ, long wK,
                                          long bN, long bB, long bR, long bQ, long bK){
        long possibility = used & -used;

        String type = forWhite ? "P" : "p";
        while (possibility != 0)
        {
            int index = Long.numberOfTrailingZeros(possibility);
            moves.append(type);
            moves.append('-');
            moves.append(index + difference);
            moves.append('-');
            moves.append(index);
            appendPawnPromotion(type, moves, index + difference);
            appendEmPassantAutIfThereWas(type, moves, index + difference, index, enemyPawn);
            appendPawnCapture(type, moves, difference);
            appendMergedBoardsFinalVal(moves, used,  wN,  wB,  wR,  wQ, enemyPawn,  bN,  bB,  bR,  bQ);
            moves.append('_');
            used &= ~possibility;
            possibility = used & -used;
        }
    }

    private static String moveDocStringExceptPawn(String type, 
                                                  long wG, long wN, long wB, long wR, long wQ, long wK,
                                                  long bP, long bN, long bB, long bR, long bQ, long bK,
                                                  long unsafe){
        StringBuilder moves = new StringBuilder();
        boolean forWhite = Character.isUpperCase(type.charAt(0));
        long used = 0;
        switch (type.charAt(0)){
            case 'N' -> used = wN;
            case 'B' -> used = wB;
            case 'R' -> used = wR;
            case 'Q' -> used = wQ;
            case 'K' -> used = wK;
            case 'n' -> used = bN;
            case 'b' -> used = bB;
            case 'r' -> used = bR;
            case 'q' -> used = bQ;
            case 'k' -> used = bK;
        }

        long i = used & -used;
        long possibility;
        while (i != 0){
            int startLoc = 63 - Long.numberOfLeadingZeros(i);

            possibility = movingPossibilities(type, startLoc);
            long j = possibility & -possibility;

            while (j != 0)
            {
                int endLoc = Long.numberOfTrailingZeros(j);
                if ("K".equals(type) || "k".equals(type)){
                    if ((1L << endLoc & unsafe) == 0){
                        moves.append(type);
                        moves.append('-');
                        moves.append(startLoc);
                        moves.append('-');
                        moves.append(endLoc);
                        appendKingMoveNote(type, moves);
                    }
                }else {
                    moves.append(type);
                    moves.append('-');
                    moves.append(startLoc);
                    moves.append('-');
                    moves.append(endLoc);
                    appendRookMoveNote(moves, type, startLoc);
                }
                if (!"K".equals(type) && !"k".equals(type) || (1L << endLoc & unsafe) == 0){
                    captureOtherNote(type, moves, possibility,
                            forWhite ? wG : bP, forWhite ? wN : bN, forWhite ? wB : bB, forWhite ? wR : bR, forWhite ? wQ : bQ
                    );
                    appendMergedBoardsFinalVal(moves, wG,  wN,  wB,  wR,  wQ, bP,  bN,  bB,  bR,  bQ);
                    moves.append('_');
                }
                possibility &= ~j;
                j = possibility & -possibility;
            }
            used &= ~i;
            i = used & -used;
        }

        return moves.toString();
    }
    
    private static long movePossibilities(String type, long w, long b){
        boolean forWhite = Character.isUpperCase(type.charAt(0));
        long used = forWhite ? w : b;
        
        long i = used & -used;
        long possibility = 0L;
        while (i != 0){
            int startLoc = 63 - Long.numberOfLeadingZeros(i);

            possibility |= movingPossibilities(type, startLoc);

            used &= ~i;
            i = used & -used;
        }
        
        return possibility;
    }

    private static long movingPossibilities(String type, int startLoc){
        long possibility = 0L;
        boolean forWhite = Character.isUpperCase(type.charAt(0));
        if ("K".equals(type) || "k".equals(type) || "N".equals(type) || "n".equals(type)) {
            if ("K".equals(type) || "k".equals(type)){
                possibility = startLoc >= 9 ? KING_SPAN << (startLoc - 9) : KING_SPAN >> (9 - startLoc);
            }else {
                possibility = startLoc >= 18 ? (KNIGHT_SPAN << (startLoc - 18)) : (KNIGHT_SPAN >> (18 - startLoc));
            }
            if ((1L << startLoc & COL_GH) != 0){
                possibility &= ~COL_AB;
            } else if ((1L << startLoc & COL_AB) != 0) {
                possibility &= ~COL_GH;
            }
        } else {
            switch (type.charAt(0)){
                case 'B', 'b' -> possibility |= diagonalAndAntiDiagonalMoves(startLoc);
                case 'R', 'r' -> possibility |= horizontalAndVerticalMoves(startLoc);
                default -> possibility |= diagonalAndAntiDiagonalMoves(startLoc) | horizontalAndVerticalMoves(startLoc);
            }
        }
        possibility &= (forWhite ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK) | EMPTY;
        return possibility;
    }

    private static void castle(String type, StringBuilder castlePlaces,
                                 boolean wKC, boolean wQC, boolean bKC, boolean bQC,
                                 long wG, long wN, long wB, long wR, long wQ, long wK,
                                 long bP, long bN, long bB, long bR, long bQ, long bK,
                                 long occupied){
        boolean forWhite = Character.isUpperCase(type.charAt(0));

        long unsafe = unsafeFor(forWhite, wG, wN, wB, wR, wQ, wK, bP, bN, bB, bR, bQ, bK);

        if (isKingSideCastleEnabled(forWhite, whiteDown, forWhite ? wKC : bKC, forWhite ? wK : bK,
                                    forWhite ? wR : bR, unsafe, occupied)){
            castleDoc(type, castlePlaces, whiteDown, true, wG, wN, wB, wR, wQ, wK, bP, bN, bB, bR, bQ, bK);
        }
        if (isQueenSideCastleEnabled(forWhite, whiteDown, forWhite ? wQC : bQC, forWhite ? wK : bK,
                                     forWhite ? wR : bR, unsafe, occupied)){
            castleDoc(type, castlePlaces, whiteDown, false, wG, wN, wB, wR, wQ, wK, bP, bN, bB, bR, bQ, bK);
        }
    }

    private static void castleDoc(String type, StringBuilder moves, boolean whiteDown, boolean kingSide,
                                 long wG, long wN, long wB, long wR, long wQ, long wK,
                                  long bP, long bN, long bB, long bR, long bQ, long bK){
        boolean forWhite = Character.isUpperCase(type.charAt(0));
        int[] castleIndexes = indexesInCastle(whiteDown, forWhite, kingSide);
        moves.append(type);
        moves.append("-");
        moves.append(whiteDown ? (forWhite ? 3 : 59) : (forWhite ? 60 : 4));
        moves.append("-");
        moves.append(castleIndexes[0]);
        appendKingMoveNote(type, moves);
        appendMergedBoardsFinalVal(moves, wG, wN, wB, wR, wQ, bP, bN, bB, bR, bQ);
        moves.append("_");
    }

    private static void captureOtherNote(String type, StringBuilder moves, long used, long hitPawn, long hitKnight, long hitBishop, long hitRook, long hitQueen){
        String capture = "-C";
        String capturedType = "";
        boolean capturerIsWhite = Character.isUpperCase(type.charAt(0));
        if ((used & hitPawn) != 0){
            capturedType = capturerIsWhite ? "p" : "P";
        } else if ((used & hitKnight) != 0) {
            capturedType = capturerIsWhite ? "n" : "N";
        } else if ((used & hitBishop) != 0) {
            capturedType = capturerIsWhite ? "b" : "B";
        } else if ((used & hitRook) != 0) {
            capturedType = capturerIsWhite ? "r" : "R";
        } else if ((used & hitQueen) != 0) {
            capturedType = capturerIsWhite ? "q" : "Q";
        } else {
          capture = "";
        }
        moves.append(capture).append(capturedType);
    }

    private static void appendRookMoveNote(StringBuilder moves, String type, int startLoc){
        if (("R".equals(type) || "r".equals(type)) && Arrays.stream(corners).anyMatch(c -> c == startLoc)){
            if ((1L << startLoc & KING_SIDE) != 0){
                moves.append(Character.isUpperCase(type.charAt(0)) ? "-K" : "-k");
            } else if ((1L << startLoc & QUEEN_SIDE) != 0) {
                moves.append(Character.isUpperCase(type.charAt(0)) ? "-Q" : "-q");
            }
        }
    }

    private static void appendKingMoveNote(String type, StringBuilder moves){
        if ("K".equals(type) || "k".equals(type)){
            moves.append("K".equals(type) ? "-KV" : "-kv");
        }
    }
    
    private static void appendMergedBoardsFinalVal(StringBuilder moves,
                                                   long wP, long wN, long wB, long wR, long wQ,
                                                   long bP, long bN, long bB, long bR, long bQ) {
        moves.append("/");
        moves.append(getEvaluationOfThisMove(moves, wP,  wN,  wB,  wR,  wQ, bP,  bN,  bB,  bR,  bQ));
    }
    
    private static boolean isKingSideCastleEnabled(boolean forWhite, boolean whiteDown, 
                                                   boolean smallCastleEnabled, long kingBoard, long rookBoard, long unsafe, long occupied){
        int smallCastlePoint, smallCastleRoad, rookOrigin, kingOrigin;
        if (!smallCastleEnabled)
            return false;
        int[] castleIndexes = indexesInCastle(whiteDown, forWhite, true);

        smallCastlePoint = castleIndexes[0];
        smallCastleRoad = castleIndexes[1];
        rookOrigin = castleIndexes[2];
        kingOrigin = castleIndexes[3];

        return castleIsEnabled(kingOrigin, rookOrigin, smallCastleRoad, smallCastlePoint,
                                kingBoard, rookBoard, unsafe, occupied);

    }
    
    private static boolean isQueenSideCastleEnabled(boolean forWhite, boolean whiteDown,
                                                    boolean bigCastleEnabled, long kingBoard, long rookBoard, long unsafe, long occupied){
        int bigCastlePoint, bigCastleRoad, rookOrigin, kingOrigin, rookPlusRoad;
        if (!bigCastleEnabled)
            return false;

        int[] castleIndexes = indexesInCastle(whiteDown, forWhite, false);

        bigCastlePoint = castleIndexes[0];
        bigCastleRoad = castleIndexes[1];
        rookOrigin = castleIndexes[2];
        kingOrigin = castleIndexes[3];
        rookPlusRoad = castleIndexes[4];

        return  castleIsEnabled(
                kingOrigin, rookOrigin, bigCastleRoad, bigCastlePoint,
                kingBoard, rookBoard, unsafe, occupied
                ) &&
                (1L << rookPlusRoad & occupied) == 0;
    }

    /**
     * @return a new array what contains the next elements
     * kingPointInCastle, kingRoadInCastle / newPlaceOfRook, rookOriginPoint, kingOriginPoint, rookPlusRoad if there were (big castle)
     */
    private static int[] indexesInCastle(boolean whiteDown, boolean forWhite, boolean kingSide){
        int[] indexes = new int[5];
        if (kingSide){
            if (whiteDown){
                indexes[0] = forWhite ? 1 : 57;
                indexes[1] = forWhite ? 2 : 58;
                indexes[2] = forWhite ? 0 : 56;
                indexes[3] = forWhite ? 3 : 59;
            }else {
                indexes[0] = forWhite ? 62 : 6;
                indexes[1] = forWhite ? 61 : 5;
                indexes[2] = forWhite ? 63 : 7;
                indexes[3] = forWhite ? 60 : 4;
            }
        }else {
            if (whiteDown){
                indexes[0] = forWhite ? 5 : 61;
                indexes[1] = forWhite ? 4 : 60;
                indexes[2] = forWhite ? 7 : 63;
                indexes[3] = forWhite ? 3 : 59;
                indexes[4] = forWhite ? 6 : 62;
            }else {
                indexes[0] = forWhite ? 58 : 2;
                indexes[1] = forWhite ? 59 : 3;
                indexes[2] = forWhite ? 56 : 0;
                indexes[3] = forWhite ? 60 : 4;
                indexes[4] = forWhite ? 57 : 1;
            }
        }
        return indexes;
    }

    private static boolean castleIsEnabled(int kingOrigin, int rookOrigin, int bigCastleRoad, int bigCastlePoint,
                                    long kingBoard, long rookBoard, long unsafe, long occupied){
        return  (1L << rookOrigin & rookBoard) != 0 && (1L << kingOrigin & kingBoard) != 0 &&
                (1L << bigCastleRoad & unsafe) == 0 && (1L << bigCastleRoad & occupied) == 0 &&
                (1L << bigCastlePoint & unsafe) == 0 && (1L << bigCastlePoint & occupied) == 0;
    }

    private static void appendPawnCapture(String type, StringBuilder moves, int difference){
        if (difference == 7 || difference == 9){
            moves.append("P".equals(type) ? "-Cp" : "-CP");
        }
    }

    private static void appendPawnPromotion(String type, StringBuilder moves, int end){
        if ("P".equals(type) ? (1L << end & ROW_8) != 0 : (1L << end & ROW_1) != 0){
            moves.append("-").append(type).append("P".equals(type) ? "Q" : "q");
        }
    }

    private static void appendEmPassantAutIfThereWas(String type, StringBuilder moves,
                                                     int startIndex, int endIndex, long enemyPawns){
        if ("P".equals(type) || "p".equals(type)){
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

    public static double getEvaluationOfThisMove(String moves, long wP, long wN, long wB, long wR, long wQ,
                                                 long bP, long bN, long bB, long bR, long bQ){
        return decideIsItCapture(
                wP, wN, wB, wR, wQ,
                bP, bN, bB, bR, bQ,
                moves.isEmpty(),
                moves.charAt(moves.length() - 3), moves.charAt(moves.length() - 2));
    }

    public static double getEvaluationOfThisMove(StringBuilder moves, long wP, long wN, long wB, long wR, long wQ,
                                                 long bP, long bN, long bB, long bR, long bQ){
        return decideIsItCapture(
                wP, wN, wB, wR, wQ,
                bP, bN, bB, bR, bQ,
                moves.isEmpty(),
                moves.charAt(moves.length() - 3), moves.charAt(moves.length() - 2));
    }

    private static double decideIsItCapture(long wP, long wN, long wB, long wR, long wQ, long bP, long bN, long bB, long bR, long bQ, boolean empty, char CifItsCapture, char captureType) {
        if (!empty){
            return evaluationOfFullBitBoard(wP, wN, wB, wR, wQ,
                    bP, bN, bB, bR, bQ,
                    CifItsCapture, captureType);
        }else {
            //Don't add anything
            return evaluationOfFullBitBoard(wP, wN, wB, wR, wQ,
                    bP, bN, bB, bR, bQ,
                    'N', 'Z');
        }
    }

    private static double evaluationOfFullBitBoard(long wP, long wN, long wB, long wR, long wQ,
                                                   long bP, long bN, long bB, long bR, long bQ,
                                                   char CifItsCapture, char capturedType) {
        return  PAWN_BASE_VALUE *
                        (Long.bitCount(wP) /* + getBaseFieldValueFor(wP, 'P') */ - Long.bitCount(bP)/* - getBaseFieldValueFor(bP, 'p')*/) +
                KNIGHT_OR_BISHOP_BASE_VALUE *
                        (Long.bitCount(wN) /* + getBaseFieldValueFor(wN, 'N') */  - Long.bitCount(bN)/* - getBaseFieldValueFor(bN, 'n')*/ +
                        Long.bitCount(wB) /* + getBaseFieldValueFor(wB, 'B') */ - Long.bitCount(bB)/* - getBaseFieldValueFor(bB, 'b')*/) +
                ROOK_BASE_VALUE *
                        (Long.bitCount(wR) /*+ getBaseFieldValueFor(wR, 'R')*/ - Long.bitCount(bR) /* - getBaseFieldValueFor(bR, 'r')*/) +
                QUEEN_BASE_VALUE *
                        (Long.bitCount(wQ) /* + getBaseFieldValueFor(wQ, 'Q') */ - Long.bitCount(bQ) /* - getBaseFieldValueFor(bQ, 'q')*/) +
                CifItsCapture == 'C' ? getHitValue(capturedType) : 0;
    }

    private static double getHitValue(char type){
        boolean white = Character.isUpperCase(type);
        PieceType pType = charToPieceType(type);
        return white ? - pType.getValueOfPieceType() : pType.getValueOfPieceType();
    }


    //endregion

}
