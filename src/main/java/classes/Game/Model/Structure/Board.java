package classes.Game.Model.Structure;


import classes.Game.I18N.Location;
import classes.Game.I18N.Pair;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static classes.Game.I18N.ChessGameException.throwBadTypeErrorIfNeeded;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.matrixChooser;
import static classes.Game.I18N.VARS.MUTABLE.*;


/**
 * Singleton.
 */
@Getter
@Setter
public class Board implements IBoard {

    //region Fields

    private int X;

    private int Y;

    private static Board board;

    private ArrayList<ArrayList<IField>> fields;

    private ArrayList<IPiece> pieces;

    private Piece whiteKing;

    private Piece blackKing;

    private Pair<IPiece, IPiece> checkers;

    //endregion


    //region Constructor

    private Board(int x, int y) {
        X = x;
        Y = y;
        fields = new ArrayList<>();
        boardSetUp(this, fields);
        pieces = new ArrayList<>();
        whiteKing = new Piece();
        blackKing = new Piece();
    }

    public static Board getBoard() {
        if(board == null){
            board = new Board(MAX_WIDTH, MAX_HEIGHT);
            return board;
        }
        return board;
    }

    //endregion


    //region Methods

    //region Get

    @Override
    public IField getField(int i, int j){
        return getFields().get(i).get(j);
    }

    @Override
    public IField getField(Location Location){
        return getField(Location.getI(), Location.getJ());
    }

    @Override
    public IField getField(IPiece piece){
        return getField(piece.getI(), piece.getJ());
    }

    @Override
    public IPiece getPiece(int i, int j) {
        for (IPiece p : pieces) {
            if (p.getI() == i && p.getJ() == j)
                return p;
        }
        return null;
    }

    @Override
    public IPiece getPiece(Location Location) {
        return getPiece(Location.getI(), Location.getJ());
    }

    @Override
    public IPiece getPiece(IField field) {
        return getPiece(field.getI(), field.getJ());
    }

    public Set<IPiece> getPieces(boolean forWhite){
        Set<IPiece> ps = new HashSet<>();
        for (IPiece p : pieces) {
            if (p.isWhite() == forWhite)
                ps.add(p);
        }
        return ps;
    }

    public Piece getKing(boolean whiteNeeded){
        return whiteNeeded ? whiteKing : blackKing;
    }

    public Location getKingsPlace(boolean forWhite){
        return getKing(forWhite).getLocation();
    }

    //endregion

    //region Main functions

    @Override
    public void cleanBoard() {

            for (ArrayList<IField> row : this.fields) {
                for (IField f : row) {
                    if (!(f instanceof Field)) {
                        throwBadTypeErrorIfNeeded(new Object[]{
                                f, Field.class.getName(),
                                " ezért nem tudom elvégezni a clean műveletet.\n"
                        });
                    }
                    f.clean();
                }
            }
            pieces.clear();
            whitePieceSet.clean();
            blackPieceSet.clean();
    }

    @Override
    public void rangeUpdater() {
        clearRangesAndStuffBeforeUpdate();
        pseudos();
        if (hasTwoKings()) {
            constrainPseudos();
            inspectCheck(!whiteToPlay);
            if (isNull(checkers)) {
                kingFreeRange(!whiteToPlay);
                kingFreeRange(whiteToPlay);
            } else {
                kingFreeRange(!whiteToPlay);
                kingRangeInsteadOfCheck(whiteToPlay);
            }
        }
    }

    //endregion

    //region Update Range Main

    private void clearRangesAndStuffBeforeUpdate(){
        checkers = null;
        for (IPiece p : pieces) {
            ((Piece) p).setPossibleRange(new HashSet<>());
            ((Piece) p).setWatchedRange(new HashSet<>());
            ((Piece) p).setLegalMoves(new HashSet<>());
            ((Piece) p).setBounderPiece(null);
            ((Piece) p).setInDefend(false);
        }
        getFields().stream()
                .flatMap(ArrayList::stream)
                .forEach(f -> ((Field) f).setValuesToZero());
    }

    public void pseudos()  {
        for (IPiece p : pieces) {
            if (p.getType() != K) {
                p.updateRange();
            }
        }
    }

    public void constrainPseudos()  {
        constrainCalculatedPseudos(!whiteToPlay);
        constrainCalculatedPseudos(whiteToPlay);
    }

    /**
     * @param enemy this means who would be the player, who gives check
     * Alapvetés szerint pedig megnézi sakkban vagyok-e, ha igen, a checkers többé nem null
     */
    private void inspectCheck(boolean enemy) {
        if (locationCollectionContains(getAttackRangeWithoutKing(enemy), getKingsPlace(!enemy))){
            findCheckers(enemy);
        }
    }

