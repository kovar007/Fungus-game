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

public class WellFallPanel extends JPanel {
    GameFrame game;
    Image background;
    Image characterImage;

    // padajici char
    float charY;
    float charX;
    double rotation = 0;

    Timer animTimer;
    Timer sceneTimer;

    public WellFallPanel(GameFrame game) {
        this.game = game;
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.BLACK);

        background = new ImageIcon(
            getClass().getResource("/assets/WellFall.png")
        ).getImage();

        // staticky obrazek
        characterImage = new ImageIcon(
            getClass().getResource("/assets/CharF.png")
        ).getImage();

        // animace - pohyb a otoceni
        animTimer = new Timer(1000 / 60, e -> {
            charY      += 6;    // rychlost padu
            rotation   += 6;    // rychlost otacek
            repaint();
        });

        // cekani chvili po vybrani moznosti smrti
        sceneTimer = new Timer(3500, e -> {
            sceneTimer.stop();
            animTimer.stop();
            game.showScene("gameOverScene");
        });
        sceneTimer.setRepeats(false);
    }

    public void startScene() {
        charX    = (1280 / 2) - 75; // centerovano, 75 = polovina sirky
        charY    = -200;             // zobrazeni nejprve nad scenou
        rotation = 0;
        animTimer.start();
        sceneTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // pozadi
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        // pad
        Graphics2D g2d = (Graphics2D) g;
        g2d.rotate(Math.toRadians(rotation),
            charX + 75,  // otaceni okolo centra
            charY + 100);

        g2d.drawImage(characterImage,
            (int) charX, (int) charY, 150, 200, this);

        // reset otacek
        g2d.rotate(-Math.toRadians(rotation),
            charX + 75,
            charY + 100);
    }
}