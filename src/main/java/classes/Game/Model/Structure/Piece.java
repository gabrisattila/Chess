package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import static classes.Game.I18N.METHODS.*;
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
        possibleRange = new HashSet<>();
        watchedRange = new HashSet<>();
    }

    public Piece(PieceAttributes attributes){
        this.attributes = attributes;
        possibleRange = new HashSet<>();
        watchedRange = new HashSet<>();
    }

    public Piece(PieceAttributes attributes, Location location, IBoard board) throws ChessGameException {
        this.attributes = attributes;
        this.location = location;
        if (! (board instanceof Board))
            throw new ChessGameException("Nem megfelelő az átadott tábla típusa");
        this.board = (Board) board;
        possibleRange = new HashSet<>();
        watchedRange = new HashSet<>();
    }

    //endregion


    //region Methods

    public int getI(){
        return location.getI();
    }

    public int getJ(){
        return location.getJ();
    }

    @Override
    public PieceType getType(){
        return attributes.getType();
    }

    @Override
    public boolean isWhite(){
        return WHITE_STRING.equals(attributes.getColor());
    }

    public boolean isEmpty(){
        return attributes == null && location == null;
    }

    public void setEmpty(){
        attributes = null;
        location = null;
        enemyStartRow = 0;
        inDefend = false;
        moveCount = 0;
        inBinding = false;
        VALUE = 0;
        possibleRange = null;
        watchedRange = null;
    }

    @Override
    public void STEP(Location from, Location to, IBoard board) {
        try {
            board.getField(to).setPiece(this);
            board.getField(from).clean();
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
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
            Location locForCalculation = loc.add(location);
            if (type == G || type == K || type == H){
                if (containsLocation(locForCalculation)){
                    if (isTherePiece(locForCalculation)){
                        if (type == G && (pawnHitFilter(locForCalculation) || pawnEmPassantFilter(locForCalculation))){
                            range.add(locForCalculation);
                        } else if (type == K && kingFilter(locForCalculation)) {
                            range.add(locForCalculation);
                        } else if (!isSameColor(locForCalculation)){
                            range.add(locForCalculation);
                        }
                    }else {
                        if (type == K && kingFilter(loc)){
                            range.add(locForCalculation);
                        }else if (type == G){
                            if (posOrWatch){
                                if (pawnFilter(locForCalculation)){
                                    range.add(locForCalculation);
                                }
                            }else {
                                if (pawnHitFilter(locForCalculation) || pawnEmPassantFilter(locForCalculation)){
                                    range.add(locForCalculation);
                                }
                            }
                        } else {
                            range.add(locForCalculation);
                        }
                    }
                }
            }else {
                range.addAll(runTrough(getI(), getJ(), loc.getI(), loc.getJ(), posOrWatch));
            }
        }
        return range;
    }


    private Set<Location> runTrough(int i, int j, int addToI, int addToJ, boolean possibleOrWatched){
        boolean b = true;
        Set<Location> pRange = new HashSet<>();
        Set<Location> wRange = new HashSet<>();
        while(b && containsLocation(i, j)){
            if (!(i == location.getI() && j == location.getJ())){
                if (!isTherePiece(i, j)){
                    pRange.add(new Location(i, j));
                }else {
                    if (!isSameColor(i, j)){
                        pRange.add((new Location(i, j)));
                    }else {
                        ((Piece)board.getPiece(i, j)).inDefend = true;
                    }
                    b = false;
                }
                if (board.getField(i, j).isGotPiece() &&
                        board.getPiece(i, j) != board.getKing(!attributes.isWhite())) {
                    wRange.add(new Location(i, j));
//                    table.getFieldByIJFromBoard(i, j).increaseWatcherCount(white);
                }
            }
            i += addToI;
            j += addToJ;
        }
        return possibleOrWatched ? pRange : wRange;
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
        return (isTherePiece(loc) && !isSameColor(loc) && loc.getJ() != getJ());
    }

    private boolean pawnFilter(Location l){
        return l.getJ() == getJ() && (enemyStartRow == 6 ? ((getI() == 1 && l.getI() == 3) || (l.getI() - getI() < 2)) : ((getI() == 6 && l.getI() == 4) || (Math.abs(l.getI() - getI()) < 2)));
    }

    private boolean isSameColor(Location location){
        return board.getPiece(location).isWhite() == isWhite();
    }

    private boolean isSameColor(int i, int j){
        return board.getPiece(i, j).isWhite() == isWhite();
    }

    private boolean isTherePiece(Location l){
        return board.getField(l).isGotPiece();
    }

    private boolean isTherePiece(int i, int j){
        return board.getField(i, j).isGotPiece();
    }

    private Location matrixPlusOriginLoc(Location l){
        return new Location(location.getI() + l.getI(), location.getJ() + l.getJ());
    }






    //endregion

}
