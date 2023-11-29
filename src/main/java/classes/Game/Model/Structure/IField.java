package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;

public interface IField {

     int getI();

     int getJ();

     Location getLoc();

     IPiece getPiece();

     boolean isGotPiece();

     void setPiece(IPiece piece) ;

     void setPiece(PieceAttributes piece);

     void clean() ;

     default String toSString(){
          return " [" + getI() + ", " + getJ() + "] ";
     }
}
