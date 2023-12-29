package classes.Model.AI.BitBoards;

import classes.Model.I18N.VARS;

import static classes.Model.I18N.VARS.MUTABLE.*;

import java.util.*;

public class BBVars {

    //region Base Piece BitBoards

    //One for each piece
    public static long[] bitBoards = new long[12];

    public static final int wPawnI = 0;

    public static final int wKnightI = 1;

    public static final int wBishopI = 2;

    public static final int wRookI = 3;

    public static final int wQueenI = 4;

    public static final int wKingI = 5;

    public static final int bPawnI = 6;

    public static final int bKnightI = 7;

    public static final int bBishopI = 8;

    public static final int bRookI = 9;

    public static final int bQueenI = 10;

    public static final int bKingI = 11;

    public static final int[] pieceIndexes = new int[]{
            wPawnI, wKnightI, wBishopI, wRookI, wQueenI, wKingI, bPawnI, bKnightI, bBishopI, bRookI, bQueenI, bKingI
    };

    //First we always generate all the possible moves from all squares
    public static long[][] pawnSimpleStepTable = new long[2][64];

    public static long[][] pawnAttackTable = new long[2][64];

    public static long[] knightPossibilityTable = new long[64];

    public static long[] bishopPossibilityTable = new long[64];

    public static long[] rookPossibilityTable = new long[64];

    public static long[] queenPossibilityTable = new long[64];

    public static long[] kingPossibilityTable = new long[64];

    public static long[][] basePossibilities = new long[12][64];

    //endregion


    /** Castling vars explanation
     0001    1  white king castle to the king side
     0010    2  white king castle to the queen side
     0100    4  black king castle to the king side
     1000    8  black king castle to the queen side
     */
    public static final int wK = 1;

    public static final int wQ = 2;

    public static final int bK = 4;

    public static final int bQ = 8;

    /**
     * In this we store castle options
     */
    public static int castle = 0; // no castles enabled, set after fen

    public static int bbEmPassant = -1;

    //For Undo move

    public static Stack<long[]> bitBoardsCopy = new Stack<>();

    public static Stack<Integer> castleCopy = new Stack<>();

    public static Stack<Integer> bbEmPassantCopy = new Stack<>();

    public static Stack<Boolean> whiteToPlayCopy = new Stack<>();


    /**
     * Helper in miniMax
     */
    public static int ply = 0;

    public static int bestMove;

    
    //region Helper Boards

    public static final long COL_A = whiteDown ? -9187201950435737472L : 72340172838076673L;

    public static final long COL_H = whiteDown ? 72340172838076673L : -9187201950435737472L;

    public static final long COL_AB = whiteDown ? -4557430888798830400L : 217020518514230019L;

    public static final long COL_GH = whiteDown ? 217020518514230019L : -4557430888798830400L;

    public static final long ROW_1 = whiteDown ? 255L : -72057594037927936L;

    public static final long ROW_2 = whiteDown ? 65280 : 71776119061217280L;

    public static final long ROW_3 = whiteDown ? 16711680 : 280375465082880L;

    public static final long ROW_6 = whiteDown ? 280375465082880L : 16711680;

    public static final long ROW_7 = whiteDown ? 71776119061217280L : 65280;

    public static final long ROW_8 = whiteDown ? -72057594037927936L : 255L;

    public static final long KING_SIDE = whiteDown ? 1085102592571150095L : ~1085102592571150095L;

    public static final long QUEEN_SIDE = whiteDown ? ~1085102592571150095L : 1085102592571150095L;

    public static long KING_SPAN = 924430;

    public static long KNIGHT_SPAN = 43234889994L;

    public static long HITTABLE_BY_WHITE;

    public static long HITTABLE_BY_BLACK;

    public static long EMPTY;

    public static long OCCUPIED;

    public static final Map<Integer, Integer> oppositeInsideEight = new HashMap<>(){{
        put(7, 0);
        put(0, 7);
        put(6, 1);
        put(1, 6);
        put(5, 2);
        put(2, 5);
        put(4, 3);
        put(3, 4);
    }};

    public static final ArrayList<String> englishPieceLetters = new ArrayList<>(){{
        add("P"); add("N"); add("B"); add("R"); add("Q"); add("K");
        add("p"); add("n"); add("b"); add("r"); add("q"); add("k");
    }};

    //endregion

}

