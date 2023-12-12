package classes.Game.I18N;

import classes.GUI.FrameParts.ViewPiece;
import classes.Game.Model.Structure.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.Timer;
import java.util.stream.Collectors;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.GUI.Frame.Window.*;

public class METHODS {

    public static void exceptionIgnorer(){
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (gameEndFlag.get()){
                System.err.println("Unhandled exception caught: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

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

    public static void waitOnPause(){
        while(pauseFlag.get()) {
            try {
                pauseFlag.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
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

    public static void putTakenPieceToItsPlace(String fenOfCurrentState, String fenOfPreviousState) {

        ArrayList<Character> prev = (ArrayList<Character>) fenOfPreviousState
                .chars()
                .filter(Character::isLetter)
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        ArrayList<Character> current = (ArrayList<Character>) fenOfCurrentState
                .chars()
                .filter(Character::isLetter)
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());

        if (current.size() != prev.size()){

            Collections.sort(prev);
            Collections.sort(current);
            char thePiece = '0';
            for (int i = 0; i < prev.size(); i++) {
                if (i == prev.size() - 1){
                    thePiece = prev.get(i);
                    break;
                }
                if (prev.get(i) != current.get(i)){
                    thePiece = prev.get(i);
                    break;
                }
            }
            PieceAttributes piece = charToPieceAttributes(thePiece);
            ViewPiece hit = new ViewPiece(createSourceStringFromGotAttributes(piece), piece);
            putTakenPieceToItsPlace(hit);
        }
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

    public static void putTakenPieceToItsPlace(ViewPiece hit)  {
        Objects.requireNonNull(getNextFreePlaceForTakenPiece(hit.isWhite())).setPiece(hit);
    }

    public static void convertOneBoardToAnother(IBoard what, IBoard to){
        FenToBoard(BoardToFen(what), to);
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
                if (c2.stream().anyMatch(l -> ((Location) l).equals((Location) t))){
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

    public static <T> Collection<T> minus(Collection<T> c, T t){
        return c.stream().filter(c1 -> !c1.equals(t)).collect(Collectors.toSet());
    }

    public static boolean locationCollectionContains(Set<Location> set, Location element){
        return set.stream().anyMatch(p -> p.equals(element));
    }

    public static <T> boolean collectionNotContains(Collection<T> collection, T element){
        if (element instanceof Location){
            return collection.stream().noneMatch(l -> ((Location) l).equals((Location) element));
        }
        return !collection.contains(element);
    }

    public static <T> boolean collectionContains(Collection<T> collection, T element){
        return collection.contains(element);
    }

    public static boolean notContainsLocation(Location Location){
        return !containsLocation(Location.getI(), Location.getJ());
    }

    public static boolean notContainsLocation(int i, int j){
        return !containsLocation(MAX_WIDTH, MAX_HEIGHT, i, j);
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

    public static Set<Location> locationSetTimesN(Set<Location> list, int n){
        return list.stream().map(t -> t.times(n)).collect(Collectors.toSet());
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

    public static Piece firstEmptyWhite(){
        return whitePieceSet.getFirstEmpty();
    }

    public static Piece firstEmptyBlack(){
        return blackPieceSet.getFirstEmpty();
    }

    public static ArrayList<Location> getCastleMatrixFor(boolean isWhite){
        return castleMatrix(isWhite, (theresOnlyOneAi || !whiteAiNeeded));
    }

    public static void showFlashFrame(String message, int durationInSeconds){

        JFrame flashFrame = new JFrame("Mentés");
        flashFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        flashFrame.setSize(400, 200);
        flashFrame.getContentPane().setBackground(BLACK);

        JLabel label = new JLabel(message);
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

    private static ArrayList<Location> castleMatrix(boolean isWhite, boolean whiteDown){
        int i = isWhite ? 0 : MAX_HEIGHT - 1;
        Pair<Integer, Integer> js = new Pair<>();
        js.setFirst(whiteDown ? 2 : 1);
        js.setSecond(whiteDown ? 5 : 6);
        return new ArrayList<>() {{
            add(new Location(i, js.getFirst()));
            add(new Location(i, js.getSecond()));
        }};
    }

}
