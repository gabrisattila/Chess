package classes.Game.Model.Structure;

import classes.Game.I18N.Location;
import classes.Game.I18N.PieceType;

public interface IPiece {

    public int getI();

    public int getJ();

    public PieceType getType();

    public boolean isWhite();

    public void STEP(Location from, Location to, IBoard board);

    public void updateRange();

}
