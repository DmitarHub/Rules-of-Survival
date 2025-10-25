package layers.ingame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import analytics.EnemyAnalytics;
import game.Game;
import layers.BottomLayer;
import layers.Layer;
import layers.gameover.GameOverLayer;
import listeners.DifferentEvents;
import listeners.Event;
import player.Monster;
import player.CircleEffect;
import player.Enemy;
import player.Entity;
import player.EntityType;
import player.Player;
import player.RocketMarker;
import player.Pirate;
import player.PlantThing;
import tiles.TileManager;

public class InGameLayer extends BottomLayer {

	private final int maxRounds = 5;
	private final int maxWaves = 4;
	private final Font fontUI = new Font("SansSerif", Font.BOLD, 30);

	private final Color slotBackground = new Color(0, 20, 120, 100);

	private final int arcWidth = 20;
	private final int arcHeight = 20;

	private final int slotWidth = 150;
	private final int slotHeight = 150;
	private final int spaceInBetween = 10;
	private final int fullSlotWidth = slotWidth * 3 + spaceInBetween * 2;
	
	private final int yStart = (int)(tileSize * columnNumber - 3.25 * tileSize);
	private final int xStart = fullSlotWidth + tileSize;
	private final int yEnd = (int)(tileSize * columnNumber - 0.25 * tileSize);
	private final int xEnd =(int)(tileSize * columnNumber - 0.5 * tileSize);
	private final int numberOfRules = 3;
	
	private final int startingX = tileSize / 2;
	private final int startingY = yStart + ((yEnd - yStart) - slotHeight) / 2;;
	private final BasicStroke slotBorder = new BasicStroke(2);
	private int iconX = 0;
	private int iconY = 0;
	private final int iconMargin = 25;
	private final int margin = 30;
	private final int firstMargin = 35;
	private final int centerX = xStart + (xEnd - xStart) / 2;

	private final int waveSpawnTimer = 1800;
	private int waveSpawnCounter = 0;
	
	private TileManager tileManager = new TileManager(this);
	private CollisionCheck collisionCheck = new CollisionCheck(this);
	
	private List<Entity> allEntities = new ArrayList<>();
	private List<Rules> activeRules = new ArrayList<>();

	private List<Object> toBeRemoved = new ArrayList<>();
	//Player stats
	private List<Player> players = new ArrayList<>();
	private int playerSpeed = 5;
	private final int playerSpawnX = tileSize * columnNumber / 2 - tileSize / 2;
	private final int playerSpawnY = tileSize * columnNumber / 2 - tileSize / 2;
	private int playerMaxHealthPoints = 5;
	private int playerAttack = 2;
	
	//Enemy stats
	private int monsterAttack = 1;
	private int monsterSpeed = 1;
	private int monsterMaxHealthPoints = 3;
	private int pirateMaxHealthPoints = 5;
	private int plantThingMaxHealthPoints = 8;

	private List<Enemy> aliveEnemies = new ArrayList<>();
	private Random random = new Random();
	private boolean[][] collisionMap = new boolean[columnNumber][rowNumber];

    private BufferedImage staticLayer;
    private Round[] rounds = new Round[maxRounds];
    
    //Ui info
    private int roundCounter = 0;
    private int score = 0;
    private boolean lastWave = false;
    private boolean lastRound = false;

    //Rules stats
	private final int speedDeBuff = 2;
	private final int speedBuff = 2;
	private final int numberOfEnemiesIncrease = 2;
	
	//Paused
	private boolean isPaused = false;
	//Choosing rules
	private boolean choosingRules = true;
	
	//RocketMarkers
	private List<RocketMarker> rocketMarkers = new ArrayList<>();
	
	//CircleEffects
	private List<CircleEffect> circleEffects = new ArrayList<>();
	
	private EnemyAnalytics analytics = new EnemyAnalytics();
	
	
	public InGameLayer()
	{
        int numOfPlayers = Game.Get().getNumberOfPlayers();
        players.add(new Player(this, playerSpawnX, playerSpawnY, playerSpeed, EntityType.PLAYER, playerMaxHealthPoints, true, Game.Get().getKeyInputs()));
        if(numOfPlayers == 2)
        {
        	players.add(new Player(this, playerSpawnX + tileSize, playerSpawnY + tileSize, playerSpeed, EntityType.PLAYER, playerMaxHealthPoints, false, Game.Get().getKeyInputs()));
        }        
    	allEntities.addAll(players);
        
        getAllValidSpawnLocations();
        initializeRounds();
	}
	
