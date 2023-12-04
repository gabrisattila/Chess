package classes.Ai;


import classes.Game.Model.Structure.IPiece;
import classes.Game.Model.Structure.Move;
import lombok.*;

import java.util.*;

import static classes.Ai.Evaluator.*;
import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.Logger.logStep;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.Board.*;
import static classes.Game.Model.Structure.Move.*;

@Getter
@Setter
public class AiTree {

    //region Fields

    private String fen;

    private double finalValue;

    private Set<AiTree> children;

    //endregion


    //region Constructor

    public AiTree(){

        children = new HashSet<>();

    }

    public AiTree(String fen){

        this.fen = fen;
        children = new HashSet<>();
    }

    //endregion


    //region Methods

    //region Tree Methods

    public boolean equals(AiTree node){
        return getFen().equals(node.getFen());
    }

    public static void addToGraph(String fen){
        fen = removeStepCountFromFen(fen);
        if (fullTreeOfGame.containsKey(fen)){
            fullTreeOfGame.get(fen).add(new AiTree(fen));
        }else {
            fullTreeOfGame.put(fen, new HashSet<>());
        }
    }

    public static void addToHappenedList(String fen){
        fen = removeStepCountFromFen(fen);
        if (happenedList.containsKey(fen)){
            int count = happenedList.get(fen);
            count++;
            happenedList.put(fen, count);
        }else {
            happenedList.put(fen, 1);
        }
    }

    //endregion

    //region Possibility Collecting

    public ArrayList<String> collectPossibilities(boolean forWhite) {
        Map<Double, Set<String>> possibilities = new TreeMap<>(forWhite ? Comparator.<Double>reverseOrder() : Comparator.<Double>naturalOrder());

        HashMap<IPiece, Set<Move>> legalMoves = collectLegalMoves(forWhite);
        doAllLegalMoves(legalMoves, possibilities);

        return turnPossibilityMapToOneSet(possibilities);
    }

    private HashMap<IPiece, Set<Move>> collectLegalMoves(boolean forWhite)  {
        HashMap<IPiece, Set<Move>> legals;
        getBoard().addLegalMovesToPieces(forWhite);
        legals = getBoard().getAllLegalMoves(forWhite);
        return legals;
    }

    private void doAllLegalMoves(HashMap<IPiece, Set<Move>> legalMoves, Map<Double, Set<String>> possibilities) {
        for (IPiece p : legalMoves.keySet()) {
            for (Move m : legalMoves.get(p)) {
                Step(m);
                lastStep = logStep(m);
                String newPosFen = BoardToFen(getBoard());
                addToGraph(newPosFen);
                putToPossibilityMap(possibilities, evaluate(), newPosFen);
                FenToBoard(fen, getBoard());
                getBoard().rangeUpdater();
            }
        }
    }

    private void putToPossibilityMap(Map<Double, Set<String>> possibilities, double score, String fen){
        if (possibilities.containsKey(score)){
            possibilities.get(score).add(fen);
        }else {
            HashSet<String> set = new HashSet<>();
            set.add(fen);
            possibilities.put(score, set);
        }
    }

    private ArrayList<String> turnPossibilityMapToOneSet(Map<Double, Set<String>> possibilities){
        ArrayList<String> list = new ArrayList<>();
        for (double k : possibilities.keySet()) {
            list.addAll(possibilities.get(k));
        }
        return list;
    }

    //endregion

    //endregion

}
