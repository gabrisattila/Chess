package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;

import java.sql.Array;
import java.util.ArrayList;

import static classes.Game.I18N.VARS.MUTUABLES.whiteToPlay;

public interface IBoard {

    public void boardSetUp();

    public void pieceSetUp(String FEN) throws ChessGameException;

    public IField getField(int i, int j);

    public IField getField(IPiece piece);

    public IField getField(Location loc);

    public ArrayList<ArrayList<IField>> getFields();

    public IPiece getPiece(int i, int j);

    public IPiece getPiece(Location loc);

    public IPiece getPiece(IField field);

    public ArrayList<IPiece> getPieces();

    public static void pieceChangeOnBoard(IPiece piece, IField from, IField to) {
        to.setPiece(piece);
        from.clean();
        whiteToPlay = !whiteToPlay;
    }
    public void cleanBoard() throws ChessGameException;

    public void updatePiecesRange()  throws ChessGameException, InterruptedException;

}
