package com.tourism;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class GenerateDestinationImages {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 200;

    public static void main(String[] args) throws Exception {
        File dir = new File("src/main/resources/images");
        dir.mkdirs();

        generateBali(new File(dir, "bali.jpg"));
        generateAlps(new File(dir, "alps.jpg"));
        generateKyoto(new File(dir, "kyoto.jpg"));
        generateParis(new File(dir, "paris.jpg"));
        generateManali(new File(dir, "manali.jpg"));
        generateGoa(new File(dir, "goa.jpg"));

        System.out.println("✅ Generated all 6 destination images in src/main/resources/images/");
    }

    private static void generateBali(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sky sunset gradient
        GradientPaint sky = new GradientPaint(0, 0, new Color(255, 94, 98), 0, 130, new Color(255, 175, 123));
        g.setPaint(sky);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Sun
        g.setColor(new Color(255, 240, 180, 220));
        g.fillOval(180, 60, 60, 60);

        // Ocean
        GradientPaint ocean = new GradientPaint(0, 130, new Color(24, 90, 157), 0, HEIGHT, new Color(10, 40, 80));
        g.setPaint(ocean);
        g.fillRect(0, 130, WIDTH, HEIGHT - 130);

        // Sun reflection on ocean
        g.setColor(new Color(255, 215, 0, 80));
        g.fillRect(170, 130, 80, HEIGHT - 130);

        // Palm silhouettes
        g.setColor(new Color(20, 25, 30));
        drawPalm(g, 40, 190, 100);
        drawPalm(g, 90, 185, 80);
        drawPalm(g, 360, 195, 110);

        g.dispose();
        ImageIO.write(img, "jpg", file);
    }

    private static void generateAlps(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Alpine sky
        GradientPaint sky = new GradientPaint(0, 0, new Color(44, 62, 80), 0, 120, new Color(189, 195, 199));
        g.setPaint(sky);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Distant mountains
        g.setColor(new Color(90, 105, 120));
        Path2D.Double m1 = new Path2D.Double();
        m1.moveTo(0, 140);
        m1.lineTo(80, 70);
        m1.lineTo(160, 130);
        m1.lineTo(240, 55);
        m1.lineTo(340, 140);
        m1.lineTo(WIDTH, 80);
        m1.lineTo(WIDTH, HEIGHT);
        m1.lineTo(0, HEIGHT);
        m1.closePath();
        g.fill(m1);

        // Snow-capped peaks foreground
        g.setColor(new Color(240, 243, 246));
        Path2D.Double snow = new Path2D.Double();
        snow.moveTo(240, 55);
        snow.lineTo(210, 95);
        snow.lineTo(270, 95);
        snow.closePath();
        g.fill(snow);

        // Foreground pine hills
        g.setColor(new Color(30, 45, 55));
        g.fillRect(0, 140, WIDTH, HEIGHT - 140);
        for (int x = 10; x < WIDTH; x += 18) {
            drawPine(g, x, 145, 25);
        }

        g.dispose();
        ImageIO.write(img, "jpg", file);
    }

    private static void generateKyoto(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Cherry blossom dusk sky
        GradientPaint sky = new GradientPaint(0, 0, new Color(138, 43, 226), 0, 140, new Color(255, 192, 203));
        g.setPaint(sky);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Full moon
        g.setColor(new Color(255, 255, 230, 230));
        g.fillOval(280, 30, 50, 50);

        // Pagoda temple silhouette
        g.setColor(new Color(25, 20, 35));
        // Tier 3 (top)
        g.fillRect(115, 60, 10, 20);
        g.fillPolygon(new int[]{90, 120, 150}, new int[]{60, 45, 60}, 3);
        // Tier 2
        g.fillRect(110, 80, 20, 25);
        g.fillPolygon(new int[]{80, 120, 160}, new int[]{80, 65, 80}, 3);
        // Tier 1 (base)
        g.fillRect(100, 105, 40, 50);
        g.fillPolygon(new int[]{70, 120, 170}, new int[]{105, 90, 105}, 3);

        // Ground hill
        g.fillArc(-50, 130, WIDTH + 100, 140, 0, 180);

        // Cherry blossoms branch
        g.setColor(new Color(255, 182, 193, 220));
        for (int i = 0; i < 35; i++) {
            int cx = 200 + (int)(Math.random() * 180);
            int cy = 40 + (int)(Math.random() * 80);
            g.fillOval(cx, cy, 7, 7);
        }

        g.dispose();
        ImageIO.write(img, "jpg", file);
    }

    private static void generateParis(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Romantic evening twilight
        GradientPaint sky = new GradientPaint(0, 0, new Color(24, 44, 97), 0, 150, new Color(235, 77, 75));
        g.setPaint(sky);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Stars
        g.setColor(new Color(255, 255, 255, 180));
        for (int i = 0; i < 25; i++) {
            int sx = (int)(Math.random() * WIDTH);
            int sy = (int)(Math.random() * 90);
            g.fillRect(sx, sy, 2, 2);
        }

        // Eiffel Tower silhouette in center
        g.setColor(new Color(20, 25, 40));
        int cx = WIDTH / 2;
        // Spire
        g.fillRect(cx - 2, 20, 4, 30);
        // Top section
        g.fillPolygon(new int[]{cx - 10, cx, cx + 10}, new int[]{80, 50, 80}, 3);
        g.fillRect(cx - 12, 80, 24, 6);
        // Middle section
        g.fillPolygon(new int[]{cx - 25, cx, cx + 25}, new int[]{130, 86, 130}, 3);
        g.fillRect(cx - 28, 130, 56, 8);
        // Legs
        g.fillPolygon(new int[]{cx - 45, cx - 28, cx - 18, cx - 35}, new int[]{HEIGHT, 138, 138, HEIGHT}, 4);
        g.fillPolygon(new int[]{cx + 35, cx + 18, cx + 28, cx + 45}, new int[]{HEIGHT, 138, 138, HEIGHT}, 4);
        g.fillArc(cx - 30, 155, 60, 45, 0, 180);

        // City skyline baseline
        g.fillRect(0, 180, WIDTH, 20);

        g.dispose();
        ImageIO.write(img, "jpg", file);
    }

    private static void generateManali(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Crisp mountain morning sky
        GradientPaint sky = new GradientPaint(0, 0, new Color(52, 152, 219), 0, 120, new Color(236, 240, 241));
        g.setPaint(sky);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Snow peaks
        g.setColor(new Color(220, 230, 242));
        Path2D.Double peaks = new Path2D.Double();
        peaks.moveTo(0, 110);
        peaks.lineTo(90, 40);
        peaks.lineTo(190, 100);
        peaks.lineTo(290, 30);
        peaks.lineTo(WIDTH, 90);
        peaks.lineTo(WIDTH, HEIGHT);
        peaks.lineTo(0, HEIGHT);
        peaks.closePath();
        g.fill(peaks);

        // Mountain river valley
        g.setColor(new Color(39, 174, 96));
        g.fillRect(0, 125, WIDTH, HEIGHT - 125);

        // River
        g.setColor(new Color(41, 128, 185));
        Path2D.Double river = new Path2D.Double();
        river.moveTo(180, 125);
        river.lineTo(210, 125);
        river.lineTo(260, HEIGHT);
        river.lineTo(130, HEIGHT);
        river.closePath();
        g.fill(river);

        // Pine trees
        g.setColor(new Color(22, 100, 50));
        for (int x = 10; x < 150; x += 15) {
            drawPine(g, x, 130 + (int)(Math.random() * 20), 30);
        }
        for (int x = 240; x < WIDTH; x += 15) {
            drawPine(g, x, 130 + (int)(Math.random() * 20), 30);
        }

        g.dispose();
        ImageIO.write(img, "jpg", file);
    }

    private static void generateGoa(File file) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Golden hour tropical sky
        GradientPaint sky = new GradientPaint(0, 0, new Color(243, 156, 18), 0, 130, new Color(241, 196, 15));
        g.setPaint(sky);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Sun
        g.setColor(new Color(255, 255, 255, 220));
        g.fillOval(80, 50, 50, 50);

        // Arabian sea
        GradientPaint sea = new GradientPaint(0, 120, new Color(22, 160, 133), 0, 160, new Color(41, 128, 185));
        g.setPaint(sea);
        g.fillRect(0, 120, WIDTH, 40);

        // Sandy beach
        GradientPaint sand = new GradientPaint(0, 160, new Color(245, 215, 110), 0, HEIGHT, new Color(225, 177, 44));
        g.setPaint(sand);
        g.fillRect(0, 160, WIDTH, HEIGHT - 160);

        // Coconut palm
        g.setColor(new Color(35, 30, 20));
        drawPalm(g, 320, 185, 110);
        drawPalm(g, 365, 190, 85);

        g.dispose();
        ImageIO.write(img, "jpg", file);
    }

    private static void drawPalm(Graphics2D g, int x, int y, int h) {
        g.setStroke(new BasicStroke(4));
        g.drawLine(x, y, x - 10, y - h);
        int topX = x - 10;
        int topY = y - h;

        g.setStroke(new BasicStroke(2));
        g.drawArc(topX - 40, topY - 10, 45, 30, 40, 120);
        g.drawArc(topX - 10, topY - 15, 45, 30, 20, 120);
        g.drawArc(topX - 35, topY - 5, 40, 30, 60, 120);
        g.drawArc(topX - 5, topY - 5, 40, 30, 10, 120);
    }

    private static void drawPine(Graphics2D g, int x, int y, int h) {
        g.fillPolygon(new int[]{x - 8, x, x + 8}, new int[]{y, y - h, y}, 3);
        g.fillPolygon(new int[]{x - 6, x, x + 6}, new int[]{y - (h / 3), y - h - 5, y - (h / 3)}, 3);
    }
}
