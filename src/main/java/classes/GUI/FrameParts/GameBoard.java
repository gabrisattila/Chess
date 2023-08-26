package classes.GUI.FrameParts;

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

    public void setPieces(){

    }

    //endregion
}
