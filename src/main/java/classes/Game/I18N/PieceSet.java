package classes.Game.I18N;

import classes.Game.Model.Structure.IPiece;
import classes.Game.Model.Structure.Piece;

import java.util.ArrayList;

import static classes.Game.I18N.VARS.MUTABLE.blackPieceSet;
import static classes.Game.I18N.VARS.MUTABLE.whitePieceSet;

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
        return whitePieceSet.getFirstEmpty();
    }

    public static Piece firstEmptyBlack(){
        return blackPieceSet.getFirstEmpty();
    }


}
