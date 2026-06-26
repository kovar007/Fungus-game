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
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

public class ForestPanel extends ScenePanel {

    Image background;
    Image lamp;
    Image mushroomsImage;
    Image chest;
    Image arrowLeft, arrowRight;
    
    BufferedImage[] chiFrames;
    int[] chiSequence = {0, 1, 2, 1, 0, 0 ,0};
    int chiSequenceIndex = 0;
    Timer chiAnimTimer;
    
    GameObject trunkZone, mushrZone, kenZone, chestZone;
    boolean trunkSearched = false, mushrPicked = false, lampLit = false, chestOpen = false;

    public ForestPanel(GameFrame game) {
        super(game);
        
        //nacteni obrazku
        background = new ImageIcon(getClass().getResource("/assets/Forest.png")).getImage();
        lamp = new ImageIcon(getClass().getResource("/assets/ForestLampUnlit.png")).getImage();
        mushroomsImage = new ImageIcon(getClass().getResource("/assets/ForestMushr.png")).getImage();
        chest = new ImageIcon(getClass().getResource("/assets/ForestChestClosed.png")).getImage();
        arrowLeft = new ImageIcon(getClass().getResource("/assets/Arrow.png")).getImage();
        arrowRight = new ImageIcon(getClass().getResource("/assets/Arrow.png")).getImage();

        chiFrames = loadFrames("/assets/Chi", 3);

        chiAnimTimer = new Timer(1000 / 8, e -> {
            chiSequenceIndex = (chiSequenceIndex + 1) % chiSequence.length;   //timer pro animaci vlkodlaka, cca 8 fps
            repaint();
        });
        chiAnimTimer.start();
        
        trunkZone = new GameObject(0, 470, 270, 140); // x, y, width, height
        mushrZone = new GameObject(300, 565, 90, 50);
        kenZone = new GameObject(1150, 440, 50, 50);
        chestZone = new GameObject(640, 400, 380, 150);

        loadMask("/assets/ForestMask.png");
        setupMouseListener();
    }
    
    public void startScene() {
        chiSequenceIndex = 0;       // na zacatku se spusti hned animace, bezi celou dobu
        chiAnimTimer.start();
    }

    private BufferedImage[] loadFrames(String basePath, int count) {
        BufferedImage[] frames = new BufferedImage[count];         //nactteni snimku se stejnym nazvem ale jinym indexem
        for (int i = 0; i < count; i++) {
            try {
                frames[i] = ImageIO.read(
                    getClass().getResourceAsStream(basePath + i + ".png")
                );
            } catch (IOException e) {
                System.out.println("Failed to load: " + basePath + i + ".png");
            }
        }
        return frames;
    }

