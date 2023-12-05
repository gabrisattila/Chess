package classes.GUI.FrameParts;

import classes.Game.Model.Structure.IBoard;
import lombok.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

import static classes.Ai.FenConverter.*;
import static classes.GUI.Frame.Window.*;
import static classes.GUI.FrameParts.GameBoard.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Game.I18N.VARS.FINALS.*;
import static classes.Game.I18N.VARS.MUTABLE.*;
import static classes.Game.Model.Logic.EDT.*;
import static classes.Game.I18N.METHODS.*;
import static classes.Game.Model.Structure.GameOverOrPositionEnd.GameOverDecision;

public class ChessButton extends JButton {

    //region Fields

    private static JDialog pauseDialog;

    private static ChessButton continueButton;

    private static ChessButton exitButton;

    //endregion


    //region Constructor

    public ChessButton(String text){
        setText(text);
        buttonStyleSetting();
    }

    public ChessButton(){
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
            } catch (InterruptedException | IOException ex) {
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
            Color tmp = ((ChessButton) e.getSource()).getForeground();
            ((ChessButton) e.getSource()).setForeground(((ChessButton) e.getSource()).getBackground());
            ((ChessButton) e.getSource()).setBackground(tmp);
        }

        private void manageClick(MouseEvent e) throws IOException, InterruptedException {
            if (((ChessButton)e.getSource()).isEnabled()) {
                switch (((ChessButton) e.getSource()).getText()) {
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

            ChessButton blackAi = new ChessButton("<html><div style='text-align: center;'>Világossal<br>szeretnék lenni</div></html>");
            ChessButton whiteAi = new ChessButton("<html><div style='text-align: center;'>Sötéttel<br>szeretnék lenni</div></html>");
            ChessButton aiVsAi = new ChessButton("<html><div style='text-align: center;'>Ai vs Ai</div></html>");
            ChessButton test = new ChessButton("<html><div style='text-align: center;'>Teszt</div></html>");

            blackAi.setBorder(BorderFactory.createLineBorder(BLACK, 13));
            whiteAi.setBorder(BorderFactory.createLineBorder(BLACK, 13));
            aiVsAi.setBorder(BorderFactory.createLineBorder(BLACK, 13));
            test.setBorder(BorderFactory.createLineBorder(BLACK, 13));

            blackAi.setBorderPainted(true);
            whiteAi.setBorderPainted(true);
            aiVsAi.setBorderPainted(true);
            test.setBorderPainted(true);

            blackAi.addActionListener(e -> {
                newGameBlackAiClicked();
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

        private void newGameBlackAiClicked() {
            newGameInitialization(true, false, false, "");
        }

        private void newGameWhiteAiClicked() {
            newGameInitialization(true, true, false, "");
        }

        private void newGameAiVsAiClicked() {
            newGameInitialization(false, false, false, "");
        }

        private void newGameTestClicked() {
            newGameInitialization(true, false, true, "");
        }

        private void pauseClicked() {

            pauseDialog = new JDialog();
            pauseDialog.setTitle("Szeretné folytatni?");
            pauseDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            pauseDialog.setLayout(new FlowLayout());

            continueButton = new ChessButton("Folytatás");
            exitButton = new ChessButton("Kilépés");

            pause();

            pauseDialog.add(continueButton);
            pauseDialog.add(exitButton);
            pauseDialog.getContentPane().setBackground(BACK_GROUND);
            pauseDialog.pack();
            pauseDialog.setModal(true);
            pauseDialog.setLocationRelativeTo(null); // A képernyő közepére helyezi az ablakot
            pauseDialog.setVisible(true);

        }

        private void saveClicked()  {
            saveBoard(getViewBoard());
        }

        private void loadClicked() throws IOException {
            isFirstOpen = false;

            JFileChooser fileChooser = new JFileChooser("src/main/Saves/");

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {

                File selectedFile = fileChooser.getSelectedFile();

                JOptionPane.showMessageDialog(null, "A kiválasztott fájl: " + selectedFile.getName());
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String aiColor = reader.readLine();
                String fen = reader.readLine();

                newGameInitialization(true, "whiteAi".equals(aiColor), false, fen);
            } else {
                JOptionPane.showMessageDialog(null, "Fájl kiválasztás megszakítva.");
            }
        }

        private void submissionClicked() {
            GameOverDecision(getViewBoard(), true, whiteToPlay ? WHITE_SUBMITTED : BLACK_SUBMITTED);
        }

        private void drawClicked() {
            GameOverDecision(getViewBoard(), true, DRAW_OFFER);
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

        private void newGameInitialization(boolean oneAi, boolean whiteAi, boolean test, String setUpFen) {
            gameEndFlag.set(false);
            stepNumber = 1;
            theresOnlyOneAi = oneAi;
            whiteAiNeeded = whiteAi;
            isTest = test;

            if (isTest && "".equals(setUpFen)){
                setUpFen = testFens.get("blackMustTakeRookButItIsnt");
//                whiteAiNeeded = setUpFen.split(" ")[1].charAt(0) == 'w';
            }

            if (!isFirstOpen && "".equals(setUpFen)){
                setUpFen = usualFens.get(theresOnlyOneAi ?
                                (whiteAiNeeded ? "blackDownStarter" : "whiteDownStarter") :
                                "whiteDownStarter");
            }

            int widthHeight = setUpFen.split("/").length;
            MAX_HEIGHT = widthHeight;
            MAX_WIDTH = widthHeight;

            whiteDown = !theresOnlyOneAi || !whiteAiNeeded;

            getWindow().addGameBoard(getWindow());
            setUpSides(setUpFen);
            buttonsEnabled(new ArrayList<>(){{add("All");}});
            labelTexting(!oneAi || !whiteAi);
            initialization();
        }

        private void saveBoard(IBoard board)  {

            String fen = BoardToFen(board);
            String save = dateToString(new Date());
            String savePath = "src\\main\\Saves\\" + save + ".txt";
            File file = new File(savePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

                if (theresOnlyOneAi) {
                    writer.write((whiteAiNeeded ? "whiteAi\n" : "blackAi\n") + fen);
                }
                else {
                    writer.write("whiteDown" + "blackAi\n" + fen);
                }
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
