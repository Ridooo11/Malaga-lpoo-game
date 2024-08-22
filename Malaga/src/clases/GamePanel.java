package clases;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

class GamePanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int SQUARE_SIZE = 32;
    private static final int GAME_WIDTH = 500;
    private static final int GAME_HEIGHT = 700;
    private static final int SQUARE_Y_POSITION = GAME_HEIGHT - SQUARE_SIZE - 20;
    private static final int PROJECTILE_SIZE = 16;
    private static final int PROJECTILE_SPEED = 10;
    private int squareX;
    private Timer moveTimer;
    private Timer shootTimer;
    private int moveDirection;
    private boolean canShoot;
    private boolean isShooting;

    private List<Rectangle> projectiles;

    public GamePanel() {
        squareX = (GAME_WIDTH - SQUARE_SIZE) / 2;
        moveDirection = 0;
        canShoot = true;
        isShooting = false;
        projectiles = new ArrayList<>();

        setFocusable(true);
        requestFocusInWindow();

        moveTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (moveDirection != 0) {
                    moveSquare(moveDirection);
                }
                if (isShooting) {
                    shootProjectile();
                }
                updateProjectiles();
            }
        });
        moveTimer.start();

        shootTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canShoot = true;
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) {
                    moveDirection = -5;
                } else if (key == KeyEvent.VK_RIGHT) {
                    moveDirection = 5;
                } else if (key == KeyEvent.VK_SPACE) {
                    isShooting = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if ((key == KeyEvent.VK_LEFT && moveDirection == -5) ||
                    (key == KeyEvent.VK_RIGHT && moveDirection == 5)) {
                    moveDirection = 0;
                } else if (key == KeyEvent.VK_SPACE) {
                    isShooting = false;
                }
            }
        });
    }

    private void moveSquare(int dx) {
        squareX += dx;
        squareX = Math.max(0, Math.min(squareX, GAME_WIDTH - SQUARE_SIZE));
        repaint();
    }

    private void shootProjectile() {
        if (canShoot) {
            int projectileX = squareX + (SQUARE_SIZE - PROJECTILE_SIZE) / 2;
            int projectileY = SQUARE_Y_POSITION;
            projectiles.add(new Rectangle(projectileX, projectileY, PROJECTILE_SIZE, PROJECTILE_SIZE));
            canShoot = false;
            shootTimer.restart();
        }
    }

    private void updateProjectiles() {
        for (int i = 0; i < projectiles.size(); i++) {
            Rectangle projectile = projectiles.get(i);
            projectile.y -= PROJECTILE_SPEED;
            if (projectile.y + PROJECTILE_SIZE < 0) {
                projectiles.remove(i);
                i--;
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(squareX, SQUARE_Y_POSITION, SQUARE_SIZE, SQUARE_SIZE);

        for (Rectangle projectile : projectiles) {
            g.fillRect(projectile.x, projectile.y, projectile.width, projectile.height);
        }
    }
}
