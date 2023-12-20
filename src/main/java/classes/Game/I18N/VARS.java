package classes.Game.I18N;

import classes.Ai.Evaluation.GameState;
import classes.GUI.FrameParts.*;
import classes.Game.Model.Structure.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static classes.Ai.Evaluation.GameState.*;
import static classes.Game.I18N.METHODS.dateToString;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

public class VARS {

    public static class FINALS {

        public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        public static final int FIELD_WIDTH = 75;

        public static final int FIELD_HEIGHT = 75;

        public static final double SCREEN_HEIGHT = screenSize.getHeight();

        public static final double SCREEN_WIDTH = screenSize.getWidth();

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

        public static final double WHITE_TAKEN_PIECES_SECOND_ROW_START_X = BOARD_PLACE_START_X - 75;

        public static final double BLACK_TAKEN_PIECES_FIRST_ROW_START_X = BOARD_PLACE_END_X;

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


        public static final String LOG_FILE_PATH = "src\\main\\Saves\\log"+dateToString(new Date())+".txt";


        public static final String WHITE_STRING = "WHITE";

        public static final String BLACK_STRING = "BLACK";

        public final static Color WHITE = new Color(255,228,196);

        public final static Color DARK_WHITE = new Color(85,85,85);

        public final static Color BLACK = new Color(131, 95, 33);

        public final static Color DARK_BLACK = new Color(77, 60, 22);

        public final static Color BACK_GROUND = new Color(33, 3, 8, 205);

        public static final Double[][] PAWN_BASE_VALUE_MATRIX_WP = new Double[][] {
                {1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 0.75, 1.65, 1.65, 0.75, 1.65, 1.65},
                {1.65, 1.65, 1.65, 2.00, 2.00, 1.65, 1.65, 1.65},
                {1.65, 1.65, 2.00, 2.00, 2.00, 2.00, 1.65, 1.65},
                {2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00},
                {3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00},
                {3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00}
        };

        public static final Double[][] PAWN_BASE_VALUE_MATRIX_BP = new Double[][]{
                {3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00},
                {3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00},
                {2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00},
                {1.65, 1.65, 2.00, 2.00, 2.00, 2.00, 1.65, 1.65},
                {1.65, 1.65, 1.65, 2.00, 2.00, 1.65, 1.65, 1.65},
                {1.65, 1.65, 0.75, 1.65, 1.65, 0.75, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65}
        };

        private static final Double[][] KNIGHT_BASE_VALUE_MATRIX = new Double[][] {
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {1.65, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 1.65},
                {1.65, 2.00, 3.00, 3.00, 3.00, 3.00, 2.00, 1.65},
                {1.65, 2.00, 3.00, 3.00, 3.00, 3.00, 2.00, 1.65},
                {1.65, 2.00, 3.00, 3.00, 3.00, 3.00, 2.00, 1.65},
                {1.65, 2.00, 3.00, 3.00, 3.00, 3.00, 2.00, 1.65},
                {1.65, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 1.65},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75}
        };


