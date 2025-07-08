import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainMenu extends JFrame {
    private JLabel startLabel, aboutLabel, quitLabel, logoLabel;
    private Image background;
    private static AudioPlayer bgm;

    public MainMenu() {
        setTitle("FireQuest 2D - Main Menu");
        setSize(1080, 620);  // Set ukuran window biar pas di layar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Kalau jendela ditutup, langsung keluar aplikasi
        setLocationRelativeTo(null);  // Posisikan jendela di tengah layar
        setResizable(false);  // Biar nggak bisa resize window
        setLayout(null);  // Biar bisa atur posisi komponen manual

        background = new ImageIcon("images/bgbutton.png").getImage();  // Masukin background gambar

        // Panel buat gambar background
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);  // Gambar background ke seluruh panel
            }
        };
        panel.setLayout(null);  // Layout manual supaya bisa atur posisi komponen
        setContentPane(panel);  // Set panel ini jadi konten utama

        Font font = new Font("Arial", Font.BOLD, 24);  // Font buat tulisan
        Color textColor = Color.WHITE;  // Warna teks putih biar jelas

        // Tambahin logo di layar utama
        ImageIcon logoIcon = new ImageIcon("images/logo.png");
        logoLabel = new JLabel(logoIcon);
        logoLabel.setBounds(290, 100, 500, 100);  // Set posisi dan ukuran logo
        panel.add(logoLabel);

        // Tombol START, waktu di klik buka GameMenu
        startLabel = createLabel("START", font, textColor, 460, 245, 150);
        startLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Kursor berubah jadi tangan
        startLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                SoundEffect.play("asset/klik.wav");  // Suara klik pas tombol ditekan
                dispose();  // Tutup MainMenu
                new GameMenu(bgm);  // Buka GameMenu
            }
        });

        // Tombol ABOUT US, buat tampilkan info tentang game
        aboutLabel = new JLabel("ABOUT US");
        aboutLabel.setFont(new Font("Arial", Font.BOLD, 24));  // Set font buat tulisan ABOUT US
        aboutLabel.setForeground(Color.WHITE);  // Tulisannya warna putih
        aboutLabel.setBounds(320, 340, 200, 40);  // Set posisi dan ukuran
        aboutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Kursor jadi tangan
        aboutLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                SoundEffect.play("asset/klik.wav");  // Suara klik saat ditekan
                new AboutUs(bgm);  // Buka jendela About Us
                dispose();  // Tutup MainMenu
            }
        });
        panel.add(aboutLabel);

        // Tombol QUIT, keluar dari aplikasi
        quitLabel = createLabel("QUIT", font, textColor, 550, 340, 250);
        quitLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Kursor jadi tangan
        quitLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                SoundEffect.play("asset/klik.wav");  // Suara klik saat ditekan
                System.exit(0);  // Keluar dari aplikasi
            }
        });

        // Masukin semua tombol ke panel
        panel.add(startLabel);
        panel.add(aboutLabel);
        panel.add(quitLabel);

        setVisible(true);  // Tampilin jendela

        // Kalau belum ada musik latar, putar musik latar
        if (bgm == null) {
            bgm = new AudioPlayer("asset/musiclatar.wav");
            bgm.setVolume(1.0f);  // Set volume musik
            bgm.playLoop();  // Putar musik latar terus-menerus
        }
    }

    // Method buat bikin label dengan parameter yang udah di set
    private JLabel createLabel(String text, Font font, Color color, int x, int y, int width) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);  // Bikin label dengan teks di tengah
        label.setFont(font);  // Set font buat tulisan
        label.setForeground(color);  // Set warna tulisan
        label.setBounds(x, y, width, 40);  // Set posisi dan ukuran label
        return label;
    }

    // Main method buat jalanin program
    public static void main(String[] args) {
        new MainMenu();  // Buka jendela MainMenu
    }
}
