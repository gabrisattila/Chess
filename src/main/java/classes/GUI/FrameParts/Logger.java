package classes.GUI.FrameParts;

import classes.Game.Model.Structure.Move;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static classes.GUI.Frame.Window.*;
import static classes.Game.I18N.METHODS.*;
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

    private void initializeLogFile() {

        File logger = new File(LOG_FILE_PATH);
        try {
            if (logger.createNewFile()){
                System.out.println("Log file created.");
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

    public static String logStep(Move move)  {
        if (canBeLogger){
            String step = " ";

            if (whiteToPlay){
                step += stepNumber + ". ";
            }else {
                step += " - ";
                stepNumber++;
            }

            if (!move.isItIsCastle()){
                step += move.getWhat().getType().toString().charAt(0);
                if (notNull(move.getPlusPiece())) {
                    step += 'x';
                }
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
            return step;
        }
        return "";
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
