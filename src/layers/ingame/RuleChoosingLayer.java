package layers.ingame;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

import game.Game;
import layers.BottomLayer;
import listeners.DifferentEvents;
import listeners.Event;
import tiles.ScaleTool;

public class RuleChoosingLayer extends BottomLayer {

    private static final int NUMBER_OF_RULES = 3;
    private static final int SHUFFLE_FRAMES = 150;
    private static final int DESCRIPTION_FRAMES = 180;

    private int animationCounter = 0;
    private int currentSelectingIndex = 0;
    private boolean descriptionShowing = false;

    private final List<Rules> allRules = Arrays.asList(Rules.values());
    private final List<Rules> selectedRules = new ArrayList<>();
    private Rules currentDisplayedRule = null;
    private final Random random = new Random();

    private final Color selectionBackground = new Color(0, 0, 100, 100);
    private final Color descriptionBackground = new Color(0, 0, 0, 200);
    private final Color slotBackground = new Color(0, 20, 120, 100);

    private static final int SLOT_WIDTH = 150;
    private static final int SLOT_HEIGHT = 150;
    private static final int SPACE_BETWEEN = 50;
    private static final int FULL_SLOT_WIDTH = SLOT_WIDTH * 3 + SPACE_BETWEEN * 2;
    private final int startingX = (width - FULL_SLOT_WIDTH) / 2;
    private final int startingY = height / 3;

    private final int slotArcWidth = 20;
    private final int slotArcHeight = 20;
    private final BasicStroke slotBorder = new BasicStroke(2);
    private final BasicStroke selectedSlotBorder = new BasicStroke(4);

    private static final int ICON_WIDTH = SLOT_WIDTH - 50;
    private static final int ICON_HEIGHT = SLOT_HEIGHT - 50;
    private final int iconMargin = 25;

    private BufferedImage questionMark;

    private final int descriptionBoxWidth = 500;
    private final int descriptionBoxHeight = 100;
    private final int descriptionBoxX = (width - descriptionBoxWidth) / 2;
    private final int descriptionBoxY = startingY + SLOT_HEIGHT + 40;
    private final int descriptionBoxTextX = descriptionBoxX + 20;
    private final int descriptionBoxTextY = descriptionBoxY + 60;
    private final int descriptionBoxMaxWidth = descriptionBoxWidth - 40;
    private final int descriptionBoxLineHeight = 22;
    private final int ruleTitleX = descriptionBoxX + 20;
    private final int ruleTitleY = descriptionBoxY + 30;

    private final Font descriptionTitleFont = new Font("SansSerif", Font.BOLD, 22);
    private final Font descriptionFont = new Font("SansSerif", Font.PLAIN, 18);
    private final Font titleFont = new Font("SansSerif", Font.BOLD, 48);
    private final String title = "Selecting three random rules!";
    
    private boolean isPaused = false;
    
    private InGameLayer gameLayer;

    public RuleChoosingLayer() {
        this.questionMark = loadQuestionMark();
        this.gameLayer = (InGameLayer)Game.Get().getCurrentLayers().get(0);
    }

    @Override
    public void onUpdate() {
    	if(!gameLayer.isChoosingRules() || isPaused) return;
        if (currentSelectingIndex >= NUMBER_OF_RULES) {
        	gameLayer.startingGame();
        	hideLayer();
        	return;
        }

        animationCounter++;

        if (!descriptionShowing) {
            if (animationCounter < SHUFFLE_FRAMES) {
                currentDisplayedRule = allRules.get(random.nextInt(allRules.size()));
            } else {
                do {
                    currentDisplayedRule = Rules.values()[random.nextInt(Rules.values().length)];
                } while (selectedRules.contains(currentDisplayedRule));

                selectedRules.add(currentDisplayedRule);
                gameLayer.activateRule(currentDisplayedRule);
                descriptionShowing = true;
                animationCounter = 0;
            }
        } else {
            if (animationCounter >= DESCRIPTION_FRAMES) {
                currentSelectingIndex++;
                descriptionShowing = false;
                animationCounter = 0;
            }
        }
    }

    @Override
    public void onRender(Graphics2D g2) {
    	if(!gameLayer.isChoosingRules()) return;
        g2.setColor(selectionBackground);
        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, width / 2 - fm.stringWidth(title) / 2, height / 4);

        for (int i = 0; i < NUMBER_OF_RULES; i++) {
            int x = startingX + i * (SLOT_WIDTH + SPACE_BETWEEN);

            g2.setColor(slotBackground);
            g2.fillRoundRect(x, startingY, SLOT_WIDTH, SLOT_HEIGHT, slotArcWidth, slotArcHeight);

            if (i == currentSelectingIndex && !descriptionShowing) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(selectedSlotBorder);
            } else {
                g2.setColor(Color.GRAY);
                g2.setStroke(slotBorder);
            }

            g2.drawRoundRect(x, startingY, SLOT_WIDTH, SLOT_HEIGHT, slotArcWidth, slotArcHeight);

            BufferedImage icon;
            if (i < selectedRules.size()) {
                icon = selectedRules.get(i).getIcon();
            } else if (i == currentSelectingIndex && currentDisplayedRule != null) {
                icon = currentDisplayedRule.getIcon();
            } else {
                icon = questionMark;
            }

            int iconX = x + iconMargin;
            int iconY = startingY + iconMargin;
            g2.drawImage(icon, iconX, iconY, null);
        }

        if (descriptionShowing && currentDisplayedRule != null) {
            g2.setColor(descriptionBackground);
            g2.fillRoundRect(descriptionBoxX, descriptionBoxY,
                    descriptionBoxWidth, descriptionBoxHeight,
                    slotArcWidth, slotArcHeight);

            g2.setColor(Color.WHITE);
            g2.setFont(descriptionTitleFont);
            g2.drawString(currentDisplayedRule.getName(), ruleTitleX, ruleTitleY);

            g2.setFont(descriptionFont);
            drawStringMultiLine(g2,
                    currentDisplayedRule.getDescription(),
                    descriptionBoxTextX,
                    descriptionBoxTextY,
                    descriptionBoxMaxWidth,
                    descriptionBoxLineHeight);
        }
    }
    
	@Override
	public void onEvent(Event e)
	{
		if(e.getCode() == DifferentEvents.TOGGLEPAUSE) isPaused = !isPaused;
	}
    
    private void hideLayer()
    {
    	animationCounter = 0;
    	descriptionShowing = false;
    	currentSelectingIndex = 0;
    	selectedRules.clear();
    }

    private void drawStringMultiLine(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int curY = y;

        for (String word : words) {
            String testLine = line + word + " ";
            int width = fm.stringWidth(testLine);
            if (width > maxWidth) {
                g2.drawString(line.toString(), x, curY);
                line = new StringBuilder(word + " ");
                curY += lineHeight;
            } else {
                line.append(word).append(" ");
            }
        }

        if (line.length() > 0) {
            g2.drawString(line.toString(), x, curY);
        }
    }
    
    private BufferedImage loadQuestionMark() {
        try {
            ScaleTool u = new ScaleTool();
            BufferedImage icon = ImageIO.read(Rules.class.getResourceAsStream("/rules/QUESTION_MARK.png"));
            return u.scaleImage(icon, ICON_WIDTH, ICON_HEIGHT);
        } catch (IOException e) {
            System.out.println("Error loading QUESTION_MARK.png");
            return null;
        }
    }
}
