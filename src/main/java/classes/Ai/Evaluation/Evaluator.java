package classes.Ai.Evaluation;

import classes.Game.I18N.PieceType;

import java.util.ArrayList;

import static classes.Ai.BitBoards.BBVars.oppositeInsideEight;
import static classes.Ai.Evaluation.GameState.*;
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
        if (params.size() == 7) { // OPENING

        } else if (params.size() == 8) { // MID_GAME

        } else if (params.size() == 9) { // END_GAME

        }
        return Double.MIN_VALUE;
    }

    //endregion



}
