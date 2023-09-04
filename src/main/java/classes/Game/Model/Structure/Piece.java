package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import static classes.Game.I18N.METHODS.containsLocation;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;

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


    public <F> void STEP(Location from, Location to, Board<F> board) {
        ((Field)(board.getField(to))).setPiece(this);
        ((Field)(board.getField(from))).clean();
    }

    public void updateRange() throws ChessGameException {
        possibleRange = getRange(getType());
    }

    private Set<Location> getRange(PieceType type) throws ChessGameException {
        if (type == null)
            throw new ChessGameException("Type can't be null when choose range to PieceType");
        switch (type){
            case G -> {
                return range(G);
            }
            case H -> {
                return range(H);
            }
            case F -> {
                return range(F);
            }
            case B -> {
                return range(B);
            }
            case V -> {
                return range(V);
            }
            case K -> {
                return range(K);
            }
        }
        return range(G);
    }

    private Set<Location> range(PieceType type){
        Set<Location> range = new HashSet<>();
        Location locationToAdd;
        for (Location l : matrixChooser.get(type)) {
            if (type == G || type == K || type == H){
                addToRange(l, range);
            }else {
                for (int i = 1; i < MAX_WIDTH; i++) {
                    locationToAdd = nTimesLoc(i, l);
                    addToRange(locationToAdd, range);
                }
            }
        }
        return range;
    }

    private void addToRange(Location l, Set<Location> range){
        if (containsLocation(matrixPlusOriginLoc(l))){
            range.add(matrixPlusOriginLoc(l));
        }
    }

    private Location nTimesLoc(int n, Location loc){
        return new Location(n * loc.getI(), n * loc.getJ());
    }

    private Location matrixPlusOriginLoc(Location l){
        return new Location(location.getI() + l.getI(), location.getJ() + l.getJ());
    }

    //endregion

}
