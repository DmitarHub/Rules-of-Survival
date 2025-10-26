package entity;

import java.awt.Graphics2D;

public class Entity {

	private final int id;
	private static int staticId = 0;
	protected HitBox hitbox;
	protected int x, y;
	protected EntityType type;

	public Entity(int x, int y, EntityType entityType)
	{
		this.id = ++staticId;
		this.x = x;
		this.y = y;
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
		case COIN:
			hitbox = new HitBox(EntityType.COIN);
		case HEART:
			hitbox = new HitBox(EntityType.HEART);
		case FROZEN:
			break;
		case STRIKING:
			break;
		}
		this.type = entityType;
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

	public void update() {}
	public void draw(Graphics2D graphics2D) {}

	public HitBox getHitbox() { return hitbox; }
	public void setHitbox(HitBox hitbox) { this.hitbox = hitbox; }
	public int getX() { return x; }
	public void setX(int x) { this.x = x; }
	public int getY() { return y; }
	public void setY(int y) { this.y = y; }
	public int getId() { return id; }
	public EntityType getType() { return type; }
}
