package classes.Game.Model.Structure;


import classes.Game.I18N.*;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.GameOver.*;


/**
 * It's not a singleton, but technically it works like a singleton.
 */
@Getter
@Setter
public class Board implements IBoard {

    //region Fields

    private int X;

    private int Y;

    private static Pair<Board, Board> board;

    private ArrayList<ArrayList<IField>> fields;

    private ArrayList<IPiece> pieces;

    private Piece whiteKing;

    private Piece blackKing;

    private Pair<IPiece, IPiece> checkers;

    /**
     * The first one is true if the one who comes got check mate in the current situation.
     * Annyi bizonyos, hogy ez esetben mindig a sakkot kapott játékos lépése során derül ki, ha ő éppen mattot kapott.
     */
    private boolean CheckMate;

    private boolean Draw;

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
        CheckMate = false;
        Draw = false;
    }

    public static Board getBoard() throws ChessGameException {
        if(board == null){
            Board b1 = new Board(MAX_WIDTH, MAX_HEIGHT);
            board = new Pair<>(b1, null);
            return board.getFirst();
        }
        return board.getFirst();
    }


    public static Board getAiBoard() throws ChessGameException {
        if(board == null){
            Board b1 = new Board(MAX_WIDTH, MAX_HEIGHT);
            board = new Pair<>(b1, null);
        }
        if (isNull(board.getSecond())){
            Board b2 = new Board(MAX_WIDTH, MAX_HEIGHT);
            board.setSecond(b2);
            return board.getSecond();
        }
        return board.getSecond();
    }

    //endregion


    //region Methods

    //region GetBy

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
    public IPiece getPiece(int i, int j) throws ChessGameException {
        for (IPiece p : pieces) {
            if (p.getI() == i && p.getJ() == j)
                return p;
        }
//        System.out.println("Nincs figura a(z) " + (this == getAiBoard() ? "aiboard" : "board") + "[" + i + ", " + j + "] mezőn.\n");
        return null;
    }

    @Override
    public IPiece getPiece(Location Location) throws ChessGameException {
        return getPiece(Location.getI(), Location.getJ());
    }

    @Override
    public IPiece getPiece(IField field) throws ChessGameException {
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

    public Set<IPiece> myPieces(){
        return getPieces(whiteToPlay);
    }

    public Set<IPiece> enemyPieces(){
        return getPieces(!whiteToPlay);
    }

    public Piece getKing(boolean whiteNeeded){
        return whiteNeeded ? whiteKing : blackKing;
    }

    public IPiece getMyKing() {
        return getKing(whiteToPlay);
    }

    private IPiece getEnemyKing() {
        return getKing(!whiteToPlay);
    }

    public Location getKingsPlace(boolean forWhite){
        return getKing(forWhite).getLocation();
    }

    //endregion

    @Override
    public void cleanBoard() throws ChessGameException {
        for (ArrayList<IField> row : this.fields) {
            for (IField f : row) {
                if (!(f instanceof Field)){
                    throw new ChessGameException(BAD_TYPE_MSG);
                }
                f.clean();
            }
        }
        pieces.clear();
        whitePieceSet.clean();
        blackPieceSet.clean();
    }

    @Override
    public void rangeUpdater() throws ChessGameException, InterruptedException {

        clearRangesAndStuffBeforeUpdate();
        pseudos();
        GameOver gameOver;
        if (theBoardHasKings()){
            constrainPseudos();
            inspectCheck(!whiteToPlay);
            if (isNull(checkers)) {
                kingsRanges();
            }
            gameOver = gameEnd(this);
            if (notNull(gameOver)){
                if (gameOver == GameOver.CheckMate){
                    CheckMate = true;
                }else {
                    Draw = true;
                }
            }
        }
    }

    private boolean theBoardHasKings() {
        return pieces.stream().anyMatch(p -> p.getType() == K);
    }

    private Location getTheMiddleLocation(Location first, Location last){
        if (first.getJ() == last.getJ()){
            return new Location((first.getI() + last.getI()) / 2, first.getJ());
        }else if (first.getI() == last.getI()){
            return new Location(first.getI(), (first.getJ() + last.getJ()) / 2);
        }
        return null;
    }

    private void clearRangesAndStuffBeforeUpdate(){
        checkers = null;
        CheckMate = false;
        Draw = false;
        for (IPiece p : pieces) {
            ((Piece) p).setPossibleRange(new HashSet<>());
            ((Piece) p).setLegals("new");
            ((Piece) p).setPseudoLegals("new");
            ((Piece) p).setBounderPiece(null);
        }
    }

    public void pseudos() throws ChessGameException {
        for (IPiece p : pieces) {
            if (p.getType() != K) {
                p.updateRange();
            }
        }
    }

    public void constrainPseudos() throws ChessGameException {
        //Leszűkítés annak függvényében, hogy valamely lépéssel sakk felfedése történne
        constrainCalculatedPseudos(!whiteToPlay);
        constrainCalculatedPseudos(whiteToPlay);

    }

    private void constrainCalculatedPseudos(boolean my) throws ChessGameException{
        Set<IPiece> piecesInBinding = findAndSetBindingFor(my);
        constrainBoundPiecesRanges(piecesInBinding);
    }

    private Set<IPiece> findAndSetBindingFor(boolean my) throws ChessGameException {
        Set<IPiece> boundPieces = new HashSet<>();
        for (IPiece enemyTiszt : getTisztek(!my)) {
            if (isThereAnyPieceInPossibleBindingBy(enemyTiszt)){
                IPiece boundPiece = boundPieceBy(enemyTiszt);
                if (notNull(boundPiece)) {
                    boundPieces.add(boundPiece);
                    ((Piece) boundPiece).setBounderPiece(enemyTiszt);
                }
            }
        }
        return boundPieces;
    }

    private void constrainBoundPiecesRanges(Set<IPiece> piecesInBinding) throws ChessGameException {
        for (IPiece p : piecesInBinding) {
            ((Piece) p).setPossibleRange(boundPieceOrKingRangeCalc(p, ((Piece) p).getBounderPiece(), null));
        }
    }

    private boolean isThereAnyPieceInPossibleBindingBy(IPiece enemyTiszt){
        return isOnTheSameLineWithMyKing(enemyTiszt) && tisztAttackRangeContainsOneOfMyPiece(enemyTiszt);
    }

    private IPiece boundPieceBy(IPiece enemyTiszt) throws ChessGameException{
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
            case V -> {
                return myKing.getI() == enemyTiszt.getI() || myKing.getJ() == enemyTiszt.getJ() ||
                        Math.abs(myKing.getI() - enemyTiszt.getI()) == Math.abs(myKing.getJ() - enemyTiszt.getJ());
            }
            case B -> {
                return myKing.getI() == enemyTiszt.getI() || myKing.getJ() == enemyTiszt.getJ();
            }
            case F -> {
                return Math.abs(myKing.getI() - enemyTiszt.getI()) == Math.abs(myKing.getJ() - enemyTiszt.getJ());
            }
            default -> {
                return false;
            }
        }
    }

    private boolean tisztAttackRangeContainsOneOfMyPiece(IPiece enemyTiszt){
        return enemyTiszt.getPossibleRange().stream().anyMatch(l -> {
            try {
                return notNull(getPiece(l)) && getPiece(l).isWhite() != enemyTiszt.isWhite() && getPiece(l).getType() != K;
            } catch (ChessGameException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private ArrayList<Location> lineFromAPieceToAnother(IPiece fromPiece, IPiece toPiece) {
        ArrayList<Location> lineToPiece = new ArrayList<>();
        Pair<Integer, Integer> addIAddJ = addToIAddToJ(fromPiece, toPiece);
        Location line = fromPiece.getLocation().add(new Location(addIAddJ.getFirst(), addIAddJ.getSecond()));
        while (containsLocation(line) && !toPiece.getLocation().EQUALS(line)){
            lineToPiece.add(line);
            line = line.add(new Location(addIAddJ.getFirst(), addIAddJ.getSecond()));
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

    public Set<Location> boundPieceOrKingRangeCalc(IPiece boundOrKing, IPiece bounderOrChecker, IPiece secondChecker) throws ChessGameException {
        Location originLocOfBound = boundOrKing.getLocation();
        if (boundOrKing.getType() == K){
            for (Location l : matrixChooser.get(K)) {
                l = getKingsPlace(boundOrKing.isWhite()).add(l);
                if (containsLocation(l))
                    boundOrKing.getPossibleRange().add(l);
            }
        }
        Set<Location> originRangeOfBound = boundOrKing.getPossibleRange();
        boundOrKing.getPossibleRange().removeIf(l -> {
            try {
                return notNull(getPiece(l)) && getPiece(l).isWhite() == boundOrKing.isWhite();
            } catch (ChessGameException e) {
                throw new RuntimeException(e);
            }
        });

        IField originFieldOfBound = getField(boundOrKing);
        IField field = new Field(-1, -1);
        field.setPiece(boundOrKing);
        originFieldOfBound.clean();
        bounderOrChecker.updateRange();
        if (notNull(secondChecker))
            secondChecker.updateRange();

        Set<Location> newRangeOfCheckersOrBounder = (Set<Location>)(
            notNull(secondChecker) ? union(bounderOrChecker.getPossibleRange(), secondChecker.getPossibleRange()) : bounderOrChecker.getPossibleRange()
        );

        if (isNull(secondChecker) && boundOrKing.getType() != K){
            newRangeOfCheckersOrBounder.clear();
            newRangeOfCheckersOrBounder.addAll(lineFromAPieceToAnother(bounderOrChecker, getKing(!bounderOrChecker.isWhite())));
        }

        Set<Location> newRangeOfBound = (Set<Location>)
                (boundOrKing.getType() == K ?
                        minus(originRangeOfBound, newRangeOfCheckersOrBounder) :
                        intersection(originRangeOfBound, newRangeOfCheckersOrBounder));
        newRangeOfBound = (Set<Location>) minus(newRangeOfBound, originLocOfBound);
        if (boundOrKing.getType() != K && locationCollectionContains(originRangeOfBound, bounderOrChecker.getLocation())){
            newRangeOfBound.add(bounderOrChecker.getLocation());
        }

        originFieldOfBound.setPiece(boundOrKing);
        field.clean();
        bounderOrChecker.updateRange();
        if (notNull(secondChecker))
            secondChecker.updateRange();

        return newRangeOfBound;
    }


    private void kingsRanges() throws ChessGameException {
        //A királyok lépéslehetőségeinek leszűkítése a már meghatározott rangek függvényében
        kingSimpleMoves(!whiteToPlay);
        kingSimpleMoves(whiteToPlay);
        //Sáncok lehetőségeink számbavétele
        // Ellenőrizni, hogy van-e lehetőség erre-arra a sáncra
        // Ha igen megnézni, hogy a sánccal megtett király mezők közül bele esik-e valamelyik az ellenfél range-be.
        kingCastle(!whiteToPlay);
        kingCastle(whiteToPlay);
    }

    /**
     * @param enemy this means who would be the player, who gives check
     * Alapvetés szerint pedig megnézi sakkban vagyok-e, ha igen, aszerint kalkulálja ki a további lehetőségeim
     */
    private void inspectCheck(boolean enemy) throws ChessGameException, InterruptedException {
        if (locationCollectionContains(getAttackRangeWithoutKing(enemy), getKingsPlace(!enemy))){

            findCheckers(enemy);

            if (notNull(checkers.getFirst()))
                constrainPossibilitiesInsteadOfCheck(enemy);

        }
    }

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

    private void constrainPossibilitiesInsteadOfCheck(boolean enemy) throws ChessGameException, InterruptedException {

        kingStepOutFromCheck(enemy);
        if (isNull(checkers.getSecond())) {
            blockCheckOrHitChecker(enemy);
        }
    }

    private void kingStepOutFromCheck(boolean enemy) throws ChessGameException {
        getKing(!enemy).setPossibleRange(
                boundPieceOrKingRangeCalc(getKing(!enemy), checkers.getFirst(), checkers.getSecond())
        );
        if (locationCollectionContains(getKing(!enemy).getPossibleRange(), checkers.getFirst().getLocation())){
            if (((Piece) checkers.getFirst()).isInDefend()){
                getKing(!enemy).getPossibleRange().remove(checkers.getFirst().getLocation());
            }
        }
        if (notNull(checkers.getSecond()) &&
                locationCollectionContains(getKing(!enemy).getPossibleRange(), checkers.getSecond().getLocation())){
            if (((Piece) checkers.getSecond()).isInDefend()){
                getKing(!enemy).getPossibleRange().remove(checkers.getSecond().getLocation());
            }
        }
    }

    private void blockCheckOrHitChecker(boolean enemy)  {
        ArrayList<Location> lineToTheKingFromChecker = lineFromAPieceToAnother(checkers.getFirst(), getKing(!enemy));
        ArrayList<Location> newRangeForPiece;
        for (IPiece p : getPieces(!enemy)) {
            if (p.getType() != K){
                newRangeForPiece = new ArrayList<>();
                if (!((Piece) p).isInBinding()) {
                    if (locationCollectionContains(p.getPossibleRange(), checkers.getFirst().getLocation())) {
                        newRangeForPiece.add(checkers.getFirst().getLocation());
                    }
                    if (checkers.getFirst().getType() != H && checkers.getFirst().getType() != G) {
                        newRangeForPiece.addAll(intersection(p.getPossibleRange(), lineToTheKingFromChecker));
                    }
                }
                p.getPossibleRange().clear();
                p.getPossibleRange().addAll(newRangeForPiece);
            }
        }
    }

    private Set<IPiece> getTisztek(boolean my) {
        return pieces.stream().filter(p -> p.isWhite() == my && p.getType() != G && p.getType() != K).collect(Collectors.toSet());
    }

    //region King Helpers

    private void kingSimpleMoves(boolean my) throws ChessGameException {
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
            }
        }
    }

    /**
     * @param forWhite in that case forWhite simbolize my color (Me is who count the step)
     */
    private void kingCastle(boolean forWhite) throws ChessGameException {
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
                              boolean smallCastleEnabled) throws ChessGameException {
        if (castleHelpersIf(forWhite, bigCastlePointLocation, bigCastleRoadLocation, bigCastleEnabled)) {
            getKing(forWhite).setPossibleRange(bigCastlePointLocation);
        }

        if (castleHelpersIf(forWhite, smallCastlePointLocation, smallCastleRoadLocation, smallCastleEnabled)) {
            getKing(forWhite).setPossibleRange(smallCastlePointLocation);
        }
    }

    private boolean castleHelpersIf(boolean forWhite, Location castlePoint, Location castleRoad, boolean castleEnabled) throws ChessGameException {
        return castleEnabled &&
                !locationCollectionContains(getAttackRangeWithoutKing(!forWhite), castleRoad) &&
                !locationCollectionContains(getAttackRangeWithoutKing(!forWhite), castlePoint) &&
                enemyKingNotInNeighbour(castleRoad, forWhite) &&
                enemyKingNotInNeighbour(castlePoint, forWhite) &&
                isNull(getPiece(castlePoint)) && isNull(getPiece(castleRoad));
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
                                        getKingsPlace(!my).EQUALS(new Location(i, j))));
    }

    //endregion

    private Set<Location> getAttackRangeWithoutKing(boolean forWhite) {
        return pieces.stream()
                .filter(p -> (p.getType() != K && p.isWhite() == forWhite))
                .flatMap(p -> (p.getType() == G ? ((Piece) p).getWatchedRange() : p.getPossibleRange()).stream())
                .collect(Collectors.toSet());
    }

    public void addLegalMovesToPieces() {
        for (IPiece p : myPieces()) {
            for (Location to : p.getPossibleRange()) {
                ((Piece) p).setLegals(new Move(
                        p,
                        to,
                        ((Piece) p).getBoard()
                ));
            }
        }
    }

    public HashMap<IPiece, Set<Move>> getAllLegalMoves(boolean forWhite){
        HashMap<IPiece, Set<Move>> legals = new HashMap<>();
        for (IPiece p : getPieces(forWhite)) {
            if (!((Piece) p).getLegalMoves().isEmpty()) {
                legals.put(
                        p,
                        ((Piece) p).getLegalMoves()
                );
            }
        }
        return legals;
    }

    //endregion

}
