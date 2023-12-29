package classes.Model.I18N;

import classes.Model.AI.BitBoards.BBVars;

import static classes.Model.I18N.VARS.FINALS.*;
import static classes.Model.I18N.VARS.MUTABLE.*;
import static classes.Model.AI.BitBoards.BBVars.*;

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

    public static PieceType getPieceType(int pieceIndex){
        switch (pieceIndex){
            case wPawnI, bPawnI -> {
                return P;
            }
            case wKnightI, bKnightI -> {
                return N;
            }
            case wBishopI, bBishopI -> {
                return B;
            }
            case wRookI, bRookI -> {
                return R;
            }
            case wQueenI, bQueenI -> {
                return Q;
            }
            default -> {
                return K;
            }
        }
    }
    
    public static PieceType getPieceType(char c){
        switch (c){
            case 'P', 'p' -> {
                return P;
            }
            case 'N', 'n' -> {
                return N;
            }
            case 'B', 'b' -> {
                return B;
            }
            case 'R', 'r' -> {
                return R;
            }
            case 'Q', 'q' -> {
                return Q;
            }
            case 'K', 'k' -> {
                return K;
            }
        }
        return null;
    }

    public static PieceType getPieceType(String s){
        switch (s){
            case "P", "p" -> {
                return P;
            }
            case "N", "n" -> {
                return N;
            }
            case "B", "b" -> {
                return B;
            }
            case "R", "r" -> {
                return R;
            }
            case "Q", "q" -> {
                return Q;
            }
            case "K", "k" -> {
                return K;
            }
        }
        return null;
    }

    public static String getProperPieceImage(char a){
        switch (a){
            case 'P' -> {
                return VARS.FINALS.pieceImagesForLog[0];
            }
            case 'N' -> {
                return VARS.FINALS.pieceImagesForLog[1];
            }
            case 'B' -> {
                return VARS.FINALS.pieceImagesForLog[2];
            }
            case 'R' -> {
                return VARS.FINALS.pieceImagesForLog[3];
            }
            case 'Q' -> {
                return VARS.FINALS.pieceImagesForLog[4];
            }
            case 'K' -> {
                return VARS.FINALS.pieceImagesForLog[5];
            }
            case 'p' -> {
                return VARS.FINALS.pieceImagesForLog[6];
            }
            case 'n' -> {
                return VARS.FINALS.pieceImagesForLog[7];
            }
            case 'b' -> {
                return VARS.FINALS.pieceImagesForLog[8];
            }
            case 'r' -> {
                return VARS.FINALS.pieceImagesForLog[9];
            }
            case 'q' -> {
                return VARS.FINALS.pieceImagesForLog[10];
            }
            case 'k' -> {
                return VARS.FINALS.pieceImagesForLog[11];
            }
        }
        return "";
    }


}
