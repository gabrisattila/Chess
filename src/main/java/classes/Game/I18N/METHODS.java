package classes.Game.I18N;

import classes.GUI.FrameParts.ViewPiece;
import classes.Game.Model.Structure.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.Timer;
import java.util.stream.Collectors;

import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ChessButton.ChessButtonMouseListener.saveBoard;
import static classes.GUI.FrameParts.ViewBoard.getViewBoard;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.GUI.Frame.Window.*;
import static java.util.Objects.requireNonNull;

public class METHODS {

    public static Double[][] mirrorMatrixHorizontally(Double[][] matrix){
        int rows = matrix.length;
        int cols = matrix[0].length;
        Double[][] mirroredMatrix = new Double[rows][cols];

        for (int i = 0; i < rows; i++) {
            System.arraycopy(matrix[rows - 1 - i], 0, mirroredMatrix[i], 0, cols);
        }
        return mirroredMatrix;
    }

    public static Double[][] mirrorMatrixVertically(Double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        Double[][] mirroredMatrix = new Double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mirroredMatrix[i][j] = matrix[i][cols - 1 - j];
            }
        }

        return mirroredMatrix;
    }


    public static void changeEvenOrOddStep(){
        if (evenOrOddStep == 0)
            evenOrOddStep = 1;
        else
            evenOrOddStep = 0;
    }

    public static void switchWhoComes(){
        aiTurn = !aiTurn;
        playerTurn = !playerTurn;
    }


    public static String dateToString(Date date){

        String[] dateParts = String.valueOf(date).split(" ");
        String month = dateParts[1], day = dateParts[2], hourMinSec = dateParts[3], year =  dateParts[5];

        return year +
                "_" +
                month +
                "_" +
                day +
                "_" +
                hourMinSec.replace(':', '-');
    }


    public static <T> Collection<T> union(Collection<T> set1, Collection<T> set2) {
        Set<T> set = new HashSet<>();

        set.addAll(set1);
        set.addAll(set2);

        return set;
    }

    public static <T> Collection<T> intersection(Collection<T> c1, Collection<T> c2){
        Collection<T> list = new HashSet<>();

        for (T t : c1) {
            if (c1.toArray()[0] instanceof Location){
                if (c2.stream().anyMatch(l -> l.equals(t))){
                    list.add(t);
                }
            }else if(c2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    public static <T> Collection<T> minus(Collection<T> c1, Collection<T> c2){
        Collection<T> C1MinusC2 = new HashSet<>();
        for (T t : c1) {
            if (collectionNotContains(c2, t))
                C1MinusC2.add(t);
        }
        return C1MinusC2;
    }

    public static boolean locationCollectionContains(Set<Location> set, Location element){
        return set.stream().anyMatch(p -> p.equals(element));
    }

    public static <T> boolean collectionNotContains(Collection<T> collection, T element){
        if (element instanceof Location){
            return collection.stream().noneMatch(l -> l.equals(element));
        }
        return !collection.contains(element);
    }

    public static boolean containsLocation(Location Location){
        return containsLocation(Location.getI(), Location.getJ());
    }

    public static boolean containsLocation(int i, int j){
        return containsLocation(MAX_WIDTH, MAX_HEIGHT, i, j);
    }

    public static boolean containsLocation(int maxWidth, int maxHeight, int x, int y){
        return x >= 0 && x < maxWidth && y >= 0 && y < maxHeight;
    }

    public static Set<Location> locationSetTimesN(Set<Location> list, int n){
        return list.stream().map(t -> t.times(n)).collect(Collectors.toSet());
    }



    public static boolean isNull(Object o){
        return o == null;
    }

    public static boolean notNull(Object o) {
        return o != null;
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
                    case 'p' -> stringBuilder.append('p');
                    case 'P' -> stringBuilder.append('P');
                    case 'n' -> stringBuilder.append('n');
                    case 'N' -> stringBuilder.append('N');
                    case 'b' -> stringBuilder.append('b');
                    case 'B' -> stringBuilder.append('B');
                    case 'r' -> stringBuilder.append('r');
                    case 'R' -> stringBuilder.append('R');
                    case 'q' -> stringBuilder.append('q');
                    case 'Q' -> stringBuilder.append('Q');
                }
            }else {
                stringBuilder.append(current);
            }
        }
        return stringBuilder.toString();
    }


    public static void showFlashFrame(String message, int durationInSeconds){

        JFrame flashFrame = new JFrame("Mentés");
        flashFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        flashFrame.setSize(400, 200);
        flashFrame.getContentPane().setBackground(BLACK);

        JLabel label = new JLabel("<html><div style='text-align: center;'>" + message.replace("\n", "<br>") + "</div></html>");
        label.setForeground(WHITE);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Source Code Pro", Font.BOLD, 20));

        flashFrame.add(label);
        flashFrame.setLocationRelativeTo(null);

        Timer timer = new Timer(durationInSeconds * 1000, e -> flashFrame.dispose());

        timer.setRepeats(false);

        flashFrame.setVisible(true);
        timer.start();

    }

}
