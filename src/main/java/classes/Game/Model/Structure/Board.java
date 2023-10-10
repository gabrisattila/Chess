package classes.Game.Model.Structure;


import classes.Game.I18N.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
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

    private Pair<Boolean, String> thereWasEmPassant;

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
    public IField getField(Location location){
        return getField(location.getI(), location.getJ());
    }

    @Override
    public IField getField(IPiece piece){
        return getField(piece.getI(), piece.getJ());
    }

    @Override
    public IPiece getPiece(int i, int j){
        for (IPiece p : pieces) {
            if (p.getI() == i && p.getJ() == j)
                return p;
        }
        throw new RuntimeException("Nincs ilyen figura.\n");
    }

    @Override
    public IPiece getPiece(Location location){
        return getPiece(location.getI(), location.getJ());
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




    public void rangeUpdater() throws ChessGameException {
        pseudoLegals();
        tightenPseudos();
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
                                    getPiece(l)
                            )
                    );
                }
            }
        }
    }

    private void tightenPseudos() {
        //Leszűkítés annak függvényében, hogy valamely lépéssel sakkba kerülne az ellenfél vagy én
        tightenTheCalculatedPseudoLegals(!whiteToPlay);
        tightenTheCalculatedPseudoLegals(whiteToPlay);
    }

    private void kingsRanges(){
        //A királyok lépéslehetőségeinek leszűkítése a már meghatározott rangek függvényében
        kingSimpleMoves(!whiteToPlay);
        kingSimpleMoves(whiteToPlay);
        //Sáncok lehetőségeink számbavétele
        // Ellenőrizni, hogy van-e lehetőség erre-arra a sáncra
        // Ha igen megnézni, hogy a sánccal megtett király mezők közül bele esik-e valamelyik az ellenfél range-be.

    }

    private void tightenTheCalculatedPseudoLegals(boolean forWhite) {
        var set = setBinding(white);

        for (int i = 0; i < set.size(); i++) {
            cleanRangeIfPieceInBinding(set.get(i).First, set.get(i).Second, set.get(i).Third);
        }
    }

    private void cleanRangeIfPieceInBinding(Piece piece, SET<Location> binding, Piece checker){

        if (containsLocation(piece.getAttackRange(), checker.getLocation()))
            binding.add(checker.getLocation());

        piece.getPossibleRange().removeIf(l -> notContainsLocation(binding, l));

    }

    private SET<Tuple<Piece, SET<Location>, Piece>> setBinding(boolean white){

        Piece ownPiece = null;
        int ownPieceCounter = 0;

        SET<Tuple<Piece, SET<Location>, Piece>> set = new SET<>();

        for (Piece p : getTisztek(!white)) {
            if (!p.isClean()){
                if (getAddIAddJ(p, this, white ? WHITESTRING : BLACKSTRING).First != -100) {
                    for (Location location : lineToTheKing(p, this, white ? WHITESTRING : BLACKSTRING)) {
                        if (!isNull(getPieceByLocationFromBoard(location))) {
                            if (getPieceByLocationFromBoard(location).isWhite() != white)
                                break;
                            if (getPieceByLocationFromBoard(location).getType() == PieceType.K)
                                break;
                            else {
                                ownPiece = getPieceByLocationFromBoard(location);
                                ownPieceCounter++;
                            }
                            if (ownPieceCounter > 1)
                                break;
                        }
                    }
                    if (ownPieceCounter == 1) {
                        for (Piece piece : white ? whitePieces : blackPieces) {
                            if (piece.EQUALS(ownPiece)) {
                                piece.setInBinding(true);
                                set.add(new Tuple<>(
                                        ownPiece,
                                        lineToTheKing(p, this, white ? WHITESTRING : BLACKSTRING),
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

    public static Set<Location> lineToTheKing(Piece piece, IBoard table, String whichKingNeeded){
        Set<Location> line = new HashSet<>();
        if (piece.getType() != PieceType.H && piece.getType() != PieceType.G){
            int i = piece.getI(), j = piece.getJ();

            Tuple<Integer, Integer, Piece> getAddIJ = new Tuple<>(getAddIAddJ(piece, table, whichKingNeeded));
            int addToI = getAddIJ.First, addToJ = getAddIJ.Second;
            i += addToI;
            j += addToJ;
            if (i != piece.getI() || j != piece.getJ()){
                while (containsLocation(i, j) && table.getPiece(i, j) != getAddIJ.Third) {
                    line.add(new Location(i, j));
                    i += addToI;
                    j += addToJ;
                }
            }
        }
        return line;
    }

    public Tuple<Integer, Integer, Piece> getAddIAddJ(Piece piece, String whichKingNeeded){

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

    private void bishopCase(Piece enemyKing, Piece piece, boolean possibility, int toI, int toJ){
        if (possibility){
            toI = enemyKing.getI() - piece.getI() > 0 ? 1 : -1;
            toJ = enemyKing.getJ() - piece.getJ() > 0 ? 1 : -1;
        }
    }

    private void rookCase(Piece enemyKing, Piece piece, boolean sameRow, boolean sameCol, int toI, int toJ){
        if (sameRow){
            toI = 0;
            toJ = enemyKing.getJ() - piece.getJ() > 0 ? 1 : -1;
        } else if (sameCol) {
            toI = enemyKing.getI() - piece.getI() > 0 ? 1 : -1;
            toJ = 0;
        }
    }
    
    private Set<Move> getLegalMoves(boolean forWhite) {
        return pieces.stream()
                .filter(p -> (p.getType() != K && p.isWhite() == forWhite))
                .flatMap(p -> ((Piece) p).getLegalMoves().stream())
                .collect(Collectors.toSet());
    }

    private void kingSimpleMoves(boolean my){
        for (Location l : matrixChooser.get(K)) {
            Location possiblePlaceOfMyKing = l.add(getKing(my).getLocation());
            Move myKingMove = new Move(
                    this,
                    getKing(!my),
                    getKingsPlace(!my),
                    possiblePlaceOfMyKing,
                    getPiece(possiblePlaceOfMyKing)
            );
            if (enemyKingNotInNeighbour(possiblePlaceOfMyKing) &&
                    !attackRangeWithoutKing(!my).contains(possiblePlaceOfMyKing)){
                getKing(my).setLegals(myKingMove);
            }
        }
    }

    private boolean enemyKingNotInNeighbour(Location placeOfMyKing) {
        return IntStream.rangeClosed(placeOfMyKing.getI() - 1, placeOfMyKing.getI() + 1)
                .noneMatch(i ->
                        IntStream.rangeClosed(placeOfMyKing.getJ() - 1, placeOfMyKing.getJ() + 1)
                                .anyMatch(j ->
                                        getKingsPlace(!whiteToPlay).EQUALS(new Location(i, j))));
    }

    private Set<Location> attackRangeWithoutKing(boolean forWhite) {
        return pieces.stream()
                .filter(p -> (p.getType() != K && p.isWhite() == forWhite))
                .flatMap(p -> (p.getType() == G ? ((Piece) p).getWatchedRange() :  p.getPossibleRange()).stream())
                .collect(Collectors.toSet());
    }

    //endregion

}
