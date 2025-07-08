import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePukulUlar extends JPanel implements MouseMotionListener, MouseListener {
    private JLabel scoreLabel, ularLabel, kucingLabel;
    private JButton startButton, gameOverButton, restartButton;
    private int score = 0;
    private Timer timer;
    private Random random = new Random();
    private AudioPlayer bgm;

    private static final int PANEL_WIDTH = 1024;
    private static final int PANEL_HEIGHT = 600;

    private BufferedImage hutanImg;
    private Image tanahImg;
    private JLabel[] tanahLabels = new JLabel[6];

    private final int MAX_NYAWA = 3;
    private int nyawa = MAX_NYAWA;
    private JLabel[] nyawaLabels = new JLabel[MAX_NYAWA];
    private ImageIcon nyawaIcon, nyawaHilangIcon;

    private Timer ularNaikTimer, kucingNaikTimer;
    private int currentTanahIdx = -1;
    private boolean objekDipukul = false;
    private boolean objekSekarangKucing = false;

    private ImageIcon ularIcon, kucingIcon;

    // STATE
    private boolean isFinished = false;
    private boolean newHighscore = false;

    private Timer elapsedTimer;
    private int elapsedSeconds = 0;
    private JLabel timeLabel;

    private int gameSpeed = 1500; // Initial spawn interval (milliseconds)

    public static ArrayList<LeaderboardEntry> readLeaderboard() {
        ArrayList<LeaderboardEntry> leaderboard = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("highscore_ular.dat"))) {
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

        try (PrintWriter pw = new PrintWriter(new FileWriter("highscore_ular.dat"))) {
            for (LeaderboardEntry entry : leaderboard) {
                pw.println(entry.getName() + "\t" + entry.getScore());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GamePukulUlar() {
        setLayout(null);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(34, 139, 34));
        setDoubleBuffered(true);

        try { hutanImg = ImageIO.read(new File("asset/hutan4.png")); }
        catch (IOException e) { hutanImg = null; }

        JLabel titleLabel = new JLabel("Pukul Ular di Lumpur", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBounds(0, 10, PANEL_WIDTH, 40);
        add(titleLabel);

        // ====== TOMBOL START GAMBAR ======
        ImageIcon startIcon = new ImageIcon("images/start.png"); // Pastikan path gambar benar!
        Image imgStart = startIcon.getImage().getScaledInstance(160, 130, Image.SCALE_SMOOTH);
        ImageIcon resizedStartIcon = new ImageIcon(imgStart);
        startButton = new JButton(resizedStartIcon);
        startButton.setBounds(PANEL_WIDTH / 2 - 80, 40, 160, 70);
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setFocusPainted(false);
        startButton.setOpaque(false);
        startButton.setToolTipText("Mulai Game");
        add(startButton);

        scoreLabel = new JLabel("0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 48));
        scoreLabel.setBounds(PANEL_WIDTH / 2 - 50, 100, 100, 60);
        add(scoreLabel);

        timeLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        timeLabel.setBounds(PANEL_WIDTH - 160, 20, 150, 40);
        add(timeLabel);

        // Nyawa
        nyawaIcon = new ImageIcon(new ImageIcon("asset/darah.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        nyawaHilangIcon = new ImageIcon(new ImageIcon("asset/darah2.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        for (int i = 0; i < MAX_NYAWA; i++) {
            nyawaLabels[i] = new JLabel(nyawaIcon);
            nyawaLabels[i].setBounds(10 + i * 32, 10, 30, 30);
            add(nyawaLabels[i]);
        }

        // Area tanah
        int[] tanahX = {200, 430, 640, 120, 430, 735};
        int[] tanahY = {320, 310, 320, 440, 470, 440};
        tanahImg = new ImageIcon("asset/tanah.png").getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);

        // Icon ular & kucing
        ularIcon = new ImageIcon(new ImageIcon("asset/ular5.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        kucingIcon = new ImageIcon(new ImageIcon("asset/kucing.png").getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));

        ularLabel = new JLabel(ularIcon);
        ularLabel.setBounds(0, 0, 100, 100);
        ularLabel.setVisible(false);
        add(ularLabel);

        kucingLabel = new JLabel(kucingIcon);
        kucingLabel.setBounds(0, 0, 90, 90);
        kucingLabel.setVisible(false);
        add(kucingLabel);

        for (int i = 0; i < tanahLabels.length; i++) {
            tanahLabels[i] = new JLabel(new ImageIcon(tanahImg));
            tanahLabels[i].setBounds(tanahX[i], tanahY[i], 150, 120);
            add(tanahLabels[i]);
        }

        addMouseMotionListener(this);
        addMouseListener(this);

        // Musik latar dengan path yang benar
        bgm = new AudioPlayer("assets/musicular.wav");
        bgm.setVolume(1.0f);
        bgm.playLoop();

        // Event Mouse
        ularLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!objekDipukul && ularLabel.isVisible() && !isFinished) {
                    SoundEffect.play("asset/memukul.wav");
                    score++;
                    scoreLabel.setText(String.valueOf(score));
                    objekDipukul = true;
                    ularLabel.setVisible(false);
                }
            }
        });

        kucingLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!objekDipukul && kucingLabel.isVisible() && !isFinished) {
                    SoundEffect.play("asset/memukul.wav");
                    kurangiNyawa();
                    objekDipukul = true;
                    kucingLabel.setVisible(false);
                }
            }
        });

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SoundEffect.play("asset/klik.wav");
                startGame();
            }
        });

        // --- Tombol Game Menu (gambar) ---
        ImageIcon menuRaw = new ImageIcon("images/gemmenu.png");
        Image menuImg = menuRaw.getImage().getScaledInstance(130, 100, Image.SCALE_SMOOTH);
        ImageIcon menuIcon = new ImageIcon(menuImg);
        gameOverButton = new JButton(menuIcon);
        gameOverButton.setBounds(PANEL_WIDTH / 2 - 120, PANEL_HEIGHT / 2 + 40, 120, 120);
        gameOverButton.setBorderPainted(false);
        gameOverButton.setContentAreaFilled(false);
        gameOverButton.setFocusPainted(false);
        gameOverButton.setOpaque(false);
        gameOverButton.setVisible(false);
        gameOverButton.setToolTipText("Kembali ke Menu");
        add(gameOverButton);
        setComponentZOrder(gameOverButton, 0);

        gameOverButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SoundEffect.play("asset/klik.wav");
                bgm.stop();
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(GamePukulUlar.this);
                topFrame.dispose();

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

        restartButton = new JButton(restartIcon);
        restartButton.setBounds(PANEL_WIDTH / 2 + 0, PANEL_HEIGHT / 2 + 40, 120, 120);
        restartButton.setBorderPainted(false);
        restartButton.setContentAreaFilled(false);
        restartButton.setFocusPainted(false);
        restartButton.setOpaque(false);
        restartButton.setVisible(false);
        restartButton.setToolTipText("Restart Game");
        add(restartButton);
        setComponentZOrder(restartButton, 0);

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SoundEffect.play("asset/klik.wav");
                startGame();
            }
        });

        // Z-ORDER
        for (int i = 0; i < tanahLabels.length; i++) setComponentZOrder(tanahLabels[i], 0);
        for (int i = 0; i < MAX_NYAWA; i++) setComponentZOrder(nyawaLabels[i], 0);
        setComponentZOrder(gameOverButton, 0);
        setComponentZOrder(restartButton, 0);
        bgm = new AudioPlayer("asset/soundgame2.wav");
        bgm.setVolume(0.8f);
        bgm.playLoop();
    }

    private void startGame() {
        score = 0;
        nyawa = MAX_NYAWA;
        isFinished = false;
        newHighscore = false;
        updateIconNyawa();
        scoreLabel.setText("0");
        startButton.setEnabled(false);
        gameOverButton.setVisible(false);
        restartButton.setVisible(false);
        ularLabel.setVisible(false);
        kucingLabel.setVisible(false);
        objekDipukul = false;

        // Reset timer waktu maju
        elapsedSeconds = 0;
        timeLabel.setText("Time: 0s");
        if (elapsedTimer != null) elapsedTimer.stop();
        elapsedTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                elapsedSeconds++;
                timeLabel.setText("Time: " + elapsedSeconds + "s");

                // Setiap 15 detik, percepat spawn objek
                if (elapsedSeconds % 15 == 0 && gameSpeed > 300) {
                    gameSpeed -= 100; // Mengurangi interval spawn objek setiap 15 detik
                    startGameTimer(); // Restart the spawn timer with the updated speed
                }
            }
        });
        elapsedTimer.start();

        munculObjek();
        startGameTimer();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (hutanImg != null) {
            g.drawImage(hutanImg, 0, 0, getWidth(), getHeight(), null);
        }
        if (isFinished) {
            // Tulis GAME OVER
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 64));
            String msg = "GAME OVER";
            int textWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (PANEL_WIDTH - textWidth) / 2, PANEL_HEIGHT / 2 - 30);

            g.setFont(new Font("Arial", Font.BOLD, 32));
            String scoreMsg = "Score: " + score;
            int scoreWidth = g.getFontMetrics().stringWidth(scoreMsg);
            g.drawString(scoreMsg, (PANEL_WIDTH - scoreWidth) / 2, PANEL_HEIGHT / 2 + 10);
        }
    }

    // Tampilkan objek random: ular atau kucing (peluang kucing 30%)
    private void munculObjek() {
        if (!objekDipukul && timer != null && timer.isRunning() && !objekSekarangKucing) {
            kurangiNyawa();
        }
        objekDipukul = false;
        objekSekarangKucing = (random.nextInt(100) < 30);

        int idx;
        do {
            idx = random.nextInt(tanahLabels.length);
        } while (idx == currentTanahIdx);
        currentTanahIdx = idx;
        Rectangle lokasi = tanahLabels[idx].getBounds();
        int x = lokasi.x + (lokasi.width - ularLabel.getWidth()) / 2;
        int yTanah = lokasi.y;
        int yBawah = yTanah + 5;
        int yAtas = yTanah - ularLabel.getHeight() + 55;

        if (objekSekarangKucing) {
            kucingLabel.setLocation(x, yBawah);
            kucingLabel.setVisible(true);
            ularLabel.setVisible(false);
            if (kucingNaikTimer != null && kucingNaikTimer.isRunning()) kucingNaikTimer.stop();
            final int[] yPos = { yBawah };
            kucingNaikTimer = new Timer(10, null);
            kucingNaikTimer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (yPos[0] > yAtas) {
                        yPos[0] -= 3;
                        kucingLabel.setLocation(x, yPos[0]);
                    } else {
                        kucingLabel.setLocation(x, yAtas);
                        kucingNaikTimer.stop();
                    }
                }
            });
            kucingNaikTimer.start();
        } else {
            ularLabel.setLocation(x, yBawah);
            ularLabel.setVisible(true);
            kucingLabel.setVisible(false);
            if (ularNaikTimer != null && ularNaikTimer.isRunning()) ularNaikTimer.stop();
            final int[] yPos = { yBawah };
            ularNaikTimer = new Timer(10, null);
            ularNaikTimer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (yPos[0] > yAtas) {
                        yPos[0] -= 3;
                        ularLabel.setLocation(x, yPos[0]);
                    } else {
                        ularLabel.setLocation(x, yAtas);
                        ularNaikTimer.stop();
                    }
                }
            });
            ularNaikTimer.start();
        }
    }

    private void kurangiNyawa() {
        if (nyawa > 0) {
            SoundEffect.play("asset/nyawaberkurang.wav");
            nyawa--;
            updateIconNyawa();
            if (nyawa == 0) {
                if (timer != null) timer.stop();
                if (elapsedTimer != null) elapsedTimer.stop();
                isFinished = true;
                SoundEffect.play("asset/gameover.wav");
                updateHighscore(score); // Panggil updateHighscore yang baru
                int hs = readLeaderboard().isEmpty() ? 0 : readLeaderboard().get(0).getScore();
                newHighscore = (score >= hs);
                ularLabel.setVisible(false);
                kucingLabel.setVisible(false);
                gameOverButton.setVisible(true);
                restartButton.setVisible(true);
                startButton.setEnabled(true);
                repaint();
                showLeaderboardPopup("Game Pukul Ular di Lumpur"); // Panggil pop-up leaderboard
            }
        }
    }

    private void updateIconNyawa() {
        for (int i = 0; i < MAX_NYAWA; i++)
            nyawaLabels[i].setIcon(i < nyawa ? nyawaIcon : nyawaHilangIcon);
    }

    private void startGameTimer() {
        if (timer != null) timer.stop();
        timer = new Timer(gameSpeed, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                munculObjek();
            }
        });
        timer.start();
    }

    // Mouse events
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

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
        ArrayList<LeaderboardEntry> leaderboard = readLeaderboard(); // Membaca leaderboard untuk GamePukulUlar

        JPanel popupPanel = new JPanel();
        popupPanel.setLayout(new BoxLayout(popupPanel, BoxLayout.Y_AXIS));
        popupPanel.setBackground(new Color(0, 0, 0, 180)); // Warna semi-transparan hitam
        popupPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        JLabel titleLabel = new JLabel("<html><font color='yellow'>TOP SKOR " + gameName.toUpperCase() + "</font></html>", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        popupPanel.add(titleLabel);
        popupPanel.add(Box.createVerticalStrut(10));

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
        popupPanel.add(Box.createVerticalStrut(10));

        // Tampilkan JOptionPane
        JOptionPane.showMessageDialog(
            this, // Parent component
            popupPanel, // Isi panel
            "Leaderboard " + gameName, // Judul pop-up
            JOptionPane.PLAIN_MESSAGE
        );
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Game Pukul Ular di Lumpur");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new GamePukulUlar());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}