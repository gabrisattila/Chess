package classes.Game.I18N;

import classes.Ai.AI;
import classes.GUI.FrameParts.ViewField;
import classes.Game.Model.Structure.*;

import java.util.ArrayList;
import java.util.Set;

import static classes.Game.I18N.VARS.MUTUABLES.*;

public class Helpers {

    public static void fieldNums(ArrayList<ArrayList<ViewField>> fields){
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                fields.get(i).get(j).setText(i + " " + j);
            }
        }
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

}
