package classes.Game.I18N;

import static classes.Game.I18N.VARS.FINALS.*;

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


    public double getValueOfPieceType(){
        switch (this){
            case P -> {
                return PAWN_BASE_VALUE;
            }
            case N, B -> {
                return KNIGHT_OR_BISHOP_BASE_VALUE;
            }
            case R -> {
                return ROOK_BASE_VALUE;
            }
            case Q -> {
                return QUEEN_BASE_VALUE;
            }
            case K -> {
                return KING_BASE_VALUE;
            }
        }
        return 0;
    }

}
