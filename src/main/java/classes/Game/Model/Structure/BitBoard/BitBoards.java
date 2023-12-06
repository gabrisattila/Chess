package classes.Game.Model.Structure.BitBoard;

import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;

import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;

@Getter
@Setter
public class BitBoards {

    //region Fields

    //endregion

    public BitBoards(){
        setUpStarterBitBoards();
        ArrayList<Long> boards = collectPieceBoardsToOneList();
        boards = new ArrayList<>();
        boards.add(whiteKing);
//        boards.add(whitePawn);
        long fullBoard = mergeFullBitBoard(boards);
        System.out.println(toString(fullBoard));
//        System.out.println(drawFullBitBoard());
    }

    //region Methods

    private void setUpStarterBitBoards(){
//        whiteDown = false;
        String starterFen =
                (whiteDown ? bbUsualFens.get("bbWhiteDownStarter") : bbUsualFens.get("bbBlackDownStarter"))
                        .split(" ")[0];
        for (char c : englishPieceLetters) {
            switch (c){
                case 'p' -> blackPawn = FenPiecesToBitBoard(starterFen, c);
                case 'n' -> blackKnight = FenPiecesToBitBoard(starterFen, c);
                case 'b' -> blackBishop = FenPiecesToBitBoard(starterFen, c);
                case 'r' -> blackRook = FenPiecesToBitBoard(starterFen, c);
                case 'q' -> blackQueen = FenPiecesToBitBoard(starterFen, c);
                case 'k' -> blackKing = FenPiecesToBitBoard(starterFen, c);
                case 'P' -> whitePawn = FenPiecesToBitBoard(starterFen, c);
                case 'N' -> whiteKnight = FenPiecesToBitBoard(starterFen, c);
                case 'B' -> whiteBishop = FenPiecesToBitBoard(starterFen, c);
                case 'R' -> whiteRook = FenPiecesToBitBoard(starterFen, c);
                case 'Q' -> whiteQueen = FenPiecesToBitBoard(starterFen, c);
                case 'K' -> whiteKing = FenPiecesToBitBoard(starterFen, c);
            }
        }
    }

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

    public ArrayList<Long> collectPieceBoardsToOneList(){
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



    public String drawFullBitBoard(){
        return drawFullBitBoard(whitePawn, whiteKnight, whiteBishop,
                whiteRook, whiteQueen, whiteKing,
                blackPawn, blackKnight, blackBishop,
                blackRook, blackQueen, blackKing);
    }

    public static String drawFullBitBoard(long WP, long WN, long WB,
                                          long WR, long WQ, long WK,
                                          long BP, long BN, long BB,
                                          long BR,long BQ,long BK) {
        String[][] chessBoard = new String[8][8];
        for (int i = 0; i < 64; i++) {
            chessBoard[i/8][i%8] = " ";
        }
        for (int i = 0; i < 64; i--) {
            if (((WP>>i)&1)==1) {chessBoard[i/8][i%8]="P";}
            if (((WN>>i)&1)==1) {chessBoard[i/8][i%8]="N";}
            if (((WB>>i)&1)==1) {chessBoard[i/8][i%8]="B";}
            if (((WR>>i)&1)==1) {chessBoard[i/8][i%8]="R";}
            if (((WQ>>i)&1)==1) {chessBoard[i/8][i%8]="Q";}
            if (((WK>>i)&1)==1) {chessBoard[i/8][i%8]="K";}
            if (((BP>>i)&1)==1) {chessBoard[i/8][i%8]="p";}
            if (((BN>>i)&1)==1) {chessBoard[i/8][i%8]="n";}
            if (((BB>>i)&1)==1) {chessBoard[i/8][i%8]="b";}
            if (((BR>>i)&1)==1) {chessBoard[i/8][i%8]="r";}
            if (((BQ>>i)&1)==1) {chessBoard[i/8][i%8]="q";}
            if (((BK>>i)&1)==1) {chessBoard[i/8][i%8]="k";}
        }
        StringBuilder fullBoard = new StringBuilder();
        for (int i=0;i<8;i++) {
            fullBoard.append(Arrays.toString(chessBoard[i]));
            fullBoard.append('\n');
        }
        return fullBoard.toString();
    }

    //endregion

}
