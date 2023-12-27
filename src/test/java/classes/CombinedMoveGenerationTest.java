package classes;


import classes.AI.Ai.AI;
import classes.Game.I18N.Location;
import classes.Game.Model.Logic.FenConverter;
import classes.Game.Model.Structure.Board;
import classes.Game.Model.Structure.IPiece;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static classes.AI.BitBoards.BitBoardMoves.*;
import static classes.AI.BitBoards.BitBoards.setUpBitBoard;
import static classes.Game.I18N.VARS.FINALS.testFens;
import static classes.Game.I18N.VARS.MUTABLE.whiteToPlay;

public class CombinedMoveGenerationTest {

    Board board = Board.getBoard();


    @Test
    public void test(){
        fillBaseBitBoardPossibilities();

        for (String key : testFens.keySet()) {
            String fen = testFens.get(key);
            System.out.println("The fen we want to test: " + fen);
            FenConverter.FenToBoard(fen, board);
            setUpBitBoard(fen);

            board.rangeUpdater();

            //Ennek a kettőnek egyenlőnek kell lennie.
            ArrayList<Location> originBoardGeneratedPossibilities = new ArrayList<>();
            ArrayList<Integer> bitBoardGeneratedMoves = AI.getPairList(generateMoves(whiteToPlay)).getSecond();
            for (IPiece p : board.getPieces()) {
                if (p.isWhite() == whiteToPlay)
                    originBoardGeneratedPossibilities.addAll(p.getPossibleRange());
            }

            Assert.assertEquals(
                    originBoardGeneratedPossibilities.size(),
                    bitBoardGeneratedMoves.size()
            );
        }
    }

}