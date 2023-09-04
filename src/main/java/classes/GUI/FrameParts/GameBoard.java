package classes.GUI.FrameParts;

import lombok.*;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;

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
        gameBoardSetUp();
    }

    //endregion


    //region Methods

    private void gameBoardSetUp(){
        setBoardCoordinates();

        rotateBoard();
        addFieldsAtTheirFinalForm();
    }

    private void setBoardCoordinates(){
        setBounds((int) BOARD_START_X, (int) BOARD_START_Y, 8 * FIELD_WIDTH, 8 * FIELD_HEIGHT);
    }

    private void rotateBoard(){

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Point rotatedPosition = rotateAntiClockwise(new Point(i, j), MAX_WIDTH);

                parentBoard.getField(i, j).setBounds(
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

    private void addFieldsAtTheirFinalForm(){
        for (ArrayList<ViewField> row: parentBoard.getFields()){
            for (ViewField f : row) {
                add(f);
            }
        }
    }

    //endregion
}
