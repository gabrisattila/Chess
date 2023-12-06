package classes;

import classes.Ai.FenConverter;
import classes.Game.I18N.ChessGameException;
import classes.Game.Model.Logic.EDT;

import javax.swing.*;

import static classes.Game.I18N.METHODS.exceptionIgnorer;
import static classes.Game.I18N.VARS.FINALS.usualFens;

public class Main {

    public static void main(String[] args) {
//           exceptionIgnorer();
        new EDT();

    }
}