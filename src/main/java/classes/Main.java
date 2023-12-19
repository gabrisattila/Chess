package classes;

import classes.Ai.BitBoards.BitBoards;
import classes.Game.I18N.VARS;
import classes.Game.Model.Logic.EDT;

import java.util.Random;

import static classes.Ai.BitBoards.BBVars.*;
import static classes.Ai.BitBoards.BitBoardMoves.fillBaseBitBoardPossibilities;
import static classes.Ai.BitBoards.BitBoards.*;

public class Main {

    public static void main(String[] args) {
//        new EDT();
        setUpBitBoard(VARS.FINALS.usualFens.get("blackDownStarter"));
        fillBaseBitBoardPossibilities();
        for (int i = 0; i < 12; i++) {
            System.out.println(englishPieceLetters.get(i));
            int from = new Random().nextInt(0, 64);
            System.out.println(BitBoards.toString(1L << from));
            System.out.println(BitBoards.toString(basePossibilities[i][from]));
        }
    }

}