package classes.Ai;

import classes.Game.I18N.ChessGameException;
import classes.Game.I18N.Location;
import classes.Game.Model.Structure.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Random;

import static classes.Ai.FenConverter.*;
import static classes.Ai.Position.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Game.Model.Structure.Board.*;

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

        Random random = new Random();

        getAiBoard().rangeUpdater();

        Piece stepper;
        ArrayList<Piece> possibleSteppers = new ArrayList<>();
        for (IPiece p : getAiBoard().getPieces()) {
            if (p.isWhite() == WHITE_STRING.equals(color) && !p.getPossibleRange().isEmpty()){
                possibleSteppers.add((Piece) p);
            }
        }
        stepper = possibleSteppers.get(random.nextInt(0, possibleSteppers.size()));


        ArrayList<Location> ableToStepThereInLocations = new ArrayList<>(stepper.getPossibleRange());

        int indexOfChosen = random.nextInt(0, ableToStepThereInLocations.size());
        ArrayList<Field> ableToStepThere = new ArrayList<>();
        for (Location l : ableToStepThereInLocations) {
            ableToStepThere.add((Field) getAiBoard().getField(l));
        }
        Field toStepOn = ableToStepThere.get(indexOfChosen);

        pieceChangeOnBoard(
                            stepper,
                            getAiBoard().getField(stepper),
                            toStepOn
        );

        return BoardToFen(getAiBoard());

    }

    private int miniMax(Position starterPos, boolean maxNeeded, int depth, int alpha, int beta) throws ChessGameException {

        if (depth == 0 || isGameEndInPos(starterPos)){

            if (isGameEndInPos(starterPos)){

            }

            return evaluate(starterPos);
        }

        ArrayList<String> possibilities = starterPos.collectPossiblePositions();

        if (maxNeeded){

        }else {

        }

        return 0;
    }

    private int evaluate(Position position){



        return 0;
    }

    //endregion

}