    private void kingFreeRange(boolean my)  {
        kingSimpleMoves(my);
        kingCastle(my);
    }

    private void kingRangeInsteadOfCheck(boolean my)  {
        kingStepOutFromCheck(!my);
        constrainMyPiecesRangeInsteadOfCheck(my);
    }

    //endregion

    //region Basic constrains, bind setting

    private void constrainCalculatedPseudos(boolean my) {
        Set<IPiece> piecesInBinding = findAndSetBindingFor(my);
        constrainBoundPiecesRanges(piecesInBinding);
    }

    private Set<IPiece> findAndSetBindingFor(boolean my)  {
        Set<IPiece> boundPieces = new HashSet<>();
        for (IPiece enemyTiszt : getTisztek(!my)) {
            if (isThereAnyPieceInPossibleBindingBy(enemyTiszt)){
                IPiece boundPiece = boundPieceBy(enemyTiszt);
                if (notNull(boundPiece) && boundPiece.isWhite() != enemyTiszt.isWhite()) {
                    boundPieces.add(boundPiece);
                    ((Piece) boundPiece).setBounderPiece(enemyTiszt);
                }
            }
        }
        return boundPieces;
    }

    private void constrainBoundPiecesRanges(Set<IPiece> piecesInBinding) {
        for (IPiece p : piecesInBinding) {
            ((Piece) p).setPossibleRange(boundPieceOrKingRangeCalc(p, ((Piece) p).getBounderPiece(), null));
        }
    }

    private boolean isThereAnyPieceInPossibleBindingBy(IPiece enemyTiszt){
        return isOnTheSameLineWithMyKing(enemyTiszt) && tisztAttackRangeContainsOneOfMyPiece(enemyTiszt);
    }

    private IPiece boundPieceBy(IPiece enemyTiszt) {
        ArrayList<Location> lineToMyKing = lineFromAPieceToAnother(getKing(!enemyTiszt.isWhite()), enemyTiszt);

        int pieceCountOnLine = 0;
        IPiece myBoundPiece = null;
        for (Location l : lineToMyKing) {
            if (notNull(getPiece(l))){
                pieceCountOnLine++;
                myBoundPiece = getPiece(l);
            }
        }

        return pieceCountOnLine == 1 ? myBoundPiece : null;
    }

    private boolean isOnTheSameLineWithMyKing(IPiece enemyTiszt) {
        IPiece myKing = getKing(!enemyTiszt.isWhite());
        switch (enemyTiszt.getType()){
            case Q -> {
                return myKing.getI() == enemyTiszt.getI() || myKing.getJ() == enemyTiszt.getJ() ||
                        Math.abs(myKing.getI() - enemyTiszt.getI()) == Math.abs(myKing.getJ() - enemyTiszt.getJ());
            }
            case R -> {
                return myKing.getI() == enemyTiszt.getI() || myKing.getJ() == enemyTiszt.getJ();
            }
            case B -> {
                return Math.abs(myKing.getI() - enemyTiszt.getI()) == Math.abs(myKing.getJ() - enemyTiszt.getJ());
            }
            default -> {
                return false;
            }
        }
    }

    private boolean tisztAttackRangeContainsOneOfMyPiece(IPiece enemyTiszt){
        return enemyTiszt.getPossibleRange().stream().anyMatch(l -> notNull(getPiece(l)) && getPiece(l).isWhite() != enemyTiszt.isWhite() && getPiece(l).getType() != K);
    }

    //endregion

    //region King Free Move

    private void kingSimpleMoves(boolean my) {
        getKing(my).setPossibleRange(new HashSet<>());
        for (Location l : matrixChooser.get(K)) {
            Location possiblePlaceOfMyKing = l.add(getKingsPlace(my));
            if (
                    containsLocation(possiblePlaceOfMyKing) &&
                            (!getField(possiblePlaceOfMyKing).isGotPiece() || getPiece(possiblePlaceOfMyKing).isWhite() != my) &&
                            enemyKingNotInNeighbour(possiblePlaceOfMyKing, my) &&
                            !locationCollectionContains(getAttackRangeWithoutKing(!my), possiblePlaceOfMyKing)
            ){
                getKing(my).setPossibleRange(possiblePlaceOfMyKing);
            } else if (containsLocation(l)) {
                getKing(my).getWatchedRange().add(l);
            }
        }
    }

