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
import java.util.ArrayList;

public class Inventory {

    public static class Item {         // podtrida s itemama
        public String name;
        public Image icon;

        public Item(String name, String iconPath, Class<?> loader) {       // nacteni obrazku itemu v inventari
            this.name = name;
            try {
                icon = new ImageIcon(loader.getResource(iconPath)).getImage();
            } catch (Exception e) {
                System.out.println("Failed to load icon: " + iconPath);
            }
        }
    }

    private ArrayList<Item> items = new ArrayList<>();       //seznam itemu v invu
    public boolean isOpen = false;

    Image bagIcon;

    int panelX = 200, panelY = 150;
    int panelWidth = 880, panelHeight = 420;
    int slotSize = 120;
    int slotPadding = 20;

    public Inventory(Class<?> loader) {     // nacteni ikony tasky
        bagIcon = new ImageIcon(loader.getResource("/assets/Bag.png")).getImage();
    }

    public void addItem(String name, String iconPath, Class<?> loader) {
        if (!hasItem(name)) {
            items.add(new Item(name, iconPath, loader));        // pridani itemu
        }
    }

    public void removeItem(String name) {
        items.removeIf(item -> item.name.equals(name));   // odstraneni
    }

    public boolean hasItem(String name) {
        return items.stream().anyMatch(item -> item.name.equals(name));
    }

    public boolean bagClicked(int mx, int my) {
        return mx >= 20 && mx <= 110 && my >= 20 && my <= 110;
    }

    public void draw(Graphics g, Component c) {
        // vzdy bag ikona
        g.drawImage(bagIcon, 20, 20, 90, 90, c);

        if (!isOpen) return; //je zavreno

        // pozadi panelu
        g.setColor(new Color(30, 20, 10, 220));
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        g.setColor(new Color(180, 140, 80));
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        // nadpis
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Inventory", panelX + 20, panelY + 40);

        // je prazdno?
        if (items.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(new Color(180, 140, 80));
            g.drawString("Your bag is empty.", panelX + 20, panelY + 100);
            return;
        }

        // neni prazdno, sloty
        int startX = panelX + slotPadding;
        int startY = panelY + 70;

        for (int i = 0; i < items.size(); i++) {
            int slotX = startX + i * (slotSize + slotPadding);
            int slotY = startY;

            // pozadi slotu
            g.setColor(new Color(80, 60, 30, 180));
            g.fillRoundRect(slotX, slotY, slotSize, slotSize, 10, 10);
            g.setColor(new Color(180, 140, 80));
            g.drawRoundRect(slotX, slotY, slotSize, slotSize, 10, 10);

            // ikony
            if (items.get(i).icon != null) {
                g.drawImage(items.get(i).icon, slotX + 5, slotY + 5,
                    slotSize - 10, slotSize - 10, c);
            }

            // jmeno itemu
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 11));
            g.drawString(items.get(i).name, slotX + 5, slotY + slotSize + 15);
        }

        // instrukce na zavreni
        g.setColor(new Color(180, 140, 80));
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Click anywhere to close", panelX + panelWidth - 200, panelY + panelHeight - 15);
    }
}