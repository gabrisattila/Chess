package classes.Game.Model.Structure;

import classes.Game.Model.Logic.FenConverter;
import org.junit.Assert;
import org.junit.Test;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.gameEndFlag;
import static classes.Game.Model.Structure.GameOverOrPositionEnd.*;

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
        FenConverter.FenToBoard(testFens.get("whiteDownWhiteGotCheckMate"), board);
        GameOverDecision(board, 0);
        Assert.assertTrue(gameEndFlag.get());
        gameEndFlag.set(false);

        FenConverter.FenToBoard(testFens.get("blackDownBlackGotCheckMate"), board);
        GameOverDecision(board, 0);
        Assert.assertTrue(gameEndFlag.get());
        gameEndFlag.set(false);

        FenConverter.FenToBoard(testFens.get("draw1-1King"), board);
        GameOverDecision(board, 0);
        Assert.assertTrue(gameEndFlag.get());
        gameEndFlag.set(false);


        FenConverter.FenToBoard(testFens.get("draw1-1King2Knight"), board);
        GameOverDecision(board, 0);
        Assert.assertTrue(gameEndFlag.get());
        gameEndFlag.set(false);


        FenConverter.FenToBoard(testFens.get("draw1-1King1-1Knight"), board);
        GameOverDecision(board, 0);
        Assert.assertTrue(gameEndFlag.get());
        gameEndFlag.set(false);

        FenConverter.FenToBoard(testFens.get("draw1-1King1-1Bishop"), board);
        GameOverDecision(board, 0);
        Assert.assertTrue(gameEndFlag.get());
        gameEndFlag.set(false);

        FenConverter.FenToBoard(testFens.get("draw1-1King1-2Knight"), board);
        GameOverDecision(board, 0);
        Assert.assertTrue(gameEndFlag.get());
    }
}