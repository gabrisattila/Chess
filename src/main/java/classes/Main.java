package classes;

import classes.Game.I18N.ChessGameException;

import javax.swing.*;

import static classes.GUI.Frame.Window.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
                    try {
                        getWindow();
                    } catch (ChessGameException e) {
                        throw new RuntimeException(e.getMsg());
                    }
                }
        );
        
    }
}