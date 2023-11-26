package classes;

import classes.Game.I18N.ChessGameException;
import classes.Game.Model.Logic.EDT;

import javax.swing.*;

import static classes.Game.I18N.METHODS.exceptionIgnorer;

public class Main {

    public static void main(String[] args) {
        try {
//            exceptionIgnorer();
            new EDT();
        } catch (ChessGameException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}