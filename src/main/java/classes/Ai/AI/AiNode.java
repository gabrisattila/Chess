package classes.Ai.AI;

import classes.Ai.BitBoards.Zobrist;
import lombok.*;

import java.util.*;

import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Ai.BitBoards.BBVars.*;
import static classes.Ai.BitBoards.BitBoards.*;

@Getter
@Setter
public class AiNode {

    //region Fields

    private long zobristKey;

    private int theMoveWhatsCreatedIt;

    private double finalValue;

    private AiNode[] children;

    private int childrenNum = 0;

    //endregion


    //region Constructor

    public AiNode(){
        children = new AiNode[100];
    }

    public AiNode(int creatorMove){
        theMoveWhatsCreatedIt = creatorMove;
        children = new AiNode[100];
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

    public static AiNode putNewToNodeMap(String creatorMove){
        return null;
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
