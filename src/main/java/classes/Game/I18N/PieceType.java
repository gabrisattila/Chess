package classes.Game.I18N;

public enum PieceType {

    G,

    H,

    F,

    B,

    V,

    K,

    normalKing,

    castleKing;

    public PieceType getKing(boolean normalOrCastle){
        if (this == K){
            return normalOrCastle ? normalKing : castleKing;
        }
        throw new RuntimeException("Nem megfelelő típuson lett használva a függvény.");
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