    /**
     * @param forWhite in that case forWhite simbolize my color (Me is who count the step)
     */
    private void kingCastle(boolean forWhite) {
        if (MAX_WIDTH == 8 && MAX_HEIGHT == 8 &&
                (   (forWhite && (whiteBigCastleEnabled || whiteSmallCastleEnabled)) ||
                        (!forWhite && (blackBigCastleEnabled || blackSmallCastleEnabled)) ) &&
                !locationCollectionContains(getAttackRangeWithoutKing(!forWhite), getKingsPlace(forWhite))
        ){

            int bigCastlePointPlus = (whiteDown ? -2 : 2);
            int bigCastleRoadPlus = (whiteDown ? -1 : 1);
            Location bigCastlePointLocation = new Location(
                    getKingsPlace(forWhite).getI(),
                    getKingsPlace(forWhite).getJ() + bigCastlePointPlus);
            Location bigCastleRoadLocation = new Location(
                    getKingsPlace(forWhite).getI(),
                    getKingsPlace(forWhite).getJ() + bigCastleRoadPlus);

            int smallCastlePointPlus = (whiteDown ? 2 : -2);
            int smallCastleRoadPlus = (whiteDown ? 1 : -1);
            Location smallCastlePointLocation = new Location(
                    getKingsPlace(forWhite).getI(),
                    getKingsPlace(forWhite).getJ() + smallCastlePointPlus);
            Location smallCastleRoadLocation = new Location(
                    getKingsPlace(forWhite).getI(),
                    getKingsPlace(forWhite).getJ() + smallCastleRoadPlus);

            Location bigCastlePlusRoad = getTheMiddleLocation(bigCastlePointLocation, new Location(
                    bigCastlePointLocation.getI(), bigCastlePointLocation.getJ() == 2 ? 1 : 6
            ));
            boolean theresNoPieceOnBigCastlePlusRoad = notNull(bigCastlePlusRoad) && isNull(getPiece(bigCastlePlusRoad));

            castleHelper(
                    forWhite,
                    bigCastlePointLocation,
                    bigCastleRoadLocation,
                    smallCastlePointLocation,
                    smallCastleRoadLocation,
                    (getKing(forWhite).isWhite() ? whiteBigCastleEnabled : blackBigCastleEnabled)
                            && theresNoPieceOnBigCastlePlusRoad,
                    getKing(forWhite).isWhite() ? whiteSmallCastleEnabled : blackSmallCastleEnabled
            );

        }
    }

    private void castleHelper(boolean forWhite,
                              Location bigCastlePointLocation,
                              Location bigCastleRoadLocation,
                              Location smallCastlePointLocation,
                              Location smallCastleRoadLocation,
                              boolean bigCastleEnabled,
                              boolean smallCastleEnabled) {
        if (castleHelpersIf(forWhite, bigCastlePointLocation, bigCastleRoadLocation, bigCastleEnabled)) {
            getKing(forWhite).setPossibleRange(bigCastlePointLocation);
        }

        if (castleHelpersIf(forWhite, smallCastlePointLocation, smallCastleRoadLocation, smallCastleEnabled)) {
            getKing(forWhite).setPossibleRange(smallCastlePointLocation);
        }
    }

    private boolean castleHelpersIf(boolean forWhite, Location castlePoint, Location castleRoad, boolean castleEnabled)  {
        return castleEnabled &&
                !locationCollectionContains(getAttackRangeWithoutKing(!forWhite), castleRoad) &&
                !locationCollectionContains(getAttackRangeWithoutKing(!forWhite), castlePoint) &&
                enemyKingNotInNeighbour(castleRoad, forWhite) &&
                enemyKingNotInNeighbour(castlePoint, forWhite) &&
                isNull(getPiece(castlePoint)) && isNull(getPiece(castleRoad));
    }

    //endregion

    //region Check

    private void findCheckers(boolean enemy){
        checkers = new Pair<>();
        for (IPiece enemyP : getPieces(enemy)) {
            if (locationCollectionContains(enemyP.getPossibleRange(), getKingsPlace(!enemy))){
                if (isNull(checkers.getFirst())) {
                    checkers.setFirst(enemyP);
                } else {
                    checkers.setSecond(enemyP);
                    break;
                }
            }
        }
    }

