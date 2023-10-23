package classes.Ai;

import classes.GUI.FrameParts.*;
import classes.Game.I18N.*;
import classes.Game.I18N.Location;
import classes.Game.Model.Structure.*;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;

/**
 * FEN string structure:
 *  "piecesOnTheBoard WhiteOrBlackToPlay castleCases_OR_-forEachImpossibleCastleOption emPassantPossibility_OR_-"
 */
public class FenConverter {

    public static void FenToBoard(String fen, IBoard board) throws ChessGameException {

        String[] separatedFen = fen.split(" ");

        String pieces = separatedFen[0];

        if (fenIsWrong(pieces))
            throw new ChessGameException("This Fen String doesn't suites for the table sizes");
        char currentChar;
        int sor = 0, oszlop = 0;
        PieceAttributes piece;
        board.cleanBoard();

        String emPassant = separatedFen[3];

        for (int i = 0; i < pieces.length(); i++) {
            if (containsLocation(MAX_WIDTH + 1, MAX_HEIGHT + 1, sor, oszlop)){
                currentChar = pieces.charAt(i);
                if (currentChar == '/'){
                    oszlop = 0;
                    sor++;
                } else {
                    if (Character.isDigit(currentChar)){
                        oszlop += Character.getNumericValue(currentChar);
                    }else {
                        classes.Game.Model.Structure.IField f = board.getField(sor, oszlop);
                        if (! ((f instanceof Field ) || (f instanceof ViewField))){
                            throw new ChessGameException(f, BAD_TYPE_MSG);
                        }

                        piece = charToPieceAttributes(currentChar);

                        enemyAndOwnStartRowFenToBoard(piece);

                        if (f instanceof Field) {
                            Piece pieceForParams = piece.isWhite() ? whitePieceSet.getFirstEmpty() : blackPieceSet.getFirstEmpty();
                            pieceForParams.setAttributes(piece);
                            pieceForParams.setLocation(new Location(sor, oszlop));
                            pieceForParams.setBoard((Board) board);
                            if (piece.getType() == K){
                                if (piece.isWhite())
                                    ((Board) board).setWhiteKing(pieceForParams);
                                else
                                    ((Board) board).setBlackKing(pieceForParams);
                            }

                            emPassantFenToBoard(emPassant, piece);

                            board.getPieces().add(pieceForParams);
                            board.getField(sor, oszlop).setPiece(pieceForParams);
                        } else {
                            ViewPiece pieceForParams = new ViewPiece(createSourceStringFromGotAttributes(piece));
                            pieceForParams.setI(sor);
                            pieceForParams.setJ(oszlop);
                            board.getPieces().add(
                                    pieceForParams
                            );
                            board.getField(sor, oszlop).setPiece(pieceForParams);
                        }
                        oszlop++;
                    }
                }
            }
        }

        castleCaseFenToBoard(separatedFen[2]);
    }

    public static String BoardToFen(IBoard board) throws ChessGameException {
        int counterForRows = 0;
        StringBuilder fenToReturn = new StringBuilder();
        for (int i = 0; i < MAX_WIDTH; i++) {
            for (int j = 0; j < MAX_HEIGHT; j++) {
                classes.Game.Model.Structure.IField f = board.getFields().get(i).get(j);
                if (! ((f instanceof Field ) || (f instanceof ViewField))){
                    throw new ChessGameException(f, BAD_TYPE_MSG);
                }
                if (f.isGotPiece()){
                    if (counterForRows != 0)
                        fenToReturn.append(counterForRows);
                    counterForRows = 0;
                    fenToReturn.append(pieceAttributesToChar(f.getPiece().getAttributes()));
                }else {
                    counterForRows++;
                }
            }
            if (counterForRows != 0)
                fenToReturn.append(counterForRows);
            counterForRows = 0;
            fenToReturn.append('/');
        }
        fenToReturn.deleteCharAt(fenToReturn.length() - 1);

        fenToReturn.append(' ');

        fenToReturn.append(toPlay);

        fenToReturn.append(' ');

        if (whiteSmallCastleHappened)
            fenToReturn.append('-');
        else
            fenToReturn.append('K');

        if (whiteBigCastleHappened)
            fenToReturn.append('-');
        else
            fenToReturn.append('V');

        if (blackSmallCastleHappened)
            fenToReturn.append('-');
        else
            fenToReturn.append('k');

        if (blackBigCastleHappened)
            fenToReturn.append('-');
        else
            fenToReturn.append('v');

        fenToReturn.append(' ');

//        if (board instanceof Board)
//            if (((Board) board).getThereWasEmPassantPossibility().getFirst())
//                fenToReturn.append(((Board) board).getThereWasEmPassantPossibility().getSecond());
//            else
//                fenToReturn.append('-');
//        else
            fenToReturn.append('-');

        return fenToReturn.toString();
    }

