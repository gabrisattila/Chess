package classes.Game.I18N;

public enum PieceType {

    P,

    N,

    B,

    R,

    Q,

    K;

    public boolean equals(PieceType type){
        return this == type;
    }

    public String toString(boolean forWhite){
        switch (this){
            case P -> {
                return forWhite ? "P" : "p";
            }
            case N -> {
                return forWhite ? "N" : "n";
            }
            case B -> {
                return forWhite ? "B" : "b";
            }
            case R -> {
                return forWhite ? "R" : "r";
            }
            case Q -> {
                return forWhite ? "Q" : "q";
            }
            case K -> {
                return forWhite ? "K" : "k";
            }
        }
        return "";
    }

    public char toLowerCase(){
        switch (this){
            case P -> {
                return 'p';
            }
            case N -> {
                return 'n';
            }
            case B -> {
                return 'b';
            }
            case R -> {
                return 'r';
            }
            case Q -> {
                return 'q';
            }
            default -> {
                return 'k';
            }
        }
    }

}
