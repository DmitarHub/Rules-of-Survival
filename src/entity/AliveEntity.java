package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import layers.ingame.InGameLayer;

public class AliveEntity extends Entity{
	

	protected Images images;
	protected Direction direction = Direction.DOWN;
	protected int maxHealthPoints;
	protected int healthPoints;
	protected int spriteCounter = 0;
	protected int spriteNumber = 1;
	protected int speed;
	protected boolean invincible;
	protected int invincibleCounter = 0;
	protected final int invincibleTimer = 45;
	
	protected InGameLayer gameLayer;
	protected boolean collisionOn;
	
	protected int startingX;
	protected int startingY;
	protected final int heartSpacing = 18;
	protected final int yMargin = 20;
	
	public AliveEntity(InGameLayer gameLayer, int x, int y, int speed, EntityType entityType, int hp)
	{
		super(x, y, entityType);
		this.images = new Images(gameLayer, entityType);
		this.gameLayer = gameLayer;
		this.speed = speed;
		this.maxHealthPoints = hp;
		this.healthPoints = hp;
	}
	
	@Override
	public void draw(Graphics2D graphics2D)
	{
		BufferedImage image = images.getSprite(direction, spriteNumber - 1);
		graphics2D.drawImage(image, x, y, null);
		int startingX = x + (gameLayer.getTileSize() / 2) - ((maxHealthPoints * heartSpacing) / 2);
		int startingY = y - yMargin;
		for(int i = 0; i < maxHealthPoints; i++)
		{
			if(i < healthPoints)
			{
				graphics2D.drawImage(images.getFullHeart(), startingX + i * heartSpacing, startingY, null);
			}
			else {
				graphics2D.drawImage(images.getEmptyHeart(), startingX + i * heartSpacing, startingY, null);
			}
		}
	}
	
	public void collisionCheck()
	{
		setCollisionOn(false);

		gameLayer.getCollisionCheck().checkTile(this);

	}



	public void move()
	{
		if(!isCollisionOn())
		{
			switch(direction)
			{
			case UP -> y -= speed;
			case DOWN -> y += speed;
			case LEFT -> x -= speed;
			case RIGHT -> x += speed;
			}
		}
	}

	public void animationCounter()
	{
		if (++spriteCounter > 12) {
		    spriteNumber = (spriteNumber % 2) + 1;
		    spriteCounter = 0;
		}
	}

	public void setCollisionOn(boolean b) { collisionOn = b; }
	public boolean isCollisionOn() { return collisionOn; }
	public void setDirection(Direction a) { direction = a; }
	public Direction getDirection() { return direction; }
	public int getSpeed() { return speed; }
	public void setSpeed(int a) { speed = a; }
}
