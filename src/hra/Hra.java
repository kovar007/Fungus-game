/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hra;

/**
 *
 * @author kovarova
 */
import javax.swing.*;

public class Hra {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {    //spusteni swing EDT, jinak GIU se tvori blbe
        GameFrame frame = new GameFrame();    //ridi vse, spousti hru
        frame.setLocationRelativeTo(null); //okno ve stredu obrazovky
        frame.setVisible(true); //zobrazeni okna
    });
    }
}
