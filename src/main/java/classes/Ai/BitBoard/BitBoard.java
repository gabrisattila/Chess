package classes.Ai.BitBoard;

import lombok.*;

import static classes.Game.I18N.METHODS.*;

@Getter
@Setter
public class BitBoard {


    //region Fields

    private static BitBoard bitBoard;
    
    private long whitePawn;

    private long whiteKnight;

    private long whiteBishop;

    private long whiteRook;

    private long whiteQueen;

    private long whiteKing;

    private long blackPawn;

    private long blackKnight;

    private long blackBishop;

    private long blackRook;

    private long blackQueen;
    
    private long blackKing;
    
    private boolean whiteTurn;
    
    private int emPassant;
    
    private boolean whiteKingCastle;
    
    private boolean whiteQueenCastle;
    
    private boolean blackKingCastle;
    
    private boolean blackQueenCastle;
    
    //endregion


    //region Constructor
    
    private BitBoard(){}
    
    public static BitBoard getBitBoard(){
        if (isNull(bitBoard)){
            bitBoard = new BitBoard();
        }
        return bitBoard;
    }

    public void setEveryThing(boolean whiteTurn, int emPassantChance,
                               boolean whiteKingCastleEnabled, boolean whiteQueenCastleEnabled,
                               boolean blackKingCastleEnabled, boolean blackQueenCastleEnabled,
                               long wP, long wN, long wB, long wR, long wQ, long wK,
                               long bP, long bN, long bB, long bR, long bQ, long bK){
        this.whiteTurn = whiteTurn;
        emPassant = emPassantChance;
        setCastles(whiteKingCastleEnabled, whiteQueenCastleEnabled, blackKingCastleEnabled, blackQueenCastleEnabled);
        setPiecesBoards(wP, wN, wR, wR, wQ, wK, bP, bN, bB, bR, bQ, bK);
    }

    public void setEveryThing(boolean whiteTurn,
                              boolean whiteKingCastleEnabled, boolean whiteQueenCastleEnabled,
                              boolean blackKingCastleEnabled, boolean blackQueenCastleEnabled,
                              long wP, long wN, long wB, long wR, long wQ, long wK,
                              long bP, long bN, long bB, long bR, long bQ, long bK){
        this.whiteTurn = whiteTurn;
        emPassant = -1;
        setCastles(whiteKingCastleEnabled, whiteQueenCastleEnabled, blackKingCastleEnabled, blackQueenCastleEnabled);
        setPiecesBoards(wP, wN, wR, wR, wQ, wK, bP, bN, bB, bR, bQ, bK);
    }


    public void setEveryThing(boolean whiteTurn,
                              long wP, long wN, long wB, long wR, long wQ, long wK,
                              long bP, long bN, long bB, long bR, long bQ, long bK){
        this.whiteTurn = whiteTurn;
        emPassant = -1;
        setCastles(false, false, false, false);
        setPiecesBoards(wP, wN, wR, wR, wQ, wK, bP, bN, bB, bR, bQ, bK);
    }

    public void setEveryThing(boolean whiteTurn, int emPassant,
                              long wP, long wN, long wB, long wR, long wQ, long wK,
                              long bP, long bN, long bB, long bR, long bQ, long bK){
        this.whiteTurn = whiteTurn;
        this.emPassant = emPassant;
        setCastles(false, false, false, false);
        setPiecesBoards(wP, wN, wR, wR, wQ, wK, bP, bN, bB, bR, bQ, bK);
    }

    private void setPiecesBoards(long wP, long wN, long wB, long wR, long wQ, long wK, long bP, long bN, long bB, long bR, long bQ, long bK) {
        whitePawn = wP; whiteKnight = wN; whiteBishop = wR; whiteRook = wR; whiteQueen = wQ; whiteKing = wK;
        blackPawn = bP; blackKnight = bN; blackBishop = bB; blackRook = bR; blackQueen = bQ; blackKing = bK;
    }

    private void setCastles(boolean wKC, boolean wQC, boolean bKC, boolean bQC){
        whiteKingCastle = wKC; whiteQueenCastle = wQC;
        blackKingCastle = bKC; blackQueenCastle = bQC;
    }

    //endregion

}
