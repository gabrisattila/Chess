package classes.Model.AI.Ai;

import classes.Model.I18N.VARS;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

import static classes.Controller.FenConverter.createFenForHappenedList;

@Getter
@Setter
public class AiNode {

    //region Fields

    private int theMoveWhatsCreatedIt;

    private double finalValue;

    private Set<AiNode> children;

    //endregion


    //region Constructor

    public AiNode(){
        children = new HashSet<>(50);
    }

    public AiNode(int creatorMove){
        theMoveWhatsCreatedIt = creatorMove;
        children = new HashSet<>(50);
    }

    //endregion


    //region Methods

    public static void appendToHappenedList(String fen) {
        fen = createFenForHappenedList(fen);
        if (VARS.MUTABLE.happenedList.containsKey(fen)) {
            int i = VARS.MUTABLE.happenedList.get(fen);
            i++;
            VARS.MUTABLE.happenedList.put(fen, i);
        }else {
            VARS.MUTABLE.happenedList.put(fen, 1);
        }
    }

    //endregion
    
}
