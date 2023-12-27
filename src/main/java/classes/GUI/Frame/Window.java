package classes.GUI.Frame;

import classes.GUI.FrameParts.*;
import classes.Game.I18N.Pair;
import classes.Game.I18N.PieceAttributes;
import classes.Game.Model.Structure.IField;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import static classes.GUI.FrameParts.GameBoard.cleanFieldFromLeftIcon;
import static classes.GUI.FrameParts.ViewBoard.getViewBoard;
import static classes.Game.I18N.METHODS.isNull;
import static classes.Game.I18N.PieceAttributes.charToPieceAttributes;
import static classes.Game.I18N.PieceAttributes.createSourceStringFromGotAttributes;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.canBeLogger;
import static java.util.Objects.requireNonNull;

@Getter
@Setter
public class Window extends JFrame {

    //region Fields

    private static Window window;

    @Getter
    private GameBoard gameBoard;

    @Getter
    private static Logger logger;

    @Getter
    private static ArrayList<ChessButton> buttons = buttons();

    @Getter
    private static Pair<ArrayList<ViewField>, ArrayList<ViewField>> takenPiecePlaces = new Pair<>(new ArrayList<>(16), new ArrayList<>(16));

    //endregion


    //region Constructor

    private Window()  {

        frameSetup();
        addGameBoard(this);

        addButtonsAndMayBeTheLoggerToo();
        setUpTakenPiecePlaces();

        setVisible(true);

    }

    public static Window getWindow()  {
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

    public static void setUpSides(String setUpFen)  {
        getViewBoard().pieceSetUp(setUpFen);
    }

    public void addGameBoard(Window window)  {
        window.clearTakenPiecePlaces();
        gameBoard = new GameBoard();
        window.add(gameBoard);
    }

    //endregion


    //region Game Buttons

    private void addButtonsAndMayBeTheLoggerToo() {
        addButtons();
        buttonsEnabled(new ArrayList<>(){{add("Új játék"); add("Betöltés"); }});

        addLogger();

    }

    private void addButtons() {
        for (JButton b : buttons) {
            this.add(b);
        }
    }

    public static void buttonsEnabled(ArrayList<String> enabledButtons){
        if (enabledButtons.contains("All")){
            for (ChessButton b : buttons) {
                b.setEnabled(true);
            }
            return;
        }
        if (enabledButtons.contains("None")){
            for (ChessButton b : buttons) {
                b.setEnabled(false);
            }
            return;
        }
        for (ChessButton b : buttons) {
            b.setEnabled(enabledButtons.contains(b.getText()));
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
        if (canBeLogger){
            logger = loggerBox();
            this.add(logger);
        }
    }

    /**
     * @return a textField where I document the steps
     */
    private Logger loggerBox() {
        return new Logger();
    }

    //endregion


    //region TakenPiecePlaces

    public static void putTakenPieceToItsPlace(String fenOfCurrentState, String fenOfPreviousState) {

        ArrayList<Character> prev = (ArrayList<Character>) fenOfPreviousState
                .chars()
                .filter(Character::isLetter)
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        ArrayList<Character> current = (ArrayList<Character>) fenOfCurrentState
                .chars()
                .filter(Character::isLetter)
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());

        if (current.size() != prev.size()){

            Collections.sort(prev);
            Collections.sort(current);
            char thePiece = '0';
            for (int i = 0; i < prev.size(); i++) {
                if (i == prev.size() - 1){
                    thePiece = prev.get(i);
                    break;
                }
                if (prev.get(i) != current.get(i)){
                    thePiece = prev.get(i);
                    break;
                }
            }
            PieceAttributes piece = charToPieceAttributes(thePiece);
            ViewPiece hit = new ViewPiece(createSourceStringFromGotAttributes(piece), piece);
            putTakenPieceToItsPlace(hit);
        }
    }

    public static void putTakenPieceToItsPlace(ViewPiece hit)  {
        requireNonNull(getNextFreePlaceForTakenPiece(hit.isWhite())).setPiece(hit);
    }

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

    private void clearTakenPiecePlaces()  {
        for (IField f : takenPiecePlaces.getFirst()) {
            cleanFieldFromLeftIcon(f);
        }
        for (IField f : takenPiecePlaces.getSecond()) {
            cleanFieldFromLeftIcon(f);
        }
    }

    //endregion

    //endregion

}
