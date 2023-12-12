package classes.Ai;

import classes.GUI.FrameParts.*;
import classes.Game.I18N.*;
import classes.Game.I18N.Location;
import classes.Game.Model.Structure.*;

import static classes.Game.I18N.ChessGameException.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

/**
 * FEN string structure:
 *  "piecesOnTheBoard WhiteOrBlackToPlay castleCases emPassantChance stepCount evenOrOddStep"
 */
public class FenConverter {

    /**
     * @param fen original fen
     * @return the same cols and rows, but in reverse order in the pieces part. Just like we use it for a BitBoard
     */
    public static String FenToBitBoardFen(String fen){
        String[] originFenParts = fen.split(" ");
        String pieces = originFenParts[0];
        String originOtherParts = originFenParts[1] + ' ' + originFenParts[2] + ' ' + originFenParts[3] + ' ' +
                                    originFenParts[4] + ' ' + originFenParts[5];
        String[] originPiecesParts = pieces.split("/");
        StringBuilder bbFenPieces = new StringBuilder();
        for (int i = originPiecesParts.length - 1; i >= 0; i--) {
            bbFenPieces.append(originPiecesParts[i]);
            if (i != 0)
                bbFenPieces.append('/');
        }
        bbFenPieces.append(' ');
        bbFenPieces.append(originOtherParts);
        return bbFenPieces.toString();
    }

