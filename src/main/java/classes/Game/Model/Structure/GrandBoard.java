package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;

import java.util.ArrayList;

import static classes.Game.I18N.METHODS.tableIf;
import static classes.Game.I18N.VARS.FINALS.BLACK_STRING;
import static classes.Game.I18N.VARS.FINALS.WHITE_STRING;
import static classes.Game.I18N.VARS.MUTUABLES.whiteToPlay;

public abstract class GrandBoard {

    //region Methods

    protected int X;

    protected int Y;

    //endregion


    //region Constructor



    //endregion


    //region Methods


    public abstract void boardSetUp();

    public abstract void pieceSetUp(String FEN) throws ChessGameException;

    public abstract IField getField(int i, int j);

    public abstract IField getField(Location loc);

    public abstract ArrayList<ArrayList<IField>> getFields();

    public abstract IPiece getPiece(int i, int j);

    public abstract IPiece getPiece(Location loc);

    public abstract IPiece getPiece(IField field);

    public abstract ArrayList<IPiece> getPieces();

    public static void pieceChangeOnBoard(IPiece piece, IField from, IField to) {
        to.setPiece(piece);
        from.clean();
        whiteToPlay = !whiteToPlay;
    }
    public abstract void cleanBoard() throws ChessGameException;

    public abstract void updatePiecesRanges()  throws ChessGameException, InterruptedException;


    //endregion

}
