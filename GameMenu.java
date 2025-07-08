// GameMenu.java
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class GameMenu extends JFrame {
    private JButton game1Button, game2Button, backButton;
    private JLabel logoLabel;
    private Image background;
    private AudioPlayer bgm;
    private JLayeredPane layeredPane; // Deklarasikan sebagai field

    // Deklarasikan JPanel untuk leaderboard
    private JPanel game1LeaderboardPanel;
    private JPanel game2LeaderboardPanel;

    public GameMenu(AudioPlayer bgm) {
        this.bgm = bgm;
        setTitle("FireQuest 2D - Game Menu");
        setSize(1024, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        background = new ImageIcon("images/gamemenu1.png").getImage();

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);
        setContentPane(panel);

        ImageIcon logoIcon = new ImageIcon("images/logo.png");
        logoLabel = new JLabel(logoIcon);
        logoLabel.setBounds(265, 40, 500, 100);
        panel.add(logoLabel);

        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1024, 600);
        panel.add(layeredPane);

        // Game1 Button
        ImageIcon game1Icon = new ImageIcon("images/game1.png");
        Image game1Img = game1Icon.getImage().getScaledInstance(360, 440, Image.SCALE_SMOOTH);
        game1Icon = new ImageIcon(game1Img);

        game1Button = new JButton(game1Icon);
        game1Button.setBounds(150, 80, 360, 440);
        game1Button.setContentAreaFilled(false);
        game1Button.setBorderPainted(false);
        game1Button.setFocusPainted(false);
        game1Button.setOpaque(false);
        layeredPane.add(game1Button, Integer.valueOf(0));

        // Panel untuk leaderboard Game1
        game1LeaderboardPanel = new JPanel();
        // UBAH POSISI Y DARI 340 MENJADI 390 (ATAU SESUAIKAN SESUAI KEBUTUHAN)
        game1LeaderboardPanel.setBounds(100, 430, 360, 100); //
        game1LeaderboardPanel.setLayout(new BorderLayout());
        game1LeaderboardPanel.setOpaque(false);
        layeredPane.add(game1LeaderboardPanel, Integer.valueOf(1));

        // Tampilkan leaderboard Game1
        displayLeaderboard(Game1.readLeaderboard(), game1LeaderboardPanel, "Game1");

        // Game2 Button
        ImageIcon game2Icon = new ImageIcon("images/game2.png");
        Image game2Img = game2Icon.getImage().getScaledInstance(397, 330, Image.SCALE_SMOOTH);
        game2Icon = new ImageIcon(game2Img);

        game2Button = new JButton(game2Icon);
        game2Button.setBounds(440, 135, 397, 330);
        game2Button.setContentAreaFilled(false);
        game2Button.setBorderPainted(false);
        game2Button.setFocusPainted(false);
        game2Button.setOpaque(false);
        layeredPane.add(game2Button, Integer.valueOf(0));

        // Panel untuk leaderboard GamePukulUlar
        game2LeaderboardPanel = new JPanel();
        // UBAH POSISI Y DARI 340 MENJADI 390 (ATAU SESUAIKAN SESUAI KEBUTUHAN)
        game2LeaderboardPanel.setBounds(440, 430, 397, 100); //
        game2LeaderboardPanel.setLayout(new BorderLayout());
        game2LeaderboardPanel.setOpaque(false);
        layeredPane.add(game2LeaderboardPanel, Integer.valueOf(1));

        // Tampilkan leaderboard GamePukulUlar
        displayLeaderboard(GamePukulUlar.readLeaderboard(), game2LeaderboardPanel, "Game2");

        // Tombol BACK
        ImageIcon backIcon = new ImageIcon("images/back.png");
        Image backImg = backIcon.getImage().getScaledInstance(240, 140, Image.SCALE_SMOOTH);
        final ImageIcon backIconScaled = new ImageIcon(backImg);

        backButton = new JButton(backIconScaled);
        backButton.setBounds(830, 10, 130, 70);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setOpaque(false);
        panel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffect.play("asset/klik.wav");
                dispose();
                new MainMenu();
            }
        });

        game1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffect.play("asset/klik.wav");
                if (bgm != null) bgm.stop();
                dispose();
                JFrame gameFrame = new JFrame("FireQuest - Game 1");
                Game1 gamePanel = new Game1();
                gameFrame.setContentPane(gamePanel);
                gameFrame.pack();
                gameFrame.setLocationRelativeTo(null);
                gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameFrame.setVisible(true);
            }
        });

        game2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundEffect.play("asset/klik.wav");
                if (bgm != null) bgm.stop();
                dispose();
                JFrame frame = new JFrame("Game Pukul Ular di Lumpur");
                GamePukulUlar gamePanel = new GamePukulUlar();
                frame.setContentPane(gamePanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });

        setVisible(true);
    }

    // Metode pembantu baru untuk menampilkan leaderboard di dalam JPanel
    private void displayLeaderboard(ArrayList<LeaderboardEntry> leaderboard, JPanel targetPanel, String gameName) {
        StringBuilder sb = new StringBuilder("<html><b>Top Skor " + gameName + ":</b><br>");
        if (leaderboard.isEmpty()) {
            sb.append("Belum ada skor.");
        } else {
            for (int i = 0; i < Math.min(5, leaderboard.size()); i++) { // Tampilkan 5 teratas
                LeaderboardEntry entry = leaderboard.get(i);
                sb.append((i + 1)).append(". ").append(entry.getName()).append(": ").append(entry.getScore()).append("<br>");
            }
        }
        sb.append("</html>");

        JLabel leaderboardLabel = new JLabel(sb.toString(), SwingConstants.CENTER);
        leaderboardLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        leaderboardLabel.setForeground(Color.YELLOW);

        targetPanel.removeAll();
        targetPanel.add(leaderboardLabel, BorderLayout.CENTER);
        targetPanel.revalidate();
        targetPanel.repaint();
    }
}