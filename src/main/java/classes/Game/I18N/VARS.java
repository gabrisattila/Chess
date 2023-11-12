package classes.Game.I18N;

import classes.Ai.AiTree;
import classes.GUI.FrameParts.*;
import classes.Game.Model.Structure.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

public class VARS {

    public static class FINALS{

        public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        public static final int FIELD_WIDTH = 75;

        public static final int FIELD_HEIGHT = 75;

        public static final double SCREEN_HEIGHT = screenSize.getHeight();

        public static final double SCREEN_WIDTH = screenSize.getWidth();;

        public static final double BOARD_HEIGHT = FIELD_HEIGHT * MAX_HEIGHT;

        public static final double BOARD_WIDTH = FIELD_WIDTH * MAX_WIDTH;

        /**
         * This is the X coordinate where the board starts. It's used in calculate the position of the board.
         */
        public static final double BOARD_START_X = (SCREEN_WIDTH - BOARD_WIDTH) / 2;

        /**
         * This is the Y coordinate where the board starts. It's used in calculate the position of the board.
         */
        public static final double BOARD_START_Y = (SCREEN_HEIGHT - BOARD_HEIGHT) / 2;

        public static final double BOARD_PLACE_START_X = (SCREEN_WIDTH - FIELD_WIDTH * 8) / 2 - 20;

        public static final double BOARD_PLACE_START_Y = (SCREEN_HEIGHT - FIELD_HEIGHT * 8) / 2 - 20;

        public static final double BOARD_PLACE_END_X = BOARD_PLACE_START_X + FIELD_WIDTH * 8 + 40;


        public static final double NEW_GAME_WINDOW_WIDTH = 6 * FIELD_WIDTH;

        public static final double NEW_GAME_WINDOW_HEIGHT = 6 * FIELD_HEIGHT;

        public static final double NEW_GAME_WINDOW_START_X = ((SCREEN_WIDTH - NEW_GAME_WINDOW_WIDTH) / 2) + 10;

        public static final double NEW_GAME_WINDOW_START_Y = (SCREEN_HEIGHT - NEW_GAME_WINDOW_HEIGHT) / 2 + 30;

        public static final double WHITE_TAKEN_PIECES_FIRST_ROW_START_X = BOARD_PLACE_START_X - 150;

        public static final double WHITE_TAKEN_PIECES_FIRST_ROW_START_Y = BOARD_PLACE_START_Y;

        public static final double WHITE_TAKEN_PIECES_SECOND_ROW_START_X = BOARD_PLACE_START_X - 75;

        public static final double BLACK_TAKEN_PIECES_FIRST_ROW_START_X = BOARD_PLACE_END_X;

        public static final double BLACK_TAKEN_PIECES_SECOND_ROW_START_Y = BOARD_START_Y;

        public static final double BLACK_TAKEN_PIECES_SECOND_ROW_START_X = BOARD_PLACE_END_X + 75;


        public static final double BUTTON_PLACE_START_Y = WHITE_TAKEN_PIECES_SECOND_ROW_START_X >= 150 ?
                BOARD_PLACE_START_Y : 0;

        public static final double LOGGER_WIDTH = WHITE_TAKEN_PIECES_FIRST_ROW_START_X - 50;

        public static final double LOGGER_HEIGHT = 4 * FIELD_HEIGHT;

        public static final double LOGGER_START_X = SCREEN_WIDTH - LOGGER_WIDTH - 20;

        public static final double LOGGER_START_Y = BUTTON_PLACE_START_Y + 4 * FIELD_HEIGHT;

        public static final double HORIZONTAL_SIDE_LABEL_WIDTH = FIELD_WIDTH;

        public static final double HORIZONTAL_SIDE_LABEL_HEIGHT = 20;

        public static final double VERTICAL_SIDE_LABEL_WIDTH = 20;

        public static final double VERTICAL_SIDE_LABEL_HEIGHT = FIELD_HEIGHT;


        public static final String WHITE_STRING = "WHITE";

        public static final String BLACK_STRING = "BLACK";

        public static final String BAD_TYPE_MSG = "is not instance of Field or ViewField";

        public final static Color WHITE = new Color(255,228,196);

        public final static Color DARK_WHITE = new Color(85,85,85);

        public final static Color BLACK = new Color(131, 95, 33);

        public final static Color DARK_BLACK = new Color(77, 60, 22);

        public final static Color BACK_GROUND = new Color(33, 3, 8, 205);


        public static final Set<Location> pawnMatrix = new HashSet<>(){{
            add(new Location(1, -1));
            add(new Location(1, 1));
            add(new Location(1, 0));
            add(new Location(2, 0));
        }};

        public static final Set<Location> knightMatrix = new HashSet<>(){{
            add(new Location(2, -1));
            add(new Location(2, 1));
            add(new Location(1, -2));
            add(new Location(1, 2));
            add(new Location(-1, -2));
            add(new Location(-1, 2));
            add(new Location(-2, -1));
            add(new Location(-2, 1));
        }};

        public static final Set<Location> bishopMatrix = new HashSet<>(){{
            add(new Location(1, -1));
            add(new Location(1, 1));
            add(new Location(-1, -1));
            add(new Location(-1, 1));
        }};

        public static final Set<Location> rookMatrix = new HashSet<>(){{
                add(new Location(1, 0));
                add(new Location(0, -1));
                add(new Location(0, 1));
                add(new Location(-1, 0));
        }};

