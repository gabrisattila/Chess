package classes.Model.Game.Structure;

import classes.Model.AI.BitBoards.BBVars;
import classes.GUI.FrameParts.ViewBoard;
import classes.Model.Game.I18N.METHODS;
import classes.Model.Game.I18N.PieceType;
import classes.Model.Game.I18N.VARS;
import lombok.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static classes.GUI.Frame.Window.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Model.Game.I18N.VARS.FINALS.MAX_WIDTH;

@Getter
@Setter
public class GameOverOrPositionEnd {

    public static void GameOverDecision(Object game, double submissionOrDrawComeFromPlayer) {

        double gameOver;

        if (game instanceof ViewBoard) {
            IBoard.convertOneBoardToAnother(getViewBoard(), Board.getBoard());
        }

        gameOver = gameEnd(Board.getBoard(), submissionOrDrawComeFromPlayer);

        if (VARS.FINALS.GAME_OVER_CASES.contains(gameOver)){
            gameEndDialog(gameOver);
        }
    }

    //region Dialogs

    public static void gameEndDialog(double gameResult)  {
        String message = "";
        String title = "";
        if (gameResult == VARS.FINALS.WHITE_GOT_CHECKMATE || gameResult == VARS.FINALS.BLACK_GOT_CHECKMATE){
            title = "Sakk Matt";
            message = "A játék véget ért, Sakk Matt!";
        } else if (gameResult == VARS.FINALS.DRAW) {
            title = "Döntetlen";
            message = "A játék döntetlen lett!";
        } else if (gameResult == VARS.FINALS.BLACK_SUBMITTED) {
            title = "A játéknak vége";
            message = VARS.MUTABLE.theresOnlyOneAi ?
                    (VARS.MUTABLE.whiteToPlay ? "Az ellenfeled feladta a partit." : "Feladtad.") :
                    ("Sötét feladta.");
        } else if (gameResult == VARS.FINALS.WHITE_SUBMITTED) {
            title = "A játéknak vége";
            message = VARS.MUTABLE.theresOnlyOneAi ?
                    (VARS.MUTABLE.whiteToPlay ? "Feladtad." : "Az ellenfeled feladta a partit.") :
                    ("Világos feladta.");
        }
        buttonsEnabled(new ArrayList<>(){{add("Új játék"); add("Mentés"); add("Betöltés");}});
        JOptionPane.showMessageDialog(getViewBoard(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showFlashFrame(String message, int durationInSeconds){

        JFrame flashFrame = new JFrame("Mentés");
        flashFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        flashFrame.setSize(400, 200);
        flashFrame.getContentPane().setBackground(VARS.FINALS.BLACK);

        JLabel label = new JLabel("<html><div style='text-align: center;'>" + message.replace("\n", "<br>") + "</div></html>");
        label.setForeground(VARS.FINALS.WHITE);
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
            return VARS.MUTABLE.whiteToPlay ? VARS.FINALS.WHITE_GOT_CHECKMATE : VARS.FINALS.BLACK_GOT_CHECKMATE;
        } else if (isDraw(submissionOrDraw, board)) {
            finalGameEnd();
            return VARS.FINALS.DRAW;
        } else if (isSubmission(submissionOrDraw)) {
            finalGameEnd();
            return submissionOrDraw;
        }

        if (submissionOrDraw == VARS.FINALS.DRAW_OFFER) {
            if (itWorthRecommendDraw()){
                showFlashFrame("Döntetlent ajánlottál, \naz ellenfeled fogadta el.", 5);
                finalGameEnd();
                return VARS.FINALS.DRAW_OFFER;
            }
        }

        return 0;
    }

    private static boolean isCheckMate(Board board) {
        if (board.hasTwoKings()){
            if (METHODS.notNull(board.getCheckers())){
                if (METHODS.notNull(board.getCheckers().getSecond())){
                    return board.getKing(VARS.MUTABLE.whiteToPlay).getPossibleRange().isEmpty();
                } else {
                    return board.getPieces(VARS.MUTABLE.whiteToPlay).stream().allMatch(p -> p.getPossibleRange().isEmpty());
                }
            }
        } else {
            return board.getPieces().stream().allMatch(IPiece::isWhite) || board.getPieces().stream().noneMatch(IPiece::isWhite);
        }
        return false;
    }

    private static boolean isDraw(double submissionOrDraw, Board board) {
        if (submissionOrDraw == VARS.FINALS.DRAW){
            return true;
        } else {

            VARS.MUTABLE.thirdSimilarPositionOfTheGame = VARS.MUTABLE.happenedList.keySet().stream().anyMatch(a -> VARS.MUTABLE.happenedList.get(a) == 3);

            boolean nextPlayerEmptyRange = METHODS.isNull(board.getCheckers()) &&
                                            board.getPieces(VARS.MUTABLE.whiteToPlay).stream().allMatch(p -> p.getPossibleRange().isEmpty());
            boolean allRemainingPieceIsKing = board.getPieces().size() == 2 &&
                                              board.getPieces().stream().allMatch(p -> p.getType() == PieceType.K);
            boolean remained2KingAnd1KnightOr1Bishop =
                                              board.getPieces().size() == 3 &&
                                              board.getPieces().stream().allMatch(p -> p.getType() == PieceType.K || p.getType() == PieceType.N || p.getType() == PieceType.B);
            boolean remained2KingAnd2DrawPieces = board.getPieces().size() == 4;
            if (remained2KingAnd2DrawPieces){
                ArrayList<IPiece> possibleKnight = new ArrayList<>(), possibleBishop = new ArrayList<>();
                for (IPiece p : board.getPieces()) {
                    if (p.getType() == PieceType.B) {
                        possibleBishop.add(p);
                    } else if (p.getType() == PieceType.N) {
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
                    if (p.getType() == PieceType.B) {
                        possibleBishop.add(p);
                    } else if (p.getType() == PieceType.N) {
                        possibleKnight.add(p);
                    }
                }
                remained2King2KnightFromSameColorAnd1Bishop =
                        possibleKnight.size() == 2 &&  possibleKnight.get(0).isWhite() == possibleKnight.get(1).isWhite() &&
                                possibleBishop.size() == 1 && possibleBishop.get(0).isWhite() != possibleKnight.get(0).isWhite();
            }
            return nextPlayerEmptyRange || VARS.MUTABLE.thirdSimilarPositionOfTheGame || allRemainingPieceIsKing || remained2KingAnd1KnightOr1Bishop || remained2KingAnd2DrawPieces ||
                    remained2King2KnightFromSameColorAnd1Bishop;
        }
    }

    public static boolean isDraw(){

        VARS.MUTABLE.thirdSimilarPositionOfTheGame = !VARS.MUTABLE.happenedList.isEmpty() && VARS.MUTABLE.happenedList.keySet().stream().anyMatch(a -> VARS.MUTABLE.happenedList.get(a) == 3);

        boolean allRemainingPiecesAreKing =
                BBVars.bitBoards[BBVars.wPawnI] == 0 && BBVars.bitBoards[BBVars.wKnightI] == 0 && BBVars.bitBoards[BBVars.wBishopI] == 0 && BBVars.bitBoards[BBVars.wRookI] == 0 && BBVars.bitBoards[BBVars.wQueenI] == 0 && BBVars.bitBoards[BBVars.wKingI] != 0 &&
                BBVars.bitBoards[BBVars.bPawnI] == 0 && BBVars.bitBoards[BBVars.bKnightI] == 0 && BBVars.bitBoards[BBVars.bBishopI] == 0 && BBVars.bitBoards[BBVars.bRookI] == 0 && BBVars.bitBoards[BBVars.bQueenI] == 0 && BBVars.bitBoards[BBVars.bKingI] != 0;
        boolean noBigOneRemained =
                (BBVars.bitBoards[BBVars.wPawnI] == 0 && BBVars.bitBoards[BBVars.wRookI] == 0 && BBVars.bitBoards[BBVars.wQueenI] == 0 && BBVars.bitBoards[BBVars.wKingI] != 0 &&
                 BBVars.bitBoards[BBVars.bPawnI] == 0 && BBVars.bitBoards[BBVars.bRookI] == 0 && BBVars.bitBoards[BBVars.bQueenI] == 0 && BBVars.bitBoards[BBVars.bKingI] != 0);
        boolean only1WBishop =
                (Long.bitCount(BBVars.bitBoards[BBVars.wBishopI]) == 1 && BBVars.bitBoards[BBVars.wKnightI] == 0 && BBVars.bitBoards[BBVars.bBishopI] == 0 && BBVars.bitBoards[BBVars.bKnightI] == 0);
        boolean only1BBishop =
                (Long.bitCount(BBVars.bitBoards[BBVars.bBishopI]) == 1 && BBVars.bitBoards[BBVars.wKnightI] == 0 && BBVars.bitBoards[BBVars.wBishopI] == 0 && BBVars.bitBoards[BBVars.bKnightI] == 0);
        boolean only1WKnight =
                (Long.bitCount(BBVars.bitBoards[BBVars.wKnightI]) == 1 && BBVars.bitBoards[BBVars.wBishopI] == 0 && BBVars.bitBoards[BBVars.bBishopI] == 0 && BBVars.bitBoards[BBVars.bKnightI] == 0);
        boolean only1BKnight =
                (Long.bitCount(BBVars.bitBoards[BBVars.bKnightI]) == 1 && BBVars.bitBoards[BBVars.wBishopI] == 0 && BBVars.bitBoards[BBVars.bBishopI] == 0 && BBVars.bitBoards[BBVars.wKnightI] == 0);
        boolean only1WBishop1BKnight =
                (Long.bitCount(BBVars.bitBoards[BBVars.wBishopI]) == 1 && (Long.bitCount(BBVars.bitBoards[BBVars.bKnightI]) == 1) && BBVars.bitBoards[BBVars.bBishopI] == 0 && BBVars.bitBoards[BBVars.wKnightI] == 0);
        boolean only1BBishop1WKnight =
                (Long.bitCount(BBVars.bitBoards[BBVars.bBishopI]) == 1 && (Long.bitCount(BBVars.bitBoards[BBVars.wKnightI]) == 1) && BBVars.bitBoards[BBVars.bBishopI] == 0 && BBVars.bitBoards[BBVars.wKnightI] == 0);
        boolean only2KnightFromTheSameColor =
                (Long.bitCount(BBVars.bitBoards[BBVars.wKnightI]) == 2 || Long.bitCount(BBVars.bitBoards[BBVars.bKnightI]) == 2) && BBVars.bitBoards[BBVars.bBishopI] == 0 && BBVars.bitBoards[BBVars.wBishopI] == 0;
        boolean twoBishopAgainstOne =
                (Long.bitCount(BBVars.bitBoards[BBVars.wBishopI]) == 2 && Long.bitCount(BBVars.bitBoards[BBVars.bBishopI]) == 1 && BBVars.bitBoards[BBVars.bKnightI] == 0 && BBVars.bitBoards[BBVars.wKnightI] == 0)  ||
                (Long.bitCount(BBVars.bitBoards[BBVars.wBishopI]) == 1 && Long.bitCount(BBVars.bitBoards[BBVars.bBishopI]) == 2 && BBVars.bitBoards[BBVars.bKnightI] == 0 && BBVars.bitBoards[BBVars.wKnightI] == 0);

        return VARS.MUTABLE.thirdSimilarPositionOfTheGame || allRemainingPiecesAreKing ||
                (noBigOneRemained &&
                        (only1WBishop || only1BBishop || only1WKnight || only1BKnight ||
                         only1WBishop1BKnight || only1BBishop1WKnight || only2KnightFromTheSameColor || twoBishopAgainstOne));


    }

    private static boolean isSubmission(double submissionOrDraw) {
        return submissionOrDraw == VARS.FINALS.WHITE_SUBMITTED || submissionOrDraw == VARS.FINALS.BLACK_SUBMITTED;
    }

    public static boolean itWorthRecommendDraw(){
        if (Board.getBoard().getPieces().size() == 3 && Board.getBoard().hasTwoKings() &&
                Board.getBoard().getPieces().stream().allMatch(p -> p.getType() == PieceType.K || p.getType() == PieceType.P)){
            IPiece onlyPawn = null;
            IPiece enemyKing;
            for (IPiece p : Board.getBoard().getPieces()) {
                if (p.getType() == PieceType.P){
                    onlyPawn = p;
                }
            }
            assert onlyPawn != null;
            enemyKing = Board.getBoard().getKing(!onlyPawn.isWhite());
            if (onlyPawn.getJ() != 0 || onlyPawn.getJ() != MAX_WIDTH - 1){
                return false;
            }
            int pawnDistance = Math.abs(onlyPawn.getJ() - onlyPawn.getEnemyStartRow());
            int kingDistance = Math.max(Math.abs(enemyKing.getJ() - onlyPawn.getJ()), Math.abs(enemyKing.getI() - onlyPawn.getEnemyStartRow()));
            return !(kingDistance < pawnDistance);
        }
        return Board.getBoard().hasTwoKings() && Board.getBoard().getPieces().size() == 4 &&
                Board.getBoard().getPieces().stream().allMatch(p -> p.getType() == PieceType.K || p.getType() == PieceType.N);
    }

    public static void finalGameEnd(){
        VARS.MUTABLE.gameEndFlag.set(true);
        getViewBoard().clearPiecesRanges();
        Board.getBoard().cleanBoard();
    }

    //endregion

}
