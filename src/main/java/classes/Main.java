package classes;

import classes.Ai.FenConverter;
import classes.Game.I18N.VARS;
import classes.Game.Model.Logic.EDT;
import classes.Game.Model.Structure.BitBoard.BBVars;
import classes.Game.Model.Structure.BitBoard.BitBoards;

public class Main {

    public static void main(String[] args) {
//           exceptionIgnorer();
//        new EDT();

//        System.out.println(FenConverter.FenToBitBoardFen(VARS.FINALS.usualFens.get("whiteDownStarter").split(" ")[0]));
//        new BitBoards();
        System.out.println(BitBoards.toString(BBVars.QUEEN_SIDE));
    }
}