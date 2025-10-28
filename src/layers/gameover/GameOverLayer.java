package layers.gameover;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import game.Game;
import layers.BottomLayer;
import layers.Layer;
import layers.ingame.InGameLayer;
import layers.leaderboard.LeaderboardLayer;
import layers.mainmenu.MainMenuLayer;
import listeners.DifferentEvents;
import listeners.Event;
import listeners.KeyInputs;

public class GameOverLayer extends BottomLayer {
	
	private final Color backgroundColor = new Color(0xAEECEF);
    private final Color titleColor = new Color(0x1D3557);
    private final Color optionColor = new Color(0x1D3557);
    private final Color optionChosenColor = new Color(0x1AA557);
    private final Color accentColor = new Color(0x103557);

    private Font titleFont = new Font("SansSerif", Font.BOLD, 60);
    private Font optionFont = new Font("SansSerif", Font.PLAIN, 42);
	private Font inputFont = new Font("SansSerif", Font.PLAIN, 32);
    
	private final String gameOver = "Game Over";
	private int titleY = height/4;
	private int titleX;
	private int optionParameters[][] = new int[GameOverOptions.values().length][GameOverOptions.values().length];
	
	private final int xOffset = 3;
	private final int yoffset = 60;
	
	private final int margin = 30;
	
	private final String username = "Enter your username";
	private int usernameX;
	private int usernameY = titleY + margin * 3;
	
	private final int blinkTimer = 500;
	private boolean typingActive = true;
	private long lastBlinkTime = 0;
	private boolean showCursor = true;
	
	private final int smallMargin = 10;
	
	private String entryName;

	private int inputBoxWidth = 300;
	private int inputBoxHeight = 40;
	private int inputBoxX = width / 2 - inputBoxWidth / 2;
	private int inputBoxY = usernameY + margin;
	private int textX = inputBoxX + smallMargin;
    private int	textY = inputBoxY + inputBoxHeight - smallMargin;
	private int finalScoreY = inputBoxY + inputBoxHeight + margin * 2;
	private int finalScoreX;

	private HashMap<String, Rectangle> mappedHitBoxes = new HashMap<>();
	private HashMap<String, Boolean> mappedHovers = new HashMap<>();
	
	private BasicStroke rectStroke = new BasicStroke(3f);
	private String finalScore;
	
	private KeyInputs keyInputs;
	
	private boolean enterReleased = true;
	
	private final int score;
	private final String externalFile = "externalLeaderBoard.txt";
	
	public GameOverLayer()
	{
		
		BufferedImage tempa = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = tempa.createGraphics();
	    g2d.setFont(titleFont);
	    titleX = centerString(g2d, gameOver);
	    g2d.setFont(optionFont);
		int temp = height/2 + height /7;
		int i = 0;

		for(GameOverOptions mo : GameOverOptions.values())
		{
			int x = centerString(g2d,mo.getName());
			optionParameters[i][0] = x;
			optionParameters[i][1] = temp;
			int stringwidth = (int) g2d.getFontMetrics().getStringBounds(mo.getName(), g2d).getWidth();
			int stringheight = (int) g2d.getFontMetrics().getStringBounds(mo.getName(), g2d).getHeight();
			int ascent = g2d.getFontMetrics().getAscent();
			mappedHitBoxes.put(mo.getName(), new Rectangle(x - xOffset, temp - ascent, stringwidth + xOffset * 2, stringheight)); 
			mappedHovers.put(mo.getName(), false);
			i++;
			temp+= yoffset;
		}
		g2d.dispose();
		keyInputs = Game.Get().getKeyInputs();
		score = Game.Get().getFinalScore();
		finalScore = "FINAL SCORE: " + score;
		usernameX = centerString(g2d, username);
		finalScoreX = centerString(g2d, finalScore);
	}
	
	@Override
	public void onUpdate()
	{
		if (!typingActive) return;

	    if (entryName == null) entryName = "";

	    String input = keyInputs.getTypedText();
	    if (input != null && !input.isEmpty()) {
	        for (char c : input.toCharArray()) {
	            if (Character.isLetterOrDigit(c) || c == ' ') {
	                if (entryName.length() < 12) {
	                    entryName += c;
	                }
	            }
	        }
	    }

	    if (keyInputs.isBackspace() && entryName.length() > 0) {
	        entryName = entryName.substring(0, entryName.length() - 1);
	    }

	    if (keyInputs.isEnter() && enterReleased) {
	        addEntry(entryName, score);
	        typingActive = false;
	        enterReleased = false;
	    } else if (!keyInputs.isEnter()) {
	        enterReleased = true;
	    }
	}

