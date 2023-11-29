package classes.Ai.Evaluate;

import classes.Game.I18N.Location;
import classes.Game.Model.Structure.Field;
import classes.Game.Model.Structure.IField;
import classes.Game.Model.Structure.IPiece;
import lombok.*;

import java.util.ArrayList;

import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.Board.*;

@Getter
public class Evaluator {

    //region Methods

    //region Final Value Calculation

    public static double finalValueCalculation(boolean forWhite){

        getBoard().getFields().stream()
                .flatMap(ArrayList::stream)
                .forEach(f -> ((Field) f).setFinalValue(forWhite));

        double finalValue =
                getBoard().getFields().stream()
                .flatMap(ArrayList::stream)
                .filter(f -> f.isGotPiece() && forWhite == f.getPiece().isWhite())
                .mapToDouble(f -> ((Field) f).getFinalValue())
                .sum();

        return forWhite ? finalValue : -finalValue;
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

    /**
     * @param evenOrOddBoardWidthHeight Megmutatja, hogy páros vagy páratlan board baseFieldValue-t állítjuk.
     * Azon belül is annak első belső körét.
     */
    private static void firstInnerConcentricCircleBaseFieldValues(boolean evenOrOddBoardWidthHeight){
        ArrayList<Double> alreadyUsedBaseValues = new ArrayList<>();
        int fromTo = evenOrOddBoardWidthHeight ? 1 : 0;
        setBaseFieldValue(firstBaseFieldValue, (MAX_WIDTH / 2) - fromTo, (MAX_WIDTH / 2) + fromTo, alreadyUsedBaseValues);
    }
    
    private static void secondInnerConcentricCircleBaseFieldValues(boolean evenOrOddBoardWidthHeight) {
        ArrayList<Double> alreadyUsedBaseValues = new ArrayList<>(){{
            add(firstBaseFieldValue);
        }};
        int fromTo = evenOrOddBoardWidthHeight ? 2 : 1;
        setBaseFieldValue(secondBaseFieldValue, (MAX_WIDTH / 2) - fromTo, (MAX_WIDTH / 2) + fromTo, alreadyUsedBaseValues);
    }

    private static void thirdInnerConcentricCircleBaseFieldValues(boolean evenOrOddBoardWidthHeight) {
        ArrayList<Double> alreadyUsedBaseValues = new ArrayList<>(){{
            add(firstBaseFieldValue);
            add(secondBaseFieldValue);
        }};
        int fromTo = evenOrOddBoardWidthHeight ? 3 : 2;
        setBaseFieldValue(thirdBaseFieldValue, (MAX_WIDTH / 2) - fromTo, (MAX_WIDTH / 2) + fromTo, alreadyUsedBaseValues);
    }

    private static void fourthInnerConcentricCircleBaseFieldValues(boolean evenOrOddBoardWidthHeight) {
        ArrayList<Double> alreadyUsedBaseValues = new ArrayList<>(){{
            add(firstBaseFieldValue);
            add(secondBaseFieldValue);
            add(thirdBaseFieldValue);
        }};
        int fromTo = evenOrOddBoardWidthHeight ? 4 : 3;
        setBaseFieldValue(fourthBaseFieldValue, (MAX_WIDTH / 2) - fromTo, (MAX_WIDTH / 2) + fromTo, alreadyUsedBaseValues);
    }

    private static void setBaseFieldValue(double baseFieldValue, int from, int to, ArrayList<Double> dontWantThatValue){
        Field field;
        for (int i = from; i < to; i++) {
            for (int j = from; j < to; j++) {
                field = ((Field) getBoard().getField(i, j));
                if (!dontWantThatValue.contains(field.getBaseValue())){
                    field.setBaseValue(baseFieldValue);
                }
            }
        }
    }

    //endregion

    //endregion

}
