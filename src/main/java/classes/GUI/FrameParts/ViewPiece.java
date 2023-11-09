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
public class ViewPiece extends ImageIcon implements IPiece, Comparable<ViewPiece>{

    //region Fields

    private Location location;

    private int i;

    private int j;

    private PieceAttributes attributes;

    private Set<Location> possibleRange;

    //endregion


    //region Constructor

    public ViewPiece(){}

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

    public ViewPiece(String source, PieceAttributes attributes){
        super(source);

        this.attributes = attributes;

        possibleRange = new HashSet<>();
    }

    //endregion


    //region Methods

    @Override
    public PieceType getType(){
        return attributes.getType();
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
    public void updateRange() {

    }

    public boolean inRange(ViewField clicked) {
        for (Location l : possibleRange) {
            if (clicked.getLoc().EQUALS(l))
                return true;
        }
        return false;
    }

    @Override
    public int compareTo(ViewPiece p1) {
        int result = Character.compare(getType().toLowerCase(), p1.getType().toLowerCase());
        //if (result == 0)
        //    result = Integer.compare(getI(), p1.getI());
        //if (result == 0)
        //    result = Integer.compare(getJ(), p1.getJ());
        return Integer.compare(hashCode(), p1.hashCode());
    }

    //endregion

}
