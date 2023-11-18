package classes.Game.I18N;


import classes.GUI.FrameParts.ViewField;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;

import static classes.GUI.FrameParts.ViewBoard.getViewBoard;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.theresOnlyOneAi;
import static classes.Game.I18N.VARS.MUTABLE.whiteAiNeeded;

@Getter
@Setter
public class Location {

    //region Fields

    private int i;

    private int j;

    //endregion


    //region Constructor

    public Location(){}

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

    public boolean EQUALS(int i, int j){
        return this.EQUALS(new Location(i, j));
    }

    public Location add(Location l){
        return new Location(i + l.i, j + l.j);
    }

    public Location times(int n){
        return new Location(n * i, n * j);
    }

    public Location times(Location Location){
        return new Location(i * Location.getI(), j * Location.getJ());
    }

    public String toString(){
        return "[" + i + ", " + j + "] ";
    }

    public String toLoggerString() throws ChessGameException {

        String loc = "";
        loc += ((ViewField)getViewBoard().getField(this)).getRow();
        loc += ((ViewField)getViewBoard().getField(this)).getCol();
        return loc;
    }

    public static Location stringToLocation(String location){
        if (
                location.length() != 2 ||
                !Character.isDigit(location.charAt(0)) ||
                !Character.isDigit(location.charAt(1))
        )
            throw new RuntimeException("A megadott ("+ location +") string nem írható át location-re.");

        return new Location(Character.getNumericValue(location.charAt(0)),
                            Character.getNumericValue(location.charAt(1)));
    }

    //endregion
}
