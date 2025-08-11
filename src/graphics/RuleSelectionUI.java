package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import tiles.Utool;
import game.Game;
import game.GameState;
import game.Rules;

public class RuleSelectionUI {

	private final int height;
	private final int width;

	private final int numberOfRules = 3;
	private static final int shuffleFrames= 150;
	private static final int descriptionFrames = 180;

	private int animationCounter = 0;
	private int currentSelectingIndex = 0;
	private boolean descriptionShowing = false;


	private List<Rules>selectedRules = new ArrayList<>();
	private Rules currentDisplayedRule = null;
	private Random random = new Random();

	private final Color selectionBackground = new Color(0, 0, 100, 100);
	private final Color descriptionBackground = new Color(0, 0, 0, 200);
	private final Color slotBackground = new Color(0, 20, 120, 100);

	private static final int slotWidth = 150;
	private static final int slotHeight = 150;
	private final int spaceInBetween = 50;
	private final int fullSlotWidth = slotWidth * 3 + spaceInBetween * 2;
	private final int startingX;
	private final int startingY;
	private final int slotArcWidth = 20;
	private final int slotArcHeight = 20;
	private final BasicStroke slotBorder = new BasicStroke(2);
	private final BasicStroke selectedSlotBorder = new BasicStroke(4);
	private static final int iconWidth = slotWidth - 50;
	private static final int iconHeight = slotHeight - 50;
	private int iconX = 0;
	private int iconY = 0;
	private final int iconMargin = 25;
	private BufferedImage questionMark = setQuestionMark();
	private final int descriptionBoxWidth = 500;
	private final int descriptionBoxHeight = 100;
	private final int descriptionBoxX;
	private final int descriptionBoxY;
	private final int descriptionBoxTextX;
	private final int descriptionBoxTextY;
	private final int descriptionBoxMaxWidth = descriptionBoxWidth - 40;
	private final int descriptionBoxLineHeight = 22;
	private final int ruleTitleX;
	private final int ruleTitleY;

	private final Font descriptionTitleFont = new Font("SansSerif", Font.BOLD, 22);
	private final Font descriptionFont = new Font("SansSerif", Font.PLAIN, 18);
	private final Font titleFont = new Font("SansSerif", Font.BOLD, 48);

	private final String title = "Selecting three random rules!";


	private Game game;

	public RuleSelectionUI( Game game, int width, int height)
	{
		this.game = game;
		this.width = width;
		this.height = height;
		this.startingY = height / 3;
		this.startingX = (width - fullSlotWidth) / 2;
		this.descriptionBoxX = (width - descriptionBoxWidth) / 2;
		this.descriptionBoxY = startingY + slotHeight + 40;
		this.descriptionBoxTextX = descriptionBoxX + 20;
		this.descriptionBoxTextY = descriptionBoxY + 60;
		this.ruleTitleX = descriptionBoxX + 20;
		this.ruleTitleY = descriptionBoxY + 30;
	}

	public BufferedImage setQuestionMark()
	{
    	try {
    		Utool u = new Utool();
    		BufferedImage icon = ImageIO.read(Rules.class.getResourceAsStream("/rules/QUESTION_MARK.png"));
    		return u.scaleImage(icon, iconWidth, iconHeight);
    	} catch (IOException e) {
    		System.out.println("Error while reading file");
    		return null;
    	}
	}

