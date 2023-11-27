package classes.Ai;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.Pair;
import classes.Game.Model.Structure.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Game.Model.Structure.Board.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.GameOver.*;
import static classes.Game.Model.Structure.Move.*;

@Getter
@Setter
public class AI extends Thread {

    //region Fields

    private String color;

    //endregion


    //region Constructor

    public AI(String color){
        this.color = color;
    }

    //endregion


    //region Methods

    @Override
    public void run(){
        try {
            aiMove();
            receivedMoveFromAi(BoardToFen(getBoard()));
        } catch (ChessGameException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void aiMove() throws ChessGameException, InterruptedException {
        convertOneBoardToAnother(getViewBoard(), getBoard());
        Move();
    }

    public void Move() throws ChessGameException, InterruptedException {
        moveWithSimpleAi();
//        moveWithMiniMaxAi();
    }

    public void moveWithSimpleAi() throws ChessGameException, InterruptedException {

        synchronized (pauseFlag){
            while (pauseFlag.get()) {
                try {
                    pauseFlag.wait();
                } catch (InterruptedException ignored) {}
            }

            getBoard().rangeUpdater();
            GameOverAction(getBoard());
            if (gameFinished())
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

    private boolean gameFinished(){
        return getBoard().isGameFinished();
    }

    private Pair<Integer, Location> kingAliveCheckRandomMove(int indexOfChosen, Location toStepOn, ArrayList<Location> ableToStepThere, Random random) throws ChessGameException {
        while (notNull(getBoard().getPiece(toStepOn)) && getBoard().getPiece(toStepOn).getType() == K){
            indexOfChosen = random.nextInt(0, ableToStepThere.size());
            toStepOn = ableToStepThere.get(indexOfChosen);
        }
        return new Pair<>(indexOfChosen, toStepOn);
    }


    //region Mini Max

    private void moveWithMiniMaxAi() throws ChessGameException, InterruptedException {
        AiTree tree = new AiTree(BoardToFen(getBoard()));

        double best = simpleMiniMaxWithAlphaBeta(tree, 0, whiteToPlay);

        String bestChildsFen = "";

        for (AiTree child : tree.getChildren()) {
            if (best == child.getFinalValue()){
                bestChildsFen = child.getFen();
                FenToBoard(bestChildsFen, getBoard());
                break;
            }
        }
    }

    private double negaMaxWithAlphaBeta(AiTree starterPos, int depth, double alpha, double beta) throws ChessGameException, InterruptedException {

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

            Set<String> possibilities = starterPos.collectPossibilities();

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

    }

    private double simpleMiniMaxWithAlphaBeta(AiTree starterPos, int depth, boolean maxNeeded) throws ChessGameException, InterruptedException {

        FenToBoard(starterPos.getFen(), getBoard());
        getBoard().rangeUpdater();
        SubmissionOrDrawThinking();

        GameOverAction(starterPos);

        if (depth == MINIMAX_DEPTH || gameFinished()){

            if (gameFinished()){
                if (getBoard().isCheckMate()) {
                    if (whiteToPlay) {
                        //Sötét nyert, mert világos kapott mattot
                        return -5000;
                    } else {
                        //Világos nyert, mert sötét kapott mattot
                        return 5000;
                    }
                } else if (getBoard().isSubmitted()) {
                    if (whiteToPlay) {
                        //Sötét nyert, mert világos kapott mattot
                        return -5000;
                    } else {
                        //Világos nyert, mert sötét kapott mattot
                        return 5000;
                    }
                } else if (getBoard().isDraw()) {
                    //TODO Kitalálni kinek hogyan súlyozzam adott helyzethez mérten
                    return 0;
                }
            }

            return evaluate(starterPos);
        }

        Set<String> possibilities = starterPos.collectPossibilities();

        AiTree nextChild;
        if (maxNeeded){
            double possibleMax = -350;
            for (String child : possibilities){

                nextChild = new AiTree(child);
                starterPos.getChildren().add(nextChild);
                double evaluatedMiniMax = simpleMiniMaxWithAlphaBeta(nextChild, depth < MINIMAX_DEPTH ? depth + 1 : depth, false);

                possibleMax = Math.max(possibleMax, evaluatedMiniMax);
            }
            starterPos.setFinalValue(possibleMax);
            return possibleMax;
        }else {
            double possibleMin = 350;
            for (String child : possibilities){

                nextChild = new AiTree(child);
                starterPos.getChildren().add(nextChild);
                double evaluatedMiniMax = simpleMiniMaxWithAlphaBeta(nextChild, depth < MINIMAX_DEPTH ? depth + 1 : depth, true);

                possibleMin = Math.min(possibleMin, evaluatedMiniMax);
            }
            starterPos.setFinalValue(possibleMin);
            return possibleMin;
        }
    }

    private void SubmissionOrDrawThinking(){
//        if (){
//            getBoard().setSubmitted(true);
//        }
    }

    public static double evaluate(AiTree aiTree) throws ChessGameException {

        FenToBoard(aiTree.getFen(), getBoard());

        double sum = 0;

        for (IPiece p : getBoard().getPieces()) {
            sum += ((Piece) p).getVALUE();
        }

        return sum;
    }

    //endregion

    //endregion

}
