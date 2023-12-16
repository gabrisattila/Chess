package classes.Game.Model.Structure;

import classes.Ai.AiNode;
import classes.GUI.FrameParts.ViewBoard;
import classes.Game.I18N.PieceType;
import lombok.*;

import javax.swing.*;

import java.util.ArrayList;

import static classes.Ai.AI.*;
import static classes.Ai.Evaluator.*;
import static classes.Ai.FenConverter.*;
import static classes.GUI.Frame.Window.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.Board.*;

@Getter
@Setter
public class GameOverOrPositionEnd {

    public static double GameOverDecision(Object game, boolean directViewCase, double submissionOrDrawComeFromPlayer) {

        double gameOver;

        if (game instanceof ViewBoard) {
            convertOneBoardToAnother(getViewBoard(), getBoard());
        } else if (game instanceof AiNode) {
            AiFenToBoard(((AiNode) game).getFen(), getBoard());
        }

        gameOver = gameEnd(getBoard(), directViewCase, submissionOrDrawComeFromPlayer);

        if (directViewCase && GAME_OVER_CASES.contains(gameOver)){
            gameEndDialog(gameOver);
        }
        return gameOver;
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

        JOptionPane.showMessageDialog(getViewBoard(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    //endregion

    //region Game End Calc

    public static double gameEnd(Board board, boolean directViewCase, double submissionOrDraw) {

        board.rangeUpdater();

        if (isCheckMate(board)){
            finalGameEnd(directViewCase);
            return whiteToPlay ? WHITE_GOT_CHECKMATE : BLACK_GOT_CHECKMATE;
        } else if (isDraw(submissionOrDraw, board)) {
            finalGameEnd(directViewCase);
            return DRAW;
        } else if (isSubmission(submissionOrDraw) || itWorthToGiveUp()) {
            finalGameEnd(directViewCase);
            return submissionOrDraw;
        }

        if (submissionOrDraw == DRAW_OFFER && itWorthToOfferOrRecommendDraw()) {
            finalGameEnd(directViewCase);
            return DRAW;
        } else {
            if (directViewCase){
                showFlashFrame("Döntetlent ajánlottál, \naz ellenfeled nem fogadta el.", 5);
            }
        }

        return evaluate();
    }

    private static boolean isCheckMate(Board board) {
        if (board.hasTwoKings()){
            if (notNull(board.getCheckers())){
                if (notNull(board.getCheckers().getSecond())){
                    return board.getKing(whiteToPlay).getPossibleRange().isEmpty();
                }else {
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
            thirdSimilarPositionOfTheGame = happenedList.keySet().stream().anyMatch(a -> happenedList.get(a) == 3) ||
                                            happenedListZKeys.keySet().stream().anyMatch(a -> happenedListZKeys.get(a) == 3);
            boolean nextPlayerEmptyRange = isNull(board.getCheckers()) &&
                                            board.getPieces(whiteToPlay).stream().allMatch(p -> p.getPossibleRange().isEmpty());
            boolean allRemainingPieceIsKing = board.getPieces().size() == 2 &&
                                              board.getPieces().stream().allMatch(p -> p.getType() == K);
            boolean remained2KingAnd1KnightOr1Bishop =
                                              board.getPieces().size() == 3 &&
                                              board.getPieces().stream().allMatch(p -> p.getType() == K || p.getType() == N || p.getType() == PieceType.R);
            boolean remained2KingAnd2Knight = board.getPieces().size() == 4;
            if (remained2KingAnd2Knight){
                IPiece possibleKnight = null, possibleBishop = null;
                int knightCount = 0, bishopCount = 0;
                for (IPiece p : board.getPieces()) {
                    if (p.getType() == PieceType.R) {
                        possibleBishop = p;
                        bishopCount++;
                    } else if (p.getType() == N) {
                        possibleKnight = p;
                        knightCount++;
                    }
                }
                remained2KingAnd2Knight = knightCount == 1 && bishopCount == 1 && possibleKnight.isWhite() != possibleBishop.isWhite();
            }
            return nextPlayerEmptyRange || thirdSimilarPositionOfTheGame || allRemainingPieceIsKing || remained2KingAnd1KnightOr1Bishop || remained2KingAnd2Knight;
        }
    }

    private static boolean isSubmission(double submissionOrDraw) {
        return submissionOrDraw == WHITE_SUBMITTED || submissionOrDraw == BLACK_SUBMITTED;
    }


    public static boolean itWorthToGiveUp(){

        double enemyPiecesValueSum = getBoard().getPieces(!whiteToPlay).stream().mapToDouble(p -> ((Piece) p).getVALUE()).sum();
        double myPiecesValueSum = getBoard().getPieces(whiteToPlay).stream().mapToDouble(p -> ((Piece) p).getVALUE()).sum();

        return Math.abs(enemyPiecesValueSum + myPiecesValueSum) > ROOK_BASE_VALUE + KNIGHT_OR_BISHOP_BASE_VALUE &&
                getBoard().getPieces(whiteToPlay).stream().allMatch(p -> p.getType() == K || p.getType() == P);
    }

    public static boolean itWorthToOfferOrRecommendDraw(){
        if (getBoard().getPieces().size() == 3 && getBoard().hasTwoKings() &&
                getBoard().getPieces().stream().allMatch(p -> p.getType() == K || p.getType() == P)){
            IPiece onlyPawn = null;
            IPiece enemyKing = null;
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


    private static void finalGameEnd(boolean directViewCase){
        if (directViewCase) {
            gameEndFlag.set(true);
            buttonsEnabled(new ArrayList<>(){{add("None");}});
            getViewBoard().clearPiecesRanges();
        }
    }

    //endregion

}
