package classes.Game.Model.Structure;

import classes.GUI.FrameParts.ViewBoard;
import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import lombok.*;

import java.util.Objects;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;
import static classes.Game.I18N.PieceType.*;
import static classes.GUI.Frame.Window.*;
import static classes.Game.Model.Structure.Board.getBoard;

@Getter
@Setter
public class Move {

    //region Fields

    private IPiece what;

    private Location from;

    private Location to;

    private IPiece hit;

    /**
     * if it was castle: Y (for white) y (for black)
     * if it made emPassant chance: xy coordinate of the possible emPassant place
     */
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

    public Move(IBoard boardToMoveOn, IPiece what, Location from, Location to, IPiece hit, String emPassantOrCastle){
        this.boardToMoveOn = boardToMoveOn;
        this.what = what;
        this.from = from;
        this.to = to;
        this.hit = hit;
        this.emPassantOrCastle = emPassantOrCastle;
    }

    //endregion


    //region Methods

    public static void MOVE(IBoard board, IPiece what, Location to) throws ChessGameException {
        Move move = new Move(board);
        if (what.getType() == K || what.getType() == B || what.getType() == G){
            for (Move m : (board instanceof ViewBoard ? ((Piece) getBoard().getPiece(what.getLocation())) : ((Piece) what)).getLegalMoves() ) {
                if (m.getTo().EQUALS(to)){
                    move.setEveryThing(what, m.getTo(), m.getEmPassantOrCastle());
                    break;
                }
            }
        }else {
            move.setEveryThing(what, to);
        }
        move.realMove();
    }

    public void setEveryThing(IPiece what, Location to) throws ChessGameException {
        this.what = what;
        from = what.getLocation();
        this.to = to;
        hit = notNull(boardToMoveOn.getPiece(to)) ? boardToMoveOn.getPiece(to) : null;
        emPassantOrCastle = "";
    }

    public void setEveryThing(IPiece what, Location to, String emPassantOrCastle) throws ChessGameException {
        this.what = what;
        from = what.getLocation();
        this.to = to;
        hit = notNull(boardToMoveOn.getPiece(to)) ? boardToMoveOn.getPiece(to) : null;
        this.emPassantOrCastle = emPassantOrCastle;
        boardIsMissing();
    }

    public void realMove(){
        pieceChangeOnBoard(true);
    }


    public void supposedMove() throws ChessGameException {
        pieceChangeOnBoard(false);
        boardRangeUpdate();
    }

    public void supposedMoveBack() throws ChessGameException {
        IField fromField = boardToMoveOn.getField(from);
        IField toField = boardToMoveOn.getField(to);
        try {
            fromField.setPiece(what);
            toField.setPiece(hit);
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
        boardRangeUpdate();
    }

    private void pieceChangeOnBoard(boolean itsNotSupposed){
        IField fromField = boardToMoveOn.getField(from);
        IField toField = boardToMoveOn.getField(to);
        boolean itsEmPassant = isItEmPassant();
        boolean itsCastle = isItCastle();
        try {
            toField.setPiece(what);
            fromField.clean();
            if (itsNotSupposed) {
                ifItsCastle(itsCastle, toField);
//            ifItsEmPassant(itsEmPassant, toField);
                logging();
                changeToPlay();
            }
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    private void boardRangeUpdate() throws ChessGameException {
        ((Board) boardToMoveOn).pseudos();
        ((Board) boardToMoveOn).constrainPseudos();
    }
    
    private void logging() throws ChessGameException {
        getLogger().append((what.isWhite() ? "White " : "Black ") + what.getType() + " went from " + from.toString() + " to " + to.toString() + '\n');
    }

    private boolean isItCastle(){
        return !"".equals(emPassantOrCastle) && castleMoveSigns.contains(emPassantOrCastle);
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
                boolean bigOrSmallCastle = Math.abs(rook.getJ() - to.getJ()) > 1;

                castleHelper(bigOrSmallCastle, rook, j);
                //Mert egyik megtörténte után nem történhet a másik
                if (what.isWhite()){
                    whiteSmallCastleEnabled = false;
                    whiteBigCastleEnabled = false;
                }else {
                    blackBigCastleEnabled = false;
                    blackSmallCastleEnabled = false;
                }
                break;
            }
        }
    }

    private boolean possibleToCastleWithRook(IPiece rook, boolean bigOrSmallCastle) {
        if (rook.isWhite()){
            return bigOrSmallCastle ? whiteSmallCastleEnabled : whiteBigCastleEnabled;
        }else {
            return bigOrSmallCastle ? blackSmallCastleEnabled : blackBigCastleEnabled;
        }
    }

    private void castleHelper(boolean bigOrSmallCastle, IPiece rookToCastleWith, int originJofRook) throws ChessGameException {
        IField fieldForRook;
        if (whiteAiNeeded){
            if (bigOrSmallCastle){
                fieldForRook = boardToMoveOn.getField(to.getI(), to.getJ() - 1);
            }else {
                fieldForRook = boardToMoveOn.getField(to.getI(), to.getJ() + 1);
            }
        }else {
            if (bigOrSmallCastle){
                fieldForRook = boardToMoveOn.getField(to.getI(), to.getJ() + 1);
            }else {
                fieldForRook = boardToMoveOn.getField(to.getI(), to.getJ() - 1);
            }
        }
        if (fieldForRook.isGotPiece())
            throw new RuntimeException(
                    "Valamilyen bábu van ott, ahová a bástya kerülne sánc után." + fieldForRook.toSString()
            );
        fieldForRook.setPiece(rookToCastleWith);
        boardToMoveOn.getField(to.getI(), originJofRook).clean();
    }

    private boolean isItEmPassant(){
        return !"".equals(emPassantOrCastle) && nums.contains(emPassantOrCastle.charAt(0)) && nums.contains(emPassantOrCastle.charAt(1));
    }

    private void ifItsEmPassant(boolean itsEmPassant, IField toField) throws ChessGameException {
        if (itsEmPassant)
            emPassantCase(toField);
    }

    private void emPassantCase(IField to) throws ChessGameException {
        if (to.getPiece().getAttributes().getEnemyAndOwnStartRow().getFirst() == 7){
            boardToMoveOn.getField(getTo().getI() - 1, getTo().getJ()).clean();
        }else {
            boardToMoveOn.getField(getTo().getI() + 1, getTo().getJ()).clean();
        }
    }

    private void boardIsMissing() {
        if (isNull(boardToMoveOn))
            throw new RuntimeException("A tábla amin lépni kellene nincs megadva.");
    }

    //endregion

}
