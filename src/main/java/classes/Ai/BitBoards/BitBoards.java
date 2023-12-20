package classes.Ai.BitBoards;

import classes.Game.I18N.ChessGameException;
import lombok.*;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Ai.BitBoards.BBVars.*;

@Getter
@Setter
public class BitBoards {

    //region Fen To BitBoards

    public static void setUpBitBoard(String fen){
        String bbFen = FenToBitBoardFen(fen);
        String[] fenParts = bbFen.split(" ");
        String pieces = fenParts[0];
        whiteToPlay = "w".equals(fenParts[1]);
        int bitCounter = 63;
        for (int i = 0; i < pieces.length(); i++) {
            if (Character.isLetter(pieces.charAt(i))){
                bitBoards[getIndex(pieces.charAt(i))] = setBit(bitBoards[getIndex(pieces.charAt(i))], bitCounter);
                bitCounter--;
            } else if (Character.isDigit(pieces.charAt(i))){
                bitCounter -= Character.getNumericValue(pieces.charAt(i));
            }
        }

        parseCastlingRights(fenParts[2]);

        BBVars.bbEmPassant = emPassantToBitBoardEmPassant(emPassantChance);

    }

    public static int getIndex(char piece){
        switch (piece){
            case 'P' -> {
                return wPawnI;
            }
            case 'N' -> {
                return wKnightI;
            }
            case 'B' -> {
                return wBishopI;
            }
            case 'R' -> {
                return wRookI;
            }
            case 'Q' -> {
                return wQueenI;
            }
            case 'K' -> {
                return wKingI;
            }
            case 'p' -> {
                return bPawnI;
            }
            case 'n' -> {
                return bKnightI;
            }
            case 'b' -> {
                return bBishopI;
            }
            case 'r' -> {
                return bRookI;
            }
            case 'q' -> {
                return bQueenI;
            }
            case 'k' -> {
                return bKingI;
            }
        }
        return -1;
    }

    private static void parseCastlingRights(String castling){
        for (int i = 0; i < 4; i++) {
            switch (castling.charAt(i)){
                case 'K' -> castle |= wK;
                case 'Q' -> castle |= wQ;
                case 'k' -> castle |= bK;
                case 'q' -> castle |= bQ;
            }
        }
    }

    public static int emPassantToBitBoardEmPassant(String emPassantChance){
        int emPassant = "-".equals(emPassantChance) ? -1 : Integer.parseInt(emPassantChance);
        emPassant = emPassant == -1 ? -1 : (emPassant / 10) * 8 + oppositeInsideEight.get(emPassant % 10);
        return emPassant;
    }

    //endregion

    //region Bit Boards To Fen

    public static String bitBoardsToFen(
            boolean whiteTurn, int emPassant, boolean wKC, boolean wQC, boolean bKC, boolean bQC,
            long whitePawn, long whiteKnight, long whiteBishop, long whiteRook, long whiteQueen, long whiteKing,
            long blackPawn, long blackKnight, long blackBishop, long blackRook, long blackQueen, long blackKing) {
        StringBuilder fen = new StringBuilder();
        StringBuilder row = new StringBuilder();
        int counter = 0;

//        System.out.println();
//        printBitBoards(true, whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing);
//        printBitBoards(false, blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing);

        for (int i = 0; i < MAX_WIDTH * MAX_HEIGHT; i++) {
            counter = upgradeCounter(i, counter,
                    whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing,
                    blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing);
            counter = getCounterAndAppendToFen(row, counter, i, whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing, true);
            counter = getCounterAndAppendToFen(row, counter, i, blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing, false);
            if ((i + 1) % MAX_WIDTH == 0){
                if (counter != 0)
                    row.append(counter);
                counter = 0;
                row.reverse();
                if (i + 1 != MAX_WIDTH * MAX_HEIGHT)
                    row.append('/');
                fen.append(row);
                row.setLength(0);
            }
        }

        fen.append(" ").append(whiteTurn ? "w" : "b");
        fen.append(" ");
        fen.append(wKC ? "K" : "-");
        fen.append(wQC ? "Q" : "-");
        fen.append(bKC ? "k" : "-");
        fen.append(bQC ? "q" : "-");
        fen.append(" ");
        String emPassantString = emPassant == -1 ? "-" : String.valueOf(emPassant / 8);
        emPassantString += emPassant == -1 ? "" : String.valueOf(emPassant % 8);
        fen.append(emPassantString);
        fen.append(" ");
        fen.append(stepNumber++);
        fen.append(" ");
        fen.append(whiteTurn ? "0" : "1");

        return fen.toString();
    }
    