    public static boolean fenIsWrong(String FEN){
        boolean fenIsWrong = false;
        String fen = FEN.split(" ")[0];
        if(MAX_HEIGHT - 1 == countOccurrences(fen, '/')){
            String[] parts = fen.split("/");
            int lengthOfPart = 0;
            for (String part : parts) {
                for (char c : part.toCharArray()) {
                    if (Character.isDigit(c)){
                        lengthOfPart += Integer.parseInt(String.valueOf(c));
                    }else {
                        lengthOfPart++;
                    }
                }
                if (MAX_HEIGHT != lengthOfPart){
                    fenIsWrong = true;
                }
                lengthOfPart = 0;
            }
        }else {
            fenIsWrong = true;
        }

        return fenIsWrong;
    }

    public static PieceAttributes charToPieceAttributes(char c){
        PieceType type;
        String color = Character.isUpperCase(c) ? "WHITE" : "BLACK";
        type = charToPieceType(c);
        return new PieceAttributes(type, color);
    }

    public static PieceType charToPieceType(char c){
        PieceType type;
        type = switch (c) {
            case 'h', 'H' -> H;
            case 'f', 'F' -> F;
            case 'b', 'B' -> B;
            case 'v', 'V' -> V;
            case 'k', 'K' -> K;
            default -> G;
        };
        return type;
    }

    private static char pieceAttributesToChar(PieceAttributes piece){
        char pieceChar;
        pieceChar = switch (piece.getType()){
            case G -> 'G';
            case H -> 'H';
            case F -> 'F';
            case B -> 'B';
            case V -> 'V';
            case K -> 'K';
            default -> throw new IllegalStateException("Itt nem megengedett ez a típus: " + piece.getType());
        };
        if (!piece.isWhite())
            pieceChar = Character.toLowerCase(pieceChar);
        return pieceChar;
    }

    private static String createSourceStringFromGotAttributes(PieceAttributes attributes){
        StringBuilder sb = new StringBuilder();
        sb.append("src\\main\\resources\\Figura_Images\\");

        sb.append(attributes.isWhite() ? "w_" : "b_");
        String type;
        switch (attributes.getType()){
            case G -> type = "gyalog";
            case H -> type = "huszar";
            case F -> type = "futo";
            case B -> type = "bastya";
            case V -> type = "vezer";
            default -> type = "kiraly";
        }
        sb.append(type);
        sb.append(".png");

        return sb.toString();
    }

    private static void emPassantFenToBoard(String emPassant, PieceAttributes piece){
        if (!("-".equals(emPassant)) && piece.getType() == G){
            char sorInChar = emPassant.charAt(0);
            char oszlopInChar = emPassant.charAt(1);
            piece.setPossibleEmPassant(
                    Integer.parseInt(String.valueOf(sorInChar)),
                    Integer.parseInt(String.valueOf(oszlopInChar))
            );
        }
    }

    private static void enemyAndOwnStartRowFenToBoard(PieceAttributes piece){
        if (theresOnlyOneAi){
            piece.setEnemyStartRow(
                    whiteAiNeeded ?
                            (piece.isWhite() ? 0 : 7) :
                            (piece.isWhite() ? 7 : 0)
            );
        }else {
            piece.setEnemyStartRow(
                    piece.isWhite() ? 7 : 0
            );
        }
        piece.setOwnStartRow(piece.getEnemyAndOwnStartRow().getFirst() == 7 ? 1 : 6);
    }

    private static void castleCaseFenToBoard(String castleCases){

        whiteSmallCastleHappened = 'K' != castleCases.charAt(0);

        whiteBigCastleHappened = 'V' != castleCases.charAt(1);

        blackSmallCastleHappened = 'k' != castleCases.charAt(2);

        blackBigCastleHappened = 'v' != castleCases.charAt(3);

    }

}
