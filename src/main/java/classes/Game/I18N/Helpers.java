package classes.Game.I18N;

import classes.GUI.FrameParts.ViewField;
import classes.Game.Model.Structure.*;

import java.util.*;

import static classes.GUI.FrameParts.ViewBoard.getViewBoard;
import static classes.Game.I18N.VARS.MUTABLE.*;

public class Helpers {

    public static void fieldNumPrinter(IBoard board){
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                ((ViewField)board.getField(i, j)).setText(i + " " + j);
            }
        }
    }

    public static void viewFieldPrinter(ArrayList<String> whatNeeded)  {
        String fieldInString = "";
        ViewField field;
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                field = ((ViewField) getViewBoard().getField(i, j));

                if (whatNeeded.contains("loc")){
                    fieldInString += field.getLoc().toString();
                    fieldInString += " ";
                }
                if (whatNeeded.contains("X")){
                    fieldInString += field.getX();
                    fieldInString += " ";
                }
                if (whatNeeded.contains("Y")){
                    fieldInString += field.getY();
                    fieldInString += " ";
                }
                if (whatNeeded.contains("row")) {
                    fieldInString += field.getRow();
                    fieldInString += " ";
                }
                if (whatNeeded.contains("col")){
                    fieldInString += field.getCol();
                    fieldInString += " ";
                }
                if (whatNeeded.contains("color")){
                    fieldInString += field.getFieldColor();
                    fieldInString += " ";
                }
                if (whatNeeded.contains("piece")){
                    if (field.isGotPiece()){
                        fieldInString += getViewBoard().getPiece(i, j).isWhite() ? "w_" : "b_";
                        fieldInString += getViewBoard().getPiece(i, j).getType();
                    }else {
                        fieldInString += "null";
                    }
                    fieldInString += " ";
                }

                System.out.print("[" + fieldInString + "]");
                fieldInString = "";
            }
            System.out.println("\n");
        }
    }

    public static String printBoardWithPieces(IBoard board, boolean inRed)  {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                if (board.getField(i, j).isGotPiece()){
                    if (board.getPiece(i, j).isWhite()){
                        sb.append("[").append(board.getPiece(i, j).getType()).append(", ").append(board.getPiece(i, j).getType()).append("] ");
                    }else {
                        sb.append("[").append(board.getPiece(i, j).getType().toLowerCase()).append(", ").append(board.getPiece(i, j).getType().toLowerCase()).append("] ");
                    }
                }else {
                    sb.append("[").append(i).append(", ").append(j).append("] ");
                }
            }
            sb.append('\n');
        }
        return inRed ? giveBackTextInRed(sb.toString()) : sb.toString();
    }

    public static void boardToString(IBoard board){
        for (IPiece p : board.getPieces()) {
            System.out.println(pieceOnBoardToString(p) + " " +
                    rangeToString(p.getPossibleRange()));
        }
    }

    public static String pieceOnBoardToString(IPiece piece){
        return "\n" + (piece.isWhite() ? "White " : "Black ") + piece.getType() + " on " +
                new Location(piece.getI(), piece.getJ()) + ":";
    }

    public static String rangeToString(Set<Location> range){
        StringBuilder sb = new StringBuilder();

        for (Location l : range) {
            sb.append(l.toString());
        }

        return sb.toString();
    }

    public static String giveBackTextInRed(String text){
        return "\u001B[31m" + text + "\u001B[0m";
    }

}
