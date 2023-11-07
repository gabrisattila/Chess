package classes.GUI.FrameParts;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.Model.Structure.IField;
import lombok.*;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;

import static classes.Game.I18N.Helpers.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.GUI.FrameParts.ViewBoard.*;

@Getter
@Setter
public class GameBoard extends JLayeredPane {

    //region Fields

    private ViewBoard parentBoard;

    private JComboBox<JButton> whatToPlay;

    private ArrayList<Location> edgeCoordinates = new ArrayList<>();

    private ArrayList<SideLabel> labels = new ArrayList<>(MAX_WIDTH * 2 + MAX_HEIGHT * 2);

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
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        setBoardCoordinates();

        rotateBoard();
        addFieldsAtTheirFinalForm();
        fieldNumPrinter(getViewBoard());

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

                if (fieldGonnaBeOnEdge(newI, newJ)){
                    edgeCoordinates.add(new Location(newI, newJ));
                }

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

    private boolean fieldGonnaBeOnEdge(int newI, int newJ) {

        return newI == 0 || newI == FIELD_HEIGHT * (MAX_HEIGHT - 1) ||
                newJ == 0 || newJ == FIELD_WIDTH * (MAX_WIDTH - 1);

    }



    private void addCorners() {
        add(new CornerSquare("UL"));
        add(new CornerSquare("UR"));
        add(new CornerSquare("DL"));
        add(new CornerSquare("DR"));
    }

    private void addLabels() {
        SideLabel label = null;
        for (Location l : edgeCoordinates) {
            if (l.getI() == 0){
                label = new SideLabel(0, l.getJ(), true);
            } else if (l.getI() == MAX_HEIGHT * (MAX_HEIGHT - 1)) {
                label = new SideLabel(l.getI() + FIELD_WIDTH + 20, l.getJ(), true);
            } else if (l.getJ() == 0) {
                label = new SideLabel(l.getI(), 0, false);
            } else if (l.getJ() == FIELD_WIDTH * (MAX_WIDTH - 1)) {
                label = new SideLabel(l.getI(), l.getJ() + FIELD_HEIGHT + 20, false);
            }
            labels.add(label);
            this.add(label);
        }
    }

    //endregion
}
