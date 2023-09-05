package classes.Game.Model.Logic;

import classes.Ai.AI;
import classes.Game.I18N.*;
import lombok.*;

import static classes.GUI.Frame.Window.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;

/**
 * Event Dispatch Thread
 */
@Getter
@Setter
public class EDT extends Thread {

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
    }

    //endregion


    //region Methods

    @Override
    public void run(){
        try {
            initialization();
            while(gameIsOn && theresOnlyOneAi){
                if (whiteAiNeeded != whiteToPlay){
                    getViewBoard().updatePieceRanges();
                    synchronized (this){
                        wait();
                    }
                }
            }
        } catch (ChessGameException | InterruptedException e) {
            throw new RuntimeException(e);
        }
//        while (gameIsOn){
//            if (whiteAiNeeded != whiteToPlay){
//
//            }
//        }
    }

    private void initialization() throws ChessGameException, InterruptedException {
        setName("EDT");
        gameIsOn = true;
        getWindow();
        initializeAis();
        getViewBoard().updatePieceRanges();
    }

    private void initializeAis() throws InterruptedException {

        if (theresOnlyOneAi){
            if (whiteAiNeeded) {
                initializeAi("WHITE");
                sleep(2000);
                aiW.start();
            }
            else {
                initializeAi("BLACK");
            }
        }else {
            initializeAi("WHITE");
            initializeAi("BLACK");
            aiMove();
        }
    }

    private void initializeAi(String color){
        if ("WHITE".equals(color)){
            aiW = new AI(color);
            aiW.setName("aiW");
        }else {
            aiB = new AI(color);
            aiB.setName("aiB");
        }
    }

    //endregion


}
