package classes.Ai.AI;

import classes.Ai.BitBoards.BitBoardMoves;
import classes.Game.I18N.ChessGameException;
import classes.Game.Model.Structure.Move;
import lombok.*;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

import static classes.Ai.AI.AiNode.*;
import static classes.Ai.BitBoards.BitBoardMoves.*;
import static classes.Ai.BitBoards.BitBoards.*;
import static classes.Ai.BitBoards.BBVars.*;
import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.Model.Logic.EDT.*;
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

        String starterFen = BoardToFen(getBoard());
        AiNode starterPos = getNewNodeAndSetUpProperEnvironmentForMinimaxStart();
        setUpBitBoard(starterFen);

        int startTime = (int) System.currentTimeMillis();

        double evaluatedSearch = miniMax(starterPos, whiteToPlay, 0, -Double.MAX_VALUE, Double.MAX_VALUE);
        bestMove = sortOutBestChild(starterPos, evaluatedSearch);

        makeMove(bestMove);
        FenToBoard(bitBoardsToFen(), getBoard());

        printSearchData(startTime, evaluatedSearch);
//        System.exit(130);
    }

    private AiNode getNewNodeAndSetUpProperEnvironmentForMinimaxStart(){
        bitBoardsCopy.clear();
        castleCopy.clear();
        bbEmPassantCopy.clear();
        whiteToPlayCopy.clear();
        return new AiNode();
    }

    private double miniMax(AiNode starterPos, boolean maxNeeded, double depth, double alpha, double beta){

        nodeNum++;

        if (depth == MINIMAX_DEPTH){

            //Here will come the evaluation
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

            starterPos.setFinalValue(/*evauation*/ 0);

            return 0;
        }

        double evaluatedMiniMax;

        int[] movesInThisTurn = generateMoves(maxNeeded);
//        System.out.println(moveListToString(movesInThisTurn));

        if (maxNeeded){
            double possibleMax = -Double.MAX_VALUE;

            for (int move : movesInThisTurn) {

                copyPosition();

                ply++;

                //Make move be the next in the generated move list
                if (makeMove(move)) { // Make Move, call miniMax recursively if it's legal move

//                    System.out.println("The board state in " + nodeNum + ". node.\nAfter " + moveToString(move) + " move completed.\n");
//                    printFullBoard();

                    AiNode next = new AiNode(move);
                    starterPos.getChildren().add(next);

                    evaluatedMiniMax = miniMax(next, false, depth + 1, alpha, beta);
                    ply--;
                    undoMove();
//                    System.out.println("The move: " + moveToString(move) + " set back.");
//                    printFullBoard();
                    possibleMax = Math.max(evaluatedMiniMax, possibleMax);

//                    alpha = Math.max(alpha, evaluatedMiniMax);
//                    if (beta <= alpha)
//                        break;

                } else {
                    ply--;
                    undoMove();
                }
            }
            starterPos.setFinalValue(possibleMax);
            return possibleMax /*+ ply*/;
        } else {
            double possibleMin = Double.MAX_VALUE;

            for (int move : movesInThisTurn) {

                copyPosition();

                ply++;

                //Make move be the next in the generated move list
                if (makeMove(move)){ // If move is legal

//                    System.out.println("The board state in " + nodeNum + ". node.\nAfter " + moveToString(move) + " move completed.\n");
//                    printFullBoard();

                    AiNode next = new AiNode(move);
                    starterPos.getChildren().add(next);

                    evaluatedMiniMax = miniMax(next, true, depth + 1, alpha, beta);
                    ply--;
                    undoMove();
//                    System.out.println("The move: " + moveToString(move) + " set back.");
//                    printFullBoard();
                    possibleMin = Math.min(evaluatedMiniMax, possibleMin);

//                    beta = Math.min(beta, evaluatedMiniMax);
//                    if (beta <= alpha)
//                        break;
                } else {
                    ply--;
                    undoMove();
                }
            }
            starterPos.setFinalValue(possibleMin);
            return possibleMin /* + ply */;
        }
    }

    private double negaMax(AiNode starterPos, int depth, double alpha, double beta){

        if (depth == MINIMAX_DEPTH){

            //Here will come the evaluation
            double evaluation = 0;
            starterPos.setFinalValue(evaluation);
            return 0;
        }

        nodeNum++;

        int move;
        generateMoves(whiteToPlay);
        int bestMoveYet = 0;

        double oldAlpha = alpha;

//        System.out.println("\nThis is the move list in the " + nodeNum + ". node.\n");
//        Arrays.stream(movesInATurn).filter(m -> getTo(m) != getFrom(m)).forEach(m -> System.out.println(moveToString(m)));
//        System.out.println("\n");

        for (int i = 0; i < moveCount; i++) {

            copyPosition();

            ply++;
            move = movesInATurn[i];

            if (!makeMove(move)){ // If the move is illegal simply take back
                ply--;
                //Skip to next move
                continue;
            }
            AiNode next = new AiNode(move);
            starterPos.getChildren().add(next);
            double evaluatedNegaMax = -negaMax(next, depth + 1, -beta, -alpha);

            if (evaluatedNegaMax == -0)
                evaluatedNegaMax = 0;

            ply--;
            undoMove();

            //Node Fail Hard Beta Cut-Off
            if (evaluatedNegaMax >= beta){
                starterPos.setFinalValue(beta);
                return beta;
            }

            //Found better move
            if (evaluatedNegaMax > alpha){
                alpha = evaluatedNegaMax;

                if (ply == 0)
                    //Set the best moves value
                    bestMoveYet = movesInATurn[i];
            }

        }

        if (oldAlpha != alpha){
            //Set best move
            bestMove = bestMoveYet;
        }

        //Node Fail Low
        starterPos.setFinalValue(alpha);
        return alpha;

    }

    private int sortOutBestChild(AiNode parent, double bestChildValue){

        ArrayList<AiNode> bestChildren = new ArrayList<>();

        //Collect the children which have the given best value
        for (AiNode child : parent.getChildren()) {
            if (bestChildValue == child.getFinalValue()){
                bestChildren.add(child);
            }
        }

        //If there's none throw error.
        if (bestChildren.size() == 0)
            throw new ChessGameException("\nThe search completed, and returned the possible best score (" + bestChildValue +
                                            ") but none of it's children have the mentioned value.\n");
        int best;
        if (bestChildren.size() == 1){
            //If only one child has the best value return its creator move.
            best = bestChildren.get(0).getTheMoveWhatsCreatedIt();
        } else {
            //If there's multiple, choose randomly from them.
            Random randomGenerator = new Random();
            best = bestChildren.get(randomGenerator.nextInt(0, bestChildren.size())).getTheMoveWhatsCreatedIt();
        }
        if (bbEmPassant != -1 && !isEmPassant(best))
            bbEmPassant = -1;
        if (castle != 0 && !isCastling(best))
            castle = 0;
        return best;
    }

    private void printSearchData(int startTime, double evaluatedSearch){
        int endTime = (int) System.currentTimeMillis();
        System.out.println("\n  MiniMax end. \n----------------");
        System.out.println("The search run for " + (double)((endTime - startTime) / 1000) + " seconds.");
        System.out.println("Searched " + (nodeNum - 1) + " nodes.");
        System.out.println("And found that the best move is: " + moveToString(bestMove) + " which score is: " + evaluatedSearch + ".");
    }

    //endregion

    //endregion

}
