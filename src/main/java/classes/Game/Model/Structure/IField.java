package classes.Game.Model.Structure;

import classes.Game.I18N.PieceAttributes;

public interface IField {

    public int getI();

    public int getJ();

    public IPiece getPiece();

    public boolean isGotPiece();

    public void setPiece(IPiece piece);

    public void setPiece(PieceAttributes piece);

    public void clean();

}
