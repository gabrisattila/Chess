package classes.GUI.FrameParts;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;

import lombok.*;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;
import static classes.GUI.Frame.Window.*;
import static classes.Ai.FenConverter.*;

@Getter
@Setter
public class ViewField extends JButton{

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

    public int getI(){
        return loc.getI();
    }

    public int getJ(){
        return loc.getJ();
    }


    public void setPiece(ViewPiece piece){
        gotPiece = notNull(piece);
        this.piece = piece;
        setIcon(piece);
    }

    public void setPiece(PieceAttributes attributes) {
        for (ViewPiece p : DICT_FOR_VIEW_PIECE) {
            if (attributes.equals(p.getAttributes())){
                setPiece(p);
            }
        }
    }

    public void clean() {
        setPiece((ViewPiece) null);
    }

    //endregion


    //region Mouse

    @Getter
    @Setter
    public static class FieldMouseListener implements MouseListener {


        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                PlayerClick((ViewField) e.getSource());
            } catch (ChessGameException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        private synchronized void PlayerClick(ViewField clicked) throws ChessGameException{

            if (CLICK_COUNTER == 0) {
                CLICK_COUNTER++;
                lastClicked = clicked;
                pieceToChange = clicked.piece;
            } else {
                if (notNull(clicked.piece)){
                    if (helperIfBasedOnColor(clicked.piece)) {
                        CLICK_COUNTER = 0;
                        return;
                    }
                }
                pieceChangeOnViewBoard(pieceToChange, lastClicked, clicked);
                CLICK_COUNTER = 0;
                aiActionAfterMove();
            }
        }

        private void pieceChangeOnViewBoard(ViewPiece piece, ViewField from, ViewField to){
            to.setPiece(piece);
            from.clean();
            whiteToPlay = !whiteToPlay;
        }

        private boolean helperIfBasedOnColor(ViewPiece piece){
            return (piece.isWhite() && whiteToPlay) ||
                    (!piece.isWhite() && !whiteToPlay);
        }

        private void aiActionAfterMove() {
            SwingUtilities.invokeLater(() ->{
                try {
                    getWindow().getAi().aiTurn();
                } catch (ChessGameException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

    }

    //endregion

}
