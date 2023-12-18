package classes.Ai.Evaluation;

import classes.Game.Model.Structure.Field;
import classes.Game.Model.Structure.IPiece;
import classes.Game.Model.Structure.Piece;
import lombok.*;

import java.util.ArrayList;

import static classes.Game.Model.Structure.Board.*;

@Getter
public class Old_Evaluator {

    //region Methods

    //region Old_Evaluator

    public static double evaluate(){
        return sumOfBoard();
    }

    private static double sumOfBoard(){
        double sum = 0;
        for (IPiece p : getBoard().getPiecesWithoutHit()) {
            sum += ((Piece) p).getVALUE();
        }
        return sum;
//        return finalValueCalculation(true) + finalValueCalculation(false);
    }

    //endregion

    //region Final Value Calculation

    public static double finalValueCalculation(boolean forWhite){

        getBoard().getFields().stream()
                .flatMap(ArrayList::stream)
                .forEach(f -> ((Field) f).setFinalValue());

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

    public static double getBaseFieldValue(IPiece p){
        return Evaluator.getBaseFieldValue(p.getType(), p.isWhite(), p.getI(), p.getJ());
    }

    //endregion

    //endregion

}
