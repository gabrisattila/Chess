package classes.Game.Model.Logic;

import classes.Ai.AI;
import classes.GUI.FrameParts.ChessGameButton;
import classes.GUI.FrameParts.ViewPiece;
import classes.Game.I18N.*;
import classes.Game.Model.Structure.IPiece;
import lombok.*;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static classes.Ai.FenConverter.BoardToFen;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                aiW.start();
            });
        }else {
            aiB = new AI(color);
            SwingUtilities.invokeLater(() -> {
                try {
                    sleep(500);
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
