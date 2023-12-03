package classes.Ai;

import classes.Game.Model.Structure.Field;
import classes.Game.Model.Structure.IPiece;
import lombok.*;

import java.util.ArrayList;

import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.Board.*;
import static classes.Game.I18N.METHODS.*;

@Getter
public class Evaluator {

    //region Methods

    //region Evaluator

    public static double evaluate(){
        return sumOfBoard();
    }

    private static double sumOfBoard(){
//        double sum = 0;
//        for (IPiece p : getBoard().getPiecesWithoutHit()) {
//            sum += ((Piece) p).getVALUE();
//        }
//        return sum;
        return finalValueCalculation(true) + finalValueCalculation(false);
    }

    //endregion

    //region Final Value Calculation

    public static double finalValueCalculation(boolean forWhite){

        getBoard().getFields().stream()
                .flatMap(ArrayList::stream)
                .forEach(f -> ((Field) f).setFinalValue(forWhite));

        return getBoard().getFields().stream()
        .flatMap(ArrayList::stream)
        .filter(f -> f.isGotPiece() && forWhite == f.getPiece().isWhite())
        .mapToDouble(f -> ((Field) f).getFinalValue())
        .sum();
    }

    //endregion

    //region Watcher Count

    public static void setWatcherCounts(boolean forWhite){
        getBoard().getPiecesWithoutHit(forWhite)
                .forEach(p -> p.getWatchedRange().stream()
                .map(loc -> (Field) getBoard().getField(loc))
                .forEach(field -> field.increaseWatcherCount(forWhite))
        );
    }

    //endregion

    //region Base Field Values

    public static double getBaseFieldValueFor(IPiece p){
        if (p.getType() != H){
            if (whiteDown){
                return p.isWhite() ? FIELD_BASE_VALUES_BY_PIECE_TYPE.get(p.getType())[p.getI()][p.getJ()] :
                                    mirrorMatrixHorizontally(FIELD_BASE_VALUES_BY_PIECE_TYPE.get(p.getType()))[p.getI()][p.getJ()];
            }else {
                Double[][] mirroredVertically = mirrorMatrixVertically(FIELD_BASE_VALUES_BY_PIECE_TYPE.get(p.getType()));
                return p.isWhite() ? mirroredVertically[p.getI()][p.getJ()] :
                        mirrorMatrixHorizontally(mirroredVertically)[p.getI()][p.getJ()];
            }
        }else {
            return FIELD_BASE_VALUES_BY_PIECE_TYPE.get(H)[p.getI()][p.getJ()];
        }
    }

    public static void addBaseFieldValues(){

        boolean evenOrOddBoardWidthHeight = MAX_WIDTH % 2 == 0;
        firstInnerConcentricCircleBaseFieldValues(evenOrOddBoardWidthHeight);
        if (MAX_WIDTH > 2){
            secondInnerConcentricCircleBaseFieldValues(evenOrOddBoardWidthHeight);
        }
        if (MAX_WIDTH > 4){
            thirdInnerConcentricCircleBaseFieldValues(evenOrOddBoardWidthHeight);
        }
        if (MAX_WIDTH > 6){
            fourthInnerConcentricCircleBaseFieldValues(evenOrOddBoardWidthHeight);
        }
    }

    private static void pawnBaseFieldValues(){
        pawnBaseFieldValues(true);
        pawnBaseFieldValues(false);
    }

    private static void pawnBaseFieldValues(boolean downPlayer) {
        
    }

    private static void knightBaseFieldValues() {

    }

    private static void bishopBaseFieldValues(){
        bishopBaseFieldValues(true);
        bishopBaseFieldValues(false);
    }

    private static void bishopBaseFieldValues(boolean downPlayer) {

    }

    private static void rookBaseFieldValues(){
        rookBaseFieldValues(true);
        rookBaseFieldValues(false);
    }

    private static void rookBaseFieldValues(boolean downPlayer){
        
    }
    
    private static void queenBaseValues() {

    }

    private static void kingBaseFieldValues(){
        kingBaseFieldValues(true);
        kingBaseFieldValues(false);
    }

    private static void kingBaseFieldValues(boolean downPlayer) {

    }

    /**
     * @param evenOrOddBoardWidthHeight Megmutatja, hogy páros vagy páratlan board baseFieldValue-t állítjuk.
     * Azon belül is annak első belső körét.
     */
    private static void firstInnerConcentricCircleBaseFieldValues(boolean evenOrOddBoardWidthHeight){
        ArrayList<Double> alreadyUsedBaseValues = new ArrayList<>();
        int fromTo = evenOrOddBoardWidthHeight ? 1 : 0;
        setBaseFieldValue(FIRST_BASE_FIELD_VALUE, (MAX_WIDTH / 2) - fromTo, (MAX_WIDTH / 2) + fromTo, alreadyUsedBaseValues);
    }
    
    private static void secondInnerConcentricCircleBaseFieldValues(boolean evenOrOddBoardWidthHeight) {
        ArrayList<Double> alreadyUsedBaseValues = new ArrayList<>(){{
            add(FIRST_BASE_FIELD_VALUE);
        }};
        int fromTo = evenOrOddBoardWidthHeight ? 2 : 1;
        setBaseFieldValue(SECOND_BASE_FIELD_VALUE, (MAX_WIDTH / 2) - fromTo, (MAX_WIDTH / 2) + fromTo, alreadyUsedBaseValues);
    }

    private static void thirdInnerConcentricCircleBaseFieldValues(boolean evenOrOddBoardWidthHeight) {
        ArrayList<Double> alreadyUsedBaseValues = new ArrayList<>(){{
            add(FIRST_BASE_FIELD_VALUE);
            add(SECOND_BASE_FIELD_VALUE);
        }};
        int fromTo = evenOrOddBoardWidthHeight ? 3 : 2;
        setBaseFieldValue(THIRD_BASE_FIELD_VALUE, (MAX_WIDTH / 2) - fromTo, (MAX_WIDTH / 2) + fromTo, alreadyUsedBaseValues);
    }

    private static void fourthInnerConcentricCircleBaseFieldValues(boolean evenOrOddBoardWidthHeight) {
        ArrayList<Double> alreadyUsedBaseValues = new ArrayList<>(){{
            add(FIRST_BASE_FIELD_VALUE);
            add(SECOND_BASE_FIELD_VALUE);
            add(THIRD_BASE_FIELD_VALUE);
        }};
        int fromTo = evenOrOddBoardWidthHeight ? 4 : 3;
        setBaseFieldValue(FOURTH_BASE_FIELD_VALUE, (MAX_WIDTH / 2) - fromTo, (MAX_WIDTH / 2) + fromTo, alreadyUsedBaseValues);
    }

    private static void setBaseFieldValue(double baseFieldValue, int from, int to, ArrayList<Double> dontWantTheseValues){
        Field field;
        for (int i = from; i < to; i++) {
            for (int j = from; j < to; j++) {
                field = ((Field) getBoard().getField(i, j));
                if (!dontWantTheseValues.contains(field.getBaseValue())){
                    field.setBaseValue(baseFieldValue);
                }
            }
        }
    }

    //endregion

    //endregion

}
