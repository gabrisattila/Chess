package classes.Game.I18N;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {

    //region Fields

    private int i;

    private int j;

    //endregion


    //region Constructor

    public Location(int i, int j){
        this.i = i;
        this.j = j;
    }

    public Location(Location toCopy){
        this.i = toCopy.i;
        this.j = toCopy.j;
    }

    //endregion


    //region Methods

    public boolean EQUALS(Location l){
        return i == l.i && j == l.j;
    }

    //endregion
}
