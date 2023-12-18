package classes.Game.Model.Structure;

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

     default int getEnemyStartRow(){
          return getAttributes().getEnemyAndOwnStartRow().getFirst();
     }

     default int getOwnStartRow(){
          return getAttributes().getEnemyAndOwnStartRow().getSecond();
     }

     Set<Location> getPossibleRange();

     Set<Location> getWatchedRange();

     void updateRange() ;

}
