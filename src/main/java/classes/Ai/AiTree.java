package classes.Ai;

import lombok.*;

import javax.swing.text.AsyncBoxView;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

@Getter
@Setter
public class AiTree {

    //region Fields
    private AiNode root;

    private Map<String, Set<AiNode>> visitedNodes;

    //endregion


    //region Constructor

    public AiTree(AiNode root){
        this.root = root;
        visitedNodes = new HashMap<>();
    }

    //endregion



    //region Methods

    public static AiNode calcNextAndAddToTree(AiNode root, String child){
        AiNode nextChild = fullTreeOfGame.get(child);
        if (isNull(nextChild)){
            nextChild = new AiNode(child);
        }
        root.getChildren().add(nextChild);
        fullTreeOfGame.put(child, nextChild);
        return nextChild;
    }

    //endregion
}
