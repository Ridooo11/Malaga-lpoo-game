package clases;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class GameWindow {

    private static final int GAME_WIDTH = 500;
    private static final int GAME_HEIGHT = 700;

    public static void main(String[] args) {

        JFrame frmMalaga = new JFrame("Juego");
        frmMalaga.setIconImage(Toolkit.getDefaultToolkit().getImage(GameWindow.class.getResource("/resources/logo_malaga.jpg")));
        frmMalaga.setTitle("Malaga");

        frmMalaga.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmMalaga.setSize(900, 850);
        frmMalaga.getContentPane().setLayout(null);
        frmMalaga.setLocationRelativeTo(null);
        frmMalaga.setResizable(false);

        GamePanel gamePanel = new GamePanel();
        gamePanel.setSize(GAME_WIDTH, GAME_HEIGHT);
        gamePanel.setBackground(Color.GRAY);
        gamePanel.setLocation(200, 100);

        JPanel logoPanel = new JPanel();
        logoPanel.setSize(900, 811);
        logoPanel.setLocation(0, 0);
        logoPanel.setBackground(Color.BLACK);

        ImageIcon gifIcon = null;
        try {
            InputStream inputStream = GameWindow.class.getClassLoader().getResourceAsStream("resources/bg_image.jpeg");
            if (inputStream != null) {
                gifIcon = new ImageIcon(ImageIO.read(inputStream));
            } else {
                System.out.println("Error: El GIF no se pudo cargar. Verifica la ruta.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel gifLabel = new JLabel(gifIcon != null ? gifIcon : new ImageIcon());
        logoPanel.add(gifLabel);

        frmMalaga.getContentPane().setBackground(Color.BLACK);

        frmMalaga.getContentPane().add(gamePanel);
        frmMalaga.getContentPane().add(logoPanel);

        frmMalaga.setVisible(true);
    }
}

