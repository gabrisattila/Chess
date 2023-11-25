package classes.GUI.Frame;

import classes.GUI.FrameParts.ChessButton;
import classes.GUI.FrameParts.GameBoard;
import classes.GUI.FrameParts.Logger;
import classes.GUI.FrameParts.ViewField;
import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Pair;
import lombok.*;

import javax.swing.*;

import java.awt.*;
import java.util.*;

import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

@Getter
@Setter
public class Window extends JFrame {

    //region Fields

    private static Window window;

    @Getter
    private static GameBoard gameBoard;

    @Getter
    private static Logger logger;

    @Getter
    private static ArrayList<ChessButton> buttons = buttons();

    private static Pair<ArrayList<ViewField>, ArrayList<ViewField>> takenPiecePlaces = new Pair<>(new ArrayList<>(16), new ArrayList<>(16));

    //endregion


    //region Constructor

    private Window() throws ChessGameException {

        frameSetup();
        addGameBoard(this);

        addButtonsAndMayBeTheLoggerToo();
        setUpTakenPiecePlaces();

        setVisible(true);

    }

    public static Window getWindow() throws ChessGameException {
        if (isNull(window))
            window = new Window();
        return window;
    }

    //endregion


    //region Methods

    //region Frame Basics

    private void frameSetup(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(screenSize);
        setTitle("Sakk Dolgozat");
        setResizable(false);
        getContentPane().setBackground(BACK_GROUND);
        setLocationRelativeTo(null);
    }

    public static void setUpSides(String setUpFen) throws ChessGameException {

        getViewBoard().pieceSetUp(setUpFen);

    }

    public static void addGameBoard(Window window) throws ChessGameException {
        deleteViewBoard();
        if (notNull(gameBoard))
            window.remove(gameBoard);
        gameBoard = new GameBoard();
        window.add(gameBoard);
    }

    //endregion


    //region Game Buttons

    private void addButtonsAndMayBeTheLoggerToo() {
        addButtons();
        buttonsEnabled();

        canBeLogger = false;
        if (canBeLogger) {
            addLogger();
        }
    }

    private void addButtons() {
        for (JButton b : buttons) {
            this.add(b);
        }
    }

    public static void buttonsEnabled(){
        for (ChessButton b : buttons) {
            if (!"Új játék".equals(b.getText()) && !"Betöltés".equals(b.getText()))
                b.setEnabled(!isFirstOpen);
        }
    }

    public static ArrayList<ChessButton> buttons(){
        ArrayList<ChessButton> buttons = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            buttons.add(new ChessButton());
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

    //endregion


    //region Logger

    private void addLogger() {
        logger = loggerBox();
        this.add(logger);
    }

    /**
     * @return a textField where I document the steps
     */
    private Logger loggerBox() {
        return new Logger();
    }

    //endregion


    //region TakenPiecePlaces

    public static ViewField getNextFreePlaceForTakenPiece(boolean forWhite){
        for(ViewField f : (forWhite ? getWhiteTakenPiecesPlace() : getBlackTakenPiecesPlace())){
            if (!f.isGotPiece()){
                return f;
            }
        }
        return null;
    }

    public static ArrayList<ViewField> getWhiteTakenPiecesPlace(){
        return takenPiecePlaces.getFirst();
    }

    public static ArrayList<ViewField> getBlackTakenPiecesPlace(){
        return takenPiecePlaces.getSecond();
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
                            (i < 8 ? WHITE_TAKEN_PIECES_FIRST_ROW_START_X : WHITE_TAKEN_PIECES_SECOND_ROW_START_X) - 10 :
                            (i < 8 ? BLACK_TAKEN_PIECES_FIRST_ROW_START_X : BLACK_TAKEN_PIECES_SECOND_ROW_START_X) + 10
                    );
            int startY = (int) (i < 8 ? (BOARD_START_Y + i * FIELD_HEIGHT) : (BOARD_START_Y + (i - 8) * FIELD_HEIGHT));

            fieldForTaken.setBounds(
                    startX,
                    startY,
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );

            fieldForTaken.setBackground(whiteSide ? BLACK : WHITE);
            fieldForTaken.setBorder(BorderFactory.createLineBorder(whiteSide ? WHITE : BLACK));
            fieldForTaken.setOpaque(true);
            fieldForTaken.setFocusable(false);
            fieldForTaken.setVisible(true);

            (whiteSide ? takenPiecePlaces.getFirst() : takenPiecePlaces.getSecond()).add(fieldForTaken);

        }
    }

    //endregion

    //endregion

}
