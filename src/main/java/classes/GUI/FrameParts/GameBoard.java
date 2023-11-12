package classes.GUI.FrameParts;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.Model.Structure.IField;
import lombok.*;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;

import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.GUI.FrameParts.ViewBoard.*;

@Getter
@Setter
public class GameBoard extends JLayeredPane {

    //region Fields

    private ViewBoard parentBoard;

    private JComboBox<JButton> whatToPlay;

    private static ArrayList<Location> edgeCoordinates = new ArrayList<>();

    private static ArrayList<SideLabel> labels = new ArrayList<>(MAX_WIDTH * 2 + MAX_HEIGHT * 2);

    private ArrayList<CornerSquare> corners = new ArrayList<>(4);

    //endregion


    //region Constructor

    public GameBoard() throws ChessGameException {
        parentBoard = getViewBoard();
        gameBoardSetUp();
    }

    //endregion


    //region Methods

    private void gameBoardSetUp() throws ChessGameException {

        setVisible(true);

        setBoardCoordinates();

        rotateBoard();
        addFieldsAtTheirFinalForm();
//        fieldNumPrinter(getViewBoard());

        addCorners();
        addLabels();
    }

    private void setBoardCoordinates(){
        setBounds((int) BOARD_START_X - 20, (int) BOARD_START_Y - 20, MAX_WIDTH * FIELD_WIDTH + 40, MAX_HEIGHT * FIELD_HEIGHT + 40);
    }

    private void rotateBoard(){

        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                Point rotatedPosition = rotateAntiClockwise(new Point(i, j), MAX_HEIGHT);

                int newI = rotatedPosition.x * FIELD_HEIGHT + 20;
                int newJ = rotatedPosition.y * FIELD_WIDTH + 20;

                ((ViewField)parentBoard.getField(i, j)).setBounds(
                        newI,
                        newJ,
                        FIELD_WIDTH,
                        FIELD_HEIGHT
                );
            }
        }


    }

    private Point rotateAntiClockwise(Point point, int width) {
        int newX = point.y;
        int newY = width - point.x - 1;
        return new Point(newX, newY);
    }

    private void addFieldsAtTheirFinalForm(){
        for (ArrayList<IField> row: parentBoard.getFields()){
            for (IField f : row) {
                add((ViewField) f);
            }
        }
    }


    private void addCorners() {
        add(new CornerSquare("UL"));
        add(new CornerSquare("UR"));
        add(new CornerSquare("DL"));
        add(new CornerSquare("DR"));
    }

    private void addLabels() {

        collectEdges();
        addLabelsByLocation(true, this);

    }

    private void collectEdges() {

        edgeCoordinates.add(new Location(0, 0));

        int lastNum = 0;

        //TODO 0-tól újraindexelve megoldani.

        for (int j = 1; j <= MAX_HEIGHT; j++) {
            edgeCoordinates.add(new Location(0, (j * FIELD_HEIGHT) + 20));

            if (j == MAX_HEIGHT)
                lastNum = j * FIELD_HEIGHT;
        }
        for (int j = 1; j <= MAX_WIDTH; j++) {
            edgeCoordinates.add(new Location(j * FIELD_WIDTH, lastNum + 20));

            if (j == MAX_WIDTH)
                lastNum = j * FIELD_WIDTH;
        }
        for (int j = MAX_HEIGHT - 1; j >= 0; j--){
            edgeCoordinates.add(new Location(lastNum, j * FIELD_HEIGHT - 20));

            if (j == 0)
                lastNum = 0;
        }

        for (int j = MAX_WIDTH - 1; j > 0; j--){
            edgeCoordinates.add(new Location(j * FIELD_WIDTH + 20, lastNum));
        }

    }

    public static void addLabelsByLocation(boolean whiteDown, GameBoard board) {

        int helperCounterForLabels = 0;
        boolean vertical = true;
        SideLabel label;
        for (Location l : edgeCoordinates) {

            if (helperCounterForLabels != 0 && (vertical ? (helperCounterForLabels % MAX_HEIGHT) : (helperCounterForLabels % MAX_WIDTH)) == 0){
                vertical = ! vertical;
            }
            label = new SideLabel(l.getI(), l.getJ(), vertical, whiteDown);
            labels.add(label);
            board.add(label);
            helperCounterForLabels++;
        }

    }

    //endregion
}
