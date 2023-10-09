package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;

@Getter
@Setter
public class Piece implements IPiece {

    //region Fields

    protected PieceAttributes attributes;

    private Location location;

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

    @Override
    public int getI(){
        return location.getI();
    }

    @Override
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

    public int getOwnStartRow(){
        return attributes.getEnemyAndOwnStartRow().getSecond();
    }

    public int getEnemyStartRow(){
        return attributes.getEnemyAndOwnStartRow().getFirst();
    }

    @Override
    public boolean isEmpty(){
        return attributes == null && location == null;
    }

    @Override
    public void setEmpty(){
        attributes = null;
        location = null;
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
        ArrayList<Location> optionsToIterateTrough = type == G && getEnemyStartRow() > getOwnStartRow() ?
                                                    matrixChooser.get(type) :
                                                    (ArrayList<Location>) locationListTimesN(matrixChooser.get(type), -1);
        for (Location loc : optionsToIterateTrough) {
            Location locForCalculation = loc.add(location);
            if (type == G){
                if (pawnCase(locForCalculation, posOrWatch)){
                    range.add(locForCalculation);
                }
            }
            if (type == K || type == H){
                if (containsLocation(locForCalculation)){
                    if (isTherePiece(locForCalculation)){
                        if (type == K && baseKingRange(locForCalculation)) {
                            range.add(locForCalculation);
                        } else if (enemyColor(locForCalculation)){
                            range.add(locForCalculation);
                        }
                    }else {
                        if (type == K && baseKingRange(locForCalculation)){
                            range.add(locForCalculation);
                        }else {
                            range.add(locForCalculation);
                        }
                    }
                }
            } else if (type != G) {
                range.addAll(runTrough(getI(), getJ(), loc.getI(), loc.getJ(), posOrWatch));
            }
        }
        return range;
    }

    private boolean pawnCase(Location l, boolean posOrWatch) {
        if (containsLocation(l)){
            if (l.getJ() == getJ()){
                if (isTherePiece(l))
                    return false;
                if (Math.abs(l.getI() - getI()) == 1){
                    return posOrWatch;
                }
                if (Math.abs(l.getI() - getI()) == 2){
                    if (posOrWatch && getI() == getOwnStartRow()){
                        if (getOwnStartRow() == 1){
                            return !isTherePiece(2, getJ());
                        }else {
                            return !isTherePiece(5, getJ());
                        }
                    }
                }
            } else {
                if (Math.abs(l.getJ() - getJ()) > 1)
                    throw new RuntimeException("Nem eshet bele a gyalog range-be");
                //if (board.getThereWasEmPassant().getFirst()){
                //
                //} else
                if (isTherePiece(l)){
                    return enemyColor(l);
                }
                return !posOrWatch;
            }
        }
        return false;
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
                    if (enemyColor(i, j)){
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
    
    private boolean baseKingRange(Location l){
        return (!isTherePiece(l)) || (isTherePiece(l) && enemyColor(l));
    }

    private boolean enemyColor(Location location){
        return board.getPiece(location).isWhite() != isWhite();
    }

    private boolean enemyColor(int i, int j){
        return board.getPiece(i, j).isWhite() != isWhite();
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
