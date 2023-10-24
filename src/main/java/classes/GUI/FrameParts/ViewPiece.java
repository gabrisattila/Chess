package classes.GUI.FrameParts;

import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;
import classes.Game.Model.Structure.IBoard;
import classes.Game.Model.Structure.IPiece;
import lombok.*;

import javax.swing.*;

import java.util.HashSet;
import java.util.Set;

import static classes.Ai.FenConverter.charToPieceType;
import static classes.Game.I18N.VARS.FINALS.*;

@Getter
@Setter
public class ViewPiece extends ImageIcon implements IPiece {

    //region Fields

    private int i;

    private int j;

    private PieceAttributes attributes;

    private Set<Location> possibleRange;

    //endregion


    //region Constructor

    public ViewPiece(String source){
        super(source);

        attributes = new PieceAttributes();

        char color = source.split("Images")[1].charAt(1);
        char type = source.split("Images")[1].charAt(3);

        if (color == 'w')
            attributes.setColor("WHITE");
        else
            attributes.setColor("BLACK");
        attributes.setType(charToPieceType(type));

        possibleRange = new HashSet<>();
    }

    //endregion


    //region Methods

    @Override
    public PieceType getType(){
        return attributes.getType();
    }

    @Override
    public Location getLocation() {
        return new Location(i, j);
    }

    @Override
    public boolean isWhite(){
        return WHITE_STRING.equals(attributes.getColor());
    }

    @Override
    public boolean isEmpty(){
        return attributes == null;
    }

    @Override
    public void setEmpty(){
        attributes = null;
    }

    @Override
    public void STEP(Location from, Location to, IBoard board) {

    }

    @Override
    public void updateRange() {

    }

    public boolean inRange(ViewField clicked) {
        for (Location l : possibleRange) {
            if (clicked.getLoc().EQUALS(l))
                return true;
        }
        return false;
    }

    //endregion

}
