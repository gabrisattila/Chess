package classes.Game.Model.Structure;


import classes.GUI.FrameParts.*;
import classes.Game.I18N.*;
import lombok.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static classes.Ai.FenConverter.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;


@Getter
@Setter
public class Board<F> {

    //region Fields

    private int X;

    private int Y;

    private ArrayList<ArrayList<F>> fields;

    private final Class<F> fieldType;

    private static Board<Field> board;

    protected ArrayList<Piece> pieces = new ArrayList<>();

    //endregion


    //region Constructor

    protected Board(int x, int y, Class<F> fieldType) throws ChessGameException {
        X = x;
        Y = y;
        this.fieldType = fieldType;
        if (fieldType != Field.class && fieldType != ViewField.class){
            throw new ChessGameException("This board can't be created because the type of it's fields isn't Field or ViewField");
        }
        boardSetUp();
    }

    public static Board<Field> getBoard() throws ChessGameException {
        if(board == null){
            return board = new Board<>(MAX_WIDTH, MAX_HEIGHT, Field.class);
        }
        return board;
    }

    //endregion


    //region Methods

    //region SetUp

    protected void boardSetUp(){
        fields = new ArrayList<>();

        F field;
        String fieldColor;
        Location location;
        ArrayList<F> row;

        try {

            Constructor<F> constructor = fieldType.getConstructor(Location.class, String.class);

            for (int i = 0; i < X; i++) {
                row = new ArrayList<>();
                for (int j = 0; j < Y; j++) {
                    location = new Location(i, j);
                    fieldColor = tableIf(WHITE_STRING, BLACK_STRING, i, j);
                    field = constructor.newInstance(location, fieldColor);
                    row.add(field);
                }
                fields.add(row);
            }

        }catch (InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
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

    public F getFieldByIJFromBoard(int i, int j){
        return fields.get(i).get(j);
    }

    public F getFieldByLocation(Location location){
        return fields.get(location.getI()).get(location.getJ());
    }

    public Piece getPieceByIJFromBoard(int i, int j){
        return ((Field)fields.get(i).get(j)).getPiece();
    }

    public Piece getPieceByLocationFromBoard(Location location){
        return getPieceByIJFromBoard(location.getI(), location.getJ());
    }

    public F getFieldByPieceFromBoard(Piece p){
        return fields.get(p.getI()).get(p.getJ());
    }

    //endregion

    public void takePieceFromAToB(Piece piece, Field A, Field B){
        Piece change = new Piece(piece.attributes);
        B.setPiece(change);
        A.setPiece((Piece) null);
    }

    public void cleanBoard() throws ChessGameException {
        for (ArrayList<F> row : fields) {
            for (F f : row) {
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
    }

    //endregion

}
