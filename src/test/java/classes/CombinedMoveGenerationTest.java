package classes;


import classes.Model.AI.Ai.AI;
import classes.Model.Game.I18N.Location;
import classes.Controller.FenConverter;
import classes.Model.Game.Structure.Board;
import classes.Model.Game.Structure.IPiece;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static classes.Model.AI.BitBoards.BitBoardMoves.fillBaseBitBoardPossibilities;
import static classes.Model.AI.BitBoards.BitBoards.setUpBitBoard;
import static classes.Model.Game.I18N.VARS.FINALS.testFens;
import static classes.Model.Game.I18N.VARS.MUTABLE.whiteToPlay;

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