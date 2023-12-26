package classes.Game.Model.Structure;

import classes.GUI.FrameParts.ViewField;
import classes.Game.I18N.Location;

import java.util.ArrayList;

import static classes.Game.I18N.METHODS.translate;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Logic.FenConverter.*;

public interface IBoard {

    default void boardSetUp(IBoard board, ArrayList<ArrayList<IField>> fields){
        IField field;
        String fieldColor;
        Location Location;
        ArrayList<IField> row;

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

    default <T> T tableIf(T o2, T o3, int i, int j){
        T o1;
        if(i % 2 == 0){
            if (j % 2 == 0){
                o1 = o3;
            }else {
                o1 = o2;
            }
        }else {
            if (j % 2 == 0){
                o1 = o2;
            }else {
                o1 = o3;
            }
        }
        return o1;
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

        FenToBoard(FEN, this);
    }

    static void convertOneBoardToAnother(IBoard what, IBoard to){
        FenToBoard(BoardToFen(what), to);
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

}
