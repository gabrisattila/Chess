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

     Location getLocation();

     void setLocation(Location location);

     boolean isWhite();

     boolean isEmpty();

     void setEmpty();

     Set<Location> getPossibleRange();

     void STEP(Location from, Location to, IBoard board);

     void updateRange() throws ChessGameException;

     default boolean EQUALS(IPiece ownPiece) {
          return isWhite() == ownPiece.isWhite() && getAttributes().equals(ownPiece.getAttributes());
     }
}
