package classes.Game.Model.Structure;

import classes.GUI.FrameParts.ViewField;
import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;

import java.util.ArrayList;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

public interface IBoard {

    default void boardSetUp(IBoard board, ArrayList<ArrayList<IField>> fields){
        classes.Game.Model.Structure.IField field;
        String fieldColor;
        Location Location;
        ArrayList<classes.Game.Model.Structure.IField> row;

        for (int i = 0; i < MAX_HEIGHT; i++) {
            row = new ArrayList<>();
            for (int j = 0; j < MAX_WIDTH; j++) {
                Location = new Location(i, j);
                fieldColor = tableIf(WHITE_STRING, BLACK_STRING, i, j);
                if (board instanceof Board)
                    field = new Field(Location, fieldColor);
                else
                    field = new ViewField(Location, fieldColor);
                row.add(field);
            }
            fields.add(row);
        }
    }

    default void pieceSetUp(String FEN){
        String[] fenParts = FEN.split(" ");
        String fenPieces = fenParts[0];
        String toPlay = fenParts[1];
        String castle = fenParts[2];
        String emPassant = fenParts[3];
        String stepNum = fenParts[4];
        String evenOrOddStep = fenParts[5];

        for (int i = 0; i < fenPieces.length(); i++) {
            if ('p' == fenPieces.charAt(i) || 'P' == fenPieces.charAt(i) || 'r' == fenPieces.charAt(i) || 'R' == fenPieces.charAt(i) ||
                    'q' == fenPieces.charAt(i) || 'Q' == fenPieces.charAt(i) || 'n' == fenPieces.charAt(i) || 'N' == fenPieces.charAt(i)){
                fenPieces = translate(fenPieces);
                castle = translate(castle);
                break;
            }
        }

        FEN = fenPieces + " " + toPlay + " " + castle + " " + emPassant + " " + stepNum + " " + evenOrOddStep;

        try {
            FenToBoard(FEN, this);
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    void cleanBoard() ;

    ArrayList<ArrayList<IField>> getFields();

    ArrayList<IPiece> getPieces();

    IField getField(int i, int j);

    IField getField(Location Location);

    IField getField(IPiece piece);

    IPiece getPiece(int i, int j) ;

    IPiece getPiece(Location Location) ;

    IPiece getPiece(IField field) ;

    void rangeUpdater() ;

    default Location getKingsOriginPlace(boolean forWhite){
        if (whiteDown){
            return new Location(4, forWhite ? 0 : 7);
        }else {
            return new Location(3, forWhite ? 7 : 0);
        }
    }
}
