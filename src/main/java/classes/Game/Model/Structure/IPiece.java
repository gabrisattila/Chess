package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;

import java.util.Set;

public interface IPiece {

    public PieceAttributes getAttributes();

    public PieceType getType();

    public boolean isWhite();

    public Set<Location> getPossibleRange();

    public void STEP(Location from, Location to, GrandBoard board);

    public void updateRange() throws ChessGameException;


}
