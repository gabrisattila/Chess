package classes.Ai.BitBoard;

import lombok.*;

import java.util.ArrayList;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Ai.BitBoard.BBVars.*;

@Getter
@Setter
public class BitBoards {

    //region Fields

    //endregion


    //region Methods

    //region Fen To BitBoards

    public static void setUpStarterBitBoards(){
//        whiteDown = false;
        String starterFen = whiteDown ? usualFens.get("whiteDownStarter") : usualFens.get("blackDownStarter");
        setUpBitBoard(starterFen);
        
    }

    public static void setUpBitBoard(String fen){
//        FenToBoard(fen, getBoard());
        String bbFen = FenToBitBoardFen(fen);
        String pieces = bbFen.split(" ")[0];
        for (char c : englishPieceLetters) {
            switch (c){
                case 'p' -> blackPawn = FenPiecesToBitBoard(pieces, c);
                case 'n' -> blackKnight = FenPiecesToBitBoard(pieces, c);
                case 'b' -> blackBishop = FenPiecesToBitBoard(pieces, c);
                case 'r' -> blackRook = FenPiecesToBitBoard(pieces, c);
                case 'q' -> blackQueen = FenPiecesToBitBoard(pieces, c);
                case 'k' -> blackKing = FenPiecesToBitBoard(pieces, c);
                case 'P' -> whitePawn = FenPiecesToBitBoard(pieces, c);
                case 'N' -> whiteKnight = FenPiecesToBitBoard(pieces, c);
                case 'B' -> whiteBishop = FenPiecesToBitBoard(pieces, c);
                case 'R' -> whiteRook = FenPiecesToBitBoard(pieces, c);
                case 'Q' -> whiteQueen = FenPiecesToBitBoard(pieces, c);
                case 'K' -> whiteKing = FenPiecesToBitBoard(pieces, c);
            }
        }
    }

    /**
     * @param Fen always should be the first part of a BitBoardFen String
     * @param piece the type of BitBoard we want to create
     * @return the BitBoard
     */
    public static long FenPiecesToBitBoard(String Fen, char piece){
        long bitBoard = 0;

        //This will be out shifter or we can say cursor
        int Field = 63;

        long actualBit;
        for (int i = 0; i < Fen.length(); i++) {
            actualBit = 1L << Field;
            switch (Fen.charAt(i)) {
                case '/':
                    Field += 1;
                    break;
                case '1':
                    break;
                case '2':
                    Field -= 1;
                    break;
                case '3':
                    Field -= 2;
                    break;
                case '4':
                    Field -= 3;
                    break;
                case '5':
                    Field -= 4;
                    break;
                case '6':
                    Field -= 5;
                    break;
                case '7':
                    Field -= 6;
                    break;
                case '8':
                    Field -= 7;
                    break;
                default:
                    if (Fen.charAt(i) == piece) {
                        bitBoard |= actualBit;
                    }
            }
            Field--;
        }

        return bitBoard;
    }

    public static int emPassantToBitBoard(String emPassantChance){
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

    private static void printBitBoard(boolean forWhite, long piece, char type) {
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


    //region Moves

    public static long mergeFullBitBoard(ArrayList<Long> pieceBoards){
        long fullBoard = 0L;
        for (long board : pieceBoards) {
            fullBoard |= board;
        }
        return fullBoard;
    }

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

    //endregion

    //endregion

}
