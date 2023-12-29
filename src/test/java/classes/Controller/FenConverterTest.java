package classes.Controller;

import classes.Model.I18N.ChessGameException;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

import static classes.Controller.FenConverter.*;
import static classes.Model.I18N.ChessGameException.getBadRow;
import static classes.Model.I18N.ChessGameException.getFenRowCalculableLength;
import static classes.Model.I18N.PieceType.*;
import static classes.Model.I18N.VARS.FINALS.usualFens;
import static classes.Model.I18N.VARS.MUTABLE.*;
import static classes.Model.Structure.Board.getBoard;

public class FenConverterTest {

    @Test
    public void testFenToBoard() {
        windowCreationCounter = 0;
        String wrongFen = "8/8/7/8/8/8/8/8 w KQkq - 1 0";
        Assert.assertThrows(ChessGameException.class, () -> FenToBoard(wrongFen, getBoard()));
        Assert.assertThrows(
                "Ez a fen:\n" +
                wrongFen +
                "nem passzol a megszabott tábla méretekhez, mert . \n" +
                "Ez a sor: " +
                        getBadRow(wrongFen.split(" ")[0].split("/")) +
                        " nem megfelelő hosszúságú, hiszen a kívánt hossz 8 a sor pedig " +
                        getFenRowCalculableLength(getBadRow(wrongFen.split(" ")[0].split("/"))) +
                        " hosszú.",
                ChessGameException.class,
                () -> FenToBoard(wrongFen, getBoard())
                );
        String goodFen = usualFens.get("whiteDownStarter");
        FenToBoard(goodFen, getBoard());
        String[] fenParts = goodFen.split(" ");
        
        int pieceNum = getBoard().getPieces().size();

        int whitePawnNum = getBoard().getPieces().stream().filter(p -> p.isWhite() && p.getType() == P).collect(Collectors.toSet()).size();
        int whiteRookNum = getBoard().getPieces().stream().filter(p -> p.isWhite() && p.getType() == R).collect(Collectors.toSet()).size();
        int whiteKnightNum = getBoard().getPieces().stream().filter(p -> p.isWhite() && p.getType() == N).collect(Collectors.toSet()).size();
        int whiteBishopNum = getBoard().getPieces().stream().filter(p -> p.isWhite() && p.getType() == B).collect(Collectors.toSet()).size();
        int whiteQueenNum = getBoard().getPieces().stream().filter(p -> p.isWhite() && p.getType() == Q).collect(Collectors.toSet()).size();
        int whiteKingNum = getBoard().getPieces().stream().filter(p -> p.isWhite() && p.getType() == K).collect(Collectors.toSet()).size();

        int blackPawnNum = getBoard().getPieces().stream().filter(p -> !p.isWhite() && p.getType() == P).collect(Collectors.toSet()).size();
        int blackRookNum = getBoard().getPieces().stream().filter(p -> !p.isWhite() && p.getType() == R).collect(Collectors.toSet()).size();
        int blackKnightNum = getBoard().getPieces().stream().filter(p -> !p.isWhite() && p.getType() == N).collect(Collectors.toSet()).size();
        int blackBishopNum = getBoard().getPieces().stream().filter(p -> !p.isWhite() && p.getType() == B).collect(Collectors.toSet()).size();
        int blackQueenNum = getBoard().getPieces().stream().filter(p -> !p.isWhite() && p.getType() == Q).collect(Collectors.toSet()).size();
        int blackKingNum = getBoard().getPieces().stream().filter(p -> !p.isWhite() && p.getType() == K).collect(Collectors.toSet()).size();
        
        Assert.assertEquals(8, whitePawnNum);
        Assert.assertEquals(2, whiteRookNum);
        Assert.assertEquals(2, whiteKnightNum);
        Assert.assertEquals(2, whiteBishopNum);
        Assert.assertEquals(1, whiteQueenNum);
        Assert.assertEquals(1, whiteKingNum);

        Assert.assertEquals(whitePawnNum, blackPawnNum);
        Assert.assertEquals(whiteRookNum, blackRookNum);
        Assert.assertEquals(whiteKnightNum, blackKnightNum);
        Assert.assertEquals(whiteBishopNum, blackBishopNum);
        Assert.assertEquals(whiteQueenNum, blackQueenNum);
        Assert.assertEquals(whiteKingNum, blackKingNum);
        
        Assert.assertEquals(32, pieceNum);
        Assert.assertEquals(
                pieceNum, 
                whitePawnNum + whiteRookNum + whiteKnightNum + whiteBishopNum + whiteQueenNum + whiteKingNum +
                        blackPawnNum + blackRookNum + blackKnightNum + blackBishopNum + blackQueenNum + blackKingNum
        );
        Assert.assertEquals("w", fenParts[1]);
        Assert.assertEquals("KQkq", fenParts[2]);
        Assert.assertEquals("-", fenParts[3]);
        Assert.assertEquals("1", fenParts[4]);
        Assert.assertEquals("0", fenParts[5]);
    }

    @Test
    public void testBoardToFen() {
        FenToBoard(usualFens.get("whiteDownStarter"), getBoard());
        emPassantChance = "45";
        whiteSmallCastleEnabled = false;
        Assert.assertNotEquals(usualFens.get("whiteDownStarter"), BoardToFen(getBoard()));
        Assert.assertEquals(
                "RNBQKBNR/PPPPPPPP/8/8/8/8/pppppppp/rnbqkbnr w -Qkq 45 1 0",
                BoardToFen(getBoard()));
    }

    @Test
    public void testCreateFenForHappenedList() {
        String originalFen = usualFens.get("whiteDownStarter");
        String[] originalParts = originalFen.split(" ");
        String forHappenedList = createFenForHappenedList(originalFen);
        String[] happenedParts = forHappenedList.split(" ");
        Assert.assertNotEquals(originalParts.length, happenedParts.length);
        Assert.assertEquals(5, happenedParts.length);
        Assert.assertEquals(originalParts[0], happenedParts[0]);
        Assert.assertEquals(originalParts[1], happenedParts[1]);
        Assert.assertEquals(originalParts[2], happenedParts[2]);
        Assert.assertEquals(originalParts[3], happenedParts[3]);
        Assert.assertEquals(originalParts[5], happenedParts[4]);
    }
}