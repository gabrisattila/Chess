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

    /*
     * enemy: 0 and 7 minden figuránál. Nyilván attól függően merre megy
     * own:
     */
    private Pair<Integer, Integer> enemyAndOwnStartRow;

    private int[] possibleEmPassant;

    private double VALUE;

    //endregion


    //region Constructor

    public PieceAttributes(){}

    public PieceAttributes(PieceType pieceType, String color){
        type = pieceType;
        this.color = color;
        valueSetting();
        enemyAndOwnStartRow = new Pair<>();
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

    public void valueSetting(){
        switch (getType()){
            case G -> VALUE = isWhite() ? 1 : -1;
            case H, F -> VALUE = isWhite() ? 3 : -3;
            case B -> VALUE = isWhite() ? 5 : -5;
            case V -> VALUE = isWhite() ? 9 : -9;
            case K -> VALUE = isWhite() ? 100 : -100;
        }
    }

    public Location getEmPassantLoc(){
        return new Location(possibleEmPassant[0], possibleEmPassant[1]);
    }

    public void setPossibleEmPassant(int sor, int oszlop){
        possibleEmPassant = new int[2];
        possibleEmPassant[0] = sor;
        possibleEmPassant[1] = oszlop;
    }

    public void setEnemyStartRow(int x){
        enemyAndOwnStartRow.setFirst(x);
    }

    public void setOwnStartRow(int x){
        enemyAndOwnStartRow.setSecond(x);
    }

    //endregion

}
