package classes.GUI.FrameParts;

import classes.Game.I18N.Pair;
import lombok.*;

import javax.swing.*;

import static classes.Game.I18N.VARS.FINALS.*;

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

        for (var v : parentBoard.getFields()) {
            for (var vv : v) {
                add(vv);
            }
        }
    }

    //endregion


    //region Methods

    private void rotateBoard(){
        Pair<Integer, Integer> xy = new Pair<>();
        for (int i = 0; i < parentBoard.getFields().size(); i++) {
            for (int j = 0; j < parentBoard.getFields().get(i).size(); j++) {
                //tmp = matrix[i][j]
                xy.setFirst(parentBoard.getFieldByIJFromBoard(i, j).getX());
                xy.setSecond(parentBoard.getFieldByIJFromBoard(i, j).getY());
                //matrix[i][j] = matrix[j][i]
                setNewBounds(i, j,
                        parentBoard.getFieldByIJFromBoard(j, i).getX(),
                        parentBoard.getFieldByIJFromBoard(j, i).getY());
                //matrix[j][i] = tmp
                setNewBounds(j, i, xy.getFirst(), xy.getSecond());
            }
        }
    }

    private void setNewBounds(int X, int Y, int newX, int newY){
        parentBoard.getFieldByIJFromBoard(X, Y).setBounds(newX, newY, FIELD_WIDTH, FIELD_HEIGHT);
    }

    //endregion
}
