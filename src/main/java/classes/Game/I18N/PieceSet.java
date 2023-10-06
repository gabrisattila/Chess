package classes.Game.I18N;

import classes.Game.Model.Structure.*;

import java.util.ArrayList;

public class PieceSet extends ArrayList<IPiece> {

    public PieceSet(){
        super();
    }

    public PieceSet(Piece piece){
        super();
        add(piece);
    }

    public PieceSet(PieceSet pieceSet){
        super(pieceSet);
    }

    public IPiece getByIJ(int i, int j){
        for (IPiece p: super.stream().toList()) {
            if (p.getI() == i && p.getJ() == j){
                return p;
            }
        }
        throw new RuntimeException("Nincs a megadott helyen figura.\n");
    }

    public Piece getFirstEmpty(){
        return (Piece) super.stream().filter(IPiece::isEmpty).findFirst().orElseThrow(
                () -> new RuntimeException("Nincs \"üres\" elem.\n")
        );
    }

}