    private void kingStepOutFromCheck(boolean enemy)  {
        if (checkers.getFirst().getType() == N || checkers.getFirst().getType() == P){
            for (Location l : matrixChooser.get(K)) {
                l = getKingsPlace(!enemy).add(l);
                if (
                        containsLocation(l) && !locationCollectionContains(getAttackRangeWithoutKing(enemy), l) &&
                                (isNull(getPiece(l)) || (notNull(getPiece(l)) && getPiece(l).isWhite() == enemy)) &&
                                enemyKingNotInNeighbour(l, !enemy)
                ){
                    getKing(!enemy).setPossibleRange(l);
                }
            }
        }else {
            getKing(!enemy).setPossibleRange(
                    boundPieceOrKingRangeCalc(getKing(!enemy), checkers.getFirst(), checkers.getSecond())
            );
            if (locationCollectionContains(getKing(!enemy).getPossibleRange(), checkers.getFirst().getLocation())){
                if (!((Piece) checkers.getFirst()).isInDefend()){
                    getKing(!enemy).getPossibleRange().add(checkers.getFirst().getLocation());
                }else {
                    getKing(!enemy).getPossibleRange().removeIf(l -> l.equals(checkers.getFirst().getLocation()));
                }
            }
            if (notNull(checkers.getSecond()) &&
                    locationCollectionContains(getKing(!enemy).getPossibleRange(), checkers.getSecond().getLocation())){
                if (!((Piece) checkers.getSecond()).isInDefend()){
                    getKing(!enemy).getPossibleRange().add(checkers.getSecond().getLocation());
                }else {
                    getKing(!enemy).getPossibleRange().removeIf(l -> l.equals(checkers.getSecond().getLocation()));
                }
            }
        }
    }

    private void constrainMyPiecesRangeInsteadOfCheck(boolean my){
        if (isNull(checkers.getSecond())) {
            blockCheckOrHitChecker(my);
        }
    }

    private void blockCheckOrHitChecker(boolean my)  {
        ArrayList<Location> lineToTheKingFromChecker = lineFromAPieceToAnother(checkers.getFirst(), getKing(my));
        ArrayList<Location> newRangeForPiece;
        for (IPiece p : getPieces(my)) {
            if (p.getType() != K) {
                newRangeForPiece = new ArrayList<>();
                if (!((Piece) p).isInBinding()) {
                    if (locationCollectionContains(p.getPossibleRange(), checkers.getFirst().getLocation())) {
                        newRangeForPiece.add(checkers.getFirst().getLocation());
                    }
                    if (!lineToTheKingFromChecker.isEmpty()) {
                        newRangeForPiece.addAll(intersection(p.getPossibleRange(), lineToTheKingFromChecker));
                    }
                }
                p.getPossibleRange().clear();
                p.getPossibleRange().addAll(newRangeForPiece);
            }
        }
    }

    //endregion

    //region Calc line from one piece to another

    private Set<Location> boundPieceOrKingRangeCalc(IPiece attacked,
                                                    IPiece attacker,
                                                    IPiece secondAttacker)  {

        Set<Location> originRangeOfBound = setBaseRangeOfAttacked(attacked);

        IField originFieldOfBound = getField(attacked);
        IField field = new Field(-1, -1);
        field.setPiece(attacked);
        originFieldOfBound.clean();

        attacker.updateRange();
        if (notNull(secondAttacker))
            secondAttacker.updateRange();

        Set<Location> newAttackRange;
        Set<Location> attackedNewRange;

        if (attacked.getType() == K){
            newAttackRange = (Set<Location>)(
                    notNull(secondAttacker) ?
                            union(attacker.getPossibleRange(), secondAttacker.getPossibleRange()) :
                            attacker.getPossibleRange()
            );
            attackedNewRange = (Set<Location>) minus(originRangeOfBound, newAttackRange);
            originFieldOfBound.setPiece(attacked);
            field.clean();
            attacker.updateRange();
            if (notNull(secondAttacker))
                secondAttacker.updateRange();
        }else {
            originFieldOfBound.setPiece(attacked);
            field.clean();
            attacker.updateRange();
            if (notNull(secondAttacker))
                secondAttacker.updateRange();
            newAttackRange = new HashSet<>(lineFromAPieceToAnother(attacker, attacked));
            if (locationCollectionContains(originRangeOfBound, attacker.getLocation()))
                newAttackRange.add(attacker.getLocation());
            attackedNewRange = (Set<Location>) intersection(originRangeOfBound, newAttackRange);
        }


        return attackedNewRange;
    }

    private Set<Location> setBaseRangeOfAttacked(IPiece boundOrKing)  {
        if (boundOrKing.getType() == K){
            for (Location l : matrixChooser.get(K)) {
                l = getKingsPlace(boundOrKing.isWhite()).add(l);

                if (
                        containsLocation(l) && enemyKingNotInNeighbour(l, boundOrKing.isWhite()) &&
                        !locationCollectionContains(getAttackRangeWithoutKing(!boundOrKing.isWhite()), l) &&
                        (isNull(getPiece(l)) || (notNull(getPiece(l)) && getPiece(l).isWhite() != boundOrKing.isWhite()))
                ) {
                    boundOrKing.getPossibleRange().add(l);
                }
            }
        }
        return boundOrKing.getPossibleRange();
    }

