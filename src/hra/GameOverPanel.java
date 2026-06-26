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

public class GameOverPanel extends JPanel {

    GameFrame game;

    public GameOverPanel(GameFrame game) {
        this.game = game;
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.BLACK);

        // kliknuti pro reset hry
        setFocusable(true);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                restartGame();
            }
        });
    }

    private void restartGame() {
        game.resetGame(); // resetuje vse
        game.showScene("cottageScene"); 
    }

    public void startScene() {
        requestFocusInWindow();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // cerno
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 64));
        String title = "You fell in to a well!";
        int titleW = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (getWidth() - titleW) / 2, 280);

        // instrukce k restartu
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String hint = "Click to try again";
        int hintW = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, (getWidth() - hintW) / 2, 380);
    }
}