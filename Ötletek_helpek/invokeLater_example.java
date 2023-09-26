import javax.swing.*;

public class BackgroundAndInvokeLaterExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Background and InvokeLater Example");
            JButton button = new JButton("Start Calculation");
            JLabel resultLabel = new JLabel("Result: ");

            button.addActionListener(e -> {
                button.setEnabled(false);
                resultLabel.setText("Calculating...");

                // Háttérszál a számításra
                new Thread(() -> {
                    int result = performCalculation();
                    SwingUtilities.invokeLater(() -> {
                        resultLabel.setText("Result: " + result);
                        button.setEnabled(true);
                    });
                }).start();
            });

            JPanel panel = new JPanel();
            panel.add(button);
            panel.add(resultLabel);

            frame.add(panel);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    private static int performCalculation() {
        // Példa számítás, valódi alkalmazásban itt lenne a számítás logikája
        try {
            Thread.sleep(3000); // Szimuláljuk a hosszabb számítást
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 42;
    }
}
