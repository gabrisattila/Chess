package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import lombok.*;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;
import static classes.Game.I18N.PieceType.*;
import static classes.GUI.Frame.Window.*;

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
//            ifItsEmPassant(itsEmPassant, toField);
            logging();
            changeToPlay();
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    private void logging() throws ChessGameException {
        getLogger().append((what.isWhite() ? "White " : "Black ") + what.getType() + " went from " + from.toString() + " to " + to.toString() + '\n');
        System.out.println();
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
                boolean bigOrSmallCastle = Math.abs(rook.getJ() - to.getJ()) > 1;
                if (possibleToCastleWithRook(rook, bigOrSmallCastle)) {

                    castleHelper(bigOrSmallCastle, rook, j);

                    //Mert egyik megtörténte után nem történhet a másik
                    if (bigOrSmallCastle){
                        if (what.isWhite()){
                            whiteSmallCastleHappened = true;
                            whiteBigCastleHappened = true;
                        }else {
                            blackBigCastleHappened = true;
                            blackSmallCastleHappened = true;
                        }
                    }

                    break;
                }
            }
        }
    }

    private boolean possibleToCastleWithRook(IPiece rook, boolean bigOrSmallCastle) {
        if (rook.isWhite()){
            return bigOrSmallCastle ? !whiteSmallCastleHappened : !whiteBigCastleHappened;
        }else {
            return bigOrSmallCastle ? !blackSmallCastleHappened : !blackBigCastleHappened;
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

    private boolean isItEmPassant(IPiece piece, Location from, Location to){
        return piece.getType() == G && from.getI() != to.getI() && !boardToMoveOn.getField(to).isGotPiece();
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


    //endregion

}