    private static void printBitBoards(boolean forWhite, long pawn, long knight, long bishop, long rook, long queen, long king) {
        printBitBoard(forWhite, pawn, 'P');
        printBitBoard(forWhite, knight, 'N');
        printBitBoard(forWhite, bishop, 'B');
        printBitBoard(forWhite, rook, 'R');
        printBitBoard(forWhite, queen, 'Q');
        printBitBoard(forWhite, king, 'K');
    }

    public static void printBitBoard(boolean forWhite, long piece, char type) {
        System.out.println((forWhite ? "White: " : "Black: ") + type);
        System.out.println(BitBoards.toString(piece));
    }

    private static int getCounterAndAppendToFen(StringBuilder fenPieces, int counter, int i,
                                                long pawn, long knight, long bishop, long rook, long queen, long king,
                                                boolean forWhite) {
        counter = appendToOnGoingFen(pawn, fenPieces, counter, i, forWhite ? 'P' : 'p');
        counter = appendToOnGoingFen(knight, fenPieces, counter, i, forWhite ? 'N' : 'n');
        counter = appendToOnGoingFen(bishop, fenPieces, counter, i, forWhite ? 'B' : 'b');
        counter = appendToOnGoingFen(rook, fenPieces, counter, i, forWhite ? 'R' : 'r');
        counter = appendToOnGoingFen(queen, fenPieces, counter, i, forWhite ? 'Q' : 'q');
        counter = appendToOnGoingFen(king, fenPieces, counter, i, forWhite ? 'K' : 'k');
        return counter;
    }

    private static int appendToOnGoingFen(long bitBoardOfAPiece, StringBuilder fenPieces, int counter, int i, char pieceType){
        if ((1L << i & bitBoardOfAPiece) != 0) {
            if (counter != 0)
                fenPieces.append(counter);
            fenPieces.append(pieceType);
            counter = 0;
        }
        return counter;
    }

    private static int upgradeCounter(int i, int counter,
                                      long whitePawn, long whiteKnight, long whiteBishop, long whiteRook, long whiteQueen, long whiteKing,
                                      long blackPawn, long blackKnight, long blackBishop, long blackRook, long blackQueen, long blackKing){
        if (
                counterCanBeUpgraded(whitePawn, i) && counterCanBeUpgraded(whiteBishop, i) && counterCanBeUpgraded(whiteKnight, i) &&
                counterCanBeUpgraded(whiteRook, i) && counterCanBeUpgraded(whiteQueen, i) && counterCanBeUpgraded(whiteKing, i) &&
                counterCanBeUpgraded(blackPawn, i) && counterCanBeUpgraded(blackBishop, i) && counterCanBeUpgraded(blackKnight, i) &&
                counterCanBeUpgraded(blackRook, i) && counterCanBeUpgraded(blackQueen, i) && counterCanBeUpgraded(blackKing, i)
        ){
            counter++;
        }
        return counter;
    }

    private static boolean counterCanBeUpgraded(long bitBoardOfAPiece, int i){
        return  (1L << i & bitBoardOfAPiece) == 0;
    }

    //endregion

    //region Methods

    public static String toString(long bitBoard){
        StringBuilder sb = new StringBuilder();
        for (int i = 63; i >= 0; i--) {
            long mask = 1L << i;
            long maskedBit = (bitBoard & mask);
            sb.append(maskedBit == 0 ? '0' : '1');
            if ((i % 8) == 0) {
                sb.append('\n'); // Minden 8. bit után sortörés
            } else {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static long getBit(long bitBoard, int index){
        if (index < 0 || index > 63)
            throw new ChessGameException("Index isn't in the range of bitBoard");
        return bitBoard & 1L << index;
    }

    public static long setBit(long bitBoard, int index){
        if (index < 0 || index > 63)
            throw new ChessGameException("Index isn't in the range of bitBoard");
        return bitBoard | 1L << index;
    }

    public static long removeBit(long bitBoard, int index){
        if (index < 0 || index > 63)
            throw new ChessGameException("Index isn't in the range of bitBoard");
        return (getBit(bitBoard, index) == 0 ? bitBoard & ~(1L << index) : 0);
    }

    //endregion

}
