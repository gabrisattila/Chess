package classes.GUI.FrameParts;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;

import classes.Game.I18N.PieceType;
import classes.Game.Model.Structure.IPiece;
import lombok.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.Move.*;

@Getter
@Setter
public class ViewField extends JButton implements classes.Game.Model.Structure.IField {

    //region Fields

    private Location loc;

    private String fieldColor;

    private ViewPiece piece;

    private boolean gotPiece;

    private ArrayList<Location> options;

    //endregion


    //region Constructor

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

    //endregion


    //region Mouse

    @Getter
    @Setter
    public static class FieldMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                if (theresOnlyOneAi){
                    PlayerClick((ViewField) e.getSource());
                    if (aiTurn) {
                        startAI();
                    }
                }
            } catch (ChessGameException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            MouseEnter((ViewField) e.getSource());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            MouseExit((ViewField) e.getSource());
        }

        private void PlayerClick(ViewField clicked) throws ChessGameException, InterruptedException {

            if (CLICK_COUNTER == 0 && clicked.isGotPiece() && clicked.piece.isWhite() == whiteToPlay){
                changeColor(clicked);
                CLICK_COUNTER++;
                lastClicked = clicked;
                pieceToChange = clicked.piece;
            } else if (CLICK_COUNTER == 1 && lastClicked.isGotPiece() && lastClicked.piece.isWhite() == whiteToPlay &&
                    lastClicked.piece.inRange(clicked)) {
                moveToClicked(clicked);
                CLICK_COUNTER = 0;
                switchWhoComes();
            } else if (CLICK_COUNTER == 0 && !clicked.isGotPiece()) {
                changeColor(clicked);
            } else if (CLICK_COUNTER == 0 && clicked.piece.isWhite() != whiteToPlay) {
                return;
            } else if (CLICK_COUNTER == 1 && !lastClicked.piece.inRange(clicked)) {
                CLICK_COUNTER = 0;
                lastClicked = null;
                return;
            }

        }

        private void MouseEnter(ViewField source) {
            changeFieldColor(source);
        }

        private void MouseExit(ViewField source) {
            changeFieldColor(source);
        }

        private void moveToClicked(ViewField clicked) throws ChessGameException {

            pawnGotInCase(clicked);

            MOVE(getViewBoard(), pieceToChange, clicked.loc, false);

            changeColor(clicked);
        }

        private void changeColor(ViewField field){
            if (field.isGotPiece()){
                ArrayList<ViewField> fields = new ArrayList<>();
                for (Location l : field.piece.getPossibleRange()) {
                    try {
                        fields.add((ViewField) getViewBoard().getField(l));
                    } catch (ChessGameException e) {
                        throw new RuntimeException(e);
                    }
                }
                for (ViewField f : fields) {
                    changeFieldColor(f);
                }
            }
        }

        private void changeFieldColorsOfARange(ArrayList<ViewField> fields){
            for (ViewField f : fields) {
                changeFieldColor(f);
            }
        }

        private void changeFieldColor(ViewField field){
            if (WHITE_STRING.equals(field.fieldColor)){
                field.setBackground(field.getBackground() == WHITE ? DARK_WHITE : WHITE);
            }else {
                field.setBackground(field.getBackground() == BLACK ? DARK_BLACK : BLACK);
            }
        }

        private boolean helperIfBasedOnColor(ViewPiece piece){
            return (piece.isWhite() && whiteToPlay) ||
                    (!piece.isWhite() && !whiteToPlay);
        }

        private void pawnGotInCase(ViewField clicked) throws ChessGameException {

            if (pieceToChange.getType() == G && (clicked.getI() == 0 || clicked.getI() == 7)){
                PieceType newType = pawnGotInCaseView();
                pawnGotInCaseBackground(pieceToChange, lastClicked, clicked, getViewBoard(), newType);
            }
        }

        private PieceType pawnGotInCaseView(){
            int result = JOptionPane.showOptionDialog(null, "Válassz melyik figurát szeretnéd.", "Lehetőségek.",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    pieceToChange.isWhite() ?
                            WhitePieceChoiceInsteadOfPawnGotIn.toArray() :
                            BlackPieceChoiceInsteadOfPawnGotIn.toArray(),
                    null);
            PieceType newType;
            switch (result){
                case 0 -> newType = H;
                case 1 -> newType = F;
                case 2 -> newType = B;
                case 3 -> newType = V;
                default -> throw new IllegalStateException("Unexpected value: " + result);
            }
            pieceToChange.getAttributes().setType(newType);

            pieceToChange.setImage(
                    pieceToChange.isWhite() ?
                            WhitePieceChoiceInsteadOfPawnGotIn.get(result).getImage() :
                            BlackPieceChoiceInsteadOfPawnGotIn.get(result).getImage()
            );
            return newType;
        }


    }

    //endregion

}
