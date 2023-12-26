package classes.AI.Evaluation;

import classes.Game.I18N.PieceType;

import java.util.ArrayList;

import static classes.AI.BitBoards.BBVars.*;
import static classes.AI.BitBoards.BitBoards.*;
import static classes.AI.Evaluation.GameState.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

public class Evaluator {

    //region With FieldValues

    public static double evaluateEndGame(){
        double eval = evaluateWithFieldValues();
        return eval;
    }

    public static double evaluateWithFieldValues(){
        long currentBitBoard = 0;
        int from, i, j;
        PieceType type;
        boolean white;

        double eval = evaluate();

        for (int piece : pieceIndexes) {
            currentBitBoard = bitBoards[piece];
            while (currentBitBoard != 0){
                from = getFirstBitIndex(currentBitBoard);
                i = from / 8;
                j = from % 8;

                type = getPieceType(piece);
                white = piece <= wKingI;

                eval += getBaseFieldValue(type, white, i, j);

                currentBitBoard = removeBit(currentBitBoard, from);
            }
        }
        return eval;
    }

    public static double evaluate(){
        double eval = 0;
        eval += PAWN_BASE_VALUE * (Long.bitCount(bitBoards[wPawnI]) - Long.bitCount(bitBoards[bPawnI]));
        eval += KNIGHT_OR_BISHOP_BASE_VALUE * (
                    Long.bitCount(bitBoards[wKnightI]) - Long.bitCount(bitBoards[bKnightI]) +
                    Long.bitCount(bitBoards[wBishopI]) - Long.bitCount(bitBoards[bBishopI])
                );
        eval += ROOK_BASE_VALUE * (Long.bitCount(bitBoards[wRookI]) - Long.bitCount(bitBoards[bRookI]));
        eval += QUEEN_BASE_VALUE * (Long.bitCount(bitBoards[wQueenI]) - Long.bitCount(bitBoards[bQueenI]));
        eval += KING_BASE_VALUE * (Long.bitCount(bitBoards[wKingI]) - Long.bitCount(bitBoards[bKingI]));
        return eval;
    }

    private static double getBaseFieldValue(PieceType type, boolean forWhite, int i, int j){
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

}
