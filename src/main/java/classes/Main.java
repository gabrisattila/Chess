package classes;

import classes.Ai.FenConverter;
import classes.Game.I18N.VARS;
import classes.Game.Model.Logic.EDT;
import classes.Game.Model.Structure.BitBoard.BBVars;
import classes.Game.Model.Structure.BitBoard.BitBoards;
import classes.Game.Model.Structure.Board;

import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoards.*;
import static classes.Game.Model.Structure.Move.*;

public class Main {

    public static void main(String[] args) {
//           exceptionIgnorer();
//        new EDT();


        setUpBitBoard(usualFens.get("whiteDownStarter"));
        whiteToPlay = true;
        long s = pawnMoves();
        whiteToPlay = false;
        long x = pawnMoves();
        System.out.println(BitBoards.toString(s));
        System.out.println(BitBoards.toString(x));
    }
}