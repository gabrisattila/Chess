package classes.Model.AI.Ai;

import classes.Model.AI.BitBoards.BitBoardMoves;
import classes.Model.I18N.ChessGameException;
import classes.Model.I18N.Pair;
import lombok.*;

import java.util.*;

import static classes.Model.AI.Ai.AiNode.*;
import static classes.Model.AI.BitBoards.BBVars.*;
import static classes.Model.AI.BitBoards.BitBoards.*;
import static classes.GUI.FrameParts.Logger.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Controller.EDT.*;
import static classes.Controller.FenConverter.*;
import static classes.Model.I18N.METHODS.*;
import static classes.Model.I18N.VARS.FINALS.*;
import static classes.Model.I18N.VARS.MUTABLE.*;
import static classes.Model.Structure.Board.*;
import static classes.Model.Structure.GameOverOrPositionEnd.*;
import static classes.Model.Structure.IBoard.*;


/**
 * thread descriptor object
 */
@Getter
@Setter
public class AI extends Thread {

    //region Constructor

    public AI(){}

    //endregion


    //region Methods

    @Override
    public void run(){
        try {
            aiMove();
            if (gameIsntFinished())
                receivedMoveFromAi(BoardToFen(getBoard()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void aiMove() throws InterruptedException {
        convertOneBoardToAnother(getViewBoard(), getBoard());
        Move();
        if (canBeLogger && gameIsntFinished())
            logAiStep(detectChessMove(BoardToFen(getViewBoard()), BoardToFen(getBoard())));
    }

    public void Move() {
        searchForBestMove();
    }
    
    public static boolean gameIsntFinished(){
        return !gameEndFlag.get();
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

    protected void searchForBestMove(){

        String starterFen = BoardToFen(getBoard());
        AiNode starterPos = getNewNodeAndSetUpProperEnvironmentForMinimaxStart();
        setUpBitBoard(starterFen);

        int startTime = (int) System.currentTimeMillis();

        double evaluatedSearch = miniMax(starterPos, whiteToPlay, 0, -Double.MAX_VALUE, Double.MAX_VALUE, 0);

        if (evaluatedSearch == WHITE_GOT_CHECKMATE || evaluatedSearch == BLACK_GOT_CHECKMATE ||
            evaluatedSearch == DRAW) {
            if (starterPos.getChildren().isEmpty()) {
                //Show final game end pop up
                gameEndDialog(evaluatedSearch);
                finalGameEnd();
                return;
            }
        }
        //We only do the best move if there's no Check Mate or Draw
        bestMove = sortOutBestChild(starterPos, evaluatedSearch);
        BitBoardMoves.makeMove(bestMove);
        String newPosFen = bitBoardsToFen();
        FenToBoard(newPosFen, getBoard());
        appendToHappenedList(newPosFen);

        printSearchData(startTime, evaluatedSearch);
    }

    public static AiNode getNewNodeAndSetUpProperEnvironmentForMinimaxStart(){

        ply = 0;
        bestMove = 0;
        nodeNum = 0;

        bitBoardsCopy.clear();
        castleCopy.clear();
        bbEmPassantCopy.clear();
        whiteToPlayCopy.clear();

        return new AiNode();
    }

    protected double miniMax(AiNode starterPos, boolean maxNeeded, double depth, double alpha, double beta, double inheritedValue){
        synchronized (pauseFlag){

            waitOnPause();

            nodeNum++;

            if (depth == MINIMAX_DEPTH || isDraw()) {

                if (isDraw()) {
                    starterPos.setFinalValue(DRAW);
                    return DRAW;
                }

                starterPos.setFinalValue(inheritedValue);
                return inheritedValue;
            }

            double evaluatedMiniMax;

            Pair<ArrayList<Double>, ArrayList<Integer>> valuedMoves = getPairList(BitBoardMoves.generateMoves(maxNeeded));

            int move;
            double value;

            ArrayList<Double> valuesInThisTurn = valuedMoves.getFirst();
            ArrayList<Integer> movesInThisTurn = valuedMoves.getSecond();

            int legalMoves = 0;

            if (maxNeeded) {
                double possibleMax = -Double.MAX_VALUE;

                for (int i = 0; i < movesInThisTurn.size(); i++) {

                    BitBoardMoves.copyPosition();

                    move = movesInThisTurn.get(i);
                    value = valuesInThisTurn.get(i);

                    if (BitBoardMoves.makeMove(move)) { //If move is legal
                        legalMoves++;
                        ply++;

                        AiNode next = new AiNode(move);
                        starterPos.getChildren().add(next);

                        evaluatedMiniMax = miniMax(next, false, (BitBoardMoves.isCheck(move) ? 0 : 1) + depth, alpha, beta, value);
                        ply--;
                        BitBoardMoves.undoMove();

                        possibleMax = Math.max(evaluatedMiniMax, possibleMax);

                        alpha = Math.max(alpha, evaluatedMiniMax);
                        if (beta <= alpha)
                            break;
                    } else {
                        ply--;
                    }
                }
                if (legalMoves == 0) {
                    if (BitBoardMoves.isSquareAttacked(false, getFirstBitIndex(bitBoards[wKingI])))
                        possibleMax = WHITE_GOT_CHECKMATE;
                    else
                        possibleMax = DRAW;
                }
                starterPos.setFinalValue(possibleMax);
                return possibleMax;
            } else {
                double possibleMin = Double.MAX_VALUE;

                for (int i = 0; i < movesInThisTurn.size(); i++) {

                    BitBoardMoves.copyPosition();

                    move = movesInThisTurn.get(i);
                    value = valuesInThisTurn.get(i);

                    if (BitBoardMoves.makeMove(move)) { // If move is legal
                        legalMoves++;
                        ply++;

                        AiNode next = new AiNode(move);
                        starterPos.getChildren().add(next);

                        evaluatedMiniMax = miniMax(next, true, (BitBoardMoves.isCheck(move) ? 0 : 1) + depth, alpha, beta, value);
                        ply--;
                        BitBoardMoves.undoMove();

                        possibleMin = Math.min(evaluatedMiniMax, possibleMin);

                        beta = Math.min(beta, evaluatedMiniMax);
                        if (beta <= alpha)
                            break;
                    } else {
                        ply--;
                    }
                }
                if (legalMoves == 0) {
                    if (BitBoardMoves.isSquareAttacked(true, getFirstBitIndex(bitBoards[bKingI])))
                        possibleMin = BLACK_GOT_CHECKMATE;
                    else
                        possibleMin = DRAW;
                }
                starterPos.setFinalValue(possibleMin);
                return possibleMin;
            }
        }
    }

    public static Pair<ArrayList<Double>, ArrayList<Integer>> getPairList(TreeMap<Double, ArrayList<Integer>> generated){
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Integer> moves = new ArrayList<>();
        for (double val : generated.keySet()) {
            for (int move : generated.get(val)) {
                values.add(val);
                moves.add(move);
            }
        }
        return new Pair<>(values, moves);
    }

    protected int sortOutBestChild(AiNode parent, double bestChildValue){

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
        if (bbEmPassant != -1 && !BitBoardMoves.isEmPassant(best))
            bbEmPassant = -1;
        return best;
    }

    protected void printSearchData(int startTime, double evaluatedSearch){
        int endTime = (int) System.currentTimeMillis();
        System.out.println("\n  MiniMax end. \n----------------");
        System.out.println("The search run for " + (double)((endTime - startTime) / 1000) + " seconds.");
        System.out.println("Searched " + (nodeNum - 1) + " nodes.");
        System.out.println("And found that the best move is: " + BitBoardMoves.moveToString(bestMove) + " which score is: " + evaluatedSearch + ".");
        System.out.println("Full BitBoard state after move: \n");
        printFullBitBoard();
        System.out.println();
    }

    //endregion

    //For Test:

    public static double miniMaxWithoutAlphaBeta(AiNode starterPos, boolean maxNeeded, double depth, double inheritedValue){

        nodeNum++;

        if (depth == MINIMAX_DEPTH || isDraw()){

            if (isDraw()){
                starterPos.setFinalValue(DRAW);
                return DRAW;
            }

            starterPos.setFinalValue(inheritedValue);
            return inheritedValue;
        }

        double evaluatedMiniMax;

        Pair<ArrayList<Double>, ArrayList<Integer>> valuedMoves = getPairList(BitBoardMoves.generateMoves(maxNeeded));

        int move;
        double value;

        ArrayList<Double> valuesInThisTurn = valuedMoves.getFirst();
        ArrayList<Integer> movesInThisTurn = valuedMoves.getSecond();

        int legalMoves = 0;

        if (maxNeeded){
            double possibleMax = -Double.MAX_VALUE;

            for (int i = 0; i < movesInThisTurn.size(); i++) {

                BitBoardMoves.copyPosition();

                move = movesInThisTurn.get(i);
                value = valuesInThisTurn.get(i);

                if (BitBoardMoves.makeMove(move)) { //If move is legal
                    legalMoves++;
                    ply++;

                    AiNode next = new AiNode(move);
                    starterPos.getChildren().add(next);

                    evaluatedMiniMax = miniMaxWithoutAlphaBeta(next, false, (BitBoardMoves.isCheck(move) ? 0 : 1) + depth, value);
                    ply--;
                    BitBoardMoves.undoMove();

                    possibleMax = Math.max(evaluatedMiniMax, possibleMax);

                }else {
                    ply--;
                }
            }
            if (legalMoves == 0){
                if (BitBoardMoves.isSquareAttacked(false, getFirstBitIndex(bitBoards[wKingI])))
                    possibleMax = WHITE_GOT_CHECKMATE;
                else
                    possibleMax = DRAW;
            }
            starterPos.setFinalValue(possibleMax);
            return possibleMax;
        } else {
            double possibleMin = Double.MAX_VALUE;

            for (int i = 0; i < movesInThisTurn.size(); i++) {

                BitBoardMoves.copyPosition();

                move = movesInThisTurn.get(i);
                value = valuesInThisTurn.get(i);

                if (BitBoardMoves.makeMove(move)){ // If move is legal
                    legalMoves++;
                    ply++;

                    AiNode next = new AiNode(move);
                    starterPos.getChildren().add(next);

                    evaluatedMiniMax = miniMaxWithoutAlphaBeta(next, true, (BitBoardMoves.isCheck(move) ? 0 : 1) + depth, value);
                    ply--;
                    BitBoardMoves.undoMove();

                    possibleMin = Math.min(evaluatedMiniMax, possibleMin);

                }else {
                    ply--;
                }
            }
            if (legalMoves == 0){
                if (BitBoardMoves.isSquareAttacked(true, getFirstBitIndex(bitBoards[bKingI])))
                    possibleMin = BLACK_GOT_CHECKMATE;
                else
                    possibleMin = DRAW;
            }
            starterPos.setFinalValue(possibleMin);
            return possibleMin;
        }
    }

    //endregion

}
