package classes.Game.I18N;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Random;

import static classes.AI.BitBoards.BBVars.*;
import static classes.Game.I18N.PieceType.*;

public class PieceTypeTest {

    PieceType pawn = P;

    PieceType knight = N;

    PieceType bishop = B;

    PieceType rook = R;

    PieceType queen = Q;

    PieceType king = K;

    Random random = new Random();

    @Test
    public void testTestEquals() {
        Assert.assertNotEquals(pawn, knight);
        Assert.assertNotEquals(pawn, bishop);
        Assert.assertNotEquals(pawn, rook);
        Assert.assertNotEquals(pawn, queen);
        Assert.assertNotEquals(pawn, king);

        Assert.assertNotEquals(knight, bishop);
        Assert.assertNotEquals(knight, rook);
        Assert.assertNotEquals(knight, queen);
        Assert.assertNotEquals(knight, king);

        Assert.assertNotEquals(bishop, rook);
        Assert.assertNotEquals(bishop, queen);
        Assert.assertNotEquals(bishop, king);

        Assert.assertNotEquals(rook, queen);
        Assert.assertNotEquals(rook, king);

        Assert.assertNotEquals(queen, king);

        Assert.assertEquals(pawn, P);
        Assert.assertEquals(knight, N);
        Assert.assertEquals(bishop, B);
        Assert.assertEquals(rook, R);
        Assert.assertEquals(queen, Q);
        Assert.assertEquals(king, K);
    }

    @Test
    public void testTestToString() {
        boolean forWhite = 1 == random.nextInt(0, 2);
        String pS = pawn.toString(forWhite), nS = knight.toString(forWhite), bS = bishop.toString(forWhite),
                rS = rook.toString(forWhite), qS = queen.toString(forWhite), kS = king.toString(forWhite);
        Assert.assertTrue(forWhite ? pS.equals("P") : pS.equals("p"));
        Assert.assertTrue(forWhite ? nS.equals("N") : nS.equals("n"));
        Assert.assertTrue(forWhite ? bS.equals("B") : bS.equals("b"));
        Assert.assertTrue(forWhite ? rS.equals("R") : rS.equals("r"));
        Assert.assertTrue(forWhite ? qS.equals("Q") : qS.equals("q"));
        Assert.assertTrue(forWhite ? kS.equals("K") : kS.equals("k"));
    }

    @Test
    public void testGetPieceTypeFromIndex() {
        for (int piece : pieceIndexes) {
            Assert.assertEquals(getPieceType(piece).toString(piece <= wKingI), englishPieceLetters.get(piece));
        }
    }

    @Test
    public void testGetPieceTypeFromString() {
        int i = 0;
        for (PieceType t : PieceType.values()) {
            Assert.assertEquals(getPieceType(englishPieceLetters.get(i)), t);
            i++;
        }
        for (PieceType t : PieceType.values()) {
            Assert.assertEquals(getPieceType(englishPieceLetters.get(i)), t);
            i++;
        }
    }

    @Test
    public void testGetPieceTypeFromChar() {
        int i = 0;
        Collections.reverse(englishPieceLetters);
        for (PieceType t : PieceType.values()) {
            Assert.assertNotEquals(getPieceType(englishPieceLetters.get(i).charAt(0)), t);
            i++;
        }
        for (PieceType t : PieceType.values()) {
            Assert.assertNotEquals(getPieceType(englishPieceLetters.get(i).charAt(0)), t);
            i++;
        }

        i = 0;
        Collections.reverse(englishPieceLetters);
        for (PieceType t : PieceType.values()) {
            Assert.assertEquals(getPieceType(englishPieceLetters.get(i).charAt(0)), t);
            i++;
        }
        for (PieceType t : PieceType.values()) {
            Assert.assertEquals(getPieceType(englishPieceLetters.get(i).charAt(0)), t);
            i++;
        }
    }
}