
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        
        try {
            ConnectDB.getDatabase();
        } catch (Exception e) {
            System.err.println("Lỗi khi kết nối database: " + e.getMessage());
        }


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }


    public static void startGame(String username) {
        int boardWidth = 450;
        int boardHeight = 600;

        JFrame frame = new JFrame("Flappy Bird - Người chơi: " + username);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird(username);
        HandController faceController = new HandController(flappyBird);
        Thread faceThread = new Thread(faceController, "hand-wave-controller");
        faceThread.setDaemon(true);

        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true);
        frame.setResizable(false);
        faceThread.start();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                faceController.stop();
            }
        });
    }
}
