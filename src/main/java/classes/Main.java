package classes;

import classes.Game.I18N.VARS;
import classes.Game.Model.Logic.EDT;
import classes.Game.Model.Structure.BitBoard.BitBoardMoves;
import classes.Game.Model.Structure.BitBoard.BitBoards;

import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoards.*;

public class Main {

    public static void main(String[] args) {
//           exceptionIgnorer();
        new EDT();
        for (int i = 63; i >= 0; i--) {
            System.out.print((i < 10 ? " " : "") + i + " ");
            if (i % 8 == 0)
                System.out.println();
        }
    }
}