	public void createStaticLayer()
	{
        staticLayer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D drawing = staticLayer.createGraphics();

        getTileManager().draw(drawing);
        drawUI(drawing);

        drawing.dispose();
	}

	
	@Override
	public void onUpdate() {
		if(isPaused || choosingRules) return;
		for(Object p : toBeRemoved) 
		{
			if(p instanceof Player)
			{
				players.remove(p);
				for(Enemy e: aliveEnemies)
				{
					e.getPlayers().remove(p);
				}
			}
			else if(p instanceof RocketMarker)
			{
				rocketMarkers.remove(p);
			}
			else if(p instanceof CircleEffect)
			{
				circleEffects.remove(p);
			}

		}
		if(players.size() == 0) {
			transitionTo(GameOverLayer.class, 0);
			Game.Get().setFinalScore(score);
		}
		toBeRemoved.clear();
		for(RocketMarker rm : rocketMarkers) rm.update();
		for(CircleEffect c : circleEffects) c.update();
		for(Player p : players) p.update();

		for(int i = 0; i < aliveEnemies.size(); i++)
		{
			aliveEnemies.get(i).update();
		}

		checkCollisions();

		checkEnemyCount();
		
		scoreAndTimeUpdate();
	}

	@Override
	public void onRender(Graphics2D draw) {
		if( choosingRules) return;
		draw.drawImage(staticLayer, 0, 0, null);
		for(RocketMarker rm : rocketMarkers) rm.draw(draw);
		for(CircleEffect c : circleEffects) c.draw(draw);
		for(Player p : players) p.draw(draw);
		
		for(int i = 0; i < aliveEnemies.size(); i++)
		{
			aliveEnemies.get(i).draw(draw);
		}
		
		drawScoreAndTime(draw);
	}
	
	@Override
	public <T extends Layer> void transitionTo(Class<T> layerClass, int layerPosition) {
		Game.Get().queueTransition(layerClass, layerPosition);
	}
	
	@Override
	public void onEvent(Event e)
	{
		if(e.getCode() == DifferentEvents.TOGGLEPAUSE)
		{
			togglePaused();
		}
	}
	
	
	public void scoreAndTimeUpdate()
	{
		if(!lastRound && !lastWave)
		{
			if(aliveEnemies.size() == 0)
			{
				analytics.printAverageLifeByType();
				spawnNewWave();
				waveSpawnCounter = 0;
			}
			else
			{
				waveSpawnCounter++;
				if(waveSpawnCounter >= waveSpawnTimer)
				{
					analytics.printAverageLifeByType();
					spawnNewWave();
					waveSpawnCounter = 0;
				}
			}
		}
	}
	
	public void drawUI( Graphics2D drawing )
	{
		drawing.setColor(slotBackground);
		drawing.fillRoundRect(xStart, startingY, xEnd - xStart, slotHeight, arcWidth, arcHeight);
		drawing.setStroke(slotBorder);
		drawing.drawRoundRect(xStart, startingY, xEnd - xStart, slotHeight, arcWidth, arcHeight);

		for(int i = 0; i < numberOfRules; i++)
		{
			int x = startingX + i * (slotWidth + spaceInBetween);

			drawing.setColor(slotBackground);
			drawing.fillRoundRect(x, startingY, slotWidth, slotHeight, arcWidth, arcHeight);
			drawing.setStroke(slotBorder);
			drawing.drawRoundRect(x, startingY, slotWidth, slotHeight, arcWidth, arcHeight);
			BufferedImage icon = activeRules.get(i).getIcon();
			iconX = x + iconMargin;
			iconY = startingY + iconMargin;
			drawing.drawImage(icon, iconX, iconY, null);
		}
	}
	
