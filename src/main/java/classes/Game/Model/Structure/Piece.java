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

    private Set<Move> pseudoLegals;

    private Set<Move> legalMoves;

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

    //region Get&Set

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

    public Set<Location> getAttackRange(){
        return getType() == G ? watchedRange : possibleRange;
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

    public void setPseudoLegals(String newOrNulling){
        if ("new".equals(newOrNulling) || "New".equals(newOrNulling) || "NEW".equals(newOrNulling))
            pseudoLegals = new HashSet<>();
        else if ("null".equals(newOrNulling) || "Null".equals(newOrNulling) || "NULL".equals(newOrNulling))
            pseudoLegals = null;
        else
            throw new RuntimeException("Vagy a paraméter típusa, vagy annak értéke rossz.");
    }

    public void setPseudoLegals(Move moveToAdd){
        if (isNull(pseudoLegals))
            setPseudoLegals("new");
        pseudoLegals.add(moveToAdd);
    }

    public void setLegals(String newOrNulling){
        if ("new".equals(newOrNulling) || "New".equals(newOrNulling) || "NEW".equals(newOrNulling))
            pseudoLegals = new HashSet<>();
        else if ("null".equals(newOrNulling) || "Null".equals(newOrNulling) || "NULL".equals(newOrNulling))
            pseudoLegals = null;
        else
            throw new RuntimeException("Vagy a paraméter típusa, vagy annak értéke rossz.");
    }

    public void setLegals(Move moveToAdd){
        if (isNull(pseudoLegals))
            setPseudoLegals("new");
        pseudoLegals.add(moveToAdd);
    }

    //endregion

    //region Moving

    @Override
    public void STEP(Location from, Location to, IBoard board) {
        try {
            board.getField(to).setPiece(this);
            board.getField(from).clean();
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    //region Range

    //region Main of range

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
        }
        return range(G, posOrWatch);
    }

    private Set<Location> range(PieceType type, boolean posOrWatch) throws ChessGameException {
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
            } else if (type == H){
                if (containsLocation(locForCalculation)){
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
            } else if (type != K){
                range.addAll(runTrough(getI(), getJ(), loc.getI(), loc.getJ(), posOrWatch));
            }
        }
        return range;
    }

    //endregion

    //region Piece cases separately

    private boolean pawnCase(Location l, boolean posOrWatch) throws ChessGameException {
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

    private Set<Location> runTrough(int i, int j, int addToI, int addToJ, boolean possibleOrWatched) throws ChessGameException {
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

    private boolean baseKingRange(Location l) throws ChessGameException {
        return (!isTherePiece(l)) || (isTherePiece(l) && enemyColor(l));
    }

    private Set<Location> kingRange(){
        Set<Location> range = new HashSet<>();





        //TODO Az egész updateRange-t erre kihegyezni:
        // Az eddigiek alapján legyen megszerkesztve a rangeUpdater hiszen ehhez, hogy meglegyen mindennek meg kell lennie.


        return range;
    }

    //endregion

    //region Helpers for ranges

    private boolean enemyColor(Location location) throws ChessGameException {
        return board.getPiece(location).isWhite() != isWhite();
    }

    private boolean enemyColor(int i, int j) throws ChessGameException {
        return board.getPiece(i, j).isWhite() != isWhite();
    }

    public boolean isTherePiece(Location l){
        return board.getField(l).isGotPiece();
    }

    public boolean isTherePiece(int i, int j){
        return board.getField(i, j).isGotPiece();
    }

    private Location matrixPlusOriginLoc(Location l){
        return new Location(location.getI() + l.getI(), location.getJ() + l.getJ());
    }


    //endregion

    //endregion

    //endregion

    //endregion
}
