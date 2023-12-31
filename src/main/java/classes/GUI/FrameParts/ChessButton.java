package classes.GUI.FrameParts;

import classes.Model.I18N.ChessGameException;
import classes.Model.Structure.IBoard;
import lombok.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

import static classes.Controller.EDT.*;
import static classes.Controller.FenConverter.*;
import static classes.GUI.Frame.Window.*;
import static classes.GUI.FrameParts.GameBoard.*;
import static classes.GUI.FrameParts.ViewBoard.*;
import static classes.Model.AI.BitBoards.BitBoardMoves.*;
import static classes.Model.I18N.METHODS.*;
import static classes.Model.I18N.VARS.FINALS.*;
import static classes.Model.I18N.VARS.MUTABLE.*;
import static classes.Model.Structure.GameOverOrPositionEnd.*;

public class ChessButton extends JButton {

    //region Fields

    private static JDialog pauseDialog;

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

            JPanel buttonPanel = new JPanel(new GridLayout(2, 2));

            newGameDialog.setTitle("Új játék kiválasztása");
            newGameDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            newGameDialog.setLayout(new GridLayout(3, 1));

            ChessButton blackAi = new ChessButton("<html><div style='text-align: center;'>Világossal<br>szeretnék lenni</div></html>");
            ChessButton whiteAi = new ChessButton("<html><div style='text-align: center;'>Sötéttel<br>szeretnék lenni</div></html>");
            ChessButton aiVsAi = new ChessButton("<html><div style='text-align: center;'>Ai vs Ai</div></html>");

            blackAi.setBorder(BorderFactory.createLineBorder(BLACK, 13));
            whiteAi.setBorder(BorderFactory.createLineBorder(BLACK, 13));
            aiVsAi.setBorder(BorderFactory.createLineBorder(BLACK, 13));

            blackAi.setBorderPainted(true);
            whiteAi.setBorderPainted(true);
            aiVsAi.setBorderPainted(true);

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

            buttonPanel.add(blackAi);
            buttonPanel.add(whiteAi);
            buttonPanel.add(aiVsAi);

            newGameDialog.add(blackAi);
            newGameDialog.add(whiteAi);
            newGameDialog.add(aiVsAi);

            newGameDialog.setBounds((int) NEW_GAME_WINDOW_START_X, (int) NEW_GAME_WINDOW_START_Y, (int) NEW_GAME_WINDOW_WIDTH, (int) NEW_GAME_WINDOW_HEIGHT); // középre pozícionálás
            newGameDialog.setModal(true); // modális beállítás

            newGameDialog.setVisible(true);
        }

        private void newGameBlackAiClicked() {
            theresOnlyOneAi = true;
            whiteAiNeeded = false;
            newGameInitialization("");
        }

        private void newGameWhiteAiClicked() {
            theresOnlyOneAi = true;
            whiteAiNeeded = true;
            newGameInitialization("");
        }

        private void newGameAiVsAiClicked() {
            theresOnlyOneAi = false;
            whiteAiNeeded = false;
            newGameInitialization("");
        }

        private void pauseClicked() {

            pauseDialog = new JDialog();
            pauseDialog.setTitle("Szeretné folytatni?");
            pauseDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            pauseDialog.setLayout(new FlowLayout());

            ChessButton continueButton = new ChessButton("Folytatás");
            ChessButton exitButton = new ChessButton("Kilépés");

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

            JFileChooser fileChooser = new JFileChooser(SAVES_DIRECTORY_PATH);

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {

                File selectedFile = fileChooser.getSelectedFile();

                JOptionPane.showMessageDialog(null, "A kiválasztott fájl: " + selectedFile.getName());
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String aiColor = reader.readLine();
                if ("log".equals(aiColor.substring(0, 3))) {
                    throw new ChessGameException("Log (azaz egy elmúlt játék leírását tartalmazó) fájl-t próbál betölteni, ez nem lehetséges");
                }
                String fen = reader.readLine();

                if ('w' == aiColor.charAt(0)){
                    if ("whiteD".equals(aiColor.substring(0, 6))){
                        theresOnlyOneAi = false;
                        whiteAiNeeded = false;
                    }else {
                        theresOnlyOneAi = true;
                        whiteAiNeeded = true;
                    }
                }else {
                    theresOnlyOneAi = true;
                    whiteAiNeeded = false;
                }

                newGameInitialization(fen);
            } else {
                JOptionPane.showMessageDialog(null, "Fájl kiválasztás megszakítva.");
            }
        }

        private void submissionClicked() {
            GameOverDecision(getViewBoard(), whiteToPlay ? WHITE_SUBMITTED : BLACK_SUBMITTED);
        }

        private void drawClicked() {
            GameOverDecision(getViewBoard(), DRAW_OFFER);
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

        private void newGameInitialization(String setUpFen) {
            gameEndFlag.set(false);

            if (!isFirstOpen && "".equals(setUpFen)){
                setUpFen = usualFens.get(theresOnlyOneAi ?
                                (whiteAiNeeded ? "blackDownStarter" : "whiteDownStarter") :
                                "whiteDownStarter");
            }

            int widthHeight = setUpFen.split("/").length;
            MAX_HEIGHT = widthHeight;
            MAX_WIDTH = widthHeight;

            whiteDown = !theresOnlyOneAi || !whiteAiNeeded;
            stepNumber = 1;

            getWindow().addGameBoard(getWindow());
            setUpSides(setUpFen);
            buttonsEnabled(new ArrayList<>(){{add("All");}});
            labelTexting();
            initialization();
            fillBaseBitBoardPossibilities();
        }

        public static void saveBoard(IBoard board) {
            String fen = BoardToFen(board);
            String save = dateToString(new Date());
            String savePath = SAVES_DIRECTORY_PATH + save + ".txt";
            File file = new File(savePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

                if (theresOnlyOneAi) {
                    writer.write((whiteAiNeeded ? "whiteAi\n" : "blackAi\n") + fen);
                }
                else {
                    writer.write("whiteDown" + "blackAi\n" + fen);
                }
                showFlashFrame("A mentés megtörtént a\n" +
                        SAVES_DIRECTORY_PATH + " helyre " +
                        save +".txt", 3);
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
