package classes.Game.I18N;

import classes.GUI.FrameParts.ViewField;
import classes.Game.Model.Structure.*;

import java.util.*;

import static classes.Ai.Evaluator.*;
import static classes.Ai.FenConverter.charToPieceType;
import static classes.GUI.FrameParts.ViewBoard.getViewBoard;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.Board.getBoard;

public class Helpers {

    public static void fieldNumPrinter(IBoard board){
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                ((ViewField)board.getField(i, j)).setText(i + " " + j);
            }
        }
    }

    public static void viewFieldPrinter(ArrayList<String> whatNeeded)  {
        StringBuilder fieldInString = new StringBuilder();
        ViewField field;
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                field = ((ViewField) getViewBoard().getField(i, j));

                if (whatNeeded.contains("loc")){
                    fieldInString.append(field.getLoc().toString());
                    fieldInString.append(" ");
                }
                if (whatNeeded.contains("X")){
                    fieldInString.append(field.getX());
                    fieldInString.append(" ");
                }
                if (whatNeeded.contains("Y")){
                    fieldInString.append(field.getY());
                    fieldInString.append(" ");
                }
                if (whatNeeded.contains("row")) {
                    fieldInString.append(field.getRow());
                    fieldInString.append(" ");
                }
                if (whatNeeded.contains("col")){
                    fieldInString.append(field.getCol());
                    fieldInString.append(" ");
                }
                if (whatNeeded.contains("color")){
                    fieldInString.append(field.getFieldColor());
                    fieldInString.append(" ");
                }
                if (whatNeeded.contains("piece")){
                    if (field.isGotPiece()){
                        fieldInString.append(getViewBoard().getPiece(i, j).isWhite() ? "w_" : "b_");
                        fieldInString.append(getViewBoard().getPiece(i, j).getType());
                    }else {
                        fieldInString.append("null");
                    }
                    fieldInString.append(" ");
                }
                if (whatNeeded.contains("base")){
                    int z = whatNeeded.indexOf("base");
                    PieceType pieceType = charToPieceType(whatNeeded.get(z + 1).charAt(0));
                    String white = 'w' == whatNeeded.get(z + 1).charAt(1) ? "WHITE" : "BLACK";
                    Piece p = new Piece(new PieceAttributes(pieceType, white), new Location(i, j));
                    fieldInString.append(getBaseFieldValueFor(p));
                }
                if (whatNeeded.contains("watchW")){
                    fieldInString.append(((Field) getBoard().getField(field.getI(), field.getJ())).getWhiteWatcherCount());
                    fieldInString.append(" ");
                }
                if (whatNeeded.contains("watchB")){
                    fieldInString.append(((Field) getBoard().getField(field.getI(), field.getJ())).getBlackWatcherCount());
                    fieldInString.append(" ");
                }

                System.out.print("[" + fieldInString + "]");
                fieldInString = new StringBuilder();
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

    public static void printBitBoardIndexes(){
        for (int i = 63; i >= 0; i--) {
            System.out.print((i < 10 ? " " : "") + i + " ");
            if (i % 8 == 0)
                System.out.println();
        }
        System.out.println();
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
