package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceType;

import java.util.Set;

public interface IPiece {

    public int getI();

    public int getJ();

    public PieceType getType();

    public boolean isWhite();

    public Set<Location> getPossibleRange();

    public Set<Location> getWatchedRange();

    public void setLocation(Location location);

    public void STEP(Location from, Location to, IBoard board);

    public void updateRange() throws ChessGameException;


}
