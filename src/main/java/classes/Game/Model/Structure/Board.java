package classes.Game.Model.Structure;


import classes.GUI.FrameParts.*;
import classes.Game.I18N.*;
import lombok.*;

import java.util.ArrayList;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;


@Getter
@Setter
public class Board extends GrandBoard {

    //region Fields

    private int X;

    private int Y;

    private static Board board;

    private ArrayList<ArrayList<Field>> fields;

    protected ArrayList<Piece> pieces = new ArrayList<>();

    //endregion


    //region Constructor

    protected Board(int x, int y) throws ChessGameException {
        X = x;
        Y = y;
        boardSetUp();
    }

    public static Board getBoard() throws ChessGameException {
        if(board == null){
            return board = new Board(MAX_WIDTH, MAX_HEIGHT);
        }
        return board;
    }

    //endregion


    //region Methods

    //region SetUp

    public void boardSetUp(){

        IField field;
        String fieldColor;
        Location location;
        ArrayList<Field> row;

        for (int i = 0; i < X; i++) {
            row = new ArrayList<>();
            for (int j = 0; j < Y; j++) {
                location = new Location(i, j);
                fieldColor = tableIf(WHITE_STRING, BLACK_STRING, i, j);
                if (Field.class.equals(fields.getClass())) {
                    field = new Field(location, fieldColor);
                }else {

                }
                row.add(field);
            }
            fields.add(row);
        }
    }

    public void pieceSetUp(String FEN) throws ChessGameException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < FEN.length(); i++) {
            if (Character.isLetter(FEN.charAt(i))){
                sb.append(Character.toLowerCase(FEN.charAt(i)));
            }else {
                sb.append(FEN.charAt(i));
            }
        }
        String fen = sb.toString();
        if (fen.contains("p") || fen.contains("r") || fen.contains("q") ||
            fen.contains("n")){
            FEN = translate(FEN);
        }
        FenToBoard(FEN, this);
    }

    //endregion


    //region GetBy

    @Override
    public IField getField(int i, int j){
        return fields.get(i).get(j);
    }

    @Override
    public IField getField(Location location){
        return getField(location.getI(), location.getJ());
    }

    @Override
    public IPiece getPiece(int i, int j){
        return fields.get(i).get(j).getPiece();
    }

    @Override
    public IPiece getPiece(Location location){
        return getPiece(location.getI(), location.getJ());
    }

    @Override
    public IPiece getPiece(IField field){
        return fields.get(field.getI()).get(field.getJ()).getPiece();
    }

    //endregion


    @Override
    public void cleanBoard() throws ChessGameException {
        for (ArrayList<Field> row : fields) {
            for (IField f : row) {
                if (!(f instanceof Field) && !(f instanceof ViewField)){
                    throw new ChessGameException(BAD_TYPE_MSG);
                }

                if (f instanceof Field && ((Field) f).isGotPiece()){
                    ((Field) f).setPiece((Piece) null);
                } else if (f instanceof ViewField && ((ViewField) f).isGotPiece()) {
                    ((ViewField) f).setPiece((ViewPiece) null);
                }
            }
        }
        pieces.clear();
    }


    @Override
    public void updatePiecesRanges() throws ChessGameException, InterruptedException {
//        setEnemyInDefendBasedOnWatching();
        for (Piece p : pieces) {
            p.updateRange();
        }
//        setInDefendBasedOnWatching();
    }


    //endregion

}
