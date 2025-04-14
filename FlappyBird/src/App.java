import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        // GAME WINDOW
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");// GAME NAME
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird bird = new FlappyBird();
        frame.add(bird);
        frame.pack();// to set game in screen excluding the game bar
        bird.requestFocus();

        frame.setVisible(true);
    }
}
