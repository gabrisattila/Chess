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
        addMouseListener(new ChessButtonMouseListener());
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

        @Override
        public void mouseEntered(MouseEvent e){
            swapColors(e);
        }


        @Override
        public void mouseExited(MouseEvent e){
            swapColors(e);
        }

        private void swapColors(MouseEvent e) {
            Color tmp = ((ChessGameButton) e.getSource()).getForeground();
            ((ChessGameButton) e.getSource()).setForeground(((ChessGameButton) e.getSource()).getBackground());
            ((ChessGameButton) e.getSource()).setBackground(tmp);
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
            JDialog newGameDialog = new JDialog();
            newGameDialog.setTitle("Új játék kiválasztása");
            newGameDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            newGameDialog.setLayout(new GridLayout(2, 2));

            ChessGameButton blackAi = new ChessGameButton("<html><div style='text-align: center;'>Világossal<br>szeretnék lenni</div></html>");
            ChessGameButton whiteAi = new ChessGameButton("<html><div style='text-align: center;'>Sötéttel<br>szeretnék lenni</div></html>");
            ChessGameButton aiVsAi = new ChessGameButton("<html><div style='text-align: center;'>Ai vs Ai</div></html>");
            ChessGameButton test = new ChessGameButton("<html><div style='text-align: center;'>Teszt</div></html>");

            blackAi.setBorder(BorderFactory.createLineBorder(BLACK, 13));
            whiteAi.setBorder(BorderFactory.createLineBorder(BLACK, 13));
            aiVsAi.setBorder(BorderFactory.createLineBorder(BLACK, 13));
            test.setBorder(BorderFactory.createLineBorder(BLACK, 13));

            blackAi.setBorderPainted(true);
            whiteAi.setBorderPainted(true);
            aiVsAi.setBorderPainted(true);
            test.setBorderPainted(true);

            blackAi.addActionListener(e -> {
                try {
                    newGameBlackAiClicked();
                } catch (ChessGameException ex) {
                    throw new RuntimeException(ex);
                }
                newGameDialog.dispose();
            });

            whiteAi.addActionListener(e -> {
                newGameWhiteAiClicked();
                newGameDialog.dispose();
            });

            aiVsAi.addActionListener(e -> {
                newGameAiVsAiClicked();
                newGameDialog.dispose();
            });

            test.addActionListener(e -> {
                newGameTestClicked();
                newGameDialog.dispose();
            });

            newGameDialog.add(blackAi);
            newGameDialog.add(whiteAi);
            newGameDialog.add(aiVsAi);
            newGameDialog.add(test);

            newGameDialog.setBounds((int) NEW_GAME_WINDOW_START_X, (int) NEW_GAME_WINDOW_START_Y, (int) NEW_GAME_WINDOW_WIDTH, (int) NEW_GAME_WINDOW_HEIGHT); // középre pozícionálás
            newGameDialog.setModal(true); // modális beállítás

            newGameDialog.setVisible(true);
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
