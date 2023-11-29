package classes.Ai;

import classes.GUI.FrameParts.*;
import classes.Game.I18N.*;
import classes.Game.I18N.Location;
import classes.Game.Model.Structure.*;

import java.util.Arrays;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

/**
 * FEN string structure:
 *  "piecesOnTheBoard WhiteOrBlackToPlay castleCases emPassantChance stepCount evenOrOddStep"
 */
public class FenConverter {

    public static void FenToBoard(String fen, IBoard board) {

        String[] separatedFen = fen.split(" ");

        String pieces = separatedFen[0];

        if (fenIsWrong(pieces)) {
            throw new ChessGameException(fenErrorMessage(pieces));
        }
        char currentChar;
        int sor = 0, oszlop = 0;
        PieceAttributes piece;
        board.cleanBoard();

        whiteToPlay = 'w' == separatedFen[1].charAt(0);

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
                        IField f = board.getField(sor, oszlop);
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

                            if (!"-".equals(emPassant))
                                emPassantFenToBoard(emPassant, piece, sor, oszlop);

                            board.getPieces().add(pieceForParams);
                            board.getField(sor, oszlop).setPiece(pieceForParams);
                        } else {
                            ViewPiece pieceForParams = new ViewPiece(createSourceStringFromGotAttributes(piece), piece);
                            pieceForParams.setI(sor);
                            pieceForParams.setJ(oszlop);
                            board.getPieces().add(pieceForParams);
                            board.getField(sor, oszlop).setPiece(pieceForParams);
                        }
                        oszlop++;
                    }
                }
            }
        }

        castleCaseFenToBoard(separatedFen[2]);

        evenOrOddStep = '1' == separatedFen[5].charAt(0) ? 1 : 0;
    }

    public static String BoardToFen(IBoard board)  {
        int counterForRows = 0;
        StringBuilder fenToReturn = new StringBuilder();
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
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

        Character toPlay = evenOrOddStep == 0 ? 'w' : 'b';
        fenToReturn.append(toPlay);

        fenToReturn.append(' ');

        if (whiteSmallCastleEnabled)
            fenToReturn.append('K');
        else
            fenToReturn.append('-');

        if (whiteBigCastleEnabled)
            fenToReturn.append('V');
        else
            fenToReturn.append('-');

        if (blackSmallCastleEnabled)
            fenToReturn.append('k');
        else
            fenToReturn.append('-');

        if (blackBigCastleEnabled)
            fenToReturn.append('v');
        else
            fenToReturn.append('-');

        fenToReturn.append(' ');

        fenToReturn.append(emPassantChance);

        fenToReturn.append(' ');

        fenToReturn.append(stepNumber);

        fenToReturn.append(' ');

        evenOrOddStep = 'w' == toPlay ? 0 : 1;
        fenToReturn.append(evenOrOddStep);

        fenToReturn.append(' ');

        return fenToReturn.toString();
    }

    private static boolean fenIsWrong(String FEN){
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

                if (MAX_WIDTH != lengthOfPart){
                    fenIsWrong = true;
                }
                lengthOfPart = 0;
            }
        }else {
            fenIsWrong = true;
        }

        return fenIsWrong;
    }
    
    private static String fenErrorMessage(String fen){
        StringBuilder errorMessage = new StringBuilder("Ez a fen:\n");
        errorMessage.append(fen).append('\n');
        errorMessage.append("nem passzol a megszabott tábla méretekhez, mert ");
        String[] rows = fen.split("/");
        boolean rowCountEqualsMaxHeight = rows.length == MAX_HEIGHT;
        boolean colCountEqualsMaxWidth = Arrays.stream(rows).allMatch(r -> r.length() == MAX_WIDTH);
        if (!rowCountEqualsMaxHeight && !colCountEqualsMaxWidth){
            badRowNum(errorMessage, rows);
            badColNum(errorMessage, rows, ". \nTovábbá ez a sor: ");
        } else if (!rowCountEqualsMaxHeight) {
            errorMessage.append("a megadott fen ").append(rows.length).append(" sort tartalmaz, holott az elvárt: ").append(MAX_HEIGHT);
        } else if (!colCountEqualsMaxWidth) {
            badColNum(errorMessage, rows, ". \nEz a sor: ");
        }
        return errorMessage.toString();
    }
    
    private static void badRowNum(StringBuilder errorMessage, String[] rows){
        errorMessage.append("a megadott fen ").append(rows.length).append(" sort tartalmaz, holott az elvárt: ").append(MAX_HEIGHT);
    }
    
    private static void badColNum(StringBuilder errorMessage, String[] rows, String openingLine) {
        errorMessage.append(openingLine);
        String badRow = "";
        for (String row : rows) {
            if (row.length() != MAX_WIDTH) {
                badRow = row;
                break;
            }
        }
        errorMessage
                .append(badRow)
                .append(" nem megfelelő hosszúságú, hiszen a kívánt hossz ")
                .append(MAX_WIDTH)
                .append(" a sor pedig ")
                .append(badRow.length())
                .append(" hosszú.\n");
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

    public static String createSourceStringFromGotAttributes(PieceAttributes attributes){
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

    private static void emPassantFenToBoard(String emPassant, PieceAttributes piece, int sor, int oszlop){
        if (piece.isWhite() == whiteToPlay &&
                piece.getType() == G &&
                Math.abs(sor - Character.getNumericValue(emPassantChance.charAt(0))) == 1 &&
                Math.abs(oszlop - Character.getNumericValue(emPassantChance.charAt(1))) == 1
        )
            emPassantHelper(emPassant, piece);
    }

    public static void emPassantHelper(String emPassant, PieceAttributes piece){
            char sorInChar = emPassant.charAt(0);
            char oszlopInChar = emPassant.charAt(1);
            piece.setPossibleEmPassant(
                    Integer.parseInt(String.valueOf(sorInChar)),
                    Integer.parseInt(String.valueOf(oszlopInChar))
            );
    }

    private static void enemyAndOwnStartRowFenToBoard(PieceAttributes piece){

        if(whiteDown){
            piece.setEnemyStartRow(piece.isWhite() ? 7 : 0);
        }else {
            piece.setEnemyStartRow(piece.isWhite() ? 0 : 7);
        }

        piece.setOwnStartRow(piece.getEnemyAndOwnStartRow().getFirst() == 7 ? 1 : 6);
    }

    private static void castleCaseFenToBoard(String castleCases){

        whiteSmallCastleEnabled = 'K' == castleCases.charAt(0);

        whiteBigCastleEnabled = 'V' == castleCases.charAt(1);

        blackSmallCastleEnabled = 'k' == castleCases.charAt(2);

        blackBigCastleEnabled = 'v' == castleCases.charAt(3);

    }

}
