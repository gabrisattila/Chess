package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;

import java.util.Set;

public interface IPiece {

     PieceAttributes getAttributes();

     PieceType getType();

     int getI();

     int getJ();

     boolean isWhite();

     boolean isEmpty();

     Set<Location> getPossibleRange();

     void STEP(Location from, Location to, IBoard board);

     void updateRange() throws ChessGameException;


}
