package classes.Ai;


import classes.Game.I18N.*;
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

    public Set<String> collectPossibilities() {
        Set<String> possibilities = new HashSet<>();

        HashMap<IPiece, Set<Move>> legalMoves = collectLegalMoves();
        doAllLegalMoves(legalMoves, possibilities);

        return possibilities;
    }


    private HashMap<IPiece, Set<Move>> collectLegalMoves()  {
        HashMap<IPiece, Set<Move>> legals;
        getBoard().addLegalMovesToPieces();
        legals = getBoard().getAllLegalMoves(whiteToPlay);
        return legals;
    }

    private void doAllLegalMoves(HashMap<IPiece, Set<Move>> legalMoves, Set<String> possibilities) {
        for (IPiece p : legalMoves.keySet()) {
            for (Move m : legalMoves.get(p)) {
                Step(m);
                possibilities.add(BoardToFen(getBoard()));
                FenToBoard(fen, getBoard());
                getBoard().rangeUpdater();
            }
        }
    }



    //endregion

}
