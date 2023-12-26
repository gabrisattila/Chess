package classes.AI.Evaluation;

import classes.Game.I18N.PieceType;

import java.util.ArrayList;

import static classes.AI.BitBoards.BBVars.*;
import static classes.AI.Evaluation.GameState.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

public class Evaluator {

    //region With FieldValues

    public static double getBaseFieldValue(int indexOfPiece, char Type){
        boolean forWhite = Character.isUpperCase(Type);
        PieceType type;
        int i = indexOfPiece / 8, j = indexOfPiece % 8;
        type = getPieceType(Type);
        return getBaseFieldValue(type, forWhite, i, j);
    }

    public static double getBaseFieldValue(int indexOfPiece, String Type){
        boolean forWhite = Character.isUpperCase(Type.charAt(0));
        PieceType type;
        int i = indexOfPiece / 8, j = oppositeInsideEight.get(indexOfPiece % 8);
        type = getPieceType(Type);
        return getBaseFieldValue(type, forWhite, i, j);
    }

    public static double getBaseFieldValue(PieceType type, boolean forWhite, int i, int j){
        if (type == P) {
            return forWhite ? PAWN_BASE_VALUE_MATRIX_WP[i][j] : -PAWN_BASE_VALUE_MATRIX_BP[i][j];
        }else if (type == B){
            return forWhite ? BISHOP_BASE_VALUE_MATRIX_WP[i][j] : -BISHOP_BASE_VALUE_MATRIX_BP[i][j];
        } else if (type == R) {
            return forWhite ? ROOK_BASE_VALUE_MATRIX_WP[i][j] : -ROOK_BASE_VALUE_MATRIX_BP[i][j];
        } else if (type == Q) {
            if (whiteDown){
                return forWhite ? QUEEN_BASE_VALUE_MATRIX_WD_WP[i][j] : -QUEEN_BASE_VALUE_MATRIX_WD_BP[i][j];
            } else {
                return forWhite ? QUEEN_BASE_VALUE_MATRIX_BD_WP[i][j] : -QUEEN_BASE_VALUE_MATRIX_BD_BP[i][j];
            }
        } else if (type == K) {
            if (whiteDown){
                return forWhite ? KING_BASE_VALUE_MATRIX_WD_WP[i][j] : -KING_BASE_VALUE_MATRIX_WD_BP[i][j];
            } else {
                return forWhite ? KING_BASE_VALUE_MATRIX_BD_WP[i][j] : -KING_BASE_VALUE_MATRIX_BD_BP[i][j];
            }
        } else {
            double val = FIELD_BASE_VALUES_BY_PIECE_TYPE.get(N)[i][j];
            return forWhite ? val : -val;
        }
    }

    //endregion


    //region Eval BitBoards

    public static int getPawnNum(long pawnBoard){
        return Long.bitCount(pawnBoard);
    }

    public static int getKnightNum(long knightBoard){
        return Long.bitCount(knightBoard);
    }

    public static int getBishopNum(long bishopBoard){
        return Long.bitCount(bishopBoard);
    }

    public static int getRookNum(long rookBoard){
        return Long.bitCount(rookBoard);
    }

    public static int getQueenNum(long queenBoard){
        return Long.bitCount(queenBoard);
    }


    //endregion


    //region Base Of Eval

    public static double evaluate(BoardState boardState){
        switch (currentGameState){
            case OPENING -> {
                return evaluate(boardState.getParams(OPENING));
            }
            case MIDDLE_GAME -> {
                return evaluate(boardState.getParams(MIDDLE_GAME));
            }
            case END_GAME -> {
                return evaluate(boardState.getParams(END_GAME));
            }
        }
        return 0;
    }

    private static double evaluate(ArrayList<Object> params){
        double finalValue = 0;
        if (params.size() == 7) { // OPENING
            finalValue =
                    PAWN_BASE_VALUE * (int) params.get(1) +
                    KNIGHT_OR_BISHOP_BASE_VALUE * ((int) params.get(2) + (int) params.get(3)) +
                    ROOK_BASE_VALUE * (int) params.get(4) +
                    QUEEN_BASE_VALUE * (int) params.get(5) +
                    KING_BASE_VALUE +
                    0.5 * (int) params.get(6);
        } else if (params.size() == 9) { // MID_GAME
            finalValue =
                    PAWN_BASE_VALUE * (int) params.get(1) +
                    KNIGHT_OR_BISHOP_BASE_VALUE * ((int) params.get(2) + (int) params.get(3)) +
                    ROOK_BASE_VALUE * (int) params.get(4) +
                    QUEEN_BASE_VALUE * (int) params.get(5) +
                    KING_BASE_VALUE -
                    0.5 * ((int) params.get(6) + (int) params.get(7)) +
                    0.1 * (int) params.get(8);
        } else if (params.size() == 10) { // END_GAME
            finalValue =
                    PAWN_BASE_VALUE * (int) params.get(1) +
                    KNIGHT_OR_BISHOP_BASE_VALUE * ((int) params.get(2) + (int) params.get(3)) +
                    ROOK_BASE_VALUE * (int) params.get(4) +
                    QUEEN_BASE_VALUE * (int) params.get(5) +
                    KING_BASE_VALUE -
                    0.5 * ((int) params.get(6) + (int) params.get(7)) +
                    0.1 * (int) params.get(8) +
                    (int) params.get(9);
        }
        return (boolean) params.get(0) ? finalValue : -finalValue;
    }

    //endregion



}
