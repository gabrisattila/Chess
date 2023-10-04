package classes.GUI.FrameParts;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class ViewFieldUI extends BasicButtonUI {
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        Color origColor = g.getColor();

        if (model.isEnabled()) {
            g.setColor(b.getForeground());
        } else {
            g.setColor(b.getForeground()); // Inaktív gomb szöveg színe
        }

        super.paint(g, c);

        g.setColor(origColor);
    }

}
