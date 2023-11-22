package classes.Game.Model.Structure;


import classes.Game.I18N.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
    public void rangeUpdater() throws ChessGameException {

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
                return;
            }
        }

        rookCastleParaming();
        emPassantCaseSet();
    }

    private boolean theBoardHasKings() {
        return pieces.stream().anyMatch(p -> p.getType() == K);
    }

    private void rookCastleParaming() throws ChessGameException {
        String whichRook = "";
        for (IPiece p : getTisztek(whiteToPlay)) {
            if (p.getType() == B){
                boolean whiteDown = (!whiteAiNeeded || !theresOnlyOneAi);
                if (p.isWhite() && ((whiteDown && p.getLocation().EQUALS(0, 0)) || (!whiteDown && p.getLocation().EQUALS(0, 7)))){
                    whichRook = "Q";
                } else if (p.isWhite() && ((whiteDown && p.getLocation().EQUALS(0, 7)) || (!whiteDown && p.getLocation().EQUALS(0, 0)))) {
                    whichRook = "K";
                }
                if (!p.isWhite() && ((whiteDown && p.getLocation().EQUALS(7, 0)) || (!whiteDown && p.getLocation().EQUALS(7, 7)))){
                    whichRook = "q";
                } else if (!p.isWhite() && ((whiteDown && p.getLocation().EQUALS(7, 7)) || (!whiteDown && p.getLocation().EQUALS(7, 0)))) {
                    whichRook = "k";
                }
                for (Location l : p.getPossibleRange()) {
                    ((Piece) p).setLegals(new Move_(
                            this,
                            p,
                            p.getLocation(),
                            l,
                            notNull(getPiece(l)) ? getPiece(l) : null,
                            whichRook
                    ));
                }
            }
        }
    }

    private void emPassantCaseSet() throws ChessGameException {
        StringBuilder emPassant = new StringBuilder();
        for (IPiece p : myPieces()) {
            if (p.getType() == G){
                for (Location l : p.getPossibleRange()) {

                    Location neighbour1 = new Location(l.getI(), l.getJ() - 1),
                            neighbour2 = new Location(l.getI(), l.getJ() + 1);

                    if (emPassantIf(p, l, neighbour1) || emPassantIf(p, l, neighbour2)){
                        Location middle = getTheMiddleLocation(p.getLocation(), l);
                        assert middle != null;
                        emPassant.append(numsForPrinting.get(middle.getI()));
                        emPassant.append(numsForPrinting.get(middle.getJ()));
                        ((Piece) p).setLegals(new Move_(
                                this,
                                p,
                                p.getLocation(),
                                l,
                                notNull(getPiece(l)) ? getPiece(l) : null,
                                emPassant.toString()
                        ));
                        emPassant = new StringBuilder();
                    }
                }
            }
        }
    }

    private boolean emPassantIf(IPiece p, Location l, Location neighbour) throws ChessGameException {
        return Math.abs(p.getI() - l.getI()) > 1 && containsLocation(neighbour) &&
                notNull(getPiece(neighbour)) && getPiece(neighbour).getType() == G &&
                getPiece(neighbour).isWhite() != p.isWhite() &&
                locationCollectionContains(
                        ((Piece)getPiece(neighbour)).getWatchedRange(),
                        getTheMiddleLocation(p.getLocation(), l)
                );
    }

    private Location getTheMiddleLocation(Location first, Location last){
        if (first.getJ() == last.getJ()){
            return new Location((first.getI() + last.getI()) / 2, first.getJ());
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
        constrainTheCalculatedPseudos(!whiteToPlay);
        constrainTheCalculatedPseudos(whiteToPlay);

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
    private void inspectCheck(boolean enemy) throws ChessGameException {
        if (locationCollectionContains(getAttackRangeWithoutKing(enemy), getKingsPlace(!enemy))){

            findCheckers(enemy);

            if (notNull(checkers.getFirst()))
                findLegalMovesInCheckCases(enemy);

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

    private void findLegalMovesInCheckCases(boolean enemy) throws ChessGameException {
        Move_ supposedMove = new Move_(this);

        if (notNull(checkers.getSecond())){

            kingStepOutFromCheck(supposedMove, enemy);
        }else {

            kingStepOutFromCheck(supposedMove, enemy);

            blockCheckOrHitChecker(supposedMove, enemy);
        }
    }

    private void kingStepOutFromCheck(Move_ supposedMove, boolean enemy) throws ChessGameException {
        constrainInsteadOfCheck(matrixChooser.get(K), supposedMove, getKing(!enemy), enemy);
    }

    private void blockCheckOrHitChecker(Move_ supposed, boolean enemy) throws ChessGameException {

        Set<Location> wholePileOfChecker = checkers.getFirst().getPossibleRange();
        wholePileOfChecker.add(checkers.getFirst().getLocation());

        Set<Location> neededIntersection;

        for (IPiece p : minus(myPieces(), getMyKing())) {

            neededIntersection = (Set<Location>) intersection(p.getPossibleRange(), wholePileOfChecker);

            if (!neededIntersection.isEmpty()) {
                constrainInsteadOfCheck(neededIntersection, supposed, p, enemy);
            }else {
                ((Piece) p).setPossibleRange(new HashSet<>());
            }
        }
        
    }

    private void constrainInsteadOfCheck(Set<Location> setThatWeCollectFrom,
                                         Move_ supposed, IPiece p, boolean enemy) throws ChessGameException {

        Set<Location> setToCollectRightPlacesToGo = new HashSet<>();
        for (Location l : setThatWeCollectFrom) {
            l = p.getLocation().add(l);
            if (
                    containsLocation(l) && enemyKingNotInNeighbour(l, !enemy) &&
                    ((isNull(getPiece(l)) || notNull(getPiece(l)) && getPiece(l).isWhite() != getKing(!enemy).isWhite())) &&
                    supposedMoveWith(supposed, p, l, enemy)
            ){
                if (p.getType() == K){
                    if (enemyKingNotInNeighbour(l, !enemy)){
                        setToCollectRightPlacesToGo.add(l);
                    }
                }else {
                    setToCollectRightPlacesToGo.add(l);
                }
            }
        }
        if (p.getPossibleRange() != setToCollectRightPlacesToGo)
            ((Piece) p).setPossibleRange(setToCollectRightPlacesToGo);
        if (p.getType() == K){
            for (Location l : p.getPossibleRange()) {
                ((Piece) p).setLegals(new Move_(
                        this,
                        p,
                        p.getLocation(),
                        l,
                        notNull(getPiece(l)) ? getPiece(l) : null
                ));
            }
        }
    }

    /**
     * @param supposed theMoveObjectThatWeUse
     * @param piece theMovesWhatParam
     * @param to theMovesToParam
     * @param enemy theColorOfEnemy
     * @return Decides about a place that if my piece moves there the check still remain or not
     */
    private boolean supposedMoveWith(Move_ supposed, IPiece piece, Location to, boolean enemy) throws ChessGameException {
        supposed.setEveryThing(piece, to);
        supposed.supposedMove();
        boolean placeIsGood = !locationCollectionContains(getAttackRangeWithoutKing(enemy), getKingsPlace(!enemy));
        supposed.supposedMoveBack();
        return placeIsGood;
    }

    private void constrainTheCalculatedPseudos(boolean forWhite) throws ChessGameException {
        var set = setBinding(forWhite);

        for (var v : set){
            cleanRangeIfPieceInBinding(v.First, v.Second, v.Third);
        }
    }

    private void cleanRangeIfPieceInBinding(IPiece piece, Set<Location> binding, IPiece checker){

        if (collectionContains(((Piece) piece).getAttackRange(), checker.getLocation()))
            binding.add(checker.getLocation());

        piece.getPossibleRange().removeIf(l -> collectionNotContains(binding, l));

    }

    private Set<Tuple<IPiece, Set<Location>, IPiece>> setBinding(boolean white) throws ChessGameException {

        IPiece ownPiece = null;
        int ownPieceCounter = 0;

        Set<Tuple<IPiece, Set<Location>, IPiece>> set = new HashSet<>();

        for (IPiece p : getTisztek(!white)) {
            if (!p.isEmpty()){
                if (getAddIAddJ(p, white ? WHITE_STRING : BLACK_STRING).First != -100) {
                    for (Location Location : lineToTheKing(p, this, white ? WHITE_STRING : BLACK_STRING)) {
                        if (!isNull(getPiece(Location))) {
                            if (getPiece(Location).isWhite() != white)
                                break;
                            if (getPiece(Location).getType() == PieceType.K)
                                break;
                            else {
                                ownPiece = getPiece(Location);
                                ownPieceCounter++;
                            }
                            if (ownPieceCounter > 1)
                                break;
                        }
                    }
                    if (ownPieceCounter == 1) {
                        for (IPiece piece : pieces) {
                            if (piece.isWhite() && piece.EQUALS(ownPiece)) {
                                ((Piece) piece).setInBinding(true);
                                set.add(new Tuple<>(
                                        ownPiece,
                                        lineToTheKing(p, this, white ? WHITE_STRING : BLACK_STRING),
                                        p
                                ));
                            }
                        }
                    }
                }
            }
        }
        return set;
    }

    public Set<Location> lineToTheKing(IPiece piece, IBoard board, String whichKingNeeded) throws ChessGameException {
        Set<Location> line = new HashSet<>();
        if (piece.getType() != PieceType.H && piece.getType() != PieceType.G){
            int i = piece.getI(), j = piece.getJ();

            Tuple<Integer, Integer, Piece> getAddIJ = new Tuple<>(getAddIAddJ(piece, whichKingNeeded));
            int addToI = getAddIJ.First, addToJ = getAddIJ.Second;
            i += addToI;
            j += addToJ;
            if (i != piece.getI() || j != piece.getJ()){
                while (containsLocation(i, j) && board.getPiece(i, j) != getAddIJ.Third) {
                    line.add(new Location(i, j));
                    i += addToI;
                    j += addToJ;
                }
            }
        }
        return line;
    }

    public Tuple<Integer, Integer, Piece> getAddIAddJ(IPiece piece, String whichKingNeeded){

        Tuple<Integer, Integer, Piece> tuple = new Tuple<>(-100, -100, null);

        int toI = -100, toJ = -100;

        Piece enemyKing = getKing(whichKingNeeded.equals(WHITE_STRING));

        boolean isThereDifferenceInTheNumsOfRowsAndCols =
                Math.abs(enemyKing.getI() - piece.getI()) ==
                Math.abs(enemyKing.getJ() - piece.getJ());

        boolean areTheyOnTheSameRow = enemyKing.getI() == piece.getI();

        boolean areTheyOnTheSameCol = enemyKing.getJ() == piece.getJ();

        if (piece.getType() == F){
            bishopCase(enemyKing, piece, isThereDifferenceInTheNumsOfRowsAndCols, toI, toJ);
        } else if (piece.getType() == B) {
            rookCase(enemyKing, piece, areTheyOnTheSameRow, areTheyOnTheSameCol, toI, toJ);
        } else if (piece.getType() == V){
            bishopCase(enemyKing, piece, isThereDifferenceInTheNumsOfRowsAndCols, toI, toJ);
            rookCase(enemyKing, piece, areTheyOnTheSameRow, areTheyOnTheSameCol, toI, toJ);
        }
        if (toI != -100) {
            tuple.First = toI;
            tuple.Second = toJ;
            tuple.Third = enemyKing;
        }
        return tuple;
    }

    private void bishopCase(Piece enemyKing, IPiece piece, boolean possibility, int toI, int toJ){
        if (possibility){
            toI = enemyKing.getI() - piece.getI() > 0 ? 1 : -1;
            toJ = enemyKing.getJ() - piece.getJ() > 0 ? 1 : -1;
        }
    }

    private void rookCase(Piece enemyKing, IPiece piece, boolean sameRow, boolean sameCol, int toI, int toJ){
        if (sameRow){
            toI = 0;
            toJ = enemyKing.getJ() - piece.getJ() > 0 ? 1 : -1;
        } else if (sameCol) {
            toI = enemyKing.getI() - piece.getI() > 0 ? 1 : -1;
            toJ = 0;
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
                getKing(my).setPossibleRange(l);

                //getKing(my).setLegals(
                //        new Move_(
                //                this,
                //                getKing(my),
                //                getKingsPlace(my),
                //                possiblePlaceOfMyKing,
                //                hitOnThisLocWith(my, possiblePlaceOfMyKing)
                //        )
                //);
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
            Location bigCastlePointLocation = getKingsPlace(forWhite).add(new Location(getKingsPlace(forWhite).getI(), bigCastlePointPlus));
            Location bigCastleRoadLocation = getKingsPlace(forWhite).add(new Location(getKingsPlace(forWhite).getI(), bigCastleRoadPlus));

            int smallCastlePointPlus = (whiteDown ? 2 : -2);
            int smallCastleRoadPlus = (whiteDown ? 1 : -1);
            Location smallCastlePointLocation = getKingsPlace(forWhite).add(new Location(getKingsPlace(forWhite).getI(), smallCastlePointPlus));
            Location smallCastleRoadLocation = getKingsPlace(forWhite).add(new Location(getKingsPlace(forWhite).getI(), smallCastleRoadPlus));

            castleHelper(
                    forWhite,
                    bigCastlePointLocation,
                    bigCastleRoadLocation,
                    smallCastlePointLocation,
                    smallCastleRoadLocation,
                    getKing(forWhite).isWhite() ? whiteBigCastleEnabled : blackBigCastleEnabled,
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
                              boolean whiteSmallCastleEnabled) {
        if (
                bigCastleEnabled &&
                !locationCollectionContains(getAttackRangeWithoutKing(!forWhite), bigCastleRoadLocation) &&
                !locationCollectionContains(getAttackRangeWithoutKing(!forWhite), bigCastlePointLocation) &&
                enemyKingNotInNeighbour(bigCastleRoadLocation, forWhite) &&
                enemyKingNotInNeighbour(bigCastlePointLocation, forWhite)
        ) {
            getKing(forWhite).setPossibleRange(bigCastlePointLocation);
        }

        if (
                whiteSmallCastleEnabled &&
                !locationCollectionContains(getAttackRangeWithoutKing(!forWhite), smallCastleRoadLocation) &&
                !locationCollectionContains(getAttackRangeWithoutKing(!forWhite), smallCastlePointLocation) &&
                enemyKingNotInNeighbour(smallCastleRoadLocation, forWhite) &&
                enemyKingNotInNeighbour(smallCastlePointLocation, forWhite)
        ) {
            getKing(forWhite).setPossibleRange(smallCastlePointLocation);
        }
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

    private IPiece hitOnThisLocWith(boolean my, Location possiblePlaceOfMyKing) throws ChessGameException {
        if (getKing(my).isTherePiece(possiblePlaceOfMyKing) && getPiece(possiblePlaceOfMyKing).isWhite() != my)
            return getPiece(possiblePlaceOfMyKing);
        else
            return null;
    }

    //endregion

    private Set<Location> getAttackRangeWithoutKing(boolean forWhite) {
        return pieces.stream()
                .filter(p -> (p.getType() != K && p.isWhite() == forWhite))
                .flatMap(p -> (p.getType() == G ? ((Piece) p).getWatchedRange() : p.getPossibleRange()).stream())
                .collect(Collectors.toSet());
    }

    public void addLegalMovesToPieces() throws ChessGameException {
        for (IPiece p : myPieces()) {
            for (Location to : p.getPossibleRange()) {
                if (((Piece) p).getLegalMoves().isEmpty()) {
                    ((Piece) p).setLegals(new Move_(
                            this,
                            p,
                            p.getLocation(),
                            to,
                            notNull(getPiece(to)) ? getPiece(to) : null
                    ));
                }else {
                    if (((Piece) p).getLegalMoves().stream().noneMatch(m -> m.getTo().EQUALS(to))){
                        ((Piece) p).setLegals(new Move_(
                                this,
                                p,
                                p.getLocation(),
                                to,
                                notNull(getPiece(to)) ? getPiece(to) : null
                        ));
                    }
                }
            }
        }
    }

    public HashMap<IPiece, Set<Move_>> getAllLegalMoves(boolean forWhite){
        HashMap<IPiece, Set<Move_>> legals = new HashMap<>();
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
