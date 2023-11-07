package classes.GUI.FrameParts;

import javax.swing.*;

import java.awt.*;

import static classes.Game.I18N.VARS.FINALS.*;


public class CornerSquare extends JLabel {

    public CornerSquare(String where){
        setUpCorner(where);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    private void setUpCorner(String where) {
        setBackground(BLACK);
        setOpaque(true);
        setVisible(true);
        switch (where){
            case "UL" -> setBounds(0, 0, 20, 20);
            case "UR" -> setBounds((int) (BOARD_WIDTH + 20), 0, 20, 20);
            case "DL" -> setBounds(0, (int) (BOARD_HEIGHT + 20), 20, 20);
            case "DR" -> setBounds((int) (BOARD_WIDTH + 20), (int) (BOARD_HEIGHT + 20), 20, 20);
        }
    }

}
