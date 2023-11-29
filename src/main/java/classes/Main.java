package classes;

import classes.Game.I18N.ChessGameException;
import classes.Game.Model.Logic.EDT;

import javax.swing.*;

import static classes.Game.I18N.METHODS.exceptionIgnorer;

public class Main {

    public static void main(String[] args) {
//           exceptionIgnorer();
        new EDT();
    }
}