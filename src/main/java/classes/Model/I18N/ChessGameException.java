package classes.Model.I18N;


import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

import static classes.Model.I18N.METHODS.*;
import static classes.Model.I18N.VARS.FINALS.*;

@Getter
@Setter
public class ChessGameException extends RuntimeException {

    //region Fields

    private final String msg;

    //endregion


    //region Constructor

    public ChessGameException(String msg) {
        this.msg = msg;
        System.out.println(msg);
    }

    //endregion


    //region Methods

    //region Fen Error

    public static void throwFenErrorIfNeeded(String fen){
        if (fenIsWrong(fen)) {
            throw new ChessGameException(fenErrorMessage(fen));
        }
    }

    private static boolean fenIsWrong(String FEN){
        boolean fenIsWrong = false;
        String fen = FEN.split(" ")[0];
        if(MAX_HEIGHT - 1 == countOccurrences(fen, '/')){
            String[] parts = fen.split("/");
            for (String part : parts) {
                if (MAX_WIDTH != getFenRowCalculableLength(part)){
                    fenIsWrong = true;
                    break;
                }
            }
        }else {
            fenIsWrong = true;
        }

        return fenIsWrong;
    }

    private static String fenErrorMessage(String fen){
        StringBuilder errorMessage = new StringBuilder("Ez a fen:\n");
        errorMessage.append(fen).append('\n');
        errorMessage.append("nem passzol a megszabott tábla méretekhez, mert ");
        String[] rows = fen.split("/");
        boolean rowCountEqualsMaxHeight = rows.length == MAX_HEIGHT;
        boolean colCountEqualsMaxWidth = Arrays.stream(rows).allMatch(r -> getFenRowCalculableLength(r) == MAX_WIDTH);
        if (!rowCountEqualsMaxHeight && !colCountEqualsMaxWidth){
            badRowNum(errorMessage, rows);
            badColNum(errorMessage, rows, ". \nTovábbá ez a sor: ");
        } else if (!rowCountEqualsMaxHeight) {
            errorMessage.append("a megadott fen ").append(rows.length).append(" sort tartalmaz, holott az elvárt: ").append(MAX_HEIGHT);
        } else if (!colCountEqualsMaxWidth) {
            badColNum(errorMessage, rows, ". \nEz a sor: ");
        }
        return errorMessage.toString();
    }

    private static void badRowNum(StringBuilder errorMessage, String[] rows){
        errorMessage.append("a megadott fen ").append(rows.length).append(" sort tartalmaz, holott az elvárt: ").append(MAX_HEIGHT);
    }

    public static String getBadRow(String[] rows){
        String badRow = "";
        for (String row : rows) {
            if (MAX_WIDTH != getFenRowCalculableLength(row)){
                badRow = row;
                break;
            }
        }
        return badRow;
    }

    private static void badColNum(StringBuilder errorMessage, String[] rows, String openingLine) {
        errorMessage.append(openingLine);
        String badRow = "";
        int rowCalculableLength = 0;
        for (String row : rows) {
            rowCalculableLength = getFenRowCalculableLength(row);
            if (rowCalculableLength != MAX_WIDTH) {
                badRow = row;
                break;
            }
        }
        errorMessage
                .append(badRow)
                .append(" nem megfelelő hosszúságú, hiszen a kívánt hossz ")
                .append(MAX_WIDTH)
                .append(" a sor pedig ")
                .append(rowCalculableLength)
                .append(" hosszú.\n");
    }

    public static int getFenRowCalculableLength(String fenRow){
        int fenRowLength = 0;
        char currentC;
        for (int i = 0; i < fenRow.length(); i++) {
            currentC = fenRow.charAt(i);
            if (Character.isLetter(currentC)){
                fenRowLength++;
            } else if (Character.isDigit(currentC)){
                fenRowLength += Character.getNumericValue(currentC);
            }
        }
        return fenRowLength;
    }

    //endregion

    //region BadType

    public static void throwBadTypeErrorIfNeeded(Object[] params){
        if (params.length == 2){
            throwBadTypeErrorIfNeeded(params[0], (String) params[1], "");
        } else if (params.length == 3){
            throwBadTypeErrorIfNeeded(params[0], (String) params[1], (String) params[2]);
        }else {
            throwBadType2ErrorIfNeeded(params[0], (String) params[1], (String) params[2], (String) params[3]);
        }
    }

    private static <T> void throwBadTypeErrorIfNeeded(T element, String neededClass, String plusMsg) {
        if (!element.getClass().getName().equals(neededClass))
            throw new ChessGameException(badTypeMsg(element, neededClass, plusMsg));
    }

    private static <T> String badTypeMsg(T element, String neededClass, String plusMsg){
        return element.toString() + " nem megfelelő típusú. " +
                "A kívánt típus: " + neededClass + " míg a kapott típus: " +
                element.getClass().getName() + ". " + plusMsg;
    }

    private static <T> void throwBadType2ErrorIfNeeded(T element, String neededClass, String secondNeededClass, String plusMsg) {
        if (!element.getClass().getName().equals(neededClass) && !element.getClass().getName().equals(secondNeededClass))
            throw new ChessGameException(badType2Msg(element, neededClass, secondNeededClass, plusMsg));
    }

    private static <T> String badType2Msg(T element, String neededClass, String secondNeededClass, String plusMsg){
        return element.toString() + " nem megfelelő típusú. " +
                "A kívánt típus: " + neededClass + " vagy " + secondNeededClass +
                ", \nmíg a kapott típus: " + element.getClass().getName() +
                ". " + plusMsg;
    }

    //endregion

    //endregion

}
