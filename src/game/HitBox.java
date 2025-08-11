package game;

import player.EntityType;

public class HitBox {

	private int offsetX;
	private int offsetY;
	private int hitboxWidth;
	private int hitboxHeight;

	public HitBox( EntityType entityType)
	{

		switch(entityType)
		{
		case PLAYER:
			offsetX = 12;
			offsetY = 24;
			hitboxWidth = 24;
			hitboxHeight = 24;
			break;
		case ENEMY:
			offsetX = 10;
			offsetY = 20;
			hitboxWidth = 28;
			hitboxHeight = 28;
			break;
		case FROZEN:
			break;
		case STRIKING:
			break;
		}
	}

	@Override
	public String toString()
	{
		return "X - " + offsetX + " Y - " + offsetY + " width - " + hitboxWidth + " height - " + hitboxHeight;
	}

	public  int getOffsetX()
	{
		return offsetX;
	}

	public  void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public  int getOffsetY()
	{
		return offsetY;
	}

	public  void setOffsetY(int offsetY)
	{
		this.offsetY = offsetY;
	}

	public  int getHitboxWidth()
	{
		return hitboxWidth;
	}

	public  void setHitboxWidth(int hitboxWidth)
	{
		this.hitboxWidth = hitboxWidth;
	}

	public  int getHitboxHeight()
	{
		return hitboxHeight;
	}

	public  void setHitboxHeight(int hitboxHeight)
	{
		this.hitboxHeight = hitboxHeight;
	}



}
