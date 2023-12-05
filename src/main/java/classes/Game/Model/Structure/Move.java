package classes.Game.Model.Structure;

import classes.GUI.FrameParts.ViewPiece;
import classes.Game.I18N.*;
import lombok.*;

import javax.swing.*;
import java.util.stream.Collectors;

import static classes.GUI.FrameParts.Logger.*;
import static classes.Game.I18N.Location.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.Board.*;

@Getter
@Setter
public class Move {

    //region Fields

    private IPiece what;

    private Location from;

    private Location to;

    private IBoard boardToMoveOn;

    private boolean mustLogged;


    /**
     * This is a mimic of the plusPiece, that was used in the move.
     * If there wasn't such, then it's null.
     * Else the first pair means isWhite and pieceType
     *      and the second pair means from to. (If it was taken, then to is null)
     */
    private Pair<PieceAttributes, Pair<Location, Location>> plusPiece;


    private boolean itIsCastle;

    private boolean itIsEmPassant;
    
    private boolean itIsEmPassantAuthorization;

    private boolean itIsPawnGotIn;

    //endregion


    //region Constructor

    public Move(){
        parameterizeToDefault();
    }

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
    }

    //endregion


    //region Methods

    public static void Step(Move move) {
        synchronized (pauseFlag){
            waitOnPause();
            move.moveCaseExploreAndSet();
            move.collectPlusPiece();
            move.pieceChangeOnBoard();
            move.doAfterChangeEffects();
        }
    }

    private void moveCaseExploreAndSet() {
        String moveCase = detectSpecialCase();
        switch (moveCase) {
            case "castle" -> setItIsCastle(true);
            case "emPassantAut" -> setItIsEmPassantAuthorization(true);
            case "emPassant" -> setItIsEmPassant(true);
            case "pawnGotIn" -> setItIsPawnGotIn(true);
        }
    }

    private void collectPlusPiece()  {
        plusPiece = new Pair<>();

        if (itIsCastle){

            plusPiece.setFirst(new PieceAttributes(B, what.isWhite() ? "WHITE" : "BLACK"));
            plusPiece.setSecond(new Pair<>(plusPieceFrom(), plusPieceTo()));

        } else if (itIsEmPassant) {

            plusPiece.setFirst(new PieceAttributes(G, !what.isWhite() ? "WHITE" : "BLACK"));
            plusPiece.setSecond(new Pair<>(plusPieceFrom(), plusPieceTo()));

        } else if (notNull(boardToMoveOn.getPiece(to))) {
            
            plusPiece.setFirst(new PieceAttributes(
                    boardToMoveOn.getPiece(to).getType(),
                    !what.isWhite() ? "WHITE" : "BLACK"));
            plusPiece.setSecond(new Pair<>(plusPieceFrom(), plusPieceTo()));

        }else {
            plusPiece = null;
        }
    }
    
    private void pieceChangeOnBoard()  {

        logStep(this);

        IField fromField = null;
        if (notNull(what.getLocation())) {
            fromField = boardToMoveOn.getField(what);
        }

        IField toField = boardToMoveOn.getField(to);
        if (notNull(boardToMoveOn.getPiece(to))){
            boardToMoveOn.getPiece(to).setLocation(new Location(-1, -1));
        }
        toField.setPiece(what);

        if (notNull(fromField)) {
            fromField.clean();
        }

    }

    private void doAfterChangeEffects()  {

        if (itIsCastle){

            boardToMoveOn.getField(plusPiece.getSecond().getSecond()).setPiece(plusPiece.getFirst());
            boardToMoveOn.getField(plusPiece.getSecond().getFirst()).clean();

        } else if (itIsEmPassant) {

            boardToMoveOn.getField(plusPiece.getSecond().getFirst()).clean();

        } else if (itIsEmPassantAuthorization) {

            Location middleLocOfPawnStep = getTheMiddleLocation(from, to);
            assert middleLocOfPawnStep != null;
            emPassantChance = "";
            emPassantChance += middleLocOfPawnStep.getI();
            emPassantChance += middleLocOfPawnStep.getJ();


        } else if (itIsPawnGotIn) {

            PieceType newType;

            if (what instanceof ViewPiece){
                newType = pawnGotInCaseView();
            }else {
                newType = V;
            }

            what.getAttributes().setType(newType);

        }

        castleBoolsModify();
        changeEvenOrOddStep();
        if (!itIsEmPassantAuthorization)
            emPassantChance = "-";
    }

    /**
     * @return "castle" if the move was castle, "emPassant" if the move was emPassant,
     *         "emPassantAut" if the move enables future emPassant
     *         "pawnGotIn" if the move was pawnGotIn
     */
    private String detectSpecialCase() {

        if (what.getType() == K && Math.abs(from.getJ() - to.getJ()) == 2){
            return "castle";
        } else if (what.getType() == G && to.equals(stringToLocation(emPassantChance))) {
            return "emPassant";
        } else if (emPassantAuthorizationIf()) {
            return "emPassantAut";
        } else if (what.getType() == G && to.getI() == what.getEnemyStartRow()) {
            return "pawnGotIn";
        } else {
            return "";
        }

    }

    private boolean emPassantAuthorizationIf() {
        return what.getType() == G && Math.abs(what.getI() - to.getI()) == 2 &&
                locationCollectionContains(
                        //Itt azt nézzük meg, hogy a közbülső mezőt tartalmazza-e bármely ellenfél gyalog watchRange-e
                        getBoard().getPieces().stream()
                                .filter(p -> p.isWhite() != what.isWhite() && p.getType() == G)
                                .flatMap(p -> p.getWatchedRange().stream())
                                .collect(Collectors.toSet()),
                        getTheMiddleLocation(what.getLocation(), to)
                );
    }

    private void castleBoolsModify() {
        if (what.getType() == B){
            if (whiteDown) {
                if (from.getJ() > 4){
                    if (what.isWhite()) {
                        whiteSmallCastleEnabled = false;
                    }
                    else {
                        blackSmallCastleEnabled = false;
                    }
                }else {
                    if (what.isWhite()) {
                        whiteBigCastleEnabled = false;
                    }
                    else {
                        blackBigCastleEnabled = false;
                    }
                }
            } else {
                if (from.getJ() < 4){
                    if (what.isWhite()) {
                        whiteSmallCastleEnabled = false;
                    }
                    else {
                        blackSmallCastleEnabled = false;
                    }
                }else {
                    if (what.isWhite()) {
                        whiteBigCastleEnabled = false;
                    }
                    else {
                        blackBigCastleEnabled = false;
                    }
                }
            }
        } else if (what.getType() == K) {
            if (what.isWhite()){
                whiteSmallCastleEnabled = false;
                whiteBigCastleEnabled = false;
            }else {
                blackSmallCastleEnabled = false;
                blackBigCastleEnabled = false;
            }
        }
    }

    private Location plusPieceFrom() {
        if (itIsCastle){
            return new Location(to.getI(), Math.abs(to.getJ()) < Math.abs(7 - to.getJ()) ? 0 : 7);
        } else if (itIsEmPassant) {
            int plusI = what.isWhite() ? (whiteDown ? -1 : 1) : (whiteDown ? 1 : -1);
            return new Location(to.getI() + plusI, to.getJ());
        } else if (notNull(plusPiece)) {
            return to;
        }
        return null;
    }

    private Location plusPieceTo() {
        if (itIsCastle){
            int rookRoadLength = to.getJ() - (to.getJ() >= 4 ? 7 : 0);

            if (rookRoadLength < 0)
                rookRoadLength--;
            else
                rookRoadLength++;

            Location rookOriginPlace = new Location(to.getI(), to.getJ() >= 4 ? 7 : 0);
            return new Location(
                    to.getI(),
                    rookOriginPlace.getJ() + rookRoadLength
            );
        }
        return null;
    }

    private PieceType pawnGotInCaseView(){
        int result = JOptionPane.showOptionDialog(null, "Válassz melyik figurát szeretnéd.", "Lehetőségek.",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                pieceToChange.isWhite() ?
                        WhitePieceChoiceInsteadOfPawnGotIn.toArray() :
                        BlackPieceChoiceInsteadOfPawnGotIn.toArray(),
                null);
        PieceType newType;
        switch (result){
            case 0 -> newType = H;
            case 1 -> newType = F;
            case 2 -> newType = B;
            case 3 -> newType = V;
            default -> throw new IllegalStateException("Unexpected value: " + result);
        }
        pieceToChange.getAttributes().setType(newType);

        pieceToChange.setImage(
                pieceToChange.isWhite() ?
                        WhitePieceChoiceInsteadOfPawnGotIn.get(result).getImage() :
                        BlackPieceChoiceInsteadOfPawnGotIn.get(result).getImage()
        );
        return newType;
    }

    //endregion

}
