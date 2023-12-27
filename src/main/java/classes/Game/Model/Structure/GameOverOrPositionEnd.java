package classes.Game.Model.Structure;

import classes.GUI.FrameParts.ViewBoard;
import classes.Game.I18N.PieceType;
import lombok.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static classes.AI.BitBoards.BBVars.*;
import static classes.GUI.Frame.Window.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.Board.getBoard;
import static classes.Game.Model.Structure.IBoard.convertOneBoardToAnother;

@Getter
@Setter
public class GameOverOrPositionEnd {

    public static void GameOverDecision(Object game, double submissionOrDrawComeFromPlayer) {

        double gameOver;

        if (game instanceof ViewBoard) {
            convertOneBoardToAnother(getViewBoard(), getBoard());
        }

        gameOver = gameEnd(getBoard(), submissionOrDrawComeFromPlayer);

        if (GAME_OVER_CASES.contains(gameOver)){
            gameEndDialog(gameOver);
        }
    }

    //region Dialogs

    public static void gameEndDialog(double gameResult)  {
        String message = "";
        String title = "";
        if (gameResult == WHITE_GOT_CHECKMATE || gameResult == BLACK_GOT_CHECKMATE){
            title = "Sakk Matt";
            message = "A játék véget ért, Sakk Matt!";
        } else if (gameResult == DRAW) {
            title = "Döntetlen";
            message = "A játék döntetlen lett!";
        } else if (gameResult == BLACK_SUBMITTED) {
            title = "A játéknak vége";
            message = theresOnlyOneAi ?
                    (whiteToPlay ? "Az ellenfeled feladta a partit." : "Feladtad.") :
                    ("Sötét feladta.");
        } else if (gameResult == WHITE_SUBMITTED) {
            title = "A játéknak vége";
            message = theresOnlyOneAi ?
                    (whiteToPlay ? "Feladtad." : "Az ellenfeled feladta a partit.") :
                    ("Világos feladta.");
        }
        buttonsEnabled(new ArrayList<>(){{add("Új játék"); add("Mentés"); add("Betöltés");}});
        JOptionPane.showMessageDialog(getViewBoard(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showFlashFrame(String message, int durationInSeconds){

        JFrame flashFrame = new JFrame("Mentés");
        flashFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        flashFrame.setSize(400, 200);
        flashFrame.getContentPane().setBackground(BLACK);

        JLabel label = new JLabel("<html><div style='text-align: center;'>" + message.replace("\n", "<br>") + "</div></html>");
        label.setForeground(WHITE);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Source Code Pro", Font.BOLD, 20));

        flashFrame.add(label);
        flashFrame.setLocationRelativeTo(null);

        Timer timer = new Timer(durationInSeconds * 1000, e -> flashFrame.dispose());

        timer.setRepeats(false);

        flashFrame.setVisible(true);
        timer.start();

    }

    //endregion

    //region Game End Calc

    public static double gameEnd(Board board, double submissionOrDraw) {

        board.rangeUpdater();

        if (isCheckMate(board)){
            finalGameEnd();
            return whiteToPlay ? WHITE_GOT_CHECKMATE : BLACK_GOT_CHECKMATE;
        } else if (isDraw(submissionOrDraw, board)) {
            finalGameEnd();
            return DRAW;
        } else if (isSubmission(submissionOrDraw)) {
            finalGameEnd();
            return submissionOrDraw;
        }

        if (submissionOrDraw == DRAW_OFFER) {
            if (itWorthRecommendDraw()){
                showFlashFrame("Döntetlent ajánlottál, \naz ellenfeled fogadta el.", 5);
                finalGameEnd();
                return DRAW_OFFER;
            }
        }

        return 0;
    }

    private static boolean isCheckMate(Board board) {
        if (board.hasTwoKings()){
            if (notNull(board.getCheckers())){
                if (notNull(board.getCheckers().getSecond())){
                    return board.getKing(whiteToPlay).getPossibleRange().isEmpty();
                } else {
                    return board.getPieces(whiteToPlay).stream().allMatch(p -> p.getPossibleRange().isEmpty());
                }
            }
        } else {
            return board.getPieces().stream().allMatch(IPiece::isWhite) || board.getPieces().stream().noneMatch(IPiece::isWhite);
        }
        return false;
    }

    private static boolean isDraw(double submissionOrDraw, Board board) {
        if (submissionOrDraw == DRAW){
            return true;
        } else {

            thirdSimilarPositionOfTheGame = happenedList.keySet().stream().anyMatch(a -> happenedList.get(a) == 3);

            boolean nextPlayerEmptyRange = isNull(board.getCheckers()) &&
                                            board.getPieces(whiteToPlay).stream().allMatch(p -> p.getPossibleRange().isEmpty());
            boolean allRemainingPieceIsKing = board.getPieces().size() == 2 &&
                                              board.getPieces().stream().allMatch(p -> p.getType() == K);
            boolean remained2KingAnd1KnightOr1Bishop =
                                              board.getPieces().size() == 3 &&
                                              board.getPieces().stream().allMatch(p -> p.getType() == K || p.getType() == N || p.getType() == B);
            boolean remained2KingAnd2DrawPieces = board.getPieces().size() == 4;
            if (remained2KingAnd2DrawPieces){
                ArrayList<IPiece> possibleKnight = new ArrayList<>(), possibleBishop = new ArrayList<>();
                for (IPiece p : board.getPieces()) {
                    if (p.getType() == B) {
                        possibleBishop.add(p);
                    } else if (p.getType() == N) {
                        possibleKnight.add(p);
                    }
                }
                remained2KingAnd2DrawPieces =
                        (possibleBishop.size() == 2 && possibleBishop.get(0).isWhite() != possibleBishop.get(1).isWhite()) ||
                        (possibleKnight.size() == 2) ||
                        (possibleKnight.size() == 1 && possibleBishop.size() == 1);
            }
            boolean remained2King2KnightFromSameColorAnd1Bishop =
                    board.getPieces().size() == 5;
            if (remained2King2KnightFromSameColorAnd1Bishop){
                ArrayList<IPiece> possibleKnight = new ArrayList<>(), possibleBishop = new ArrayList<>();
                for (IPiece p : board.getPieces()) {
                    if (p.getType() == B) {
                        possibleBishop.add(p);
                    } else if (p.getType() == N) {
                        possibleKnight.add(p);
                    }
                }
                remained2King2KnightFromSameColorAnd1Bishop =
                        possibleKnight.size() == 2 &&  possibleKnight.get(0).isWhite() == possibleKnight.get(1).isWhite() &&
                                possibleBishop.size() == 1 && possibleBishop.get(0).isWhite() != possibleKnight.get(0).isWhite();
            }
            return nextPlayerEmptyRange || thirdSimilarPositionOfTheGame || allRemainingPieceIsKing || remained2KingAnd1KnightOr1Bishop || remained2KingAnd2DrawPieces ||
                    remained2King2KnightFromSameColorAnd1Bishop;
        }
    }

    public static boolean isDraw(){

        thirdSimilarPositionOfTheGame = !happenedList.isEmpty() && happenedList.keySet().stream().anyMatch(a -> happenedList.get(a) == 3);

        boolean allRemainingPiecesAreKing =
                bitBoards[wPawnI] == 0 && bitBoards[wKnightI] == 0 && bitBoards[wBishopI] == 0 && bitBoards[wRookI] == 0 && bitBoards[wQueenI] == 0 && bitBoards[wKingI] != 0 &&
                bitBoards[bPawnI] == 0 && bitBoards[bKnightI] == 0 && bitBoards[bBishopI] == 0 && bitBoards[bRookI] == 0 && bitBoards[bQueenI] == 0 && bitBoards[bKingI] != 0;
        boolean noBigOneRemained =
                (bitBoards[wPawnI] == 0 && bitBoards[wRookI] == 0 && bitBoards[wQueenI] == 0 && bitBoards[wKingI] != 0 &&
                 bitBoards[bPawnI] == 0 && bitBoards[bRookI] == 0 && bitBoards[bQueenI] == 0 && bitBoards[bKingI] != 0);
        boolean only1WBishop =
                (Long.bitCount(bitBoards[wBishopI]) == 1 && bitBoards[wKnightI] == 0 && bitBoards[bBishopI] == 0 && bitBoards[bKnightI] == 0);
        boolean only1BBishop =
                (Long.bitCount(bitBoards[bBishopI]) == 1 && bitBoards[wKnightI] == 0 && bitBoards[wBishopI] == 0 && bitBoards[bKnightI] == 0);
        boolean only1WKnight =
                (Long.bitCount(bitBoards[wKnightI]) == 1 && bitBoards[wBishopI] == 0 && bitBoards[bBishopI] == 0 && bitBoards[bKnightI] == 0);
        boolean only1BKnight =
                (Long.bitCount(bitBoards[bKnightI]) == 1 && bitBoards[wBishopI] == 0 && bitBoards[bBishopI] == 0 && bitBoards[wKnightI] == 0);
        boolean only1WBishop1BKnight =
                (Long.bitCount(bitBoards[wBishopI]) == 1 && (Long.bitCount(bitBoards[bKnightI]) == 1) && bitBoards[bBishopI] == 0 && bitBoards[wKnightI] == 0);
        boolean only1BBishop1WKnight =
                (Long.bitCount(bitBoards[bBishopI]) == 1 && (Long.bitCount(bitBoards[wKnightI]) == 1) && bitBoards[bBishopI] == 0 && bitBoards[wKnightI] == 0);
        boolean only2KnightFromTheSameColor =
                (Long.bitCount(bitBoards[wKnightI]) == 2 || Long.bitCount(bitBoards[bKnightI]) == 2) && bitBoards[bBishopI] == 0 && bitBoards[wBishopI] == 0;
        boolean twoBishopAgainstOne =
                (Long.bitCount(bitBoards[wBishopI]) == 2 && Long.bitCount(bitBoards[bBishopI]) == 1 && bitBoards[bKnightI] == 0 && bitBoards[wKnightI] == 0)  ||
                (Long.bitCount(bitBoards[wBishopI]) == 1 && Long.bitCount(bitBoards[bBishopI]) == 2 && bitBoards[bKnightI] == 0 && bitBoards[wKnightI] == 0);

        return thirdSimilarPositionOfTheGame || allRemainingPiecesAreKing ||
                (noBigOneRemained &&
                        (only1WBishop || only1BBishop || only1WKnight || only1BKnight ||
                         only1WBishop1BKnight || only1BBishop1WKnight || only2KnightFromTheSameColor || twoBishopAgainstOne));


    }

    private static boolean isSubmission(double submissionOrDraw) {
        return submissionOrDraw == WHITE_SUBMITTED || submissionOrDraw == BLACK_SUBMITTED;
    }

    public static boolean itWorthRecommendDraw(){
        if (getBoard().getPieces().size() == 3 && getBoard().hasTwoKings() &&
                getBoard().getPieces().stream().allMatch(p -> p.getType() == K || p.getType() == P)){
            IPiece onlyPawn = null;
            IPiece enemyKing;
            for (IPiece p : getBoard().getPieces()) {
                if (p.getType() == P){
                    onlyPawn = p;
                }
            }
            assert onlyPawn != null;
            enemyKing = getBoard().getKing(!onlyPawn.isWhite());
            if (onlyPawn.getJ() != 0 || onlyPawn.getJ() != MAX_WIDTH - 1){
                return false;
            }
            int pawnDistance = Math.abs(onlyPawn.getJ() - onlyPawn.getEnemyStartRow());
            int kingDistance = Math.max(Math.abs(enemyKing.getJ() - onlyPawn.getJ()), Math.abs(enemyKing.getI() - onlyPawn.getEnemyStartRow()));
            return !(kingDistance < pawnDistance);
        }
        return getBoard().hasTwoKings() && getBoard().getPieces().size() == 4 &&
                getBoard().getPieces().stream().allMatch(p -> p.getType() == K || p.getType() == N);
    }

    public static void finalGameEnd(){
        gameEndFlag.set(true);
        getViewBoard().clearPiecesRanges();
        getBoard().cleanBoard();
    }

    //endregion

}
