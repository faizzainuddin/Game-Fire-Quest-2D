import javax.swing.*;
import java.awt.*;

public class AboutUs extends JFrame {
    private AudioPlayer bgm;

    public AboutUs(AudioPlayer bgm) {
        this.bgm = bgm;
        setTitle("About Us - FireQuest 2D");
        setSize(1024, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        Image bg = new ImageIcon("images/aboutuss.jpg").getImage();

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);

        ImageIcon rawIcon = new ImageIcon("images/kita.png");
        Image scaledImg = rawIcon.getImage().getScaledInstance(170, 100, Image.SCALE_SMOOTH);
        JButton btnKita = new JButton(new ImageIcon(scaledImg));
        btnKita.setBounds(10, 10, 80, 80);
        btnKita.setBorderPainted(false);
        btnKita.setContentAreaFilled(false);
        btnKita.setFocusPainted(false);

        btnKita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                SoundEffect.play("asset/klik.wav");

                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setBackground(Color.WHITE);

                String fotoPath = "images/foto3f.jpg";
                ImageIcon fotoIcon = new ImageIcon(fotoPath);
                Image scaledFoto = fotoIcon.getImage().getScaledInstance(500, 400, Image.SCALE_SMOOTH);
                JLabel lblFoto = new JLabel(new ImageIcon(scaledFoto));
                lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
                infoPanel.add(lblFoto);

                infoPanel.add(Box.createVerticalStrut(15));

                JOptionPane.showMessageDialog(
                        AboutUs.this,
                        infoPanel,
                        "Profil Developer",
                        JOptionPane.PLAIN_MESSAGE
                );
            }
        });
        panel.add(btnKita);

        final int btnWidth = 130;
        final int btnHeight = 90;
        final ImageIcon backIcon = new ImageIcon("images/back.png");
        final ImageIcon backHoverIcon = new ImageIcon("images/back2.png");
        Image backImg = backIcon.getImage().getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
        final ImageIcon backIconScaled = new ImageIcon(backImg);
        Image backHoverImg = backHoverIcon.getImage().getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
        final ImageIcon backHoverIconScaled = new ImageIcon(backHoverImg);

        JButton aboutBackButton = new JButton(backIconScaled);
        aboutBackButton.setBounds(870, 10, btnWidth, btnHeight);
        aboutBackButton.setBorderPainted(false);
        aboutBackButton.setContentAreaFilled(false);
        aboutBackButton.setFocusPainted(false);
        aboutBackButton.setOpaque(false);
        panel.add(aboutBackButton);

        aboutBackButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                aboutBackButton.setIcon(backHoverIconScaled);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                aboutBackButton.setIcon(backIconScaled);
            }
        });

        aboutBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                SoundEffect.play("asset/klik.wav");
                dispose();
                new MainMenu();
            }
        });

        setContentPane(panel);
        setVisible(true);
    }
}
