package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.Game;
import game.GameState;

public class PlayingUI {

	private final int width;
	private final int height;
	private final int maxRounds;
	private final int maxWaves = 4;
	private final Font fontUI = new Font("SansSerif", Font.BOLD, 30);
	private final int yStart;
	private final int xStart;
	private final int yEnd;
	private final int xEnd;
	private final int numberOfRules = 3;

	private final Color slotBackground = new Color(0, 20, 120, 100);

	private final int arcWidth = 20;
	private final int arcHeight = 20;

	private final int slotWidth = 150;
	private final int slotHeight = 150;
	private final int spaceInBetween = 10;
	private final int fullSlotWidth = slotWidth * 3 + spaceInBetween * 2;
	private final int startingX;
	private final int startingY;
	private final BasicStroke slotBorder = new BasicStroke(2);
	private int iconX = 0;
	private int iconY = 0;
	private final int iconMargin = 25;
	private final int margin = 30;
	private final int firstMargin = 35;
	private final int centerX;

	private final int waveSpawnTimer = 1800;
	private int waveSpawnCounter = 0;

	private Game game;

	public PlayingUI(Game game, int width, int height)
	{
		this.game = game;
		this.width = width;
		this.height = height;
		yStart = (int)(game.getTileSize() * game.getColumnNumber() - 3.25 * game.getTileSize());
		yEnd = (int)(game.getTileSize() * game.getColumnNumber() - 0.25 * game.getTileSize());
		startingX = game.getTileSize() / 2;
		startingY = yStart + ((yEnd - yStart) - slotHeight) / 2;
		xStart = fullSlotWidth + game.getTileSize() ;
		xEnd =(int)(game.getTileSize() * game.getColumnNumber() - 0.5 * game.getTileSize());
		maxRounds = game.getRounds().length;
		centerX = xStart + (xEnd - xStart) / 2;
	}

	public void setWaveSpawnCounter()
	{
		this.waveSpawnCounter = 0;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public void update() {
		if(!game.isLastRound() && game.getGameState() == GameState.PLAYING)
		{
			if(!game.isLastWave())
			{
				if(game.getAliveEnemies().size() == 0)
				{
					game.spawnNewWave();
					waveSpawnCounter = 0;
				}
				else {
					waveSpawnCounter++;
					if(waveSpawnCounter >= waveSpawnTimer)
					{
						game.spawnNewWave();
						waveSpawnCounter = 0;
					}
				}
			}
		}
	}

	public void draw(Graphics2D drawing) {
	    drawing.setFont(fontUI);
	    FontMetrics fm = drawing.getFontMetrics();
	    String waveText;
	    String roundText;
	    if(!game.isLastWave()) waveText = "Wave: " + game.getRounds()[game.getRoundCounter()].getCurrentWaveIndex() + " / " + maxWaves;
	    else waveText = "Last Wave!";

	    if(!game.isLastRound()) roundText = "Round: " + (game.getRoundCounter() + 1) + " / " + maxRounds;
	    else roundText = "Last Round!";
	    String scoreText = "Score: " + game.getScore();

	    int remainingFrames = waveSpawnTimer - waveSpawnCounter;
	    double remainingSeconds = remainingFrames / 60.0;
	    if(remainingSeconds < 0) remainingSeconds = 0;
	    String nextWave = "";
	    if(!game.isLastWave()) nextWave = String.format("Next wave: %.1f s", remainingSeconds);
	    else nextWave = String.format("Next wave: - ", remainingSeconds);


	    int waveTextWidth = fm.stringWidth(waveText);
	    int roundTextWidth = fm.stringWidth(roundText);
	    int scoreTextWidth = fm.stringWidth(scoreText);
	    int nextWaveWidth = fm.stringWidth(nextWave);

	    drawing.drawString(waveText, centerX - waveTextWidth / 2, yStart + firstMargin);
	    drawing.drawString(roundText, centerX - roundTextWidth / 2, yStart + margin + firstMargin);
	    drawing.drawString(scoreText, centerX - scoreTextWidth / 2, yStart + 2 * margin + firstMargin);
	    drawing.drawString(nextWave, centerX - nextWaveWidth / 2, yStart + 3 * margin + firstMargin);
	}

	public void drawOnce(Graphics2D drawing)
	{
		drawing.setColor(slotBackground);
		drawing.fillRoundRect(xStart, startingY, xEnd - xStart, slotHeight, arcWidth, arcHeight);
		drawing.setStroke(slotBorder);
		drawing.drawRoundRect(xStart, startingY, xEnd - xStart, slotHeight, arcWidth, arcHeight);

		for(int i = 0; i < numberOfRules; i++)
		{
			int x = startingX + i * (slotWidth + spaceInBetween);

			drawing.setColor(slotBackground);
			drawing.fillRoundRect(x, startingY, slotWidth, slotHeight, arcWidth, arcHeight);
			drawing.setStroke(slotBorder);
			drawing.drawRoundRect(x, startingY, slotWidth, slotHeight, arcWidth, arcHeight);
			BufferedImage icon = game.getActiveRules().get(i).getIcon();
			iconX = x + iconMargin;
			iconY = startingY + iconMargin;
			drawing.drawImage(icon, iconX, iconY, null);
		}
	}




}
