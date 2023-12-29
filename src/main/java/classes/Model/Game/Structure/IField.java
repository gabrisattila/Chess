package classes.Model.Game.Structure;

import classes.Model.Game.I18N.Location;
import classes.Model.Game.I18N.PieceAttributes;

public interface IField {

     int getI();

     int getJ();

     Location getLoc();

     IPiece getPiece();

     boolean isGotPiece();

     void setPiece(IPiece piece) ;

     void setPiece(PieceAttributes piece);

     void clean() ;

}
