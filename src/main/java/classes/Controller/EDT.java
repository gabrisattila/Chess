package classes.Controller;

import classes.Model.AI.Ai.AI;
import classes.Model.Game.I18N.VARS;
import lombok.*;

import javax.swing.*;
import java.util.ArrayList;

import static classes.GUI.Frame.Window.*;
import static classes.GUI.FrameParts.ChessButton.ChessButtonMouseListener.saveBoard;
import static classes.GUI.FrameParts.Logger.initializeLogFile;
import static classes.GUI.FrameParts.ViewBoard.getViewBoard;
import static classes.Model.Game.I18N.METHODS.switchWhoComes;
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


    //region Constructor

    public EDT() {
//        saveBoardInsteadOfException();
        initialization();
    }

    //endregion


    //region Methods


    public static void initialization() {
        if (VARS.MUTABLE.isFirstOpen){
            getWindow();
        } else {
            initializeAis();
            initializeLogFile();
            if (VARS.MUTABLE.theresOnlyOneAi)
                getViewBoard().rangeUpdater();
        }
    }


    private static void initializeAis(){
        if (VARS.MUTABLE.theresOnlyOneAi){
            if ((VARS.MUTABLE.whiteToPlay && VARS.MUTABLE.whiteAiNeeded) || (!VARS.MUTABLE.whiteToPlay && !VARS.MUTABLE.whiteAiNeeded)){
                VARS.MUTABLE.aiTurn = true;
                VARS.MUTABLE.playerTurn = false;
                SwingUtilities.invokeLater(EDT::startAI);
            }else {
                VARS.MUTABLE.aiTurn = false;
                VARS.MUTABLE.playerTurn = true;
            }
        }else {
            SwingUtilities.invokeLater(EDT::startAI);
        }
    }

    public static void startAI(){
        if (!VARS.MUTABLE.gameEndFlag.get()){
            buttonsEnabled(new ArrayList<>(){{add("Új játék"); add("Mentés"); add("Szünet");}});
            ai = new AI();
            try {
                sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ai.start();
        }
    }

    public static void receivedMoveFromAi(String fen){
        setUpViewBoard(fen);
        if (VARS.MUTABLE.theresOnlyOneAi){
            switchWhoComes();
            buttonsEnabled(new ArrayList<>(){{add("All");}});
            getViewBoard().rangeUpdater();
            getViewBoard().setFieldColorsToNormal();
        }else {
            buttonsEnabled(new ArrayList<>(){{add("Új játék"); add("Mentés"); add("Szünet");}});
            getViewBoard().setFieldColorsToNormal();
            startAI();
        }
    }


    private static void setUpViewBoard(String fen)  {

        putTakenPieceToItsPlace(fen.split(" ")[0], BoardToFen(getViewBoard()).split(" ")[0]);

        FenToBoard(fen, getViewBoard());

    }


    public static void saveBoardInsteadOfException(){
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.out.println(e.getMessage());
            e.printStackTrace();
            saveBoard(getViewBoard());
        });
    }

    //endregion


}
