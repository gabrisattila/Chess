package classes.Game.I18N;


import lombok.*;

@Getter
@Setter
public class ChessGameException extends Exception{

    //region Fields

    private final String msg;

    //endregion


    //region Constructor

    public ChessGameException(Object o, String msg) {
        this.msg = msg;
        System.out.println(o + msg);
    }

    public ChessGameException(String msg) {
        this.msg = msg;
        System.out.println(msg);
    }

    //endregion

}
