package classes.Model.I18N;


import lombok.*;

import static classes.Model.I18N.PieceType.*;

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

    //endregion


    //region Constructor

    public PieceAttributes(){}

    public PieceAttributes(PieceType pieceType, String color){
        type = pieceType;
        this.color = color;
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
        return METHODS.isNull(type) || METHODS.isNull(color);
    }

    public boolean isWhite(){
        return VARS.FINALS.WHITE_STRING.equals(color);
    }

    public void setType(PieceType type){
        this.type = type;
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


    public static PieceAttributes charToPieceAttributes(char c){
        PieceType type;
        String color = Character.isUpperCase(c) ? "WHITE" : "BLACK";
        type = getPieceType(c);
        return new PieceAttributes(type, color);
    }

    public static char pieceAttributesToChar(PieceAttributes piece){
        char pieceChar;
        pieceChar = switch (piece.getType()){
            case P -> 'P';
            case N -> 'N';
            case B -> 'B';
            case R -> 'R';
            case Q -> 'Q';
            case K -> 'K';
        };
        if (!piece.isWhite())
            pieceChar = Character.toLowerCase(pieceChar);
        return pieceChar;
    }

    public static String createSourceStringFromGotAttributes(PieceAttributes attributes){
        StringBuilder sb = new StringBuilder();
        sb.append("src\\main\\resources\\Figura_Images\\");

        sb.append(attributes.isWhite() ? "w_" : "b_");
        String type;
        switch (attributes.getType()){
            case P -> type = "pawn";
            case N -> type = "night";
            case B -> type = "bishop";
            case R -> type = "rook";
            case Q -> type = "queen";
            default -> type = "king";
        }
        sb.append(type);
        sb.append(".png");

        return sb.toString();
    }


    //endregion

}
