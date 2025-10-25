package layers.leaderboard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import game.Game;
import layers.BottomLayer;
import layers.Layer;
import layers.mainmenu.MainMenuLayer;
import listeners.DifferentEvents;
import listeners.Event;

public class LeaderboardLayer extends BottomLayer {


    private final String top10 = "TOP 10 SCORES";
    private final String returnText = "Return";
    private final int ten = 10;

    private final String fileName = "/leaderboard.txt";
    private final String externalFile = "externalLeaderBoard.txt";

    private final Color backgroundColor = new Color(0xAEECEF);
    private final Color titleColor = new Color(0x1D3557);
    private final Color textColor = new Color(0x1D3557);
    private final Color hoverFill = new Color(255, 255, 255, 60);
    private final Color hoverBorder = new Color(26, 165, 87);

    private final Color firstPlace = new Color(0xFFD700);
    private final Color secondPlace = new Color(0xC0C0C0);
    private final Color thirdPlace = new Color(0xCD7F32);

    private Font titleFont = new Font("SansSerif", Font.BOLD, 60);
    private Font entryFont = new Font("SansSerif", Font.PLAIN, 36);
    private Font returnFont = new Font("SansSerif", Font.PLAIN, 42);

    private int startY = height / 4;
    private int bigMargin = 50;
    private int rankX = width / 6, nameX = width / 6 + bigMargin, scoreX = width - width / 6;
    private int lineHeight = 40;

    private int returnX;
    private int returnY = height - 100;
    private int titleX;
    private int titleY = height / 6;
    
    private BasicStroke rectStroke = new BasicStroke(3f);
    private Rectangle returnHoverBox;
    private boolean returnHovered = false;
    private int xBoxOffset = 5;

    private final List<Entries> leaderBoardEntries = new ArrayList<>();


    public LeaderboardLayer() {

        BufferedImage tempa = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = tempa.createGraphics();
	    g2d.setFont(returnFont);
	    returnX = centerString(g2d, returnText);
        int returnWidth = (int) g2d.getFontMetrics().getStringBounds(returnText, g2d).getWidth();
        int returnHeight = (int) g2d.getFontMetrics().getStringBounds(returnText, g2d).getHeight();
        int ascetn = g2d.getFontMetrics().getAscent();
        returnHoverBox = new Rectangle (returnX - xBoxOffset, returnY - ascetn, returnWidth + xBoxOffset * 2, returnHeight);
	    g2d.setFont(titleFont);
        titleX = centerString(g2d, top10);
        
        loadLeaderboard();
    }


    private void loadLeaderboard() {
        File file = new File(externalFile);

        if (!file.exists()) {
            try (InputStream is = getClass().getResourceAsStream(fileName);
                 OutputStream os = new FileOutputStream(file)) {

                if (is == null) {
                    System.out.println("Resource " + fileName + " not found!");
                    return;
                }

                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

            } catch (IOException e) {
                System.out.println("Error copying leaderboard resource!");
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] entries = line.split(";");
                for (String entry : entries) {
                    entry = entry.trim();
                    if (!entry.isEmpty()) {
                        String[] parts = entry.split(" ");
                        if (parts.length == 2) {
                            String name = parts[0].trim();
                            int score = Integer.parseInt(parts[1].trim());
                            addEntry(name, score);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error while opening leaderboard file!");
        }
    }

    private void saveLeaderboard() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(externalFile))) {
            List<Entries> sorted = new ArrayList<>(leaderBoardEntries);
            sorted.sort((a, b) -> Integer.compare(b.getFinalscore(), a.getFinalscore()));

            for (int i = 0; i < sorted.size(); i++) {
                Entries e = sorted.get(i);
                bw.write(e.getName() + " " + e.getFinalscore());
                if (i != sorted.size() - 1) bw.write("; ");
            }
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error saving leaderboard!");
        }
    }

    public void addEntry(String name, int score) {
        leaderBoardEntries.add(new Entries(name, score));
        leaderBoardEntries.sort((a, b) -> Integer.compare(b.getFinalscore(), a.getFinalscore()));
        saveLeaderboard();
    }


    @Override
    public void onRender(Graphics2D g) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, width, height);

        g.setColor(titleColor);
        g.setFont(titleFont);
        g.drawString(top10, titleX, titleY);

        g.setFont(entryFont);
        int i = 0;
        for (Entries entry : leaderBoardEntries) {
            if (i >= ten) break;

            String rankStr = (i + 1) + ".";
            if (i == 0) g.setColor(firstPlace);
            else if (i == 1) g.setColor(secondPlace);
            else if (i == 2) g.setColor(thirdPlace);
            else g.setColor(textColor);
            g.drawString(rankStr, rankX, startY + i * lineHeight);

            g.setColor(textColor);
            g.drawString(entry.getName(), nameX, startY + i * lineHeight);

            String scoreStr = String.valueOf(entry.getFinalscore());
            int scoreWidth = (int) g.getFontMetrics().getStringBounds(scoreStr, g).getWidth();
            g.drawString(scoreStr, scoreX - scoreWidth, startY + i * lineHeight);

            i++;
        }

        g.setFont(returnFont);
        g.setColor(titleColor);
        g.drawString(returnText, returnX, returnY);

        if (returnHovered) {
        	Stroke old = g.getStroke();
        	g.setColor(hoverFill);
            g.fillRect(returnHoverBox.x, returnHoverBox.y, returnHoverBox.width, returnHoverBox.height);
            g.setColor(hoverBorder);
            g.setStroke(rectStroke);
            g.drawRect(returnHoverBox.x, returnHoverBox.y, returnHoverBox.width, returnHoverBox.height);
            g.setStroke(old);
        }
    }

    @Override
    public void onEvent(Event e) {
        if (returnHoverBox.contains(e.getPoint())) {
            if (e.getCode() == DifferentEvents.MOVED) returnHovered = true;
            else if (e.getCode() == DifferentEvents.CLICKED) transitionTo(MainMenuLayer.class, 0);
        } else if (e.getCode() == DifferentEvents.MOVED) returnHovered = false;
    }

    @Override
    public <T extends Layer> void transitionTo(Class<T> layerClass, int layerPosition) {
        Game.Get().queueTransition(layerClass, 0);
    }

}
