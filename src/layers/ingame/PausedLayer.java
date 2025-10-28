package layers.ingame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

import game.Game;
import layers.Layer;
import layers.mainmenu.MainMenuLayer;
import listeners.DifferentEvents;
import listeners.Event;

public class PausedLayer extends Layer {

    private boolean isPaused = false;
    private final int width = 864;
    private final int height = 864;
    private final Color titleColor = new Color(0x1D3557);
    private final Color overlayColor = new Color(174, 236, 239, 150); 
    private final Color optionColor = new Color(0x1D3557);
    private final Color optionHoverColor = new Color(0x1AA557);
    private final Color optionHighlightFill = new Color(255, 255, 255, 60);

    private final Font optionFont = new Font("SansSerif", Font.PLAIN, 42);
    private Font titleFont = new Font("SansSerif", Font.BOLD, 60);
    
	private final String paused = "Paused";
	private int titleY = height / 4;
	private int titleX;
    
    private final String[] options = { "Resume", "Exit to Main Menu", "Exit" };
    private int[][] optionParameters = new int[options.length][2];
    private HashMap<String, Rectangle> mappedHitBoxes = new HashMap<>();
    private HashMap<String, Boolean> mappedHovers = new HashMap<>();

    private final int yOffset = 60;
    private final int xOffset = 3;
    private final BasicStroke rectStroke = new BasicStroke(3f);

    public PausedLayer() {
        BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImg.createGraphics();
        g2d.setFont(titleFont);
        titleX = centerString(g2d, paused);
        g2d.setFont(optionFont);
        
        int tempY = Game.Get().getHeight() / 2 - (options.length * yOffset) / 2 + 50;
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            int x = centerString(g2d, option);
            optionParameters[i][0] = x;
            optionParameters[i][1] = tempY;

            int stringWidth = (int) g2d.getFontMetrics().getStringBounds(option, g2d).getWidth();
            int stringHeight = (int) g2d.getFontMetrics().getStringBounds(option, g2d).getHeight();
            int ascent = g2d.getFontMetrics().getAscent();

            mappedHitBoxes.put(option, new Rectangle(x - xOffset, tempY - ascent, stringWidth + xOffset * 2, stringHeight));
            mappedHovers.put(option, false);

            tempY += yOffset;
        }
        g2d.dispose();
    }

    @Override
    public void onUpdate() {
        if (!isPaused) return;
    }

    @Override
    public void onRender(Graphics2D draw) {
        if (!isPaused) return;

        draw.setColor(overlayColor);
        draw.fillRect(0, 0, width, height);
        
        draw.setColor(titleColor);
        draw.setFont(titleFont);
        draw.drawString(paused, titleX, titleY);

        draw.setFont(optionFont);
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            int x = optionParameters[i][0];
            int y = optionParameters[i][1];

            if (mappedHovers.get(option)) {
                Rectangle box = mappedHitBoxes.get(option);
                Stroke oldStroke = draw.getStroke();

                draw.setColor(optionHighlightFill);
                draw.fillRect(box.x, box.y, box.width, box.height);

                draw.setColor(optionHoverColor);
                draw.setStroke(rectStroke);
                draw.drawRect(box.x, box.y, box.width, box.height);
                draw.setStroke(oldStroke);
            }

            draw.setColor(optionColor);
            draw.drawString(option, x, y);
        }
    }

    @Override
    public void onEvent(Event e) {
        if (e.getCode() == DifferentEvents.TOGGLEPAUSE) {
            isPaused = !isPaused;
            return;
        }
        if(e.getType() == 1) return;
        if (!isPaused) return;

        for (String option : options) {
            Rectangle hitBox = mappedHitBoxes.get(option);
            boolean inside = hitBox.contains(e.getPoint());

            if (e.getCode() == DifferentEvents.MOVED) {
                mappedHovers.put(option, inside);
            }

            if (e.getCode() == DifferentEvents.LEFTCLICKPRESS && inside) {
                switch (option) {
                    case "Resume":
                        List<Layer> layers = Game.Get().getCurrentLayers();
                        for(Layer l : layers) l.onEvent(new Event(DifferentEvents.TOGGLEPAUSE));
                        break;
                    case "Exit to Main Menu":
                        isPaused = false;
                        transitionTo(MainMenuLayer.class, 0);
                        break;
                    case "Exit":
                        System.exit(0);
                        break;
                }
            }
        }
    }

    @Override
    public <T extends Layer> void transitionTo(Class<T> layerClass,int layerPosition) {
        Game.Get().queueTransition(layerClass, 0);
    }

    private int centerString(Graphics2D g2d, String text) {
        int width = (int) g2d.getFontMetrics().getStringBounds(text, g2d).getWidth();
        return Game.Get().getWidth() / 2 - width / 2;
    }
}
