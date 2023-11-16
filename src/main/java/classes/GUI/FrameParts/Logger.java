package classes.GUI.FrameParts;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static classes.Game.I18N.METHODS.*;
import static classes.Game.I18N.VARS.FINALS.*;

public class Logger extends JTextArea {


    public Logger() {
        initializeLogger();
    }

    private void initializeLogger() {
        setBounds((int) (LOGGER_START_X), (int) (LOGGER_START_Y), ((int) LOGGER_WIDTH), ((int) LOGGER_HEIGHT));
        setAlignmentX(JComponent.CENTER_ALIGNMENT);
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
