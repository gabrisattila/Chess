package classes.Model.I18N;

import lombok.*;

@Getter
@Setter
public class Pair<T, U> {

    //region Fields

    private T First;

    private U Second;

    //endregion


    //region Constructor

    public Pair(){}

    public Pair(T First, U Second){
        this.First = First;
        this.Second = Second;
    }

    //endregion

}
