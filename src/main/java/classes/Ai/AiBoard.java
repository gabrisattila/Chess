package classes.Ai;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.Model.Structure.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

import static classes.Game.I18N.METHODS.isNull;
import static classes.Game.I18N.VARS.MUTUABLES.MAX_HEIGHT;
import static classes.Game.I18N.VARS.MUTUABLES.MAX_WIDTH;

@Getter
@Setter
public class AiBoard implements IBoard {

    //region Fields

    private static AiBoard aiBoard;

    //endregion


    //region Constructor

    protected AiBoard(int x, int y, Class<Field> fieldClass) throws ChessGameException {
        boardSetUp();
    }

    public static AiBoard getAiBoard() throws ChessGameException {
        if (isNull(aiBoard)){
            aiBoard = new AiBoard(MAX_WIDTH, MAX_HEIGHT, Field.class);
        }
        return aiBoard;
    }

    //endregion


    //region Methods

    public void calculate(){

    }

    @Override
    public void boardSetUp() {

    }

    @Override
    public void pieceSetUp(String FEN) throws ChessGameException {

    }

    @Override
    public IField getField(int i, int j) {
        return null;
    }

    @Override
    public IField getField(IPiece piece) {
        return null;
    }

    @Override
    public IField getField(Location loc) {
        return null;
    }

    @Override
    public ArrayList<ArrayList<IField>> getFields() {
        return null;
    }

    @Override
    public IPiece getPiece(int i, int j) {
        return null;
    }

    @Override
    public IPiece getPiece(Location loc) {
        return null;
    }

    @Override
    public IPiece getPiece(IField field) {
        return null;
    }

    @Override
    public ArrayList<IPiece> getPieces() {
        return null;
    }

    @Override
    public void cleanBoard() throws ChessGameException {

    }

    @Override
    public void updatePiecesRange() throws ChessGameException, InterruptedException {

    }

    //endregion
}
