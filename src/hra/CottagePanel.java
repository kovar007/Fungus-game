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

public class CottagePanel extends ScenePanel{
    Image background;
    Image arrowRight, arrowLeft;
    GameObject wizzZone;
    Image hint;
    Image hintIcon;
    boolean showHint, ending = false;
    
    public CottagePanel(GameFrame game) {
        super(game); 

        //nacteni obrazku
        background = new ImageIcon(getClass().getResource("/assets/Cottage.png")).getImage();
        arrowRight = new ImageIcon(getClass().getResource("/assets/Arrow.png")).getImage();
        hint = new ImageIcon(getClass().getResource("/assets/Hint.png")).getImage();
        hintIcon = new ImageIcon(getClass().getResource("/assets/HintIcon.png")).getImage();
        
        wizzZone    = new GameObject(280, 300, 155, 330); // x, y, width, height
        showHint = true;
        
        loadMask("/assets/CottageMask.png"); 
        setupMouseListener();              
    }
    
    @Override
    protected void onMousePressed(int mx, int my) {
        if (isDialogueActive()) return; // nejde klikat pri dialogu

        if (mx >= 1180 && mx <= 1260 && my >= 320 && my <= 400) {        //sipka na dalsi scenu
            game.showScene("grassScene");
            return;
        }

        if (mx >= 1108 && mx <= 1260 && my >= 14 && my <= 76) {    // klik na zarovku
            showHint = true;
            repaint();
            return;
        }

        // kliknuti jinde zavre napovedu
        showHint = false;
        
        //wizz klick
        if (wizzZone.isClicked(mx, my)) {
            game.player.walkTo(800, 650); 
            contextMenu.show(mx, my,
                new ContextMenu.MenuOption("Talk", () -> {
                    showDialogue(
                            "Caped figure: You are a wizzard, Harry!",
                            "Caped figure: Wait... What?? Just remember it's never lupus.",
                            "Caped figure: Anyway, I will teach you. Bring me some liquid from the well, mushrooms and the item hidden in the forrest chest!",
                            "Caped figure: GO!"
                    );
                    
                    repaint();
                }),
                new ContextMenu.MenuOption("I'm done", () -> {
                    if(game.inventory.hasItem("methanol bucket") && game.inventory.hasItem("mushrooms") && game.inventory.hasItem("finger")){
                        showDialogue(
                                "Caped figure: Well done.",
                                "Caped figure: Now we can get cooking.");
                        ending = true;
                    }
                    else
                        showDialogue("Caped figure: Get moving. Don't test my patience");
                    repaint();
                }),
                new ContextMenu.MenuOption("Leave", () -> {
                    // niccc
                })
            );
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
        g.drawImage(arrowRight, 1180, 310, 90, 90, this);
        drawRotatedImage(g, arrowLeft, 20, 300, 90, 90, 180);
        g.drawImage(hintIcon, 0, 0, getWidth(), getHeight(), this);
        if (showHint) {
            g.drawImage(hint, 0, 0, getWidth(), getHeight(), this);
        }
        
        game.inventory.draw(g, this);
        
        drawUI(g);  // rozhrani, stejne pro vsechny herni sceny
        if(ending && !isDialogueActive()){
            game.showScene("endingScene");}  //pokud hrac splnil quest a precetl si finalni dialog
    }
    
}
