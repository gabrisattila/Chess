package classes.Model.Game.I18N;

import classes.Model.Game.Structure.IPiece;
import classes.Model.Game.Structure.Piece;

import java.util.ArrayList;

public class PieceSet extends ArrayList<IPiece> {

    public PieceSet(){
        super();
    }

    public Piece getFirstEmpty(){
        return (Piece) super.stream().filter(IPiece::isEmpty).findFirst().orElseThrow(
                () -> new RuntimeException("Nincs \"üres\" elem.\n")
        );
    }

    public void clean(){
        for (IPiece p : super.stream().toList()) {
            p.setEmpty();
        }
    }



    public static Piece firstEmptyWhite(){
        return VARS.MUTABLE.whitePieceSet.getFirstEmpty();
    }

    public static Piece firstEmptyBlack(){
        return VARS.MUTABLE.blackPieceSet.getFirstEmpty();
    }


}