    @Override
    protected void onMousePressed(int mx, int my) {
        if (isDialogueActive()) return; // nejde klikat pri dialogu
        
        if (mx >= 20 && mx <= 100 && my >= 310 && my <= 400) {     // sipka zpatky
            chiAnimTimer.stop();
            game.showScene("grassScene");
            return;
        }
        
        if (mx >= 1180 && mx <= 1260 && my >= 320 && my <= 400) {      //sipka dal
            game.showScene("nestScene");
            return;
        }
        
        // trunk click - zobrazeni menu
        if (trunkZone.isClicked(mx, my)) {
            game.player.walkTo(150, 643); 
            contextMenu.show(mx, my,
                new ContextMenu.MenuOption("Search trunk", () -> {
                    if (!trunkSearched) {
                        trunkSearched = true;
                        game.inventory.addItem("matches", "/assets/InvMatches.png", getClass());
                        showDialogue("You found a matches!");
                    } else {
                        showDialogue("There is nothing else left.");
                    }
                    repaint();
                }),
                new ContextMenu.MenuOption("Leave", () -> {
                    // niiicc
                })
            );
            repaint();
            return;
        }

        // mushroom click - zobrazeni menu
        if (!mushrPicked && mushrZone.isClicked(mx, my)) {
            game.player.walkTo(340, 631); 
            contextMenu.show(mx, my,
                new ContextMenu.MenuOption("Collect mushrooms", () -> {
                    mushrPicked = true;
                    game.inventory.addItem("mushrooms", "/assets/InvMushr.png", getClass());
                    showDialogue("Mushrooms colected!");
                    repaint();
                }),
                new ContextMenu.MenuOption("Leave", () -> {
                    // nic
                })
            );
            repaint();
            return;
        }
        
        if (kenZone.isClicked(mx, my)) {
           game.player.walkTo(1088,698); 
           
            // moznosti v menu podle faze hry
            java.util.ArrayList<ContextMenu.MenuOption> options = new java.util.ArrayList<>();
            
            options.add(new ContextMenu.MenuOption("Leave", () -> {
                //niiiiiiiiic
            }));

            // lit - jen kdyz ma hrac uz zapalky
            if (game.inventory.hasItem("matches")) {
                options.add(new ContextMenu.MenuOption("Light candle", () -> {
                    game.inventory.removeItem("matches");
                    lampLit = true;
                    lamp = new ImageIcon(getClass().getResource("/assets/ForestLampLit.png")).getImage();
                }));
            }

            contextMenu.show(mx, my,
                options.toArray(new ContextMenu.MenuOption[0]));
            repaint();
            return;
        }
        
        if (chestZone.isClicked(mx, my)) {
           game.player.walkTo(731,682); 
           
            // moznosti v menu podle faze hry
            java.util.ArrayList<ContextMenu.MenuOption> options = new java.util.ArrayList<>();

            // bedna je zavrena a hrac nema klic
            if (!game.inventory.hasItem("key") && !chestOpen) {
                options.add(new ContextMenu.MenuOption("Talk", () -> {
                    showDialogue(
                            "Werewolf: I will not let you open it for free!",
                            "Werewolf: It is so dark and cold here at night for someone with lupus.",
                            "Werewolf: Get me some light and griffin's feathers, and I'll even give you a key."
                    );
                }));
                
                options.add(new ContextMenu.MenuOption("Ask for a key", () -> {
                    if(game.inventory.hasItem("feathers") && lampLit){
                        showDialogue("Werewolf: Are you done? There you go.",
                                "You have obtained a key."
                                );
                        game.inventory.addItem("key", "/assets/InvKey.png", getClass());
                        game.inventory.removeItem("feathers");
                    } else
                        showDialogue("Werewolf: You're not done yet. Move already.");
                }));
                
            }else if(!chestOpen){         //hrac ma klic ale bedna je porad zavrena -> otevrit
                options.add(new ContextMenu.MenuOption("Open chest", () -> {
                    game.inventory.removeItem("key");
                    chestOpen = true;
                    chest = new ImageIcon(getClass().getResource("/assets/ForestChestOpen.png")).getImage();
                    showDialogue("You got a mummy's finger");
                    game.inventory.addItem("finger", "/assets/InvFinger.png", getClass());
                }));
            }
            
            options.add(new ContextMenu.MenuOption("Leave", () -> {
                            //niiiiiiiiic
                        }));
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
        g.drawImage(background,     0, 0, getWidth(), getHeight(), this);
        g.drawImage(lamp,      0, 0, getWidth(), getHeight(), this);
        if (!mushrPicked) {
        g.drawImage(mushroomsImage, 0, 0, getWidth(), getHeight(), this);
        }
        g.drawImage(chest,    0, 0, getWidth(), getHeight(), this);

        if (chiFrames[chiSequence[chiSequenceIndex]] != null) {
            g.drawImage(chiFrames[chiSequence[chiSequenceIndex]],
                675, 270, 450, 450, this);
        }

        game.player.draw(g, this);
        
        drawRotatedImage(g, arrowLeft, 20, 300, 90, 90, 180);
        g.drawImage(arrowRight, 1180, 310, 90, 90, this);
        
        drawUI(g); // rozhrani, stejne pro vsechny herni sceny
    }
}