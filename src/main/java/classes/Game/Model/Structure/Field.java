package classes.Game.Model.Structure;


import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import lombok.Getter;
import lombok.Setter;

import static classes.Game.I18N.ChessGameException.throwBadTypeErrorIfNeeded;
import static classes.Game.I18N.METHODS.isNull;
import static classes.Game.I18N.METHODS.notNull;
import static classes.Game.I18N.PieceSet.firstEmptyBlack;
import static classes.Game.I18N.PieceSet.firstEmptyWhite;

@Getter
@Setter
public class Field implements IField {

    //region Fields

    private Location Location;

    private String fieldColor;

    private Piece piece;

    private boolean gotPiece;

    //endregion


    //region Constructor

    public Field(int i, int j) {
        Location = new Location(i, j);
        setPiece((Piece) null);
    }

    public Field(Location Location, String color) {
        this.Location = Location;
        fieldColor = color;
        setPiece((Piece) null);
    }

    //endregion


    //region Methods

    // GetBy

    @Override
    public int getI(){
        return Location.getI();
    }

    @Override
    public int getJ(){
        return Location.getJ();
    }

    @Override
    public Location getLoc(){
        return getLocation();
    }

    //Pieces

    @Override
    public void setPiece(IPiece piece)  {
        gotPiece = notNull(piece);

        if (gotPiece && piece instanceof Piece) {
            this.piece = (Piece) piece;
            piece.setLocation(Location);
        }else {
            this.piece = null;
        }

        if (piece != null){
            throwBadTypeErrorIfNeeded(
                    new Object[]{piece, Piece.class.getName()}
            );
        }
    }

    @Override
    public void setPiece(PieceAttributes attributes){
        if (isNull(attributes)) {
            piece = null;
            gotPiece = false;
        }
        piece = attributes.isWhite() ? firstEmptyWhite() : firstEmptyBlack();
        piece.setAttributes(attributes);
        gotPiece = true;
        piece.setLocation(Location);
    }

    @Override
    public void clean()  {
        gotPiece = false;
        setPiece((Piece) null);
    }

}
