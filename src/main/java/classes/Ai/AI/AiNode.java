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

    private String theMoveWhatsCreatedIt;

    private double finalValue;

    private Set<AiNode> children;

    private boolean wKC, wQC, bKC, bQC;

    private int emPassant;

    private long wP,  wN,  wB,  wR, wQ, wK, bP,  bN,  bB,  bR,  bQ, bK;

    //endregion


    //region Constructor

    public AiNode(){
        children = new HashSet<>();
    }

    public AiNode(long zKey, String creatorMove){
        zobristKey = zKey;
        theMoveWhatsCreatedIt = creatorMove;
        children = new HashSet<>();
    }

    public static void setDescriptiveParts(AiNode node, int emPassant,
                                           boolean wKC, boolean wQC, boolean bKC, boolean bQC,
                                           long wP, long wN, long wB, long wR, long wQ, long wK,
                                           long bP, long bN, long bB, long bR, long bQ, long bK){

        node.wKC = wKC; node.wQC = wQC; node.bKC = bKC; node.bQC = bQC;

        node.emPassant = emPassant;

        node.wP = wP; node.wN = wN; node.wB = wB; node.wR = wR; node.wQ = wQ; node.wK = wK;
        node.bP = bP; node.bN = bN; node.bB = bB; node.bR = bR; node.bQ = bQ; node.bK = bK;
    }

    //endregion


    //region Methods

    @Override
    public boolean equals(Object o){
        return o instanceof AiNode && zobristKey == ((AiNode) o).zobristKey;
    }

    public static String aiNodeToFen(AiNode node){
        return bitBoardsToFen(whiteToPlay, node.emPassant, node.wKC, node.wQC, node.bKC, node.bQC,
                                node.wP, node.wN, node.wB, node.wR, node.wQ, node.wK,
                                node.bP, node.bN, node.bB, node.bR, node.bQ, node.bK);
    }

    public static long calcZobristKey(boolean forWhite, int emPassant,
                                      boolean wKC, boolean wQC, boolean bKC, boolean bQC,
                                      long wP, long wN, long wB, long wR, long wQ, long wK,
                                      long bP, long bN, long bB, long bR, long bQ, long bK){
        return Zobrist.getZobristKey(forWhite, emPassant, wKC, wQC, bKC, bQC, wP, wN, wB, wR, wQ, wK, bP, bN, bB, bR, bQ, bK);
    }

    public static AiNode putNewToNodeMap(AiNode root, String creatorMove,
                                         boolean forWhite, int emPassant, boolean wKC, boolean wQC, boolean bKC, boolean bQC,
                                         long whitePawn, long whiteKnight, long whiteBishop, long whiteRook, long whiteQueen, long whiteKing,
                                         long blackPawn, long blackKnight, long blackBishop, long blackRook, long blackQueen, long blackKing){

        long zKey = calcZobristKey(forWhite, emPassant,  wKC,  wQC,  bKC,  bQC,
                whitePawn, whiteKnight, whiteBishop,  whiteRook,  whiteQueen,  whiteKing,
                blackPawn, blackKnight, blackBishop,  blackRook,  blackQueen,  blackKing);

        AiNode nextChild = alreadyWatchedNodes.get(zKey);
        if (isNull(nextChild)) {
            nextChild = new AiNode(zKey, creatorMove);
            putToAlreadyWatchedZKeys(zKey, nextChild);
        } else {
            transPosNum++;
        }
        setDescriptiveParts(nextChild, emPassant,  wKC,  wQC,  bKC,  bQC,
                whitePawn,  whiteKnight, whiteBishop,  whiteRook,  whiteQueen,  whiteKing,
                blackPawn,  blackKnight, blackBishop,  blackRook,  blackQueen,  blackKing);
        root.getChildren().add(nextChild);
        return nextChild;
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
