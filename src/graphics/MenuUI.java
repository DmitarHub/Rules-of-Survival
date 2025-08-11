package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import game.Game;
import game.GameState;
import listeners.KeyInputs;

public class MenuUI {

	private final int height;
	private final int width;
	private KeyInputs keyInputs;

	private final Color backgroundColor = new Color(0xAEECEF);
	private final Color titleColor = new Color(0x1D3557);
	private final Color optionColor = new Color(0x1D3557);
	private final Color optionChosenColor = new Color(0x1AA557);
	private final Color accentColor = new Color(0x103557);

	private Font titleFont = new Font("SansSerif", Font.BOLD, 60);
	private Font optionFont = new Font("SansSerif", Font.PLAIN, 42);



	private final String gameName = "Rules Of Survival";
	private int titleY;
	private int titleX;
	private boolean setVariables = false;
	private int optionParameters[][];
	private int optionsIndex = 0;
	private MenuOptions hoveredOption = MenuOptions.START_GAME;
	private boolean canNavigate = true;

	private boolean enterReleased;

	private Game game;

	public MenuUI(Game game, int width, int height, KeyInputs keyInputs)
	{
		this.game = game;
		this.width = width;
		this.height = height;
		this.keyInputs = keyInputs;
		optionParameters = new int[MenuOptions.values().length][MenuOptions.values().length];
	}

	public void setup()
	{
		enterReleased = false;
	}

	public void update() {
	    if (keyInputs.isEnter())
	    {
	        if (enterReleased)
	        {
	            enterReleased = false;

	            switch (hoveredOption) {
	                case START_GAME:
	                    game.setGameState(GameState.RULE_CHOOSING);
	                    game.getPlayer().setup();
	                    break;
	                case LEADERBOARD:
	                    game.setGameState(GameState.LEADERBOARD);
	                    game.getLeaderBoardUI().setup();
	                    break;
	                case EXIT:
	                    System.exit(0);
	                    break;
	            }
	        }
	    } else {
	        enterReleased = true;
	        if (canNavigate)
	        {
	            if (keyInputs.isDown())
	            {
	                optionsIndex++;
	                if (optionsIndex >= MenuOptions.values().length)
	                {
	                    optionsIndex = 0;
	                }
	                hoveredOption = MenuOptions.values()[optionsIndex];
	                canNavigate = false;
	            } else if (keyInputs.isUp())
	            {
	                optionsIndex--;
	                if (optionsIndex < 0)
	                {
	                    optionsIndex = MenuOptions.values().length - 1;
	                }
	                hoveredOption = MenuOptions.values()[optionsIndex];
	                canNavigate = false;
	            }
	        }

	        if (!keyInputs.isDown() && !keyInputs.isUp() && !keyInputs.isEnter())
	        {
	            canNavigate = true;
	        }
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
			titleX = centerString(drawing, gameName);
			drawing.setFont(optionFont);
			for(int i = 0; i < MenuOptions.values().length; i++)
			{
				optionParameters[i][0] = centerString(drawing,MenuOptions.values()[i].name());
				optionParameters[i][1] = height / 2 + (i * 60);
				optionParameters[i][2] = (int)drawing
						.getFontMetrics()
						.getStringBounds(MenuOptions.values()[i].name(), drawing)
						.getWidth();
			}
		}

		drawing.setColor(backgroundColor);
		drawing.fillRect(0, 0, width, height);

		drawing.setColor(titleColor);
		drawing.setFont(titleFont);
		drawing.drawString(gameName, titleX, titleY);

		drawing.setFont(optionFont);


		for(int i = 0; i < MenuOptions.values().length; i++)
		{
			drawing.setColor(i == optionsIndex ? optionChosenColor : optionColor);
			drawing.drawString(MenuOptions.values()[i].getName(), optionParameters[i][0], optionParameters[i][1]);

			if(i == optionsIndex)
			{
				drawing.setColor(accentColor);
				drawing.fillRect(optionParameters[i][0], optionParameters[i][1] + 10, optionParameters[i][2], 3);
			}
		}
	}

	public int centerString(Graphics2D draw, String string)
	{
		int length = (int)draw.getFontMetrics().getStringBounds(string, draw).getWidth();
		int x = width / 2 - length / 2;
		return x;
	}

	public MenuOptions getHoveredOption() {
		return hoveredOption;
	}

	public void setHoveredOption(MenuOptions hoveredOption) {
		this.hoveredOption = hoveredOption;
	}
}
