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
public class Piece implements IPiece {

    //region Fields

    protected PieceAttributes attributes;

    private Location location;

    private int enemyStartRow;

    private boolean inDefend;

    private int moveCount = 0;

    private boolean inBinding;

    private double VALUE;

    private Board board;

    private Set<Location> possibleRange;

    private Set<Location> watchedRange;

    private Set<Location> rangeInsteadOfCheck;

    //endregion


    //region Constructor

    public Piece(){
    }

    public Piece(PieceAttributes attributes){
        this.attributes = attributes;
    }

    public Piece(PieceAttributes attributes, Location location, IBoard board){
        this.attributes = attributes;
        this.location = location;
        this.board = (Board) board;
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

    public void STEP(Location from, Location to, IBoard board) {
        board.getField(to).setPiece(this);
        board.getField(from).clean();
    }

    public void updateRange() throws ChessGameException {
        possibleRange = getRange(getType(), true);
        watchedRange = getRange(getType(), false);
//        if (board.isCheckForCurrent()) {
//            possibleRange = checkConstraint(possibleRange);
//        }
    }

    private Set<Location> getRange(PieceType type, boolean posOrWatch) throws ChessGameException {
        if (type == null)
            throw new ChessGameException("Type can't be null when choose range to PieceType");
        switch (type){
            case G -> {
                return range(G, posOrWatch);
            }
            case H -> {
                return range(H, posOrWatch);
            }
            case F -> {
                return range(F, posOrWatch);
            }
            case B -> {
                return range(B, posOrWatch);
            }
            case V -> {
                return range(V, posOrWatch);
            }
            case K -> {
                return range(K, posOrWatch);
            }
        }
        return range(G, posOrWatch);
    }

    private Set<Location> range(PieceType type, boolean posOrWatch){
        Set<Location> range = new HashSet<>();
        for (Location loc : matrixChooser.get(type)) {
            if (type == G || type == K || type == H){
                if (containsLocation(loc)){
                    if (hasPiece(loc)){
                        if (type == G && (pawnHitFilter(loc) || pawnEmPassantFilter(loc))){
                            range.add(loc);
                        } else if (type == K && kingFilter(loc)) {
                            range.add(loc);
                        } else if (!isSameColor(loc)){
                            range.add(loc);
                        }
                    }else {
                        if (type == K && kingFilter(loc)){
                            range.add(loc);
                        }else if (type == G){
                            if (posOrWatch){
                                if (pawnFilter(loc)){
                                    range.add(loc);
                                }
                            }else {
                                if (pawnHitFilter(loc) || pawnEmPassantFilter(loc)){
                                    range.add(loc);
                                }
                            }
                        } else {
                            range.add(loc);
                        }
                    }
                }
            }else {
                Location locToAdd;
                for (int i = 1; i < MAX_WIDTH; i++) {
                    locToAdd = nTimesLoc(i, loc);
                    if (containsLocation(locToAdd)){
                        if (hasPiece(locToAdd)){
                            if (isSameColor(locToAdd)){
                                if (!posOrWatch) {
                                    range.add(locToAdd);
                                    break;
                                }
                            }else {
                                range.add(locToAdd);
                                break;
                            }
                        }else {
                            range.add(locToAdd);
                        }
                    }
                }
            }
        }
        return range;
    }

    private boolean kingFilter(Location l){
//        return !board.enemyKingInNeighbour(l, this) && !thereWouldBeCheck(l);
        return true;
    }

    private boolean pawnEmPassantFilter(Location loc){
//        return board.isEmPassantEnabledToHere_For(loc, this);
        return false;
    }

    private boolean pawnHitFilter(Location loc){
        return (hasPiece(loc) && !isSameColor(loc) && loc.getJ() != getJ());
    }

    private boolean pawnFilter(Location l){
        return l.getJ() == getJ() && (enemyStartRow == 6 ? ((getI() == 1 && l.getI() == 3) || (l.getI() - getI() < 2)) : ((getI() == 6 && l.getI() == 4) || (Math.abs(l.getI() - getI()) < 2)));
    }

    private boolean isSameColor(Location location){
        return board.getPiece(location).isWhite() == isWhite();
    }

    private boolean hasPiece(Location l){
        return board.getField(l).isGotPiece();
    }

    private Location nTimesLoc(int n, Location loc){
        return new Location(n * loc.getI(), n * loc.getJ());
    }

    private Location matrixPlusOriginLoc(Location l){
        return new Location(location.getI() + l.getI(), location.getJ() + l.getJ());
    }






    //endregion

}
