import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener, MouseListener {
    int boardWidth = 450;
    int boardHeight = 600;

    // buttons
    int btnX = 150;
    int btnY = 450;
    int btnWidth = 160;
    int btnHeight = 50;

    // Return button
    int returnBtnY = btnY + btnHeight + 20;

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
    int velocityX = -5;
    int velocityY = 0;
    int gravity = 0;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;
    boolean gameStarted = false;
    int openingSpace;

    // TÀI KHOẢN
    String playerName;
    double highScore = 0;
    boolean scoreSaved = false;

    FlappyBird(String playerName) {
        this.playerName = playerName;
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

        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        placePipesTimer = new Timer(1600, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        gameLoop = new Timer(1000 / 60, this);
    }

    public void placePipes() {
        int baseOpening = boardHeight / 2;
        int diff = 6;

        openingSpace = baseOpening - (int) (score * diff);
        openingSpace = Math.max(openingSpace, boardHeight / 5);

        int margin = 80;

        int minY = margin;
        int maxY = boardHeight - openingSpace - margin;

        int randomGapY = minY + (int) (Math.random() * (maxY - minY));

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
        g.drawImage(bgImg, 0, 0, boardWidth, boardHeight, null);

        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));

        if (!gameStarted || gameOver) {
            g.drawString("Điểm: " + String.valueOf((int) score), 10, 40);
            g.drawString("Kỷ lục: " + String.valueOf((int) highScore), 10, 80);

            // Start button - Gold Yellow #FFD700
            g.setColor(new Color(255, 215, 0));
            g.fillRoundRect(btnX, btnY, btnWidth, btnHeight, 20, 20);
            g.setColor(Color.black);

            String startText = "Start";
            Font font = new Font("Arial", Font.BOLD, 20);
            g.setFont(font);
            FontMetrics metrics = g.getFontMetrics(font);
            int startTextX = btnX + (btnWidth - metrics.stringWidth(startText)) / 2;
            int startTextY = btnY + ((btnHeight - metrics.getHeight()) / 2) + metrics.getAscent();
            g.drawString(startText, startTextX, startTextY);

            // Return button - Cyan #00BCD4
            g.setColor(new Color(0, 188, 212));
            g.fillRoundRect(btnX, returnBtnY, btnWidth, btnHeight, 20, 20);
            String returnText = "Return";
            int returnTextX = btnX + (btnWidth - metrics.stringWidth(returnText)) / 2;
            int returnTextY = returnBtnY + ((btnHeight - metrics.getHeight()) / 2) + metrics.getAscent();
            g.setColor(Color.black);
            g.drawString(returnText, returnTextX, returnTextY);
        } else {
            g.drawString(String.valueOf((int) score), 10, 40);
        }
    }

    public void resetGame() {
        score = 0;
        velocityY = 0;
        bird.y = birdY;
        pipes.clear();

        gameOver = false;
        gameStarted = true;
        scoreSaved = false;
    }

    public void startGameControl() {
        if (!gameStarted) {
            resetGame();
            gameLoop.start();
            placePipesTimer.start();
        }
    }

    public void applyHandControl(int direction) {
        startGameControl();

        if (direction < 0) {
            velocityY = -4;
        } else if (direction > 0) {
            velocityY = 4;
        } else {
            velocityY = 0;
        }
    }

    public void setBirdPositionFromControl(double normalizedY) {
        startGameControl();
        velocityY = 0;

        double clamped = Math.max(0.0, Math.min(1.0, normalizedY));
        int minBirdY = 0;
        int maxBirdY = boardHeight - bird.height;
        bird.y = minBirdY + (int) Math.round(clamped * maxBirdY);
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);
        bird.y = Math.min(bird.y, boardHeight - bird.height);

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

        if (bird.y > boardHeight - bird.height) {
            bird.y = boardHeight - bird.height;
        }
    }

    // public boolean collision(Bird a, Pipe b) {
    // return a.x < b.x + b.width &&
    // a.x + a.width > b.x &&
    // a.y < b.y + b.height &&
    // a.y + a.height > b.y;
    // }
    // đổi sang thuật toán kiểm tra va chạm có padding để tránh có khoảng cách giữa
    // bird và pipe khi thua (không chân thực)
    public boolean collision(Bird a, Pipe b) {
        int paddingX = 5;
        int paddingY = 5;

        int birdLeft = a.x + paddingX;
        int birdRight = a.x + a.width - paddingX;
        int birdTop = a.y + paddingY;
        int birdBottom = a.y + a.height - paddingY;

        int pipeLeft = b.x;
        int pipeRight = b.x + b.width;
        int pipeTop = b.y;
        int pipeBottom = b.y + b.height;

        return birdLeft < pipeRight &&
                birdRight > pipeLeft &&
                birdTop < pipeBottom &&
                birdBottom > pipeTop;
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
            startGameControl();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (!gameStarted) {
            // Start button
            if (mouseX >= btnX && mouseX <= btnX + btnWidth &&
                    mouseY >= btnY && mouseY <= btnY + btnHeight) {
                startGameControl();
            } // Return button
            else if (mouseX >= btnX && mouseX <= btnX + btnWidth &&
                    mouseY >= returnBtnY && mouseY <= returnBtnY + btnHeight) {
                JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                gameFrame.dispose();
                new MenuFrame(playerName).setVisible(true);
            }
        }
    }

    // Unused
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
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
