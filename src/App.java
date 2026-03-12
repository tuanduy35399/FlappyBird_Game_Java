/* //import java.awt.Color;

import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
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
        frame.setResizable(false);
    }
}

*/
import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        // Khởi tạo kết nối database trước khi mở giao diện
        try {
            ConnectDB.getDatabase();
        } catch (Exception e) {
            System.err.println("Lỗi khi kết nối database: " + e.getMessage());
        }

        // Mở màn hình Đăng nhập
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }

    // Hàm này được LoginFrame gọi sau khi đăng nhập thành công
    public static void startGame(String username) {
        int boardWidth = 450;
        int boardHeight = 600;

        JFrame frame = new JFrame("Flappy Bird - Người chơi: " + username);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Truyền tên người chơi vào game để lưu điểm
        FlappyBird flappyBird = new FlappyBird(username);
        frame.add(flappyBird);
        frame.pack();   
        flappyBird.requestFocus();
        frame.setVisible(true);
        frame.setResizable(false);
    }
}