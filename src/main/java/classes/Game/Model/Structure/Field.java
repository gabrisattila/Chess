package classes.Game.Model.Structure;


import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;
import lombok.*;

import java.util.Map;

import static classes.Game.I18N.ChessGameException.throwBadTypeErrorIfNeeded;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.MUTABLE.blackPieceSet;
import static classes.Game.I18N.VARS.MUTABLE.whitePieceSet;

@Getter
@Setter
public class Field implements classes.Game.Model.Structure.IField {

    //region Fields

    private Location Location;

    private String fieldColor;

    private Piece piece;

    private boolean gotPiece;

    private double finalValue;

    private double baseValue;

    private double kingBoost;

    private int blackWatcherCount;

    private int whiteWatcherCount;

    //endregion


    //region Constructor

    public Field()  {
        setPiece((Piece) null);
    }

    public Field(Location Location) {
        this.Location = Location;
        setPiece((Piece) null);
        setValuesToZero();
    }

    public Field(int i, int j) {
        Location = new Location(i, j);
        setPiece((Piece) null);
        setValuesToZero();
    }

    public Field(Location Location, String color) {
        this.Location = Location;
        fieldColor = color;
        setPiece((Piece) null);
        setValuesToZero();
    }

    public Field(Location Location, String color, Piece piece) {
        this.Location = Location;
        fieldColor = color;
        setPiece(piece);
        setValuesToZero();
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


    //Values
    public void setValuesToZero(){
        finalValue = 0;
        baseValue = 0;
        kingBoost = 0;
        whiteWatcherCount = 0;
        blackWatcherCount = 0;
    }

    public void setKingBoostToZero(){
        kingBoost = 0;
    }

    public void increaseWatcherCount(boolean forWhite){
        if (forWhite)
            whiteWatcherCount++;
        else
            blackWatcherCount--;
    }

    public void setWatcherCountToZero(){
        whiteWatcherCount = 0;
        blackWatcherCount = 0;
    }

    public void setFinalValue(boolean forWhite){
        if (forWhite ? whiteWatcherCount == 0 : blackWatcherCount == 0){
            if (isGotPiece())
                finalValue = getPiece().getVALUE();
            else
                finalValue = 0;
        }else {
            finalValue = (Math.abs(getPiece().getVALUE()) + getBaseValue()) * (forWhite ? whiteWatcherCount : blackWatcherCount);
        }
    }

    //endregion

}