        public static final Set<Location> qkMatrix = new HashSet<>(){{
                add(new Location(1, -1));
                add(new Location(1, 0));
                add(new Location(1, 1));
                add(new Location(0, -1));
                add(new Location(0, 1));
                add(new Location(-1, -1));
                add(new Location(-1, 0));
                add(new Location(-1, 1));
        }};

        public static final Map<PieceType, Set<Location>> matrixChooser = new HashMap<>(){{
            put(G, pawnMatrix);
            put(H, knightMatrix);
            put(F, bishopMatrix);
            put(B, rookMatrix);
            put(V, qkMatrix);
            put(K, qkMatrix);
        }};

        public static final ArrayList<String> castleMoveSigns = new ArrayList<>(){{
            add("Y"); add("y"); 
            add("K"); add("Q"); 
            add("k"); add("q");
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

        public static final ArrayList<String> numsForPrinting = new ArrayList<>(){{
            add("0");
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
            add("6");
            add("7");
        }};

        public static Map<String, String> usualFens = new HashMap<>(){{
            put("whiteDownStarter", "RNBQKBNR/PPPPPPPP/8/8/8/8/pppppppp/rnbqkbnr w KQkq - 1 0");
            put("blackDownStarter", "rnbkqbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR w KQkq - 1 0");
            put("onlyTwoKnights8x8", "1N4N1/8/8/8/8/8/8/1n4n1 w ---- - 1 0");
            put("onlyKnights4x4", "1N2/4/4/2n1 w ---- - 1 0");
            put("onlyBishops", "2F2F2/8/8/8/8/8/8/2f2f2 w ---- - 1 0");
            put("whiteDownOnlyKingsAndRooks", "R3K2R/8/8/8/8/8/8/r3k2r w KQkq - 1 0");
            put("whiteUpOnlyKingsAndRooks", "r2k3r/8/8/8/8/8/8/R2K3R w KQkq - 1 0");
            put("onlyTwoBishops6x6", "1F4/6/6/6/6/4f1 w ---- - 1 0");
            put("onlyTwoRooks6x6", "B5/6/6/6/6/5b w ---- - 1 0");
            put("onlyTwoQueens6x6", "2V3/6/6/6/6/3v2 w ---- - 1 0");
            put("onlyTwoKings4x4", "1K2/4/4/2k1 w ---- - 1 0");
            put("whiteDownPawnsFrontEmPassant", "8/8/8/PPPPPPPP/8/8/pppppppp/8 w ---- - 1 0");
            put("blackDownPawnsFrontEmPassant", "8/8/8/pppppppp/8/8/PPPPPPPP/8 w ---- - 1 0");
            put("whiteDownPawnsFront", "8/PPPPPPPP/8/8/8/8/pppppppp/8 w ---- - 1 0");
            put("blackDownPawnsFront", "8/pppppppp/8/8/8/8/PPPPPPPP/8 w ---- - 1 0");
            put("whiteDown2x3Pawn", "8/8/8/P3P3/8/8/3ppp2/8 w ---- - 1 0");
            put("whiteDownOneRookTwoKing4x4", "K3/1R2/4/k3 b ---- - 1 1");
            put("whiteDownOneRookTwoKing3x3", "K1R/3/1k1 w ---- - 1 0");
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

        public final static ArrayList<ImageIcon> WhitePieceChoiceInsteadOfPawnGotIn = new ArrayList<>(){{
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\w_huszar.png"));
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\w_futo.png"));
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\w_bastya.png"));
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\w_vezer.png"));
        }};

        public final static ArrayList<ImageIcon> BlackPieceChoiceInsteadOfPawnGotIn = new ArrayList<>(){{
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\b_huszar.png"));
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\b_futo.png"));
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\b_bastya.png"));
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\b_vezer.png"));
        }};

        //endregion

    }

    public static class MUTABLE {

        public static int MAX_WIDTH = 8;

        public static int MAX_HEIGHT = 8;

        public static int MINIMAX_DEPTH = 2;

        public static String starterFen = "whiteDownStarter";

        public static AiTree continuousTree = new AiTree(starterFen);

        public static AiTree storeTree = new AiTree();

        public static HashSet<String> alreadyEvaluatedFens = new HashSet<>();

        public static PieceSet whitePieceSet = new PieceSet(){{
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
        }};

        public static PieceSet blackPieceSet = new PieceSet(){{
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
            add(new Piece());
        }};

        public static int CLICK_COUNTER = 0;

        public static ViewField lastClicked;

        public static ViewPiece pieceToChange;

        public static boolean whiteToPlay;

        public static Boolean gameIsOn = true;

        public static boolean isFirstOpen = true;

        public static boolean theresOnlyOneAi = false;

        public static boolean whiteAiNeeded = false;

        public static boolean isTest = false;

        public static boolean playerTurn;

        public static boolean aiTurn;

        public static boolean whiteSmallCastleEnabled = true;

        public static boolean whiteBigCastleEnabled = true;

        public static boolean blackSmallCastleEnabled = true;

        public static boolean blackBigCastleEnabled = true;

        public static String emPassantChance = "-";

        public static int stepNumber = 1;

        public static int evenOrOddStep = 0;

        public static int labelCounter = 0;

        public static boolean canBeLogger;

    }

}
