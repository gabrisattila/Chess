package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import lombok.*;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;
import static classes.Game.I18N.PieceType.*;

@Getter
@Setter
public class Move {

    //region Fields

    private IPiece what;

    private Location from;

    private Location to;

    private IPiece hit;

    private String emPassantOrCastle;

    private IBoard boardToMoveOn;

    //endregion


    //region Constructor

    public Move(){
        what = null;
        from = null;
        to = null;
        hit = null;
        emPassantOrCastle = "-";
    }

    public Move(IPiece what, Location from, Location to){
        this.what = what;
        this.from = from;
        this.to = to;
        hit = null;
        emPassantOrCastle = "-";
    }

    public Move(IPiece what, Location from, Location to, IPiece hit){
        this.what = what;
        this.from = from;
        this.to = to;
        this.hit = hit;
        emPassantOrCastle = "-";
    }

    public Move(IPiece what, Location from, Location to, String emPassantOrCastle){
        this.what = what;
        this.from = from;
        this.to = to;
        hit = null;
        this.emPassantOrCastle = emPassantOrCastle;
    }

    public Move(IBoard boardToMoveOn){
        this.boardToMoveOn = boardToMoveOn;
    }

    public Move(IBoard boardToMoveOn, IPiece what, Location from, Location to, IPiece hit){
        this.boardToMoveOn = boardToMoveOn;
        this.what = what;
        this.from = from;
        this.to = to;
        this.hit = hit;
        emPassantOrCastle = "-";
    }

    public Move(IBoard boardToMoveOn, IPiece what, Location from, Location to, String emPassantOrCastle){
        this.boardToMoveOn = boardToMoveOn;
        this.what = what;
        this.from = from;
        this.to = to;
        hit = null;
        this.emPassantOrCastle = emPassantOrCastle;
    }

    //endregion


    //region Methods

    public void pieceChangeOnBoard(){
        IField fromField = boardToMoveOn.getField(from);
        IField toField = boardToMoveOn.getField(to);
        boolean itsEmPassant = isItEmPassant(what, from, to);
        boolean itsCastle = isItCastle(what, from, to);
        try {
            toField.setPiece(what);
            fromField.clean();
            ifItsCastle(itsCastle, toField);
            changeToPlay();
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isItEmPassant(IPiece piece, Location from, Location to){
        return piece.getType() == G && from.getI() != to.getI() && !boardToMoveOn.getField(to).isGotPiece();
    }

    private boolean isItCastle(IPiece piece, Location from, Location to){
        return piece.getType() == K && Math.abs(from.getJ() - to.getJ()) > 1;
    }

    private void ifItsCastle(boolean itsCastle, IField to) throws ChessGameException {
        if (itsCastle) {
            castleCase(to);
        }
    }

    private void castleCase(IField to) throws ChessGameException {
        for (int j = to.getJ() - 2; j < to.getJ() + 3; j++) {
            if (
                    containsLocation(to.getI(), j) && j != to.getJ() &&
                    boardToMoveOn.getField(to.getI(), j).isGotPiece() &&
                    boardToMoveOn.getPiece(to.getI(), j).getType() == B
            ){
                IPiece rook = boardToMoveOn.getPiece(to.getI(), j);
                if (possibleToCastleWithRook(rook))
                    castleHelper(Math.abs(rook.getJ() - to.getJ()) > 1, rook, j);
            }
        }
    }

    private boolean possibleToCastleWithRook(IPiece rook) {
        if (rook.isWhite()){
            return Math.abs(rook.getJ() - to.getJ()) > 1 ? !whiteSmallCastleHappened : !whiteBigCastleHappened;
        }else {
            return Math.abs(rook.getJ() - to.getJ()) > 1 ? !blackSmallCastleHappened : !blackBigCastleHappened;
        }
    }


    public void emPassantCase(IPiece piece, IField from, IField to){

    }


    private void castleHelper(boolean plusOrMinusSideOfKing, IPiece rookToCastleWith, int originJofRook) throws ChessGameException {
        IField fieldForRook;
        if (plusOrMinusSideOfKing){
            fieldForRook = boardToMoveOn.getField(to.getI(), to.getJ() + 1);
        }else {
            fieldForRook = boardToMoveOn.getField(to.getI(), to.getJ() - 1);
        }
        if (fieldForRook.isGotPiece())
            throw new RuntimeException(
                    "Valamilyen bábu van ott, ahová a bástya kerülne sánc után." + fieldForRook.toSString()
            );
        fieldForRook.setPiece(rookToCastleWith);
        boardToMoveOn.getField(to.getI(), originJofRook).clean();
    }

    //endregion

}
