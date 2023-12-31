package classes.GUI.FrameParts;

import classes.Model.I18N.Location;
import classes.Model.I18N.PieceAttributes;
import classes.Model.I18N.PieceType;
import classes.Model.Structure.IPiece;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

import static classes.Model.I18N.PieceType.getPieceType;
import static classes.Model.I18N.VARS.FINALS.WHITE_STRING;

@Getter
@Setter
public class ViewPiece extends ImageIcon implements IPiece{

    //region Fields

    private Location location;

    private int i;

    private int j;

    private PieceAttributes attributes;

    private Set<Location> possibleRange;

    private Set<Location> watchedRange;

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
        attributes.setType(getPieceType(type));

        possibleRange = new HashSet<>();
        watchedRange = new HashSet<>();
    }

    public ViewPiece(String source, PieceAttributes attributes){
        super(source);

        this.attributes = attributes;

        possibleRange = new HashSet<>();
        watchedRange = new HashSet<>();
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
    public void updateRange() {}

    //endregion

}
