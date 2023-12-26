package classes.GUI.FrameParts;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setVisible(true);
    }

    //endregion


    //region Methods

    public static ArrayList<String> collectProperLabelTexts(boolean whiteDown){
        ArrayList<String> labelTextList = new ArrayList<>();
        List<Character> numsSubList = nums.subList(0, MAX_HEIGHT);
        List<Character> abcSubList = abc.subList(0, MAX_WIDTH);
        if (!whiteDown){
            Collections.reverse(abcSubList);
        }else {
            Collections.reverse(numsSubList);
        }
        for (int i = 0; i < MAX_HEIGHT; i++) {
            labelTextList.add(String.valueOf(numsSubList.get(i)));
        }
        for (int i = 0; i < MAX_WIDTH; i++) {
            labelTextList.add(" " + abcSubList.get(i) + " ");
        }
        for (int i = MAX_HEIGHT - 1; i >= 0; i--) {
            labelTextList.add(String.valueOf(numsSubList.get(i)));
        }
        for (int i = MAX_WIDTH - 1; i >= 0; i--) {
            labelTextList.add(" " + abcSubList.get(i) + " ");
        }
        return labelTextList;
    }

    //endregion

}
