package classes.Game.Model.Structure.BitBoard;

import classes.Game.I18N.PieceType;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.Move.*;

@Getter
@Setter
public class BitBoards {

    //region Fields

    //endregion

    //region Constructor

    public BitBoards(String fen){
        setUpBitBoard(fen);
    }

    //endregion

    //region Methods

    //region Fen To BitBoards

    public static void setUpStarterBitBoards(){
//        whiteDown = false;
        String starterFen = whiteDown ? usualFens.get("whiteDownStarter") : usualFens.get("blackDownStarter");
        setUpBitBoard(starterFen);
        
    }

    public static void setUpBitBoard(String fen){
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

    //endregion

    //region Bit Boards To Fen

    public static String bitBoardsToFenPieces() {
        StringBuilder fenPieces = new StringBuilder();
        StringBuilder row = new StringBuilder();
        int counter = 0;
        for (int i = 0; i < MAX_WIDTH * MAX_HEIGHT; i++) {
            counter = upgradeCounter(i, counter);
            counter = getCounterAndAppendToFen(row, counter, i, whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing, true);
            counter = getCounterAndAppendToFen(row, counter, i, blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing, false);
            if ((i + 1) % 8 == 0){
                if (counter != 0)
                    row.append(counter);
                counter = 0;
                row.reverse();
                if (i + 1 != MAX_WIDTH * MAX_HEIGHT)
                    row.append('/');
                fenPieces.append(row);
                row.setLength(0);
            }
        }

        return fenPieces.toString();
    }

    private static int getCounterAndAppendToFen(StringBuilder fenPieces, int counter, int i,
                                                long pawn, long knight, long bishop, long rook, long queen, long king,
                                                boolean forWhite) {
        counter = appendToOnGoingFen(pawn, fenPieces, counter, i, forWhite ? 'G' : 'g');
        counter = appendToOnGoingFen(knight, fenPieces, counter, i, forWhite ? 'H' : 'h');
        counter = appendToOnGoingFen(bishop, fenPieces, counter, i, forWhite ? 'F' : 'f');
        counter = appendToOnGoingFen(rook, fenPieces, counter, i, forWhite ? 'B' : 'b');
        counter = appendToOnGoingFen(queen, fenPieces, counter, i, forWhite ? 'V' : 'v');
        counter = appendToOnGoingFen(king, fenPieces, counter, i, forWhite ? 'K' : 'k');
        return counter;
    }

    private static int appendToOnGoingFen(long bitBoardOfAPiece, StringBuilder fenPieces, int counter, int i, char pieceType){
        if ( ((bitBoardOfAPiece >> i) & 1) == 1 ) {
            if (counter != 0)
                fenPieces.append(counter);
            fenPieces.append(pieceType);
            counter = 0;
        }
        return counter;
    }

    private static int upgradeCounter(int i, int counter){
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
        return  ((bitBoardOfAPiece >> i) & 1) != 1;
    }

    //endregion


    //region Moves

    /**
     * @param bitBoardOfAPiece We know what is the pieceType of the bitBoard
     * @return all the start indexes of the pieces from the bitBoard
     */
    public ArrayList<Integer> getPiecesStartIndexesOnABoard(long bitBoardOfAPiece){
        ArrayList<Integer> indexesOfPieces = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            if (((1L << i) & bitBoardOfAPiece) == 1){
                indexesOfPieces.add(i);
            }
        }
        return indexesOfPieces;
    }

    public static ArrayList<Long> collectPieceBoardsToOneList(){
        ArrayList<Long> boards = new ArrayList<>();
        boards.add(whitePawn);
        boards.add(whiteKnight);
        boards.add(whiteBishop);
        boards.add(whiteRook);
        boards.add(whiteQueen);
        boards.add(whiteKing);
        boards.add(blackPawn);
        boards.add(blackKnight);
        boards.add(blackBishop);
        boards.add(blackRook);
        boards.add(blackQueen);
        boards.add(blackKing);
        return boards;
    }

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
