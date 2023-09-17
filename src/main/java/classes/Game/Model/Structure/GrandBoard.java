package classes.Game.Model.Structure;

import classes.GUI.FrameParts.ViewField;
import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import lombok.*;

import java.util.ArrayList;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;

@Getter
@Setter
public class GrandBoard {

    //region Methods

    protected int X;

    protected int Y;

    protected ArrayList<ArrayList<IField>> fields;

    protected ArrayList<IPiece> pieces;

    //endregion


    //region Constructor

    public GrandBoard(int x, int y){
        X = x;
        Y = y;
    }

    //endregion


    //region Methods



    public void pieceSetUp(String FEN) throws ChessGameException {

    }

    public IField getField(int i, int j){
        return fields.get(i).get(j);
    }

    public IField getField(Location location){
        return getField(location.getI(), location.getJ());
    }

    public IField getField(IPiece piece){
        return getField(piece.getI(), piece.getJ());
    }

    public IPiece getPiece(int i, int j){
        return fields.get(i).get(j).getPiece();
    }

    public IPiece getPiece(Location location){
        return getPiece(location.getI(), location.getJ());
    }

    public IPiece getPiece(IField field){
        return fields.get(field.getI()).get(field.getJ()).getPiece();
    }


    public void cleanBoard() throws ChessGameException {
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                if (getField(i, j).isGotPiece())
                    getField(i, j).clean();
            }
        }
        pieces.clear();
    }

    public void updatePiecesRanges()  throws ChessGameException, InterruptedException{

    }


    //endregion

}
