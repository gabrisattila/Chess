package classes.Game.Model.Logic;

import classes.Ai.AI;
import classes.Game.I18N.*;
import lombok.*;

import javax.swing.*;

import static classes.GUI.Frame.Window.*;
import static classes.Game.I18N.METHODS.aiMove;
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

    public EDT(){
        aiGameIsOn = true;
    }

    //endregion


    //region Methods

    @Override
    public void run(){
        try {
            getWindow();
            initializeAis();
        } catch (ChessGameException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeAis() throws InterruptedException {

        if (theresOnlyOneAi){
            if (whiteAiNeeded) {
                aiW = new AI("WHITE");
                sleep(1000);
                aiW.start();
            }
            else {
                aiB = new AI("BLACK");
            }
        }else {
            aiW = new AI("WHITE");
            aiB = new AI("BLACK");
            aiMove();
        }
    }

    //endregion


}
