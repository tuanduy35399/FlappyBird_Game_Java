import javax.swing.*;
import java.awt.*;

public class MenuFrame extends JFrame {

    private Image bgImage;
    private String username;

    public MenuFrame(String username) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.username = username;
        bgImage = new ImageIcon(getClass().getResource("./LoginScreen.png")).getImage();

        setTitle("Flappy Bird - Menu (" + username + ")");
        setSize(600, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(35, 35, 35, 240));
        formPanel.setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));
        formPanel.setMaximumSize(new Dimension(400, 500));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Chào " + username + "!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);

        formPanel.add(Box.createVerticalStrut(40));

        JButton btnStart = new JButton("START GAME");
        styleButton(btnStart);
        btnStart.setBackground(new Color(255, 215, 0)); // Gold Yellow #FFD700
        btnStart.addActionListener(e -> {
            dispose();
            App.startGame(username);
        });
        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnStart.setMaximumSize(new Dimension(300, 60));
        formPanel.add(btnStart);

        formPanel.add(Box.createVerticalStrut(20));

        JButton btnQuit = new JButton("QUIT GAME");
        styleButton(btnQuit);
        btnQuit.setBackground(new Color(255, 152, 0)); // Orange #FF9800
        btnQuit.addActionListener(e -> System.exit(0));
        btnQuit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnQuit.setMaximumSize(new Dimension(300, 60));
        formPanel.add(btnQuit);

        formPanel.add(Box.createVerticalStrut(20));

        JButton btnLogout = new JButton("LOGOUT");
        styleButton(btnLogout);
        btnLogout.setBackground(new Color(0, 188, 212)); // Cyan #00BCD4
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(300, 60));
        formPanel.add(btnLogout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(100, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(formPanel, gbc);
        add(mainPanel);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
