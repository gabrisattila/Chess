package classes.Controller;

import classes.Model.AI.Ai.AI;
import classes.Model.I18N.VARS;
import lombok.*;

import javax.swing.*;
import java.util.ArrayList;

import static classes.GUI.Frame.Window.*;
import static classes.GUI.FrameParts.ChessButton.ChessButtonMouseListener.saveBoard;
import static classes.GUI.FrameParts.Logger.initializeLogFile;
import static classes.GUI.FrameParts.ViewBoard.getViewBoard;
import static classes.Model.I18N.METHODS.switchWhoComes;
import static classes.Model.I18N.VARS.MUTABLE.*;
import static classes.Model.I18N.VARS.FINALS.*;
import static classes.Controller.FenConverter.*;
import static java.lang.Thread.sleep;

/**
 * Event Dispatch Thread
 */
@Getter
@Setter
public class EDT {

    //region Fields

    /**
     * This is the AI wich should start when player turn ends.
     * Then it finishes it's job. After player come again.
     * Similar with two AI. First initialize and works for white move.
     * Finishes it's job, and in the next move,
     * we initialize it again and works for black.
     */

    public static AI ai;

    //endregion

    //region Methods

    public static void initialization() {
        if (isFirstOpen){
            getWindow();
        } else {
            initializeAis();
            initializeLogFile();
            if (theresOnlyOneAi)
                getViewBoard().rangeUpdater();
        }
    }


    private static void initializeAis(){
        if (theresOnlyOneAi){
            if ((whiteToPlay && whiteAiNeeded) || (!whiteToPlay && !whiteAiNeeded)){
                aiTurn = true;
                playerTurn = false;
                SwingUtilities.invokeLater(EDT::startAI);
            }else {
                aiTurn = false;
                playerTurn = true;
            }
        }else {
            SwingUtilities.invokeLater(EDT::startAI);
        }
    }

    public static void startAI(){
        if (!gameEndFlag.get()){
            buttonsEnabled(new ArrayList<>(){{add("Új játék"); add("Mentés"); add("Szünet");}});
            ai = new AI();
            try {
                sleep(theresOnlyOneAi ? 300 : 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ai.start();
        }
    }

    public static void receivedMoveFromAi(String fen){
        setUpViewBoard(fen);
        if (theresOnlyOneAi){
            switchWhoComes();
            buttonsEnabled(new ArrayList<>(){{add("All");}});
            getViewBoard().rangeUpdater();
        }else {
            buttonsEnabled(new ArrayList<>(){{add("Új játék"); add("Mentés"); add("Szünet");}});
            startAI();
        }
        getViewBoard().setFieldColorsToNormal();
    }


    private static void setUpViewBoard(String fen)  {

        putTakenPieceToItsPlace(fen.split(" ")[0], BoardToFen(getViewBoard()).split(" ")[0]);

        FenToBoard(fen, getViewBoard());

    }


    //endregion

}
