package classes.Game.Model.Structure;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import lombok.*;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;

@Getter
@Setter
public class Move {

    //region Fields

    private IPiece what;

    private Location from;

    private Location to;

    private IPiece hit;

    private String emPassantOrCastle;

    private IBoard boardToMoveOn;

    //endregion


    //region Constructor

    public Move(){
        what = null;
        from = null;
        to = null;
        hit = null;
        emPassantOrCastle = "-";
    }

    public Move(IPiece what, Location from, Location to){
        this.what = what;
        this.from = from;
        this.to = to;
        hit = null;
        emPassantOrCastle = "-";
    }

    public Move(IPiece what, Location from, Location to, IPiece hit){
        this.what = what;
        this.from = from;
        this.to = to;
        this.hit = hit;
        emPassantOrCastle = "-";
    }

    public Move(IPiece what, Location from, Location to, String emPassantOrCastle){
        this.what = what;
        this.from = from;
        this.to = to;
        hit = null;
        this.emPassantOrCastle = emPassantOrCastle;
    }

    public Move(IBoard boardToMoveOn){
        this.boardToMoveOn = boardToMoveOn;
    }

    public Move(IBoard boardToMoveOn, IPiece what, Location from, Location to, IPiece hit){
        this.boardToMoveOn = boardToMoveOn;
        this.what = what;
        this.from = from;
        this.to = to;
        this.hit = hit;
        emPassantOrCastle = "-";
    }

    public Move(IBoard boardToMoveOn, IPiece what, Location from, Location to, String emPassantOrCastle){
        this.boardToMoveOn = boardToMoveOn;
        this.what = what;
        this.from = from;
        this.to = to;
        hit = null;
        this.emPassantOrCastle = emPassantOrCastle;
    }

    //endregion


    //region Methods

    public static void pieceChangeOnBoard(IPiece piece, IField from, IField to){
        try {
            to.setPiece(piece);
            from.clean();
            changeToPlay();
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }



    //endregion

}
