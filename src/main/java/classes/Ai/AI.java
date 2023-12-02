package classes.Ai;

import classes.GUI.Frame.Window;
import classes.Game.I18N.Location;
import classes.Game.I18N.Pair;
import classes.Game.Model.Structure.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Random;

import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.GUI.Frame.Window.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Game.Model.Structure.Board.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.GameOverOrPositionEnd.*;
import static classes.Game.Model.Structure.Move.*;

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
    }

    public void Move()  {
//        moveWithSimpleAi();
        moveWithMiniMaxAi();
    }

    public void moveWithSimpleAi() {
        synchronized (pauseFlag){
            while (pauseFlag.get()) {
                try {
                    pauseFlag.wait();
                } catch (InterruptedException ignored) {}
            }

            double gameOver = GameOverDecision(getBoard(), false, Double.MIN_VALUE);
            if (gameFinished(gameOver))
                return;

            Random random = new Random();
            Piece stepper;
            ArrayList<Piece> possibleSteppers = new ArrayList<>();
            for (IPiece p : getBoard().myPieces()) {
                if (!p.getPossibleRange().isEmpty()){
                    possibleSteppers.add((Piece) p);
                }
            }
            stepper = possibleSteppers.get(random.nextInt(0, possibleSteppers.size()));


            ArrayList<Location> ableToStepThereIn = new ArrayList<>(stepper.getPossibleRange());
            int indexOfChosen = random.nextInt(0, ableToStepThereIn.size());
            Location toStepOn = ableToStepThereIn.get(indexOfChosen);
            Pair<Integer, Location> kingCheck = kingAliveCheckRandomMove(indexOfChosen, toStepOn, ableToStepThereIn, random);
            if (kingCheck.getFirst() != indexOfChosen){
                toStepOn = kingCheck.getSecond();
            }

            Move move = new Move(stepper, toStepOn, getBoard());
            move.setMustLogged(true);
            Step(move);
        }
    }
    
    private boolean gameFinished(double gameOver){
        return GAME_OVER_CASES.contains(gameOver);
    }

    private Pair<Integer, Location> kingAliveCheckRandomMove(int indexOfChosen, Location toStepOn, ArrayList<Location> ableToStepThere, Random random)  {
        while (notNull(getBoard().getPiece(toStepOn)) && getBoard().getPiece(toStepOn).getType() == K){
            indexOfChosen = random.nextInt(0, ableToStepThere.size());
            toStepOn = ableToStepThere.get(indexOfChosen);
        }
        return new Pair<>(indexOfChosen, toStepOn);
    }


    //region Mini Max

    private void moveWithMiniMaxAi() {

        double gameOver = GameOverDecision(getBoard(), true, Double.MIN_VALUE);
        if (!gameFinished(gameOver)) {
            AiTree tree = new AiTree(BoardToFen(getBoard()));

            double bestChildValue = miniMax(tree, 0, whiteToPlay, -Double.MAX_VALUE, Double.MAX_VALUE);

            AiTree bestChild = sortOutBestChild(tree, bestChildValue);
//            getLogger().log(bestChild.getFen().split(" ")[6]);
            FenToBoard(bestChild.getFen(), getBoard());
        }
    }

    private double miniMax(AiTree starterPos, int depth, boolean maxNeeded, double alpha, double beta) {

        synchronized (pauseFlag){

            while(pauseFlag.get()) {
                try {
                    pauseFlag.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            double gameOver = GameOverDecision(starterPos, false, Double.MIN_VALUE);

            if (depth == MINIMAX_DEPTH || gameFinished(gameOver)) {
                starterPos.setFinalValue(gameOver);
                return gameOver;
            }

            ArrayList<String> possibilities = starterPos.collectPossibilities(maxNeeded);

            AiTree nextChild;
            if (maxNeeded) {
                double possibleMax = -Double.MAX_VALUE;
                for (String child : possibilities) {
                    nextChild = new AiTree(child);
                    starterPos.getChildren().add(nextChild);

                    double evaluatedMiniMax = miniMax(nextChild, depth + 1, false, alpha, beta);
                    possibleMax = Math.max(possibleMax, evaluatedMiniMax); //GAME_OVER_CASES.contains(evaluatedMiniMax) ? evaluatedMiniMax :
                    alpha = Math.max(alpha, evaluatedMiniMax);
                    if (beta <= alpha)
                        break;
                }
                starterPos.setFinalValue(possibleMax);
                return possibleMax;
            } else {
                double possibleMin = Double.MAX_VALUE;
                for (String child : possibilities) {
                    nextChild = new AiTree(child);
                    starterPos.getChildren().add(nextChild);

                    double evaluatedMiniMax = miniMax(nextChild, depth + 1, true, alpha, beta);
                    possibleMin = Math.min(possibleMin, evaluatedMiniMax); //GAME_OVER_CASES.contains(evaluatedMiniMax) ? evaluatedMiniMax :
                    beta = Math.min(beta, evaluatedMiniMax);
                    if (beta <= alpha)
                        break;
                }
                starterPos.setFinalValue(possibleMin);
                return possibleMin;
            }
        }
    }

    private AiTree sortOutBestChild(AiTree tree, double bestChildValue){
        ArrayList<AiTree> bestChildren = new ArrayList<>();
        for (AiTree child : tree.getChildren()) {
            if (child.getFinalValue() == bestChildValue) {
                bestChildren.add(child);
            }
        }
        AiTree bestChild;
        if (bestChildren.size() == 1) {
            bestChild = bestChildren.get(0);
        } else {
            Random random = new Random();
            int randomChosenBestIndex = random.nextInt(0, bestChildren.size());
            bestChild = bestChildren.get(randomChosenBestIndex);
        }
        emPassantChance = bestChild.getFen().split(" ")[3];
        castleCaseFenToBoard(bestChild.getFen().split(" ")[2]);
        return bestChild;
    }

    public static boolean itWorthToGiveUp(){

        double enemyPiecesValueSum = getBoard().getPieces(!whiteToPlay).stream().mapToDouble(p -> ((Piece) p).getVALUE()).sum();
        double myPiecesValueSum = getBoard().getPieces(whiteToPlay).stream().mapToDouble(p -> ((Piece) p).getVALUE()).sum();

        return Math.abs(enemyPiecesValueSum + myPiecesValueSum) > ROOK_BASE_VALUE + KNIGHT_OR_BISHOP_BASE_VALUE &&
                                        getBoard().getPieces(whiteToPlay).stream().allMatch(p -> p.getType() == K || p.getType() == G);
    }

    public static boolean itWorthToOfferOrRecommendDraw(){
        if (getBoard().getPieces().size() == 3 && getBoard().hasTwoKings() &&
                getBoard().getPieces().stream().allMatch(p -> p.getType() == K || p.getType() == G)){
            IPiece onlyPawn = null;
            IPiece enemyKing = null;
            for (IPiece p : getBoard().getPieces()) {
                if (p.getType() == G){
                    onlyPawn = p;
                }
            }
            assert onlyPawn != null;
            enemyKing = getBoard().getKing(!onlyPawn.isWhite());
            if (onlyPawn.getJ() != 0 || onlyPawn.getJ() != MAX_WIDTH - 1){
                return false;
            }
            int pawnDistance = Math.abs(onlyPawn.getJ() - onlyPawn.getEnemyStartRow());
            int kingDistance = Math.max(Math.abs(enemyKing.getJ() - onlyPawn.getJ()), Math.abs(enemyKing.getI() - onlyPawn.getEnemyStartRow()));
            return !(kingDistance < pawnDistance);
        }
        return getBoard().hasTwoKings() && getBoard().getPieces().size() == 4 &&
                getBoard().getPieces().stream().allMatch(p -> p.getType() == K || p.getType() == H);
    }

    //NegaMax
    /*private double negaMaxWithAlphaBeta(AiTree starterPos, int depth, double alpha, double beta) {

        synchronized (pauseFlag){

            while(pauseFlag.get()) {
                try {
                    pauseFlag.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            FenToBoard(starterPos.getFen(), getBoard());
            getBoard().rangeUpdater();

            if (depth == 0){
                return evaluate(starterPos);
            }

            if (starterPos.isGameEndInPos()){
                if (getBoard().isCheckMate()){
                    return -5000;
                }else {
                    return 0;
                }
            }

            ArrayList<String> possibilities = starterPos.collectPossibilities(false);

            AiTree nextChild;
            for (String child : possibilities) {

                nextChild = new AiTree(child);
                starterPos.getChildren().add(nextChild);

                double evaluation = -negaMaxWithAlphaBeta(nextChild, depth - 1, -beta, -alpha);

                if (evaluation >= beta){
                    break;
                }

                alpha = Math.max(alpha, evaluation);

            }

            starterPos.setFinalValue(alpha);
            return alpha;
        }

    }*/

    //endregion

    //endregion

}
