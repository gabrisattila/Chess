package classes.GUI.FrameParts;

import classes.Game.I18N.Location;
import classes.Game.Model.Structure.IField;
import lombok.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import static classes.GUI.FrameParts.SideLabel.*;
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

    private static ArrayList<String> labelTextList;

    private ArrayList<CornerSquare> corners = new ArrayList<>(4);

    //endregion


    //region Constructor

    public GameBoard()  {
        parentBoard = getViewBoard();
        gameBoardSetUp();
    }

    //endregion


    //region Methods

    public static void clearLeftIcons()  {

        for (var v : getViewBoard().getFields()) {
            for (IField f : v) {
                cleanFieldFromLeftIcon(f);
            }
        }
    }

    public static void cleanFieldFromLeftIcon(IField f)  {
        f.clean();
        Timer timer = new Timer(1000, new ActionListener() {
            private boolean isHovered = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Az egér belépését szimuláljuk
                if (!isHovered) {
                    ((JButton) f).getModel().setRollover(true);
                    isHovered = true;
                } else {
                    // Az egér kijutását szimuláljuk
                    ((JButton) f).getModel().setRollover(false);
                    isHovered = false;
                }

                // A JButton újrarajzolása a változások érvényesítése érdekében
                ((JButton) f).repaint();
            }
        });
        // A Timer indítása
        timer.start();
    }

    private void gameBoardSetUp()  {

        setBoardCoordinates();

        rotateBoard();
        addFieldsAtTheirFinalForm();
        clearLeftIcons();
//        viewFieldPrinter(new ArrayList<>(){{add("base"); add("Pw");}});


        addCorners();
        addLabels();
        setVisible(true);
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

    private void addFieldsAtTheirFinalForm() {
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
        addLabelsByLocation(this);

    }

    private void collectEdges() {

        edgeCoordinates = new ArrayList<>();

        int lastNum = 0;

        for (int j = 0; j < MAX_HEIGHT; j++) {
            edgeCoordinates.add(new Location(0, (j * FIELD_HEIGHT) + 20));

            if (j == MAX_HEIGHT - 1) {
                lastNum = (j + 1) * FIELD_HEIGHT;
            }
        }
        for (int j = 0; j < MAX_WIDTH; j++) {
            edgeCoordinates.add(new Location(j * FIELD_WIDTH + 20, lastNum + 20));

            if (j == MAX_WIDTH - 1)
                lastNum = (j + 1) * FIELD_WIDTH;
        }
        for (int j = MAX_HEIGHT - 1; j >= 0; j--){
            edgeCoordinates.add(new Location(lastNum + 20, j * FIELD_HEIGHT + 20));

            if (j == 0)
                lastNum = 0;
        }
        for (int j = MAX_WIDTH - 1; j >= 0; j--){
            edgeCoordinates.add(new Location(j * FIELD_WIDTH + 20, lastNum));
        }

    }

    public static void addLabelsByLocation(GameBoard board) {

        int helperCounterForLabels = 0;
        boolean vertical = true;
        SideLabel label;
        labels = new ArrayList<>();

        for (Location l : edgeCoordinates) {

            if (helperCounterForLabels != 0 && (vertical ? (helperCounterForLabels % MAX_HEIGHT) : (helperCounterForLabels % MAX_WIDTH)) == 0){
                vertical = ! vertical;
            }
            label = new SideLabel(l.getI(), l.getJ(), vertical);
            labels.add(label);
            board.add(label);
            helperCounterForLabels++;
        }

    }

    public static void labelTexting(boolean whiteDown)  {
        labelTextList = collectProperLabelTexts(whiteDown);
        int i = 0;
        for (SideLabel label : labels) {
            label.setText(labelTextList.get(i));
            i++;
        }

        if (nums.get(0) == '8')
            Collections.reverse(nums);
        if (abc.get(0) == 'N')
            Collections.reverse(abc);
        for (int j = 0; j < MAX_HEIGHT; j++) {
            for (int k = 0; k < MAX_WIDTH; k++) {
                ((ViewField) getViewBoard().getField(j, k)).setCol(whiteDown ? nums.get(j) : nums.get(MAX_HEIGHT - (j + 1)));
                ((ViewField) getViewBoard().getField(j, k)).setRow(whiteDown ? abc.get(k) : abc.get(MAX_HEIGHT - (k + 1)));
            }
        }
    }

    //endregion
}
