package classes.Model.BitBoards;

import classes.Controller.FenConverter;
import classes.Model.I18N.VARS;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Random;

import static classes.Model.AI.BitBoards.BBVars.*;
import static classes.Model.AI.BitBoards.BitBoards.*;
import static classes.Model.I18N.METHODS.countOccurrences;
import static classes.Model.I18N.METHODS.notNull;
import static classes.Model.I18N.PieceType.getPieceType;
import static classes.Model.I18N.VARS.FINALS.usualFens;
import static classes.Model.Structure.Board.getBoard;

public class BitBoardsTest extends TestCase {

    String fen = usualFens.get("whiteDownStarter");

    public void testBitBoardsToFen() {
        String[] fenParts = fen.split(" ");
        VARS.MUTABLE.emPassantChance = fenParts[3];
        VARS.MUTABLE.whiteSmallCastleEnabled = true;
        VARS.MUTABLE.whiteBigCastleEnabled = true;
        VARS.MUTABLE.blackSmallCastleEnabled = true;
        VARS.MUTABLE.blackBigCastleEnabled = true;
        setUpBitBoard(fen);
        Assert.assertEquals(fen, bitBoardsToFen());
    }

    public void testSetUpBitBoard() {
        FenConverter.FenToBoard(fen, getBoard());
        setUpBitBoard(fen);
        long currentBitBoardCopy;
        int pieceIndex;
        for (int piece : pieceIndexes) {
            currentBitBoardCopy = bitBoards[piece];
            while (currentBitBoardCopy != 0){
                pieceIndex = getFirstBitIndex(currentBitBoardCopy);
                Assert.assertTrue(notNull(getBoard().getPiece(pieceIndex / 8, 7 - (pieceIndex % 8))));
                Assert.assertEquals(
                        getPieceType(piece),
                        getBoard().getPiece(pieceIndex / 8, 7 - (pieceIndex % 8)).getType()
                );
                Assert.assertEquals(
                        piece <= wKingI,
                        getBoard().getPiece(pieceIndex / 8, 7 - (pieceIndex % 8)).isWhite()
                );
                currentBitBoardCopy = removeBit(currentBitBoardCopy, pieceIndex);
            }
        }
    }

    public void testFullBoardToString() {
        String fen = usualFens.get("whiteDownStarter");
        setUpBitBoard(fen);
        FenConverter.FenToBoard(fen, getBoard());
        String bitBoardsInString = fullBoardToString();
        Assert.assertEquals(128, bitBoardsInString.length());
        Assert.assertEquals(32, countOccurrences(bitBoardsInString, '1'));
        Assert.assertEquals(32, countOccurrences(bitBoardsInString, '0'));
    }

    public void testGetBit() {
        setUpBitBoard(fen);
        for (int i = 8; i < 16; i++) {
            Assert.assertEquals(1L << i, getBit(bitBoards[wPawnI], i));
        }
        Assert.assertEquals(1L, getBit(bitBoards[wRookI], 0));
        Assert.assertEquals(1L << 7, getBit(bitBoards[wRookI], 7));
        Assert.assertEquals(1L << 1, getBit(bitBoards[wKnightI], 1));
        Assert.assertEquals(1L << 6, getBit(bitBoards[wKnightI], 6));
        Assert.assertEquals(1L << 2, getBit(bitBoards[wBishopI], 2));
        Assert.assertEquals(1L << 5, getBit(bitBoards[wBishopI], 5));
        Assert.assertEquals(1L << 3, getBit(bitBoards[wKingI], 3));
        Assert.assertEquals(1L << 4, getBit(bitBoards[wQueenI], 4));

        for (int i = 48; i < 56; i++) {
            Assert.assertEquals(1L << i, getBit(bitBoards[bPawnI], i));
        }

        Assert.assertEquals(1L << 63, getBit(bitBoards[bRookI], 63));
        Assert.assertEquals(1L << 56, getBit(bitBoards[bRookI], 56));
        Assert.assertEquals(1L << 62, getBit(bitBoards[bKnightI], 62));
        Assert.assertEquals(1L << 57, getBit(bitBoards[bKnightI], 57));
        Assert.assertEquals(1L << 61, getBit(bitBoards[bBishopI], 61));
        Assert.assertEquals(1L << 58, getBit(bitBoards[bBishopI], 58));
        Assert.assertEquals(1L << 60, getBit(bitBoards[bQueenI], 60));
        Assert.assertEquals(1L << 59, getBit(bitBoards[bKingI], 59));
    }

    public void testSetBit() {
        Random random = new Random();
        int setThisIndex = random.nextInt(0, 63);
        long l = 0L;
        l = setBit(l, setThisIndex);
        Assert.assertEquals(l, 1L << setThisIndex);
    }

    public void testRemoveBit() {
        Random random = new Random();
        int setThisIndex = random.nextInt(0, 63);
        long l = 0L;
        l = setBit(l, setThisIndex);
        Assert.assertEquals(l, 1L << setThisIndex);
        l = removeBit(l, setThisIndex);
        Assert.assertEquals(l, 0L);
    }

}