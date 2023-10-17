package classes.Game.Model.Structure;

import classes.GUI.FrameParts.ViewField;
import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;

import java.util.ArrayList;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;

public interface IBoard {

    default void boardSetUp(IBoard board, ArrayList<ArrayList<IField>> fields){
        IField field;
        String fieldColor;
        Location location;
        ArrayList<IField> row;

        for (int i = 0; i < MAX_HEIGHT; i++) {
            row = new ArrayList<>();
            for (int j = 0; j < MAX_WIDTH; j++) {
                location = new Location(i, j);
                fieldColor = tableIf(WHITE_STRING, BLACK_STRING, i, j);
                if (board instanceof Board)
                    field = new Field(location, fieldColor);
                else
                    field = new ViewField(location, fieldColor);
                row.add(field);
            }
            fields.add(row);
        }
    }

    default void pieceSetUp(String FEN){
        String fenPieces = FEN.split(" ")[0];
        int separateIndex = FEN.indexOf(' ');
        String fenOtherParts = FEN.substring(separateIndex);

        for (int i = 0; i < fenPieces.length(); i++) {
            if ('p' == fenPieces.charAt(i) || 'P' == fenPieces.charAt(i) || 'r' == fenPieces.charAt(i) || 'R' == fenPieces.charAt(i) ||
                    'q' == fenPieces.charAt(i) || 'Q' == fenPieces.charAt(i) || 'n' == fenPieces.charAt(i) || 'N' == fenPieces.charAt(i)){
                fenPieces = translate(fenPieces);
                break;
            }
        }

        FEN = fenPieces + fenOtherParts;

        try {
            FenToBoard(FEN, this);
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    void cleanBoard() throws ChessGameException;

    ArrayList<ArrayList<IField>> getFields();

    ArrayList<IPiece> getPieces();

    IField getField(int i, int j);

    IField getField(Location location);

    IField getField(IPiece piece);

    IPiece getPiece(int i, int j) throws ChessGameException;

    IPiece getPiece(Location location) throws ChessGameException;

    IPiece getPiece(IField field);

    void rangeUpdater() throws ChessGameException, InterruptedException;
}
