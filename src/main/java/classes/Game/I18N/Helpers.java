package classes.Game.I18N;

import classes.Ai.AI;
import classes.GUI.FrameParts.ViewField;

import java.util.ArrayList;

import static classes.Game.I18N.VARS.MUTUABLES.*;

public class Helpers {

    public static void fieldNums(ArrayList<ArrayList<ViewField>> fields){
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                fields.get(i).get(j).setText(i + " " + j);
            }
        }
    }

}
