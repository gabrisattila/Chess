package classes;

import classes.Game.I18N.ChessGameException;
import classes.Game.Model.Logic.EDT;

import javax.swing.*;

public class Main {

    public static EDT edt;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                edt = new EDT();
            } catch (ChessGameException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            edt.start();
        });
    }
}