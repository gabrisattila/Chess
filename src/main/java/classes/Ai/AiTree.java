package classes.Ai;


import classes.Game.Model.Structure.IPiece;
import classes.Game.Model.Structure.Move;
import lombok.*;

import java.util.*;

import static classes.Ai.AI.*;
import static classes.Ai.FenConverter.*;
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

    public static void addToContinuousTree(String fen)  {
        AiTree child = new AiTree(fen);
        child.setFinalValue(evaluate(child));
        for (AiTree next = continuousTree; !next.getChildren().isEmpty(); next = ((AiTree)next.getChildren().toArray()[0])){
            next.getChildren().add(child);
        }
    }

    //endregion

    public boolean isGameEndInPos()  {
        return getBoard().isDraw() || getBoard().isCheckMate() || getBoard().isSubmitted();
    }

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
                putToPossibilityMap(possibilities, evaluate(), BoardToFen(getBoard()));
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

}
