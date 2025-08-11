package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import game.Game;

public class FPSUI {

	private Font FPSFont = new Font("Sansserif", Font.BOLD, 30);

	private final int xOffset = 10;
	private final int yOffset = 30;
	private Game game;

	public FPSUI(Game game)
	{
		this.game = game;
	}

	public void draw(Graphics2D drawing)
	{
		String text = "FPS: " + game.getFPS();
		drawing.setColor(Color.white);
		drawing.setFont(FPSFont);
		drawing.drawString(text, xOffset, yOffset);
	}
}
