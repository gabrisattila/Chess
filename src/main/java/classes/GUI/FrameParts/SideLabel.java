package classes.GUI.FrameParts;

import classes.Game.I18N.Location;
import lombok.*;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;

import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

@Getter
@Setter
public class SideLabel extends JLabel {

    //region Fields

    private int xCoordinate;

    private int yCoordinate;

    ArrayList<String> labelTextList = collectProperLabelTexts();

    //endregion


    //region Constructor

    public SideLabel(int x, int y, boolean vertical){

        //TODO nem mindegy sötéttel vagy világossal vagyunk.
        setText(labelTextList.get(labelCounter));
        labelCounter++;

        setBackground(BLACK);
        setForeground(WHITE);
        setVerticalAlignment(CENTER);
        setHorizontalAlignment(CENTER);
        setFont(new Font("Source Code Pro", Font.BOLD, 20));
        setOpaque(true);

        setBounds(
                x, y,
                (int) (vertical ? VERTICAL_SIDE_LABEL_WIDTH : HORIZONTAL_SIDE_LABEL_WIDTH),
                (int) (vertical ? VERTICAL_SIDE_LABEL_HEIGHT : HORIZONTAL_SIDE_LABEL_HEIGHT)
        );

        setBorder(BorderFactory.createLineBorder(WHITE, 2));
        setVisible(true);
    }

    //endregion


    //region Methods

    public static ArrayList<String> collectProperLabelTexts(){
        ArrayList<String> labelTextList = new ArrayList<>();
        for (int i = 0; i < MAX_HEIGHT; i++) {
            labelTextList.add(String.valueOf(számok.get(i)));
        }
        for (int i = 0; i < MAX_WIDTH; i++) {
            labelTextList.add(" " + abc.get(i) + " ");
        }
        for (int i = MAX_HEIGHT - 1; i >= 0; i--) {
            labelTextList.add(String.valueOf(számok.get(i)));
        }
        for (int i = MAX_WIDTH - 1; i >= 0; i--) {
            labelTextList.add(" " + abc.get(i) + " ");
        }
        return labelTextList;
    }

    //endregion

}
