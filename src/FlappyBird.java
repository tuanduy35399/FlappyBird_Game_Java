import javax.swing.*;
import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;


public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 450;
    int boardHeight = 600;
    
    //Images
    Image bgImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 40;
    int birdHeight = 40;

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;

        }
    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 80;
    int pipeHeight = 450;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false; //check bird da vuot qua pipe chua??
        Pipe (Image img){
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -4; //van toc ngang cua pipe
    int velocityY = 6; //van toc len xuong cua bird
    int gravity = 1; //trong luc keo bird xuong

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.blue);

        setFocusable(true);
        addKeyListener(this);

        //load Images
        bgImg = new ImageIcon(getClass().getResource("./bg5.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./bird12_rm.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./pipetop.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./pipebottom.png")).getImage();
    
        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            //2000ms = 2s moi xuat hien 1 pipes
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //game timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

    }

    public void placePipes(){
        //random độ cao của pipe
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()* (pipeHeight/2));
        //tao k/c giữa 2 pipe
        int openingSpace = boardHeight/4;
        
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);
        Pipe bottomPipe = new Pipe(bottomPipeImg);

        //y chính là diem bat dau của pipe
        //y của pipe dưới = y của pipe trên + chiều cao của pipe + k/c giữa 2 pipe
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);



    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //background
        g.drawImage(bgImg, 0, 0, boardWidth, boardHeight, null);
    
        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        
        //pipes
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver){
            g.drawString("Game over: " + String.valueOf((int) score), 10, 35);
        }
        else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    
    }

    public void move(){
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5; //vuot qua 1 pipe duoi hoac tren thi tang 0.5 diem
            }

            if (collision(bird, pipe)){
            gameOver = true;
            }
        }

        if (bird.y > boardHeight){ 
            gameOver = true;
        }
    }
    
    public boolean collision (Bird a, Pipe b){
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
}
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9; // Jump
            if (gameOver){
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
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

}
