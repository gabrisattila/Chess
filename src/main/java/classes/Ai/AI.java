package classes.Ai;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.I18N.Pair;
import classes.Game.I18N.Tuple;
import classes.Game.Model.Structure.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import static classes.Ai.AiTree.*;
import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Game.Model.Structure.Board.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.Move.*;
import static classes.Game.Model.Structure.GameOver.*;

@Getter
@Setter
public class AI extends Thread {

    //region Fields

    private String color;
    
    private Tuple<Boolean, Boolean, Boolean> gameEnd = new Tuple<>(false, false, false);

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
            String fen = aiMove();
            receivedMoveFromAi(fen);
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the Fen String of the best option what minimax chosen
     */
    public String aiMove() throws ChessGameException {
        convertOneBoardToAnother(getViewBoard(), getAiBoard());
        return calculate();
    }

    /**
     * @return the Fen String of the best option what minimax chosen
     */
    public String calculate() {
        String fen;
        try {
            fen = Move();
            addToContinuousTree(fen);
        } catch (ChessGameException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return fen;
    }

    public String Move() throws ChessGameException, InterruptedException {

        String fenToMoveTowardsWith = moveWithSimpleAi();
//        String fenToMoveTowardsWith = moveWithMiniMaxAi();
        if (fenToMoveTowardsWith.length() < 10){
            getViewBoard().gameEndDialog(fenToMoveTowardsWith);
            this.interrupt();
        }
        return fenToMoveTowardsWith;

    }

    public String moveWithSimpleAi() throws ChessGameException, InterruptedException {

        synchronized (pauseFlag){
            while (pauseFlag.get()) {
                try {
                    pauseFlag.wait();
                } catch (InterruptedException ignored) {}
            }

            Random random = new Random();
            getAiBoard().rangeUpdater();
            gameEndCheck();
            if (gameFinished()){
                if (gameEnd.getFirst()){
                    return "CheckMate";
                } else if (gameEnd.getSecond()) {
                    return "Draw";
                } else if (gameEnd.getThird()) {
                    return "Submitted";
                }
            }

            Piece stepper;
            ArrayList<Piece> possibleSteppers = new ArrayList<>();
            for (IPiece p : getAiBoard().myPieces()) {
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

            Move move = new Move(stepper, toStepOn, getAiBoard());
            move.setMustLogged(true);
            Step(move);

            return BoardToFen(getAiBoard());
        }

    }

    private void gameEndCheck() throws ChessGameException {
        if (getAiBoard().isCheckMate()){
            gameEnd.setFirst(true);
        } else if (getAiBoard().isDraw()) {
            gameEnd.setSecond(true);
        } else if (getAiBoard().isSubmitted()) {
            gameEnd.setThird(true);
        }
    }

    private boolean gameFinished(){
        return gameEnd.getFirst() || gameEnd.getSecond() || gameEnd.getThird();
    }

    private static boolean checkIfItsPawnGotIn(IPiece stepper, Location to){
        return stepper.getType() == G && (
                to.getI() == stepper.getAttributes().getEnemyAndOwnStartRow().getFirst() ||
                to.getI() == 7 || to.getI() == 0);
    }

    private Pair<Integer, Location> kingAliveCheckRandomMove(int indexOfChosen, Location toStepOn, ArrayList<Location> ableToStepThere, Random random) throws ChessGameException {
        while (notNull(getAiBoard().getPiece(toStepOn)) && getAiBoard().getPiece(toStepOn).getType() == K){
            indexOfChosen = random.nextInt(0, ableToStepThere.size());
            toStepOn = ableToStepThere.get(indexOfChosen);
        }
        return new Pair<>(indexOfChosen, toStepOn);
    }


    //region Mini Max

    private String moveWithMiniMaxAi() throws ChessGameException, InterruptedException {
        AiTree tree = new AiTree(BoardToFen(getAiBoard()));

//        double best = newMiniMax(tree, MINIMAX_DEPTH, -350, 350);
        double best = simpleMiniMaxWithAlphaBeta(tree, 0, whiteToPlay);

        String bestChildsFen = "";

        for (AiTree child : tree.getChildren()) {
            if (best == child.getFinalValue()){
                bestChildsFen = child.getFen();
                FenToBoard(bestChildsFen, getAiBoard());
                break;
            }
        }

        if (getAiBoard().isDraw() || getAiBoard().isCheckMate()){
            //TODO GameOver lekezelése
        }
        return bestChildsFen;
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

            FenToBoard(starterPos.getFen(), getAiBoard());
            getAiBoard().rangeUpdater();

            if (depth == 0){
                return evaluate(starterPos);
            }

            if (starterPos.isGameEndInPos()){
                if (getAiBoard().isCheckMate()){
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

        FenToBoard(starterPos.getFen(), getAiBoard());
        getAiBoard().rangeUpdater();

        if (depth == MINIMAX_DEPTH || starterPos.isGameEndInPos()){

            if (starterPos.isGameEndInPos()){
                if (getAiBoard().isCheckMate()) {
                    if (whiteToPlay) {
                        //Sötét nyert, mert világos kapott mattot
                        return -5000;
                    } else {
                        //Világos nyert, mert sötét kapott mattot
                        return 5000;
                    }
                }else if (getAiBoard().isDraw()) {
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

    public static double evaluate(AiTree aiTree) throws ChessGameException {

        FenToBoard(aiTree.getFen(), getAiBoard());

        double sum = 0;

        for (IPiece p : getAiBoard().getPieces()) {
            sum += ((Piece) p).getVALUE();
        }

        return sum;
    }

    //endregion

    //endregion

}
