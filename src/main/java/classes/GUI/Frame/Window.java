package classes.GUI.Frame;

import classes.GUI.FrameParts.GameBoard;
import classes.Game.I18N.ChessGameException;
import lombok.*;

import javax.swing.*;

import java.util.Scanner;

import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

@Getter
@Setter
public class Window extends JFrame {

    //region Fields

    private static Window window;

    private GameBoard gameBoard;

    @Getter
    private static JTextArea logger;

    //endregion


    //region Constructor

    private Window() throws ChessGameException {

        setAiNumberDemand();

        frameSetup();
//        setUpSides();
        getViewBoard().pieceSetUp(usualFens.get("whiteDownPawnsFront"));

        gameBoard = new GameBoard(getViewBoard());
        add(gameBoard);

        logger = loggerBox();
//        add(logger);

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
        getContentPane().setBackground(BACK_GROUND);
        setLocationRelativeTo(null);
    }

    private void setAiNumberDemand(){

        System.out.println("\nSzeretné-e végig nézni a gép csatáját saját maga ellen, vagy inkább ön mérkőzik meg vele? \n (Igen / Nem)");

        theresOnlyOneAi = "Nem".equals(new Scanner(System.in).nextLine().trim());

        if (theresOnlyOneAi){
            System.out.println("Világossal szeretne lenni? (Igen / Nem)");
            whiteAiNeeded = "Nem".equals(new Scanner(System.in).nextLine().trim());
        }
//
//        theresOnlyOneAi = false;
//        whiteAiNeeded = true;
    }

    private void setUpSides() throws ChessGameException {
        getViewBoard().pieceSetUp(usualFens.get(
                theresOnlyOneAi ? (whiteAiNeeded ? "blackDownStarter" : "whiteDownStarter") :
                                  "whiteDownStarter"
            )
        );
    }


    /**
     * @return a textField where I document the steps
     */
    private JTextArea loggerBox() {
        JTextArea area = new JTextArea();
        area.setBounds((int) (LOGGER_START_X), (int) (LOGGER_START_Y), ((int) LOGGER_WIDTH), ((int) LOGGER_HEIGHT));
        area.setVisible(true);
        area.setEditable(false);
        return area;
    }

    //endregion

}
