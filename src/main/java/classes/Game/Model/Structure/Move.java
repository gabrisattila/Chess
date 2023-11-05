package classes.Game.Model.Structure;

import classes.GUI.FrameParts.ViewBoard;
import classes.GUI.FrameParts.ViewPiece;
import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import lombok.*;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.I18N.PieceType.*;
import static classes.GUI.Frame.Window.*;
import static classes.Game.Model.Structure.Board.*;

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
     * if it made pawnGotIn: p_PieceTypeWeWantToUseLater // H, F, B, V -- AI: by default V
     */
    private String emPassantCastlePawnGotIn;


    private boolean castle;

    private Location castleRookOrigin;

    private Location castleRookTo;

    private IPiece castleRook;


    private boolean emPassant;

    private Location emPassantTakenPawnLoc;

    private IPiece emPassantTakenPawn;


    private boolean pawnGotIn;


    private IBoard boardToMoveOn;

    //endregion


    //region Constructor

    public Move(){
        what = null;
        from = null;
        to = null;
        hit = null;
        emPassantCastlePawnGotIn = "-";
    }

    public Move(IPiece what, Location from, Location to){
        this.what = what;
        this.from = from;
        this.to = to;
        hit = null;
        emPassantCastlePawnGotIn = "-";
        emPassant = false;
        castle = false;
    }

    public Move(IPiece what, Location from, Location to, IPiece hit){
        this.what = what;
        this.from = from;
        this.to = to;
        this.hit = hit;
        emPassantCastlePawnGotIn = "-";
        emPassant = false;
        castle = false;
    }

    public Move(IPiece what, Location from, Location to, String emPassantCastlePawnGotIn){
        this.what = what;
        this.from = from;
        this.to = to;
        hit = null;
        this.emPassantCastlePawnGotIn = emPassantCastlePawnGotIn;
        emPassant = false;
        castle = false;
    }

    public Move(IBoard boardToMoveOn){
        this.boardToMoveOn = boardToMoveOn;
        emPassant = false;
        castle = false;
    }

    public Move(IBoard boardToMoveOn, IPiece what, Location from, Location to, IPiece hit){
        this.boardToMoveOn = boardToMoveOn;
        this.what = what;
        this.from = from;
        this.to = to;
        this.hit = hit;
        emPassantCastlePawnGotIn = "-";
        emPassant = false;
        castle = false;
    }

    public Move(IBoard boardToMoveOn, IPiece what, Location from, Location to, String emPassantCastlePawnGotIn){
        this.boardToMoveOn = boardToMoveOn;
        this.what = what;
        this.from = from;
        this.to = to;
        hit = null;
        this.emPassantCastlePawnGotIn = emPassantCastlePawnGotIn;
        emPassant = false;
        castle = false;
    }

    public Move(IBoard boardToMoveOn, IPiece what, Location from, Location to, IPiece hit, String emPassantCastlePawnGotIn){
        this.boardToMoveOn = boardToMoveOn;
        this.what = what;
        this.from = from;
        this.to = to;
        this.hit = hit;
        this.emPassantCastlePawnGotIn = emPassantCastlePawnGotIn;
        emPassant = false;
        castle = false;
    }

    //endregion


    //region Methods

    //region Move main

    public static void MOVE(IBoard board, IPiece what, Location to) throws ChessGameException {
        Move move = new Move(board);
        if (what.getType() == K || what.getType() == B){
            emPassantChance = "-";
            for (Move m : (board instanceof ViewBoard ? ((Piece) getBoard().getPiece(what.getLocation())) : ((Piece) what)).getLegalMoves() ) {
                if (m.getTo().EQUALS(to)){
                    move.setEveryThing(what, m.getTo(), m.getEmPassantCastlePawnGotIn());
                    break;
                }
            }
        } else if (what.getType() == G &&
                !(board instanceof ViewBoard ?
                        ((Piece) getBoard().getPiece(what.getLocation())) :
                        ((Piece) what))
                        .getLegalMoves().isEmpty() &&
                ((Move)(
                        board instanceof ViewBoard ?
                                ((Piece) getBoard().getPiece(what.getLocation())) :
                                ((Piece) what)).getLegalMoves().toArray()[0]
                ).getTo().EQUALS(to)) {
            emPassantChance = ((Move)((Piece) what).getLegalMoves().toArray()[0]).getEmPassantCastlePawnGotIn();
            move.setEveryThing(what, to, emPassantChance);
        }else {
            emPassantChance = "-";
            move.setEveryThing(what, to);
        }
        move.realMove();
    }

    public static void MOVE(IBoard board, Move move) throws ChessGameException {
        move.setBoardToMoveOn(board);
        move.realMove();
    }

    public void setEveryThing(IPiece what, Location to) throws ChessGameException {
        this.what = what;
        from = what.getLocation();
        this.to = to;
        hit = notNull( boardToMoveOn.getPiece(to)) ? boardToMoveOn.getPiece(to) : null;
        emPassantCastlePawnGotIn = "";
    }

    public void setEveryThing(IPiece what, Location to, String castle) throws ChessGameException {
        this.what = what;
        from = what.getLocation();
        this.to = to;
        hit = notNull(boardToMoveOn.getPiece(to)) ? boardToMoveOn.getPiece(to) : null;
        this.emPassantCastlePawnGotIn = castle;
        boardIsMissing();
    }

    private void boardIsMissing() {
        if (isNull(boardToMoveOn))
            throw new RuntimeException("A tábla amin lépni kellene nincs megadva.");
    }

    public void realMove() throws ChessGameException {
        pieceChangeOnBoard(true);
    }

    public void supposedMove() throws ChessGameException {
        pieceChangeOnBoard(false);
        boardRangeUpdate();
    }

    public void supposedMoveBack() {
        IField fromField = boardToMoveOn.getField(from);
        IField toField = boardToMoveOn.getField(to);
        try {
            fromField.setPiece(what);
            toField.setPiece(hit);
            if (emPassant){
                boardToMoveOn.getField(emPassantTakenPawnLoc).setPiece(emPassantTakenPawn);
                boardToMoveOn.getPieces().add(emPassantTakenPawn);
            } else if (castle){
                boardToMoveOn.getField(castleRookOrigin).setPiece(castleRook);
                boardToMoveOn.getField(castleRookTo).clean();
            }
            boardRangeUpdate();
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    private void pieceChangeOnBoard(boolean itsNotSupposed) throws ChessGameException {
        IField fromField = boardToMoveOn.getField(from);
        IField toField = boardToMoveOn.getField(to);
        emPassant = isItEmPassant();
        castle = isItCastle();
        pawnGotIn = isItPawnGotIn();
        try {
            toField.setPiece(what);
            fromField.clean();
            if (itsNotSupposed) {
                ifItsCastle(castle, toField);
                ifItsEmPassant(emPassant);
                logging();
                changeEvenOrOddStep();
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

    //endregion

    //region EmPassant

    private boolean isItEmPassant() throws ChessGameException {
        if (emPassantChance.isEmpty() && from.getJ() != to.getJ() && hit == null)
            throw new ChessGameException("Elvileg em passant akar lépni, de ez nem lehetséges jelen helyzetben");
        return what.getType() == G && Math.abs(from.getI() - to.getI()) == 1 && Math.abs(from.getJ() - to.getJ()) == 1 && isNull(boardToMoveOn.getPiece(to));
    }

    private void ifItsEmPassant(boolean itsEmPassant) throws ChessGameException {
        if (itsEmPassant)
            emPassantCase();
    }

    private void emPassantCase() throws ChessGameException {
        int i, j;
        i = what.getAttributes().getEnemyAndOwnStartRow().getFirst() == 7 ?
                (containsLocation(getTo().getI() - 1, getTo().getJ()) ? getTo().getI() - 1 : - 1) :
                (containsLocation(getTo().getI() + 1, getTo().getJ()) ? getTo().getI() + 1 : - 1);
        j = getTo().getJ();
        emPassantTakenPawnLoc = new Location(i, j);
        if (i != -1){
            emPassantTakenPawn = boardToMoveOn.getPiece(emPassantTakenPawnLoc);
        }else {
            throw new ChessGameException("Nem Megfelelő helyről akarunk emPassant által figurát levenni.");
        }

        emPassantChance = "-";
    }

    //endregion

    //region Castle

    private boolean isItCastle(){
        return !"".equals(emPassantCastlePawnGotIn) && castleMoveSigns.contains(emPassantCastlePawnGotIn) && what.getType() == K && (what.getI() == 0 || what.getI() == 7);
    }

    private void ifItsCastle(boolean itsCastle, IField to) throws ChessGameException {
        if (itsCastle) {

            if (what.getType() != K && (what.getI() != 0 || what.getI() != 7))
                throw new RuntimeException("Nem megfelelő figura (" + what.getType() + ") próbál sáncolni, vagy a király már elmozdult (jelen helye: " + from.toString() + ") az eredeti helyéről ahonnan tudott volna.");

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

                castleRook = boardToMoveOn.getPiece(to.getI(), j);
                boolean bigOrSmallCastle = Math.abs(castleRook.getJ() - to.getJ()) > 1;

                castleHelper(bigOrSmallCastle, j);
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

    private void castleHelper(boolean bigOrSmallCastle, int originJofRook) throws ChessGameException {
        IField fieldForRook;
        int i, j;
        i = to.getI();
        j = whiteAiNeeded ? (bigOrSmallCastle ? - 1 : 1) : (bigOrSmallCastle ? 1 : - 1);
        j += to.getJ();
        castleRookOrigin = new Location(to.getI(), originJofRook);
        castleRookTo = new Location(i, j);

        fieldForRook = boardToMoveOn.getField(castleRookTo);

        if (fieldForRook.isGotPiece())
            throw new RuntimeException("Valamilyen bábu van ott, ahová a bástya kerülne sánc után." + fieldForRook.toSString());

        fieldForRook.setPiece(castleRook);
        boardToMoveOn.getField(castleRookOrigin).clean();
    }

    //endregion

    //region Pawn Got In

    private boolean isItPawnGotIn(){
        return what.getType() == G && to.getI() == what.getAttributes().getEnemyAndOwnStartRow().getFirst();
    }

    private void ifItIsPawnGotIn(boolean itIsPawnGotIn){
        if (itIsPawnGotIn)
            pawnGotInCase();
    }

    private void pawnGotInCase(){
        if (emPassantCastlePawnGotIn.isEmpty() ||
            emPassantCastlePawnGotIn.charAt(0) != 'c' || emPassantCastlePawnGotIn.charAt(1) != '_'){
            throw new RuntimeException("Nem gyalogra akarunk gyalogbemenetelt végrehajtani");
        }
        char newType = emPassantCastlePawnGotIn.charAt(2);
        what.getAttributes().setType(charToPieceType(newType));
        what.getAttributes().valueSetting();
        if (what instanceof ViewPiece){
            //Icon change
            //TODO Elgondolni mi legyen azzal, hogy lehessen rendesen működik-e majd ez így?
        }
    }

    //endregion

    //endregion

}
