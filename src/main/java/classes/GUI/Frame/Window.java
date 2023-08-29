package classes.GUI.Frame;

import classes.Ai.AI;
import classes.GUI.FrameParts.GameBoard;
import classes.GUI.FrameParts.ViewField;
import classes.GUI.FrameParts.ViewPiece;
import classes.Game.I18N.ChessGameException;
import lombok.*;

import javax.swing.*;

import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTUABLES.*;

@Getter
@Setter
public class Window extends JFrame {

    //region Fields

    private AI ai;

    private static Window window;

    private GameBoard gameBoard;

    //endregion


    //region Constructor

    private Window() throws ChessGameException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(screenSize);
        setTitle("Sakk Dolgozat");
        setResizable(false);
        setLocationRelativeTo(null);
        getViewBoard().pieceSetUp("RNBQKBNR/PPPPPPPP/8/8/8/8/pppppppp/rnbqkbnr");
//        fieldNums(getViewBoard().getFields());

        gameBoard = new GameBoard(getViewBoard());

        add(gameBoard);
        ai = new AI("BLACK");

        setVisible(true);
    }

    public static Window getWindow() throws ChessGameException {
        if (window == null){
            window = new Window();
        }
        return window;
    }

    //endregion


    //region Methods


    //endregion

}
