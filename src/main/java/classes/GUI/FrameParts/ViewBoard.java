package classes.GUI.FrameParts;

import classes.Game.I18N.*;
import classes.Game.Model.Structure.*;
import lombok.*;

import java.util.*;

import static classes.Game.I18N.Helpers.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;
import static classes.Game.Model.Structure.Board.*;
import static classes.Game.I18N.VARS.*;

@Getter
@Setter
public class ViewBoard implements IBoard {

    //region Fields

    private int X;

    private int Y;

    private static ViewBoard viewBoard;

    private ArrayList<ArrayList<IField>> fields;

    private ArrayList<IPiece> pieces;

    //endregion


    //region Constructor

    protected ViewBoard(int x, int y) {
        X = x;
        Y = y;
        fields = new ArrayList<>();
        boardSetUp(this, fields);
        pieces = new ArrayList<>();
    }

    public static ViewBoard getViewBoard() throws ChessGameException {
        if (isNull(viewBoard)){
            viewBoard = new ViewBoard(MAX_WIDTH, MAX_HEIGHT);
        }
        return viewBoard;
    }


    //endregion


    //region Methods

    //region GetBy

    @Override
    public IField getField(int i, int j){
        return getFields().get(i).get(j);
    }

    @Override
    public IField getField(Location location){
        return getField(location.getI(), location.getJ());
    }

    @Override
    public IField getField(IPiece piece){
        return getField(piece.getI(), piece.getJ());
    }

    @Override
    public IPiece getPiece(int i, int j){
        return getFields().get(i).get(j).getPiece();
    }

    @Override
    public IPiece getPiece(Location location){
        return getPiece(location.getI(), location.getJ());
    }

    @Override
    public IPiece getPiece(IField field){
        return getField(field.getI(), field.getJ()).getPiece();
    }


    //endregion

    @Override
    public void cleanBoard(){
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                if (getField(i, j).isGotPiece()) {
                    try {
                        getField(i, j).clean();
                    } catch (ChessGameException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        pieces.clear();
    }

    @Override
    public void updatePiecesRanges() throws ChessGameException, InterruptedException {

        passViewBoardInFenTo(getBoard());
        getBoard().updatePiecesRanges();

//        boardToString(getBoard());

        System.out.println("getBoard().updateRanges() happened.");

        for (IPiece p : getBoard().getPieces()) {
            for (Location l : p.getPossibleRange()) {
                getPiece(p.getI(), p.getJ()).getPossibleRange().add(l);
            }
        }
    }

    //endregion

}
