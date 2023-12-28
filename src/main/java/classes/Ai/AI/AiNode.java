package classes.AI.Ai;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

import static classes.Game.I18N.VARS.MUTABLE.happenedList;
import static classes.Game.Model.Logic.FenConverter.createFenForHappenedList;

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
        if (happenedList.containsKey(fen)) {
            int i = happenedList.get(fen);
            i++;
            happenedList.put(fen, i);
        }else {
            happenedList.put(fen, 1);
        }
    }

    //endregion
    
}
