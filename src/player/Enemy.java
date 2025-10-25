package player;

import java.util.List;
import java.util.Random;

import game.Game;
import layers.ingame.InGameLayer;

public class Enemy extends Entity{

	protected List<Player> players;

	protected boolean spawning;
	protected final int spawnDuration = 60;
	protected int spawnCounter = 0;

	protected int analyticsId;
	protected boolean dead = false;

	public Enemy(InGameLayer game, int x, int y, int speed, EntityType entityType, int healthPoints, List<Player> players)
	{
		super(game, x, y, speed, entityType, healthPoints);
		this.players = players;
		this.spawning = true;
	}

	@Override
	public void update()
	{
		
	}
	protected void updateSpawning()
	{
		if(spawning)
		{
			spawnCounter++;
			if(spawnCounter >= spawnDuration)
			{

				spawnCounter = 0;
				spawning = false;
			}
		}
	}

	public boolean getSpawning()
	{
		return spawning;
	}

	protected void updateInvincible()
	{
		if(invincible)
		{
			invincibleCounter++;
			if(invincibleCounter >= invincibleTimer)
			{
				invincibleCounter = 0;
				invincible = false;
			}
		}
	}

	protected double getDistanceToPlayer(Player p)
	{
		int dx = p.getX() - x;
		int dy = p.getY() - y;
		return Math.sqrt(dx * dx + dy * dy);
	}


	public void takeDamage(int amount)
	{
		if(invincible) return;
		healthPoints -= amount;
		if(healthPoints <= 0)
		{
			gameLayer.getAnalytics().registerDeath(analyticsId);
			this.setDead(true);
		}
		else {
			gameLayer.getAnalytics().registerDamageTaken(analyticsId, amount);
			invincible = true;
		}
	}

	public void setDead(boolean dead)
	{
		this.dead = dead;
	}

	public boolean isDead()
	{
		return dead;
	}

	public List<Player> getPlayers() { return players; }

	public EntityType getType() {
		// TODO Auto-generated method stub
		return type;
	}

	public void setAnalyticsId(int id) {
		// TODO Auto-generated method stub
		analyticsId = id;
	}
	
	public int getAnalyticsId() { return analyticsId; }
}
