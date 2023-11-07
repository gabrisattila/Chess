package classes.GUI.FrameParts;

import classes.Game.I18N.Location;
import lombok.*;

import javax.swing.*;

import java.awt.*;

import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

@Getter
@Setter
public class SideLabel extends JLabel {

    //region Fields

    private int xCoordinate;

    private int yCoordinate;

    //endregion


    //region Constructor

    public SideLabel(Location location, boolean vertical){
        new SideLabel(location.getI(), location.getJ(), vertical);
    }

    public SideLabel(int x, int y, boolean vertical){

        String text = " " + (vertical ? számok.get(numLabelCounter) : abc.get(abcLabelCounter)) + " ";

        setText(text);

        if (vertical)
            numLabelCounter++;
        else
            abcLabelCounter++;

        setBackground(BLACK);
        setForeground(WHITE);
        setVerticalAlignment(CENTER);
        setHorizontalAlignment(CENTER);
        setFont(new Font("Source Code Pro", Font.BOLD, 20));
        setVisible(true);
        setOpaque(false);

        setBounds(
                x, y,
                (int) (vertical ? VERTICAL_SIDE_LABEL_WIDTH : HORIZONTAL_SIDE_LABEL_WIDTH),
                (int) (vertical ? VERTICAL_SIDE_LABEL_HEIGHT : HORIZONTAL_SIDE_LABEL_HEIGHT)
        );

        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setVisible(true);
    }

    //endregion


    //region Methods

    //endregion

}
