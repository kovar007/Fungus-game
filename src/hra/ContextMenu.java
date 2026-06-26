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
import java.util.ArrayList;

public class ContextMenu {
    public static class MenuOption {     // trida ve tride, jedna polozka v context menu
        public String label;
        public Runnable action;   // co se stane po kliknuti

        public MenuOption(String label, Runnable action) {
            this.label = label;
            this.action = action;
        }
    }

    private ArrayList<MenuOption> options = new ArrayList<>();       //seznam aktualnich moznosti v menu
    private int x, y;                       // pozice na obrazovce (misto kliknuti)
    private boolean visible = false;

    int optionWidth  = 200;          // rozmery tlacitka
    int optionHeight = 40;          
    int padding = 8;         // mezera mezi moznostmi

    public void show(int x, int y, MenuOption... opts) {
        this.x = x;           //ulozi pozici kliknuti
        this.y = y;
        options.clear();   // vymazani starych moznosti
        for (MenuOption o : opts) 
            options.add(o);       // pridani novych moznosti do seznamu
        visible = true;        // zapne zobrazeni
    }

    public void hide() {
        visible = false;         // schova menu, vymaze obsah
        options.clear();
    }

    public boolean isVisible() {
        return visible;
    }

    // zpracovani kliknuti mysi
    public boolean handleClick(int mx, int my) {
        if (!visible) return false;     // menu zavrene -> niiiiic

        for (int i = 0; i < options.size(); i++) {          // prochazeni moznosti
            int optX = x;
            int optY = y + i * (optionHeight + padding);  // vypocet konkr. tlacitka
            
            if (mx >= optX && mx <= optX + optionWidth &&
                my >= optY && my <= optY + optionHeight) {   //kliknulo se na tlacitko?
                options.get(i).action.run();  //spusteni nastavene akce
                
                hide();    //zavre menu
                return true;  // spotrebovani kliknuti i kdyz se nic nevybralo, mezera tlacitek atd.
            } 
        }

        // klik mimo -> schovat
        hide();
        return true; // spotreb. kliknuti
    }

    public void draw(Graphics g) {
        if (!visible) return;      // neviditelne -> nic se nekresli

        for (int i = 0; i < options.size(); i++) {
            int optX = x;
            int optY = y + i * (optionHeight + padding);

            // pozadi tlacitka
            g.setColor(new Color(30, 20, 10, 220));
            g.fillRoundRect(optX, optY, optionWidth, optionHeight, 10, 10);

            // overline tlacitka
            g.setColor(new Color(180, 140, 80));
            g.drawRoundRect(optX, optY, optionWidth, optionHeight, 10, 10);

            // text
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString(options.get(i).label, optX + 15, optY + 26);
        }
    }
}
