package classes.GUI.FrameParts;

import classes.Game.I18N.ChessGameException;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.*;

import static classes.GUI.Frame.Window.getWindow;
import static classes.Game.I18N.VARS.FINALS.*;

public class ChessGameButton extends JButton {

    //region Fields



    //endregion


    //region Constructor

    public ChessGameButton(String text){
        setText(text);
        buttonStyleSetting();
    }

    public ChessGameButton(){
        buttonStyleSetting();
    }

    //endregion


    //region Methods

    private void buttonStyleSetting() {
        setBackground(WHITE);
        setForeground(BLACK);
        setVerticalAlignment(CENTER);
        setHorizontalAlignment(CENTER);
        setFont(new Font("Source Code Pro", Font.BOLD, 20));
        setOpaque(true);
        setFocusable(false);
        setBorderPainted(false);
    }


    @Getter
    @Setter
    public static class ChessButtonMouseListener extends MouseAdapter{

        @Override
        public void mouseClicked(MouseEvent e){
            try {
                manageClick(e);
            } catch (ChessGameException ex) {
                throw new RuntimeException(ex);
            }
        }

        private void manageClick(MouseEvent e) throws ChessGameException {
            switch (((ChessGameButton)e.getSource()).getText()){
                case "Új játék":{
                    newGameClicked();
                    break;
                }
                case "Világossal szeretnék lenni":{
                    newGameBlackAiClicked();
                    break;
                }
                case "Sötéttel szeretnék lenni":{
                    newGameWhiteAiClicked();
                    break;
                }
                case "Ai vs Ai":{
                    newGameAiVsAiClicked();
                    break;
                }
                case "Test":{
                    newGameTestClicked();
                    break;
                }
                case "Szünet":{
                    pauseClicked();
                    break;
                }
                case "Mentés":{
                    saveClicked();
                    break;
                }
                case "Betöltés":{
                    loadClicked();
                    break;
                }
                case "Feladás":{
                    submissionClicked();
                    break;
                }
                case "Döntetlen":{
                    drawClicked();
                    break;
                }
            }
        }

        private void newGameClicked() {
            JFrame newGameFrame = new JFrame("Új játék kiválasztása");
            newGameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            newGameFrame.setLayout(new GridLayout(2, 2));

            ChessGameButton blackAi = new ChessGameButton("Világossal szeretnék lenni");
            ChessGameButton whiteAi = new ChessGameButton("Sötéttel szeretnék lenni");
            ChessGameButton aiVsAi = new ChessGameButton("Ai vs Ai");
            ChessGameButton test = new ChessGameButton("Teszt");

            blackAi.addActionListener(e -> {
                try {
                    newGameBlackAiClicked();
                } catch (ChessGameException ex) {
                    throw new RuntimeException(ex);
                }
                newGameFrame.dispose();
            });

            whiteAi.addActionListener(e -> {
                newGameWhiteAiClicked();
                newGameFrame.dispose();
            });

            aiVsAi.addActionListener(e -> {
                newGameAiVsAiClicked();
                newGameFrame.dispose();
            });

            test.addActionListener(e -> {
                newGameTestClicked();
                newGameFrame.dispose();
            });

            newGameFrame.add(blackAi);
            newGameFrame.add(whiteAi);
            newGameFrame.add(aiVsAi);
            newGameFrame.add(test);

            newGameFrame.pack();
            newGameFrame.setVisible(true);
        }

        private void newGameBlackAiClicked() throws ChessGameException {
            getWindow();
        }

        private void newGameWhiteAiClicked() {

        }

        private void newGameAiVsAiClicked() {

        }

        private void newGameTestClicked() {

        }

        private void pauseClicked() {

        }

        private void saveClicked() {

        }

        private void loadClicked() {

        }

        private void submissionClicked() {

        }

        private void drawClicked() {

        }

    }

    //endregion

}
