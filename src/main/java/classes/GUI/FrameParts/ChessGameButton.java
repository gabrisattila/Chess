package classes.GUI.FrameParts;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static classes.Game.I18N.VARS.FINALS.*;

public class ChessGameButton extends JButton {

    //region Fields



    //endregion


    //region Constructor

    public ChessGameButton(){
        setBackground(WHITE);
        setForeground(BLACK);
        setVerticalAlignment(CENTER);
        setHorizontalAlignment(CENTER);
        setFont(new Font("Source Code Pro", Font.BOLD, 20));
        setOpaque(true);
        setFocusable(false);
        setBorderPainted(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                swapColors();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                swapColors();
            }
        });

    }

    //endregion


    //region Methods

    private void swapColors() {
        Color temp = getForeground();
        setForeground(getBackground());
        setBackground(temp);
    }

    //endregion

}
