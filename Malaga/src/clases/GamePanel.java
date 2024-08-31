package clases;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int SQUARE_SIZE = 48;
    private static final int PROJECTILE_SIZE = 16;
    private static final int PROJECTILE_SPEED = 10;
    private static final int ENEMY_SIZE = 38;
    private static final int ENEMY_SPEED = 3;
    private static final int ENEMY_DROP_DISTANCE = 30;
    private static final int ENEMY_DROP_THRESHOLD = 2;
    private static final int GAME_WIDTH = 500;
    private static final int GAME_HEIGHT = 700;
    private static final int SQUARE_Y_POSITION = GAME_HEIGHT - SQUARE_SIZE - 20;
    private static final int ENEMY_SHOOT_INTERVAL = 400;
    private static final int ENEMY_SHOOT_PROBABILITY = 5;
    private static final int CAMICASE_SPEED = 7;
    private static final int CAMICASE_SPAWN_INTERVAL = 1100;
    
    
    private static final int MAX_ENEMY_Y =  50 + SQUARE_Y_POSITION - ENEMY_SIZE;

    private JLabel levelLabel;

    private ImageIcon backgroundImage;
    private ImageIcon playerImage;
    private ImageIcon bulletImage;
    private ImageIcon enemyImage;
    private ImageIcon enemyBulletImage;
    private ImageIcon camicaseImage;
    private ImageIcon corazonImage;
    
    private boolean gameOver = false;
    
    private int squareX;
    private Timer gameTimer;
    private Timer shootTimer;
    private Timer enemyShootTimer;
    private Timer camicaseSpawnTimer;
    private int moveDirection;
    private boolean canShoot;
    private boolean isShooting;
    private int enemyDropCounter = 0;
    private List<Rectangle> projectiles;
    private List<Enemy> enemies;
    private List<Rectangle> enemyProjectiles;
    private List<Camicase> camicases;
    private int enemyDirection = ENEMY_SPEED;
    private int level = 1;
    private int lives = 3; 
    private Random random = new Random();
    int hitsToDestroy;

    public GamePanel() {
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        squareX = (GAME_WIDTH - SQUARE_SIZE) / 2;
        moveDirection = 0;
        canShoot = true;
        isShooting = false;
        projectiles = new ArrayList<>();
        enemies = new ArrayList<>();
        enemyProjectiles = new ArrayList<>();
        camicases = new ArrayList<>();

        setFocusable(true);
        requestFocusInWindow();

        initializeEnemies();

        gameTimer = new Timer(10, this::gameLoop);
        gameTimer.start();

        shootTimer = new Timer(500, e -> canShoot = true);

        enemyShootTimer = new Timer(ENEMY_SHOOT_INTERVAL, e -> {
            if (level > 1) {
                shootEnemyProjectiles();
            }
        });
        enemyShootTimer.start();
        
        camicaseSpawnTimer = new Timer(CAMICASE_SPAWN_INTERVAL, e -> {
            if (level >= 3) {
                spawnCamicase();
            }
        });
        camicaseSpawnTimer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver) { 
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        resetGame();
                    }
                } else {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_LEFT) moveDirection = -5;
                    else if (key == KeyEvent.VK_RIGHT) moveDirection = 5;
                    else if (key == KeyEvent.VK_SPACE) isShooting = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (!gameOver) {
                    if (key == KeyEvent.VK_LEFT && moveDirection == -5 ||
                        key == KeyEvent.VK_RIGHT && moveDirection == 5) moveDirection = 0;
                    else if (key == KeyEvent.VK_SPACE) isShooting = false;
                }
            }
        });
    
    
    backgroundImage = new ImageIcon(getClass().getClassLoader().getResource("resources/bg.gif"));
    
    bulletImage = new ImageIcon(getClass().getClassLoader().getResource("resources/bullet.png"));
    
    playerImage = new ImageIcon(getClass().getClassLoader().getResource("resources/player.png"));
    
    enemyImage = new ImageIcon(getClass().getClassLoader().getResource("resources/enemigoVioleta.png"));
    
    enemyBulletImage = new ImageIcon(getClass().getClassLoader().getResource("resources/enemy_bullet.png"));
    
    camicaseImage = new ImageIcon(getClass().getClassLoader().getResource("resources/camicase.PNG"));
    
    corazonImage = new ImageIcon(getClass().getClassLoader().getResource("resources/corazon.png"));
    
}


    private void initializeEnemies() {
        enemies.clear();
        camicases.clear();
        hitsToDestroy = (level >= 4) ? 2 : 1;
        int rows = level == 1 ? 3 : (level == 2 ? 4 : 4);
        int cols = level == 1 ? 6 : (level == 2 ? 5 : 6);
        int xOffset = 10;
        int yOffset = 40;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = xOffset + col * (ENEMY_SIZE + 10);
                int y = yOffset + row * (ENEMY_SIZE + 10);
                enemies.add(new Enemy(x, y, ENEMY_SIZE, ENEMY_SIZE, hitsToDestroy));
            }
        }
    }

    private void spawnCamicase() {
        if (camicases.size() < 5) { 
            int camicaseX = random.nextInt(GAME_WIDTH - ENEMY_SIZE);
            int camicaseY = -ENEMY_SIZE; 
            camicases.add(new Camicase(camicaseX, camicaseY, ENEMY_SIZE, ENEMY_SIZE, hitsToDestroy));
        }
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

    private void shootEnemyProjectiles() {
        for (Enemy enemy : enemies) {
            if (random.nextInt(100) < ENEMY_SHOOT_PROBABILITY) {
                int projectileX = enemy.x + (ENEMY_SIZE - PROJECTILE_SIZE) / 2;
                int projectileY = enemy.y + ENEMY_SIZE;
                enemyProjectiles.add(new Rectangle(projectileX, projectileY, PROJECTILE_SIZE, PROJECTILE_SIZE));
            }
        }
    }

    private void updateProjectiles() {
        projectiles.removeIf(projectile -> {
            projectile.y -= PROJECTILE_SPEED;
            return projectile.y + PROJECTILE_SIZE < 0;
        });
    }

    private void updateEnemyProjectiles() {
        enemyProjectiles.removeIf(projectile -> {
            projectile.y += PROJECTILE_SPEED;
            return projectile.y > GAME_HEIGHT;
        });
    }

    private void updateEnemies() {
        boolean hitEdge = false;

        for (Enemy enemy : enemies) {
            enemy.x += enemyDirection;
            if (enemy.x <= 0 || enemy.x + ENEMY_SIZE >= GAME_WIDTH) hitEdge = true;
        }

        if (hitEdge) {
            enemyDropCounter++;
            enemyDirection = -enemyDirection;

            if (enemyDropCounter >= ENEMY_DROP_THRESHOLD) {
                for (Enemy enemy : enemies) {
                    // Verificar si el siguiente movimiento hacia abajo excede el límite
                    if (enemy.y + ENEMY_DROP_DISTANCE <= MAX_ENEMY_Y) {
                        enemy.y += ENEMY_DROP_DISTANCE;
                    } else {
                        enemy.y = MAX_ENEMY_Y;
                    }
                }
                enemyDropCounter = 0;
            }
        }

        repaint();
    }
    
    public void setLevelLabel(JLabel levelLabel) {
        this.levelLabel = levelLabel;
    }

    

    private void updateLabels() {
        if (levelLabel != null) {
            levelLabel.setText("Nivel: " + level);
        }
    }

    private void updateCamicases() {
        List<Camicase> camicasesToRemove = new ArrayList<>();
        for (Camicase camicase : camicases) {
            camicase.y += CAMICASE_SPEED;
            if (camicase.y > GAME_HEIGHT) {
                camicasesToRemove.add(camicase);
            }
        }
        camicases.removeAll(camicasesToRemove);
    }

    private void checkCollisions() {
        List<Enemy> enemiesToRemove = new ArrayList<>();
        List<Rectangle> projectilesToRemove = new ArrayList<>();
        List<Camicase> camicasesToRemove = new ArrayList<>();
        List<Rectangle> enemyProjectilesToRemove = new ArrayList<>();

        Rectangle playerBounds = new Rectangle(squareX, SQUARE_Y_POSITION, SQUARE_SIZE, SQUARE_SIZE);

        // Verificar colisiones entre proyectiles del jugador y camicases
        for (Rectangle projectile : projectiles) {
            for (Camicase camicase : camicases) {
                Rectangle camicaseBounds = new Rectangle(camicase.x, camicase.y, camicase.width, camicase.height);
                if (projectile.intersects(camicaseBounds)) {
                    camicase.hit();
                    projectilesToRemove.add(projectile);
                    if (camicase.isDestroyed()) {
                        camicasesToRemove.add(camicase);
                    }
                }
            }
        }

        // Verificar colisiones entre proyectiles del jugador y enemigos
        for (Rectangle projectile : projectiles) {
            for (Enemy enemy : enemies) {
                Rectangle enemyBounds = new Rectangle(enemy.x, enemy.y, enemy.width, enemy.height);
                if (projectile.intersects(enemyBounds)) {
                    projectilesToRemove.add(projectile);
                    enemy.hit();
                    if (enemy.isDestroyed()) {
                        enemiesToRemove.add(enemy);
                    }
                }
            }
        }

        // Verificar colisiones entre proyectiles enemigos y el jugador
        for (Rectangle projectile : enemyProjectiles) {
            if (projectile.intersects(playerBounds)) {
                enemyProjectilesToRemove.add(projectile);
                loseLife();
            }
        }

        // Verificar colisiones entre camicases y el jugador
        for (Camicase camicase : camicases) {
            Rectangle camicaseBounds = new Rectangle(camicase.x, camicase.y, camicase.width, camicase.height);
            if (camicaseBounds.intersects(playerBounds)) {
                camicase.hit();
                if (camicase.isDestroyed()) {
                    camicasesToRemove.add(camicase);
                }
                loseLife();
            }
        }

        // Verificar colisiones entre enemigos y el jugador
        for (Enemy enemy : enemies) {
            Rectangle enemyBounds = new Rectangle(enemy.x, enemy.y, enemy.width, enemy.height);
            if (playerBounds.intersects(enemyBounds)) {
                loseLife();
                enemiesToRemove.add(enemy);
                break;
            }
        }

        
        enemies.removeAll(enemiesToRemove);
        camicases.removeAll(camicasesToRemove);
        projectiles.removeAll(projectilesToRemove);
        enemyProjectiles.removeAll(enemyProjectilesToRemove);

        
        if (enemies.isEmpty()) {
            nextLevel();
        }

        
        if (level >= 4) {
            for (Camicase camicase : camicases) {
                camicase.shootDiagonal(enemyProjectiles);
            }
        }
    }


    private void loseLife() {
        lives--;
        if (lives <= 0) {
            gameOver();
        }
        updateLabels();
    }

    private void nextLevel() {
        level++;
        initializeEnemies();
        updateLabels();
    }

    private void gameOver() {
        gameOver = true;
        gameTimer.stop();
        shootTimer.stop();
        enemyShootTimer.stop();
        camicaseSpawnTimer.stop();
        repaint(); // Llamar a repaint para mostrar la pantalla de Game Over
    }

    private void resetGame() {
        level = 1;
        lives = 3;
        squareX = (GAME_WIDTH - SQUARE_SIZE) / 2;
        moveDirection = 0;
        canShoot = true;
        isShooting = false;
        projectiles.clear();
        enemies.clear();
        enemyProjectiles.clear();
        camicases.clear();
        initializeEnemies();
        updateLabels();
        gameOver = false;
        gameTimer.start();
        shootTimer.start();
        enemyShootTimer.start();
        camicaseSpawnTimer.start();
    }

    private void gameLoop(ActionEvent e) {
        if (!gameOver) {
            moveSquare(moveDirection);
            if (isShooting) shootProjectile();
            updateProjectiles();
            updateEnemyProjectiles();
            updateEnemies();
            updateCamicases();
            checkCollisions();
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        
        g.drawImage(backgroundImage.getImage(), 0, 0, GAME_WIDTH, GAME_HEIGHT, this);

      
        g.drawImage(playerImage.getImage(), squareX, SQUARE_Y_POSITION, SQUARE_SIZE, SQUARE_SIZE, this);

  
        g.setColor(Color.YELLOW);
        for (Rectangle projectile : projectiles) {
            g.drawImage(bulletImage.getImage(), projectile.x, projectile.y, projectile.width, projectile.height, this);
        }

     
        for (Enemy enemy : enemies) {
            g.drawImage(enemyImage.getImage(), enemy.x, enemy.y, enemy.width, enemy.height, this);
        }

        
        g.setColor(Color.RED);
        for (Rectangle projectile : enemyProjectiles) {
            g.drawImage(enemyBulletImage.getImage(), projectile.x, projectile.y, projectile.width, projectile.height, this);
        }
        
        
        for (Camicase camicase : camicases) {
            g.drawImage(camicaseImage.getImage(), camicase.x, camicase.y, camicase.width, camicase.height, this);
        }
        
        for (int i = 0; i < lives; i++) {
            int heartX = GAME_WIDTH - (i + 1) * 40;
            int heartY = 10;
            g.drawImage(corazonImage.getImage(), heartX, heartY, 30, 30, null);
        }

        if (gameOver) {
            
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(0, 0, 0, 150)); 
            g2d.fillRect(0, 0, getWidth(), getHeight());

            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            FontMetrics fm = g2d.getFontMetrics();
            String message = "¡Has Perdido!";
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(message, x, y);

            
            String restartMessage = "Presiona Enter para reiniciar";
            x = (20 + getWidth() - fm.stringWidth(restartMessage)) / 4;
            y += fm.getHeight() + 20;
            g2d.drawString(restartMessage, x, y);

            g2d.dispose();
        }
    }

    private static class Enemy {
        int x, y, width, height;
        int hitsToDestroy;

        public Enemy(int x, int y, int width, int height, int hitsToDestroy) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.hitsToDestroy = hitsToDestroy;
        }

        public void hit() {
            hitsToDestroy--;
        }

        public boolean isDestroyed() {
            return hitsToDestroy <= 0;
        }
    }

    private static class Camicase {
        int x, y, width, height;
        int hitsToDestroy;
        int shotsReceived; // Para contar los disparos recibidos

        public Camicase(int x, int y, int width, int height, int hitsToDestroy) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.hitsToDestroy = hitsToDestroy;
            this.shotsReceived = 0; // Inicializa el contador de disparos
        }

        public void hit() {
            shotsReceived++;
            if (shotsReceived >= 2) { // Si recibe 2 disparos, se destruye
                hitsToDestroy--;
                shotsReceived = 0; // Reinicia el contador para el siguiente camicase
            }
        }

        public boolean isDestroyed() {
            return hitsToDestroy <= 0;
        }

        public void shootDiagonal(List<Rectangle> enemyProjectiles) {
            int projectileX1 = x + (width - PROJECTILE_SIZE) / 2 - 10;
            int projectileX2 = x + (width - PROJECTILE_SIZE) / 2 + 10;
            int projectileY = y + height;

            Rectangle projectile1 = new Rectangle(projectileX1, projectileY, PROJECTILE_SIZE, PROJECTILE_SIZE);
            Rectangle projectile2 = new Rectangle(projectileX2, projectileY, PROJECTILE_SIZE, PROJECTILE_SIZE);

            enemyProjectiles.add(projectile1);
            enemyProjectiles.add(projectile2);
        }
    }

}
