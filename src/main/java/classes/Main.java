package classes;

import classes.Ai.BitBoards.BitBoardMoves;
import classes.Ai.BitBoards.BitBoards;
import classes.Game.I18N.VARS;
import classes.Game.Model.Logic.EDT;

import java.util.Random;

import static classes.Ai.BitBoards.BBVars.*;
import static classes.Ai.BitBoards.BitBoardMoves.*;
import static classes.Ai.BitBoards.BitBoards.*;

public class Main {

    public static void main(String[] args) {
//        new EDT();
        setUpBitBoard(VARS.FINALS.usualFens.get("whiteDownStarter"));
        fillBaseBitBoardPossibilities();
    }

}