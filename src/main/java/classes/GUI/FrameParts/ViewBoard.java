package classes.GUI.FrameParts;

import classes.Game.I18N.*;
import classes.Game.Model.Structure.*;
import lombok.*;

import java.util.*;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;
import static classes.Game.Model.Structure.Board.*;

@Getter
@Setter
public class ViewBoard extends GrandBoard {

    //region Fields

    private static ViewBoard viewBoard;

    private ArrayList<ArrayList<ViewField>> fields;

    private Set<ViewPiece> pieces;

    //endregion


    //region Constructor

    protected ViewBoard(int x, int y, Class<ViewField> fieldViewClass) throws ChessGameException {
        boardSetUp();
    }

    public static ViewBoard getViewBoard() throws ChessGameException {
        if (isNull(viewBoard)){
            viewBoard = new ViewBoard(MAX_WIDTH, MAX_HEIGHT, ViewField.class);
        }
        return viewBoard;
    }


    //endregion


    //region Methods

    @Override
    public void boardSetUp() {

    }

    @Override
    public void pieceSetUp(String FEN) throws ChessGameException {

    }

    @Override
    public IField getField(int i, int j) {
        return fields.get(i).get(j);
    }

    @Override
    public IField getField(Location loc) {
        return getField(loc.getI(), loc.getJ());
    }

    @Override
    public IPiece getPiece(int i, int j) {
        return getField(i, j).getPiece();
    }

    @Override
    public IPiece getPiece(Location loc) {
        return getPiece(loc.getI(), loc.getJ());
    }

    @Override
    public IPiece getPiece(IField field) {
        return field.getPiece();
    }

    @Override
    public void cleanBoard() throws ChessGameException {
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                if (getField(i, j).isGotPiece())
                    getField(i, j).clean();
            }
        }
    }

    public void updatePiecesRanges() throws ChessGameException, InterruptedException {

        passViewBoardInFenTo(getBoard());


        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {

                if (getField(i, j).isGotPiece() && !getBoard().getField(i, j).isGotPiece())
                    throw new RuntimeException("Nem megfelelően íródott át a FEN boardra.");

                if (getField(i, j).isGotPiece())
                    for (Location l : getBoard().getField(i, j).getPiece().getPossibleRange()) {
                        ((ViewPiece)getField(i, j).getPiece()).getOptions().add((ViewField)getField(l));
                    }
            }
        }
    }

    //endregion

}
