import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // IMAGES
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // PIPE
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; // SCALEE BY 1 / 6;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false; // TOP TRACK THE SCORE

        Pipe(Image img) {
            this.img = img;
        }
    }

    // BIRD
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

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

    // GAME LOGIC
    Bird bird;

    int velocityX = -4; // MOVE PIPE TO LEFT SPEED (STIMULATES BIRD IS MOVING RIGHT)
    int velocityY = 0; // MOVE BIRD UP
    int gravity = 1; // MOVE BIRD DOWN
    double score = 0;

    ArrayList<Pipe> pipes;

    Random random;

    Timer gameLoop;
    Timer placePipTimer;

    boolean gameOver;

    FlappyBird() {
        setPreferredSize(new DimensionUIResource(boardWidth, boardHeight));
        setFocusable(true);// TAKE OUR KEY EVENTS
        addKeyListener(this);// CHECK THE FUNCTION OF KEYLISTENER

        // LOAD IMAGE
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // BIRD
        bird = new Bird(birdImg);

        // PIPE
        pipes = new ArrayList<>();

        // PLACE PIPE TIMER
        placePipTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipe();
            }
        });
        placePipTimer.start();

        // GAME TIMER
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void placePipe() {
        //(0-1) * pipeHeight/2.
        // 0 -> -128 (pipeHeight/4)
        // 1 -> -128 - 256 (pipeHeight/4 - pipeHeight/2) = -3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random()*(pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // BACKGROUND
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // BIRD
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        
        // PIPE
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // SCORE
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 32));
        if(gameOver) {
            g.drawString("GAME OVER", 85, 300);
            g.drawString(String.valueOf((int) score), 168, 350);
        }else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // BIRD
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);// apply gravity to current bird.y, limit the bird.y to top of the canvas

        // PIPE
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // BECAUSE THERE ARE 2 PIPES
            }

            if(collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if(bird.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver) {
            placePipTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -7;

            if (gameOver) {
                //restart game by resetting conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placePipTimer.start();
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