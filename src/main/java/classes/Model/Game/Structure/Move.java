package classes.Model.Game.Structure;

import classes.Model.AI.Ai.AI;
import classes.GUI.FrameParts.ViewPiece;
import classes.Model.Game.I18N.*;
import lombok.*;

import javax.swing.*;
import java.util.stream.Collectors;

import static classes.GUI.FrameParts.Logger.*;

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

    private void parameterizeToDefault(){
        itIsCastle = false;
        itIsEmPassant = false;
        itIsEmPassantAuthorization = false;
        itIsPawnGotIn = false;
    }

    //endregion


    //region Methods

    public static void Step(Move move) {
        synchronized (VARS.MUTABLE.pauseFlag){
            AI.waitOnPause();
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

            plusPiece.setFirst(new PieceAttributes(PieceType.R, what.isWhite() ? "WHITE" : "BLACK"));
            plusPiece.setSecond(new Pair<>(plusPieceFrom(), plusPieceTo()));

        } else if (itIsEmPassant) {

            plusPiece.setFirst(new PieceAttributes(PieceType.P, !what.isWhite() ? "WHITE" : "BLACK"));
            plusPiece.setSecond(new Pair<>(plusPieceFrom(), plusPieceTo()));

        } else if (METHODS.notNull(boardToMoveOn.getPiece(to))) {
            
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
        if (METHODS.notNull(what.getLocation())) {
            fromField = boardToMoveOn.getField(what);
        }

        IField toField = boardToMoveOn.getField(to);
        if (METHODS.notNull(boardToMoveOn.getPiece(to))){
            boardToMoveOn.getPiece(to).setLocation(new Location(-1, -1));
        }
        toField.setPiece(what);

        if (METHODS.notNull(fromField)) {
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

            Location middleLocOfPawnStep = Board.getTheMiddleLocation(from, to);
            assert middleLocOfPawnStep != null;
            VARS.MUTABLE.emPassantChance = "";
            VARS.MUTABLE.emPassantChance += middleLocOfPawnStep.getI();
            VARS.MUTABLE.emPassantChance += middleLocOfPawnStep.getJ();


        } else if (itIsPawnGotIn) {

            PieceType newType;

            if (what instanceof ViewPiece){
                newType = pawnGotInCaseView();
            }else {
                newType = PieceType.Q;
            }

            what.getAttributes().setType(newType);

        }

        castleBoolsModify();
        METHODS.changeEvenOrOddStep();
        if (!itIsEmPassantAuthorization)
            VARS.MUTABLE.emPassantChance = "-";
    }

    /**
     * @return "castle" if the move was castle, "emPassant" if the move was emPassant,
     *         "emPassantAut" if the move enables future emPassant
     *         "pawnGotIn" if the move was pawnGotIn
     */
    private String detectSpecialCase() {

        if (what.getType() == PieceType.K && Math.abs(from.getJ() - to.getJ()) == 2){
            return "castle";
        } else if (what.getType() == PieceType.P && to.equals(Location.emPassantStringToLocation(VARS.MUTABLE.emPassantChance))) {
            return "emPassant";
        } else if (emPassantAuthorizationIf()) {
            return "emPassantAut";
        } else if (what.getType() == PieceType.P && to.getI() == what.getEnemyStartRow()) {
            return "pawnGotIn";
        } else {
            return "";
        }

    }

    private boolean emPassantAuthorizationIf() {
        return what.getType() == PieceType.P && Math.abs(what.getI() - to.getI()) == 2 &&
                METHODS.locationCollectionContains(
                        //Itt azt nézzük meg, hogy a közbülső mezőt tartalmazza-e bármely ellenfél gyalog watchRange-e
                        Board.getBoard().getPieces().stream()
                                .filter(p -> p.isWhite() != what.isWhite() && p.getType() == PieceType.P)
                                .flatMap(p -> p.getWatchedRange().stream())
                                .collect(Collectors.toSet()),
                        Board.getTheMiddleLocation(what.getLocation(), to)
                );
    }

    private void castleBoolsModify() {
        if (what.getType() == PieceType.R){
            if (VARS.MUTABLE.whiteDown) {
                if (from.getJ() > 4){
                    if (what.isWhite()) {
                        VARS.MUTABLE.whiteSmallCastleEnabled = false;
                    }
                    else {
                        VARS.MUTABLE.blackSmallCastleEnabled = false;
                    }
                }else {
                    if (what.isWhite()) {
                        VARS.MUTABLE.whiteBigCastleEnabled = false;
                    }
                    else {
                        VARS.MUTABLE.blackBigCastleEnabled = false;
                    }
                }
            } else {
                if (from.getJ() < 4){
                    if (what.isWhite()) {
                        VARS.MUTABLE.whiteSmallCastleEnabled = false;
                    }
                    else {
                        VARS.MUTABLE.blackSmallCastleEnabled = false;
                    }
                }else {
                    if (what.isWhite()) {
                        VARS.MUTABLE.whiteBigCastleEnabled = false;
                    }
                    else {
                        VARS.MUTABLE.blackBigCastleEnabled = false;
                    }
                }
            }
        } else if (what.getType() == PieceType.K) {
            if (what.isWhite()){
                VARS.MUTABLE.whiteSmallCastleEnabled = false;
                VARS.MUTABLE.whiteBigCastleEnabled = false;
            }else {
                VARS.MUTABLE.blackSmallCastleEnabled = false;
                VARS.MUTABLE.blackBigCastleEnabled = false;
            }
        }
    }

    private Location plusPieceFrom() {
        if (itIsCastle){
            return new Location(to.getI(), Math.abs(to.getJ()) < Math.abs(7 - to.getJ()) ? 0 : 7);
        } else if (itIsEmPassant) {
            int plusI = what.isWhite() ? (VARS.MUTABLE.whiteDown ? -1 : 1) : (VARS.MUTABLE.whiteDown ? 1 : -1);
            return new Location(to.getI() + plusI, to.getJ());
        } else if (METHODS.notNull(plusPiece)) {
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
                VARS.MUTABLE.pieceToChange.isWhite() ?
                        VARS.FINALS.WhitePieceChoiceInsteadOfPawnGotIn.toArray() :
                        VARS.FINALS.BlackPieceChoiceInsteadOfPawnGotIn.toArray(),
                null);
        PieceType newType;
        switch (result){
            case 0 -> newType = PieceType.N;
            case 1 -> newType = PieceType.R;
            case 2 -> newType = PieceType.R;
            case 3 -> newType = PieceType.Q;
            default -> throw new IllegalStateException("Unexpected value: " + result);
        }
        VARS.MUTABLE.pieceToChange.getAttributes().setType(newType);

        VARS.MUTABLE.pieceToChange.setImage(
                VARS.MUTABLE.pieceToChange.isWhite() ?
                        VARS.FINALS.WhitePieceChoiceInsteadOfPawnGotIn.get(result).getImage() :
                        VARS.FINALS.BlackPieceChoiceInsteadOfPawnGotIn.get(result).getImage()
        );
        return newType;
    }

    //endregion

}
