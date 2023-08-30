package classes;

import classes.Game.I18N.ChessGameException;
import classes.Game.Model.Logic.EDT;

import javax.swing.*;

import static classes.GUI.Frame.Window.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
                    EDT edt = new EDT();
                    edt.start();
                }
        );
        
    }
}