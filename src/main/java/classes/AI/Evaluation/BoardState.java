package classes.AI.Evaluation;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
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


    private int kingPossibleMoveNum;


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

    /**
     * @returns 7 element
     */
    private ArrayList<Object> getOpeningParams(){
        ArrayList<Object> params = alwaysNeededParams();
        params.add(possibleMoveNum);
        return params;
    }

    /**
     * @returns 9 element
     */
    private ArrayList<Object> getMidGameParams(){
        ArrayList<Object> params = alwaysNeededParams();
        params.add(blockedPawnNum);
        params.add(isolatedPawnNum);
        params.add(possibleMoveNum);
        return params;
    }

    /**
     * @returns 10 element
     */
    private ArrayList<Object> getEndGameParams(){
        ArrayList<Object> params = alwaysNeededParams();
        params.add(blockedPawnNum);
        params.add(isolatedPawnNum);
        params.add(possibleMoveNum);
        params.add(kingPossibleMoveNum);
        return params;
    }

    /**
     * @return basicly it returns a list that contains 6 element
     */
    private ArrayList<Object> alwaysNeededParams() {
        ArrayList<Object> params = new ArrayList<>();
        params.add(whiteTurn);
        params.add(pawnNum);
        params.add(knightNum);
        params.add(bishopNum);
        params.add(rookNum);
        params.add(queenNum);
        return params;
    }

    //endregion

}