	public void drawScoreAndTime(Graphics2D drawing)
	{
	    drawing.setFont(fontUI);
	    FontMetrics fm = drawing.getFontMetrics();
	    String waveText;
	    String roundText;
	    if(!lastWave) waveText = "Wave: " + rounds[roundCounter].getCurrentWaveIndex() + " / " + maxWaves;
	    else waveText = "Last Wave!";

	    if(!lastRound) roundText = "Round: " + (roundCounter + 1) + " / " + maxRounds;
	    else roundText = "Last Round!";
	    String scoreText = "Score: " + score;

	    int remainingFrames = waveSpawnTimer - waveSpawnCounter;
	    double remainingSeconds = remainingFrames / 60.0;
	    if(remainingSeconds < 0) remainingSeconds = 0;
	    String nextWave = "";
	    if(!lastWave) nextWave = String.format("Next wave: %.1f s", remainingSeconds);
	    else nextWave = String.format("Next wave: - ", remainingSeconds);


	    int waveTextWidth = fm.stringWidth(waveText);
	    int roundTextWidth = fm.stringWidth(roundText);
	    int scoreTextWidth = fm.stringWidth(scoreText);
	    int nextWaveWidth = fm.stringWidth(nextWave);

	    drawing.drawString(waveText, centerX - waveTextWidth / 2, yStart + firstMargin);
	    drawing.drawString(roundText, centerX - roundTextWidth / 2, yStart + margin + firstMargin);
	    drawing.drawString(scoreText, centerX - scoreTextWidth / 2, yStart + 2 * margin + firstMargin);
	    drawing.drawString(nextWave, centerX - nextWaveWidth / 2, yStart + 3 * margin + firstMargin);
	}
	
	public void spawnNewWave()
	{
    	Wave currentWave = rounds[roundCounter].getWaves().get(rounds[roundCounter].getCurrentWaveIndex());
    	for(int i = 0; i < currentWave.getNumberOfEnemies(); i++)
    	{
    		int[] spawnpos = generateRandomSpawnPositions();
    		int enemyGen = random.nextInt(3);
    		switch(enemyGen)
    		{
    		case 0:
    			aliveEnemies.add(new Monster(this, spawnpos[1], spawnpos[0], monsterSpeed, EntityType.ENEMY, monsterMaxHealthPoints, players));
        		allEntities.add(aliveEnemies.get(aliveEnemies.size() - 1));
    			break;
    		case 1:
    			aliveEnemies.add(new PlantThing(this, spawnpos[1], spawnpos[0], EntityType.PLANTTHING, plantThingMaxHealthPoints, players));
        		allEntities.add(aliveEnemies.get(aliveEnemies.size() - 1));
    			break;
    		case 2:
    			aliveEnemies.add(new Pirate(this, spawnpos[1], spawnpos[0], EntityType.PIRATE, pirateMaxHealthPoints, players));
        		allEntities.add(aliveEnemies.get(aliveEnemies.size() - 1));
    			break;
    		}
    		int analyticsId = analytics.registerSpawn(aliveEnemies.get(aliveEnemies.size() - 1), rounds[roundCounter].getCurrentWaveIndex());
    		aliveEnemies.get(aliveEnemies.size() - 1).setAnalyticsId(analyticsId);

    	}
    	rounds[roundCounter].incrementWave();
    	if(rounds[roundCounter].getWaves().size() == rounds[roundCounter].getCurrentWaveIndex())
    	{
    		lastWave = true;
    	}
	}
	
	private int[] generateRandomSpawnPositions()
    {
		int x = random.nextInt(columnNumber);
		int y = random.nextInt(rowNumber);;

		while(collisionMap[x][y])
		{
			x = random.nextInt(columnNumber);
			y = random.nextInt(rowNumber);;
		}


        int spawnX = x * tileSize;
        int spawnY = y * tileSize;

        return new int[]{spawnX, spawnY};
    }


	private void togglePaused() { isPaused = !isPaused; }
	
    private void checkEnemyCount()
    {
		if(aliveEnemies.size() == 0 && lastWave)
		{
			if(lastRound)
			{
				transitionTo(GameOverLayer.class, 0);
			}
			else {
				roundCounter++;
				lastWave = false;
				for(Player p: players) p.setHP(playerMaxHealthPoints);
				deActivateRules();
				choosingRules = true; 
				if(roundCounter == rounds.length) lastRound = true;
			}
		}
    }

