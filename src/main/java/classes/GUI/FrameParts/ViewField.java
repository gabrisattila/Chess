package classes.GUI.FrameParts;

import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.Model.Logic.EDT;
import classes.Game.Model.Structure.IPiece;
import classes.Game.Model.Structure.Move;
import lombok.*;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static classes.AI.Ai.AiNode.appendToHappenedList;
import static classes.GUI.Frame.Window.putTakenPieceToItsPlace;
import static classes.GUI.FrameParts.ViewBoard.getViewBoard;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Logic.FenConverter.BoardToFen;
import static classes.Game.Model.Structure.Move.Step;

@Getter
@Setter
public class ViewField extends JButton implements classes.Game.Model.Structure.IField {

    //region Fields

    private Location loc;

    /**
     * The x coordinate of the upper right corner. (Where it start drawn.)
     */
    private int x;

    /**
     * The y coordinate of the upper right corner. (Where it start drawn.)
     */
    private int y;

    private char row;

    private char col;

    private String fieldColor;

    private ViewPiece piece;

    private boolean gotPiece;

    private ArrayList<Location> options;

    //endregion


    //region Constructor

    public ViewField(){}

    public ViewField(Location loc, String fieldColor){
        this.loc = loc;
        this.fieldColor = fieldColor;
        setSize(FIELD_WIDTH, FIELD_HEIGHT);
        if (fieldColor.equals(WHITE_STRING))
            setBackground(WHITE);
        else
            setBackground(BLACK);
        setFocusable(false);
        setBorder(BorderFactory.createEmptyBorder());
        addMouseListener(new FieldMouseListener());
        setVisible(true);
    }

    //endregion


    //region Methods

    @Override
    public int getI(){
        return loc.getI();
    }

    @Override
    public int getJ(){
        return loc.getJ();
    }

    @Override
    public void setPiece(IPiece piece) {
        if (piece instanceof ViewPiece)
            setPiece((ViewPiece) piece);
    }


    public void setPiece(ViewPiece piece){
        gotPiece = notNull(piece);
        this.piece = piece;
        if (gotPiece)
            piece.setLocation(loc);
        setIcon(piece);
    }

    @Override
    public void setPiece(PieceAttributes attributes) {
        for (ViewPiece p : DICT_FOR_VIEW_PIECE) {
            if (attributes.equals(p.getAttributes())){
                setPiece(p);
            }
        }
    }

    @Override
    public void clean() {
        setPiece((ViewPiece) null);
    }

    @Override
    public void setBounds(int x, int y, int width, int height){
        super.setBounds(x, y, width, height);

        this.x = x;
        this.y = y;
    }

    public void setBackColorToNormal(){
        if (WHITE_STRING.equals(fieldColor))
            setBackground(WHITE);
        else
            setBackground(BLACK);
    }

    //endregion


    //region Mouse

    @Getter
    @Setter
    public static class FieldMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (theresOnlyOneAi){
                PlayerClick((ViewField) e.getSource());
                if (aiTurn){
                    getViewBoard().setFieldColorsToNormal();
                    SwingUtilities.invokeLater(EDT::startAI);
                }
            }
        }

        private void PlayerClick(ViewField clicked) {
            if (CLICK_COUNTER == 0 && clicked.isGotPiece() && clicked.piece.isWhite() == whiteToPlay && !clicked.piece.getPossibleRange().isEmpty()){
                changeColor(clicked);
                lastClicked = clicked;
                pieceToChange = clicked.piece;
                CLICK_COUNTER++;
            } else if (CLICK_COUNTER == 1 && locationCollectionContains(pieceToChange.getPossibleRange(), clicked.loc)) {
                changeColor(lastClicked);
                moveToClicked(clicked);
                pieceToChange = null;
                lastClicked = null;
                CLICK_COUNTER--;
                switchWhoComes();
            } else if (CLICK_COUNTER == 1) {
                changeColor(lastClicked);
                pieceToChange = null;
                lastClicked = null;
                CLICK_COUNTER--;
            }
        }

        private void moveToClicked(ViewField clicked) {

            ViewPiece hit = null;
            if (clicked.isGotPiece())
                hit = clicked.getPiece();

            Move move = new Move(pieceToChange, clicked.getLoc(), getViewBoard());
            move.setMustLogged(true);
            changeFieldColor(clicked);
            appendToHappenedList(BoardToFen(getViewBoard()));
            Step(move);

            if (notNull(hit))
                putTakenPieceToItsPlace(hit);
        }

        private void changeColor(ViewField field){
            if (field.isGotPiece()){
                ArrayList<ViewField> fields = new ArrayList<>();
                for (Location l : field.piece.getPossibleRange()) {
                    fields.add((ViewField) getViewBoard().getField(l));
                }
                for (ViewField f : fields) {
                    changeFieldColor(f);
                }
            }
        }

        private void changeFieldColor(ViewField field){
            if (WHITE_STRING.equals(field.fieldColor)){
                field.setBackground(field.getBackground() == WHITE ? DARK_WHITE : WHITE);
            }else {
                field.setBackground(field.getBackground() == BLACK ? DARK_BLACK : BLACK);
            }
        }

    }

    //endregion

}
