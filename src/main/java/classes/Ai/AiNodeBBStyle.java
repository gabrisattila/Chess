package classes.Ai;

import classes.Game.I18N.Pair;
import classes.Game.Model.Structure.BitBoard.BitBoardMoves;
import classes.Game.Model.Structure.BitBoard.Zobrist;
import lombok.*;

import java.util.*;

import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoardMoves.*;

@Getter
@Setter
public class AiNodeBBStyle {

    //region Fields

    private long zobristKey;

    private String theMoveWhatsCreatedIt;

    private double finalValue;

    private Set<AiNodeBBStyle> children;

    //endregion


    //region Constructor

    public AiNodeBBStyle(){
        children = new HashSet<>();
    }

    public AiNodeBBStyle(long zKey, String creatorMove){
        zobristKey = zKey;
        theMoveWhatsCreatedIt = creatorMove;
        children = new HashSet<>();
    }

    //endregion


    @Override
    public boolean equals(Object o){
        return o instanceof AiNodeBBStyle && zobristKey == ((AiNodeBBStyle) o).zobristKey;
    }

    public static long getZobristKey(boolean forWhite, int emPassant,
                                boolean wKC, boolean wQC, boolean bKC, boolean bQC,
                                long wP, long wH, long wF, long wB, long wV, long wK,
                                long bP, long bH, long bF, long bB, long bV, long bK){
        return Zobrist.getZobristKey(forWhite, emPassant, wKC, wQC, bKC, bQC, wP, wH, wF, wB, wV, wK, bP, bH, bF, bB, bV, bK);
    }


    public static AiNodeBBStyle putNewToNodeMap(AiNodeBBStyle root, String creatorMove,
            boolean forWhite, int emPassant, boolean wKC, boolean wQC, boolean bKC, boolean bQC,
            long whitePawn, long whiteBishop, long whiteKnight, long whiteRook, long whiteQueen, long whiteKing,
            long blackPawn, long blackBishop, long blackKnight, long blackRook, long blackQueen, long blackKing){

        long zKey = getZobristKey(forWhite, emPassant,  wKC,  wQC,  bKC,  bQC,
                whitePawn,  whiteBishop,  whiteKnight,  whiteRook,  whiteQueen,  whiteKing,
                blackPawn,  blackBishop,  blackKnight,  blackRook,  blackQueen,  blackKing);

        AiNodeBBStyle nextChild = alreadyWatchedNodes.get(zKey);
        if (isNull(nextChild)) {
            nextChild = new AiNodeBBStyle(zKey, creatorMove);
            alreadyWatchedNodes.put(zKey, nextChild);
        } else {
            transPosNum++;
        }
        root.getChildren().add(nextChild);
        return nextChild;
    }
    
}
