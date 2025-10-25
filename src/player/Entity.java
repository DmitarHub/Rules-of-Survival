package player;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.Game;
import layers.ingame.InGameLayer;

public class Entity {

	private final int id;
	private static int staticId = 0;
	protected Images images;
	protected Direction direction;
	protected int maxHealthPoints;
	protected int healthPoints;
	protected int spriteCounter = 0;
	protected int spriteNumber = 1;
	protected HitBox hitbox;
	protected int x, y;
	protected int speed;
	protected boolean collisionOn = false;
	protected InGameLayer gameLayer;
	protected int startingX;
	protected int startingY;
	protected final int heartSpacing = 18;
	protected final int yMargin = 20;

	protected boolean invincible;
	protected int invincibleCounter = 0;
	protected final int invincibleTimer = 45;
	protected final EntityType type;

	public Entity(InGameLayer game, int x, int y, int speed, EntityType entityType, int healthPoints)
	{
		this.id = ++staticId;
		this.gameLayer = game;
		this.x = x;
		this.y = y;
		this.speed = speed;
		images = new Images(game, entityType);
		this.maxHealthPoints = healthPoints;
		this.healthPoints = healthPoints;
		switch(entityType)
		{
		case PLAYER:
			hitbox = new HitBox(EntityType.PLAYER);
			break;
		case ENEMY:
			hitbox = new HitBox(EntityType.ENEMY);
			break;
		case PIRATE:
			hitbox = new HitBox(EntityType.PIRATE);
			break;
		case PLANTTHING:
			hitbox = new HitBox(EntityType.PLANTTHING);
			break;
		case FROZEN:
			break;
		case STRIKING:
			break;
		}
		this.type = entityType;
		direction = Direction.DOWN;

	}

	public int getId()
	{
		return id;
	}

	public void update()
	{

	}
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

	public boolean collisionWith(Entity other)
	{

		int leftA = this.x + hitbox.getOffsetX();
		int topA = this.y + hitbox.getOffsetY();
		int rightA = leftA + hitbox.getHitboxWidth();
		int bottomA = topA + hitbox.getHitboxHeight();

		int leftB = other.x + other.getHitbox().getOffsetX();
		int topB = other.y + other.getHitbox().getOffsetY();
		int rightB = leftB + other.getHitbox().getHitboxWidth();
		int bottomB = topB + other.getHitbox().getHitboxHeight();

		boolean horizontalCollision = leftA < rightB && rightA > leftB;
		boolean verticalCollision = topA < bottomB && bottomA > topB;

		return horizontalCollision && verticalCollision;
	}

	public void animationCounter()
	{
		if (++spriteCounter > 12) {
		    spriteNumber = (spriteNumber % 2) + 1;
		    spriteCounter = 0;
		}
	}

	public HitBox getHitbox() {
		return hitbox;
	}

	public void setHitbox(HitBox hitbox) {
		this.hitbox = hitbox;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isCollisionOn() {
		return collisionOn;
	}

	public void setCollisionOn(boolean collisionOn) {
		this.collisionOn = collisionOn;
	}
}
