import javax.swing.*;

import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        // attempt database connection (reads values from .env)
        try {
            ConnectDB.getDatabase();
        } catch (Exception e) {
            System.err.println("Lỗi khi kết nối database: " + e.getMessage());
        }

        int boardWidth = 450;
        int boardHeight = 600;

        JFrame frame = new JFrame("Flappy Bird");
        // frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true);
        // Ensure the game panel has focus so it receives key events
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                flappyBird.requestFocusInWindow();
            }
        });
        frame.setResizable(false);

    }
}
