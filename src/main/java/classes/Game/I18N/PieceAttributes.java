package classes.Game.I18N;


import lombok.Getter;
import lombok.Setter;

import static classes.Game.I18N.METHODS.isNull;
import static classes.Game.I18N.VARS.FINALS.WHITE_STRING;

@Getter
@Setter
public class PieceAttributes {

    //region Fields

    private PieceType type;

    private String color;

    //endregion


    //region Constructor

    public PieceAttributes(){}

    public PieceAttributes(PieceType pieceType, String color){
        type = pieceType;
        this.color = color;
    }

    //endregion


    //region Methods

    public boolean equals(PieceAttributes a1, PieceAttributes a2){
        return a1.type == a2.type && a1.getColor().equals(a2.getColor());
    }

    public boolean equals(PieceAttributes a){
        return type == a.type && color.equals(a.getColor());
    }

    public boolean isEmpty(){
        return isNull(type) || isNull(color);
    }

    public boolean isWhite(){
        return WHITE_STRING.equals(color);
    }

    //endregion

}
