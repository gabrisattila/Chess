package classes.Game.Model.Logic;

import classes.Ai.AI;
import classes.Game.I18N.*;
import lombok.*;

import javax.swing.*;

import static classes.Ai.FenConverter.BoardToFen;

import static classes.Ai.FenConverter.FenToBoard;
import static classes.GUI.Frame.Window.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
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


    public static void initialization() throws ChessGameException, InterruptedException {
        if (isFirstOpen){
            getWindow();
        } else {
            SwingUtilities.invokeLater(EDT::initializeAis);

            if (theresOnlyOneAi && !whiteAiNeeded)
                getViewBoard().rangeUpdater();
        }
    }

    private static void initializeAis(){
        if (theresOnlyOneAi){
            if (whiteAiNeeded) {
                aiTurn = true;
                playerTurn = false;
                startAI();
            }
            else {
                aiTurn = false;
                playerTurn = true;
            }
        }else {
            startAI();
        }
    }

    public static void startAI(){
        startAnAi(whiteToPlay ? "WHITE" : "BLACK");
    }

    public static void startAnAi(String color){
        if (WHITE_STRING.equals(color)){
            aiW = new AI(color);
            SwingUtilities.invokeLater(() -> {
//                try {
//                    sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
                aiW.start();
            });
        }else {
            aiB = new AI(color);
            SwingUtilities.invokeLater(() -> {
//                try {
//                    sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
                aiB.start();
            });
        }
    }

    public static void receivedMoveFromAi(String fen){
        try {
            setUpViewBoard(fen);
            if (theresOnlyOneAi){
                switchWhoComes();
                getViewBoard().rangeUpdater();
            }else {
                startAI();
            }
        } catch (ChessGameException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setUpViewBoard(String fen) throws ChessGameException {

        putTakenPieceToItsPlace(fen.split(" ")[0], BoardToFen(getViewBoard()).split(" ")[0]);

        FenToBoard(fen, getViewBoard());

    }


    //endregion


}
