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

public abstract class ScenePanel extends JPanel {

    GameFrame game;
    BufferedImage walkMask;
    protected ContextMenu contextMenu = new ContextMenu();

    // dialogy
    private String[] dialogueLines = null;
    private int dialogueLine = 0;
    private boolean dialogueActive = false;

    protected PauseMenu pauseMenu;

    public ScenePanel(GameFrame game) {
        this.game = game;
        setFocusable(true);
        requestFocusInWindow(); ///fix bugu
        setPreferredSize(new Dimension(1280, 720));
        pauseMenu = new PauseMenu(getClass());

        // space na posun dialogu
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                    if (dialogueActive) {
                        dialogueLine++;
                        if (dialogueLine >= dialogueLines.length) {
                            // konec dialogu
                            dialogueActive = false;
                            dialogueLines = null;
                        }
                        repaint();
                    }
                }
            }
        });

        // ESC na pauznuti hry
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("ESCAPE"), "pause"
        );
        getActionMap().put("pause", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                pauseMenu.toggle();
                repaint();
            }
        });
    }

    //nacteni masky
    protected void loadMask(String maskPath) {
        try {
            walkMask = ImageIO.read(getClass().getResourceAsStream(maskPath));
            game.player.setMask(walkMask);   //pridani masky k hraci
        } catch (IOException e) {
            System.out.println("Mask not found: " + maskPath);
        }
    }

    //jde sem jit?
    protected boolean isWalkable(int x, int y) {
        if (walkMask == null) return true;
        int maskX = x * walkMask.getWidth()  / getWidth();      //prevod souradnic masky ze souradnice sceny
        int maskY = y * walkMask.getHeight() / getHeight();
        
        if (maskX < 0 || maskY < 0 || maskX >= walkMask.getWidth() || maskY >= walkMask.getHeight()) 
            return false;   // mimo masku -> nelze sem jit
        
        int rgb = walkMask.getRGB(maskX, maskY);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8)  & 0xFF;     //urceni pruchodnosti - svetle pruchozi, tmave ne
        int b = rgb & 0xFF;
        return (r + g + b) / 3 > 128;
    }

    // sdileny mouse listener
    protected void setupMouseListener() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow();  //aby panel prijal inputy
                int mx = e.getX(), my = e.getY();
                System.out.println(mx + " " + my);

                // pause icona
                if (pauseMenu.iconClicked(mx, my)) {
                    pauseMenu.toggle();
                    repaint();
                    return;
                }

                // pause menu otevrene - klikani ridi samo
                if (pauseMenu.isOpen()) {
                    String result = pauseMenu.handleClick(mx, my, getWidth(), getHeight());
                    if ("menu".equals(result)) {
                        pauseMenu.close();
                        game.resetGame();
                        game.showScene("menu");
                    } else if ("restart".equals(result)) {
                        pauseMenu.close();
                        game.resetGame();
                        game.showScene("cottageScene");
                    }
                    repaint();
                    return;
                }

                // blockovani inputu pri dialogu
                if (dialogueActive) return;

                // inventar, klik na tasku
                if (game.inventory.bagClicked(mx, my)) {
                    game.inventory.isOpen = !game.inventory.isOpen;
                    repaint();
                    return;
                }

                // zavreni inventare
                if (game.inventory.isOpen) {
                    game.inventory.isOpen = false;
                    repaint();
                    return;
                }

                // context menu
                if (contextMenu.handleClick(mx, my)) {
                    repaint();
                    return;
                }

                // jinak predani inf scene
                onMousePressed(mx, my);
            }
        });
    }

    protected void drawRotatedImage(Graphics g, Image img, int x, int y, int width, int height, double degrees) {
        Graphics2D g2d = (Graphics2D) g;
        //otoci obrazek okolo stredu = souradnic kam se vykresli
        g2d.rotate(Math.toRadians(degrees), x + width / 2, y + height / 2);
        g2d.drawImage(img, x, y, width, height, this);
        g2d.rotate(-Math.toRadians(degrees), x + width / 2, y + height / 2); // reset rotace, jinak dalsi veci taky otocene
    }

    // jednotlive sceny prepisou
    protected abstract void onMousePressed(int mx, int my);

    protected void drawUI(Graphics g) {
        game.inventory.draw(g, this);
        contextMenu.draw(g);
        drawDialogue(g);
        pauseMenu.draw(g, this); // vykresleni nad scenou
    }

    private void drawDialogue(Graphics g) {
        if (!dialogueActive || dialogueLines == null) return;

        // box dole
        int boxY      = getHeight() - 160;
        int boxWidth  = getWidth() - 170;
        int boxX      = (getWidth() - boxWidth) / 2;
        int boxHeight = 140;

        // pozadi
        g.setColor(new Color(30, 20, 10, 220));
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

        // border
        g.setColor(new Color(180, 140, 80));
        g.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

        // nastaveni textu dialogu
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        drawWrappedText(g, dialogueLines[dialogueLine],
            boxX + 20, boxY + 40, boxWidth - 40);

        // indikator stranky dialogu
        g.setColor(new Color(180, 140, 80));
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString((dialogueLine + 1) + "/" + dialogueLines.length,
            boxX + boxWidth - 50, boxY + boxHeight - 15);

        // instrukce
        g.drawString("SPACE to continue", boxX + 20, boxY + boxHeight - 15);
    }

    // zalamovani textu
    private void drawWrappedText(Graphics g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineHeight = fm.getHeight();
        int currentY = y;

        for (String word : words) {
            String test = line + (line.length() > 0 ? " " : "") + word;
            if (fm.stringWidth(test) > maxWidth) {
                // draw current line and start a new one
                g.drawString(line.toString(), x, currentY);
                currentY += lineHeight;
                line = new StringBuilder(word);
            } else {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            }
        }
        // posledni radek
        if (line.length() > 0) {
            g.drawString(line.toString(), x, currentY);
        }
    }

    protected void showDialogue(String... lines) {
        dialogueLines = lines;
        dialogueLine = 0;
        dialogueActive = true;
        repaint();
    }

    protected boolean isDialogueActive() {
        return dialogueActive;
    }

    //zakladni paint
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}