        public static final Double[][] BISHOP_BASE_VALUE_MATRIX_WP = new Double[][] {
                {2.00, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 2.00},
                {1.65, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 1.65},
                {1.65, 3.00, 2.00, 2.00, 2.00, 2.00, 3.00, 1.65},
                {1.65, 2.00, 3.00, 3.00, 3.00, 3.00, 2.00, 1.65},
                {1.65, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 1.65},
                {1.65, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 1.65},
                {1.65, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 1.65},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75}
        };

        public static final Double[][] BISHOP_BASE_VALUE_MATRIX_BP = new Double[][]{
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {1.65, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 1.65},
                {1.65, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 1.65},
                {1.65, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 1.65},
                {1.65, 2.00, 3.00, 3.00, 3.00, 3.00, 2.00, 1.65},
                {1.65, 3.00, 2.00, 2.00, 2.00, 2.00, 3.00, 1.65},
                {1.65, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 1.65},
                {2.00, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 2.00}
        };

        public static final Double[][] ROOK_BASE_VALUE_MATRIX_WP = new Double[][] {
                {1.65, 1.65, 1.65, 3.00, 3.00, 3.00, 1.65, 1.65},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {1.65, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 1.65},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75}
        };

        public static final Double[][] ROOK_BASE_VALUE_MATRIX_BP = new Double[][] {
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {1.65, 2.00, 2.00, 2.00, 2.00, 2.00, 2.00, 1.65},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {1.65, 1.65, 1.65, 3.00, 3.00, 3.00, 1.65, 1.65}
        };

        public static final Double[][] QUEEN_BASE_VALUE_MATRIX_WD_WP = new Double[][] {
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {1.65, 2.00, 3.00, 3.00, 3.00, 2.00, 2.00, 1.65},
                {1.65, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 1.65},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {1.65, 1.65, 1.65, 2.00, 2.00, 1.65, 1.65, 1.65}
        };

        public static final Double[][] QUEEN_BASE_VALUE_MATRIX_WD_BP = new Double[][] {
                {1.65, 1.65, 1.65, 2.00, 2.00, 1.65, 1.65, 1.65},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {1.65, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 1.65},
                {1.65, 2.00, 3.00, 3.00, 3.00, 2.00, 2.00, 1.65},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75}
        };

        public static final Double[][] QUEEN_BASE_VALUE_MATRIX_BD_WP = new Double[][] {
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75},
                {1.65, 2.00, 2.00, 3.00, 3.00, 3.00, 2.00, 1.65},
                {1.65, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 1.65},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {1.65, 1.65, 1.65, 2.00, 2.00, 1.65, 1.65, 1.65}
        };

        public static final Double[][] QUEEN_BASE_VALUE_MATRIX_BD_BP = new Double[][] {
                {1.65, 1.65, 1.65, 2.00, 2.00, 1.65, 1.65, 1.65},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {2.00, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 2.00},
                {1.65, 3.00, 3.00, 3.00, 3.00, 3.00, 3.00, 1.65},
                {1.65, 2.00, 2.00, 3.00, 3.00, 3.00, 2.00, 1.65},
                {0.75, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 0.75}
        };

        public static final Double[][] KING_BASE_VALUE_MATRIX_WD_WP = new Double[][] {
                {2.00, 3.00, 3.00, 1.65, 1.65, 1.65, 3.00, 2.00},
                {2.00, 2.00, 1.65, 1.65, 1.65, 1.65, 2.00, 2.00},
                {1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65}
        };

        public static final Double[][] KING_BASE_VALUE_MATRIX_WD_BP = new Double[][] {
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65},
                {2.00, 2.00, 1.65, 1.65, 1.65, 1.65, 2.00, 2.00},
                {2.00, 3.00, 3.00, 1.65, 1.65, 1.65, 3.00, 2.00}
        };

        public static final Double[][] KING_BASE_VALUE_MATRIX_BD_WP = new Double[][] {
                {2.00, 3.00, 1.65, 1.65, 1.65, 3.00, 3.00, 2.00},
                {2.00, 2.00, 1.65, 1.65, 1.65, 1.65, 2.00, 2.00},
                {1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65}
        };

        public static final Double[][] KING_BASE_VALUE_MATRIX_BD_BP = new Double[][] {
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 0.75, 0.75, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65},
                {1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65, 1.65},
                {2.00, 2.00, 1.65, 1.65, 1.65, 1.65, 2.00, 2.00},
                {2.00, 3.00, 1.65, 1.65, 1.65, 3.00, 3.00, 2.00}
        };
        
        public static final Map<PieceType, Double[][]> FIELD_BASE_VALUES_BY_PIECE_TYPE = new HashMap<>(){{
            put(P, PAWN_BASE_VALUE_MATRIX_WP);
            put(N, KNIGHT_BASE_VALUE_MATRIX);
            put(B, BISHOP_BASE_VALUE_MATRIX_WP);
            put(R, ROOK_BASE_VALUE_MATRIX_WP);
            put(Q, QUEEN_BASE_VALUE_MATRIX_WD_WP);
            put(K, KING_BASE_VALUE_MATRIX_WD_WP);
        }};

        public static final double WHITE_GOT_CHECKMATE = -10000;

        public static final double BLACK_GOT_CHECKMATE = 10000;

        public static final double DRAW = 0.005;

        public static final double DRAW_OFFER = 0.001;

        public static final double BLACK_SUBMITTED = 5000;

        public static final double WHITE_SUBMITTED = -5000;

        public static final ArrayList<Double> GAME_OVER_CASES = new ArrayList<>(){{
            add(WHITE_GOT_CHECKMATE);
            add(BLACK_GOT_CHECKMATE);
            add(DRAW);
            add(WHITE_SUBMITTED);
            add(BLACK_SUBMITTED);
        }};

        public static final double PAWN_BASE_VALUE = 1;

        public static final double KNIGHT_OR_BISHOP_BASE_VALUE = 3;

        public static final double ROOK_BASE_VALUE = 5;

        public static final double QUEEN_BASE_VALUE = 9;

        public static final double KING_BASE_VALUE = 200;

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
            put(P, pawnMatrix);
            put(N, knightMatrix);
            put(B, bishopMatrix);
            put(R, rookMatrix);
            put(Q, qkMatrix);
            put(K, qkMatrix);
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

        public static final Map<Character, Character> englishToHungarianPieceLetters = new HashMap<>(){{
            put('P', 'G');
            put('p', 'g');
            put('N', 'H');
            put('n', 'h');
            put('B', 'F');
            put('b', 'f');
            put('R', 'B');
            put('r', 'b');
            put('Q', 'V');
            put('q', 'v');
        }};

        public static Map<String, String> usualFens = new HashMap<>(){{
            put("whiteDownStarter", "RNBQKBNR/PPPPPPPP/8/8/8/8/pppppppp/rnbqkbnr w KQkq - 1 0");
            put("blackDownStarter", "rnbkqbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKQBNR w KQkq - 1 0");
        }};

        public static Map<String, String> testFens = new HashMap<>(){{

            //region Simple Piece Tests

            put("whiteDownWithOutPawns", "RNBQKBNR/8/8/8/8/8/8/rnbqkbnr w KQkq - 1 0");
            put("onlyTwoKnights8x8", "1NN5/8/8/8/8/8/8/1n4n1 w ---- - 1 0");
            put("onlyTwoKings8x8", "1K6/8/8/8/8/8/8/6k1 w ---- - 1 0");
            put("onlyKnights4x4", "1N2/4/4/2n1 w ---- - 1 0");
            put("onlyBishops", "2B2B2/8/8/8/8/8/8/2b2b2 w ---- - 1 0");
            put("onlyRooks", "R6R/8/8/8/8/8/8/r6r w ---- - 1 0");
            put("onlyTwoBishops6x6", "1F4/6/6/6/6/4f1 w ---- - 1 0");
            put("onlyTwoRooks6x6", "B5/6/6/6/6/5b w ---- - 1 0");
            put("onlyTwoQueens6x6", "2V3/6/6/6/6/3v2 w ---- - 1 0");
            put("onlyTwoQueensHitTest6x6", "2V3/6/6/6/6/2v3 w ---- - 1 0");
            put("onlyTwoKings4x4", "1K2/4/4/2k1 w ---- - 1 0");

            //endregion


            //region Special Case Tests

            //region EmPassant Case

            put("whiteDownWhitePawnsFrontEmPassant", "8/8/8/PPPPPPP1/6pP/8/pppppp1p/8 w ---- - 1 0");
            put("whiteDownBlackPawnsFrontEmPassant", "8/PPPPPPPP/8/8/pppppppp/8/8/8 w ---- - 1 0");
            put("blackDownBlackPawnsFrontEmPassant", "8/8/8/pppppppp/8/8/PPPPPPPP/8 w ---- - 1 0");
            put("blackDownWhitePawnsFrontEmPassant", "8/pppppppp/8/8/PPPPPPPP/8/8/8 w ---- - 1 0");
            put("whiteDown2And2Pawn", "8/8/8/P3P3/8/8/3p1p2/8 w ---- - 1 0");
            put("blackDown2And2Pawn", "8/8/8/4p3/8/8/3P1P2/8 b ---- - 1 1");
            put("whiteDown1And2Pawn", "8/8/8/4P3/8/8/3p1p2/8 w ---- - 1 0");
            put("blackDownSneakyEmPassant", "8/5p2/4p3/r5PK/k7/8/8/8 b ---- - 1 1");

            //endregion

            //region Castle Case

            put("whiteDownOnlyKingsAndRooks", "R3K2R/8/8/8/8/8/8/r3k2r w KQkq - 1 0");
            put("whiteUpOnlyKingsAndRooks", "r2k3r/8/8/8/8/8/8/R2K3R w KQkq - 1 0");
            put("whiteDownEachCanCastleBothSides", "R3K2R/8/8/8/8/8/8/r3k2r w KQkq - 1 0");
            put("whiteDownBlackCanCastleOneSide", "R3K3/8/8/8/8/8/8/4k2r w -Qk- - 1 0");
            put("whiteDownBlackCanCastleNoSide", "R3KR2/8/8/8/8/8/8/4k2r b -Qk- - 1 1");
            put("whiteDownBlackCanCastleNoSideBecauseItIsInCheck", "R3K3/4R3/8/8/8/8/8/4k2r b -Qk- - 1 1");

            //endregion

            //region Pawn Promotion

            put("whiteDownOnePawnInTheEdgeOfPawnPromotion", "2K5/8/8/8/7k/8/1P6/8 w ---- - 1 0");
            put("whiteDownOneOnePawn", "2K5/p7/8/8/7k/8/1P6/8 w ---- - 1 0");

            //endregion

            //region Binding

            put("whiteDownBindingTestWithQueen", "qQK5/8/8/8/7k/8/1P6/8 w ---- - 1 0");
            put("whiteDownBindingTestWithKnight", "qNK5/8/8/8/7k/8/8/8 w ---- - 1 0");
            put("whiteDownRookInBindingAfterCastle", "2KR3r/8/8/8/8/8/8/r3k3 w KQkq - 1 0");

            //endregion

            //region Check

            put("whiteDownCheckTestCheckWithQueenPiecesAroundEnemyKing", "K7/Q5rr/7k/6nn/8/8/8/8 w ---- - 1 0");
            put("whiteDownOutOfMemoryAfterCheck1", "R1B1KR2/3PPPn1/N1P2NP1/1P4pP/2pB1p1p/4p1qb/p2p4/r1b1k1nr w -Qkq - 1 0");
            put("whiteDownOutOfMemoryAfterCheck2", "2BK1BR1/3Q4/N1n3q1/3ppNpP/2b1n3/R6r/ppk2p1b/6r1 w ---- - 1 0");
            put("problemWithCheck", "R3KB1B/PPPB1PPP/5N2/3P4/1NQ4p/1k4p1/pppp4/rnbq1bnb b ---- - 1 1");
            put("bitBoardAiStaysInCheck", "RN2KB1R/PPP2PPP/5N2/3PP3/8/7Q/pppppp1p/rnb1qkr1 b ---- - 7 1");

            //endregion

            //region CheckMate

            put("whiteDownCheckMateInOneTwoKingOneRookOneStep3x3", "1k1/3/K1R b ---- - 1 1");
            put("whiteDownCheckMateInOneTwoKingOneRookOneStep3x3WhiteStarts", "k2/3/K1R w ---- - 1 0");
            put("whiteDownCheckMateInOneTwoKingOneRookTwoStep4x4", "k3/4/K3/2R1 b ---- - 1 1");
            put("whiteDownCheckMateInOneTwoKingOneRookSomeSteps4x4", "k3/4/4/K2R b ---- - 1 1");
            put("whiteDownCheckMateInOneTwoKingOneQueenSteps5x5", "k4/5/5/5/K3Q b ---- - 1 1");
            put("whiteGotCheckMate", "5R1K/1Pr3qP/1B2p3/6n1/5P2/1Q5p/3R1pp1/4r1k1 w ---- - 1 0");
            put("blackGotCheckMateWrong", "RNB1K2R/PPP2PPP/8/2BPP3/8/5n1N/pppppQ1p/rnb2kr1 b KQ-- - 10 1");

            //endregion

            //region Draw



            //endregion

            //region Others

            put("whiteDownOneRookTwoKing4x4", "K3/1R2/4/k3 b ---- - 1 1");
            put("whiteDownOneRookTwoKing3x3", "K1R/3/1k1 w ---- - 1 0");
            put("polgarVsKasparov", "3R1R1K/1Pr3PP/4p3/q5n1/3B1P2/1Q5p/5pp1/4r1k1 w ---- - 1 0");
            put("whiteDownKingTakesOwnKnight", "1RB1KBR1/P4P1P/N3P1P1/3PpN2/1pPp4/5nQ1/pp2kpp1/r1bqnbr1 b ---- - 1 1");
            put("blackMustTakeRookButItIsnt", "3R1R1K/1Pq2nPP/4p3/8/1Q3P2/7p/5pp1/4r1k1 w ---- - 1 0");
            put("polgarVsKasparov25", "2r1r2k/1bqnbpp1/pp1p1n1p/4pP2/P3P2B/2N2B2/1PPN2PP/3RRQ1K b ---- - 5 1");
            put("4mpOn6depth", "8/8/8/8/K1P4r/8/3p4/3k4 b ---- - 1 1");
            put("test1WhiteDown", "8/8/8/5K2/3P2R1/2b5/1p2k3/8 b ---- - 1 1");
            put("test2WhiteDown", "8/8/8/8/3K4/2P5/1b6/3k4 b ---- - 1 1");

            //endregion

            //endregion

        }};

        //region PieceViews

        public static final ArrayList<ViewPiece> DICT_FOR_VIEW_PIECE = new ArrayList<>(){{

            //Világos

            add(new ViewPiece("src\\main\\resources\\Figura_Images\\w_pawn.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\w_night.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\w_bishop.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\w_rook.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\w_queen.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\w_king.png"));

            //Sötét

            add(new ViewPiece("src\\main\\resources\\Figura_Images\\b_pawn.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\b_night.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\b_bishop.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\b_rook.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\b_queen.png"));
            add(new ViewPiece("src\\main\\resources\\Figura_Images\\b_king.png"));

        }};

        public final static ArrayList<ImageIcon> WhitePieceChoiceInsteadOfPawnGotIn = new ArrayList<>(){{
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\w_night.png"));
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\w_bishop.png"));
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\w_rook.png"));
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\w_queen.png"));
        }};

        public final static ArrayList<ImageIcon> BlackPieceChoiceInsteadOfPawnGotIn = new ArrayList<>(){{
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\b_night.png"));
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\b_bishop.png"));
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\b_rook.png"));
            add(new ImageIcon("src\\main\\resources\\Figura_Images\\b_queen.png"));
        }};

        //endregion

        //First white ones after black ones
        public static final String[] pieceImagesForLog = new String[]{"♙", "♘", "♗", "♖", "♕", "♔", "♟︎", "♞", "♝", "♜", "♛", "♚"};

    }

    public static class MUTABLE {

        public static int MAX_WIDTH = 8;

        public static int MAX_HEIGHT = 8;

        public static int MINIMAX_DEPTH = 6;

        public static GameState currentGameState = MIDDLE_GAME;

        public static int nodeNum = 0;

        public static int transPosNum = 0;

        public static HashMap<String, Integer> happenedList = new HashMap<>();

        public static boolean thirdSimilarPositionOfTheGame = false;

        public static PieceSet whitePieceSet = new PieceSet(){{
            add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece()); add(new Piece());
        }};

        public static PieceSet blackPieceSet = new PieceSet(){{
            add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());add(new Piece());
        }};

        public static int CLICK_COUNTER = 0;

        public static ViewField lastClicked;

        public static ViewPiece pieceToChange;

        public static boolean whiteToPlay;

        public static boolean isFirstOpen = true;

        public static boolean theresOnlyOneAi = false;

        public static boolean whiteAiNeeded = false;

        public static boolean isTest = false;

        public static boolean whiteDown = true;

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

        public static final AtomicBoolean pauseFlag = new AtomicBoolean(false);

        public static AtomicBoolean gameEndFlag = new AtomicBoolean(false);

    }

}