    private static void FenToBoard(String fen, IBoard board, boolean isAiFen) {
        synchronized (pauseFlag){

            waitOnPause();

            String[] separatedFen = fen.split(" ");

            String pieces = separatedFen[0];

            throwFenErrorIfNeeded(pieces);

            char currentChar;
            int sor = 0, oszlop = 0;
            PieceAttributes piece;
            board.cleanBoard();

            whiteToPlay = 'w' == separatedFen[1].charAt(0);

            String emPassant = separatedFen[3];

            for (int i = 0; i < pieces.length(); i++) {
                if (containsLocation(MAX_WIDTH + 1, MAX_HEIGHT + 1, sor, oszlop)) {
                    currentChar = pieces.charAt(i);
                    if (currentChar == '/') {
                        oszlop = 0;
                        sor++;
                    } else {
                        if (Character.isDigit(currentChar)) {
                            oszlop += Character.getNumericValue(currentChar);
                        } else {
                            IField f = board.getField(sor, oszlop);

                            throwBadTypeErrorIfNeeded(
                                    new Object[]
                                            {f, Field.class.getName(), ViewField.class.getName(),
                                                    "Emiatt most nem tudom a fen-t átírni " + board.getClass().getName() + "-ra."}
                            );

                            piece = charToPieceAttributes(currentChar);

                            enemyAndOwnStartRowFenToBoard(piece);

                            if (f instanceof Field) {
                                Piece pieceForParams = piece.isWhite() ? whitePieceSet.getFirstEmpty() : blackPieceSet.getFirstEmpty();
                                pieceForParams.setAttributes(piece);
                                pieceForParams.setLocation(new Location(sor, oszlop));
                                pieceForParams.setBoard((Board) board);
                                if (piece.getType() == K) {
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

            evenOrOddStep = '1' == separatedFen[isAiFen ? 4 : 5].charAt(0) ? 1 : 0;
        }
    }

    private static String BoardToFen(IBoard board, boolean isAiFen) {
        synchronized (pauseFlag){

            waitOnPause();

            int counterForRows = 0;
            StringBuilder fenToReturn = new StringBuilder();
            for (int i = 0; i < MAX_HEIGHT; i++) {
                for (int j = 0; j < MAX_WIDTH; j++) {
                    IField f = board.getFields().get(i).get(j);

                    throwBadTypeErrorIfNeeded(
                            new Object[]
                                    {f, Field.class.getName(), ViewField.class.getName(), "Emiatt itt nem tudom a " +
                                            (board instanceof Board ? "Board-ot átírni Fen-re." : "ViewBoard-ot átírni Fen-re.")}
                    );

                    if (f.isGotPiece()) {
                        if (counterForRows != 0)
                            fenToReturn.append(counterForRows);
                        counterForRows = 0;
                        fenToReturn.append(pieceAttributesToChar(f.getPiece().getAttributes()));
                    } else {
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
                fenToReturn.append('Q');
            else
                fenToReturn.append('-');

            if (blackSmallCastleEnabled)
                fenToReturn.append('k');
            else
                fenToReturn.append('-');

            if (blackBigCastleEnabled)
                fenToReturn.append('q');
            else
                fenToReturn.append('-');

            fenToReturn.append(' ');

            fenToReturn.append(emPassantChance.charAt(0));
            if (emPassantChance.length() == 2) {
                fenToReturn.append(emPassantChance.charAt(1));
            }

            if (!isAiFen) {

                fenToReturn.append(' ');

                fenToReturn.append(stepNumber);
            }

            fenToReturn.append(' ');

            evenOrOddStep = 'w' == toPlay ? 0 : 1;
            fenToReturn.append(evenOrOddStep);

//        fenToReturn.append(lastStep);

            return fenToReturn.toString();
        }
    }

    public static String AiFenToFen(String fen){
        String[] fenParts = fen.split(" ");
        stepNumber++;
        return fenParts[0] +
                ' ' +
                fenParts[1] +
                ' ' +
                fenParts[2] +
                ' ' +
                fenParts[3] +
                ' ' +
                stepNumber +
                ' ' +
                fenParts[4];
    }

    public static void AiFenToBoard(String fen, IBoard board){
        FenToBoard(fen, board, true);
    }

    public static String BoardToAiFen(IBoard board){
        return BoardToFen(board, true);
    }

    public static void FenToBoard(String fen, IBoard board){
        FenToBoard(fen, board, false);
    }

    public static String BoardToFen(IBoard board){
        return BoardToFen(board, false);
    }

    public static boolean FenEquals(String fen1, String fen2){
        return removeStepCountFromFen(fen1).equals(removeStepCountFromFen(fen2));
    }

    public static String removeStepCountFromFen(String fen){
        String[] fenParts = fen.split(" ");
        return fenParts[0] +
                ' ' +
                fenParts[1] +
                ' ' +
                fenParts[2] +
                ' ' +
                fenParts[3] +
                ' ' +
                fenParts[5];
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
            case 'n', 'N' -> N;
            case 'b', 'B' -> B;
            case 'r', 'R' -> R;
            case 'q', 'Q' -> Q;
            case 'k', 'K' -> K;
            default -> P;
        };
        return type;
    }

    private static char pieceAttributesToChar(PieceAttributes piece){
        char pieceChar;
        pieceChar = switch (piece.getType()){
            case P -> 'P';
            case N -> 'N';
            case B -> 'B';
            case R -> 'R';
            case Q -> 'Q';
            case K -> 'K';
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
            case P -> type = "pawn";
            case N -> type = "night";
            case B -> type = "bishop";
            case R -> type = "rook";
            case Q -> type = "queen";
            default -> type = "king";
        }
        sb.append(type);
        sb.append(".png");

        return sb.toString();
    }

    private static void emPassantFenToBoard(String emPassant, PieceAttributes piece, int sor, int oszlop){
        if (piece.isWhite() == whiteToPlay &&
                piece.getType() == P &&
                Math.abs(sor - Character.getNumericValue(emPassant.charAt(0))) == 1 &&
                Math.abs(oszlop - Character.getNumericValue(emPassant.charAt(1))) == 1
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

    public static void castleCaseFenToBoard(String castleCases){

        whiteSmallCastleEnabled = 'K' == castleCases.charAt(0);

        whiteBigCastleEnabled = 'Q' == castleCases.charAt(1);

        blackSmallCastleEnabled = 'k' == castleCases.charAt(2);

        blackBigCastleEnabled = 'q' == castleCases.charAt(3);

    }

}
