package classes.Game.Model.Structure;

import classes.GUI.FrameParts.ViewBoard;
import classes.GUI.FrameParts.ViewPiece;
import classes.Game.I18N.*;
import lombok.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.Location.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.GUI.Frame.Window.*;
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


    /**
     *      ColorType_fromXfromY_toXtoY_
     *      pluszfiguraColor/-pluszfiguraType/-_
     *      pluszfigurahonnanX/-pluszfigurahonnanY/-_plusfigurahovaX/-pluszfigurahovaY/-_
     *      bigCastlechangesmallCastlechange_possibleempassantXpossibleempassantY
     * <p>
     *      In the description above the capital letters are the ones that mark a char
     */
    private String moveDocString;

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
        moveDocString = "";
    }

    //endregion


    //region Methods

    public static void Step(Move move) {

        move.moveCaseExploreAndSet();
        move.collectPlusPiece();
        move.pieceChangeOnBoard();
        move.doAfterChangeEffects();

    }

    private static void logStep(Move move)  {
        if (canBeLogger && move.mustLogged){
            String step = "";

            if (whiteToPlay){
                step += stepNumber + ". ";
            }else {
                step += " - ";
                stepNumber++;
            }

            if (!move.itIsCastle){
                step += move.what.getType().toString().charAt(0);
                if (notNull(move.plusPiece)) {
                    step += 'x';
                }
                step += move.to.toLoggerString();
            }else {
                if (Math.abs(move.plusPiece.getSecond().getFirst().getJ() - move.plusPiece.getSecond().getSecond().getJ()) > 2){
                    step += "0-0-0";
                }else{
                    step += "0-0";
                }
            }

            step += "\t" + move.moveDocString;
            step += '\n';

            getLogger().log(step);
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

        moveDocumenting();
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

    private void moveDocumenting(){
        stepperDocumenting();
        plusPieceDocumenting();
        castleChanceDocumenting();
        emPassantChanceDocumenting();
        pawnGotInDocumenting();
    }

    private void stepperDocumenting(){
        documentationStringCreation(what.isWhite() ? 'W' : 'B');
        documentationStringCreation(what.getType().toString().charAt(0));
        documentationStringCreation(indexNums.get(from.getI()).charAt(0));
        documentationStringCreation(indexNums.get(from.getJ()).charAt(0));
        documentationStringCreation(indexNums.get(to.getI()).charAt(0));
        documentationStringCreation(indexNums.get(to.getJ()).charAt(0));
    }

    private void plusPieceDocumenting(){
        if (notNull(plusPiece)) {
            documentationStringCreation(plusPiece.getFirst().isWhite() ? 'W' : 'B');
            documentationStringCreation(plusPiece.getFirst().getType().toString().charAt(0));
            documentationStringCreation((char) plusPiece.getSecond().getFirst().getI());
            documentationStringCreation((char) plusPiece.getSecond().getFirst().getJ());
            if (notNull(plusPiece.getSecond().getSecond())){
                documentationStringCreation((char) plusPiece.getSecond().getSecond().getI());
                documentationStringCreation((char) plusPiece.getSecond().getSecond().getJ());
            }else {
                documentationStringCreation('-');
                documentationStringCreation('-');
            }
        }else {
            documentationStringCreation('-');
            documentationStringCreation('-');
            documentationStringCreation('-');
            documentationStringCreation('-');
            documentationStringCreation('-');
            documentationStringCreation('-');
        }
    }

    private void castleChanceDocumenting() {
        if (what.getType() == B){
            if (whiteDown) {
                if (from.getJ() > 4){
                    if (what.isWhite()) {
                        whiteSmallCastleEnabled = false;
                        documentationStringCreation('K');
                    }
                    else {
                        blackSmallCastleEnabled = false;
                        documentationStringCreation('k');
                    }
                }else {
                    if (what.isWhite()) {
                        whiteBigCastleEnabled = false;
                        documentationStringCreation('V');
                    }
                    else {
                        blackBigCastleEnabled = false;
                        documentationStringCreation('v');
                    }
                }
            } else {
                if (from.getJ() < 4){
                    if (what.isWhite()) {
                        whiteSmallCastleEnabled = false;
                        documentationStringCreation('K');
                    }
                    else {
                        blackSmallCastleEnabled = false;
                        documentationStringCreation('k');
                    }
                }else {
                    if (what.isWhite()) {
                        whiteBigCastleEnabled = false;
                        documentationStringCreation('V');
                    }
                    else {
                        blackBigCastleEnabled = false;
                        documentationStringCreation('v');
                    }
                }
            }
            documentationStringCreation('-');
        } else if (what.getType() == K) {
            if (what.isWhite()){
                whiteSmallCastleEnabled = false;
                whiteBigCastleEnabled = false;
                documentationStringCreation('K');
                documentationStringCreation('V');
            }else {
                blackSmallCastleEnabled = false;
                blackBigCastleEnabled = false;
                documentationStringCreation('k');
                documentationStringCreation('v');
            }
        } else {
            documentationStringCreation('-');
            documentationStringCreation('-');
        }
    }

    private void emPassantChanceDocumenting() {
        documentationStringCreation(emPassantChance.charAt(0));
        if (!"-".equals(emPassantChance)) {
            documentationStringCreation(emPassantChance.charAt(1));
        }else {
            documentationStringCreation('-');
        }
    }

    private void pawnGotInDocumenting(){
        if (itIsPawnGotIn) {
            documentationStringCreation('G');
            documentationStringCreation(what.getType().toString().charAt(0));
        }
    }

    /**
     * @param appendThis if we append numbers (those will mean locations) it appends the first,
     *                   and if the next isn't a number we throw RunTimeException
     */
    private void documentationStringCreation(char appendThis) throws RuntimeException {

        if (!moveDocString.isEmpty() &&
                Character.isDigit(moveDocString.charAt(moveDocString.length() - 1)) &&
                !Character.isDigit(appendThis)) {
            throw new RuntimeException("Úgy akarunk mást hozzá fűzni, " +
                    "hogy a megkezdett docStringet még nem fejeztük be: " + moveDocString);
        }

        moveDocString += appendThis;

        if (moveDocString.length() > 1 &&
                (
                        (Character.isLetter(appendThis) && Character.isLetter(moveDocString.charAt(moveDocString.length() - 2)) ||
                        (Character.isDigit(appendThis) && Character.isDigit(moveDocString.charAt(moveDocString.length() - 2))) ||
                        ('-' == appendThis && '-' == moveDocString.charAt(moveDocString.length() - 2)))
                )
        ){
            moveDocString += "_";
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


    //region StepBack
    public static void StepBack(Move move) {
        deCryptAndStepBackMove(move);
        changeEvenOrOddStep();
        logStepBack(move);
    }

    private static void logStepBack(Move move) {
        int lineCount = getLogger().getLineCount();
        if (move.mustLogged){
            try {
                int startOffset = getLogger().getLineStartOffset(lineCount - 1);
                int endOffset = getLogger().getLineEndOffset(lineCount - 1);
                getLogger().replaceRange("", startOffset, endOffset);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void deCryptAndStepBackMove(Move move) {
        List<Object> backMoveParams = deCryptMoveDocStringToList(move);
        StepBackMove(backMoveParams);
        castleCasesSetBack(move);
        emPassantChanceSetBack(move);
        updateRangesBackAndFront(move);
    }

    private static List<Object> deCryptMoveDocStringToList(Move origin) {
        Move back = new Move();
        back.parameterize(origin.boardToMoveOn);

        String[] originParams = origin.getMoveDocString().split("_");

        boolean backWhatWhite = 'W' == originParams[0].charAt(0);
        PieceType backWhatType = charToPieceType(originParams[0].charAt(1));
        Location backWhatTo = new Location( // The from of the origin
                Character.getNumericValue(originParams[1].charAt(0)),
                Character.getNumericValue(originParams[1].charAt(1))
        );
        Location backWhatFrom = new Location(
                Character.getNumericValue(originParams[2].charAt(0)),
                Character.getNumericValue(originParams[2].charAt(1))
        );
        boolean backPlusWhite = false;
        PieceType backPlusType = null;
        Location backPlusTo = null;
        Location backPlusFrom = null;
        if ('-' != originParams[3].charAt(0)){
            backPlusWhite = 'W' == originParams[3].charAt(0);
            backPlusType = charToPieceType(originParams[3].charAt(1));
            backPlusTo = new Location( // The from of the origin
                    Character.getNumericValue(originParams[4].charAt(0)),
                    Character.getNumericValue(originParams[4].charAt(1))
            );
            if ('-' != originParams[5].charAt(0)) {
                backPlusFrom = new Location(
                        Character.getNumericValue(originParams[5].charAt(0)),
                        Character.getNumericValue(originParams[5].charAt(1))
                );
            }
        }else {
            back.plusPiece = null;
        }

        List<Object> backMoveParams = new ArrayList<>();
        backMoveParams.add(back);
        backMoveParams.add(backWhatWhite);
        backMoveParams.add(backWhatType);
        backMoveParams.add(backWhatTo);
        backMoveParams.add(backWhatFrom);
        backMoveParams.add(backPlusWhite);
        backMoveParams.add(backPlusType);
        backMoveParams.add(backPlusTo);
        backMoveParams.add(backPlusFrom);

        return backMoveParams;
    }

    private static void StepBackMove(List<Object> backMoveParams)  {

        Move back = (Move) backMoveParams.get(0);
        boolean backWhatWhite = (Boolean) backMoveParams.get(1);
        PieceType backWhatType = (PieceType) backMoveParams.get(2);
        Location backWhatTo = (Location) backMoveParams.get(3);
        Location backWhatFrom = (Location) backMoveParams.get(4);
        boolean backPlusWhite = (Boolean) backMoveParams.get(5);
        PieceType backPlusType = (PieceType) backMoveParams.get(6);
        Location backPlusTo = (Location) backMoveParams.get(7);
        Location backPlusFrom = (Location) backMoveParams.get(8);

        IBoard backBoard = back.boardToMoveOn;
        PieceAttributes backWhatAttrs = new PieceAttributes(backWhatType, backWhatWhite ? "WHITE" : "BLACK");
        IPiece backWhat = backBoard instanceof Board ?
                new Piece(backWhatAttrs) :
                new ViewPiece(createSourceStringFromGotAttributes(backWhatAttrs), backWhatAttrs);
        IField backFromField = backBoard.getField(backWhatFrom);
        IField backToField = backBoard.getField(backWhatTo);

        backToField.setPiece(backWhat);
        backFromField.clean();

        if (notNull(backPlusType)){
            PieceAttributes backPlusAttrs = new PieceAttributes(backPlusType, backPlusWhite ? "WHITE" : "BLACK");
            IPiece backPlus = backBoard instanceof Board ?
                    new Piece(backPlusAttrs) :
                    new ViewPiece(createSourceStringFromGotAttributes(backPlusAttrs), backPlusAttrs);
            IField backPlusFromField = null;
            if (notNull(backPlusFrom)){
                backPlusFromField = backBoard.getField(backPlusFrom);
            }
            IField backPlusToField = backBoard.getField(backPlusTo);

            backPlusToField.setPiece(backPlus);
            if (notNull(backPlusFromField)){
                backPlusFromField.clean();
            }
        }
    }

    private static void castleCasesSetBack(Move origin){
        String castleCasesChange = origin.moveDocString.split("_")[6];

        for (int i = 0; i < castleCasesChange.length(); i++) {
            switch (castleCasesChange.charAt(i)) {
                case 'K' -> whiteSmallCastleEnabled = true;
                case 'V' -> whiteBigCastleEnabled = true;
                case 'k' -> blackSmallCastleEnabled = true;
                case 'v' -> blackBigCastleEnabled = true;
                case '-' -> {}
            }
        }
    }

    private static void emPassantChanceSetBack(Move origin){
        String emPassantChanceChange = origin.moveDocString.split("_")[7];

        if ('-' != emPassantChanceChange.charAt(0)){

            int sor = Character.getNumericValue(emPassantChanceChange.charAt(0));
            int oszlop = Character.getNumericValue(emPassantChanceChange.charAt(1));

            for (IPiece p : origin.boardToMoveOn.getPieces()) {
                if (p.isWhite() != whiteToPlay &&
                        p.getType() == G &&
                        Math.abs(sor - Integer.parseInt(String.valueOf(emPassantChance.charAt(0)))) == 1 &&
                        Math.abs(oszlop - Integer.parseInt(String.valueOf(emPassantChance.charAt(1)))) == 1){
                    emPassantHelper(emPassantChanceChange, p.getAttributes());
                }
            }
        }
    }

    private static void pawnGotInCaseSetBack(Move origin){
        String pawnGotInChange = origin.moveDocString.split("_").length > 8 ? origin.moveDocString.split("_")[8] : "";
        if (!"".equals(pawnGotInChange)){
            origin.what.getAttributes().setType(G);
        }
    }

    private static void updateRangesBackAndFront(Move move) {
        whiteToPlay = !whiteToPlay;
        move.boardToMoveOn.rangeUpdater();
        whiteToPlay = !whiteToPlay;
    }

    //endregion

    //endregion

}
