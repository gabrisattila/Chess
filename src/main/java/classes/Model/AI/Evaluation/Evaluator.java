package classes.Model.AI.Evaluation;

import classes.Model.AI.BitBoards.BBVars;
import classes.Model.AI.BitBoards.BitBoards;
import classes.Model.I18N.VARS;
import classes.Model.I18N.PieceType;

import static classes.Model.I18N.PieceType.*;

public class Evaluator {

    public static double evaluate(int possibilityNumAfterMove, double enemyKingPossibilityNumAfterMove, boolean forWhite){
        double eval = evaluateWithFieldValues();
        eval += (forWhite ? +1 : -1) * 0.1 * possibilityNumAfterMove;
        if (VARS.MUTABLE.currentGameState == GameState.SECOND_STATE)
            eval -= (forWhite ? +1 : -1) * enemyKingPossibilityNumAfterMove;
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
        eval += VARS.FINALS.PAWN_BASE_VALUE * (Long.bitCount(BBVars.bitBoards[BBVars.wPawnI]) - Long.bitCount(BBVars.bitBoards[BBVars.bPawnI]));
        eval += VARS.FINALS.KNIGHT_OR_BISHOP_BASE_VALUE * (
                    Long.bitCount(BBVars.bitBoards[BBVars.wKnightI]) - Long.bitCount(BBVars.bitBoards[BBVars.bKnightI]) +
                    Long.bitCount(BBVars.bitBoards[BBVars.wBishopI]) - Long.bitCount(BBVars.bitBoards[BBVars.bBishopI])
                );
        eval += VARS.FINALS.ROOK_BASE_VALUE * (Long.bitCount(BBVars.bitBoards[BBVars.wRookI]) - Long.bitCount(BBVars.bitBoards[BBVars.bRookI]));
        eval += VARS.FINALS.QUEEN_BASE_VALUE * (Long.bitCount(BBVars.bitBoards[BBVars.wQueenI]) - Long.bitCount(BBVars.bitBoards[BBVars.bQueenI]));
        eval += VARS.FINALS.KING_BASE_VALUE * (Long.bitCount(BBVars.bitBoards[BBVars.wKingI]) - Long.bitCount(BBVars.bitBoards[BBVars.bKingI]));
        return eval;
    }

    private static double getBaseFieldValue(PieceType type, boolean forWhite, int i, int j){
        if (type == P) {
            return forWhite ? VARS.FINALS.PAWN_BASE_VALUE_MATRIX_WP[i][j] : -VARS.FINALS.PAWN_BASE_VALUE_MATRIX_BP[i][j];
        }else if (type == B){
            return forWhite ? VARS.FINALS.BISHOP_BASE_VALUE_MATRIX_WP[i][j] : -VARS.FINALS.BISHOP_BASE_VALUE_MATRIX_BP[i][j];
        } else if (type == R) {
            return forWhite ? VARS.FINALS.ROOK_BASE_VALUE_MATRIX_WP[i][j] : -VARS.FINALS.ROOK_BASE_VALUE_MATRIX_BP[i][j];
        } else if (type == Q) {
            if (VARS.MUTABLE.whiteDown){
                return forWhite ? VARS.FINALS.QUEEN_BASE_VALUE_MATRIX_WD_WP[i][j] : -VARS.FINALS.QUEEN_BASE_VALUE_MATRIX_WD_BP[i][j];
            } else {
                return forWhite ? VARS.FINALS.QUEEN_BASE_VALUE_MATRIX_BD_WP[i][j] : -VARS.FINALS.QUEEN_BASE_VALUE_MATRIX_BD_BP[i][j];
            }
        } else if (type == K) {
            if (VARS.MUTABLE.whiteDown){
                return forWhite ? VARS.FINALS.KING_BASE_VALUE_MATRIX_WD_WP[i][j] : -VARS.FINALS.KING_BASE_VALUE_MATRIX_WD_BP[i][j];
            } else {
                return forWhite ? VARS.FINALS.KING_BASE_VALUE_MATRIX_BD_WP[i][j] : -VARS.FINALS.KING_BASE_VALUE_MATRIX_BD_BP[i][j];
            }
        } else {
            double val = VARS.FINALS.FIELD_BASE_VALUES_BY_PIECE_TYPE.get(N)[i][j];
            return forWhite ? val : -val;
        }
    }

}
