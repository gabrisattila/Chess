package classes.Controller;

import classes.GUI.FrameParts.ViewField;
import classes.GUI.FrameParts.ViewPiece;
import classes.Model.I18N.*;
import classes.Model.Structure.*;

import static classes.Model.I18N.VARS.FINALS.*;

public class FenConverter {


    /**
     * @param fen original fen
     * @return the same cols and rows, but in reverse order in the pieces part. Just like we use it for a BitBoards
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

    public static void FenToBoard(String fen, IBoard board) {

        String[] separatedFen = fen.split(" ");

        String pieces = separatedFen[0];

        ChessGameException.throwFenErrorIfNeeded(pieces);
        char currentChar;
        int sor = 0, oszlop = 0;
        PieceAttributes piece;
        board.cleanBoard();

        VARS.MUTABLE.whiteToPlay = 'w' == separatedFen[1].charAt(0);

        String emPassant = separatedFen[3];

        for (int i = 0; i < pieces.length(); i++) {
            if (METHODS.containsLocation(MAX_WIDTH + 1, MAX_HEIGHT + 1, sor, oszlop)) {
                currentChar = pieces.charAt(i);
                if (currentChar == '/') {
                    oszlop = 0;
                    sor++;
                } else {
                    if (Character.isDigit(currentChar)) {
                        oszlop += Character.getNumericValue(currentChar);
                    } else {
                        IField f = board.getField(sor, oszlop);

                        ChessGameException.throwBadTypeErrorIfNeeded(
                                new Object[]
                                        {f, Field.class.getName(), ViewField.class.getName(),
                                                "Emiatt most nem tudom a fen-t átírni " + board.getClass().getName() + "-ra."}
                        );

                        piece = PieceAttributes.charToPieceAttributes(currentChar);

                        enemyAndOwnStartRowFenToBoard(piece);

                        if (f instanceof Field) {
                            Piece pieceForParams = piece.isWhite() ? VARS.MUTABLE.whitePieceSet.getFirstEmpty() : VARS.MUTABLE.blackPieceSet.getFirstEmpty();
                            pieceForParams.setAttributes(piece);
                            pieceForParams.setLocation(new Location(sor, oszlop));
                            pieceForParams.setBoard((Board) board);
                            if (piece.getType() == PieceType.K) {
                                if (piece.isWhite())
                                    ((Board) board).setWhiteKing(pieceForParams);
                                else
                                    ((Board) board).setBlackKing(pieceForParams);
                            }
                            if (!"-".equals(emPassant)) emPassantFenToBoard(emPassant, piece, sor, oszlop);
                            board.getPieces().add(pieceForParams);
                            board.getField(sor, oszlop).setPiece(pieceForParams);
                        } else {
                            ViewPiece pieceForParams = new ViewPiece(PieceAttributes.createSourceStringFromGotAttributes(piece), piece);
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

        VARS.MUTABLE.evenOrOddStep = '1' == separatedFen[5].charAt(0) ? 1 : 0;

    }

    public static String BoardToFen(IBoard board) {
        int counterForRows = 0;
        StringBuilder fenToReturn = new StringBuilder();
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                IField f = board.getFields().get(i).get(j);
                ChessGameException.throwBadTypeErrorIfNeeded(
                        new Object[]
                                {f, Field.class.getName(), ViewField.class.getName(), "Emiatt itt nem tudom a " +
                                        (board instanceof Board ? "Board-ot átírni Fen-re." : "ViewBoard-ot átírni Fen-re.")}
                );
                if (f.isGotPiece()) {
                    if (counterForRows != 0)
                        fenToReturn.append(counterForRows);
                    counterForRows = 0;
                    fenToReturn.append(PieceAttributes.pieceAttributesToChar(f.getPiece().getAttributes()));
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
        Character toPlay = VARS.MUTABLE.evenOrOddStep == 0 ? 'w' : 'b';
        fenToReturn.append(toPlay);

        fenToReturn.append(' ');

        if (VARS.MUTABLE.whiteSmallCastleEnabled)
            fenToReturn.append('K');
        else
            fenToReturn.append('-');

        if (VARS.MUTABLE.whiteBigCastleEnabled)
            fenToReturn.append('Q');
        else
            fenToReturn.append('-');

        if (VARS.MUTABLE.blackSmallCastleEnabled)
            fenToReturn.append('k');
        else
            fenToReturn.append('-');

        if (VARS.MUTABLE.blackBigCastleEnabled)
            fenToReturn.append('q');
        else
            fenToReturn.append('-');

        fenToReturn.append(' ');
        fenToReturn.append(VARS.MUTABLE.emPassantChance.charAt(0));

        if (VARS.MUTABLE.emPassantChance.length() == 2) {
            fenToReturn.append(VARS.MUTABLE.emPassantChance.charAt(1));
        }

        fenToReturn.append(' ');
        fenToReturn.append(VARS.MUTABLE.stepNumber);

        fenToReturn.append(' ');

        VARS.MUTABLE.evenOrOddStep = 'w' == toPlay ? 0 : 1;

        fenToReturn.append(VARS.MUTABLE.evenOrOddStep);

        return fenToReturn.toString();
    }

    public static String createFenForHappenedList(String fen){
        String[] fenParts = fen.split(" ");
        return fenParts[0] + " " + fenParts[1] + " " + fenParts[2] + " " + fenParts[3] + " " + fenParts[5];
    }

    private static void emPassantFenToBoard(String emPassant, PieceAttributes piece, int sor, int oszlop){
        if (piece.isWhite() == VARS.MUTABLE.whiteToPlay &&
                piece.getType() == PieceType.P &&
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

        if(VARS.MUTABLE.whiteDown){
            piece.setEnemyStartRow(piece.isWhite() ? 7 : 0);
        }else {
            piece.setEnemyStartRow(piece.isWhite() ? 0 : 7);
        }

        piece.setOwnStartRow(piece.getEnemyAndOwnStartRow().getFirst() == 7 ? 1 : 6);
    }

    public static void castleCaseFenToBoard(String castleCases){

        VARS.MUTABLE.whiteSmallCastleEnabled = 'K' == castleCases.charAt(0);

        VARS.MUTABLE.whiteBigCastleEnabled = 'Q' == castleCases.charAt(1);

        VARS.MUTABLE.blackSmallCastleEnabled = 'k' == castleCases.charAt(2);

        VARS.MUTABLE.blackBigCastleEnabled = 'q' == castleCases.charAt(3);

    }
}
