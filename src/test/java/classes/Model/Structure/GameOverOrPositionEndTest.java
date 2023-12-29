package classes.Model.Structure;

import classes.Controller.FenConverter;
import classes.Model.Structure.Board;
import classes.Model.I18N.VARS;
import classes.Model.Structure.GameOverOrPositionEnd;
import org.junit.Assert;
import org.junit.Test;

public class GameOverOrPositionEndTest {

    Board board = Board.getBoard();

    @Test
    public void testGameOverDecision() {

        FenConverter.FenToBoard(VARS.FINALS.usualFens.get("whiteDownStarter"), board);
        double eval = VARS.FINALS.WHITE_SUBMITTED;
        GameOverOrPositionEnd.GameOverDecision(board, eval);
        Assert.assertTrue(VARS.MUTABLE.gameEndFlag.get());

        VARS.MUTABLE.gameEndFlag.set(false);
        FenConverter.FenToBoard(VARS.FINALS.usualFens.get("whiteDownStarter"), board);
        eval = VARS.FINALS.BLACK_SUBMITTED;
        GameOverOrPositionEnd.GameOverDecision(board, eval);
        Assert.assertTrue(VARS.MUTABLE.gameEndFlag.get());

        VARS.MUTABLE.gameEndFlag.set(false);
        FenConverter.FenToBoard(VARS.FINALS.usualFens.get("whiteDownStarter"), board);
        eval = VARS.FINALS.DRAW_OFFER;
        GameOverOrPositionEnd.GameOverDecision(board, eval);
        Assert.assertFalse(VARS.MUTABLE.gameEndFlag.get());
    }

    @Test
    public void testGameEnd() {
        for (String key : VARS.FINALS.gameEndFens.keySet()) {
            FenConverter.FenToBoard(VARS.FINALS.gameEndFens.get(key), board);
            GameOverOrPositionEnd.GameOverDecision(board, 0);
            Assert.assertTrue(VARS.MUTABLE.gameEndFlag.get());
            VARS.MUTABLE.gameEndFlag.set(false);
        }
    }
}