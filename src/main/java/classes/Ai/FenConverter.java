package classes.Ai;

import classes.GUI.FrameParts.*;
import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;
import classes.Game.Model.Structure.*;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;

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
                        IField f = board.getField(sor, oszlop);
                        if (! ((f instanceof Field ) || (f instanceof ViewField))){
                            throw new ChessGameException(f, BAD_TYPE_MSG);
                        }
                        piece = charToPieceAttributes(currentChar);
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

        String castleCases = separatedFen[1];

        whiteSmallCastleHappened = 'K' == castleCases.charAt(0);

        whiteBigCastleHappened = 'V' == castleCases.charAt(1);

        blackSmallCastleHappened = 'k' == castleCases.charAt(2);

        blackBigCastleHappened = 'v' == castleCases.charAt(3);
    }

    public static String BoardToFen(IBoard board) throws ChessGameException {
        int counterForRows = 0;
        StringBuilder fenToReturn = new StringBuilder();
        for (int i = 0; i < MAX_WIDTH; i++) {
            for (int j = 0; j < MAX_HEIGHT; j++) {
                IField f = board.getFields().get(i).get(j);
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

}
