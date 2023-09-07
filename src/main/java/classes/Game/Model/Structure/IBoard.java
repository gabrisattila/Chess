package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;

public interface IBoard {

    public void boardSetUp();

    public void pieceSetUp(String FEN);

    public IField getField(int i, int j);

    public IField getField(IPiece piece);

    public IField getField(Location loc);

    public IPiece getPiece(int i, int j);

    public IPiece getPiece(Location loc);

    public IPiece getPiece(IField field);

    public void pieceChangeOnBoard(IPiece piece, IField from, IField to);

    public void cleanBoard() throws ChessGameException;

    public void updatePiecesRange();

}
