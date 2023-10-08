package classes.Game.I18N;

import classes.Game.Model.Structure.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;

public class METHODS {

    public static void switchWhoComes(){
        aiTurn = !aiTurn;
        playerTurn = !playerTurn;
    }

    public static void convertOneBoardToAnother(IBoard what, IBoard to){
        try {
            FenToBoard(BoardToFen(what), to);
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean containsLocation(Location location){
        return containsLocation(location.getI(), location.getJ());
    }

    public static boolean containsLocation(int i, int j){
        return containsLocation(MAX_WIDTH, MAX_HEIGHT, i, j);
    }

    public static boolean containsLocation(int maxWidth, int maxHeight, int x, int y){
        return x >= 0 && x < maxWidth && y >= 0 && y < maxHeight;
    }

    public static boolean isNull(Object o){
        return o == null;
    }

    public static boolean notNull(Object o) {
        return o != null;
    }

    public static <T> T tableIf(T o2, T o3, int i, int j){
        T o1;
        if(i % 2 == 0){
            if (j % 2 == 0){
                o1 = o3;
            }else {
                o1 = o2;
            }
        }else {
            if (j % 2 == 0){
                o1 = o2;
            }else {
                o1 = o3;
            }
        }
        return o1;
    }

    public static void pieceChangeOnBoard(IPiece piece, IField from, IField to) {
        try {
            to.setPiece(piece);
            from.clean();
            whiteToPlay = !whiteToPlay;
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Location> locationListTimesN(ArrayList<Location> list, int n){
        return list.stream().map(t -> t.times(n)).collect(Collectors.toList());
    }

    public static String replace(String original, int index, char newChar) {
        if (index < 0 || index >= original.length()) {
            throw new IndexOutOfBoundsException("Invalid index");
        }

        char[] charArray = original.toCharArray();
        charArray[index] = newChar;
        return new String(charArray);
    }

    public static int countOccurrences(String text, char targetChar) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == targetChar) {
                count++;
            }
        }
        return count;
    }

    public static String translate(String englishFen){
        StringBuilder stringBuilder = new StringBuilder();
        char current;
        for (int i = 0; i < englishFen.length(); i++) {
            current = englishFen.charAt(i);
            if (current != 'k' && current != 'K' &&
                    Character.isLetter(current)) {
                switch (current) {
                    case 'p' -> stringBuilder.append('g');
                    case 'P' -> stringBuilder.append('G');
                    case 'n' -> stringBuilder.append('h');
                    case 'N' -> stringBuilder.append('H');
                    case 'b' -> stringBuilder.append('f');
                    case 'B' -> stringBuilder.append('F');
                    case 'r' -> stringBuilder.append('b');
                    case 'R' -> stringBuilder.append('B');
                    case 'q' -> stringBuilder.append('v');
                    case 'Q' -> stringBuilder.append('V');
                }
            }else {
                stringBuilder.append(current);
            }
        }
        return stringBuilder.toString();
    }

    public static Piece firstEmptyWhite(){
        return whitePieceSet.getFirstEmpty();
    }

    public static Piece firstEmptyBlack(){
        return blackPieceSet.getFirstEmpty();
    }

}
