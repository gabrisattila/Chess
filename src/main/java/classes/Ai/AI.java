package classes.Ai;

import classes.Game.I18N.ChessGameException;
import classes.Game.Model.Structure.Board;
import classes.Game.Model.Structure.Field;
import classes.Game.Model.Structure.Piece;
import lombok.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

import static classes.Ai.AiBoard.*;
import static classes.Ai.FenConverter.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;

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

    public void aiTurn() throws ChessGameException, InterruptedException {

        putToFenQueue(BoardToFen(getViewBoard()), rightQueue(whiteToPlay ? "WHITE" : "BLACK"));

        if (aiStarted) {
            calculate();
        } else {
            start();
            aiStarted = true;
        }
    }

    @Override
    public void run(){
        calculate();
    }

    public void calculate() {
        String baseFen;
        String fenToPut;
        try {
            baseFen = takeFromFenQueue(rightQueue(color));
            getAiBoard().pieceSetUp(baseFen);
            fenToPut = Move();
            putToFenQueue(fenToPut, rightQueue(whiteToPlay ? "WHITE" : "BLACK"));
            FenToBoard(fenToPut, getViewBoard());
        } catch (InterruptedException | ChessGameException e) {
            throw new RuntimeException(e);
        }
        AiTurn = false;
    }

    public String Move() throws ChessGameException {

        Random random = new Random();

        int indexOfChosen = random.nextInt(0, getAiBoard().getPieces().size());

        Piece stepper;
//        int i = 0;
//        int numOfPieces = getAiBoard().getPieces().size();
        while ((stepper = getAiBoard().getPieces().get(indexOfChosen)).isWhite() != WHITE_STRING.equals(color)){
//                || i < numOfPieces){
            indexOfChosen = random.nextInt(0, getAiBoard().getPieces().size());
//            i++;
        }
//        if (i == numOfPieces){
//            throw new ChessGameException("The game is over!");
//        }

        ArrayList<Field> ableToStepThere = new ArrayList<>();

        for (ArrayList<Field> row : getAiBoard().getFields()) {
            for (Field f : row) {
                if (f.isGotPiece()){
                    if ((f.getPiece().isWhite() && !WHITE_STRING.equals(color)) ||
                            (!f.getPiece().isWhite() && WHITE_STRING.equals(color))){
                        ableToStepThere.add(f);
                    }
                }else {
                    ableToStepThere.add(f);
                }
            }
        }

        indexOfChosen = random.nextInt(0, ableToStepThere.size());
        Field toStepOn = ableToStepThere.get(indexOfChosen);

        pieceChangeOnBoard(
                            stepper,
                            getAiBoard().getFieldByPieceFromBoard(stepper),
                            toStepOn
        );

        return BoardToFen(getAiBoard());

    }

    //endregion

}
