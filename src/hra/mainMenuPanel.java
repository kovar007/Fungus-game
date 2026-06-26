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


public class mainMenuPanel extends ScenePanel {
    Image background;
    Image bush;

    //zoan start tlacitka
    int btnX = 486, btnY = 462, btnW = 350, btnH = 110;

    public mainMenuPanel(GameFrame game) {
        super(game);
        background = new ImageIcon(getClass().getResource("/assets/Start.png")).getImage();
        bush = new ImageIcon(getClass().getResource("/assets/StartBushes.png")).getImage();
        setupMouseListener();
    }

    @Override
    protected void onMousePressed(int mx, int my) {
        // start
        if (mx >= btnX && mx <= btnX + btnW &&
            my >= btnY && my <= btnY + btnH) {
            game.showScene("cottageScene");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        game.player.draw(g, this, 250, 350); // vetsi velikost char
        g.drawImage(bush, 0, 0, getWidth(), getHeight(), this);
    }
}