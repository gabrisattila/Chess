package classes.Game.Model.Structure.BitBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static classes.Game.I18N.VARS.MUTABLE.whiteDown;

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

    public static final long COL_A = whiteDown ? -9187201950435737472L : 72340172838076673L;

    public static final long COL_H = whiteDown ? 72340172838076673L : -9187201950435737472L;

    public static final long COL_AB = whiteDown ? -4557430888798830400L : 217020518514230019L;

    public static final long COL_GH = whiteDown ? 217020518514230019L : -4557430888798830400L;

    public static final long ROW_1 = whiteDown ? 255L : -72057594037927936L;

    public static final long ROW_8 = whiteDown ? -72057594037927936L : 255L;

    public static final long ROW_4 = whiteDown ? 4278190080L : 1095216660480L;

    public static final long ROW_5 = whiteDown ? 1095216660480L : 4278190080L;

    public static final long CENTRE = 103481868288L;

    public static final long EXTENDED_CENTRE = 66229406269440L;

    public static final long KING_SIDE = whiteDown ? 1085102592571150095L : ~1085102592571150095L;

    public static final long QUEEN_SIDE = whiteDown ? ~1085102592571150095L : 1085102592571150095L;

    public static final long KING_B7 = 460039L;

    public static final long KNIGHT_C6 = 43234889994L;

    public static long HITTABLE_BY_WHITE;

    public static long HITTABLE_BY_BLACK;

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

    public static final ArrayList<Character> hunPieceLetters = new ArrayList<>(){{
        add('g'); add('h'); add('f'); add('b'); add('v'); add('k');
        add('G'); add('H'); add('F'); add('B'); add('v'); add('K');
    }};

}
