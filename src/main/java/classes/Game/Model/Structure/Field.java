package classes.Game.Model.Structure;


import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;
import lombok.Getter;
import lombok.Setter;

import static classes.Game.I18N.METHODS.containsLocation;
import static classes.Game.I18N.METHODS.notNull;

@Getter
@Setter
public class Field implements IField {

    //region Fields

    private Location location;

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

    public Field(){
        setPiece((Piece) null);
    }

    public Field(Location location){
        this.location = location;
        setPiece((Piece) null);
        setValuesToZero();
    }

    public Field(int i, int j){
        location = new Location(i, j);
        setPiece((Piece) null);
        setValuesToZero();
    }

    public Field(Location location, String color){
        this.location = location;
        fieldColor = color;
        setPiece((Piece) null);
        setValuesToZero();
    }

    public Field(Location location, String color, Piece piece){
        this.location = location;
        fieldColor = color;
        setPiece(piece);
        setValuesToZero();
    }

    //endregion


    //region Methods

    // GetBy

    public int getI(){
        return location.getI();
    }

    public int getJ(){
        return location.getJ();
    }

    //Pieces

    public void setPiece(IPiece piece){
        this.piece = (Piece) piece;
        gotPiece = notNull(piece);
        if (notNull(piece))
            piece.setLocation(location);
    }

    public void setPiece(PieceAttributes attributes){
        piece = new Piece(attributes);
        gotPiece = true;
        if (notNull(attributes))
            piece.setLocation(location);
    }

    public void clean(){
        setPiece((Piece) null);
    }

    public boolean isEnemyKingInNeighbour(boolean white, Board board) {

        for (int i = location.getI() - 1; i < location.getI() + 2; i++) {
            for (int j = location.getJ() - 1; j < location.getJ() + 2; j++) {
                if (containsLocation(i, j) && notNull(board.getField(i, j).getPiece())
                        && board.getField(i, j).getPiece().getType() == PieceType.K){
                    if (board.getField(i, j).getPiece().isWhite() != white){
                        return board.getField(i, j).getPiece().isWhite() != white;
                    }
                }
            }
        }
        return false;
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

    //endregion

}
