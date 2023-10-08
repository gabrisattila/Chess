package classes.Game.Model.Structure;


import classes.Game.I18N.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    public void updatePiecesRanges() throws ChessGameException, InterruptedException {
//        setEnemyInDefendBasedOnWatching();
        for (IPiece p : pieces) {
            p.updateRange();
        }
//        setInDefendBasedOnWatching();
    }

    public Piece getKing(boolean whiteNeeded){
        return whiteNeeded ? whiteKing : blackKing;
    }

    public Set<Location> enemyPawnWatchedRange(boolean imWithWhite){
        Set<Location> range = new HashSet<>();
        for (IPiece p : pieces) {
            if (p.getType() == G && p.isWhite() != imWithWhite) {
                range.addAll( ((Piece) p).getWatchedRange() );
            }
        }
        return range;
    }

    //endregion

}
