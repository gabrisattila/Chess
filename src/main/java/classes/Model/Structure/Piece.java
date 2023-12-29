package classes.Model.Structure;

import classes.Model.I18N.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Piece implements IPiece {

    //region Fields

    protected PieceAttributes attributes;

    private classes.Model.I18N.Location Location;

    private boolean inDefend;

    private IPiece bounderPiece;

    private Board board;

    private Set<Location> possibleRange;

    private Set<Location> watchedRange;

    private Set<Move> legalMoves;

    //endregion


    //region Constructor

    public Piece(){
        possibleRange = new HashSet<>();
        watchedRange = new HashSet<>();
        bounderPiece = null;
    }

    //endregion


    //region Methods

    //region Get&Set

    @Override
    public int getI(){
        return Location.getI();
    }

    @Override
    public int getJ(){
        return Location.getJ();
    }

    @Override
    public PieceType getType(){
        return attributes.getType();
    }

    @Override
    public boolean isWhite(){
        return VARS.FINALS.WHITE_STRING.equals(attributes.getColor());
    }

    public int getOwnStartRow(){
        return attributes.getEnemyAndOwnStartRow().getSecond();
    }

    public int getEnemyStartRow(){
        return attributes.getEnemyAndOwnStartRow().getFirst();
    }

    public boolean isInBinding(){
        return METHODS.notNull(bounderPiece);
    }

    @Override
    public boolean isEmpty(){
        return attributes == null && Location == null;
    }

    public void setPossibleRange(Set<Location> possibleRange){
        this.possibleRange = possibleRange;
    }

    public void setPossibleRange(Location l){
        if (METHODS.isNull(possibleRange))
            possibleRange = new HashSet<>();
        possibleRange.add(l);
    }

    public Set<Location> getAttackRange(){
        if (getType() == PieceType.P){
            return watchedRange;
        }
        return possibleRange;
    }

    public Location getEmPassantLocation(){
        return METHODS.notNull(attributes.getEmPassantLoc()) ? attributes.getEmPassantLoc() : null;
    }

    @Override
    public void setEmpty(){
        attributes = null;
        Location = null;
        inDefend = false;
        bounderPiece = null;
        possibleRange = null;
        watchedRange = null;
    }

    //endregion

    //region Range

    //region Main of range

    @Override
    public void updateRange()  {
        possibleRange = getRange(getType(), true);
        watchedRange = getRange(getType(), false);
    }

    protected Set<Location> getRange(PieceType type, boolean posOrWatch)  {
        switch (type){
            case P -> {
                return range(PieceType.P, posOrWatch);
            }
            case N -> {
                return range(PieceType.N, posOrWatch);
            }
            case B -> {
                return range(PieceType.B, posOrWatch);
            }
            case R -> {
                return range(PieceType.R, posOrWatch);
            }
            case Q -> {
                return range(PieceType.Q, posOrWatch);
            }
        }
        return range(PieceType.P, posOrWatch);
    }

    protected Set<Location> range(PieceType type, boolean posOrWatch)  {
        Set<Location> range = new HashSet<>();
        Set<Location> optionsToIterateTrough = (type == PieceType.P && getEnemyStartRow() > getOwnStartRow()) ?
                VARS.FINALS.matrixChooser.get(type) :
                METHODS.locationSetTimesN(VARS.FINALS.matrixChooser.get(type), - 1);
        for (Location loc : optionsToIterateTrough) {
            Location locForCalculation = loc.add(Location);
            if (type == PieceType.P){
                if (pawnCase(locForCalculation, posOrWatch)){
                    range.add(locForCalculation);
                }
            } else if (type == PieceType.N){
                if (METHODS.containsLocation(locForCalculation)){
                    if (isTherePiece(locForCalculation)){
                        if (posOrWatch){
                            if (enemyColor(locForCalculation))
                                range.add(locForCalculation);
                        }else {
                            range.add(locForCalculation);
                        }
                    }else {
                        range.add(locForCalculation);
                    }
                }
            } else if (type != PieceType.K){
                range.addAll(runTrough(getI(), getJ(), loc.getI(), loc.getJ(), posOrWatch));
            }
        }
        return range;
    }

    //endregion

    //region Piece cases separately

    protected boolean pawnCase(Location l, boolean posOrWatch)  {
        if (METHODS.containsLocation(l)){
            if (l.getJ() == getJ()){
                if (isTherePiece(l))
                    return false;
                if (Math.abs(l.getI() - getI()) == 1){
                    return posOrWatch;
                }
                if (Math.abs(l.getI() - getI()) == 2){
                    if (posOrWatch && getI() == getOwnStartRow()){
                        if (getOwnStartRow() == 1){
                            return theresNoPiece(2, getJ());
                        }else {
                            return theresNoPiece(5, getJ());
                        }
                    }
                }
            } else {
                if (Math.abs(l.getJ() - getJ()) > 1) {
                    String msg = "A következő helyen lévő gyalog (" + getLocation().toString() + ") egy olyan helyre akar " +
                            "ütni, ami nem esik bele a saját range-be. (" + l + ")";
                    throw new ChessGameException(msg);
                }
                if (posOrWatch){
                    if (isTherePiece(l)){
                        if (!enemyColor(l))
                            ((Piece) getBoard().getPiece(l)).inDefend = true;
                        return enemyColor(l);
                    }else {
                        return METHODS.notNull(attributes.getPossibleEmPassant()) && l.equals(getEmPassantLocation());
                    }
                }else {
                    return true;
                }
            }
        }
        return false;
    }

    protected Set<Location> runTrough(int i, int j, int addToI, int addToJ, boolean possibleOrWatched)  {
        boolean b = true;
        Set<Location> pRange = new HashSet<>();
        Set<Location> wRange = new HashSet<>();
        while(b && METHODS.containsLocation(i, j)){
            if (!(i == Location.getI() && j == Location.getJ())){
                if (theresNoPiece(i, j)){
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

    //endregion

    //region Helpers for ranges

    protected boolean enemyColor(Location Location)  {
        return board.getPiece(Location).isWhite() != isWhite();
    }

    protected boolean enemyColor(int i, int j)  {
        return board.getPiece(i, j).isWhite() != isWhite();
    }

    public boolean isTherePiece(Location l){
        return board.getField(l).isGotPiece();
    }

    public boolean theresNoPiece(int i, int j){
        return !board.getField(i, j).isGotPiece();
    }

    //endregion

    //endregion

    //endregion
}
