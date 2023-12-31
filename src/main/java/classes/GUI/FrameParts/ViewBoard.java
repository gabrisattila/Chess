package classes.GUI.FrameParts;

import classes.Model.I18N.Location;
import classes.Model.Structure.IBoard;
import classes.Model.Structure.IField;
import classes.Model.Structure.IPiece;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import static classes.Model.AI.Ai.AI.*;
import static classes.Model.I18N.METHODS.*;
import static classes.Model.I18N.PieceType.P;
import static classes.Model.I18N.VARS.FINALS.*;
import static classes.Model.Structure.Board.*;
import static classes.Model.Structure.GameOverOrPositionEnd.*;

@Getter
@Setter
public class ViewBoard extends Component implements IBoard {

    //region Fields

    private int X;

    private int Y;

    private static ViewBoard viewBoard;

    private ArrayList<ArrayList<IField>> fields;

    private ArrayList<IPiece> pieces;

    //endregion


    //region Constructor

    protected ViewBoard(int x, int y) {
        X = x;
        Y = y;
        fields = new ArrayList<>();
        boardSetUp(this, fields);
        pieces = new ArrayList<>();
    }

    public static ViewBoard getViewBoard()  {
        if (isNull(viewBoard)){
            viewBoard = new ViewBoard(MAX_WIDTH, MAX_HEIGHT);
        }
        return viewBoard;
    }

    //endregion


    //region Methods

    //region GetBy

    @Override
    public IField getField(int i, int j){
        return getFields().get(i).get(j);
    }

    @Override
    public IField getField(Location Location){
        return getField(Location.getI(), Location.getJ());
    }

    @Override
    public IField getField(IPiece piece){
        return getField(piece.getI(), piece.getJ());
    }

    @Override
    public IPiece getPiece(int i, int j){
        for (IPiece p : pieces) {
            if (p.getI() == i && p.getJ() == j)
                return p;
        }
        return null;
    }

    @Override
    public IPiece getPiece(Location Location){
        return getPiece(Location.getI(), Location.getJ());
    }


    //endregion

    @Override
    public void cleanBoard(){
        for (int i = 0; i < MAX_HEIGHT; i++) {
            for (int j = 0; j < MAX_WIDTH; j++) {
                if (getField(i, j).isGotPiece()) {
                    getField(i, j).clean();
                }
            }
        }
        pieces.clear();
    }

    @Override
    public void rangeUpdater() {

        GameOverDecision(this, Double.MIN_VALUE);

        if (gameIsntFinished()) {
            for (int i = 0; i < MAX_HEIGHT; i++) {
                for (int j = 0; j < MAX_WIDTH; j++) {
                    if (getBoard().getField(i, j).isGotPiece() && notNull(getBoard().getPiece(i, j).getPossibleRange())) {
                        getPiece(i, j).getPossibleRange().addAll(getBoard().getPiece(i, j).getPossibleRange());
                        if (getPiece(i, j).getType() == P) {
                            getPiece(i, j).getWatchedRange().addAll(getBoard().getPiece(i, j).getWatchedRange());
                        }
                    }
                }
            }
        }
    }

    public void setFieldColorsToNormal(){
        for (ArrayList<IField> fields : getFields()) {
            for (IField f : fields) {
                ((ViewField) f).setBackColorToNormal();
            }
        }
    }

    public void clearPiecesRanges(){
        for (IPiece p : pieces) {
            ((ViewPiece) p).setPossibleRange(new HashSet<>());
        }
    }

    //endregion

}
