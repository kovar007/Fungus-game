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

public class PlayerCharacter {
    float x, y;
    float targetX, targetY;
    float speed = 5.0f;
    boolean isWalking = false;

    Image staticImage;
    BufferedImage[] walkFrames;
    BufferedImage walkMask;

    int width = 150, height = 200;

    // walk animacni sekv
    int[] frameSequence = {0, 1, 1, 2};
    int sequenceIndex = 0;
    int currentFrame = 0;
    Timer frameTimer;

    // blink anim.
    BufferedImage[] blinkFrames;
    int[] blinkSequence = {0, 0, 1, 2, 3, 4, 5, 0, 0};
    int blinkIndex = 0;
    boolean isBlinking = false;
    Timer blinkAnimTimer;
    Timer blinkIdleTimer;
    Random random = new Random();

    public PlayerCharacter(String staticPath, String[] walkFramePaths,
                           float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.targetX = startX;
        this.targetY = startY;

        //staticky PNG
        staticImage = new ImageIcon(getClass().getResource(staticPath)).getImage();

        //walk PNG snimky
        walkFrames = new BufferedImage[walkFramePaths.length];
        for (int i = 0; i < walkFramePaths.length; i++) {
            try {
                walkFrames[i] = ImageIO.read(
                    getClass().getResourceAsStream(walkFramePaths[i])
                );
            } catch (IOException e) {
                System.out.println("Failed to load frame: " + walkFramePaths[i]);
            }
        }

        //blink PNG snimky
        blinkFrames = new BufferedImage[6];
        for (int i = 0; i < 6; i++) {
            try {
                blinkFrames[i] = ImageIO.read(
                    getClass().getResourceAsStream("/assets/CharBlink" + i + ".png")
                );
            } catch (IOException e) {
                System.out.println("Failed to load blink frame: " + i);
            }
        }

        // walk timer — 12fps
        frameTimer = new Timer(1000 / 12, e -> {
            if (isWalking) {
                // projiti sekvence
                sequenceIndex = (sequenceIndex + 1) % frameSequence.length;
                currentFrame = frameSequence[sequenceIndex];
            } else {
                // reset na prvni snimek a stop
                sequenceIndex = 0;
                currentFrame = 0;
            }
        });
        frameTimer.start();

        // blink timer — 10fps
        blinkAnimTimer = new Timer(1000 / 10, e -> {
            blinkIndex++;
            if (blinkIndex >= blinkSequence.length) {
                // bl. hotovo
                blinkIndex = 0;
                isBlinking = false;
                blinkAnimTimer.stop();
                resetBlinkTimer(); // cekani
            }
        });

        // idle timer - random mrkani
        blinkIdleTimer = new Timer(0, e -> {
            if (!isWalking) { // blink jen kdyz spoji na miste
                isBlinking = true;
                blinkIndex = 0;
                blinkAnimTimer.start();
            } else {
                resetBlinkTimer(); // chodi? -> skip a cekat
            }
        });
        blinkIdleTimer.setRepeats(false);
        resetBlinkTimer();
    }

    private void resetBlinkTimer() {
        blinkIdleTimer.stop();
        blinkIdleTimer.setInitialDelay(3000 + random.nextInt(4000)); // 3–7 sec random delay mezi mrkanim
        blinkIdleTimer.restart();
    }

    public void setMask(BufferedImage mask) {
        this.walkMask = mask;                     // nastaveni walk masky
    }

    public void update() {
        if (!isWalking) return;

        float dx = targetX - x;
        float dy = targetY - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy); // vzdalenost od cile

        if (dist < speed) {
            // v cili
            x = targetX;
            y = targetY;
            isWalking = false;
        } else {
            // pohyb k cili
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }
    }

    public void walkTo(float tx, float ty) {
        targetX = tx;
        targetY = ty;
        isWalking = true;
        // zruseni mrkani pri chuzi
        isBlinking = false;
        blinkAnimTimer.stop();
    }

    //teleport
    public void teleport(float tx, float ty) {
        x = tx;
        y = ty;
        targetX = tx;
        targetY = ty;
        isWalking = false;
    }

    public void draw(Graphics g, Component c) {
        float minScale = 0.05f;     //y=0
        float maxScale = 1.3f;   //y=720
        float sceneHeight = 720f;

        float scale = minScale + (y / sceneHeight) * (maxScale - minScale);        //perspektiva, urceni velikosti hrace

        int scaledWidth  = (int) (width  * scale);
        int scaledHeight = (int) (height * scale);

        int drawX = (int) x - scaledWidth  / 2;     // korekce pozice, nohy na zemi, x je stred
        int drawY = (int) y - scaledHeight; 

        // vyber animace
        if (isWalking && walkFrames.length > 0) {
            g.drawImage(walkFrames[currentFrame], drawX, drawY, scaledWidth, scaledHeight, c);
        } else if (isBlinking && blinkFrames[blinkSequence[blinkIndex]] != null) {
            g.drawImage(blinkFrames[blinkSequence[blinkIndex]], drawX, drawY, scaledWidth, scaledHeight, c);
        } else {
            g.drawImage(staticImage, drawX, drawY, scaledWidth, scaledHeight, c);
        }
    }
    
    public void draw(Graphics g, Component c, int customWidth, int customHeight) {    //alternativni draw s danou velikosti (menu)
        int drawX = (int) x - customWidth  / 2;
        int drawY = (int) y - customHeight;

        if (isWalking && walkFrames.length > 0) {
            g.drawImage(walkFrames[currentFrame], drawX, drawY, customWidth, customHeight, c);
        } else if (isBlinking && blinkFrames[blinkSequence[blinkIndex]] != null) {
            g.drawImage(blinkFrames[blinkSequence[blinkIndex]], drawX, drawY, customWidth, customHeight, c);
        } else {
            g.drawImage(staticImage, drawX, drawY, customWidth, customHeight, c);
        }
    }
}