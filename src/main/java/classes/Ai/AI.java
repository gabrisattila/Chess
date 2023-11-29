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

    private Pair<Integer, Location> kingAliveCheckRandomMove(int indexOfChosen, Location toStepOn, ArrayList<Location> ableToStepThere, Random random)  {
        while (notNull(getBoard().getPiece(toStepOn)) && getBoard().getPiece(toStepOn).getType() == K){
            indexOfChosen = random.nextInt(0, ableToStepThere.size());
            toStepOn = ableToStepThere.get(indexOfChosen);
        }
        return new Pair<>(indexOfChosen, toStepOn);
    }


    //region Mini Max

    private void moveWithMiniMaxAi() {
        AiTree tree = new AiTree(BoardToFen(getBoard()));

        double best = simpleMiniMax(tree, 0, whiteToPlay, -350, 350);

        for (AiTree child : tree.getChildren()) {
            if (best == child.getFinalValue()){
                FenToBoard(child.getFen(), getBoard());
                break;
            }
        }
    }

    private double negaMaxWithAlphaBeta(AiTree starterPos, int depth, double alpha, double beta) {

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

    private double simpleMiniMax(AiTree starterPos, int depth, boolean maxNeeded, double alpha, double beta) {

        FenToBoard(starterPos.getFen(), getBoard());
        getBoard().rangeUpdater();
        SubmissionOrDrawThinking();

        GameOverAction(starterPos);

        if (depth == MINIMAX_DEPTH || gameFinished()){
            double evaluation = 0;
            if (gameFinished()){
                if (getBoard().isCheckMate()) {
                    if (whiteToPlay) {
                        //Sötét nyert, mert világos kapott mattot
                        evaluation = -5000;
                    } else {
                        //Világos nyert, mert sötét kapott mattot
                        evaluation = 5000;
                    }
                } else if (getBoard().isSubmitted()) {
                    if (whiteToPlay) {
                        //Sötét nyert, mert világos adta fel
                        evaluation = -5000;
                    } else {
                        //Világos nyert, mert sötét adta fel
                        evaluation = 5000;
                    }
                } else if (getBoard().isDraw()) {
                    //TODO Kitalálni kinek hogyan súlyozzam adott helyzethez mérten
                    evaluation = 0;
                }
            }else {
                evaluation = evaluate(starterPos);
            }

            starterPos.setFinalValue(evaluation);
            return evaluation;
        }

        Set<String> possibilities = starterPos.collectPossibilities();

        AiTree nextChild;
        if (maxNeeded){
            double possibleMax = -350;
            for (String child : possibilities){
                nextChild = new AiTree(child);
                starterPos.getChildren().add(nextChild);

                double evaluatedMiniMax = simpleMiniMax(nextChild, depth + 1, false, -350, 350);
                possibleMax = Math.max(possibleMax, evaluatedMiniMax);
                alpha = Math.max(alpha, evaluatedMiniMax);
                if (beta <= alpha)
                    break;
            }
            starterPos.setFinalValue(possibleMax);
            return possibleMax;
        }else {
            double possibleMin = 350;
            for (String child : possibilities){
                nextChild = new AiTree(child);
                starterPos.getChildren().add(nextChild);

                double evaluatedMiniMax = simpleMiniMax(nextChild, depth + 1, true, -350, 350);
                possibleMin = Math.min(possibleMin, evaluatedMiniMax);
                beta = Math.min(beta, evaluatedMiniMax);
                if (beta <= alpha)
                    break;
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

    public static double evaluate(AiTree aiTree)  {

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
