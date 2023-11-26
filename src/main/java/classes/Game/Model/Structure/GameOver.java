package classes.Game.Model.Structure;

import lombok.*;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

@Getter
public enum GameOver {

    CheckMate,

    Draw;

    public static GameOver gameEnd(Board board){
        if (notNull(board.getCheckers())){
            if (notNull(board.getCheckers().getSecond()) &&
                    board.getMyKing().getPossibleRange().isEmpty()){
                gameEndFlag.set(true);
                return CheckMate;
            } else if (isNull(board.getCheckers().getSecond()) &&
                    board.myPieces().stream().allMatch(p -> p.getPossibleRange().isEmpty())) {
                gameEndFlag.set(true);
                return CheckMate;
            }
        } else if (board.getPieces().stream().allMatch(p -> p.getType() == K) || board.myPieces().stream().allMatch(p ->((Piece) p).getPossibleRange().isEmpty())){
            gameEndFlag.set(true);
            return Draw;
        }

        return null;
    }

}
