package classes.GUI.FrameParts;


import classes.GUI.Frame.Window;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Objects;

import static classes.Model.I18N.METHODS.*;
import static classes.Model.I18N.VARS.FINALS.*;


public class LoggerTest  {

    @Test
    public void testLogFilesCheck() throws IOException {
        Window.getWindow();
        File directory = new File(SAVES_DIRECTORY_PATH);
        File[] files = directory.listFiles();
        assert files != null;
        for (File f : files) {
            if ("log".equals(f.getName().substring(0, 3))){
                BufferedReader reader = new BufferedReader(new FileReader(SAVES_DIRECTORY_PATH + f.getName()));
                String line = reader.readLine();
                int pieceIndex;
                while (notNull(line)){
                    line = line.trim();
                    pieceIndex = 0;
                    for (int i = 0; i < line.length(); i++) {
                        for (String s : pieceImagesForLog) {
                            if (line.charAt(0) == s.charAt(0)) {
                                pieceIndex = i;
                                break;
                            }
                        }
                        if (pieceIndex != 0) {
                            if (line.charAt(i) != ' ' && line.charAt(i) != '.') {
                                if (i != pieceIndex){
                                    Assert.assertTrue(
                                            nums.contains(line.charAt(i)) ||
                                                    abc.contains(line.charAt(i))
                                    );
                                }
                            }
                        }
                    }
                    line = reader.readLine();
                }
            }
        }
    }

}