    private void checkCollisions()
    {
    	for (int i = 0; i < allEntities.size(); i++)
    	{
    	    for (int j = i + 1; j < allEntities.size(); j++)
    	    {
    	    	Entity e1 = allEntities.get(i);
    	    	Entity e2 = allEntities.get(j);
    	    	if(e1.collisionWith(e2)) handleCollision(e1, e2);
    	    }
    	}

    	for (int i = 0; i < rocketMarkers.size(); i++)
        {
            RocketMarker marker = rocketMarkers.get(i);
            if(marker.isCountingDown()) continue;
            Rectangle markerBounds = marker.getBounds();

            for (Player p : players)
            {
                Rectangle playerBounds = p.getBounds();

                if (playerBounds.intersects(markerBounds))
                {
                	p.loseHP(marker.getDamage());
                	analytics.registerDamageDealt((marker.getOwner()).getAnalyticsId(), marker.getDamage());
                }
            }
        }
    	

        for (Player p : players)
        {
            boolean inCircle = false;

            for (CircleEffect c : circleEffects)
            {
                if (p.getBounds().intersects(c.getBounds()))
                {
                    inCircle = true;
                    break;
                }
            }

            if (inCircle)
            {
                p.setSpeed(playerSpeed - 2); 
            }
            else
            {
                p.setSpeed(playerSpeed);
            }
        }
        
        
        
	}
    
    public void deActivateRules()
	{
		for(int i = 0; i < activeRules.size(); i++)
		{
			switch(activeRules.get(i))
			{
			case SLOWED:
				playerSpeed += speedDeBuff;
				for(Player player : players) player.setSpeed(player.getSpeed() + speedDeBuff);
				break;
			case FREEZE:
				for(Player player : players) player.setFreeze(false);
				break;
			//case ONE_HP:
				//for(Player player : players) player.setHP(playerMaxHealthPoints);
				//break;
			//case REVERSED_INPUTS:
				//for(Player player : players) player.reverseInputs(false);
				//break;
//			case FASTER_ENEMIES:
//				monsterSpeed -= speedBuff;
//				break;
			case DISARMED:
				for(Player player : players) player.setDisarmed(false);
				break;
			case WEAKENED_DEFENSE:
				monsterAttack -= 1;
				break;
			//case RANDOM_TELEPORT:
				//for(Player player : players) player.setRandomTeleport(false);
				//break;
			case INCREASED_ENEMIES:
				increaseEnemies(-numberOfEnemiesIncrease);
				break;
			}
		}
		activeRules.clear();
	}
    
	public void increaseEnemies(int amount)
	{
		List<Wave> waves = rounds[roundCounter].getWaves();
		for(int i = 0; i < waves.size(); i++)
		{
			waves.get(i).setNumberOfEnemies(amount);
		}
	}
	
	public void getAllValidSpawnLocations()
    {
    	for(int i = 0; i < columnNumber; i++)
    	{
    		for(int j = 0; j < rowNumber; j++)
    		{
    			int tileNum = tileManager.getMap()[i][j];
    			boolean isWalkable = !tileManager.getTiles()[tileNum].isCollision();

    			if(!isWalkable)
    			{
    				collisionMap[i][j] = true;
    			}
    			else collisionMap[i][j] = false;

    		}
    	}
    }
	
	private void handleCollision(Entity e1, Entity e2)
    {
    	if(e1 instanceof Player || e2 instanceof Player)
    	{
    		if(e1 instanceof Monster && !((Monster) e1).getSpawning() && !((Monster) e1).isDead())
    		{
    			((Player)e2).loseHP(monsterAttack);
    			analytics.registerDamageDealt(((Enemy) e1).getAnalyticsId(), monsterAttack);
    		}
    		else if(e2 instanceof Monster && !((Monster) e2).getSpawning() && !((Monster) e2).isDead())
    		{
    			((Player)e1).loseHP(monsterAttack);
    			analytics.registerDamageDealt(((Enemy) e2).getAnalyticsId(), monsterAttack);
    		}
    	}
    	else {
    		if(((Enemy) e2).getSpawning() || ((Enemy) e1).getSpawning())
    		{
    			return;
    		}
    		else if(e1 instanceof Monster)
    		{
    			((Monster) e1).changeToRandomDirection();
    		}
    		else if(e2 instanceof Monster)
    		{
    			((Monster) e2).changeToRandomDirection();
    		}
    	}
	}
	
