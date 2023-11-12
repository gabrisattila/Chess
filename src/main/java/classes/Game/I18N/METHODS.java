package classes.Game.I18N;

import classes.Ai.FenConverter;
import classes.GUI.FrameParts.ViewPiece;
import classes.Game.Model.Structure.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.Timer;
import java.util.stream.Collectors;

import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.GUI.Frame.Window.*;

public class METHODS {

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

    public static void putTakenPieceToItsPlace(String fenOfCurrentState, String fenOfPreviousState) throws ChessGameException {

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

    public static void putTakenPieceToItsPlace(ViewPiece hit) throws ChessGameException {
        Objects.requireNonNull(getNextFreePlaceForTakenPiece(hit.isWhite())).setPiece(hit);
    }

    public static void convertOneBoardToAnother(IBoard what, IBoard to){
        try {
            FenToBoard(BoardToFen(what), to);
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    private static String changeWhiteToPlayInFen(String boardToFen) {
        StringBuilder sb = new StringBuilder(boardToFen);
        int startIndex = boardToFen.indexOf(" ");
        sb.setCharAt(startIndex + 1, 'w' == boardToFen.charAt(startIndex + 1) ? 'b' : 'w');
        return sb.toString();
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
            if(c2.contains(t)) {
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
        return set.stream().anyMatch(p -> p.EQUALS(element));
    }

    public static <T> boolean collectionNotContains(Collection<T> collection, T element){
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