	public void update()
	{

		if(currentSelectingIndex >= numberOfRules)
		{
			game.setGameState(GameState.PLAYING);
			game.getPlayingUI().setWaveSpawnCounter();
			game.getActiveRules().clear();
			game.getActiveRules().addAll(selectedRules);
			game.createStaticLayer();
			game.spawnNewWave();
			currentSelectingIndex = 0;
			descriptionShowing = false;
			animationCounter = 0;
			selectedRules.clear();
			return;
		}

		animationCounter++;

		if(!descriptionShowing)
		{

			if(animationCounter < shuffleFrames)
			{
				currentDisplayedRule = game.getAllRules().get(random.nextInt(game.getAllRules().size()));
			} else {
				do {
					currentDisplayedRule = Rules.values()[random.nextInt(Rules.values().length)];
				} while(selectedRules.contains(currentDisplayedRule));

				selectedRules.add(currentDisplayedRule);
				game.activateRule(currentDisplayedRule);
				descriptionShowing = true;
				animationCounter = 0;
			}
		}
		else {
			if(animationCounter >= descriptionFrames)
			{
				currentSelectingIndex++;
				descriptionShowing = false;
				animationCounter = 0;
			}
		}
	}

	public void draw(Graphics2D drawing)
	{



		drawing.setColor(selectionBackground);
		drawing.fillRect(0, 0, getWidth(), getHeight());

		drawing.setColor(Color.white);
		drawing.setFont(titleFont);
		FontMetrics fm = drawing.getFontMetrics();
		drawing.drawString(title, getWidth() / 2 - fm.stringWidth(title) / 2, getHeight() / 4);

	    for (int i = 0; i < numberOfRules; i++)
	    {
	        int x = startingX + i * (slotWidth + spaceInBetween);

	        drawing.setColor(slotBackground);
	        drawing.fillRoundRect(x, startingY, slotWidth, slotHeight, slotArcWidth, slotArcHeight);

	        if (i == currentSelectingIndex && !descriptionShowing)
	        {
	        	drawing.setColor(Color.YELLOW);
	        	drawing.setStroke(selectedSlotBorder);
	        } else {
	        	drawing.setColor(Color.GRAY);
	        	drawing.setStroke(slotBorder);

	        }
	        drawing.drawRoundRect(x, startingY, slotWidth, slotHeight, slotArcWidth, slotArcHeight);
	        BufferedImage icon = null;
	        if (i < selectedRules.size())
	        {
	            icon = selectedRules.get(i).getIcon();
	        } else if (i == currentSelectingIndex) {
	            icon = currentDisplayedRule.getIcon();
	        } else {
	        	icon = questionMark;
	        }

        	iconX = x + iconMargin;
        	iconY = startingY + iconMargin;
        	drawing.drawImage(icon, iconX, iconY, null);
	    }

	    if (descriptionShowing && currentDisplayedRule != null)
	    {
	        drawing.setColor(descriptionBackground);
	        drawing.fillRoundRect(descriptionBoxX, descriptionBoxY,
	        					  descriptionBoxWidth, descriptionBoxHeight,
	        					  slotArcWidth, slotArcHeight);

	        drawing.setColor(Color.WHITE);
	        drawing.setFont(descriptionTitleFont);
	        drawing.drawString(currentDisplayedRule.getName(),ruleTitleX, ruleTitleY);

	        drawing.setFont(descriptionFont);
	        drawStringMultiLine(drawing, currentDisplayedRule.getDescription(),
	        					descriptionBoxTextX, descriptionBoxTextY,
	        					descriptionBoxMaxWidth, descriptionBoxLineHeight);
	    }
	}

	private void drawStringMultiLine(Graphics2D drawing, String text, int x, int y, int maxWidth, int lineHeight)
	{
	    FontMetrics fm = drawing.getFontMetrics();
	    String[] words = text.split(" ");
	    StringBuilder line = new StringBuilder();
	    int curY = y;

	    for (String word : words)
	    {
	        String testLine = line + word + " ";
	        int width = fm.stringWidth(testLine);
	        if (width > maxWidth)
	        {
	        	drawing.drawString(line.toString(), x, curY);
	            line = new StringBuilder(word + " ");
	            curY += lineHeight;
	        } else {
	            line.append(word).append(" ");
	        }
	    }
	    if (line.length() > 0)
	    {
	    	drawing.drawString(line.toString(), x, curY);
	    }
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

	public static int getIconWidth()
	{
		return iconWidth;
	}

	public static int getIconHeight()
	{
		return iconHeight;
	}
}
