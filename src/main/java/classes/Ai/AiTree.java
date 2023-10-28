package classes.Ai;


import classes.Game.I18N.*;
import classes.Game.Model.Structure.IPiece;
import classes.Game.Model.Structure.Move;
import lombok.*;

import java.util.*;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.METHODS.*;
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

    public static boolean isGameEndInPos(AiTree aiTree){
        return false;
    }

    public Set<String> collectPossibilities() throws ChessGameException {
        Set<String> possibilities = new HashSet<>();

        HashMap<IPiece, Set<Move>> legalMoves = collectLegalMoves();
        doAllLegalMoves(legalMoves, possibilities);

        return possibilities;
    }


    private HashMap<IPiece, Set<Move>> collectLegalMoves() throws ChessGameException {
        HashMap<IPiece, Set<Move>> legals;
        FenToBoard(fen, getAiBoard());
        getAiBoard().rangeUpdater();
        getAiBoard().addLegalMovesToPieces();
        legals = getAiBoard().getAllLegalMoves(whiteToPlay());
        return legals;
    }

    private void doAllLegalMoves(HashMap<IPiece, Set<Move>> legalMoves, Set<String> possibilities) throws ChessGameException {
        for (IPiece p : legalMoves.keySet()) {
            for (Move m : legalMoves.get(p)) {
                MOVE(getAiBoard(), m);
                possibilities.add(BoardToFen(getAiBoard()));
                FenToBoard(fen, getAiBoard());
            }
        }
    }



    //endregion

}
