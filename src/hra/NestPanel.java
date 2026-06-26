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
import java.util.Random;

public class NestPanel extends ScenePanel {
    
    Image background;
    GameObject bush;
    Image arrowLeft;

    Image griffinStatic;
    BufferedImage[] blinkFrames;
    BufferedImage[] tapFrames;
    int[] tapSequence = {0, 1, 0, 0, 2, 0};
    BufferedImage[] currentAnim = null;  //neprehrava se zadna animace
    int[] currentSequence = null; //neni specialni poradi snimku
    int sequenceIndex = 0;  //index aktualniho snimku

    Timer animTimer;  //nastaveni FPS
    Timer idleTimer;  //cekani mezi animacemi
    Random random = new Random();

    GameObject griffZone;

    BufferedImage[] boomFrames;      //exploze griffina
    int boomFrame = 0;
    boolean boomPlaying = false;
    Timer boomTimer;
    boolean griffinBoomed = false; // jakmile true, griffin uz neni

    public NestPanel(GameFrame game) {
        super(game);

        //nacteni obrazku
        background    = new ImageIcon(getClass().getResource("/assets/NestF.png")).getImage();
        arrowLeft     = new ImageIcon(getClass().getResource("/assets/Arrow.png")).getImage();
        bush          = new GameObject(0, 0, "/assets/NestBush.png");
        griffinStatic = new ImageIcon(getClass().getResource("/assets/GriffF.png")).getImage();

        griffZone = new GameObject(650, 100, 600, 290); // x, y, width, height

        //nacteni snimku animaci
        blinkFrames = loadFrames("/assets/GriffBlink", 3);
        tapFrames   = loadFrames("/assets/GriffTap",   3);
        boomFrames = loadFrames("/assets/Boom", 10);

        // cca 12fps pro BOOM
        boomTimer = new Timer(1000 / 12, e -> {
            boomFrame++;
            if (boomFrame >= boomFrames.length) {
                // explosion finished — griffin is gone
                boomTimer.stop();
                boomPlaying = false;
                boomFrame = 0;
                griffinBoomed = true;
                showDialogue("You have obtained griffith's feathers.");
                repaint();
            }
            repaint();
        });
        boomTimer.setRepeats(true);

        //nastaveni masky pro walkable zony
        loadMask("/assets/NestMask.png");
        setupMouseListener();

        //cca 10 FPS pro TAP a BLINK
        animTimer = new Timer(1000 / 10, e -> nextFrame());
        animTimer.setRepeats(true);

        idleTimer = new Timer(0, e -> playRandomAnim());  //az timer dobehne, spusteni fce
        idleTimer.setRepeats(false);  //spusteni animece jen 1x
        resetIdleTimer();  //spusteni 1. cekani na animaci
    }

    //nacteni snimku
    private BufferedImage[] loadFrames(String basePath, int count) {
        BufferedImage[] frames = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            try {
                frames[i] = ImageIO.read(getClass().getResourceAsStream(basePath + i + ".png"));
            } catch (IOException e) {
                System.out.println("Failed to load: " + basePath + i);
            }
        }
        return frames;
    }

    @Override
    protected void onMousePressed(int mx, int my) {
        if (isDialogueActive()) return; // nejde klikat pri dialogu

        // zpatky sipka
        if (mx >= 20 && mx <= 100 && my >= 320 && my <= 400) {
            stopScene();
            game.showScene("forestScene");
            return;
        }

        // jde klikat jen pokud griffin JE a prave nevybuchuje
        if (!griffinBoomed && !boomPlaying && griffZone.isClicked(mx, my)) {
            contextMenu.show(mx, my,
                new ContextMenu.MenuOption("Sing", () -> {
                    stopGriffinAnim(); //zastaveni idle animaci
                    boomFrame = 0;
                    boomPlaying = true;
                    boomTimer.start(); //spusteni exploze
                    game.inventory.addItem("feathers", "/assets/InvFeathers.png", getClass());
                    repaint();
                }),
                new ContextMenu.MenuOption("Leave", () -> {
                    // nic
                })
            );
            repaint();
            return;
        }

        if (isWalkable(mx, my)) {
            game.player.walkTo(mx, my);
        }
    }

    // zastaveni griffin animaci pred explozi
    private void stopGriffinAnim() {
        animTimer.stop();
        idleTimer.stop();
        currentAnim = null;
        currentSequence = null;
        sequenceIndex = 0;
    }

    //vyber nahodne animace
    private void playRandomAnim() {
        if (griffinBoomed) return; //nespoustet animaci pokud uz je griffin pryc

        sequenceIndex = 0;
        int pick = random.nextInt(2); //vyber 0 nebo 1
        if (pick == 0) {
            currentAnim = blinkFrames;
            currentSequence = null; //jednoducha sekvence snimku
        } else {
            currentAnim = tapFrames;
            currentSequence = tapSequence; //custom sekvence, jine poradi snimku
        }

        //start animace
        animTimer.start();
        repaint();
    }

    //posun v animaci k dalsimu snimku
    private void nextFrame() {
        sequenceIndex++;

        // Délka animace:
        // - pokud máme sekvenci, její délka
        // - jinak počet snímků
        int length = (currentSequence != null) ? currentSequence.length : currentAnim.length;

        //ukonceni animace
        if (sequenceIndex >= length) {
            animTimer.stop();

            //vynulovani + staticky snimek
            currentAnim = null;
            currentSequence = null;
            sequenceIndex = 0;
            repaint();

            //cekani na dalsi animaci
            resetIdleTimer();
            return;
        }
        repaint();
    }

    //vrati aktualni snimek
    private BufferedImage getCurrentFrame() {
        if (currentAnim == null) return null;

        //pouziti sekvence
        int frameIndex = (currentSequence != null) ? currentSequence[sequenceIndex] : sequenceIndex;
        return currentAnim[frameIndex];
    }

    private void resetIdleTimer() {
        if (griffinBoomed) return; //neresetovat pokud uz je griffin pryc
        idleTimer.stop();
        //nahodne 2-4 sek
        idleTimer.setInitialDelay(2000 + random.nextInt(2000));
        idleTimer.restart();
    }

    public void startScene() {
        animTimer.stop();
        currentAnim = null;
        currentSequence = null;
        sequenceIndex = 0;
        if (!griffinBoomed) resetIdleTimer(); // pokud griffin JE, zacne timer na spusteni animace
    }

    public void stopScene() {
        idleTimer.stop();
        animTimer.stop();
        boomTimer.stop(); //zastavi explozi pri odchodu ze sceny
        currentAnim = null;
        currentSequence = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

        // griffin - skryty pokud je boomed nebo probiha exploze
        if (!griffinBoomed && !boomPlaying) {
            BufferedImage frame = getCurrentFrame();
            if (frame != null) {
                g.drawImage(frame, 600, 45, 600, 400, this); //animovany snimek
            } else {
                g.drawImage(griffinStatic, 600, 45, 600, 400, this); //staticky obrazek
            }
        }

        // exploze griffina
        if (boomPlaying && boomFrames[boomFrame] != null) {
            g.drawImage(boomFrames[boomFrame], 600, 45, 600, 400, this);
        }

        game.player.draw(g, this);
        g.drawImage(bush.image, 0, 0, getWidth(), getHeight(), this);

        drawRotatedImage(g, arrowLeft, 20, 300, 90, 90, 180);

        game.inventory.draw(g, this);
        drawUI(g);
    }
}