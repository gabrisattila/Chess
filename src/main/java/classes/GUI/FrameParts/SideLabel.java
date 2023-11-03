package classes.GUI.FrameParts;

import lombok.*;

import javax.swing.*;

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

    public SideLabel(int x, int y, boolean vertical){
        setBounds(
                x, y,
                (int) (vertical ? VERTICAL_SIDE_LABEL_WIDTH : HORIZONTAL_SIDE_LABEL_WIDTH),
                (int) (vertical ? VERTICAL_SIDE_LABEL_HEIGHT : HORIZONTAL_SIDE_LABEL_HEIGHT)
        );
    }

    //endregion


    //region Methods

    public static void setUpLabels(){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {

                if (i % 2 == 0) /* Horizontal - Vízszintes */ {

                }else /* Vertical - Függőleges */ {

                }

            }
        }
    }

    //endregion

}
