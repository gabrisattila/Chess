package classes.GUI.FrameParts;

import classes.Game.Model.Logic.FenConverter;
import classes.Game.Model.Structure.IPiece;
import junit.framework.TestCase;
import org.junit.Assert;

import static classes.Game.I18N.VARS.FINALS.testFens;
import static classes.Game.I18N.VARS.MUTABLE.gameEndFlag;
import static classes.Game.Model.Structure.Board.getBoard;

public class ViewBoardTest extends TestCase {

    ViewBoard viewBoard = ViewBoard.getViewBoard();

    public void testGetViewBoard() {
        Assert.assertNotNull(viewBoard);
    }

    public void testRangeUpdater() {
        getBoard();
        int piecesPossibilityNum;
        int vPiecesPossibilityNum;
        for (String key : testFens.keySet()) {
            piecesPossibilityNum = 0;
            vPiecesPossibilityNum = 0;
            FenConverter.FenToBoard(testFens.get(key), viewBoard);
            FenConverter.FenToBoard(testFens.get(key), getBoard());

            viewBoard.rangeUpdater();

            for (IPiece p : getBoard().getPieces()) {
                piecesPossibilityNum += p.getPossibleRange().size();
            }
            for (IPiece p : viewBoard.getPieces()) {
                vPiecesPossibilityNum += p.getPossibleRange().size();
            }
            if (!gameEndFlag.get())
                Assert.assertEquals(piecesPossibilityNum, vPiecesPossibilityNum);
            else
                Assert.assertEquals(0, vPiecesPossibilityNum);
        }
    }

    public void testClearPiecesRanges() {
        int vPiecesPossibilityNum;
        for (String key : testFens.keySet()) {
            vPiecesPossibilityNum = 0;
            FenConverter.FenToBoard(testFens.get(key), viewBoard);
            FenConverter.FenToBoard(testFens.get(key), getBoard());

            viewBoard.rangeUpdater();

            for (IPiece p : viewBoard.getPieces()) {
                vPiecesPossibilityNum += p.getPossibleRange().size();
            }
            if (!gameEndFlag.get()) {
                Assert.assertNotEquals(0, vPiecesPossibilityNum);
                return;
            }
            viewBoard.clearPiecesRanges();
            for (IPiece p : viewBoard.getPieces()) {
                vPiecesPossibilityNum += p.getPossibleRange().size();
            }
            Assert.assertEquals(0, vPiecesPossibilityNum);
        }
    }
}