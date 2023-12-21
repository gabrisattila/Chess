package classes.Ai.AI;

import lombok.*;

import static classes.Ai.BitBoards.BitBoardMoves.*;
import static classes.Ai.BitBoards.BitBoards.*;
import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Ai.BitBoards.BBVars.*;
import static classes.Game.Model.Structure.Board.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.GUI.FrameParts.Logger.*;
import static classes.Game.Model.Structure.IBoard.convertOneBoardToAnother;


/**
 * thread descriptor object
 */
@Getter
@Setter
public class AI extends Thread {

    //region Fields

    //endregion


    //region Constructor

    public AI(){}

    //endregion


    //region Methods

    @Override
    public void run(){
        try {
            aiMove();
            receivedMoveFromAi(BoardToFen(getBoard()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void aiMove() throws InterruptedException {
        convertOneBoardToAnother(getViewBoard(), getBoard());
        Move();
        if (canBeLogger)
            logAiStep(detectChessMove(BoardToFen(getViewBoard()), BoardToFen(getBoard())));
    }

    public void Move() {
        searchForBestMove();
    }
    
    private boolean gameFinished(){
        return gameEndFlag.get();
    }

    public static void waitOnPause(){
        while(pauseFlag.get()) {
            try {
                pauseFlag.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //region Search

    private void searchForBestMove(){

        ply = 0;
        bestMove = 0;
        nodeNum = 0;

        setUpBitBoard(BoardToFen(getBoard()));

        int startTime = (int) System.currentTimeMillis();

        double evaluatedSearch = miniMax(whiteToPlay, 0, -Double.MAX_VALUE, Double.MAX_VALUE);
//        double evaluatedSearch = negaMax(0, -Double.MAX_VALUE, Double.MAX_VALUE);

        printSearchData(startTime, evaluatedSearch);
        System.exit(130);
    }

    private double miniMax(boolean maxNeeded, double depth, double alpha, double beta){

        if (depth == MINIMAX_DEPTH){

            //Here will comes the evaluation
            double evaluation;
            //return maxNeeded ?
            //        (
            //                PAWN_BASE_VALUE * Long.bitCount(bitBoards[wPawnI]) +
            //                KNIGHT_OR_BISHOP_BASE_VALUE * (Long.bitCount(bitBoards[wKnightI] + Long.numberOfLeadingZeros(bitBoards[wBishopI]))) +
            //                ROOK_BASE_VALUE * Long.bitCount(bitBoards[wRookI]) +
            //                QUEEN_BASE_VALUE * Long.bitCount(bitBoards[wQueenI])
            //        ) :
            //        (
            //                -PAWN_BASE_VALUE * Long.bitCount(bitBoards[wPawnI]) -
            //                KNIGHT_OR_BISHOP_BASE_VALUE * (Long.bitCount(bitBoards[wKnightI] + Long.numberOfLeadingZeros(bitBoards[wBishopI]))) -
            //                ROOK_BASE_VALUE * Long.bitCount(bitBoards[wRookI]) -
            //                QUEEN_BASE_VALUE * Long.bitCount(bitBoards[wQueenI])
            //        );
            return 0;
        }

        nodeNum++;

        double evaluatedMiniMax;
        generateMoves();
        int move;

        if (maxNeeded){
            double possibleMax = -Double.MAX_VALUE;

            for (int i = 0; i < moveCount; i++) {

                copyPosition();

                ply++;

                //Make move be the next in the generated move list
                move = movesInATurn[i];
                if (makeMove(move)){ // Make Move, call miniMax recursively if it's legal move
                    evaluatedMiniMax = miniMax(false, depth + 1, alpha, beta);
                    undoMove();
                    possibleMax = Math.max(possibleMax, evaluatedMiniMax);
                    alpha = Math.max(alpha, evaluatedMiniMax);
                    if (beta <= alpha)
                        break;
                } else { // else take back move
                    ply--;
                    undoMove();
                }
            }
            return possibleMax + ply;
        } else {
            double possibleMin = Double.MAX_VALUE;

            for (int i = 0; i < moveCount; i++) {

                copyPosition();

                ply++;

                //Make move be the next in the generated move list
                move = movesInATurn[i];
                if (makeMove(move)){ // If move is legal
                    evaluatedMiniMax = miniMax(true, depth + 1, alpha, beta);
                    undoMove();
                    ply--;
                    possibleMin = Math.min(possibleMin, evaluatedMiniMax);
                    beta = Math.min(beta, evaluatedMiniMax);
                    if (beta <= alpha)
                        break;
                } else { // else
                    ply--;
                    undoMove();
                }
            }
            return possibleMin + ply;
        }
    }

    private double negaMax(int depth, double alpha, double beta){

        if (depth == MINIMAX_DEPTH){

            //Here will come the evaluation
            double evaluation;



            return 0;
        }

        nodeNum++;

        int move;
        generateMoves();
        int bestMoveYet = 0;

        double oldAlpha = alpha;

        for (int i = 0; i < moveCount; i++) {

            copyPosition();

            ply++;
            move = movesInATurn[i];

            if (!makeMove(move)){ // If the move is illegal simply take back
                ply--;
                undoMove();
            }

            double evaluatedNegaMax = -negaMax(depth + 1, -beta, -alpha);

            ply--;
            undoMove();

            //Node Fail Hard Beta Cut-Off
            if (evaluatedNegaMax >= beta){
                return beta;
            }

            //Found better move
            if (evaluatedNegaMax > alpha){
                alpha = evaluatedNegaMax;

                if (ply == 0)
                    //Set the best moves value
                    bestMoveYet = movesInATurn[moveCount];
            }

        }

        if (oldAlpha != alpha){
            //Set best move
            bestMove = bestMoveYet;
        }

        //Node Fail Low
        return alpha;

    }

    //endregion

    private void printSearchData(int startTime, double evaluatedSearch){
        int endTime = (int) System.currentTimeMillis();
        System.out.println("The search run for " + (double)((endTime - startTime) / 1000) + " seconds.");
        System.out.println("Searched " + nodeNum + " nodes.");
        System.out.println("And found that the best move is: " + moveToString(bestMove) + " which score is: " + evaluatedSearch + ".");
    }

    //endregion

}
