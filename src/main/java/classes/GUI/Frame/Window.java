package classes.GUI.Frame;

import classes.GUI.FrameParts.GameBoard;
import classes.Game.I18N.ChessGameException;
import lombok.*;

import javax.swing.*;

import java.util.Scanner;

import java.io.IOException;
import java.util.Scanner;

import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;

@Getter
@Setter
public class Window extends JFrame {

    //region Fields

    private static Window window;

    private GameBoard gameBoard;

    //endregion


    //region Constructor

    private Window() throws ChessGameException {

        setAiNumberDemand();

        frameSetup();
        getViewBoard().pieceSetUp(usualFens.get("baseWhiteDownStarter"));

        //        fieldNums(getViewBoard().getFields());
        gameBoard = new GameBoard(getViewBoard());
        add(gameBoard);
        setVisible(true);

    }

    public static void getWindow() throws ChessGameException {
        if (window == null){
            window = new Window();
        }
    }

    //endregion


    //region Methods

    private void frameSetup(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(screenSize);
        setTitle("Sakk Dolgozat");
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void setAiNumberDemand(){

        System.out.println("Szeretné-e végig nézni a gép csatáját saját maga ellen, vagy inkább ön mérkőzik meg vele? \n (Igen / Nem)");

        theresOnlyOneAi = "Nem".equals(new Scanner(System.in).nextLine().trim());

        if (theresOnlyOneAi){
            System.out.println("Világossal szeretne lenni? (Igen / Nem)");
            whiteAiNeeded = "Nem".equals(new Scanner(System.in).nextLine().trim());
        }
    }


    //endregion

}
