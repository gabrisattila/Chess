package classes.Game.I18N;

import lombok.Setter;

public enum PieceType {

    G,

    H,

    F,

    B,

    V,

    K;

    public boolean equals(PieceType type){
        return this == type;
    }

    public String toString(boolean forWhite){
        switch (this){
            case G -> {
                return forWhite ? "G" : "g";
            }
            case H -> {
                return forWhite ? "H" : "h";
            }
            case F -> {
                return forWhite ? "F" : "f";
            }
            case B -> {
                return forWhite ? "B" : "b";
            }
            case V -> {
                return forWhite ? "V" : "v";
            }
            case K -> {
                return forWhite ? "K" : "k";
            }
        }
        return "";
    }

    public char toLowerCase(){
        switch (this){
            case G -> {
                return 'g';
            }
            case H -> {
                return 'h';
            }
            case F -> {
                return 'f';
            }
            case B -> {
                return 'b';
            }
            case V -> {
                return 'v';
            }
            default -> {
                return 'k';
            }
        }
    }

}
