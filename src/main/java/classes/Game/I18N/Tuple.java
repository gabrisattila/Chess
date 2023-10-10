package classes.Game.I18N;

import lombok.Getter;
import lombok.Setter;

public class Tuple<F, S, T> {

    //region Fields

    public F First;

    public S Second;

    public T Third;

    //endregion


    //region Constructor

    public Tuple(){}

    public Tuple(F first, S second, T third){
        this.First = first;
        this.Second = second;
        this.Third = third;
    }

    public Tuple(Tuple<F, S, T> toCopy){
        this.First = toCopy.First;
        this.Second = toCopy.Second;;
        this.Third = toCopy.Third;
    }

    //endregion


    //region Methods

    public boolean Equals(Tuple<F, S, T> toCompare){
        return First == toCompare.First && Second == toCompare.Second && Third == toCompare.Third;
    }

    //endregion
}
