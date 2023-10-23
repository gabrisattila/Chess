package classes.Game.Model.Logic;

import classes.Ai.AI;
import classes.Game.I18N.*;
import lombok.*;

import javax.swing.*;

import static classes.Ai.FenConverter.FenToBoard;
import static classes.GUI.Frame.Window.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;
import static java.lang.Thread.sleep;

/**
 * Event Dispatch Thread
 */
@Getter
@Setter
public class EDT {

    //region Fields

    /**
     * Always this should be the white one. Even if it's alone, even if it has a pair.
     */

    public static AI aiW;

    /**
     * Always this should be the black one. Even if it's alone, even if it has a pair.
     */
    public static AI aiB;

    //endregion


    //region Constructor

    public EDT() throws ChessGameException, InterruptedException {
        initialization();
    }

    //endregion


    //region Methods


    private void initialization() throws ChessGameException, InterruptedException {
        getWindow();
        SwingUtilities.invokeLater(this::initializeAis);

        if (theresOnlyOneAi && !whiteAiNeeded)
            getViewBoard().rangeUpdater();
    }

    private void initializeAis(){
        if (theresOnlyOneAi){
            if (whiteAiNeeded) {
                aiTurn = true;
                playerTurn = false;
                startAi("WHITE");
            }
            else {
                aiTurn = false;
                playerTurn = true;
            }
        }else {
            startAi("WHITE");
        }
    }

    public static void startAi(String color){
        if (WHITE_STRING.equals(color)){
            aiW = new AI(color);
            SwingUtilities.invokeLater(() -> {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                aiW.start();
            });
        }else {
            aiB = new AI(color);
            SwingUtilities.invokeLater(() -> {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                aiB.start();
            });
        }
    }

    public static void interruptAi(String color){
        if (WHITE_STRING.equals(color)){
            aiW.interrupt();
        }else {
            aiB.interrupt();
        }
    }


    public static void receivedMoveFromAi(String fen){
        try {
            FenToBoard(fen, getViewBoard());
            switchWhoComes();
            if (theresOnlyOneAi){
                getViewBoard().rangeUpdater();
            }else {
                startAi(whiteToPlay() ? "WHITE" : "BLACK");
            }
        } catch (ChessGameException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //endregion


}
