package classes.GUI.Frame;

import classes.GUI.FrameParts.ChessButton;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static classes.GUI.Frame.Window.*;

public class WindowTest {

    @Test
    public void testButtonsEnabled() {
        buttons();
        ArrayList<String> buttonTextList = new ArrayList<>(){{
            add("Új játék"); add("Szünet"); add("Mentés"); add("Betöltés"); add("Feladás"); add("Döntetlen");
        }};
        Random random = new Random();
        String whichIsEnabled = buttonTextList.get(random.nextInt(0, buttonTextList.size()));
        buttonsEnabled(new ArrayList<>(){{ add(whichIsEnabled); }});
        for (ChessButton button : Window.getButtons()) {
            Assert.assertEquals(button.getText().equals(whichIsEnabled), button.isEnabled());
        }
    }

    @Test
    public void testSetUpFrame() {
        Window window = getWindow();
        Assert.assertNotNull(window);
        Assert.assertNotNull(window.getGameBoard());
        Assert.assertNotNull(window.getContentPane());
        Assert.assertNotNull(Window.getButtons());
        Assert.assertNotNull(Window.getTakenPiecePlaces());
    }
}