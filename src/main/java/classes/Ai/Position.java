package classes.Ai;


import classes.Game.I18N.*;
import classes.Game.Model.Structure.*;
import lombok.*;

import java.util.ArrayList;

import static classes.Ai.FenConverter.*;
import static classes.Game.Model.Structure.Board.*;

@Getter
@Setter
public class Position {

    //region Fields

    private String fen;

    private int finalValue;

    //endregion


    //region Constructor

    public Position(){

    }

    public Position(String fen){

        this.fen = fen;

    }

    //endregion


    //region Methods

    public ArrayList<String> collectPossiblePositions() throws ChessGameException {
        FenToBoard(fen, getAiBoard());
        ArrayList<String> possibilities = new ArrayList<>();
        Location from;
        for (IPiece p : getAiBoard().getPieces()) {
            for (Location l : p.getPossibleRange()) {
                from = ((Piece)p).getLocation();
                p.STEP(from, l, getAiBoard());
                possibilities.add(BoardToFen(getAiBoard()));
                p.STEP(l, from, getAiBoard());
            }
        }
        return possibilities;
    }

    public static boolean isGameEndInPos(Position position){
        return false;
    }

    //endregion

}
