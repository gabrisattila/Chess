package classes.GUI.FrameParts;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static classes.GUI.FrameParts.SideLabel.collectProperLabelTexts;
import static classes.GUI.FrameParts.SideLabel.setBackNumsAndAbcToNormal;
import static classes.Model.Game.I18N.VARS.FINALS.nums;
import static classes.Model.Game.I18N.VARS.FINALS.abc;
import static classes.Model.Game.I18N.VARS.MUTABLE.whiteDown;

public class SideLabelTest {

    @Test
    public void testCollectProperLabelTexts() {
        whiteDown = true;
        ArrayList<String> goodLabelTextList = new ArrayList<>(){{
            add("8"); add("7"); add("6"); add("5"); add("4"); add("3"); add("2"); add("1");
            add("A"); add("B"); add("C"); add("D"); add("E"); add("F"); add("G"); add("H");
            add("1"); add("2"); add("3"); add("4"); add("5"); add("6"); add("7"); add("8");
            add("H"); add("G"); add("F"); add("E"); add("D"); add("C"); add("B"); add("A");
        }};
        ArrayList<String> calculatedLabels = collectProperLabelTexts();
        Assert.assertEquals(goodLabelTextList.size(), calculatedLabels.size());
        for (int i = 0; i < goodLabelTextList.size(); i++) {
            Assert.assertEquals(goodLabelTextList.get(i), calculatedLabels.get(i));
        }

        whiteDown = false;
        goodLabelTextList = new ArrayList<>(){{
            add("1"); add("2"); add("3"); add("4"); add("5"); add("6"); add("7"); add("8");
            add("H"); add("G"); add("F"); add("E"); add("D"); add("C"); add("B"); add("A");
            add("8"); add("7"); add("6"); add("5"); add("4"); add("3"); add("2"); add("1");
            add("A"); add("B"); add("C"); add("D"); add("E"); add("F"); add("G"); add("H");
        }};
        calculatedLabels = collectProperLabelTexts();
        Assert.assertEquals(goodLabelTextList.size(), calculatedLabels.size());
    }

    @Test
    public void testSetBackNumsAndAbcToNormal() {
        if (new Random().nextBoolean()){
            if (new Random().nextBoolean())
                Collections.reverse(nums);
            if (new Random().nextBoolean())
                Collections.reverse(abc);
        }
        setBackNumsAndAbcToNormal();
        ArrayList<Character> properNums = new ArrayList<>(){{
            add('1'); add('2'); add('3'); add('4'); add('5'); add('6'); add('7'); add('8');
        }};
        ArrayList<Character> properAbc = new ArrayList<>(){{
            add('A'); add('B'); add('C'); add('D'); add('E'); add('F'); add('G'); add('H');
        }};
        for (int i = 0; i < properNums.size(); i++) {
            Assert.assertEquals(nums.get(i), properNums.get(i));
        }
        for (int i = 0; i < properNums.size(); i++) {
            Assert.assertEquals(abc.get(i), properAbc.get(i));
        }
    }
}