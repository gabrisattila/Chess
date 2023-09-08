package classes.GUI.FrameParts;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.PieceAttributes;
import classes.Game.I18N.PieceType;
import classes.Game.Model.Structure.GrandBoard;
import classes.Game.Model.Structure.IPiece;
import lombok.*;

import javax.swing.*;

import java.util.ArrayList;

import static classes.Ai.FenConverter.charToPieceType;
import static classes.Game.I18N.VARS.FINALS.*;

@Getter
@Setter
public class ViewPiece extends ImageIcon implements IPiece {

    //region Fields

    private final PieceAttributes attributes;

    private ArrayList<Location> possibleRange;

    private ArrayList<ViewField> options;

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

        possibleRange = new ArrayList<>();
        options = new ArrayList<>();
    }

    //endregion


    //region Methods

    public PieceType getType(){
        return attributes.getType();
    }

    public boolean isWhite(){
        return WHITE_STRING.equals(attributes.getColor());
    }

    @Override
    public void STEP(Location from, Location to, GrandBoard board) {

    }

    @Override
    public void updateRange() throws ChessGameException {

    }

    //endregion

}
