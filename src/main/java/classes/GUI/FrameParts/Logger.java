package classes.GUI.FrameParts;

import classes.Game.Model.Structure.Move;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static classes.AI.BitBoards.BBVars.KING_SIDE;
import static classes.AI.BitBoards.BBVars.QUEEN_SIDE;
import static classes.AI.BitBoards.BitBoardMoves.getKingBoard;
import static classes.GUI.Frame.Window.*;
import static classes.Game.I18N.PieceType.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;

public class Logger extends JTextArea {


    public Logger() {
        initializeLogger();
    }

    private void initializeLogger() {
        setBounds((int) (LOGGER_START_X), (int) (LOGGER_START_Y), ((int) LOGGER_WIDTH), ((int) LOGGER_HEIGHT));
        setVisible(true);
        setEditable(false);
        initializeLogFile();
    }

    public static void initializeLogFile() {

        if (canBeLogger) {
            File logger = new File(LOG_FILE_PATH);
            try {
                if (logger.createNewFile()) {
                    System.out.println("\nLog file created.\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH))) {
                writer.write("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void logStep(Move move)  {
        if (canBeLogger){
            String step = " ";

            if (whiteToPlay){
                step += stepNumber + ". ";
            }else {
                step += stepNumber < 10 ? "    " : "     ";
                stepNumber++;
            }

            if (!move.isItIsCastle()){
                step += getProperPieceImage(move.getWhat().getType().toString(move.getWhat().isWhite()).charAt(0));
                step += " ";
                step += move.getFrom().toLoggerString();
                step += " - ";
                step += move.getTo().toLoggerString();
            }else {
                if (Math.abs(move.getPlusPiece().getSecond().getFirst().getJ() - move.getPlusPiece().getSecond().getSecond().getJ()) > 2){
                    step += "0-0-0";
                }else{
                    step += "0-0";
                }
            }

            step += '\n';

            if (move.isMustLogged())
                getLogger().log(step);
        }
    }

    public static void logAiStep(String move){
        getLogger().log(move);
    }

    public static String detectChessMove(String fen1, String fen2) {
        char[][] board1 = simpleFenParserToCharArray(fen1);
        char[][] board2 = simpleFenParserToCharArray(fen2);
        int fromRow = -1, fromCol = -1, toRow = -1, toCol = -1;
        char stepper = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board1[i][j] != board2[i][j]) {
                    if (board2[i][j] == ' '){
                        fromRow = i;
                        fromCol = j;
                    }else {
                        toRow = i;
                        toCol = j;
                        stepper = board2[i][j];
                    }
                }
            }
        }
        //Castle
        if ((stepper == 'K' || stepper == 'k') && Math.abs(fromCol - toCol) == 2){
            if ((getKingBoard(stepper == 'K') & KING_SIDE) != 0)
                return "0-0";
            if ((getKingBoard(stepper == 'K') & QUEEN_SIDE) != 0)
                return "0-0-0";
        }
        //Others
        return convertToChessNotation(fromRow, fromCol, stepper, true) +
                " - " +
                convertToChessNotation(toRow, toCol, (char) 0, false) +
                "\n";
    }

    private static char[][] simpleFenParserToCharArray(String fen) {
        char[][] board = new char[8][8];
        String[] parts = fen.split(" ");
        String[] rows = parts[0].split("/");
        for (int i = 0; i < 8; i++) {
            String row = rows[i];
            int col = 0;
            for (int j = 0; j < row.length(); j++) {
                char c = row.charAt(j);
                if (Character.isDigit(c)) {
                    int numEmpty = Character.getNumericValue(c);
                    for (int k = 0; k < numEmpty; k++) {
                        board[i][col++] = ' ';
                    }
                } else {
                    board[i][col++] = c;
                }
            }
        }
        return board;
    }

    private static String convertToChessNotation(int row, int col, char pieceHere, boolean firstLoc) {
        char r = nums.get(whiteDown ? row : 7 - row);
        char c = abc.get(whiteDown ? col : 7 - col);
        String step = firstLoc ? " " : "";
        step += firstLoc ? (!whiteToPlay ? (stepNumber + ". ") : (stepNumber < 10 ? "    " : "     ")) : "";
        step += pieceHere == 0 ? "" : getProperPieceImage(pieceHere);
        step += firstLoc ? " " : "";
        step += Character.toLowerCase(c);
        step += r;
        return step;
    }


    public void log(String message) {

        append(message);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        checkAndClearTextArea();
    }

    private void checkAndClearTextArea() {
        int lineCount = getLineCount();
        int maxLines = 20; // Változtasd meg a kívánt maximális sorok számára

        if (lineCount > maxLines) {
            // Ha elérte a maximális sorok számát, törölje a tartalmat
            setText("");
        }
    }
}
