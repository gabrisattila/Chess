package classes.Ai.Evaluation;

import java.util.ArrayList;
import java.util.Objects;

public class BoardState {

    //region Fields

    private boolean whiteTurn;

    private int pawnNum;

    private int knightNum;

    private int bishopNum;

    private int rookNum;

    private int queenNum;


    private int blockedPawnNum;

    private int isolatedPawnNum;

    private int possibleMoveNum;


    //endregion


    //region Constructor



    //endregion


    //region Methods

    public ArrayList<Object> getParams(GameState gameState){
        switch (gameState){
            case OPENING -> {
                return getOpeningParams();
            }
            case MIDDLE_GAME -> {
                return getMidGameParams();
            }
            case END_GAME -> {
                return getEndGameParams();
            }
        }
        return null;
    }

    private ArrayList<Object> getOpeningParams(){
        return null;
    }

    private ArrayList<Object> getMidGameParams(){
        return null;
    }

    private ArrayList<Object> getEndGameParams(){
        return null;
    }

    //endregion

}
