package classes.Game.Model.Structure;


import classes.Game.I18N.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;


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

    private Pair<Boolean, String> thereWasEmPassantPossibility;

    private Move lastMove;

    //endregion


    //region Constructor

    private Board(int x, int y) throws ChessGameException {
        X = x;
        Y = y;
        fields = new ArrayList<>();
        boardSetUp(this, fields);
        pieces = new ArrayList<>();
        whiteKing = new Piece();
        blackKing = new Piece();
        lastMove = new Move(this);
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
    public IPiece getPiece(IField field){
        return getField(field.getI(), field.getJ()).getPiece();
    }

    public Piece getKing(boolean whiteNeeded){
        return whiteNeeded ? whiteKing : blackKing;
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
        pseudoLegals();
        constrainPseudos();
        kingsRanges();
    }

    private void pseudoLegals() throws ChessGameException {
        for (IPiece p : pieces) {
            if (p.getType() != K){
                p.updateRange();
                for (Location l : p.getPossibleRange()) {
                    ((Piece) p).setLegals(
                            new Move(
                                    this,
                                    p,
                                    ((Piece) p).getLocation(),
                                    l,
                                    notNull(getPiece(l)) ? getPiece(l) : null
                            )
                    );
                }
            }
        }
    }

    private void constrainPseudos() throws ChessGameException {
        //Leszűkítés annak függvényében, hogy valamely lépéssel sakkba kerülne az ellenfél vagy én
        constrainTheCalculatedPseudoLegals(!whiteToPlay());
        constrainTheCalculatedPseudoLegals(whiteToPlay());
    }

    private void kingsRanges() throws ChessGameException {
        //A királyok lépéslehetőségeinek leszűkítése a már meghatározott rangek függvényében
        kingSimpleMoves(!whiteToPlay());
        kingSimpleMoves(whiteToPlay());
        //Sáncok lehetőségeink számbavétele
        // Ellenőrizni, hogy van-e lehetőség erre-arra a sáncra
        // Ha igen megnézni, hogy a sánccal megtett király mezők közül bele esik-e valamelyik az ellenfél range-be.
        kingCastle(!whiteToPlay());
        kingCastle(whiteToPlay());
    }

    private void constrainTheCalculatedPseudoLegals(boolean forWhite) throws ChessGameException {
        var set = setBinding(forWhite);

        for (var v : set){
            cleanRangeIfPieceInBinding(v.First, v.Second, v.Third);
        }
    }

    private void cleanRangeIfPieceInBinding(IPiece piece, Set<Location> binding, IPiece checker){

        if (collectionContains(((Piece) piece).getAttackRange(), ((Piece) checker).getLocation()))
            binding.add(((Piece) checker).getLocation());

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

    private Set<IPiece> getTisztek(boolean forWhite) {
        return pieces.stream().filter(p -> p.isWhite() == forWhite && p.getType() != G && p.getType() != K).collect(Collectors.toSet());
    }


    private Set<Move> getLegalMoves(boolean forWhite) {
        return pieces.stream()
                .filter(p -> (p.getType() != K && p.isWhite() == forWhite))
                .flatMap(p -> ((Piece) p).getLegalMoves().stream())
                .collect(Collectors.toSet());
    }

    private void kingSimpleMoves(boolean forWhite) throws ChessGameException {
        getKing(forWhite).setPossibleRange(new HashSet<>());
        for (Location l : matrixChooser.get(K)) {
            Location possiblePlaceOfMyKing = l.add(getKing(forWhite).getLocation());
            if (containsLocation(possiblePlaceOfMyKing) &&
                    (!getField(possiblePlaceOfMyKing).isGotPiece() || getPiece(possiblePlaceOfMyKing).isWhite() != forWhite)){
                Move myKingMove = new Move(
                        this,
                        getKing(forWhite),
                        getKingsPlace(forWhite),
                        possiblePlaceOfMyKing,
                        hitOnThisLocWith(forWhite, possiblePlaceOfMyKing)
                );
                if (enemyKingNotInNeighbour(possiblePlaceOfMyKing, forWhite) &&
                        !attackRangeWithoutKing(!forWhite).contains(possiblePlaceOfMyKing)){
                    getKing(forWhite).setLegals(myKingMove);
                }
            }
        }
    }

    private void kingCastle(boolean forWhite) throws ChessGameException {
        if (!whiteAiNeeded || !theresOnlyOneAi){
            if (forWhite){
                checkCastleOptions(
                        new Location(0, 6),
                        new Location(0, 5),
                        ((Piece)getPiece(0, 7)).isNotMovedAlready(),
                        new Location(0, 2),
                        new Location(0, 3),
                        ((Piece)getPiece(0, 0)).isNotMovedAlready(),
                        true,
                        true
                );
            }else {
                checkCastleOptions(
                        new Location(7, 6),
                        new Location(7, 5),
                        ((Piece)getPiece(7, 7)).isNotMovedAlready(),
                        new Location(7, 2),
                        new Location(7, 3),
                        ((Piece)getPiece(7, 0)).isNotMovedAlready(),
                        false,
                        true
                );
            }
        }else {
            if (forWhite){
                checkCastleOptions(
                        new Location(7, 1),
                        new Location(7, 2),
                        ((Piece)getPiece(7, 0)).isNotMovedAlready(),
                        new Location(7, 5),
                        new Location(7, 4),
                        ((Piece)getPiece(7, 7)).isNotMovedAlready(),
                        true,
                        false
                );
            }else {
                checkCastleOptions(
                        new Location(0, 1),
                        new Location(0, 2),
                        ((Piece)getPiece(7, 0)).isNotMovedAlready(),
                        new Location(0, 5),
                        new Location(0, 4),
                        ((Piece)getPiece(7, 7)).isNotMovedAlready(),
                        false,
                        false
                );
            }
        }
    }

    private void checkCastleOptions(Location kingSidePoint, Location kingSideRoad, boolean kingRookIsNotMoved,
                                    Location queenSidePoint, Location queenSideRoad, boolean queenRookIsNotMoved,
                                    boolean forWhite, boolean whiteDown){

        if (kingRookIsNotMoved){
            addCastleMove(kingSidePoint, kingSideRoad, forWhite, true);
        }
        if (queenRookIsNotMoved){
            addCastleMove(
                    queenSidePoint, queenSideRoad, forWhite,
                    whiteDown ? !getField(forWhite ? 0 : 7, 1).isGotPiece() : !getField(forWhite ? 0 : 7, 6).isGotPiece());
        }
    }

    private void addCastleMove(Location Point, Location Road, boolean forWhite, boolean queenOrKingSideRook){
        if (
                !getField(Point).isGotPiece() && !getField(Road).isGotPiece() &&
                !attackRangeWithoutKing(!forWhite).contains(Point) && !attackRangeWithoutKing(!forWhite).contains(Road) &&
                enemyKingNotInNeighbour(Point, forWhite) && enemyKingNotInNeighbour(Road, forWhite) &&
                queenOrKingSideRook
        ){
            getKing(forWhite).setLegals(new Move(
                    this,
                    getKing(forWhite),
                    getKingsPlace(forWhite),
                    Point,
                    forWhite ? (queenOrKingSideRook ? "Q" : "K") : (queenOrKingSideRook ? "q" : "k")
            ));
        }
    }

    /**
     * @param placeToCheck A location amit ellenőrizni akarok
     * @param forWhite A saját királyom színe, ennek az ellentétét ellenőrzöm
     * @return Azt nézi meg, hogy az ellenfél király a kapott lokáció közelében van-e
     */
    private boolean enemyKingNotInNeighbour(Location placeToCheck, boolean forWhite) {
        return IntStream.rangeClosed(placeToCheck.getI() - 1, placeToCheck.getI() + 1)
                .noneMatch(i ->
                        IntStream.rangeClosed(placeToCheck.getJ() - 1, placeToCheck.getJ() + 1)
                                .anyMatch(j ->
                                        getKingsPlace(!forWhite).EQUALS(new Location(i, j))));
    }

    private Set<Location> attackRangeWithoutKing(boolean forWhite) {
        return pieces.stream()
                .filter(p -> (p.getType() != K && p.isWhite() == forWhite))
                .flatMap(p -> (p.getType() == G ? ((Piece) p).getWatchedRange() :  p.getPossibleRange()).stream())
                .collect(Collectors.toSet());
    }

    private IPiece hitOnThisLocWith(boolean my, Location possiblePlaceOfMyKing) throws ChessGameException {
        if (getKing(my).isTherePiece(possiblePlaceOfMyKing) && getPiece(possiblePlaceOfMyKing).isWhite() != my)
            return getPiece(possiblePlaceOfMyKing);
        else
            return null;
    }

    //endregion

}
