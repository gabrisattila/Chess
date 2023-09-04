package classes.GUI.FrameParts;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.Model.Structure.Board;

import static classes.Game.I18N.METHODS.isNull;
import static classes.Game.I18N.METHODS.passViewBoardInFenTo;
import static classes.Game.I18N.VARS.MUTUABLES.*;

public class ViewBoard extends Board<ViewField> {

    //region Fields

    private static ViewBoard viewBoard;

    //endregion


    //region Constructor

    protected ViewBoard(int x, int y, Class<ViewField> fieldViewClass) throws ChessGameException {
        super(x, y, fieldViewClass);
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
    public void updatePieceRanges() throws ChessGameException, InterruptedException {

        passViewBoardInFenTo(getBoard());

        super.updatePieceRanges();

        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {

                if (getField(i, j).isGotPiece() && !getBoard().getField(i, j).isGotPiece())
                    throw new RuntimeException("Nem megfelelően íródott át a FEN boardra.");

                if (getField(i, j).isGotPiece())
                    for (Location l : getBoard().getField(i, j).getPiece().getPossibleRange()) {
                        getField(i, j).getPiece().getOptions().add(getField(l));
                    }
            }
        }
    }

    //endregion

}
