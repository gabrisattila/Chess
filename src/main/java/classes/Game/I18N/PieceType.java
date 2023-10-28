package classes.Game.I18N;

public enum PieceType {

    G,

    H,

    F,

    B,

    V,

    K;

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
