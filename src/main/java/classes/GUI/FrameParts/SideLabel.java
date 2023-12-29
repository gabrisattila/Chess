package classes.GUI.FrameParts;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static classes.Model.Game.I18N.VARS.FINALS.*;
import static classes.Model.Game.I18N.VARS.MUTABLE.*;

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
        xCoordinate = x;
        yCoordinate = y;

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

    public static ArrayList<String> collectProperLabelTexts(){
        ArrayList<String> labelTextList = new ArrayList<>();
        setBackNumsAndAbcToNormal();

        List<Character> numsSubList = nums.subList(0, MAX_HEIGHT);
        List<Character> abcSubList = abc.subList(0, MAX_WIDTH);

        if (whiteDown){
            addReversedCharacterListToLabelList(labelTextList, numsSubList);
            addCharacterListToLabelList(labelTextList, abcSubList);
        }else {
            addCharacterListToLabelList(labelTextList, numsSubList);
            addReversedCharacterListToLabelList(labelTextList, abcSubList);
        }
        addReversedCharacterListToLabelList(labelTextList, numsSubList);
        addReversedCharacterListToLabelList(labelTextList, abcSubList);

        return labelTextList;
    }

    private static void addCharacterListToLabelList(List<String> labelTextList, List<Character> characters){
        for (char c : characters) {
            labelTextList.add(String.valueOf(c));
        }
    }

    private static void addReversedCharacterListToLabelList(List<String> labelTextList, List<Character> characters){
        Collections.reverse(characters);
        addCharacterListToLabelList(labelTextList, characters);
    }

    public static void setBackNumsAndAbcToNormal(){
        if (nums.get(0) != '1')
            Collections.reverse(nums);
        if (abc.get(0) != 'A')
            Collections.reverse(abc);
    }

    //endregion

}
