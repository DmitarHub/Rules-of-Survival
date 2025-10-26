package entity.collectable;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import entity.Entity;
import entity.EntityType;

public class Collectable extends Entity {

	private List<BufferedImage> images;
	private final int spriteSize = 32;
	private final int fps = 60;
	private int spriteNumber;
	private int animationTimer;
	private int animationCounter = 0;
	private int spriteCounter = 0;
	
	public Collectable(int x, int y, EntityType entityType)
	{
		super(x, y, entityType);
		initializePictures();
	}
	
	
	private void initializePictures()
	{
		images = new ArrayList<>();
		StringBuilder sb = new StringBuilder("/utility/");
		try {
			switch(type)
			{
			case COIN:
				sb.append("Coin 1.png");
				break;
			case HEART:
				sb.append("Heart 1.png");
				break;
			default:
				break;
			}
			BufferedImage spriteSheet = ImageIO.read(getClass().getResourceAsStream(sb.toString()));
			int numberOfRows = spriteSheet.getHeight() / spriteSize;
			int numberOfCols = spriteSheet.getWidth() / spriteSize;
			spriteNumber = numberOfRows * numberOfCols;
			animationTimer = fps / spriteNumber;
			for(int i = 0; i < numberOfRows; i++)
			{
				for(int j = 0; j < numberOfCols; j++)
				{
					images.add(spriteSheet.getSubimage(j * spriteSize, i * spriteSize, spriteSize, spriteSize));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void update()
	{
		if(++animationCounter > animationTimer)
		{
			spriteCounter = (spriteCounter + 1) % spriteNumber;
			animationCounter = 0;
		}
	}
	
	@Override
	public void draw(Graphics2D draw)
	{
		draw.drawImage(images.get(spriteCounter), x, y, null);
	}
}
