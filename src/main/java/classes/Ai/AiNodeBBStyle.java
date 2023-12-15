package classes.Ai;

import classes.Game.Model.Structure.BitBoard.Zobrist;
import lombok.*;

import java.util.*;

import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.Model.Structure.BitBoard.BBVars.*;
import static classes.Game.Model.Structure.BitBoard.BitBoards.*;

@Getter
@Setter
public class AiNodeBBStyle {

    //region Fields

    private long zobristKey;

    private String theMoveWhatsCreatedIt;

    private double finalValue;

    private Set<AiNodeBBStyle> children;
    
    private boolean wKC, wQC, bKC, bQC;
    
    private int emPassant;
    
    private long wP,  wN,  wB,  wR, wQ, wK, bP,  bN,  bB,  bR,  bQ, bK;

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

    public static void setDescriptiveParts(AiNodeBBStyle node, int emPassant,
                                    boolean wKC, boolean wQC, boolean bKC, boolean bQC,
                                    long wP, long wN, long wB, long wR, long wQ, long wK,
                                    long bP, long bN, long bB, long bR, long bQ, long bK){

        node.wKC = wKC; node.wQC = wQC; node.bKC = bKC; node.bQC = bQC;

        node.emPassant = emPassant;

        node.wP = wP; node.wN = wN; node.wB = wB; node.wR = wR; node.wQ = wQ; node.wK = wK;
        node.bP = bP; node.bN = bN; node.bB = bB; node.bR = bR; node.bQ = bQ; node.bK = bK;
    }

    //endregion


    @Override
    public boolean equals(Object o){
        return o instanceof AiNodeBBStyle &&
                ((AiNodeBBStyle) o).wKC  == wKC && ((AiNodeBBStyle) o).wQC  == wQC && ((AiNodeBBStyle) o).bKC  == bKC &&
                ((AiNodeBBStyle) o).bQC  == bQC && ((AiNodeBBStyle) o).emPassant == emPassant &&
                ((AiNodeBBStyle) o).wP  == wP && ((AiNodeBBStyle) o).wN  == wN && ((AiNodeBBStyle) o).wB  == wB &&
                ((AiNodeBBStyle) o).wR  == wR && ((AiNodeBBStyle) o).wQ  == wQ && ((AiNodeBBStyle) o).wK  == wK &&
                ((AiNodeBBStyle) o).bP  == bP && ((AiNodeBBStyle) o).bN  == bN && ((AiNodeBBStyle) o).bB  == bB &&
                ((AiNodeBBStyle) o).bR  == bR && ((AiNodeBBStyle) o).bQ  == bQ && ((AiNodeBBStyle) o).bK  == bK;
    }

    public static String aiNodeBBToFen(AiNodeBBStyle node){
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

    public static AiNodeBBStyle putNewToNodeMap(AiNodeBBStyle root, String creatorMove,
                                                boolean forWhite, int emPassant, boolean wKC, boolean wQC, boolean bKC, boolean bQC,
                                                long whitePawn, long whiteKnight, long whiteBishop, long whiteRook, long whiteQueen, long whiteKing,
                                                long blackPawn, long blackKnight, long blackBishop, long blackRook, long blackQueen, long blackKing){

        long zKey = calcZobristKey(forWhite, emPassant,  wKC,  wQC,  bKC,  bQC,
                whitePawn, whiteKnight, whiteBishop,  whiteRook,  whiteQueen,  whiteKing,
                blackPawn, blackKnight, blackBishop,  blackRook,  blackQueen,  blackKing);

        AiNodeBBStyle nextChild = alreadyWatchedNodes.get(zKey);
        if (isNull(nextChild)) {
            nextChild = new AiNodeBBStyle(zKey, creatorMove);
            alreadyWatchedNodes.put(zKey, nextChild);
        } else {
            transPosNum++;
        }
        setDescriptiveParts(nextChild, emPassant,  wKC,  wQC,  bKC,  bQC,
                whitePawn,  whiteKnight, whiteBishop,  whiteRook,  whiteQueen,  whiteKing,
                blackPawn,  blackKnight, blackBishop,  blackRook,  blackQueen,  blackKing);
        root.getChildren().add(nextChild);
        return nextChild;
    }
    
}
