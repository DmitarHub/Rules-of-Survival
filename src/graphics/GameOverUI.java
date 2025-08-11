package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import game.Game;
import game.GameState;
import listeners.KeyInputs;

public class GameOverUI {

	private Game game;
	private final int width;
	private final int height;
	private KeyInputs keyInputs;

    private final Color backgroundColor = new Color(0xAEECEF);
    private final Color titleColor = new Color(0x1D3557);
    private final Color optionColor = new Color(0x1D3557);
    private final Color optionChosenColor = new Color(0x1AA557);
    private final Color accentColor = new Color(0x103557);

    private Font titleFont = new Font("SansSerif", Font.BOLD, 60);
    private Font optionFont = new Font("SansSerif", Font.PLAIN, 42);

    private int optionsIndex = 0;
    private boolean canNavigate = true;
    private int[][] optionParameters;
	private GameOverOptions hoveredOption = GameOverOptions.NEW_GAME;

	private final String gameOver = "GAME OVER";
	private String finalScore;

	private final int margin = 30;
	private final int smallMargin = 10;
	private int titleY;
	private int titleX;
	private int finalScoreX;
	private int finalScoreY;
	private int inputBoxX;
	private int inputBoxY;
	private int textX;
	private int textY;
	private int inputBoxWidth = 300;
	private int inputBoxHeight = 40;
	private boolean setVariables = false;
	private final int blinkTimer = 500;

	private String playerName = "";
	private boolean typingActive = true;
	private long lastBlinkTime = 0;
	private boolean showCursor = true;
	private Font inputFont = new Font("SansSerif", Font.PLAIN, 32);
	private int maxNameLength = 12;

	private boolean enterReleased = true;
	private boolean justFinishedTyping = false;
	private final String username = "Enter your username";
	private int usernameX;
	private int usernameY;

	public GameOverUI(Game game, int width, int height, KeyInputs keyInputs)
	{
		this.game = game;
		this.width = width;
		this.height = height;
		this.keyInputs = keyInputs;
		optionParameters = new int[GameOverOptions.values().length][3];
	}


	public void setup()
	{
		typingActive = true;
		playerName = "";
		lastBlinkTime = 0;
		optionsIndex = 0;
	}


	public void update()
	{
		finalScore = "FINAL SCORE: " + game.getScore();
		handleTyping();

		if (typingActive)
		{
	        if (keyInputs.isEnter())
	        {
	            if (enterReleased)
	            {
	            	game.getLeaderBoardUI().addEntry(playerName, game.getScore());
	                typingActive = false;
	                canNavigate = true;
	                justFinishedTyping = true;
	                enterReleased = false;
	            }
	        } else
	        {
	            enterReleased = true;
	        }
	        return;
	    }

		if (justFinishedTyping)
		{

	        if (!keyInputs.isEnter())
	        {
	            enterReleased = true;
	            justFinishedTyping = false;
	        }
	        return;
	    }

        if (canNavigate) {
            if (keyInputs.isDown())
            {
            	optionsIndex++;
                if (optionsIndex >= GameOverOptions.values().length) optionsIndex = 0;

                hoveredOption = GameOverOptions.values()[optionsIndex];
                canNavigate = false;

            } else if (keyInputs.isUp()) {
            	optionsIndex--;
                if (optionsIndex < 0) optionsIndex = GameOverOptions.values().length - 1;

                hoveredOption = GameOverOptions.values()[optionsIndex];
                canNavigate = false;

            } else if (keyInputs.isEnter()) {

                switch (hoveredOption)
                {
                    case NEW_GAME:
                    	game.resetGame();
                        game.setGameState(GameState.RULE_CHOOSING);
                        game.getPlayer().setup();
                        break;
                    case LEADERBOARD:
                        game.setGameState(GameState.LEADERBOARD);
                        game.getLeaderBoardUI().setup();
                        break;
                    case MAIN_MENU:
                        game.setGameState(GameState.MENU);
                        game.getMenuUI().setup();
                        break;
                    case EXIT:
                        System.exit(0);
                        break;
                }
            }
        }
        if (!keyInputs.isDown() && !keyInputs.isUp() && !keyInputs.isEnter())
        {
            canNavigate = true;
        }
	}

	public void handleTyping()
	{
	    if (!typingActive) return;
	    String input = keyInputs.getTypedText();
	    if (input != null && !input.isEmpty()) {
	        for (char c : input.toCharArray()) {
	            if (Character.isLetterOrDigit(c) || c == ' ')
	            {
	                if (playerName.length() < maxNameLength)
	                {
	                    playerName += c;
	                }
	            }
	        }
	    }


	    if (keyInputs.isBackspace() && playerName.length() > 0)
	    {
	        playerName = playerName.substring(0, playerName.length() - 1);
	    }
	}

	public void draw(Graphics2D drawing)
	{
		if(!setVariables)
		{
			setVariables = true;
			drawing.setColor(titleColor);
			drawing.setFont(titleFont);
			titleY = height / 4;
			titleX = centerString(drawing, gameOver);
			usernameY = titleY + margin * 3;
			inputBoxX = width / 2 - inputBoxWidth / 2;
			inputBoxY = usernameY + margin;
			textX = inputBoxX + smallMargin;
			textY = inputBoxY + inputBoxHeight - smallMargin;
			finalScoreY = inputBoxY + inputBoxHeight + margin * 2;
			drawing.setFont(optionFont);
			for(int i = 0; i < GameOverOptions.values().length; i++)
			{
				optionParameters[i][0] = centerString(drawing,GameOverOptions.values()[i].name());
				optionParameters[i][1] = height / 2 + height / 6 + (i * 60);
				optionParameters[i][2] = (int)drawing
						.getFontMetrics()
						.getStringBounds(GameOverOptions.values()[i].name(), drawing)
						.getWidth();
			}
		}

		drawing.setColor(backgroundColor);
		drawing.fillRect(0, 0, getWidth(), getHeight());

		drawing.setColor(titleColor);
		drawing.setFont(titleFont);
		drawing.drawString(gameOver, titleX, titleY);

		drawing.setFont(optionFont);

		for(int i = 0; i < GameOverOptions.values().length; i++)
		{
			drawing.setColor(i == optionsIndex ? optionChosenColor : optionColor);
			drawing.drawString(GameOverOptions.values()[i].getName(), optionParameters[i][0], optionParameters[i][1]);

			if(i == optionsIndex)
			{
				drawing.setColor(accentColor);
				drawing.fillRect(optionParameters[i][0], optionParameters[i][1] + 10, optionParameters[i][2], 3);
			}
		}


		usernameX = centerString(drawing, username);
		finalScoreX = centerString(drawing, finalScore);
		drawing.setFont(optionFont);
		drawing.setColor(titleColor);
		drawing.drawString(finalScore, finalScoreX, finalScoreY);

		drawing.drawString(username, usernameX, usernameY);

		drawing.setColor(Color.WHITE);
		drawing.fillRect(inputBoxX, inputBoxY, inputBoxWidth, inputBoxHeight);

		drawing.setColor(Color.BLACK);
		drawing.drawRect(inputBoxX, inputBoxY, inputBoxWidth, inputBoxHeight);

		drawing.setFont(inputFont);
		String displayName = playerName;
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

	public int centerString(Graphics2D draw, String string)
	{
		int length = (int)draw.getFontMetrics().getStringBounds(string, draw).getWidth();
		int x = width / 2 - length / 2;
		return x;
	}


	public int getWidth() {
		return width;
	}


	public int getHeight() {
		return height;
	}



}

