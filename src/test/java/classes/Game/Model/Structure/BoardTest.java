package classes.Game.Model.Structure;


import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static classes.Game.I18N.METHODS.notNull;

public class BoardTest {

    Board board = Board.getBoard();

    @Test
    public void getBoardTest(){
        Assert.assertTrue(notNull(board));
    }

    @Test
    public void testCleanBoard() {
        for (ArrayList<IField> fields : board.getFields()) {
            for (IField f : fields) {
                Assert.assertFalse(f.isGotPiece());
            }
        }
    }

//    @Test
//    public void testRangeUpdater() {
//    }
//
//    @Test
//    public void testPseudos() {
//    }
//
//    @Test
//    public void testConstrainPseudos() {
//    }
//
//    @Test
//    public void testHasTwoKings() {
//    }
//
//    @Test
//    public void testGetTheMiddleLocation() {
//    }
}