package layers.mainmenu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import game.Game;
import layers.BottomLayer;
import layers.Layer;
import layers.ingame.InGameLayer;
import layers.leaderboard.LeaderboardLayer;
import listeners.DifferentEvents;
import listeners.Event;

public class MainMenuLayer extends BottomLayer {

	private final Color backgroundColor = new Color(0xAEECEF);
	private final Color titleColor = new Color(0x1D3557);
	private final Color optionColor = new Color(0x1D3557);
	private final Color optionChosenColor = new Color(0x1AA557);

	private Font titleFont = new Font("SansSerif", Font.BOLD, 60);
	private Font optionFont = new Font("SansSerif", Font.PLAIN, 42);
	
	private final String gameName = "Rules Of Survival";
	private int titleY = height/4;
	private int titleX;
	private int optionParameters[][] = new int[MenuOptions.values().length][MenuOptions.values().length];
	
	private final int xOffset = 3;
	private final int yoffset = 60;

	private HashMap<String, Rectangle> mappedHitBoxes = new HashMap<>();
	private HashMap<String, Boolean> mappedHovers = new HashMap<>();
	
	private BasicStroke rectStroke = new BasicStroke(3f);
	
	
	public MainMenuLayer()
	{
		
		BufferedImage tempa = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = tempa.createGraphics();
	    g2d.setFont(titleFont);
	    titleX = centerString(g2d, gameName);
	    g2d.setFont(optionFont);
		int temp = height/2;
		int i = 0;
		for(MenuOptions mo : MenuOptions.values())
		{
			int x = centerString(g2d,mo.getName());
			optionParameters[i][0] = x;
			optionParameters[i][1] = temp;
			int stringwidth = (int) g2d.getFontMetrics().getStringBounds(mo.getName(), g2d).getWidth();
			int stringheight = (int) g2d.getFontMetrics().getStringBounds(mo.getName(), g2d).getHeight();
			int ascent = g2d.getFontMetrics().getAscent();
			mappedHitBoxes.put(mo.getName(), new Rectangle(x - xOffset, temp - ascent, stringwidth + xOffset * 2, stringheight)); 
			mappedHovers.put(mo.getName(), false);
			temp+= yoffset;
			i++;
		}
		g2d.dispose();
	}

	@Override
	public void onRender(Graphics2D drawing) 
	{
		drawing.setColor(backgroundColor);
		drawing.fillRect(0, 0, width, height);

		drawing.setColor(titleColor);
		drawing.setFont(titleFont);
		drawing.drawString(gameName, titleX, titleY);

		drawing.setFont(optionFont);


		for(int i = 0; i < MenuOptions.values().length; i++)
		{
			drawing.setColor(optionColor);
			drawing.drawString(MenuOptions.values()[i].getName(), optionParameters[i][0], optionParameters[i][1]);

			if(mappedHovers.get(MenuOptions.values()[i].getName()))
			{
				Stroke oldStroke = drawing.getStroke();
				
				Rectangle temp = mappedHitBoxes.get(MenuOptions.values()[i].getName());
				drawing.setColor(new Color(255, 255, 255, 60));
				drawing.fillRect((int) temp.getX(), (int) temp.getY(), temp.width, temp.height);
				drawing.setColor(optionChosenColor);
				drawing.setStroke(rectStroke);
				drawing.drawRect((int)temp.getX(), (int)temp.getY(), temp.width, temp.height);
				drawing.setStroke(oldStroke);
			}
		}
	}

	@Override
	public <T extends Layer> void transitionTo(Class<T> layerClass, int layerPosition) 
	{
		Game.Get().queueTransition(layerClass, layerPosition);
	}
	
	@Override
	public void onEvent(Event e)
	{
		
		for(MenuOptions mo : MenuOptions.values())
		{
			if(mappedHitBoxes.get(mo.getName()).contains(e.getPoint()))
			{
				if(e.getCode() == DifferentEvents.MOVED) mappedHovers.replace(mo.getName(), true);
				else if(e.getCode() == DifferentEvents.CLICKED) 
				{
					switch(mo)
					{
					case ONE_PLAYER: 
						Game.Get().setNumPlayers(1);
						transitionTo(InGameLayer.class, 0);
						break;
					case TWO_PLAYERS: 
						Game.Get().setNumPlayers(2);
						transitionTo(InGameLayer.class, 0);
						break;
					case LEADERBOARD: 
						transitionTo(LeaderboardLayer.class, 0);
						break;
					case CONTROLS: 
						transitionTo(ControlsLayer.class, 0);
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
	
}
