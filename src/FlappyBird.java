/* import javax.swing.*;
import java.awt.*;
//import java.awt.RenderingHints.Key;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener, MouseListener {
    int boardWidth = 450;
    int boardHeight = 600;

    // button
    int btnX = 150;
    int btnY = 450;
    int btnWidth = 160;
    int btnHeight = 50;

    // Images
    Image bgImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 40;
    int birdHeight = 40;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;

        }
    }

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 80;
    int pipeHeight = 450;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false; // check bird da vuot qua pipe chua??

        Pipe(Image img) {
            this.img = img;
        }
    }

    // game logic
    Bird bird;
    int velocityX = -5; // van toc ngang cua pipe
    int velocityY = 3; // van toc len xuong cua bird
    int gravity = 1; // trong luc keo bird xuong

    ArrayList<Pipe> pipes;
    Random random = new Random(); // random de tao do cao ngau nhien cho pipe

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 4;
    boolean gameStarted = false;
    int openingSpace;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.white);

        setFocusable(true);
        addKeyListener(this); // them key listener de nhan su kien bam phim
        addMouseListener(this); // them mouse listener de nhan su kien bam chuot

        // load Images
        bgImg = new ImageIcon(getClass().getResource("./bg5.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./bird12_rm.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./pipetop.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./pipebottom.png")).getImage();

        // bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // place pipes timer
        placePipesTimer = new Timer(1600, new ActionListener() {
            // 2000ms = 2s moi xuat hien 1 pipes
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        // placePipesTimer.start();

        // game timer
        gameLoop = new Timer(1000 / 60, this);
        // gameLoop.start();

    }

    public void placePipes() {
        // random độ cao của pipe
        int baseOpening = boardHeight / 2; // k/c mặc định giữa 2 pipe
        int diff = 6;

        // tao k/c giữa 2 pipe thu hẹp dần theo điểm số (tạo độ khó cho game)
        openingSpace = baseOpening - (int) (score * diff);
        openingSpace = Math.max(openingSpace, boardHeight/5); // đảm bảo k/c tối thiểu giữa 2 pipe

        int margin = 80; // khoảng cách tối thiểu tới mép trên/dưới

        int minY = margin;
        int maxY = boardHeight - openingSpace - margin;
        
        //int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        //int randomPipeY = (int) (Math.random() * (boardHeight - openingSpace));
        int randomGapY = minY + (int)(Math.random() * (maxY - minY));

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomGapY - pipeHeight; // y của pipe trên = y của pipe dưới - chiều cao của pipe
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        // y chính là diem bat dau của pipe
        // y của pipe dưới = y của pipe trên + chiều cao của pipe + k/c giữa 2 pipe
        bottomPipe.y = randomGapY + openingSpace;
        pipes.add(bottomPipe);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawString("Opening: " + openingSpace, 10, 50);
        // background
        g.drawImage(bgImg, 0, 0, boardWidth, boardHeight, null);

        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (!gameStarted || gameOver) {
            // hien thi diem khi game over
            g.drawString("Game over: " + String.valueOf((int) score), 10, 35);

            // button
            g.setColor(new Color(135, 206, 250));
            g.fillRoundRect(btnX, btnY, btnWidth, btnHeight, 20, 20);
            g.setColor(Color.black);

            // font cho button
            String text = "Start";
            Font font = new Font("Arial", Font.BOLD, 20);
            g.setFont(font);
            // Lấy thông tin font
            FontMetrics metrics = g.getFontMetrics(font);

            // Tính toán để căn giữa
            int textX = btnX + (btnWidth - metrics.stringWidth(text)) / 2;
            int textY = btnY + ((btnHeight - metrics.getHeight()) / 2) + metrics.getAscent();

            g.setColor(Color.black);
            g.drawString(text, textX, textY);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }

    }

    public void resetGame(){
        score = 0;
        velocityY = 0;
        bird.y = birdY;
        pipes.clear();

        gameOver = false;
        gameStarted = true;

        // gameLoop.start();
        // placePipesTimer.start();
    }

    public void move() {
        // bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // vuot qua 1 pipe duoi hoac tren thi tang 0.5 diem
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    // kiem tra va cham giua bird va pipe
    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // move();
        // repaint();
        if (gameStarted) {
            move();
        }
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
            gameStarted = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9; // Jump
            if (gameOver) {
                resetGame();
                placePipesTimer.start();

            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (!gameStarted) {
            if (mouseX >= btnX && mouseX <= btnX + btnWidth && 
                mouseY >= btnY && mouseY <= btnY + btnHeight) {
                resetGame();
                gameLoop.start();
                placePipesTimer.start();
            }
        } 
        else{
            velocityY = -9; // Jump
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener, MouseListener {
    int boardWidth = 450;
    int boardHeight = 600;

    // button
    int btnX = 150;
    int btnY = 450;
    int btnWidth = 160;
    int btnHeight = 50;

    // Images
    Image bgImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 40;
    int birdHeight = 40;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 80;
    int pipeHeight = 450;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false; 

        Pipe(Image img) {
            this.img = img;
        }
    }

    // game logic
    Bird bird;
    int velocityX = -5; // van toc ngang cua pipe
    int velocityY = 3; // van toc len xuong cua bird
    int gravity = 1; // trong luc keo bird xuong

    ArrayList<Pipe> pipes;
    Random random = new Random(); 

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0; // Đổi lại thành 0 lúc bắt đầu
    boolean gameStarted = false;
    int openingSpace;

    // --- BIẾN QUẢN LÝ TÀI KHOẢN & DATABASE ---
    String playerName;
    UserManager userManager;
    double highScore = 0;
    boolean scoreSaved = false;

    // Sửa constructor để nhận tên người chơi
    FlappyBird(String playerName) {
        // --- KHỞI TẠO DATABASE ---
        this.playerName = playerName;
        this.userManager = new UserManager();
        this.highScore = userManager.getHighScore(playerName);

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.white);

        setFocusable(true);
        addKeyListener(this); 
        addMouseListener(this); 

        // load Images
        bgImg = new ImageIcon(getClass().getResource("./bg5.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./bird12_rm.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./pipetop.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./pipebottom.png")).getImage();

        // bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // place pipes timer
        placePipesTimer = new Timer(1600, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        // game timer
        gameLoop = new Timer(1000 / 60, this);
    }

    public void placePipes() {
        int baseOpening = boardHeight / 2; 
        int diff = 6;

        openingSpace = baseOpening - (int) (score * diff);
        openingSpace = Math.max(openingSpace, boardHeight/5); 

        int margin = 80; 

        int minY = margin;
        int maxY = boardHeight - openingSpace - margin;
        
        int randomGapY = minY + (int)(Math.random() * (maxY - minY));

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomGapY - pipeHeight; 
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = randomGapY + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // background
        g.drawImage(bgImg, 0, 0, boardWidth, boardHeight, null);

        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Hiện debug opening space nếu muốn
        // g.drawString("Opening: " + openingSpace, 10, 50);

        // score & high score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        
        if (!gameStarted || gameOver) {
            // Hiển thị Điểm và Kỷ lục
            g.drawString("Điểm: " + String.valueOf((int) score), 10, 40);
            g.drawString("Kỷ lục: " + String.valueOf((int) highScore), 10, 80);

            // Vẽ button "Start"
            g.setColor(new Color(135, 206, 250));
            g.fillRoundRect(btnX, btnY, btnWidth, btnHeight, 20, 20);
            g.setColor(Color.black);

            String text = "Start";
            Font font = new Font("Arial", Font.BOLD, 20);
            g.setFont(font);
            FontMetrics metrics = g.getFontMetrics(font);

            int textX = btnX + (btnWidth - metrics.stringWidth(text)) / 2;
            int textY = btnY + ((btnHeight - metrics.getHeight()) / 2) + metrics.getAscent();

            g.setColor(Color.black);
            g.drawString(text, textX, textY);
        } else {
            // Khi đang chơi chỉ hiện điểm hiện tại
            g.drawString(String.valueOf((int) score), 10, 40);
        }
    }

    public void resetGame(){
        score = 0;
        velocityY = 0;
        bird.y = birdY;
        pipes.clear();

        gameOver = false;
        gameStarted = true;
        scoreSaved = false; // Reset cờ lưu điểm để ván sau lưu tiếp
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; 
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }

        // --- LOGIC LƯU ĐIỂM XUỐNG MONGODB KHI THUA ---
        if (gameOver && !scoreSaved) {
            userManager.updateHighScore(playerName, score); // Lưu vào DB
            highScore = userManager.getHighScore(playerName); // Cập nhật lại kỷ lục hiển thị
            scoreSaved = true; // Đánh dấu đã lưu
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted) {
            move();
        }
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
            gameStarted = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameStarted) {
                resetGame();
                gameLoop.start();
                placePipesTimer.start();
            } else {
                velocityY = -9; // Jump
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (!gameStarted) {
            // Click vào nút Start
            if (mouseX >= btnX && mouseX <= btnX + btnWidth && 
                mouseY >= btnY && mouseY <= btnY + btnHeight) {
                resetGame();
                gameLoop.start();
                placePipesTimer.start();
            }
        } 
        else {
            velocityY = -9; // Jump
        }
    }

    // Các hàm không dùng đến
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}