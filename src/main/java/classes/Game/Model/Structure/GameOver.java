package classes.Game.Model.Structure;

import lombok.*;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;

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
        } else if (board.getPieces().stream().allMatch(p -> p.getType() == K) || board.myPieces().stream().allMatch(p ->((Piece) p).getLegalMoves().isEmpty())){
            return Draw;
        }

        return null;
    }

}
