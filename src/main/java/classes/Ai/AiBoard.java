package classes.Ai;

import classes.Game.I18N.ChessGameException;
import classes.Game.Model.Structure.Board;
import classes.Game.Model.Structure.Field;
import classes.Game.Model.Structure.Piece;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

import static classes.Game.I18N.METHODS.isNull;
import static classes.Game.I18N.METHODS.replace;
import static classes.Game.I18N.VARS.FINALS.nums;
import static classes.Game.I18N.VARS.MUTUABLES.MAX_HEIGHT;
import static classes.Game.I18N.VARS.MUTUABLES.MAX_WIDTH;

@Getter
@Setter
public class AiBoard extends Board<Field> {

    //region Fields

    private static AiBoard aiBoard;

    //endregion


    //region Constructor

    protected AiBoard(int x, int y, Class<Field> fieldClass) throws ChessGameException {
        super(x, y, fieldClass);
        boardSetUp();
    }

    public static AiBoard getAiBoard() throws ChessGameException {
        if (isNull(aiBoard)){
            aiBoard = new AiBoard(MAX_WIDTH, MAX_HEIGHT, Field.class);
        }
        return aiBoard;
    }

    //endregion


    //region Methods

    public void calculate(){

    }

    //endregion
}
