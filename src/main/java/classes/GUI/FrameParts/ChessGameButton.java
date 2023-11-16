package classes.GUI.FrameParts;

import classes.Game.I18N.ChessGameException;
import classes.Game.Model.Structure.IBoard;
import lombok.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;

import static classes.Ai.FenConverter.*;
import static classes.GUI.Frame.Window.*;
import static classes.GUI.FrameParts.GameBoard.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Game.I18N.METHODS.*;

public class ChessGameButton extends JButton {

    //region Fields

    private static JDialog pauseDialog;

    private static ChessGameButton continueButton;

    private static ChessGameButton exitButton;

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
            } catch (ChessGameException | InterruptedException | IOException ex) {
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

        private void manageClick(MouseEvent e) throws ChessGameException, InterruptedException, IOException {
            if (((ChessGameButton)e.getSource()).isEnabled()) {
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
        }

        private void newGameClicked() {
            if (notNull(getLogger())) {
                getLogger().setText("");
            }
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
                    newGameDialog.dispose();
                } catch (ChessGameException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });

            whiteAi.addActionListener(e -> {
                try {
                    newGameWhiteAiClicked();
                    newGameDialog.dispose();
                } catch (ChessGameException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });

            aiVsAi.addActionListener(e -> {
                try {
                    newGameAiVsAiClicked();
                    newGameDialog.dispose();
                } catch (ChessGameException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            });

            test.addActionListener(e -> {
                try {
                    newGameTestClicked();
                    newGameDialog.dispose();
                } catch (ChessGameException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
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
            newGameInitialization(true, false, false, "");
        }

        private void newGameWhiteAiClicked() throws ChessGameException, InterruptedException {
            newGameInitialization(true, true, false, "");
        }

        private void newGameAiVsAiClicked() throws ChessGameException, InterruptedException {
            newGameInitialization(false, false, false, "");
        }

        private void newGameTestClicked() throws ChessGameException, InterruptedException {
            newGameInitialization(false, false, true, "");
        }

        private void pauseClicked() {

            pauseDialog = new JDialog();
            pauseDialog.setTitle("Szeretné folytatni?");
            pauseDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            pauseDialog.setLayout(new FlowLayout());

            continueButton = new ChessGameButton("Folytatás");
            exitButton = new ChessGameButton("Kilépés");

            pause();

            pauseDialog.add(continueButton);
            pauseDialog.add(exitButton);
            pauseDialog.getContentPane().setBackground(BACK_GROUND);
            pauseDialog.pack();
            pauseDialog.setModal(true);
            pauseDialog.setLocationRelativeTo(null); // A képernyő közepére helyezi az ablakot
            pauseDialog.setVisible(true);

        }

        private void saveClicked() throws ChessGameException {
            saveBoard(getViewBoard());
        }

        private void loadClicked() throws IOException, ChessGameException, InterruptedException {
            isFirstOpen = false;

            JFileChooser fileChooser = new JFileChooser("src/main/Saves/");

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {

                File selectedFile = fileChooser.getSelectedFile();

                JOptionPane.showMessageDialog(null, "A kiválasztott fájl: " + selectedFile.getAbsolutePath());
                // Ai vs Ai
                int whiteSideOption = - 1;
                int aiVsAiOption = JOptionPane.showOptionDialog(
                        null,
                        "Ai vs Ai?",
                        "Ai vs Ai választás",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Igen", "Nem"},
                        null
                );

                if (aiVsAiOption == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "Ai vs Ai választás: Igen");
                } else {
                    // Sötéttel szeretnél lenni?
                    whiteSideOption = JOptionPane.showOptionDialog(
                            null,
                            "Világossal szeretnél lenni?",
                            "Szín válssztás",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new Object[]{"Igen", "Nem"},
                            null
                    );

                    if (whiteSideOption == JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(null, "Világossal szeretnél lenni: Igen");
                    } else {
                        JOptionPane.showMessageDialog(null, "Világossal szeretnél lenni: Nem");
                    }
                }
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String fen = reader.readLine();
                newGameInitialization(
                        0 == aiVsAiOption, 1 == whiteSideOption,
                        false,
                        fen);
            } else {
                JOptionPane.showMessageDialog(null, "Fájl kiválasztás megszakítva.");
            }
        }

        private void submissionClicked() {

        }

        private void drawClicked() {

        }

        private void continueClicked(){
            pauseDialog.dispose();
            releasePause();
        }

        private void exitClicked(Window window){
            pauseDialog.dispose();
            window.dispose();
            System.exit(0);
        }

        private void newGameInitialization(boolean oneAi, boolean whiteAi, boolean test, String setUpFen) throws ChessGameException, InterruptedException {
            theresOnlyOneAi = oneAi;
            whiteAiNeeded = whiteAi;
            isTest = test;
            if (!isFirstOpen && "".equals(setUpFen)){
                setUpFen = usualFens.get(theresOnlyOneAi ?
                                (whiteAiNeeded ? "blackDownStarter" : "whiteDownStarter") :
                                "whiteDownStarter");
            }
            setUpSides(setUpFen);
            buttonsEnabled();
            initialization();
            labelTexting(!oneAi || !whiteAi);
        }

        private void saveBoard(IBoard board) throws ChessGameException {

            String fen = BoardToFen(board);
            String save = dateToString(new Date());
            String savePath = "src\\main\\Saves\\" + save + ".txt";
            File file = new File(savePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(fen);
                showFlashFrame("<html><div style='text-align: center;'>A mentés megtörtént a<br>" +
                        "src\\main\\java\\Saves helyre<br>" +
                        save +".txt</div></html>", 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void pause(){
            synchronized(pauseFlag){
                pauseFlag.set(true);
            }
        }

        void releasePause(){
            synchronized(pauseFlag){
                pauseFlag.set(false);
                pauseFlag.notifyAll();
            }
        }

    }

    //endregion

}
