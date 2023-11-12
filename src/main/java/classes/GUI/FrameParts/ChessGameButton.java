package classes.GUI.FrameParts;

import classes.GUI.Frame.Window;
import classes.Game.I18N.ChessGameException;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import static classes.GUI.Frame.Window.*;
import static classes.GUI.FrameParts.GameBoard.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Logic.EDT.*;

public class ChessGameButton extends JButton {

    //region Fields

    private static final JDialog pauseDialog = new JDialog();

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
            } catch (ChessGameException | InterruptedException ex) {
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

        private void manageClick(MouseEvent e) throws ChessGameException, InterruptedException {
            switch (((ChessGameButton) e.getSource()).getText()) {
                case "Új játék" -> newGameClicked();
                case "Világossal szeretnék lenni" -> newGameBlackAiClicked();
                case "Sötéttel szeretnék lenni" -> newGameWhiteAiClicked();
                case "Ai vs Ai" -> newGameAiVsAiClicked();
                case "Test" -> newGameTestClicked();
                case "Szünet" -> pauseClicked();
                case "Mentés" -> saveClicked();
                case "Betöltés" -> loadClicked();
                case "Feladás" -> submissionClicked();
                case "Döntetlen" -> drawClicked();
                case "Folytatás" -> continueClicked();
                case "Kilépés" -> exitClicked(getWindow());
            }
        }

        private void newGameClicked() {
            isFirstOpen = false;
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
                } catch (ChessGameException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                newGameDialog.dispose();
            });

            whiteAi.addActionListener(e -> {
                try {
                    newGameWhiteAiClicked();
                } catch (ChessGameException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                newGameDialog.dispose();
            });

            aiVsAi.addActionListener(e -> {
                try {
                    newGameAiVsAiClicked();
                } catch (ChessGameException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                newGameDialog.dispose();
            });

            test.addActionListener(e -> {
                try {
                    newGameTestClicked();
                } catch (ChessGameException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
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

        private void newGameBlackAiClicked() throws ChessGameException, InterruptedException {
            newGameInitialization(true, false, false);
        }

        private void newGameWhiteAiClicked() throws ChessGameException, InterruptedException {
            newGameInitialization(true, true, false);
        }

        private void newGameAiVsAiClicked() throws ChessGameException, InterruptedException {
            newGameInitialization(false, false, false);
        }

        private void newGameTestClicked() throws ChessGameException, InterruptedException {
            newGameInitialization(false, false, true);
        }

        private void pauseClicked() {

            pauseDialog.setTitle("Szeretné folytatni?");
            pauseDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            pauseDialog.setLayout(new FlowLayout());

            ChessGameButton continueButton = new ChessGameButton("Folytatás");
            ChessGameButton exitButton = new ChessGameButton("Kilépés");

            if (theresOnlyOneAi) {
                interruptAi(whiteToPlay);
            }else {
                interruptAi(true);
                interruptAi(false);
            }

            pauseDialog.add(continueButton);
            pauseDialog.add(exitButton);
            pauseDialog.getContentPane().setBackground(BACK_GROUND);

            pauseDialog.pack();
            pauseDialog.setModal(true);
            pauseDialog.setLocationRelativeTo(null); // A képernyő közepére helyezi az ablakot
            pauseDialog.setVisible(true);
        }

        private void saveClicked() {

        }

        private void loadClicked() {

        }

        private void submissionClicked() {

        }

        private void drawClicked() {

        }

        private void continueClicked(){
            pauseDialog.dispose();
            if (whiteToPlay){
                if (!theresOnlyOneAi || whiteAiNeeded)
                    startAI();
            } else {
                if (!whiteAiNeeded)
                    startAI();
            }
        }

        private void exitClicked(Window window){
            pauseDialog.dispose();
            window.dispose();
        }

        private void newGameInitialization(boolean oneAi, boolean whiteAi, boolean test) throws ChessGameException, InterruptedException {
            theresOnlyOneAi = oneAi;
            whiteAiNeeded = whiteAi;
            isTest = test;
            setUpSides();
            buttonsEnabled();
            initialization();
            labelTexting(!oneAi || !whiteAi);
        }

    }

    //endregion

}
