package classes.Game.I18N;

import classes.GUI.FrameParts.ViewField;
import classes.Game.Model.Structure.*;

import java.util.*;

import static classes.Game.I18N.VARS.MUTABLE.*;

public class Helpers {

    public static void fieldNums(ArrayList<ArrayList<ViewField>> fields){
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                fields.get(i).get(j).setText(i + " " + j);
            }
        }
    }

    public static String printBoardWithPieces(IBoard board, boolean inRed) throws ChessGameException {
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