    private ArrayList<Location> lineFromAPieceToAnother(IPiece fromPiece, IPiece toPiece) {
        ArrayList<Location> lineToPiece = new ArrayList<>();
        if (fromPiece.getType() != N && fromPiece.getType() != P && !fromPieceIsOnCorner(fromPiece, toPiece)){
            Pair<Integer, Integer> addIAddJ = addToIAddToJ(fromPiece, toPiece);
            Location line = fromPiece.getLocation().add(new Location(addIAddJ.getFirst(), addIAddJ.getSecond()));
            while (containsLocation(line) && !toPiece.getLocation().equals(line)){
                lineToPiece.add(line);
                line = line.add(new Location(addIAddJ.getFirst(), addIAddJ.getSecond()));
            }
        }
        return lineToPiece;
    }

    private Pair<Integer, Integer> addToIAddToJ(IPiece fromPiece, IPiece toPiece){
        Pair<Integer, Integer> addIAddJ = new Pair<>();
        int addToI = 0, addToJ = 0;
        if (toPiece.getI() == fromPiece.getI()){
            addToJ = fromPiece.getJ() - toPiece.getJ() > 0 ? -1 : 1;
        } else if (toPiece.getJ() == fromPiece.getJ()){
            addToI = fromPiece.getI() - toPiece.getI() > 0 ? -1 : 1;
        } else if (Math.abs(fromPiece.getI() - toPiece.getI()) == Math.abs(fromPiece.getJ() - toPiece.getJ())) {
            addToI = fromPiece.getI() - toPiece.getI() > 0 ? -1 : 1;
            addToJ = fromPiece.getJ() - toPiece.getJ() > 0 ? -1 : 1;
        }
        addIAddJ.setFirst(addToI);
        addIAddJ.setSecond(addToJ);
        return addIAddJ;
    }

    private boolean fromPieceIsOnCorner(IPiece fromPiece, IPiece toPiece) {
        return Math.abs(fromPiece.getI() - toPiece.getI()) == 1 && Math.abs(fromPiece.getJ() - toPiece.getJ()) == 1;
    }


    //endregion

    //region Simple Helpers

    public boolean hasTwoKings() {
        return !pieces.isEmpty() && pieces.stream().filter(p -> p.getType() == K).count() == 2;
    }

    public static Location getTheMiddleLocation(Location first, Location last){
        if (first.getJ() == last.getJ()){
            return new Location((first.getI() + last.getI()) / 2, first.getJ());
        }else if (first.getI() == last.getI()){
            return new Location(first.getI(), (first.getJ() + last.getJ()) / 2);
        } else if (Math.abs(first.getI() - last.getI()) == Math.abs(first.getJ() - last.getJ())) {
            return new Location((first.getI() + last.getI()) / 2, (first.getJ() + last.getJ()) / 2);
        }
        return null;
    }

    /**
     * @param placeToCheck A location amit ellenőrizni akarok
     * @param my A saját királyom színe, ennek az ellentétét ellenőrzöm
     * @return Azt nézi meg, hogy az ellenfél király a kapott lokáció közelében van-e
     */
    private boolean enemyKingNotInNeighbour(Location placeToCheck, boolean my) {
        return IntStream.rangeClosed(placeToCheck.getI() - 1, placeToCheck.getI() + 1)
                .noneMatch(i ->
                        IntStream.rangeClosed(placeToCheck.getJ() - 1, placeToCheck.getJ() + 1)
                                .anyMatch(j ->
                                        getKingsPlace(!my).equals(new Location(i, j))));
    }

    private Set<Location> getAttackRangeWithoutKing(boolean forWhite) {
        Set<Location> fullAttackRange = new HashSet<>();
        for (IPiece p : getPieces(forWhite)) {
            if (p.getType() != K){
                fullAttackRange.addAll(((Piece) p).getAttackRange());
            }
        }
        return fullAttackRange;
    }

    private Set<IPiece> getTisztek(boolean my) {
        return pieces.isEmpty() ? new HashSet<>() :
                pieces.stream().filter(p -> p.isWhite() == my && p.getType() != P && p.getType() != K).collect(Collectors.toSet());
    }

    //endregion

    //endregion

}
