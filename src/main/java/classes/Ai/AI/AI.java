package classes.Ai.AI;

import classes.Game.I18N.ChessGameException;
import lombok.*;

import java.util.ArrayList;
import java.util.Random;

import static classes.Ai.BitBoards.BitBoardMoves.*;
import static classes.Ai.BitBoards.BitBoards.*;
import static classes.Ai.BitBoards.BBVars.*;
import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Game.Model.Structure.Board.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.GUI.FrameParts.Logger.*;
import static classes.Game.Model.Structure.GameOverOrPositionEnd.*;
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
        if (evaluatedSearch == WHITE_GOT_CHECKMATE + ply || evaluatedSearch == BLACK_GOT_CHECKMATE + ply) {
            finalGameEnd();
            return;
        }

        printSearchData(startTime, evaluatedSearch);
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

        if (depth == MINIMAX_DEPTH || isDraw()){

            if (isDraw()){
                starterPos.setFinalValue(DRAW);
                return DRAW;
            }

            //Here will come the evaluation
            double evaluation;

            evaluation =
                    PAWN_BASE_VALUE * Long.bitCount(bitBoards[wPawnI]) +
                        KNIGHT_OR_BISHOP_BASE_VALUE * (Long.bitCount(bitBoards[wKnightI] + Long.bitCount(bitBoards[wBishopI]))) +
                        ROOK_BASE_VALUE * Long.bitCount(bitBoards[wRookI]) +
                        QUEEN_BASE_VALUE * Long.bitCount(bitBoards[wQueenI])
                        +
                    -PAWN_BASE_VALUE * Long.bitCount(bitBoards[bPawnI]) -
                        KNIGHT_OR_BISHOP_BASE_VALUE * (Long.bitCount(bitBoards[bKnightI] + Long.bitCount(bitBoards[bBishopI]))) -
                        ROOK_BASE_VALUE * Long.bitCount(bitBoards[bRookI]) -
                        QUEEN_BASE_VALUE * Long.bitCount(bitBoards[bQueenI]);


            starterPos.setFinalValue(evaluation);
            return evaluation;
        }

        double evaluatedMiniMax;

        int[] movesInThisTurn = generateMoves(maxNeeded);

        int legalMoves = 0;

        if (maxNeeded){
            double possibleMax = -Double.MAX_VALUE;

            for (int move : movesInThisTurn) {

                copyPosition();

                if (makeMove(move)) { //If move is legal
                    legalMoves++;
                    ply++;

                    AiNode next = new AiNode(move);
                    starterPos.getChildren().add(next);

                    evaluatedMiniMax = miniMax(next, false, depth + 1, alpha, beta);
                    ply--;
                    undoMove();

                    possibleMax = Math.max(evaluatedMiniMax, possibleMax);

                    alpha = Math.max(alpha, evaluatedMiniMax);
                    if (beta <= alpha)
                        break;
                }else {
                    ply--;
                }
            }
            if (legalMoves == 0){
                if (isSquareAttacked(false, 63 - Long.numberOfLeadingZeros(bitBoards[wKingI])))
                    possibleMax = WHITE_GOT_CHECKMATE + ply;
                else
                    possibleMax = DRAW + ply;
            }
            starterPos.setFinalValue(possibleMax);
            return possibleMax;
        } else {
            double possibleMin = Double.MAX_VALUE;

            for (int move : movesInThisTurn) {

                copyPosition();

                if (makeMove(move)){ // If move is legal
                    legalMoves++;
                    ply++;

                    AiNode next = new AiNode(move);
                    starterPos.getChildren().add(next);

                    evaluatedMiniMax = miniMax(next, true, depth + 1, alpha, beta);
                    ply--;
                    undoMove();

                    possibleMin = Math.min(evaluatedMiniMax, possibleMin);

                    beta = Math.min(beta, evaluatedMiniMax);
                    if (beta <= alpha)
                        break;
                }else {
                    ply--;
                }
            }
            if (legalMoves == 0){
                if (isSquareAttacked(true,63 - Long.numberOfLeadingZeros(bitBoards[bKingI])))
                    possibleMin = BLACK_GOT_CHECKMATE + ply;
                else
                    possibleMin = DRAW + ply;
            }
            starterPos.setFinalValue(possibleMin);
            return possibleMin;
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
            if (notNull(child) && bestChildValue == child.getFinalValue()){
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
