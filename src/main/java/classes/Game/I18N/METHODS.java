package classes.Game.I18N;

import classes.Ai.AI;
import classes.Game.Model.Structure.Board;
import classes.Game.Model.Structure.Field;

import javax.swing.*;
import java.util.concurrent.BlockingQueue;

import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;
import static classes.Game.Model.Logic.EDT.*;

public class METHODS {

    public static void aiMove() throws InterruptedException {
        if (theresOnlyOneAi)
            SwingUtilities.invokeLater(() -> aiAction(whiteAiNeeded ? aiW : aiB));
        else
            while (gameIsOn){
                Thread.sleep(500);
                SwingUtilities.invokeLater(() -> aiAction(whiteToPlay ? aiW : aiB));
            }
    }

    private static void aiAction(AI ai){
        if (ai.isAlive()){
            synchronized (ai){
                ai.notify();
            }
        }else {
            ai.start();
        }
    }

    public static <F> void passViewBoardInFenTo(Board<F> board) throws ChessGameException, InterruptedException {
        putToFenQueue(BoardToFen(getViewBoard()), rightQueue(whiteToPlay ? "WHITE" : "BLACK"));
        board.pieceSetUp(takeFromFenQueue(rightQueue(whiteToPlay ? "WHITE" : "BLACK")));
    }

    public static boolean containsLocation(int i, int j){
        return containsLocation(MAX_WIDTH, MAX_HEIGHT, i, j);
    }

    public static boolean containsLocation(int maxWidth, int maxHeight, int x, int y){
        return x >= 0 && x <= maxWidth && y >= 0 && y <= maxHeight;
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

    public static <T> Field castToField(Board<T> board, int i, int j){
        return (Field)(board.getField(i, j));
    }

    public static <T> Field castToField(T fieldInT){
        return (Field) fieldInT;
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

    public static void putToFenQueue(String fen, BlockingQueue<String> queue) throws InterruptedException {
        queue.put(fen);
    }

    public static String takeFromFenQueue(BlockingQueue<String> queue) throws InterruptedException {
        return queue.take();
    }

    public static BlockingQueue<String> rightQueue(String color){
        return WHITE_STRING.equals(color) ? fenChannelFirst : fenChannelSecond;
    }

}
