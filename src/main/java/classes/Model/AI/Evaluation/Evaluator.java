package classes.Model.AI.Evaluation;

import classes.Model.AI.BitBoards.BBVars;
import classes.Model.AI.BitBoards.BitBoards;
import classes.Model.I18N.VARS;
import classes.Model.I18N.PieceType;

import static classes.Model.I18N.PieceType.*;
import static classes.Model.I18N.VARS.FINALS.*;
import static classes.Model.I18N.VARS.MUTABLE.*;

public class Evaluator {

    public static double evaluate(int possibilityNumAfterMove, boolean forWhite){
        double eval = evaluateWithFieldValues();
        eval += (forWhite ? +1 : -1) * 0.1 * possibilityNumAfterMove;
        return eval;
    }

    public static double evaluateWithFieldValues(){
        long currentBitBoard;
        int from, i, j;
        PieceType type;
        boolean white;

        double eval = evaluate();

        for (int piece : BBVars.pieceIndexes) {
            currentBitBoard = BBVars.bitBoards[piece];
            while (currentBitBoard != 0){
                from = BitBoards.getFirstBitIndex(currentBitBoard);
                i = from / 8;
                j = from % 8;

                type = getPieceType(piece);
                white = piece <= BBVars.wKingI;

                eval += getBaseFieldValue(type, white, i, j);

                currentBitBoard = BitBoards.removeBit(currentBitBoard, from);
            }
        }
        return eval;
    }

    public static double evaluate(){
        double eval = 0;
        eval += PAWN_BASE_VALUE * (Long.bitCount(BBVars.bitBoards[BBVars.wPawnI]) - Long.bitCount(BBVars.bitBoards[BBVars.bPawnI]));
        eval += KNIGHT_OR_BISHOP_BASE_VALUE * (
                    Long.bitCount(BBVars.bitBoards[BBVars.wKnightI]) - Long.bitCount(BBVars.bitBoards[BBVars.bKnightI]) +
                    Long.bitCount(BBVars.bitBoards[BBVars.wBishopI]) - Long.bitCount(BBVars.bitBoards[BBVars.bBishopI])
                );
        eval += ROOK_BASE_VALUE * (Long.bitCount(BBVars.bitBoards[BBVars.wRookI]) - Long.bitCount(BBVars.bitBoards[BBVars.bRookI]));
        eval += QUEEN_BASE_VALUE * (Long.bitCount(BBVars.bitBoards[BBVars.wQueenI]) - Long.bitCount(BBVars.bitBoards[BBVars.bQueenI]));
        eval += KING_BASE_VALUE * (Long.bitCount(BBVars.bitBoards[BBVars.wKingI]) - Long.bitCount(BBVars.bitBoards[BBVars.bKingI]));
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

}
