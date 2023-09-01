package classes.Game.Model.Logic;

import classes.Ai.AI;
import classes.Game.I18N.*;
import lombok.*;

import javax.swing.*;

import static classes.GUI.Frame.Window.*;

/**
 * Event Dispatch Thread
 */
@Getter
@Setter
public class EDT extends Thread {

    //region Fields

    public static boolean theresOnlyOneAi;

    public static boolean whiteAiNeeded;

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

    }

    //endregion


    //region Methods

    @Override
    public void run(){
        try {
            getWindow();
            initializeAis();
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeAis() {

        theresOnlyOneAi = false;
        whiteAiNeeded = true;

        if (theresOnlyOneAi){
            if (whiteAiNeeded) {
                aiW = new AI("WHITE");
                aiW.start();
                SwingUtilities.invokeLater(() -> {
                    try {
                        sleep(5000);
                        aiW.aiTurn();
                    } catch (ChessGameException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            else {
                aiB = new AI("BLACK");
                aiB.start();
            }
        }else {
            aiW = new AI("WHITE");
            aiB = new AI("BLACK");
            aiW.start();
            aiB.start();
            SwingUtilities.invokeLater(() ->{
                try {
                    sleep(3000);
                    aiW.aiTurn();
                    sleep(3000);
                    aiB.aiTurn();
                } catch (ChessGameException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    //endregion


}
