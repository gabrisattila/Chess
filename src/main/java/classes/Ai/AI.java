package classes.Ai;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.Model.Structure.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Game.Model.Structure.Board.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Structure.Move.MOVE;

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
            String fen = aiMove();
            receivedMoveFromAi(fen);
        } catch (InterruptedException | ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the Fen String of the best option what minimax chosen
     */
    public String aiMove() throws ChessGameException, InterruptedException {
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
        } catch (ChessGameException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return fen;
    }

    public String Move() throws ChessGameException, InterruptedException {

//        return moveWithSimpleAi();
        return moveWithMiniMaxAi();

    }

    public String moveWithSimpleAi() throws ChessGameException {

        Random random = new Random();
        getAiBoard().rangeUpdater();

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

        MOVE(getAiBoard(), stepper, toStepOn);


        return BoardToFen(getAiBoard());
    }

    private String moveWithMiniMaxAi() throws ChessGameException {
        AiTree tree = new AiTree(BoardToFen(getAiBoard()));

        double best = miniMax(tree, 0, whiteToPlay, -350, 350);

        String bestChildsFen = "";

        for (AiTree child : tree.getChildren()) {
            if (best == child.getFinalValue()){
                bestChildsFen = child.getFen();
                break;
            }
        }

        return bestChildsFen;
    }

    private double miniMax(AiTree starterPos, int depth, boolean maxNeeded, double alpha, double beta) throws ChessGameException {

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
                double evaluatedMiniMax = miniMax(nextChild, depth < MINIMAX_DEPTH ? depth + 1 : depth, false, alpha, beta);

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
                double evaluatedMiniMax = miniMax(nextChild, depth < MINIMAX_DEPTH ? depth + 1 : depth, true, alpha, beta);

                possibleMin = Math.min(possibleMin, evaluatedMiniMax);
                beta = Math.min(beta, evaluatedMiniMax);
                if (beta <= alpha)
                    break;
            }
            starterPos.setFinalValue(possibleMin);
            return possibleMin;
        }
    }

    private double evaluate(AiTree aiTree) throws ChessGameException {

        FenToBoard(aiTree.getFen(), getAiBoard());

        return getAiBoard().getPieces().stream()
                .mapToDouble(p -> ((Piece) p).getVALUE())
                .sum();
    }


    //endregion

}
