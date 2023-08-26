package classes.GUI.FrameParts;

import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;
import lombok.*;

import javax.swing.*;

import static classes.Ai.FenConverter.charToPieceType;
import static classes.Game.I18N.VARS.FINALS.*;

@Getter
@Setter
public class ViewPiece extends ImageIcon {

    //region Fields

    private final PieceAttributes attributes;

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
    }

    //endregion


    //region Methods

    public PieceType getType(){
        return attributes.getType();
    }

    public String getColor(){
        return attributes.getColor();
    }

    public boolean isWhite(){
        return WHITE_STRING.equals(attributes.getColor());
    }

    //endregion

}
