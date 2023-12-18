package classes.Ai.BitBoards;

import classes.Ai.AI.AiNode;

import java.util.*;

import static classes.Ai.Evaluation.Evaluator.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Ai.BitBoards.BBVars.*;
import static classes.Ai.BitBoards.BitBoards.*;
import static java.util.Objects.requireNonNull;

public class BitBoardMoves {

    public static ArrayList<String> possibleMoves(boolean forWhite, AiNode node){
        return possibleMoves(forWhite,
                node.getEmPassant(), node.isWKC(), node.isWQC(), node.isBKC(), node.isBQC(),
                node.getWP(), node.getWN(), node.getWB(), node.getWR(), node.getWQ(), node.getWK(),
                node.getBP(), node.getBN(), node.getBB(), node.getBR(), node.getBQ(), node.getBK());
    }

    public static ArrayList<String> possibleMoves(boolean maxNeeded, int emPassantChance,
                                       boolean whiteKingCastleEnabled, boolean whiteQueenCastleEnabled,
                                       boolean blackKingCastleEnabled, boolean blackQueenCastleEnabled,
                                       long wP, long wN, long wB, long wR, long wQ, long wK,
                                       long bP, long bN, long bB, long bR, long bQ, long bK) {

        HITTABLE_BY_BLACK = wP | wN | wB | wR | wQ;
        HITTABLE_BY_WHITE = bP | bN | bB | bR | bQ;
        OCCUPIED = mergeFullBitBoard(new ArrayList<>(){{add(wP); add(wN); add(wB); add(wR); add(wQ); add(wK);
            add(bP); add(bN); add(bB); add(bR); add(bQ); add(bK);}});
        EMPTY = ~OCCUPIED;
        String moves = pawnMoves(
                maxNeeded, emPassantChance, wP,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
        moves += knightMoves(maxNeeded, wP,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
        moves += bishopMoves(maxNeeded, wP,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
        moves += rookMoves(maxNeeded, wP,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
        moves += queenMoves(maxNeeded, wP,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK);
        moves += kingMoves(
                maxNeeded, whiteKingCastleEnabled, whiteQueenCastleEnabled,
                blackKingCastleEnabled, blackQueenCastleEnabled,
                wP, wN, wB, wR, wQ, wK, bP, bN, bB, bR, bQ, bK, OCCUPIED);
        String[] moveList = moves.split("_");
        TreeMap<Double, Set<String>> finalMoveMap = new TreeMap<>(maxNeeded ?
                                                                    Comparator.<Double>reverseOrder() :
                                                                    Comparator.<Double>naturalOrder());
        if (!moves.isEmpty()){
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
        return new ArrayList<>();
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

    public static long unsafeFor(boolean forWhite, AiNode node){
        return unsafeFor(forWhite,
                node.getWP(), node.getWN(), node.getWB(), node.getWR(), node.getWQ(), node.getWK(),
                node.getBB(), node.getBN(), node.getBB(), node.getBR(), node.getBQ(), node.getBK());
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
            shouldBeInThatPart = EMPTY & ROW_4 & ROW_3;
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
            shouldBeInThatPart = EMPTY & ROW_5 & ROW_6;
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
                wG,  wN,  wB,  wR,  wQ,  wK, bP,  bN,  bB,  bR,  bQ,  bK,
                occ, unsafe);
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
            appendMergedBoardsFinalVal(moves, used,  wN,  wB,  wR,  wQ, wK, enemyPawn,  bN,  bB,  bR,  bQ, bK);
            moves.append('_');
            used &= ~possibility;
            possibility = used & -used;
        }
    }

    private static String moveDocStringExceptPawn(String type, 
                                                  long wP, long wN, long wB, long wR, long wQ, long wK,
                                                  long bP, long bN, long bB, long bR, long bQ, long bK,
                                                  long unsafe){
        StringBuilder moves = new StringBuilder();
        boolean forWhite = Character.isUpperCase(type.charAt(0));
        long used = getBitBoardFromType(type, wP, wN, wB, wR, wQ, wK, bP, bN, bB, bR, bQ, bK);
        
        long i = used & -used;
        long possibility;
        while (i != 0){
            int startLoc = 63 - Long.numberOfLeadingZeros(i);

            possibility = movePossibilitiesFor1PieceFromALoc(type, startLoc, false);
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
                }
                if (!"K".equals(type) && !"k".equals(type) || (1L << endLoc & unsafe) == 0){
                    appendRookMoveNote(moves, type, startLoc);
                    captureOtherNote(type, moves, endLoc,
                            forWhite ? bP : wP, forWhite ? bN : wN, forWhite ? bB : wB, forWhite ? bR : wR, forWhite ? bQ : wQ
                    );
                    appendMergedBoardsFinalVal(moves, 0, wN, wB, wR, wQ, wK, 0, bN, bB, bR, bQ, bK);
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
    
    private static long getBitBoardFromType(String type,
                                            long wP, long wN, long wB, long wR, long wQ, long wK,
                                            long bP, long bN, long bB, long bR, long bQ, long bK){
        switch (type.charAt(0)){
            case 'P' -> {
                return wP;
            }
            case 'N' -> {
                return wN;
            }
            case 'B' -> {
                return wB;
            }
            case 'R' -> {
                return wR;
            }
            case 'Q' -> {
                return wQ;
            }
            case 'K' -> {
                return wK;
            }
            case 'p' -> {
                return bP;
            }
            case 'n' -> {
                return bN;
            }
            case 'b' -> {
                return bB;
            }
            case 'r' -> {
                return bR;
            }
            case 'q' -> {
                return bQ;
            }
            case 'k' -> {
                return bK;
            }
        }
        return 0;
    }
    
    private static long movePossibilities(String type, long w, long b){
        boolean forWhite = Character.isUpperCase(type.charAt(0));
        long used = forWhite ? w : b;
        
        long i = used & -used;
        long possibility = 0L;
        while (i != 0){
            int startLoc = 63 - Long.numberOfLeadingZeros(i);

            possibility |= movePossibilitiesFor1PieceFromALoc(type, startLoc, true);

            used &= ~i;
            i = used & -used;
        }
        
        return possibility;
    }

    private static long movePossibilitiesFor1PieceFromALoc(String type, int startLoc, boolean lookForUnSafePlaces){
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
        if (!lookForUnSafePlaces)
            possibility &= (forWhite ? HITTABLE_BY_WHITE : HITTABLE_BY_BLACK) | EMPTY;
        return possibility;
    }

    private static void castle(String type, StringBuilder castlePlaces,
                                 boolean wKC, boolean wQC, boolean bKC, boolean bQC,
                                 long wG, long wN, long wB, long wR, long wQ, long wK,
                                 long bP, long bN, long bB, long bR, long bQ, long bK,
                                 long occupied, long unsafe){

        boolean forWhite = Character.isUpperCase(type.charAt(0));

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
        appendMergedBoardsFinalVal(moves, wG, wN, wB, wR, wQ, wK, bP, bN, bB, bR, bQ, bK);
        moves.append("_");
    }

    private static void captureOtherNote(String type, StringBuilder moves, int endLoc, long hitPawn, long hitKnight, long hitBishop, long hitRook, long hitQueen){
        String capture = "-C";
        String capturedType = "";
        boolean capturerIsWhite = Character.isUpperCase(type.charAt(0));
        if (((1L << endLoc) & hitPawn) != 0){
            capturedType = capturerIsWhite ? "p" : "P";
        } else if (((1L << endLoc) & hitKnight) != 0) {
            capturedType = capturerIsWhite ? "n" : "N";
        } else if (((1L << endLoc) & hitBishop) != 0) {
            capturedType = capturerIsWhite ? "b" : "B";
        } else if (((1L << endLoc) & hitRook) != 0) {
            capturedType = capturerIsWhite ? "r" : "R";
        } else if (((1L << endLoc) & hitQueen) != 0) {
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
                                                   long wP, long wN, long wB, long wR, long wQ, long wK,
                                                   long bP, long bN, long bB, long bR, long bQ, long bK) {
        moves.append("/");
        moves.append(evaluationOfAMoveWithOutFieldValues(moves, wP, wN, wB, wR, wQ, wK, bP, bN, bB, bR, bQ, bK));
//        moves.append(evaluationOfAMoveWithFieldValues(moves, wP, wN, wB, wR, wQ, wK, bP, bN, bB, bR, bQ, bK));
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

    public static double evaluationOfAMoveWithFieldValues(StringBuilder moves,
                                                          long wP, long wN, long wB, long wR, long wQ, long wK,
                                                          long bP, long bN, long bB, long bR, long bQ, long bK){

        return evaluationOfAMoveWithFieldValues(
                moves.toString(),
                wP, wN, wB, wR, wQ, wK,
                bP, bN, bB, bR, bQ, bK);
    }

    public static double evaluationOfAMoveWithFieldValues(AiNode node){
        return evaluationOfAMoveWithFieldValues(node.getTheMoveWhatsCreatedIt(),
                node.getWP(), node.getWN(), node.getWB(), node.getWR(), node.getWQ(), node.getWK(),
                node.getBB(), node.getBN(), node.getBB(), node.getBR(), node.getBQ(), node.getBK());
    }

    public static double evaluationOfAMoveWithFieldValues(String moves,
                                                          long wP, long wN, long wB, long wR, long wQ, long wK,
                                                          long bP, long bN, long bB, long bR, long bQ, long bK){
        if (moves.isEmpty()){
            return
                    evaluationOfFullBitBoard(
                            moves,
                            wP, wN, wB, wR, wQ, wK,
                            bP, bN, bB, bR, bQ, bK,
                            'N', 'Z');

        }else {
            return
                    evaluationOfFullBitBoard(
                            moves,
                            wP, wN, wB, wR, wQ, wK,
                            bP, bN, bB, bR, bQ, bK,
                            moves.charAt(moves.length() - 3), moves.charAt(moves.length() - 2));
        }
    }


    private static double evaluationOfFullBitBoard(String moves,
                                                   long wP, long wN, long wB, long wR, long wQ, long wK,
                                                   long bP, long bN, long bB, long bR, long bQ, long bK,
                                                   char CifItsCapture, char capturedType) {

        double basicEval = evaluationWithFieldValuesOnBitBoards(
                wP, wN, wB, wR, wQ, wK,
                bP, bN, bB, bR, bQ, bK);

        String[] s = moves.split("_");
        String move = s[s.length - 1];
        String[] moveParts = move.split("-");
        String type = moveParts[0];
        int startIndex = Integer.parseInt(moveParts[1]);
        if (moveParts[2].charAt(moveParts[2].length() - 1) == '/')
            moveParts[2] = moveParts[2].substring(0, moveParts[2].length() - 1);
        int endIndex = Integer.parseInt(moveParts[2]);

        boolean forWhite = Character.isUpperCase(type.charAt(0));

        if (("P".equals(type) && (1L << endIndex & ROW_8) != 0) ||
                ("p".equals(type) && (1L << endIndex & ROW_1) != 0)){ // Pawn Promotion
            if ('C' == CifItsCapture){
                basicEval -= getBaseFieldValue(endIndex, capturedType);
            }
            basicEval -= getBaseFieldValue(startIndex, type);
            basicEval += getBaseFieldValue(endIndex, "P".equals(type) ? 'Q' : 'q');
        } else if ('C' == CifItsCapture){ // Simple capture with any piece
            basicEval -= getBaseFieldValue(startIndex, type);
            basicEval -= getBaseFieldValue(endIndex, capturedType);
            basicEval -= forWhite ?
                    -requireNonNull(getPieceType(capturedType)).getValueOfPieceType() :
                    requireNonNull(getPieceType(capturedType)).getValueOfPieceType();
            basicEval += getBaseFieldValue(endIndex, type.charAt(0));
        } else if (("P".equals(type) || "p".equals(type)) &&
                (Math.abs(startIndex - endIndex) == 7 || Math.abs(startIndex - endIndex) == 9)) { // EmPassant capture
            if ("P".equals(type) && (1L << endIndex & bP) == 0){ // with white
                basicEval -= Math.abs(startIndex - endIndex) == 7 ? 
                        getBaseFieldValue(startIndex - 1, 'p') :
                        getBaseFieldValue(startIndex + 1, 'p');
                basicEval -= requireNonNull(getPieceType('p')).getValueOfPieceType();
            }
            if ("p".equals(type) && (1L << endIndex & wP) == 0){
                basicEval -= Math.abs(startIndex - endIndex) == 7 ?
                        getBaseFieldValue(startIndex + 1, 'P') :
                        getBaseFieldValue(startIndex - 1, 'P');
                basicEval -= requireNonNull(getPieceType('P')).getValueOfPieceType();
            }
        } else if (("K".equals(type) || "k".equals(type)) && Math.abs(startIndex - endIndex) == 2) { // Castle
            basicEval -= getBaseFieldValue(startIndex, type);
            basicEval += getBaseFieldValue(endIndex, type);

            int[] kingCastleIndexes = indexesInCastle(whiteDown, "K".equals(type), true);
            int[] queenCastleIndexes = indexesInCastle(whiteDown, "K".equals(type), false);

            basicEval -= endIndex == kingCastleIndexes[0] ?
                    getBaseFieldValue(kingCastleIndexes[2], forWhite ? 'R' : 'r') :
                    getBaseFieldValue(queenCastleIndexes[2], forWhite ? 'R' : 'r');
            basicEval += endIndex == kingCastleIndexes[0] ?
                    getBaseFieldValue(kingCastleIndexes[1], forWhite ? 'R' : 'r') :
                    getBaseFieldValue(queenCastleIndexes[1], forWhite ? 'R' : 'r');
        } else {
            basicEval -= getBaseFieldValue(startIndex, type);
            basicEval += getBaseFieldValue(endIndex, type);
        }

        return basicEval;
    }

    private static double evaluationWithFieldValuesOnBitBoards(long wP, long wN, long wB, long wR, long wQ, long wK,
                                                               long bP, long bN, long bB, long bR, long bQ, long bK){
        double finalValue = 0;
        for (int i = 0; i < 63; i++) {
            if ((1L << i & wP) != 0){
                finalValue += getBaseFieldValue(i, 'P') + PAWN_BASE_VALUE;
            } else if ((1L << i & wN) != 0){
                finalValue += getBaseFieldValue(i, 'N') + KNIGHT_OR_BISHOP_BASE_VALUE;
            } else if ((1L << i & wB) != 0){
                finalValue += getBaseFieldValue(i, 'B') + KNIGHT_OR_BISHOP_BASE_VALUE;
            } else if ((1L << i & wR) != 0){
                finalValue += getBaseFieldValue(i, 'R') + ROOK_BASE_VALUE;
            } else if ((1L << i & wQ) != 0){
                finalValue += getBaseFieldValue(i, 'Q') + QUEEN_BASE_VALUE;
            } else if ((1L << i & wK) != 0) {
                finalValue += getBaseFieldValue(i, 'K') + KING_BASE_VALUE;
            } else if ((1L << i & bP) != 0){
                finalValue += getBaseFieldValue(i, 'p') - PAWN_BASE_VALUE;
            } else if ((1L << i & bN) != 0){
                finalValue -= getBaseFieldValue(i, 'n') - KNIGHT_OR_BISHOP_BASE_VALUE;
            } else if ((1L << i & bB) != 0){
                finalValue += getBaseFieldValue(i, 'b') - KNIGHT_OR_BISHOP_BASE_VALUE;
            } else if ((1L << i & bR) != 0){
                finalValue += getBaseFieldValue(i, 'r') - ROOK_BASE_VALUE;
            } else if ((1L << i & bQ) != 0){
                finalValue += getBaseFieldValue(i, 'q') - QUEEN_BASE_VALUE;
            }else if ((1L << i & bK) != 0) {
                finalValue += getBaseFieldValue(i, 'k') - KING_BASE_VALUE;
            }
        }
        return finalValue;
    }

    public static double evaluationOfAMoveWithOutFieldValues(StringBuilder moves,
                                                             long wP, long wN, long wB, long wR, long wQ, long wK,
                                                             long bP, long bN, long bB, long bR, long bQ, long bK){
        return evaluationOfAMoveWithOutFieldValues(
                moves.toString(),
                wP, wN, wB, wR, wQ, wK,
                bP, bN, bB, bR, bQ, bK);
    }

    public static double evaluationOfAMoveWithOutFieldValues(String moves,
                                                              long wP, long wN, long wB, long wR, long wQ, long wK,
                                                              long bP, long bN, long bB, long bR, long bQ, long bK){
        if (moves.isEmpty()){
            return
                    evaluationOfAMoveWithOutFieldValues(
                            moves,
                            wP, wN, wB, wR, wQ, wK,
                            bP, bN, bB, bR, bQ, bK,
                            'N', 'Z');

        } else {
            return
                    evaluationOfAMoveWithOutFieldValues(
                            moves,
                            wP, wN, wB, wR, wQ, wK,
                            bP, bN, bB, bR, bQ, bK,
                            moves.charAt(moves.length() - 3), moves.charAt(moves.length() - 2));
        }
    }

    private static double evaluationOfAMoveWithOutFieldValues(String moves,
                                                             long wP, long wN, long wB, long wR, long wQ, long wK,
                                                             long bP, long bN, long bB, long bR, long bQ, long bK,
                                                             char CifItsCapture, char capturedType){

        double finalValue = evaluationWithOutFieldValuesOnBitBoards(
                wP, wN, wB, wR, wQ, wK,
                bP, bN, bB, bR, bQ, bK);

        String[] s = moves.split("_");
        String move = s[s.length - 1];
        String[] moveParts = move.split("-");
        String type = moveParts[0];

        boolean forWhite = Character.isUpperCase(type.charAt(0));

        if ('C' == CifItsCapture){
            finalValue -= forWhite ?
                                -requireNonNull(getPieceType(capturedType)).getValueOfPieceType() :
                                 requireNonNull(getPieceType(capturedType)).getValueOfPieceType();
        }

        return finalValue;
    }

    private static double evaluationWithOutFieldValuesOnBitBoards(long wP, long wN, long wB, long wR, long wQ, long wK,
                                                                  long bP, long bN, long bB, long bR, long bQ, long bK){
        double finalValue = 0;
        for (int i = 0; i < 63; i++) {
            if ((1L << i & wP) != 0){
                finalValue += PAWN_BASE_VALUE;
            } else if ((1L << i & wN) != 0){
                finalValue += KNIGHT_OR_BISHOP_BASE_VALUE;
            } else if ((1L << i & wB) != 0){
                finalValue += KNIGHT_OR_BISHOP_BASE_VALUE;
            } else if ((1L << i & wR) != 0){
                finalValue += ROOK_BASE_VALUE;
            } else if ((1L << i & wQ) != 0){
                finalValue += QUEEN_BASE_VALUE;
            } else if ((1L << i & wK) != 0) {
                finalValue += KING_BASE_VALUE;
            } else if ((1L << i & bP) != 0){
                finalValue -= PAWN_BASE_VALUE;
            } else if ((1L << i & bN) != 0){
                finalValue -= KNIGHT_OR_BISHOP_BASE_VALUE;
            } else if ((1L << i & bB) != 0){
                finalValue -= KNIGHT_OR_BISHOP_BASE_VALUE;
            } else if ((1L << i & bR) != 0){
                finalValue -= ROOK_BASE_VALUE;
            } else if ((1L << i & bQ) != 0){
                finalValue -= QUEEN_BASE_VALUE;
            }else if ((1L << i & bK) != 0) {
                finalValue -= KING_BASE_VALUE;
            }
        }
        return finalValue;
    }

}
