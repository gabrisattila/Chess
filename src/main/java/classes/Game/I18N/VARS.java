package classes.Game.I18N;

import classes.GUI.FrameParts.ViewField;
import classes.GUI.FrameParts.ViewPiece;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;

public class VARS {

    public static class FINALS{

        public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        public static final int FIELD_WIDTH = 75;

        public static final int FIELD_HEIGHT = 75;

        public static final String WHITE_STRING = "WHITE";

        public static final String BLACK_STRING = "BLACK";

        public static final String BAD_TYPE_MSG = "is not instance of Field or ViewField";

        public final static Color WHITE = new Color(255,228,196);

        public final static Color DARK_WHITE = new Color(85,85,85);

        public final static Color BLACK = new Color(131, 95, 33);

        public final static Color DARK_BLACK = new Color(77, 60, 22);

        public final static Color BACK_GROUND = new Color(33, 3, 8, 205);


        public static final ArrayList<Location> pawnMatrix = new ArrayList<>(){{
            add(new Location(1, -1));
            add(new Location(1, 0));
            add(new Location(1, 1));
            add(new Location(2, 0));
        }};

        public static final ArrayList<Location> knightMatrix = new ArrayList<>(){{
            add(new Location(2, -1));
            add(new Location(2, 1));
            add(new Location(1, -2));
            add(new Location(1, 2));
            add(new Location(-1, -2));
            add(new Location(-1, 2));
            add(new Location(-2, -1));
            add(new Location(-2, 1));
        }};

        public static final ArrayList<Location> bishopMatrix = new ArrayList<>(){{
            add(new Location(1, -1));
            add(new Location(1, 1));
            add(new Location(-1, -1));
            add(new Location(-1, 1));
        }};

        public static final ArrayList<Location> rookMatrix = new ArrayList<>(){{
                add(new Location(1, 0));
                add(new Location(0, -1));
                add(new Location(0, 1));
                add(new Location(-1, 0));
        }};

        public static final ArrayList<Location> qkMatrix = new ArrayList<>(){{
                add(new Location(1, -1));
                add(new Location(1, 0));
                add(new Location(1, 1));
                add(new Location(0, -1));
                add(new Location(0, 1));
                add(new Location(-1, -1));
                add(new Location(-1, 0));
                add(new Location(-1, 1));
        }};

        public static final Map<PieceType, ArrayList<Location>> matrixChooser = new HashMap<>(){{
            put(G, new ArrayList<>(pawnMatrix));
            put(H, knightMatrix);
            put(F, bishopMatrix);
            put(B, rookMatrix);
            put(V, qkMatrix);
            put(K, qkMatrix);
        }};

        public final static ArrayList<Character> whiteBlackLetters = new ArrayList<>(){{
            add('W');
            add('B');
        }};


        public static final ArrayList<Character> pieceLetters = new ArrayList<>(){{
            add('G');
            add('H');
            add('F');
            add('B');
            add('V');
            add('K');
        }};

        public static final ArrayList<Character> abc = new ArrayList<>(){{
            add('A');
            add('B');
            add('C');
            add('D');
            add('E');
            add('F');
            add('G');
            add('H');
        }};

        public static final ArrayList<Character> nums = new ArrayList<>(){{
            add('1');
            add('2');
            add('3');
            add('4');
            add('5');
            add('6');
            add('7');
            add('8');
        }};


        public static Map<String, String> usualFens = new HashMap<>(){{
            put("whiteDownStarter", "RNBQKBNR/PPPPPPPP/8/8/8/8/pppppppp/rnbqkbnr KQkq -");
            put("blackDownStarter", "rnbkqbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR KQkq -");
        }};

        //region PieceViews

        public static final ArrayList<ViewPiece> DICT_FOR_VIEW_PIECE = new ArrayList<>(){{

            //Világos

            add(new ViewPiece("src\\main\\resources\\Figura_Images\\w_gyalog.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\w_huszar.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\w_futo.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\w_bastya.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\w_vezer.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\w_kiraly.png"));

            //Sötét

            add(new ViewPiece("src\\main\\resources\\Figura_Images\\b_gyalog.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\b_huszar.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\b_futo.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\b_bastya.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\b_vezer.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\b_kiraly.png"));

        }};

        //endregion

    }

    public static class MUTUABLES{

        public static BlockingQueue<String> fenChannelFirst = new LinkedBlockingQueue<>();

        public static BlockingQueue<String> fenChannelSecond = new LinkedBlockingQueue<>();

        public static int MAX_WIDTH = 8;

        public static int MAX_HEIGHT = 8;

        public static double SCREEN_HEIGHT = screenSize.getHeight();

        public static double SCREEN_WIDTH = screenSize.getWidth();;

        public static double BOARD_HEIGHT = FIELD_HEIGHT * MAX_HEIGHT;

        public static double BOARD_WIDTH = FIELD_WIDTH * MAX_WIDTH;


        /**
         * This is the X coordinate where the board starts. It's used in calculate the position of the board.
         */
        public static double BOARD_START_X = (SCREEN_WIDTH - BOARD_WIDTH) / 2;

        /**
         * This is the Y coordinate where the board starts. It's used in calculate the position of the board.
         */
        public static double BOARD_START_Y = (SCREEN_HEIGHT - BOARD_HEIGHT) / 2;

        public static int CLICK_COUNTER = 0;

        public static ViewField lastClicked;

        public static ViewPiece pieceToChange;

        public static Boolean whiteToPlay = true;

//        public static Boolean gameIsOn = false;

        public static boolean theresOnlyOneAi;

        public static boolean whiteAiNeeded = false;

        public static boolean playerTurn;

        public static boolean aiTurn;

        public static boolean whiteSmallCastleHappened = false;

        public static boolean whiteBigCastleHappened = false;

        public static boolean blackSmallCastleHappened = false;

        public static boolean blackBigCastleHappened = false;

        public static String emPassantPossibility = "-";

    }

}
