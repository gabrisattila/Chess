package classes.GUI.FrameParts;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static classes.Game.I18N.VARS.FINALS.*;

public class Logger extends JTextArea {

    //TODO Megoldani, hogy log+Date legyen a file neve
    private static final String LOG_FILE_PATH = "src\\main\\java\\Saved_Games\\log.txt";


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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH))) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
