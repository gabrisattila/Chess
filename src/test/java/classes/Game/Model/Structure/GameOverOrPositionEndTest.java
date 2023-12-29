package classes.Game.Model.Structure;

import classes.Controller.FenConverter;
import classes.Model.Game.Structure.Board;
import org.junit.Assert;
import org.junit.Test;

import static classes.Model.Game.I18N.VARS.MUTABLE.gameEndFlag;

public class GameOverOrPositionEndTest {

    Board board = Board.getBoard();

    @Test
    public void testGameOverDecision() {

        FenConverter.FenToBoard(usualFens.get("whiteDownStarter"), board);
        double eval = WHITE_SUBMITTED;
        GameOverDecision(board, eval);
        Assert.assertTrue(gameEndFlag.get());

        gameEndFlag.set(false);
        FenConverter.FenToBoard(usualFens.get("whiteDownStarter"), board);
        eval = BLACK_SUBMITTED;
        GameOverDecision(board, eval);
        Assert.assertTrue(gameEndFlag.get());

        gameEndFlag.set(false);
        FenConverter.FenToBoard(usualFens.get("whiteDownStarter"), board);
        eval = DRAW_OFFER;
        GameOverDecision(board, eval);
        Assert.assertFalse(gameEndFlag.get());
    }

    @Test
    public void testGameEnd() {
        for (String key : gameEndFens.keySet()) {
            FenConverter.FenToBoard(gameEndFens.get(key), board);
            GameOverDecision(board, 0);
            Assert.assertTrue(gameEndFlag.get());
            gameEndFlag.set(false);
        }
    }
}