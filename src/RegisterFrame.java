import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterFrame extends JFrame {

    // --- 1. KHAI BÁO BIẾN GIAO DIỆN ---
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword; // Thêm ô xác nhận mật khẩu
    private UserManager userManager;
    private Image bgImage;

    public RegisterFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        userManager = new UserManager();
        bgImage = new ImageIcon(getClass().getResource("./LoginScreen.png")).getImage();

        // --- 2. THIẾT LẬP CỬA SỔ CHÍNH ---
        setTitle("Flappy Bird - Đăng Ký");
        setSize(600, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // --- 3. TẠO CÁC KHUNG CHỨA ---
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
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        // --- 4. TẠO CÁC THÀNH PHẦN BÊN TRONG ---
        
        // Ô Username
        JPanel userPanel = createInputPanel("USERNAME");
        txtUsername = new JTextField();
        styleTextField(txtUsername);
        userPanel.add(txtUsername, BorderLayout.CENTER);

        // Ô Password
        JPanel passPanel = createInputPanel("PASSWORD");
        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        passPanel.add(txtPassword, BorderLayout.CENTER);

        // Ô Confirm Password
        JPanel confirmPassPanel = createInputPanel("CONFIRM PASSWORD");
        txtConfirmPassword = new JPasswordField();
        styleTextField(txtConfirmPassword);
        confirmPassPanel.add(txtConfirmPassword, BorderLayout.CENTER);

        // Nút Đăng Ký
        JButton btnRegister = new JButton("CREATE ACCOUNT");
        btnRegister.setFont(new Font("Arial", Font.BOLD, 16)); 
        btnRegister.setForeground(Color.BLACK);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0)); 
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(300, 45)); 

        // Link quay lại Đăng Nhập
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginPanel.setOpaque(false);
        JLabel lblHaveAccount = new JLabel("Already have an account?");
        lblHaveAccount.setForeground(new Color(170, 170, 170));
        lblHaveAccount.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel lblLogIn = new JLabel("Log in");
        lblLogIn.setForeground(Color.WHITE);
        lblLogIn.setFont(new Font("Arial", Font.BOLD, 14));
        lblLogIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        loginPanel.add(lblHaveAccount);
        loginPanel.add(lblLogIn);

        // --- 5. LẮP RÁP FORM ---
        formPanel.add(Box.createVerticalStrut(20)); 
        formPanel.add(userPanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passPanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(confirmPassPanel);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(btnRegister);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(loginPanel);

        // --- 6. CĂN CHỈNH VỊ TRÍ KHUNG ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(90, 10, 10, 10); 
        mainPanel.add(formPanel, gbc);
        add(mainPanel);

        // --- 7. XỬ LÝ SỰ KIỆN ---

        // Khi bấm chữ "Log in" -> Quay lại màn hình Login
        lblLogIn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { lblLogIn.setForeground(new Color(59, 133, 38)); }
            @Override
            public void mouseExited(MouseEvent e) { lblLogIn.setForeground(Color.WHITE); }
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); // Tắt màn hình Đăng Ký
                new LoginFrame().setVisible(true); // Mở lại màn hình Đăng Nhập
            }
        });

        // Khi bấm nút "CREATE ACCOUNT" -> Kiểm tra và lưu CSDL
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());
                String confirmPassword = new String(txtConfirmPassword.getPassword());

                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Please fill in all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (userManager.register(username, password)) {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Account created successfully! Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Tắt màn hình Đăng Ký
                    new LoginFrame().setVisible(true); // Trở về màn hình Đăng Nhập
                } else {
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // --- 8. CÁC HÀM HỖ TRỢ ---
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