package classes.Game.Model.Structure;


import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;
import lombok.*;

import static classes.Game.I18N.METHODS.*;

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

    public Field() throws ChessGameException {
        setPiece((Piece) null);
    }

    public Field(Location location) {
        this.location = location;
        try {
            setPiece((Piece) null);
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
        setValuesToZero();
    }

    public Field(int i, int j) {
        location = new Location(i, j);
        try {
            setPiece((Piece) null);
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
        setValuesToZero();
    }

    public Field(Location location, String color) {
        this.location = location;
        fieldColor = color;
        try {
            setPiece((Piece) null);
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
        setValuesToZero();
    }

    public Field(Location location, String color, Piece piece) {
        this.location = location;
        fieldColor = color;
        try {
            setPiece(piece);
        } catch (ChessGameException e) {
            throw new RuntimeException(e.getMsg());
        }
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

    public void setPiece(IPiece piece) throws ChessGameException {
        gotPiece = notNull(piece);

        if (gotPiece && piece instanceof Piece) {
            this.piece = (Piece) piece;
            ((Piece) piece).setLocation(location);
        }else {
            this.piece = null;
        }

        if (piece != null && ! (piece instanceof Piece))
            throw new ChessGameException("Nem megfelelő típus");
    }

    public void setPiece(PieceAttributes attributes){
        if (isNull(attributes)) {
            piece = null;
            gotPiece = false;
        }
        piece = attributes.isWhite() ? firstEmptyWhite() : firstEmptyBlack();
        piece.setAttributes(attributes);
        gotPiece = true;
        piece.setLocation(location);
    }

    public void clean() throws ChessGameException {
        gotPiece = false;
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
