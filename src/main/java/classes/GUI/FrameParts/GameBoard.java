package classes.GUI.FrameParts;

import classes.Game.I18N.Pair;
import lombok.*;

import javax.swing.*;

import java.awt.*;

import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;

@Getter
@Setter
public class GameBoard extends JLayeredPane {

    //region Fields

    private ViewBoard parentBoard;

    private JComboBox<JButton> whatToPlay;

    //endregion


    //region Constructor

    public GameBoard(ViewBoard viewBoard){
        parentBoard = viewBoard;
        setBounds(300, 100, 8 * FIELD_WIDTH, 8 * FIELD_HEIGHT);

        rotateBoard();

        for (var v : parentBoard.getFields()) {
            for (var vv : v) {
                add(vv);
            }
        }
    }

    //endregion


    //region Methods

    private void rotateBoard(){

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Point rotatedPosition = rotateAntiClockwise(new Point(i, j), MAX_WIDTH);

                parentBoard.getFieldByIJFromBoard(i, j).setBounds(
                        rotatedPosition.x * FIELD_HEIGHT,
                        rotatedPosition.y * FIELD_WIDTH,
                        FIELD_WIDTH,
                        FIELD_HEIGHT
                );
            }
        }
    }

    private Point rotateAntiClockwise(Point point, int width) {
        int newX = point.y;
        int newY = width - point.x - 1;
        return new Point(newX, newY);
    }


    private void setTmp(Pair<Integer, Integer> tmp, int i, int j){
        tmp.setFirst(parentBoard.getFieldByIJFromBoard(i, j).getX());
        tmp.setSecond(parentBoard.getFieldByIJFromBoard(i, j).getY());
    }

    private void setNewBounds(int elementToChangeI, int elementToChangeJ, int newX, int newY){
        parentBoard.getFieldByIJFromBoard(elementToChangeI, elementToChangeJ)
                .setBounds(newX, newY, FIELD_WIDTH, FIELD_HEIGHT);
    }

    //endregion
}
