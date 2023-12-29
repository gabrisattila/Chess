package classes.GUI.FrameParts;

import javax.swing.*;
import java.awt.*;

import static classes.Model.I18N.VARS.FINALS.BOARD_HEIGHT;
import static classes.Model.I18N.VARS.FINALS.BOARD_WIDTH;


public class CornerSquare extends JLabel {

    public CornerSquare(String where){
        setUpCorner(where);
        setBorder(BorderFactory.createEmptyBorder());
        setBackground(Color.BLACK);
        setOpaque(true);
        setVisible(true);
    }

    private void setUpCorner(String where) {
        switch (where){
            case "UL" -> setBounds(0, 0, 20, 20);
            case "UR" -> setBounds((int) (BOARD_WIDTH + 20), 0, 20, 20);
            case "DL" -> setBounds(0, (int) (BOARD_HEIGHT + 20), 20, 20);
            case "DR" -> setBounds((int) (BOARD_WIDTH + 20), (int) (BOARD_HEIGHT + 20), 20, 20);
        }
    }

}
