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

public class GameObject {
    int x, y;      //souradnice objektu
    Image image;
    int width, height;

    public GameObject(int x, int y, String path) {
        this.x = x;
        this.y = y;

        image = new ImageIcon(getClass().getResource(path)).getImage(); //nacteni grafiky

        width = image.getWidth(null);   // prevzeti rozmeru obrazku
        height = image.getHeight(null);
        //System.out.println(width + " " + height);
    }

    public GameObject(int x, int y, int width, int height, String path) {
        this.x = x;
        this.y = y;                                             // konstruktor pro velikost s OBRAZKEM
        this.width = width;
        this.height = height;

        image = new ImageIcon(getClass().getResource(path)).getImage();
}
    
    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;                                // konstruktor pro velikost bez obrazku, hit zony
        this.height = height;
}
    
    public void draw(Graphics g, Component c) {
        g.drawImage(image, x, y, width, height, c);
    }

    public boolean isClicked(int mx, int my) {
        return mx >= x && mx <= x + width &&           // kliknut?
               my >= y && my <= y + height;
    }
}
