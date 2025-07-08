import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Game1 extends JPanel implements ActionListener, KeyListener, MouseListener {
    private javax.swing.Timer timer;
    private int carX, carY;
    private final int PANEL_WIDTH = 1024, PANEL_HEIGHT = 600;
    private final int carWidth = 160, carHeight = 160;
    private BufferedImage playerImg, backgroundImg, heartImg, hpImg;
    private BufferedImage[] obstacleImages;
    private final ArrayList<Obstacle> obstacles = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private boolean isFinished = false;
    private int score = 0;
    private int lives = 3;
    private JButton btnGameOver;
    private JButton btnRestart;
    private JLabel gameOverLabel;
    private JLabel gameOverImageLabel;
    private final Random random = new Random();
    private AudioPlayer bgm;
    private AudioPlayer sirine;

    private boolean moveLeft = false;
    private boolean moveRight = false;

    private int speed = 2;
    private long startTime;
    private long lastSpeedupTime = 0;

    private int backgroundY1 = 0, backgroundY2;
    private final int roadLeft = 240;
    private final int roadRight = 790;

    public Game1() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);

        try {
            playerImg = ImageIO.read(new File("images/mobil2.png"));
            backgroundImg = ImageIO.read(new File("images/jalankota.png"));
            heartImg = ImageIO.read(new File("images/heart.png"));
            hpImg = ImageIO.read(new File("images/nyawa.png"));

            obstacleImages = new BufferedImage[] {
                ImageIO.read(new File("images/mobilterbakar.gif")),
                ImageIO.read(new File("images/mobilapi.png")),
                ImageIO.read(new File("images/pohon.png"))
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

        int bgHeight = backgroundImg != null ? backgroundImg.getHeight() : PANEL_HEIGHT;
        backgroundY2 = -bgHeight;

        carX = PANEL_WIDTH / 2 - carWidth / 2;
        carY = PANEL_HEIGHT - carHeight - 20;

        generateObstacles();

        setLayout(null);

        
        // Tombol untuk membuka Menu
        JButton btnMenu = new JButton("Menu");
        btnMenu.setBounds(PANEL_WIDTH - 120, 80, 100, 40); // Tempatkan tombol di bawah love (di kanan atas)
        add(btnMenu);
        setComponentZOrder(btnMenu, 0);

        // Aksi yang terjadi saat tombol Menu diklik
        btnMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Menghentikan permainan sementara (pause game)
                SoundEffect.play("asset/klik.wav"); // Efek suara klik
                bgm.stop(); // Menghentikan musik latar belakang game
                sirine.stop(); // Menghentikan suara sirine
                timer.stop(); // Menghentikan timer game

                // Menampilkan gambar menu jika diperlukan
                ImageIcon menuImage = new ImageIcon("images/menudigame.png"); // Ganti dengan path gambar yang sesuai
                Image menuImg = menuImage.getImage().getScaledInstance(300, 600, Image.SCALE_SMOOTH); // Menyesuaikan ukuran gambar
                menuImage = new ImageIcon(menuImg); // Menyimpan hasil gambar yang telah diperkecil

                JLabel menuImageLabel = new JLabel(menuImage);
                menuImageLabel.setBounds(PANEL_WIDTH / 2 - 150, PANEL_HEIGHT / 2 - 300, 300, 600); // Sesuaikan posisi dan ukuran gambar
                add(menuImageLabel); // Menambahkan gambar ke panel
                setComponentZOrder(menuImageLabel, 0); // Menampilkan gambar di depan elemen lainnya

                // Menyembunyikan tombol utama dan tombol game lainnya
                btnMenu.setVisible(false);

                // Tombol Resume untuk melanjutkan game (akan muncul setelah gambar ditampilkan)
                ImageIcon resumeIcon = new ImageIcon("images/resume.png"); // Ganti dengan path gambar untuk tombol Resume
                Image resumeImg = resumeIcon.getImage().getScaledInstance(150, 60, Image.SCALE_SMOOTH); // Mengubah ukuran gambar tombol Resume
                JButton btnResume = new JButton(new ImageIcon(resumeImg));
                btnResume.setBounds(PANEL_WIDTH / 2 - 75, PANEL_HEIGHT / 2 - 50, 150, 50); // Posisikan tombol Resume di tengah
                btnResume.setContentAreaFilled(false); // Menghilangkan background tombol
                btnResume.setBorderPainted(false); // Menghilangkan border
                add(btnResume);
                setComponentZOrder(btnResume, 0);

                // Tombol Quit untuk keluar dari game
                ImageIcon quitIcon = new ImageIcon("images/quit.png"); // Ganti dengan path gambar untuk tombol Quit
                Image quitImg = quitIcon.getImage().getScaledInstance(150, 70, Image.SCALE_SMOOTH); // Mengubah ukuran gambar tombol Quit
                JButton btnExit = new JButton(new ImageIcon(quitImg));
                btnExit.setBounds(PANEL_WIDTH / 2 - 75, PANEL_HEIGHT / 2 + 20, 150, 50); // Posisikan tombol Quit lebih dekat dengan Resume
                btnExit.setContentAreaFilled(false); // Menghilangkan background tombol
                btnExit.setBorderPainted(false); // Menghilangkan border
                add(btnExit);
                setComponentZOrder(btnExit, 0);

                btnResume.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Menyembunyikan gambar dan tombol Resume, serta melanjutkan permainan
                        menuImageLabel.setVisible(false);
                        btnResume.setVisible(false);
                        btnExit.setVisible(false);
                        btnMenu.setVisible(true); // Menampilkan tombol Menu kembali
                        bgm.playLoop(); // Memulai ulang musik latar belakang
                        sirine.playLoop(); // Memulai kembali suara sirine
                        timer.start(); // Memulai kembali timer game agar berjalan
                    }
                });

                btnExit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Membuat objek AudioPlayer untuk GameMenu
                        AudioPlayer bgmMenu = new AudioPlayer("asset/musiclatar.wav");
                        bgmMenu.setVolume(0.7f);
                        bgmMenu.playLoop();
                        
                        // Menutup jendela game dan membuka GameMenu
                        SwingUtilities.getWindowAncestor(Game1.this).dispose();
                        new GameMenu(bgmMenu); // Mengarahkan ke GameMenu dengan AudioPlayer sebagai argumen
                    }
                });

                repaint(); // Memperbarui tampilan game setelah menampilkan gambar
            }
        });

        // --- Tombol Game Menu ---
        ImageIcon menuRaw = new ImageIcon("images/gemmenu.png");
        Image menuImg = menuRaw.getImage().getScaledInstance(130, 100, Image.SCALE_SMOOTH);
        ImageIcon menuIcon = new ImageIcon(menuImg);
        btnGameOver = new JButton(menuIcon);
        btnGameOver.setBounds(PANEL_WIDTH / 2 - 130, PANEL_HEIGHT / 2 + 0, 120, 120);
        btnGameOver.setBorderPainted(false);
        btnGameOver.setContentAreaFilled(false);
        btnGameOver.setFocusPainted(false);
        btnGameOver.setOpaque(false);
        btnGameOver.setVisible(false);
        btnGameOver.setToolTipText("Kembali ke Menu");
        add(btnGameOver);
        setComponentZOrder(btnGameOver, 0);

        btnGameOver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffect.play("asset/klik.wav");
                bgm.stop();
                SwingUtilities.getWindowAncestor(Game1.this).dispose();

                AudioPlayer bgmMenu = new AudioPlayer("asset/musiclatar.wav");
                bgmMenu.setVolume(0.7f);
                bgmMenu.playLoop();
                new GameMenu(bgmMenu);
            }
        });

        // --- Tombol Restart (gambar) ---
        ImageIcon restartRaw = new ImageIcon("images/restart.png");
        Image restartImg = restartRaw.getImage().getScaledInstance(120, 97, Image.SCALE_SMOOTH);
        ImageIcon restartIcon = new ImageIcon(restartImg);
        btnRestart = new JButton(restartIcon);
        btnRestart.setBounds(PANEL_WIDTH / 2 + 0, PANEL_HEIGHT / 2 + 0, 120, 120);
        btnRestart.setBorderPainted(false);
        btnRestart.setContentAreaFilled(false);
        btnRestart.setFocusPainted(false);
        btnRestart.setOpaque(false);
        btnRestart.setVisible(false);
        btnRestart.setToolTipText("Restart Game");
        add(btnRestart);
        setComponentZOrder(btnRestart, 0);

        btnRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffect.play("asset/klik.wav");
                restartGame();
                sirine.playLoop();
            }
        });

        // Gambar Game Over (PNG transparan)
        ImageIcon gameOverIcon = new ImageIcon("images/gameover.png");
        Image imgGameOver = gameOverIcon.getImage().getScaledInstance(270, 170, Image.SCALE_SMOOTH);
        gameOverImageLabel = new JLabel(new ImageIcon(imgGameOver));
        gameOverImageLabel.setBounds(PANEL_WIDTH / 2 - 125, PANEL_HEIGHT / 2 - 160, 250, 150);
        gameOverImageLabel.setVisible(false);
        add(gameOverImageLabel);

        // Label GAME OVER (teks, tetap seperti biasa)
        gameOverLabel = new JLabel("", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 48));
        gameOverLabel.setForeground(Color.YELLOW);
        gameOverLabel.setBounds(PANEL_WIDTH / 2 - 200, PANEL_HEIGHT / 2 - 30, 400, 60);
        gameOverLabel.setVisible(false);
        add(gameOverLabel);

        timer = new javax.swing.Timer(30, this);
        timer.start();

        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        startTime = System.currentTimeMillis();
        lastSpeedupTime = startTime;
        bgm = new AudioPlayer("asset/soundgame1.wav");
        bgm.setVolume(0.0f);
        bgm.playLoop();
        sirine = new AudioPlayer("asset/sirine.wav");
        sirine.setVolume(1.0f); // volume lebih kecil agar tidak mengganggu
        sirine.playLoop();
    }

    private void restartGame() {
        carX = PANEL_WIDTH / 2 - carWidth / 2;
        carY = PANEL_HEIGHT - carHeight - 20;
        score = 0;
        lives = 3;
        isFinished = false;
        speed = 2;
        bullets.clear();
        obstacles.clear();
        generateObstacles();
        btnGameOver.setVisible(false);
        btnRestart.setVisible(false);
        gameOverLabel.setVisible(false);
        gameOverImageLabel.setVisible(false);
        timer.start();
        startTime = System.currentTimeMillis();
        lastSpeedupTime = startTime;
        repaint();
    }

    private void generateObstacles() {
        while (obstacles.size() < 4) {
            int x = roadLeft + random.nextInt(roadRight - roadLeft - 80);
            int y = -random.nextInt(600);
            if (!isOverlapping(x, y, 80)) {
                BufferedImage chosenImg = obstacleImages[random.nextInt(obstacleImages.length)];
                obstacles.add(new Obstacle(x, y, chosenImg));
            }
        }
    }

    private boolean isOverlapping(int newX, int newY, int size) {
        Rectangle newRect = new Rectangle(newX, newY, size, size);
        for (Obstacle o : obstacles) {
            Rectangle r = new Rectangle(o.x, o.y, o.size, o.size);
            if (newRect.intersects(r)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImg != null) {
            int bgHeight = backgroundImg.getHeight();
            g.drawImage(backgroundImg, 0, backgroundY1, PANEL_WIDTH, bgHeight, null);
            g.drawImage(backgroundImg, 0, backgroundY2, PANEL_WIDTH, bgHeight, null);
        }

        if (playerImg != null)
            g.drawImage(playerImg, carX, carY, carWidth, carHeight, null);

        // Draw obstacles + HP bar
        for (Obstacle o : obstacles) {
            if (o.image != null)
                g.drawImage(o.image, o.x, o.y, o.size, o.size, null);

            int hpIconW = 30, hpIconH = 5;
            int hpSpacing = 5;
            int totalWidth = o.health * hpIconW + (o.health - 1) * hpSpacing;
            int startX = o.x + (o.size - totalWidth) / 2;
            int hpY = o.y + o.size + 5;
            for (int i = 0; i < o.health; i++) {
                g.drawImage(hpImg, startX + i * (hpIconW + hpSpacing), hpY, hpIconW, hpIconH, null);
            }
        }

        g.setColor(Color.CYAN);
        for (Bullet b : bullets) {
            if (b.active) g.fillOval((int) b.x, (int) b.y, 10, 10);
        }

        for (int i = 0; i < lives; i++) {
            if (heartImg != null)
                g.drawImage(heartImg, 950 - i * 30, 20, 24, 24, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 20, 30);

        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        g.drawString("Time: " + elapsed + " s", 200, 30);
        g.drawString("Speed: " + speed, 350, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Pergerakan mobil smooth
        if (moveLeft) {
            carX -= 5;
            if (carX < roadLeft) carX = roadLeft;
        }
        if (moveRight) {
            carX += 5;
            if (carX > roadRight - carWidth) carX = roadRight - carWidth;
        }

        if (!isFinished) {
            long now = System.currentTimeMillis();
            if ((now - lastSpeedupTime) >= 15000) {
                speed++;
                lastSpeedupTime = now;
            }

            int bgHeight = backgroundImg != null ? backgroundImg.getHeight() : PANEL_HEIGHT;
            backgroundY1 += speed;
            backgroundY2 += speed;

            if (backgroundY1 >= PANEL_HEIGHT) backgroundY1 = backgroundY2 - bgHeight;
            if (backgroundY2 >= PANEL_HEIGHT) backgroundY2 = backgroundY1 - bgHeight;

            for (Obstacle o : obstacles) {
                o.move(speed);

                if (new Rectangle(o.x, o.y, o.size, o.size).intersects(new Rectangle(carX, carY, carWidth, carHeight))) {
                    SoundEffect.play("asset/nyawaberkurang.wav");
                    lives--;
                    if (lives <= 0) {
                        isFinished = true;
                        timer.stop();
                        SoundEffect.play("asset/gameover.wav");
                        if (sirine != null) sirine.stop();
                        btnGameOver.setVisible(true);
                        btnRestart.setVisible(true);
                        gameOverLabel.setVisible(true);
                        gameOverImageLabel.setVisible(true); // Tampilkan gambar Game Over!
                        updateHighscore(score); // Panggil updateHighscore yang baru
                        showLeaderboardPopup("FireQuest - Game 1"); // Panggil pop-up leaderboard
                        return;
                    }
                    int newX, newY;
                    do {
                        newX = roadLeft + random.nextInt(roadRight - roadLeft - o.size);
                        newY = -random.nextInt(600);
                    } while (isOverlapping(newX, newY, o.size));
                    BufferedImage newImg = obstacleImages[random.nextInt(obstacleImages.length)];
                    o.respawn(newX, newY, newImg);
                    carX = PANEL_WIDTH / 2 - carWidth / 2;
                }

                if (o.y > PANEL_HEIGHT) {
                    int newX, newY;
                    do {
                        newX = roadLeft + random.nextInt(roadRight - roadLeft - o.size);
                        newY = -random.nextInt(600);
                    } while (isOverlapping(newX, newY, o.size));
                    BufferedImage newImg = obstacleImages[random.nextInt(obstacleImages.length)];
                    o.respawn(newX, newY, newImg);
                }
            }

            Iterator<Bullet> it = bullets.iterator();
            while (it.hasNext()) {
                Bullet b = it.next();
                b.move();
                if (b.x < 0 || b.x > PANEL_WIDTH || b.y < 0 || b.y > PANEL_HEIGHT) b.active = false;

                for (Obstacle o : obstacles) {
                    if (b.getBounds().intersects(o.getBounds())) {
                        o.health--;
                        b.active = false;
                        if (o.health <= 0) {
                            int newX, newY;
                            do {
                                newX = roadLeft + random.nextInt(roadRight - roadLeft - o.size);
                                newY = -random.nextInt(600);
                            } while (isOverlapping(newX, newY, o.size));
                            BufferedImage newImg = obstacleImages[random.nextInt(obstacleImages.length)];
                            o.respawn(newX, newY, newImg);
                            score += 10;
                        }
                        break;
                    }
                    if(!b.active && b.getBounds().intersects(o.getBounds())) { // jika peluru mengenai objek tetapi sudah tidak aktif, jangan berikan skor
                        b.active = false; // pastikan ini tidak mengubah skor lagi
                    }
                }
            }

            bullets.removeIf(b -> !b.active);
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            moveLeft = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            moveRight = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            moveLeft = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            moveRight = false;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isFinished) {
            SoundEffect.play("asset/shot.wav");
            int centerX = carX + carWidth / 2;
            int centerY = carY;
            bullets.add(new Bullet(centerX, centerY, e.getX(), e.getY()));
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // --------------- HIGHSCORE I/O STATIC ---------------
    public static ArrayList<LeaderboardEntry> readLeaderboard() {
        ArrayList<LeaderboardEntry> leaderboard = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("highscore.dat"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    leaderboard.add(new LeaderboardEntry(name, score));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        Collections.sort(leaderboard);
        return leaderboard;
    }

    public static void updateLeaderboard(String userName, int newScore) {
        ArrayList<LeaderboardEntry> leaderboard = readLeaderboard();
        
        boolean found = false;
        for (LeaderboardEntry entry : leaderboard) {
            if (entry.getName().equalsIgnoreCase(userName)) {
                if (newScore > entry.getScore()) {
                    leaderboard.remove(entry);
                    leaderboard.add(new LeaderboardEntry(userName, newScore));
                }
                found = true;
                break;
            }
        }
        if (!found) {
            leaderboard.add(new LeaderboardEntry(userName, newScore));
        }
        
        Collections.sort(leaderboard);

        int maxEntries = 10;
        if (leaderboard.size() > maxEntries) {
            leaderboard = new ArrayList<>(leaderboard.subList(0, maxEntries));
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter("highscore.dat"))) {
            for (LeaderboardEntry entry : leaderboard) {
                pw.println(entry.getName() + "\t" + entry.getScore());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateHighscore(int newScore) {
        String userName = JOptionPane.showInputDialog(null, "Game Over! Masukkan nama Anda untuk leaderboard:", "Masukkan Nama", JOptionPane.PLAIN_MESSAGE);
        if (userName != null && !userName.trim().isEmpty()) {
            updateLeaderboard(userName.trim(), newScore);
        } else {
            updateLeaderboard("Anonim", newScore);
        }
    }

    // Metode baru untuk menampilkan pop-up leaderboard
    private void showLeaderboardPopup(String gameName) {
        ArrayList<LeaderboardEntry> leaderboard = readLeaderboard(); // Membaca leaderboard untuk Game1

        JPanel popupPanel = new JPanel();
        popupPanel.setLayout(new BoxLayout(popupPanel, BoxLayout.Y_AXIS));
        popupPanel.setBackground(new Color(0, 0, 0, 180)); // Warna semi-transparan hitam
        popupPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        JLabel titleLabel = new JLabel("<html><font color='yellow'>TOP SKOR " + gameName.toUpperCase() + "</font></html>", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        popupPanel.add(titleLabel);
        popupPanel.add(Box.createVerticalStrut(10)); // Spasi vertikal

        if (leaderboard.isEmpty()) {
            JLabel noScoreLabel = new JLabel("<html><font color='white'>Belum ada skor yang tercatat.</font></html>", SwingConstants.CENTER);
            noScoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            noScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            popupPanel.add(noScoreLabel);
        } else {
            for (int i = 0; i < Math.min(5, leaderboard.size()); i++) { // Tampilkan 5 teratas
                LeaderboardEntry entry = leaderboard.get(i);
                JLabel scoreEntryLabel = new JLabel(
                    "<html><font color='white'>" + (i + 1) + ". " + entry.getName() + ": " + entry.getScore() + "</font></html>",
                    SwingConstants.CENTER
                );
                scoreEntryLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                scoreEntryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                popupPanel.add(scoreEntryLabel);
            }
        }
        popupPanel.add(Box.createVerticalStrut(10)); // Spasi vertikal

        // Tampilkan JOptionPane
        JOptionPane.showMessageDialog(
            this, // Parent component
            popupPanel, // Isi panel
            "Leaderboard " + gameName, // Judul pop-up
            JOptionPane.PLAIN_MESSAGE
        );
    }
}

class Obstacle {
    int x, y, size = 80, health = 3;
    BufferedImage image;

    public Obstacle(int x, int y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public Rectangle getBounds() { return new Rectangle(x, y, size, size); }

    public void respawn(int newX, int newY, BufferedImage newImage) {
        this.x = newX;
        this.y = newY;
        this.health = 3;
        this.image = newImage;
    }

    public void move(int dy) { y += dy; }
}

class Bullet {
    double x, y, dx, dy;
    boolean active = true;

    public Bullet(int startX, int startY, int targetX, int targetY) {
        this.x = startX; 
        this.y = startY;
        double angle = Math.atan2(targetY - startY, targetX - startX);
        double speed = 10;
        this.dx = speed * Math.cos(angle);
        this.dy = speed * Math.sin(angle);
    }

    public void move() { x += dx; y += dy; }
    public Rectangle getBounds() { return new Rectangle((int)x, (int)y, 10, 10); }
}