package classes.Model.Structure;


import classes.Model.I18N.*;
import lombok.Getter;
import lombok.Setter;

import static classes.Model.I18N.VARS.MUTABLE.*;
import static classes.Model.I18N.VARS.FINALS.*;

@Getter
@Setter
public class Field implements IField {

    //region Fields

    private classes.Model.I18N.Location Location;

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
        gotPiece = METHODS.notNull(piece);

        if (gotPiece && piece instanceof Piece) {
            this.piece = (Piece) piece;
            piece.setLocation(Location);
        }else {
            this.piece = null;
        }

        if (piece != null){
            ChessGameException.throwBadTypeErrorIfNeeded(
                    new Object[]{piece, Piece.class.getName()}
            );
        }
    }

    @Override
    public void setPiece(PieceAttributes attributes){
        if (METHODS.isNull(attributes)) {
            piece = null;
            gotPiece = false;
        }
        piece = attributes.isWhite() ? PieceSet.firstEmptyWhite() : PieceSet.firstEmptyBlack();
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
