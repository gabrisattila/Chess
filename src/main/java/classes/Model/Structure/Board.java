package classes.Model.Structure;


import classes.Model.I18N.Location;
import classes.Model.I18N.Pair;
import classes.Model.I18N.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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
            board = new Board(VARS.FINALS.MAX_WIDTH, VARS.FINALS.MAX_HEIGHT);
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
                    ChessGameException.throwBadTypeErrorIfNeeded(new Object[]{
                            f, Field.class.getName(),
                            " ezért nem tudom elvégezni a clean műveletet.\n"
                    });
                }
                f.clean();
            }
        }
        pieces.clear();
        VARS.MUTABLE.whitePieceSet.clean();
        VARS.MUTABLE.blackPieceSet.clean();
    }

    @Override
    public void rangeUpdater() {
        clearRangesAndStuffBeforeUpdate();
        pseudos();
        if (hasTwoKings()) {
            constrainPseudos();
            inspectCheck(!VARS.MUTABLE.whiteToPlay);
            if (METHODS.isNull(checkers)) {
                kingFreeRange(!VARS.MUTABLE.whiteToPlay);
                kingFreeRange(VARS.MUTABLE.whiteToPlay);
            } else {
                kingFreeRange(!VARS.MUTABLE.whiteToPlay);
                kingRangeInsteadOfCheck(VARS.MUTABLE.whiteToPlay);
            }
        }
        clearDuplicatesFromRanges();
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
    }

    public void pseudos()  {
        for (IPiece p : pieces) {
            if (p.getType() != PieceType.K) {
                p.updateRange();
            }
        }
    }

    private void clearDuplicatesFromRanges(){
        Set<Location> finalRange = new HashSet<>();
        for (IPiece p : pieces) {
            for (Location l : p.getPossibleRange()) {
                if (!METHODS.locationCollectionContains(finalRange, l)){
                    finalRange.add(l);
                }
            }
            ((Piece) p).setPossibleRange(finalRange);
            finalRange = new HashSet<>();
        }
    }

    public void constrainPseudos()  {
        constrainCalculatedPseudos(!VARS.MUTABLE.whiteToPlay);
        constrainCalculatedPseudos(VARS.MUTABLE.whiteToPlay);
    }

    /**
     * @param enemy this means who would be the player, who gives check
     * Alapvetés szerint pedig megnézi sakkban vagyok-e, ha igen, a checkers többé nem null
     */
    private void inspectCheck(boolean enemy) {
        if (METHODS.locationCollectionContains(getAttackRangeWithoutKing(enemy), getKingsPlace(!enemy))){
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
                if (METHODS.notNull(boundPiece) && boundPiece.isWhite() != enemyTiszt.isWhite()) {
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
            if (METHODS.notNull(getPiece(l))){
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
        return enemyTiszt.getPossibleRange().stream().anyMatch(l -> METHODS.notNull(getPiece(l)) && getPiece(l).isWhite() != enemyTiszt.isWhite() && getPiece(l).getType() != PieceType.K);
    }

    //endregion

    //region King Free Move

    private void kingSimpleMoves(boolean my) {
        getKing(my).setPossibleRange(new HashSet<>());
        for (Location l : VARS.FINALS.matrixChooser.get(PieceType.K)) {
            Location possiblePlaceOfMyKing = l.add(getKingsPlace(my));
            if (
                    METHODS.containsLocation(possiblePlaceOfMyKing) &&
                            (!getField(possiblePlaceOfMyKing).isGotPiece() || getPiece(possiblePlaceOfMyKing).isWhite() != my) &&
                            enemyKingNotInNeighbour(possiblePlaceOfMyKing, my) &&
                            !METHODS.locationCollectionContains(getAttackRangeWithoutKing(!my), possiblePlaceOfMyKing)
            ){
                getKing(my).setPossibleRange(possiblePlaceOfMyKing);
            } else if (METHODS.containsLocation(l)) {
                getKing(my).getWatchedRange().add(l);
            }
        }
    }

    /**
     * @param forWhite in that case forWhite simbolize my color (Me is who count the step)
     */
    private void kingCastle(boolean forWhite) {
        if (VARS.FINALS.MAX_WIDTH == 8 && VARS.FINALS.MAX_HEIGHT == 8 &&
                (   (forWhite && (VARS.MUTABLE.whiteBigCastleEnabled || VARS.MUTABLE.whiteSmallCastleEnabled)) ||
                        (!forWhite && (VARS.MUTABLE.blackBigCastleEnabled || VARS.MUTABLE.blackSmallCastleEnabled)) ) &&
                !METHODS.locationCollectionContains(getAttackRangeWithoutKing(!forWhite), getKingsPlace(forWhite))
        ){

            int bigCastlePointPlus = (VARS.MUTABLE.whiteDown ? -2 : 2);
            int bigCastleRoadPlus = (VARS.MUTABLE.whiteDown ? -1 : 1);
            Location bigCastlePointLocation = new Location(
                    getKingsPlace(forWhite).getI(),
                    getKingsPlace(forWhite).getJ() + bigCastlePointPlus);
            Location bigCastleRoadLocation = new Location(
                    getKingsPlace(forWhite).getI(),
                    getKingsPlace(forWhite).getJ() + bigCastleRoadPlus);

            int smallCastlePointPlus = (VARS.MUTABLE.whiteDown ? 2 : -2);
            int smallCastleRoadPlus = (VARS.MUTABLE.whiteDown ? 1 : -1);
            Location smallCastlePointLocation = new Location(
                    getKingsPlace(forWhite).getI(),
                    getKingsPlace(forWhite).getJ() + smallCastlePointPlus);
            Location smallCastleRoadLocation = new Location(
                    getKingsPlace(forWhite).getI(),
                    getKingsPlace(forWhite).getJ() + smallCastleRoadPlus);

            Location bigCastlePlusRoad = getTheMiddleLocation(bigCastlePointLocation, new Location(
                    bigCastlePointLocation.getI(), bigCastlePointLocation.getJ() == 2 ? 1 : 6
            ));
            boolean theresNoPieceOnBigCastlePlusRoad = METHODS.notNull(bigCastlePlusRoad) && METHODS.isNull(getPiece(bigCastlePlusRoad));

            castleHelper(
                    forWhite,
                    bigCastlePointLocation,
                    bigCastleRoadLocation,
                    smallCastlePointLocation,
                    smallCastleRoadLocation,
                    (getKing(forWhite).isWhite() ? VARS.MUTABLE.whiteBigCastleEnabled : VARS.MUTABLE.blackBigCastleEnabled)
                            && theresNoPieceOnBigCastlePlusRoad,
                    getKing(forWhite).isWhite() ? VARS.MUTABLE.whiteSmallCastleEnabled : VARS.MUTABLE.blackSmallCastleEnabled
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
                !METHODS.locationCollectionContains(getAttackRangeWithoutKing(!forWhite), castleRoad) &&
                !METHODS.locationCollectionContains(getAttackRangeWithoutKing(!forWhite), castlePoint) &&
                enemyKingNotInNeighbour(castleRoad, forWhite) &&
                enemyKingNotInNeighbour(castlePoint, forWhite) &&
                METHODS.isNull(getPiece(castlePoint)) && METHODS.isNull(getPiece(castleRoad));
    }

    //endregion

    //region Check

    private void findCheckers(boolean enemy){
        checkers = new Pair<>();
        for (IPiece enemyP : getPieces(enemy)) {
            if (METHODS.locationCollectionContains(enemyP.getPossibleRange(), getKingsPlace(!enemy))){
                if (METHODS.isNull(checkers.getFirst())) {
                    checkers.setFirst(enemyP);
                } else {
                    checkers.setSecond(enemyP);
                    break;
                }
            }
        }
    }

    private void kingStepOutFromCheck(boolean enemy)  {
        if (checkers.getFirst().getType() == PieceType.N || checkers.getFirst().getType() == PieceType.P){
            for (Location l : VARS.FINALS.matrixChooser.get(PieceType.K)) {
                l = getKingsPlace(!enemy).add(l);
                if (
                        METHODS.containsLocation(l) && !METHODS.locationCollectionContains(getAttackRangeWithoutKing(enemy), l) &&
                                (METHODS.isNull(getPiece(l)) || (METHODS.notNull(getPiece(l)) && getPiece(l).isWhite() == enemy)) &&
                                enemyKingNotInNeighbour(l, !enemy)
                ){
                    getKing(!enemy).setPossibleRange(l);
                }
            }
        }else {
            getKing(!enemy).setPossibleRange(
                    boundPieceOrKingRangeCalc(getKing(!enemy), checkers.getFirst(), checkers.getSecond())
            );
            if (METHODS.locationCollectionContains(getKing(!enemy).getPossibleRange(), checkers.getFirst().getLocation())){
                if (!((Piece) checkers.getFirst()).isInDefend()){
                    getKing(!enemy).getPossibleRange().add(checkers.getFirst().getLocation());
                }else {
                    getKing(!enemy).getPossibleRange().removeIf(l -> l.equals(checkers.getFirst().getLocation()));
                }
            }
            if (METHODS.notNull(checkers.getSecond()) &&
                    METHODS.locationCollectionContains(getKing(!enemy).getPossibleRange(), checkers.getSecond().getLocation())){
                if (!((Piece) checkers.getSecond()).isInDefend()){
                    getKing(!enemy).getPossibleRange().add(checkers.getSecond().getLocation());
                }else {
                    getKing(!enemy).getPossibleRange().removeIf(l -> l.equals(checkers.getSecond().getLocation()));
                }
            }
        }
    }

    private void constrainMyPiecesRangeInsteadOfCheck(boolean my){
        if (METHODS.isNull(checkers.getSecond())) {
            blockCheckOrHitChecker(my);
        }
    }

    private void blockCheckOrHitChecker(boolean my)  {
        ArrayList<Location> lineToTheKingFromChecker = lineFromAPieceToAnother(checkers.getFirst(), getKing(my));
        ArrayList<Location> newRangeForPiece;
        for (IPiece p : getPieces(my)) {
            if (p.getType() != PieceType.K) {
                newRangeForPiece = new ArrayList<>();
                if (!((Piece) p).isInBinding()) {
                    if (METHODS.locationCollectionContains(p.getPossibleRange(), checkers.getFirst().getLocation())) {
                        newRangeForPiece.add(checkers.getFirst().getLocation());
                    }
                    if (!lineToTheKingFromChecker.isEmpty()) {
                        newRangeForPiece.addAll(METHODS.intersection(p.getPossibleRange(), lineToTheKingFromChecker));
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
        if (METHODS.notNull(secondAttacker))
            secondAttacker.updateRange();

        Set<Location> newAttackRange;
        Set<Location> attackedNewRange;

        if (attacked.getType() == PieceType.K){
            newAttackRange = (Set<Location>)(
                    METHODS.notNull(secondAttacker) ?
                            METHODS.union(attacker.getPossibleRange(), secondAttacker.getPossibleRange()) :
                            attacker.getPossibleRange()
            );
            attackedNewRange = (Set<Location>) METHODS.minus(originRangeOfBound, newAttackRange);
            originFieldOfBound.setPiece(attacked);
            field.clean();
            attacker.updateRange();
            if (METHODS.notNull(secondAttacker))
                secondAttacker.updateRange();
        }else {
            originFieldOfBound.setPiece(attacked);
            field.clean();
            attacker.updateRange();
            if (METHODS.notNull(secondAttacker))
                secondAttacker.updateRange();
            newAttackRange = new HashSet<>(lineFromAPieceToAnother(attacker, attacked));
            if (METHODS.locationCollectionContains(originRangeOfBound, attacker.getLocation()))
                newAttackRange.add(attacker.getLocation());
            attackedNewRange = (Set<Location>) METHODS.intersection(originRangeOfBound, newAttackRange);
        }


        return attackedNewRange;
    }

    private Set<Location> setBaseRangeOfAttacked(IPiece boundOrKing)  {
        if (boundOrKing.getType() == PieceType.K){
            for (Location l : VARS.FINALS.matrixChooser.get(PieceType.K)) {
                l = getKingsPlace(boundOrKing.isWhite()).add(l);

                if (
                        METHODS.containsLocation(l) && enemyKingNotInNeighbour(l, boundOrKing.isWhite()) &&
                        !METHODS.locationCollectionContains(getAttackRangeWithoutKing(!boundOrKing.isWhite()), l) &&
                        (METHODS.isNull(getPiece(l)) || (METHODS.notNull(getPiece(l)) && getPiece(l).isWhite() != boundOrKing.isWhite()))
                ) {
                    boundOrKing.getPossibleRange().add(l);
                }
            }
        }
        return boundOrKing.getPossibleRange();
    }

    private ArrayList<Location> lineFromAPieceToAnother(IPiece fromPiece, IPiece toPiece) {
        ArrayList<Location> lineToPiece = new ArrayList<>();
        if (fromPiece.getType() != PieceType.N && fromPiece.getType() != PieceType.P && !fromPieceIsOnCorner(fromPiece, toPiece)){
            Pair<Integer, Integer> addIAddJ = addToIAddToJ(fromPiece, toPiece);
            Location line = fromPiece.getLocation().add(new Location(addIAddJ.getFirst(), addIAddJ.getSecond()));
            while (METHODS.containsLocation(line) && !toPiece.getLocation().equals(line)){
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
        return !pieces.isEmpty() && pieces.stream().filter(p -> p.getType() == PieceType.K).count() == 2;
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
            if (p.getType() != PieceType.K){
                fullAttackRange.addAll(((Piece) p).getAttackRange());
            }
        }
        return fullAttackRange;
    }

    private Set<IPiece> getTisztek(boolean my) {
        return pieces.isEmpty() ? new HashSet<>() :
                pieces.stream().filter(p -> p.isWhite() == my && p.getType() != PieceType.P && p.getType() != PieceType.K).collect(Collectors.toSet());
    }

    //endregion

    //endregion

}
