package classes.AI.Ai;

import classes.AI.BitBoards.Zobrist;
import lombok.*;

import java.util.*;

import static classes.AI.BitBoards.BBVars.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

@Getter
@Setter
public class AiNode {

    //region Fields

    private long zobristKey;

    private int theMoveWhatsCreatedIt;

    private double finalValue;

    private Set<AiNode> children;

    private int childrenNum = 0;

    //endregion


    //region Constructor

    public AiNode(){
        children = new HashSet<>(50);
    }

    public AiNode(int creatorMove){
        theMoveWhatsCreatedIt = creatorMove;
        children = new HashSet<>(50);
    }

    //endregion


    //region Methods

    @Override
    public boolean equals(Object o){
        return o instanceof AiNode && zobristKey == ((AiNode) o).zobristKey;
    }

    public static long calcZobristKey(boolean forWhite, int emPassant,
                                      boolean wKC, boolean wQC, boolean bKC, boolean bQC,
                                      long wP, long wN, long wB, long wR, long wQ, long wK,
                                      long bP, long bN, long bB, long bR, long bQ, long bK){
        return Zobrist.getZobristKey(forWhite, emPassant, wKC, wQC, bKC, bQC, wP, wN, wB, wR, wQ, wK, bP, bN, bB, bR, bQ, bK);
    }

    public static AiNode putNewToNodeMap(int creatorMove){
        long zKey = calcZobristKey(
                whiteToPlay, bbEmPassant, (castle & wK) != 0, (castle & wQ) != 0, (castle & bK) != 0, (castle & bQ) != 0,
                bitBoards[wPawnI], bitBoards[wKnightI], bitBoards[wBishopI], bitBoards[wRookI], bitBoards[wQueenI], bitBoards[wKingI],
                        bitBoards[bPawnI], bitBoards[bKnightI], bitBoards[bBishopI], bitBoards[bRookI], bitBoards[bQueenI], bitBoards[bKingI]
        );
//        AiNode next = alreadyWatchedNodes.get(zKey);
        AiNode next = new AiNode(creatorMove);
//        if (isNull(next)){
//            putToAlreadyWatchedZKeys(zKey, next);
//        } else {
//            transPosNum++;
//        }
        return next;
    }

    public static void putToAlreadyWatchedZKeys(long zKey, AiNode node){
        alreadyWatchedNodes.put(zKey, node);
    }

    public static void appendToHappenedList(String fen) {
        if (happenedList.containsKey(fen)) {
            int i = happenedList.get(fen);
            happenedList.put(fen, i);
        }else {
            happenedList.put(fen, 1);
        }
    }

    //endregion
    
}
