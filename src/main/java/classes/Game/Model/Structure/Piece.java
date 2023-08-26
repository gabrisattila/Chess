package classes.Game.Model.Structure;

import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import static classes.Game.I18N.VARS.FINALS.WHITE_STRING;

@Getter
@Setter
public class Piece {

    //region Fields

    protected PieceAttributes attributes;

    private Location location;

    private int enemyStartRow;

    private boolean inDefend;

    private int moveCount = 0;

    private boolean inBinding;

    private double VALUE;

    private Set<Location> possibleRange;

    private Set<Location> watchedRange;

    private Set<Location> rangeInsteadOfCheck;

    //endregion


    //region Constructor

    public Piece(){}

    public Piece(PieceAttributes attributes){
        this.attributes = attributes;
    }

    public Piece(PieceAttributes attributes, Location location){
        this.attributes = attributes;
        this.location = location;
    }

    //endregion


    //region Methods

    public int getI(){
        return location.getI();
    }

    public int getJ(){
        return location.getJ();
    }

    public PieceType getType(){
        return attributes.getType();
    }

    public boolean isWhite(){
        return WHITE_STRING.equals(attributes.getColor());
    }

    //endregion

}
