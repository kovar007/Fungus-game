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

public class GamePanel extends ScenePanel {

    Image background;
    GameObject bush;
    Image arrowRight, arrowLeft;
    GameObject wellZone, bushZone;
    boolean bushSearched;
    
    public GamePanel(GameFrame game) {
        super(game); 

        background = new ImageIcon(getClass().getResource("/assets/GrassF.png")).getImage();
        bush = new GameObject(-15, -30, 1280, 720, "/assets/GrassBush.png");
        arrowRight = new ImageIcon(getClass().getResource("/assets/Arrow.png")).getImage();
        arrowLeft = new ImageIcon(getClass().getResource("/assets/Arrow.png")).getImage();

        wellZone    = new GameObject(680, 360, 240, 180); // x, y, width, height
        bushZone    = new GameObject(950, 450, 325, 265); // x, y, width, height
        
        loadMask("/assets/GrassMask.png"); 
        setupMouseListener();              
    }

    @Override
    protected void onMousePressed(int mx, int my) {
        if (isDialogueActive()) return; // nejde klikat pri dialogu
        // sipka dal
        if (mx >= 1180 && mx <= 1260 && my >= 320 && my <= 400) {
            game.showScene("forestScene");
            return;
        }
        // sipka zpet
        if (mx >= 20 && mx <= 100 && my >= 310 && my <= 400) {
            game.showScene("cottageScene");
            return;
        }
        
        if (bushZone.isClicked(mx, my)) {          //moznosti s kerem
            game.player.walkTo(1053, 626); 
            contextMenu.show(mx, my,
                new ContextMenu.MenuOption("Search", () -> {
                    if (!bushSearched) {
                        bushSearched = true;                //pokud neni ker prohledan
                        game.inventory.addItem("bucket", "/assets/InvBucket.png", getClass());
                        showDialogue("You found a bucket!");
                    } else {
                        showDialogue("There is nothing else left.");
                    }
                    repaint();
                }),
                new ContextMenu.MenuOption("Leave", () -> {
                    // nic
                })
            );
            repaint();
            return;
        }
        
        // well klik
        if (wellZone.isClicked(mx, my)) {

           game.player.walkTo(819,557); 
           
            // moznosti v menu podle faze hry
            java.util.ArrayList<ContextMenu.MenuOption> options = new java.util.ArrayList<>();

            options.add(new ContextMenu.MenuOption("Look closer", () -> {   
                new Timer(1500, e -> {
                ((Timer) e.getSource()).stop();
                game.showScene("wellFallScene");
            }).start();
            }));
            
            options.add(new ContextMenu.MenuOption("Leave", () -> {
                //niiiiiiiiic
            }));

            // jen pokud ma hrac kbelik
            if (game.inventory.hasItem("bucket")) {
                options.add(new ContextMenu.MenuOption("Get suspicious liquid", () -> {
                    game.inventory.removeItem("bucket");
                    game.inventory.addItem("methanol bucket", "/assets/InvWaterBucket.png", getClass());
                    showDialogue("You filled the bucket with something.");
                }));
            }

            contextMenu.show(mx, my,
                options.toArray(new ContextMenu.MenuOption[0]));
            repaint();
            return;
        }
        
        if (isWalkable(mx, my)) {
            game.player.walkTo(mx, my);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        game.player.draw(g, this);
        g.drawImage(bush.image, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(arrowRight, 1180, 310, 90, 90, this);
        drawRotatedImage(g, arrowLeft, 20, 300, 90, 90, 180);
        
        game.inventory.draw(g, this);
        drawUI(g);
    }
}