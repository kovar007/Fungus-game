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
import java.awt.*;

public class GameFrame extends JFrame{
    private CardLayout cardLayout;
    private JPanel deck;
    public PlayerCharacter player;
    public Inventory inventory;
    
    CottagePanel cottagePanel;
    GamePanel grassPanel;
    NestPanel nestPanel;
    ForestPanel forestPanel;
    WellFallPanel wellFallPanel;
    GameOverPanel gameOverPanel;
    mainMenuPanel mainMenu;
    EndingPanel endingPanel;
    
    public GameFrame() {
        setTitle("Fungus"); 
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();          //system prepinani obrazovek
        deck = new JPanel(cardLayout);      //balicek scen

        player = new PlayerCharacter("/assets/CharF.png",
            new String[]{
                "/assets/Walk1.png",         //snimky walk animace
                "/assets/Walk2.png",
                "/assets/Walk3.png"
            },
            640, 500);

        inventory = new Inventory(getClass());
        
        mainMenu = new mainMenuPanel(this);        // inicializace vsech scen
        cottagePanel = new CottagePanel(this);
        grassPanel = new GamePanel(this);
        nestPanel  = new NestPanel(this);
        forestPanel = new ForestPanel(this);
        wellFallPanel = new WellFallPanel(this);
        gameOverPanel = new GameOverPanel(this);
        endingPanel = new EndingPanel(this);
        
        deck.add(endingPanel, "endingScene");          // pridani scen do balicku
        deck.add(mainMenu, "menu");
        deck.add(cottagePanel, "cottageScene");
        deck.add(grassPanel, "grassScene");
        deck.add(nestPanel,  "nestScene");
        deck.add(forestPanel, "forestScene");
        deck.add(wellFallPanel, "wellFallScene");
        deck.add(gameOverPanel, "gameOverScene");
        
        
        add(deck);
        pack(); // nastavuje velikost okna na 1280x720

        Timer gameLoop = new Timer(1000 / 60, e -> {          //hlavni herni smycka 60 FPS
            player.update();                             // aktualizace hrace
            for (Component c : deck.getComponents()) {
                if (c.isVisible()) {              // aktualizace jen aktivni sceny 
                    c.repaint();
                    break;
                }
            }
        });
        gameLoop.start();

        showScene("menu"); //zobrazeni prvni sceny po zapnuti hry
    }

    public void resetGame() {
        // reset hrace
        player.teleport(800, 650);
        player.isWalking = false;

        // reset inventare
        inventory = new Inventory(getClass());

        // reset stavu scen
        cottagePanel = new CottagePanel(this);
        grassPanel  = new GamePanel(this);
        nestPanel   = new NestPanel(this);
        forestPanel = new ForestPanel(this);


        // re-registerace scen
        deck.removeAll();
        deck.add(endingPanel, "endingScene");
        deck.add(mainMenu, "menu");
        deck.add(cottagePanel, "cottageScene");
        deck.add(grassPanel,   "grassScene");
        deck.add(nestPanel,    "nestScene");
        deck.add(forestPanel,  "forestScene");
        deck.add(wellFallPanel, "wellFallScene");
        deck.add(gameOverPanel, "gameOverScene");
}
    
    
    public void showScene(String name) {      // prepinani scen, ridi hru
        
        nestPanel.stopScene();
        forestPanel.chiAnimTimer.stop();      //zastaveni animaci momo aktivni scenu

        switch (name) {
           case "grassScene":
                                player.teleport(700, 650);
                                player.setMask(grassPanel.walkMask);           // nastaveni viditelnosti sceny a polohy hrace v ni
                                break;
           case "cottageScene":
                                player.teleport(800, 650);
                                player.setMask(cottagePanel.walkMask);
                                break;
           case "nestScene":
                                player.teleport(113, 590);
                                player.setMask(nestPanel.walkMask);
                                nestPanel.startScene();
                                break;
           case "forestScene":
                                player.teleport(200, 700);
                                player.setMask(forestPanel.walkMask);
                                forestPanel.startScene();
                                break;
           case "wellFallScene":
                                player.isWalking = false;
                                wellFallPanel.startScene();
                                break;
           case "gameOverScene":
                                gameOverPanel.startScene();
                                break;   
           case "menu":
                                player.teleport(1060, 700); // position behind the bushes — adjust to your art
                                player.setMask(null);
                                break;
           case "endingScene":
                                endingPanel.startScene();
                                break;
                                }
        cardLayout.show(deck, name);  // prepnuti na viditelne sceny
}
}

