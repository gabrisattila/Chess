package classes.Game.Model.Structure;

import classes.Ai.AiTree;
import classes.Ai.FenConverter;
import classes.GUI.FrameParts.ViewBoard;
import classes.Game.I18N.ChessGameException;
import lombok.*;

import javax.swing.*;

import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.getViewBoard;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.Board.*;

@Getter
public enum GameOver {

    CheckMate,

    Draw,

    Submission;
    
    public static void GameOverAction(Object game) {
        GameOver gameOver;
        if (game instanceof ViewBoard) {
            convertOneBoardToAnother(getViewBoard(), getBoard());
            getBoard().rangeUpdater();
            gameEndDialog(gameEnd(getBoard()));
        }else if (game instanceof Board){
            gameOver = gameEnd((Board) game);
            if (notNull(gameOver)){
                switch (gameOver) {
                    case CheckMate -> ((Board) game).setCheckMate(true);
                    case Draw -> ((Board) game).setDraw(true);
                    case Submission -> ((Board) game).setSubmitted(true);
                }
            }
        } else if (game instanceof AiTree) {
            gameOver = gameEnd(getBoard());
            if (notNull(gameOver)){
                switch (gameOver) {
                    case CheckMate -> getBoard().setCheckMate(true);
                    case Draw -> getBoard().setDraw(true);
                    case Submission -> getBoard().setSubmitted(true);
                }
            }
        }
    }

    private static void gameEndDialog(GameOver gameResult)  {
        if (notNull(gameResult)){
            String message = "";
            String title = "";
            switch (gameResult) {
                case CheckMate -> {
                    title = "Sakk Matt";
                    message = "A játék véget ért, Sakk Matt!";
                }
                case Draw -> {
                    title = "Döntetlen";
                    message = "A játék döntetlen lett!";
                }
                case Submission -> {
                    title = "A játéknak vége";
                    message = "A játék befejeződött!";
                }
            }

            JOptionPane.showMessageDialog(getViewBoard(), message, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static GameOver gameEnd(Board board) {
        if (notNull(board.getCheckers())){
            if (notNull(board.getCheckers().getSecond()) &&
                    board.getMyKing().getPossibleRange().isEmpty()){
                return CheckMate;
            } else if (isNull(board.getCheckers().getSecond()) &&
                    board.myPieces().stream().allMatch(p -> p.getPossibleRange().isEmpty())) {
                return CheckMate;
            }
        } else if (board.getPieces().stream().allMatch(p -> p.getType() == K) || board.myPieces().stream().allMatch(p -> p.getPossibleRange().isEmpty())){
            return Draw;
        }

        return null;
    }

}
