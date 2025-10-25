package layers.mainmenu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import game.Game;
import layers.BottomLayer;
import layers.Layer;
import listeners.DifferentEvents;
import listeners.Event;

public class ControlsLayer extends BottomLayer {

    private final Color backgroundColor = new Color(0xAEECEF);
    private final Color titleColor = new Color(0x1D3557);
    private final Color keyFill = Color.WHITE;
    private final Color keyBorder = new Color(29, 53, 87);
    private final Color keyText = new Color(29, 53, 87);
    private final Color hoverFill = new Color(255, 255, 255, 60);
    private final Color hoverBorder = new Color(26, 165, 87);

    private final Font titleFont = new Font("SansSerif", Font.BOLD, 60);
    private final Font returnFont = new Font("SansSerif", Font.PLAIN, 42);
    private final Font keyFont = new Font("Monospaced", Font.BOLD, 36);

    private final String title = "Controls";
    private final String returnText = "Return";

    private int titleX;
    private final int titleY = height / 6;
    private final int returnY = height - 80;
    private int returnX;
    private int startY;

    private final int startYOffset = 150;
    private final int colSpacing = width / 2;
    private final int col1X = colSpacing / 2;
    private final int col2X = colSpacing + colSpacing / 2;
    private final int rowHeight = 80;
    private final int keyGap = 15;

    private final int keySize = 60;
    private final int largeKeyWidth = 130;
    private final int playerLabelYOffset = 50;
    private final int ctrlFYOffset = rowHeight * 7/2;
    private final int escYOffset = ctrlFYOffset + 50;

    private BasicStroke rectStroke = new BasicStroke(3f);
    private Rectangle returnHoverBox;
    private boolean returnHovered = false;
    private int xBoxOffset = 5;

    public ControlsLayer() {
        BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImg.createGraphics();
        g2d.setFont(titleFont);
        titleX = centerString(g2d, title);
        startY = titleY + startYOffset;
        g2d.setFont(returnFont);
        returnX = centerString(g2d, returnText);
        int stringWidth = (int) g2d.getFontMetrics().getStringBounds(returnText, g2d).getWidth();
        int stringHeight = (int) g2d.getFontMetrics().getStringBounds(returnText, g2d).getHeight();
        int ascent = g2d.getFontMetrics().getAscent();
        returnHoverBox = new Rectangle(returnX - xBoxOffset, returnY - ascent, stringWidth + xBoxOffset * 2, stringHeight);
        g2d.dispose();
    }

    @Override
    public void onRender(Graphics2D g) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, width, height);

        g.setFont(titleFont);
        g.setColor(titleColor);
        g.drawString(title, titleX, titleY);


        g.setFont(keyFont);

        drawKey(g, "W", col1X, startY, keySize, keySize);
        drawKey(g, "A", col1X - keySize - keyGap, startY + rowHeight, keySize, keySize);
        drawKey(g, "S", col1X, startY + rowHeight, keySize, keySize);
        drawKey(g, "D", col1X + keySize + keyGap, startY + rowHeight, keySize, keySize);
        drawKey(g, "SPACE", col1X, startY + rowHeight * 2, largeKeyWidth, keySize);
        drawStringCentered(g, "Player 1", col1X, startY - playerLabelYOffset);

        drawKey(g, "↑", col2X, startY, keySize, keySize);
        drawKey(g, "←", col2X - keySize - keyGap, startY + rowHeight, keySize, keySize);
        drawKey(g, "↓", col2X, startY + rowHeight, keySize, keySize);
        drawKey(g, "→", col2X + keySize + keyGap, startY + rowHeight, keySize, keySize);
        drawKey(g, "ENTER", col2X, startY + rowHeight * 2, largeKeyWidth, keySize);
        drawStringCentered(g, "Player 2", col2X, startY - playerLabelYOffset);

        drawStringCentered(g, "CTRL + F - Toggle FPS / Stats", width / 2, startY + ctrlFYOffset);
        drawStringCentered(g, "ЕSC ( in Game ) - Pause / UnPause", width / 2, startY + escYOffset);

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

    private void drawKey(Graphics2D g, String key, int centerX, int centerY, int width, int height) {
        int x = centerX - width / 2;
        int y = centerY - height / 2;

        g.setColor(keyFill);
        g.fillRect(x, y, width, height);
        g.setColor(keyBorder);
        g.setStroke(new BasicStroke(3f));
        g.drawRect(x, y, width, height);

        g.setColor(keyText);
        Font oldFont = g.getFont();
        g.setFont(keyFont);
        int textWidth = (int) g.getFontMetrics().getStringBounds(key, g).getWidth();
        int textHeight = (int) g.getFontMetrics().getStringBounds(key, g).getHeight();
        g.drawString(key, centerX - textWidth / 2, centerY + textHeight / 4);
        g.setFont(oldFont);
    }

    private void drawStringCentered(Graphics2D g, String text, int centerX, int y) {
        int textWidth = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
        g.drawString(text, centerX - textWidth / 2, y);
    }

    @Override
    public <T extends Layer> void transitionTo(Class<T> layerClass, int layerPosition) {
        Game.Get().queueTransition(layerClass, 0);
    }

    @Override
    public void onEvent(Event e) {
        if (returnHoverBox.contains(e.getPoint())) {
            if (e.getCode() == DifferentEvents.MOVED)
                returnHovered = true;
            else if (e.getCode() == DifferentEvents.CLICKED)
                transitionTo(MainMenuLayer.class, 0);
        } else if (e.getCode() == DifferentEvents.MOVED)
        	returnHovered = false;
    }


}
