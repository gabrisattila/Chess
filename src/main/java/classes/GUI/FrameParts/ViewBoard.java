package classes.GUI.FrameParts;

import classes.Game.I18N.ChessGameException;
import classes.Game.Model.Structure.Board;

import java.util.ArrayList;

import static classes.Game.I18N.METHODS.isNull;
import static classes.Game.I18N.VARS.MUTUABLES.MAX_HEIGHT;
import static classes.Game.I18N.VARS.MUTUABLES.MAX_WIDTH;

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



    //endregion

}
