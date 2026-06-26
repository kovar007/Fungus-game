/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hra;

/**
 *
 * @author kovarova
 */
import java.awt.*;
import javax.swing.*;
public class EndingPanel extends JPanel{
    Image stew;

    public EndingPanel(GameFrame game) {
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.BLACK);
        setFocusable(true);

        stew = new ImageIcon(getClass().getResource("/assets/Stew.png")).getImage();

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                // kliknutim zpet do menu
                game.resetGame();
                game.showScene("menu");
            }
        });
    }

    public void startScene() {
        requestFocusInWindow();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // cerne pozadi
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // stew obrazek v centru
        int imgW = 300, imgH = 300;
        int imgX = (getWidth() - imgW) / 2;
        g.drawImage(stew, imgX, 80, imgW, imgH, this);

        // ending text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 42));
        String title = "You finished the demo!";
        int titleW = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (getWidth() - titleW) / 2, 450);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String sub = "You made a 1 day blinding stew.";
        int subW = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, (getWidth() - subW) / 2, 510);

        // instrukce jak jit do menu
        g.setColor(new Color(180, 140, 80));
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String hint = "Click to return to main menu";
        int hintW = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, (getWidth() - hintW) / 2, 650);
    }
}
