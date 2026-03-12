import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    // --- 1. KHAI BÁO BIẾN GIAO DIỆN ---
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private UserManager userManager;
    private Image bgImage;

    public LoginFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Khởi tạo trình quản lý database và nạp ảnh nền
        userManager = new UserManager();
        bgImage = new ImageIcon(getClass().getResource("./LoginScreen.png")).getImage();

        // --- 2. THIẾT LẬP CỬA SỔ CHÍNH (FRAME) ---
        setTitle("Flappy Bird - Đăng Nhập");
        setSize(600, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Căn giữa màn hình
        setResizable(false);

        // --- 3. TẠO CÁC KHUNG CHỨA (PANEL) ---
        
        // Panel chính: Dùng để vẽ ảnh nền game trùm kín màn hình
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Panel Form: Là cái hộp màu đen xám chứa các ô nhập liệu
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(35, 35, 35, 240)); 
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        // --- 4. TẠO CÁC THÀNH PHẦN BÊN TRONG FORM ---

        // Khu vực nhập Tài khoản
        JPanel userPanel = createInputPanel("USERNAME");
        txtUsername = new JTextField();
        styleTextField(txtUsername);
        userPanel.add(txtUsername, BorderLayout.CENTER);

        // Khu vực nhập Mật khẩu
        JPanel passPanel = createInputPanel("PASSWORD");
        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        passPanel.add(txtPassword, BorderLayout.CENTER);

        // Nút Đăng nhập
        JButton btnLogin = new JButton("LOG IN");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0)); 
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(300, 45)); 

        // Khu vực link "Đăng ký" ở dưới cùng
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);
        
        JLabel lblNoAccount = new JLabel("Don't have an account?");
        lblNoAccount.setForeground(new Color(170, 170, 170));
        lblNoAccount.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel lblSignUp = new JLabel("Sign up");
        lblSignUp.setForeground(Color.WHITE);
        lblSignUp.setFont(new Font("Arial", Font.BOLD, 14));
        lblSignUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        registerPanel.add(lblNoAccount);
        registerPanel.add(lblSignUp);

        // --- 5. LẮP RÁP CÁC THÀNH PHẦN VÀO FORM ---
        formPanel.add(Box.createVerticalStrut(30)); // Tạo khoảng trống 30px
        formPanel.add(userPanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passPanel);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(btnLogin);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(registerPanel);

        // --- 6. CĂN CHỈNH VỊ TRÍ FORM VÀ ĐƯA LÊN MÀN HÌNH ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(60, 10, 10, 10); 
        mainPanel.add(formPanel, gbc);
        add(mainPanel);

        // --- 7. XỬ LÝ SỰ KIỆN NÚT BẤM VÀ CHUỘT ---

        // Hiệu ứng và Logic khi bấm vào chữ "Sign up"
        lblSignUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                lblSignUp.setForeground(new Color(59, 133, 38)); 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                lblSignUp.setForeground(Color.WHITE); 
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                // --- SỬA CHỖ NÀY ---
                dispose(); // Đóng LoginFrame
                new RegisterFrame().setVisible(true); // Mở RegisterFrame lên
            }
        });

        // Logic khi bấm vào nút "LOG IN"
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());

                if (userManager.login(username, password)) {
                    dispose(); // Đóng giao diện đăng nhập
                    App.startGame(username); // Chuyển cảnh vào Game
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid email or password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // --- 8. CÁC HÀM HỖ TRỢ VẼ GIAO DIỆN ---

    // Hàm căn chỉnh layout cho khu vực ghi chữ "USERNAME/PASSWORD"
    private JPanel createInputPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(300, 60)); 

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(new Color(200, 200, 200)); 
        panel.add(lbl, BorderLayout.NORTH);

        return panel;
    }

    // Hàm thiết kế ô nhập liệu
    private void styleTextField(JTextField textField) {
        textField.setBackground(new Color(30, 30, 30)); 
        textField.setForeground(Color.WHITE); 
        textField.setCaretColor(Color.WHITE); 
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }
}