	public void checkCollisionEnemy(Player player)
    {

    	int playerX = player.getX();
    	int playerY = player.getY();

    	int playerOffsetX = player.getHitbox().getOffsetX();
    	int playerOffsetY = player.getHitbox().getOffsetY();

    	int playerWidth = player.getHitbox().getHitboxWidth();
    	int playerHeight = player.getHitbox().getHitboxHeight();

    	switch(player.getDirection())
    	{
    	case UP:
    		player.setY(playerY - player.getAttackHeight());
    		break;
    	case DOWN:
    		player.setY(playerY + player.getAttackHeight());
    		break;
    	case LEFT:
    		player.setX(playerX - player.getAttackWidth());
    		break;
    	case RIGHT:
    		player.setX(playerX + player.getAttackWidth());
    		break;
    	}

    	player.getHitbox().setOffsetX((tileSize - player.getAttackWidth()) / 2);
    	player.getHitbox().setOffsetY((tileSize - player.getAttackHeight()) / 2);
    	player.getHitbox().setHitboxWidth(player.getAttackWidth());
    	player.getHitbox().setHitboxHeight(player.getAttackHeight());

    	for(Enemy e : aliveEnemies)
    	{

    		if(e.collisionWith(player)) e.takeDamage(playerAttack);
    		if(e.isDead()) score += 5;
    	}
    	aliveEnemies.removeIf(enemy -> enemy.isDead());
    	player.setX(playerX);
    	player.setY(playerY);
    	player.getHitbox().setOffsetX(playerOffsetX);
    	player.getHitbox().setOffsetY(playerOffsetY);
    	player.getHitbox().setHitboxWidth(playerWidth);
    	player.getHitbox().setHitboxHeight(playerHeight);
    }
	
	public void startingGame()
	{
		waveSpawnCounter = 0;
		spawnNewWave();
		
		createStaticLayer();
		choosingRules = false;
	}
	
	public void activateRule( Rules currentDisplayedRule )
	{
		activeRules.add(currentDisplayedRule);
		switch(currentDisplayedRule)
		{
		case SLOWED:
			playerSpeed -= speedDeBuff;
			for(Player player : players) player.setSpeed(player.getSpeed() - speedDeBuff);
			break;
		case FREEZE:
			for(Player player : players) player.setFreeze(true);
			break;
		//case ONE_HP:
			//for(Player player : players) player.setHP(1);
			//break;
		//case REVERSED_INPUTS:
			//for(Player player : players) player.reverseInputs(true);
			//break;
//		case FASTER_ENEMIES:
//			monsterSpeed += speedBuff;
//			break;
		case DISARMED:
			for(Player player : players) player.setDisarmed(true);
			break;
		case WEAKENED_DEFENSE:
			monsterAttack += 1;
			break;
		//case RANDOM_TELEPORT:
			//for(Player player : players) player.setRandomTeleport(true);
			//break;
		case INCREASED_ENEMIES:
			increaseEnemies(numberOfEnemiesIncrease);
			break;
		}
		
	}
	
	private void initializeRounds()
    {
    	try
    	{
			InputStream inputStream = getClass().getResourceAsStream("/rounds.txt");
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    		String line;
    		int i = 0;

    		while((line = bufferedReader.readLine()) != null)
    		{
    			int colonIndex = line.indexOf(':');
    			if(colonIndex == -1) continue;

    			String wavesPart = line.substring(colonIndex + 1).trim();
    			String[] waveEntries = wavesPart.split(",");
    			Round round = new Round();
    			List<Wave> waves = new ArrayList<>();

    			for(String entry : waveEntries)
    			{
    				entry.trim();

    				int dashIndex = entry.indexOf('-');
    				if(dashIndex == -1) continue;

    				String enemyCounterString = entry.substring(dashIndex + 1).trim();
    				int enemyCount = Integer.parseInt(enemyCounterString);

    				waves.add(new Wave(enemyCount));
    			}

    			round.setWaves(waves);
    			rounds[i++] = round;
    		}
    	} catch (FileNotFoundException e) {
			System.out.println("Greska fajl nije pronadjen!");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	

	public void playerDead(Player p)
	{
		toBeRemoved.add(p);
	}
	
	public Random getRandom() { return random; }
	public boolean[][] getCollisionMap() { return collisionMap; }
	public TileManager getTileManager() { return tileManager; }
	public CollisionCheck getCollisionCheck() { return collisionCheck; }
	public boolean isChoosingRules() { return choosingRules; }
	public List<RocketMarker> getRocketMarkers() { return rocketMarkers; }
	public List<CircleEffect> getCircleEffects() { return circleEffects; }
	public List<Object> getToBeRemoved() { return toBeRemoved; }
	public EnemyAnalytics getAnalytics() { return analytics; }
}
