package classes;

import classes.Game.I18N.VARS;
import classes.Game.Model.Logic.EDT;
import classes.Game.Model.Structure.BitBoard.BitBoardMoves;
import classes.Game.Model.Structure.BitBoard.BitBoards;

import static classes.Game.I18N.METHODS.saveBoardInsteadOfException;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoards.*;

public class Main {

    public static void main(String[] args) {
//           exceptionIgnorer();
        saveBoardInsteadOfException();
        new EDT();
    }
}