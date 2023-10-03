package classes;

import classes.Game.I18N.ChessGameException;
import classes.Game.Model.Logic.EDT;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            new EDT();
        } catch (ChessGameException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}