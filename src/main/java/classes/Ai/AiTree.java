package classes.Ai;


import classes.Game.I18N.*;
import classes.Game.Model.Structure.IPiece;
import classes.Game.Model.Structure.Move;
import classes.Game.Model.Structure.Piece;
import lombok.*;

import java.util.*;

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

    public boolean isGameEndInPos() throws ChessGameException {
        return getAiBoard().isDraw() || getAiBoard().isCheckMate();
    }

    public Set<String> collectPossibilities() throws ChessGameException {
        Set<String> possibilities = new HashSet<>();

        HashMap<IPiece, Set<Move>> legalMoves = collectLegalMoves();
        doAllLegalMoves(legalMoves, possibilities);

        return possibilities;
    }


    private HashMap<IPiece, Set<Move>> collectLegalMoves() throws ChessGameException {
        HashMap<IPiece, Set<Move>> legals;
        getAiBoard().addLegalMovesToPieces();
        legals = getAiBoard().getAllLegalMoves(whiteToPlay);
        for (IPiece p : legals.keySet()) {
            for (Move m : ((Piece) p).getLegalMoves()) {
                System.out.print(m.getTo().toString());
            }
            System.out.println();
        }
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
