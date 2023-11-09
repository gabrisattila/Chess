package classes.GUI.Frame;

import classes.GUI.FrameParts.ChessGameButton;
import classes.GUI.FrameParts.GameBoard;
import classes.GUI.FrameParts.ViewField;
import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Pair;
import lombok.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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

    @Getter
    private static ArrayList<ChessGameButton> buttons = buttons();

    private static Pair<ArrayList<ViewField>, ArrayList<ViewField>> takenPiecePlaces = new Pair<>(new ArrayList<>(16), new ArrayList<>(16));

    //endregion


    //region Constructor

    private Window(boolean twoAi, boolean whiteAi, boolean test) throws ChessGameException {

        setAiNumberDemand();

        frameSetup();
//        setUpSides();
//        getViewBoard().pieceSetUp(usualFens.get("whiteDownPawnsFront"));

        gameBoard = new GameBoard();
        add(gameBoard);

        addButtonsAndMayBeTheLoggerToo();
        setUpTakenPiecePlaces();


        setVisible(true);

    }

    public static void getWindow() throws ChessGameException {
        if (window == null){
            window = new Window(false, false, false);
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



    private void addButtonsAndMayBeTheLoggerToo() {
        addButtons();
        if (canBeLogger)
            addLogger();
    }

    private void addButtons() {
        for (JButton b : buttons) {
            this.add(b);
        }
    }

    public static ArrayList<ChessGameButton> buttons(){
        ArrayList<ChessGameButton> buttons = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            buttons.add(new ChessGameButton());
        }

        double buttonHeight;
        double buttonWidth;

        if (WHITE_TAKEN_PIECES_FIRST_ROW_START_X >= 150){

            buttons.get(0).setText("Új játék");
            buttons.get(1).setText("Szünet");
            buttons.get(2).setText("Mentés");
            buttons.get(3).setText("Betöltés");
            buttons.get(4).setText("Feladás");
            buttons.get(5).setText("Döntetlen");

            buttonWidth = WHITE_TAKEN_PIECES_FIRST_ROW_START_X - 50;
            buttonHeight = FIELD_HEIGHT;

            for (int i = 0; i < 4; i++) {
                buttons.get(i).setBounds(20, (int) (BUTTON_PLACE_START_Y + i * 2 * buttonHeight), (int) buttonWidth, (int) buttonHeight);
            }

            buttons.get(4).setBounds(
                    (int) (SCREEN_WIDTH - buttonWidth - 20),
                    (int) BUTTON_PLACE_START_Y,
                    (int) buttonWidth,
                    (int) buttonHeight
            );

            buttons.get(5).setBounds(
                    (int) (SCREEN_WIDTH - buttonWidth - 20),
                    (int) BUTTON_PLACE_START_Y + 2 * FIELD_WIDTH,
                    (int) buttonWidth,
                    (int) buttonHeight
            );
            buttons.get(5).setFont(new Font("Source Code Pro", Font.BOLD, 18));

            canBeLogger = true;

        }else {

            buttons.get(0).setText("Mentés");
            buttons.get(1).setText("Betöltés");
            buttons.get(2).setText("Új játék");
            buttons.get(3).setText("Szünet");
            buttons.get(4).setText("Feladás");
            buttons.get(5).setText("Döntetlen");

            buttonWidth = (SCREEN_WIDTH - 140) / 6;
            buttonHeight = BOARD_START_Y - 40;

            for (int i = 0; i < 6; i++) {
                buttons.get(i).setBounds((int) (i * (buttonWidth + 20)) + 20, 10, (int) buttonWidth, (int) buttonHeight);
            }

            canBeLogger = false;
        }

        return buttons;
    }

    private void addLogger() {
        logger = loggerBox();
        this.add(logger);
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

    private void setUpTakenPiecePlaces(){
        setUpASideForTakenPieces(true);
        setUpASideForTakenPieces(false);
        for (ViewField f : takenPiecePlaces.getFirst()) {
            this.add(f);
        }
        for (ViewField f : takenPiecePlaces.getSecond()) {
            this.add(f);
        }
    }

    private void setUpASideForTakenPieces(boolean whiteSide){
        ViewField fieldForTaken;
        for (int i = 0; i < 16; i++) {

            fieldForTaken = new ViewField();

            int startX = (int)
                    (whiteSide ?
                            (i < 8 ? WHITE_TAKEN_PIECES_FIRST_ROW_START_X : WHITE_TAKEN_PIECES_SECOND_ROW_START_X) :
                            (i < 8 ? BLACK_TAKEN_PIECES_FIRST_ROW_START_X : BLACK_TAKEN_PIECES_SECOND_ROW_START_X)
                    );
            int startY = (int) (i < 8 ? (BOARD_START_Y + i * FIELD_HEIGHT) : (BOARD_START_Y + (i - 8) * FIELD_HEIGHT));

            fieldForTaken.setBounds(
                    startX,
                    startY,
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );

            fieldForTaken.setBackground(BACK_GROUND);
            fieldForTaken.setBorder(BorderFactory.createLineBorder(WHITE));
            fieldForTaken.setOpaque(true);
            fieldForTaken.setFocusable(false);
            fieldForTaken.setVisible(true);
            fieldForTaken.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    // Ne csinálj semmit, amikor az egér belép a gomb területére
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // Ne csinálj semmit, amikor az egér elhagyja a gomb területét
                }
            });

            (whiteSide ? takenPiecePlaces.getFirst() : takenPiecePlaces.getSecond()).add(fieldForTaken);

        }
    }

    //endregion

}
