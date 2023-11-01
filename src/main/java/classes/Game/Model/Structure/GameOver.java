package classes.Game.Model.Structure;

import lombok.*;

import static classes.Game.I18N.METHODS.isNull;
import static classes.Game.I18N.METHODS.notNull;

@Getter
public enum GameOver {

    CheckMate,

    Draw;

    public static GameOver gameEnd(Board board){
        if (notNull(board.getCheckers())){
            if (notNull(board.getCheckers().getSecond()) &&
                    ((Piece) board.getMyKing()).getLegalMoves().isEmpty()){
                return CheckMate;
            } else if (isNull(board.getCheckers().getSecond()) &&
                    board.myPieces().stream().allMatch(p -> p.getPossibleRange().isEmpty())) {
                return CheckMate;
            }
        } else if (board.myPieces().stream().allMatch(p -> p.getPossibleRange().isEmpty())){
            return Draw;
        }

        return null;
    }

}
