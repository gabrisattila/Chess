package classes.Ai.BitBoards;

import classes.Ai.AI.AiNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static classes.Game.I18N.VARS.MUTABLE.whiteDown;

public class BBVars {

    //region Base Piece BitBoards

    //One for each piece
    public static final long[] bitBoards = new long[12];

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
    public static long[][] pawnPossibilityTable = new long[2][64];

    public static long[] knightPossibilityTable = new long[64];

    public static long[] bishopPossibilityTable = new long[64];

    public static long[] rookPossibilityTable = new long[64];

    public static long[] queenPossibilityTable = new long[64];

    public static long[] kingPossibilityTable = new long[64];

    public static long[][] basePossibilities = new long[12][64];

    //endregion

    public static final long[] occupancies = new long[3];

    //We use it like white to play but more useful here in int 1 = white, 0 = black
    public static int sideToMove;


    /**
     *
     0001    1  white king castle to the king side
     0010    2  white king castle to the queen side
     0100    4  black king castle to the king side
     1000    8  black king castle to the queen side
     */
    public static final int wK = 1;

    public static final int wQ = 2;

    public static final int bK = 3;

    public static final int bQ = 4;

    /**
     * In this we store castle options
     */
    public static int castle = 0;

    public static int emPassantBB = -1;
    
    //region Helper Boards

    public static final long COL_A = whiteDown ? -9187201950435737472L : 72340172838076673L;

    public static final long COL_H = whiteDown ? 72340172838076673L : -9187201950435737472L;

    public static final long COL_AB = whiteDown ? -4557430888798830400L : 217020518514230019L;

    public static final long COL_GH = whiteDown ? 217020518514230019L : -4557430888798830400L;

    public static final long ROW_1 = whiteDown ? 255L : -72057594037927936L;

    public static final long ROW_2 = whiteDown ? 0x000000000000FF00L : 0x00FF000000000000L;

    public static final long ROW_3 = whiteDown ? 0x00000000000000FFL : 0x000000FF00000000L;

    public static final long ROW_4 = whiteDown ? 4278190080L : 1095216660480L;

    public static final long ROW_5 = whiteDown ? 1095216660480L : 4278190080L;

    public static final long ROW_6 = whiteDown ? 0x000000FF00000000L : 0x00000000000000FFL;

    public static final long ROW_7 = whiteDown ? 1095216660480L : 4278190080L;

    public static final long ROW_8 = whiteDown ? -72057594037927936L : 255L;

    public static final Integer[] corners = new Integer[]{0, 7, 56, 63};

    public static final long KING_SIDE = whiteDown ? 1085102592571150095L : ~1085102592571150095L;

    public static final long QUEEN_SIDE = whiteDown ? ~1085102592571150095L : 1085102592571150095L;

    public static long KING_SPAN = 460039L;

    public static long KNIGHT_SPAN = 43234889994L;

    public static long HITTABLE_BY_WHITE;

    public static long HITTABLE_BY_BLACK;

    public static long EMPTY;

    public static long OCCUPIED;

    /**
     * The zobrist key list of the table. Zeroth element stands for bottom-left element on table.
     * The sequence goes from left to right, down to up. As we usually read longs.
     */
    public static ArrayList<HashMap<Character, Long>> ZOBRIST_KEY_FIELD_LIST = new ArrayList<>();

    public static ArrayList<Long> ZOBRIST_KEYS = new ArrayList<>();

    public static ArrayList<HashMap<Long, Character>> INVERSE_ZOBRIST_FIELD_LIST = new ArrayList<>();

    public static Long[] ZOBRIST_CASTLE_LIST = new Long[4];

    public static Long[] ZOBRIST_WHITE_BLACK_TO_PLAY = new Long[2];

    public static HashMap<Long, AiNode> alreadyWatchedNodes = new HashMap<>();

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

    public static final long[] RowMasks8 = {
            0xFFL,
            0xFF00L,
            0xFF0000L,
            0xFF000000L,
            0xFF00000000L,
            0xFF0000000000L,
            0xFF000000000000L,
            0xFF00000000000000L
    };

    public static final long[] ColMasks8 = {
            0x101010101010101L,
            0x202020202020202L,
            0x404040404040404L,
            0x808080808080808L,
            0x1010101010101010L,
            0x2020202020202020L,
            0x4040404040404040L,
            0x8080808080808080L
    };

    /*from bottom right to top left*/

    public static long[] DiagonalMasks8 = {
            0x1L, 0x102L, 0x10204L, 0x1020408L, 0x102040810L, 0x10204081020L, 0x1020408102040L,
            0x102040810204080L, 0x204081020408000L, 0x408102040800000L, 0x810204080000000L,
            0x1020408000000000L, 0x2040800000000000L, 0x4080000000000000L, 0x8000000000000000L
    };


    public static long[] AntiDiagonalMasks8 =/*from top right to bottom left*/{
        0x80L, 0x8040L, 0x804020L, 0x80402010L, 0x8040201008L, 0x804020100804L, 0x80402010080402L,
        0x8040201008040201L, 0x4020100804020100L, 0x2010080402010000L, 0x1008040201000000L,
        0x804020100000000L, 0x402010000000000L, 0x201000000000000L, 0x100000000000000L
    };


    //endregion

    public static final ArrayList<Character> englishPieceLetters = new ArrayList<>(){{
        add('P'); add('N'); add('B'); add('R'); add('Q'); add('K');
        add('p'); add('n'); add('b'); add('r'); add('q'); add('k');
    }};

    public static final String[] squaresStrings = new String[]{
            "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
            "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
            "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
            "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
            "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
            "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
            "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
            "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
    };

}