	@Override
	public void onRender(Graphics2D drawing) 
	{
		drawing.setColor(backgroundColor);
		drawing.fillRect(0, 0, width, height);

		drawing.setColor(titleColor);
		drawing.setFont(titleFont);
		drawing.drawString(gameOver, titleX, titleY);

		drawing.setFont(optionFont);


		for(int i = 0; i < GameOverOptions.values().length; i++)
		{
			drawing.setColor(optionColor);
			drawing.drawString(GameOverOptions.values()[i].getName(), optionParameters[i][0], optionParameters[i][1]);

			if(mappedHovers.get(GameOverOptions.values()[i].getName()))
			{
				Stroke oldStroke = drawing.getStroke();
				
				Rectangle temp = mappedHitBoxes.get(GameOverOptions.values()[i].getName());
				drawing.setColor(new Color(255, 255, 255, 60));
				drawing.fillRect((int) temp.getX(), (int) temp.getY(), temp.width, temp.height);
				drawing.setColor(optionChosenColor);
				drawing.setStroke(rectStroke);
				drawing.drawRect((int)temp.getX(), (int)temp.getY(), temp.width, temp.height);
				drawing.setStroke(oldStroke);
			}
		}
		
		drawing.setFont(optionFont);
		drawing.setColor(titleColor);
		drawing.drawString(finalScore, finalScoreX, finalScoreY);

		drawing.drawString(username, usernameX, usernameY);

		drawing.setColor(Color.WHITE);
		drawing.fillRect(inputBoxX, inputBoxY, inputBoxWidth, inputBoxHeight);

		drawing.setColor(Color.BLACK);
		drawing.drawRect(inputBoxX, inputBoxY, inputBoxWidth, inputBoxHeight);
		
		drawing.setFont(inputFont);
		String displayName = entryName;
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastBlinkTime > blinkTimer)
		{
		    showCursor = !showCursor;
		    lastBlinkTime = currentTime;
		}
		if (showCursor && typingActive)
		{
		    displayName += "_";
		}
		drawing.drawString(displayName, textX, textY);
	}

	@Override
	public <T extends Layer> void transitionTo(Class<T> layerClass, int layerPosition) 
	{
		Game.Get().queueTransition(layerClass, layerPosition);
	}
	
	@Override
	public void onEvent(Event e)
	{
		
		for(GameOverOptions mo : GameOverOptions.values())
		{
			if(mappedHitBoxes.get(mo.getName()).contains(e.getPoint()))
			{
				if(e.getCode() == DifferentEvents.MOVED) mappedHovers.replace(mo.getName(), true);
				else if(e.getCode() == DifferentEvents.LEFTCLICK) 
				{
					switch(mo)
					{
					case NEW_GAME: 
						transitionTo(InGameLayer.class, 0);
						break;
					case MAIN_MENU: 
						transitionTo(MainMenuLayer.class, 0);
						break;
					case LEADERBOARD: 
						transitionTo(LeaderboardLayer.class, 0);
						break;
					case EXIT: 
						System.exit(0);
						break;

					}
				}
			}
			else 
			{
				if(e.getCode() == DifferentEvents.MOVED) mappedHovers.replace(mo.getName(), false);
			}
		}
	}
	
	public void addEntry(String entryName, int finalScore)
	{
		List<String> entries = new ArrayList<>();
	    File file = new File(externalFile);

	    try {
	        if (file.exists()) {
	            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	                String line;
	                while ((line = br.readLine()) != null) {
	                    entries.add(line.trim());
	                }
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading leaderboard file: " + e.getMessage());
	    }

	    String newEntry = entryName + " " + score;
	    if (!entries.isEmpty()) {
	        entries.set(0, entries.get(0) + "; " + newEntry);
	    } else {
	        entries.add(newEntry);
	    }

	    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
	        for (String entry : entries) {
	            bw.write(entry);
	        }
	        bw.newLine();
	    } catch (IOException e) {
	        System.out.println("Error writing leaderboard file: " + e.getMessage());
	    }
	}
}
