package classes.Game.Model.Structure.BitBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BBVars {

    //region Base BitBoards

    public static long whitePawn = 0L;

    public static long whiteKnight = 0L;

    public static long whiteBishop = 0L;

    public static long whiteRook = 0L;

    public static long whiteQueen = 0L;

    public static long whiteKing = 0L;


    public static long blackPawn = 0L;

    public static long blackKnight = 0L;

    public static long blackBishop = 0L;

    public static long blackRook = 0L;

    public static long blackQueen = 0L;

    public static long blackKing = 0L;

    //endregion
    
    //region Helper Boards

    public static final long FILE_A = 72340172838076673L;

    public static final long FILE_H = -9187201950435737472L;

    public static final long FILE_AB = 217020518514230019L;

    public static final long FILE_GH = -4557430888798830400L;

    public static final long RANK_1 = -72057594037927936L;

    public static final long RANK_4 = 1095216660480L;

    public static final long RANK_5 = 4278190080L;

    public static final long RANK_8 = 255L;

    public static final long CENTRE = 103481868288L;

    public static final long EXTENDED_CENTRE = 66229406269440L;

    //These two flips if white isn't down.
    public static final long KING_SIDE = 1085102592571150095L;

    public static final long QUEEN_SIDE = ~1085102592571150095L;

    public static final long KING_B7 = 460039L;

    public static final long KNIGHT_C6 = 43234889994L;

    public static long NOT_WHITE_PIECES;

    public static long BLACK_PIECES;

    public static long EMPTY;

    //endregion
    

    public static final Map<String, String> bbUsualFens = new HashMap<>(){{
        put("bbWhiteDownStarter", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 1 0");
        put("bbBlackDownStarter", "RNBKQBNR/PPPPPPPP/8/8/8/8/pppppppp/rnbkqbnr w KQkq - 1 0");
    }};

    public static final ArrayList<Character> englishPieceLetters = new ArrayList<>(){{
        add('p'); add('n'); add('b'); add('r'); add('q'); add('k');
        add('P'); add('N'); add('B'); add('R'); add('Q'); add('K');
    }};

}

