package classes.Game.Model.Structure;

import classes.Game.I18N.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static classes.Game.I18N.Location.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

@Getter
@Setter
public class Move {

    //region Fields

    private IPiece what;

    private Location from;

    private Location to;

    private IBoard boardToMoveOn;


    /**
     * This is a mimic of the plusPiece, that was used in the move.
     * If there wasn't such, then it's null.
     * Else the first pair means isWhite and pieceType
     *      and the second pair means from to. (If it was taken, then to is null)
     */
    private Pair<Pair<Boolean, PieceType>, Pair<Location, Location>> plusPiece;


    private boolean itIsCastle;

    private boolean itIsEmPassant;
    
    private boolean itIsEmPassantAuthorization;

    private boolean itIsPawnGotIn;


    private String moveDocString;

    //endregion


    //region Constructor

    public Move(){}

    public Move(IPiece what, Location to){
        this.what = what;
        from = what.getLocation();
        this.to = to;
        parameterizeToDefault();
    }

    public Move(IBoard boardToMoveOn){
        this.boardToMoveOn = boardToMoveOn;
        parameterizeToDefault();
    }

    public Move(IPiece what, Location to, IBoard boardToMoveOn){
        this.what = what;
        from = what.getLocation();
        this.to = to;
        this.boardToMoveOn = boardToMoveOn;
        parameterizeToDefault();
    }

    public void parameterize(IBoard boardToMoveOn){
        this.boardToMoveOn = boardToMoveOn;
        parameterizeToDefault();
    }

    public void parameterize(IPiece what, Location to){
        this.what = what;
        from = what.getLocation();
        this.to = to;
        parameterizeToDefault();
    }

    public void parameterize(IPiece what, Location to, IBoard boardToMoveOn){
        this.what = what;
        from = what.getLocation();
        this.to = to;
        this.boardToMoveOn = boardToMoveOn;
        parameterizeToDefault();
    }

    private void parameterizeToDefault(){
        itIsCastle = false;
        itIsEmPassant = false;
        itIsEmPassantAuthorization = false;
        itIsPawnGotIn = false;
        moveDocString = "";
    }

    //endregion


    //region Methods

    public static void Step(Move move) throws ChessGameException {
        
        String moveCase = move.detectSpecialCase();
        if ("castle".equals(moveCase)){
            move.setItIsCastle(true);
        } else if ("emPassant".equals(moveCase)) {
            move.setItIsEmPassant(true);
        } else if ("pawnGotIn".equals(moveCase)) {
            move.setItIsPawnGotIn(true);
        }
        
        move.collectPlusPiece();
        move.pieceChangeOnBoard();
        move.doAfterChangeEffects();
        
    }

    public static void StepBack(Move move){
        if (move.getMoveDocString().isEmpty())
            //TODO Megírni szépen a honnan hovát (valami e4-ről ...)
            throw new RuntimeException("A " + move.what + " bábu " + move.to.toString() +
                    " -re készülő lépéséhez nem tartozik docString" +
                    " ami alapján meg lehetne tenni a visszalépést." );
    }

    private void collectPlusPiece() throws ChessGameException {
        plusPiece = new Pair<>();
        
        if (itIsCastle){

            IPiece neededRook;

            if (Math.abs(to.getJ()) < Math.abs(7 - to.getJ()))
                neededRook = boardToMoveOn.getPiece(to.getI(), 0);
            else
                neededRook = boardToMoveOn.getPiece(to.getI(), 7);


            int rookRoadLength = to.getJ() - neededRook.getJ();

            if (rookRoadLength < 0)
                rookRoadLength--;
            else
                rookRoadLength++;

            plusPiece.setFirst(new Pair<>(
                    what.isWhite(), B
            ));

            plusPiece.setSecond(new Pair<>(
                    new Location(neededRook.getLocation()),
                    new Location(neededRook.getI(), neededRook.getJ() + rookRoadLength)
            ));

        } else if (itIsEmPassant) {
            
            int plusI = what.isWhite() ? (whiteDown ? -1 : 1) : (whiteDown ? 1 : -1);
            
            plusPiece.setFirst(
                    new Pair<>(!what.isWhite(), G)
            );
            
            plusPiece.setSecond(
                    new Pair<>(new Location(to.getI() + plusI, to.getJ()), null)
            );
                        
        } else if (notNull(boardToMoveOn.getPiece(to))) {
            
            IPiece taken = boardToMoveOn.getPiece(to);
            
            plusPiece.setFirst(
                    new Pair<>(taken.isWhite(), taken.getType())
            );
            plusPiece.setSecond(
                    new Pair<>(taken.getLocation(), null)
            );
            
        }else {
            plusPiece = null;
        }
    }
    
    private void pieceChangeOnBoard(){
        
    }

    private void doAfterChangeEffects(){

    }

    /**
     * @return "castle" if the move was castle, "emPassant" if the move was emPassant,
     *         "emPassantAut" if the move enables future emPassant
     *         "pawnGotIn" if the move was pawnGotIn
     */
    private String detectSpecialCase(){

        if (what.getType() == K && Math.abs(from.getJ() - to.getJ()) == 2){
            return "castle";
        } else if (what.getType() == G && to.EQUALS(stringToLocation(emPassantChance))) {
            return "emPassant";
        } else if (emPassantAuthorizationIf()) {
            return "emPassantAut";
        } else if (what.getType() == G && to.getI() == what.getEnemyStartRow()) {
            return "pawnGotIn";
        } else {
            return "";
        }

    }

    private boolean emPassantAuthorizationIf(){
        return what.getType() == G && Math.abs(what.getI() - to.getI()) == 2 &&
                locationCollectionContains(
                        //Itt azt nézzük meg, hogy a közbülső mezőt tartalmazza-e bármely ellenfél gyalog watchRange-e
                        boardToMoveOn.getPieces().stream()
                                .filter(p -> p.isWhite() != what.isWhite() && p.getType() == G)
                                .flatMap(p -> ((Piece) p).getWatchedRange().stream())
                                .collect(Collectors.toSet()),
                        getTheMiddleLocation(what.getLocation(), to)
                );
    }

    private Location getTheMiddleLocation(Location first, Location last){
        if (first.getJ() == last.getJ()){
            return new Location((first.getI() + last.getI()) / 2, first.getJ());
        }
        return null;
    }


    /**
     * @return the moveDocString which format is:
     * <p>
     *      ColorType_fromXfromY_toXtoY_
     *      pluszfiguraColorpluszfiguraType_
     *      pluszfigurahonnanXpluszfigurahonnanX_pluszfigurahonnanXpluszfigurahonnanY_
     *      plusfigurahovaXpluszfigurahovaX_plusfigurahovaXpluszfigurahovaY_
     *      bigCastlechangesmallCastlechange_possibleempassantXpossibleempassantY
     *
     *      In the description above the capital letters are the ones that mark a char
     */
    private String createDocumentationString(){
        return "";
    }

    //endregion

}
