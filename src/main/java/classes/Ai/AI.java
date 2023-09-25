package classes.Ai;

import classes.Game.I18N.ChessGameException;
import classes.Game.Model.Structure.Field;
import classes.Game.Model.Structure.IField;
import classes.Game.Model.Structure.Piece;
import lombok.*;

import java.util.ArrayList;
import java.util.Random;

import static classes.Ai.FenConverter.*;
import static classes.Ai.Position.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.Helpers.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;
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
        while (gameIsOn){
            try {
                aiTurn();
                synchronized (this){
                    this.wait();
                }
            } catch (InterruptedException | ChessGameException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void aiTurn() throws ChessGameException, InterruptedException {
        passViewBoardInFenTo(getAiBoard());
        calculate();
        aiTurn = false;
    }

    public void calculate() {
        String fenToPut;
        try {
            fenToPut = Move();
            FenToBoard(fenToPut, getViewBoard());
            System.out.println("--------------------------");
            System.out.println("By AI:\n" + printBoardWithPieces(getViewBoard(), true));
            System.out.println("--------------------------");
        } catch (ChessGameException e) {
            throw new RuntimeException(e);
        }
    }

    public String Move() throws ChessGameException {

        Random random = new Random();

        int indexOfChosen = random.nextInt(0, getAiBoard().getPieces().size());

        Piece stepper;
//        int i = 0;
//        int numOfPieces = getAiBoard().getPieces().size();
        while ((stepper = ((Piece)getAiBoard().getPieces().get(indexOfChosen))).isWhite() != WHITE_STRING.equals(color)){
//                || i < numOfPieces){
            indexOfChosen = random.nextInt(0, getAiBoard().getPieces().size());
//            i++;
        }
//        if (i == numOfPieces){
//            throw new ChessGameException("The game is over!");
//        }

        ArrayList<Field> ableToStepThere = new ArrayList<>();

        for (ArrayList<IField> row : getAiBoard().getFields()) {
            for (IField f : row) {
                if (f.isGotPiece()){
                    if ((f.getPiece().isWhite() && !WHITE_STRING.equals(color)) ||
                            (!f.getPiece().isWhite() && WHITE_STRING.equals(color))){
                        ableToStepThere.add((Field) f);
                    }
                }else {
                    ableToStepThere.add((Field) f);
                }
            }
        }

        indexOfChosen = random.nextInt(0, ableToStepThere.size());
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
