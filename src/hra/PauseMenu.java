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
public class PauseMenu {
     private boolean paused = false;
    private Image pauseIcon;

    // pause tlacitko pozice
    int iconX = 1180, iconY = 20, iconSize = 60;

    // velikost panelu
    int panelW = 400, panelH = 320;
    int btnW = 300, btnH = 50;

    public PauseMenu(Class<?> loader) {
        pauseIcon = new ImageIcon(loader.getResource("/assets/PauseIcon.png")).getImage();     //ikona stop
    }

    public boolean isOpen() {
        return paused;
    }

    public boolean iconClicked(int mx, int my) {
        return mx >= iconX && mx <= iconX + iconSize &&
               my >= iconY && my <= iconY + iconSize;
    }

    public void toggle() {
        paused = !paused;         // prepnuti stavu pauzy
    }

    public void close() {
        paused = false;      //k zavreni menu
    }

    // zpracovani kliknuti pri zastaveni hry
    // vraci menu, restart a mnic (klik mimo)
    public String handleClick(int mx, int my, int screenW, int screenH) {
        if (!paused) return null;      // nic, hra jede

        int panelX = (screenW - panelW) / 2;
        int panelY = (screenH - panelH) / 2;          //centrovani
        int btnX   = panelX + (panelW - btnW) / 2;

        // main menu
        int btn1Y = panelY + 110;
        if (mx >= btnX && mx <= btnX + btnW &&
            my >= btn1Y && my <= btn1Y + btnH) {
            return "menu";
        }

        // restart 
        int btn2Y = panelY + 190;
        if (mx >= btnX && mx <= btnX + btnW &&
            my >= btn2Y && my <= btn2Y + btnH) {
            return "restart";
        }

        return null;
    }

    public void draw(Graphics g, Component c) {
        // vzdy ikona pause
        g.drawImage(pauseIcon, iconX, iconY, iconSize, iconSize, c);

        if (!paused) return;     //hra jede -> nic

        int screenW = c.getWidth();
        int screenH = c.getHeight();

        // tmavy overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, screenW, screenH);

        // stred menu
        int panelX = (screenW - panelW) / 2;
        int panelY = (screenH - panelH) / 2;

        // pozadi panelu
        g.setColor(new Color(30, 20, 10, 240));
        g.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        // border panelu
        g.setColor(new Color(180, 140, 80));
        g.drawRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        // napis
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        String title = "Paused";
        int titleW = g.getFontMetrics().stringWidth(title);       //centrovani
        g.drawString(title, panelX + (panelW - titleW) / 2, panelY + 60);

        // oddelovaci cara
        g.setColor(new Color(180, 140, 80));
        g.drawLine(panelX + 30, panelY + 75, panelX + panelW - 30, panelY + 75);

        int btnX = panelX + (panelW - btnW) / 2; //centrovani

        // main menu
        int btn1Y = panelY + 110;
        g.setColor(new Color(80, 60, 30, 220));
        g.fillRoundRect(btnX, btn1Y, btnW, btnH, 10, 10);
        g.setColor(new Color(180, 140, 80));
        g.drawRoundRect(btnX, btn1Y, btnW, btnH, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String btn1 = "Main Menu";
        int btn1W = g.getFontMetrics().stringWidth(btn1);
        g.drawString(btn1, btnX + (btnW - btn1W) / 2, btn1Y + 33);

        // restart
        int btn2Y = panelY + 190;
        g.setColor(new Color(80, 60, 30, 220));
        g.fillRoundRect(btnX, btn2Y, btnW, btnH, 10, 10);
        g.setColor(new Color(180, 140, 80));
        g.drawRoundRect(btnX, btn2Y, btnW, btnH, 10, 10);
        g.setColor(Color.WHITE);
        String btn2 = "Restart";
        int btn2W = g.getFontMetrics().stringWidth(btn2);
        g.drawString(btn2, btnX + (btnW - btn2W) / 2, btn2Y + 33);

        // instrukce
        g.setColor(new Color(180, 140, 80));
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        String hint = "or press ESC";
        int hintW = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, panelX + (panelW - hintW) / 2, panelY + panelH - 20);
    }
}
