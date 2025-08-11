package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

import graphics.FPSUI;
import graphics.GameOverUI;
import graphics.LeaderBoardUI;
import graphics.MenuUI;
import graphics.PlayingUI;
import graphics.RuleSelectionUI;
import listeners.KeyInputs;
import player.EntityType;
import player.Player;
import tiles.TileManager;
import player.Enemy;
import player.Entity;

public class Game extends JPanel implements Runnable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private JFrame mainFrame;

	private final static int originalTileSize = 32;
	private final int columnNumber = 18;
	private final int rowNumber = 18;

	private static double scale = 1.5;
	private static int tileSize =(int)((double)originalTileSize * scale);

	private int width = tileSize * columnNumber;
	private int height = tileSize * rowNumber;

	private Thread gameThread;
	private volatile boolean running;

	private static final int fps = 60;
	private static final double timePerFrame = 1000000000.0 / fps;

	private GameState gameState;

	private MenuUI menuUI;
	private RuleSelectionUI ruleSelectionUI;
	private PlayingUI playingUI;
	private GameOverUI gameOverUI;
	private FPSUI fpsUI;
	private LeaderBoardUI leaderBoardUI;
	private TileManager tileManager;
	private CollisionCheck collisionCheck;
	private Player player;
	private List<Entity> allEntities = new ArrayList<>();
	private KeyInputs keyInputs;
	private final String title = "RulesOfSurvival";

	private List<Rules> allRules = Arrays.asList(Rules.values());
	private List<Rules> activeRules = new ArrayList<>();

	private int playerSpeed = 5;
	private final int playerSpawnX = tileSize * columnNumber / 2 - tileSize / 2;
	private final int playerSpawnY = tileSize * columnNumber / 2 - tileSize / 2;
	private int playerMaxHealthPoints = 5;
	private int playerAttack = 2;

	private int enemyAttack = 1;
	private int enemySpeed = 1;
	private int enemyMaxHealthPoints = 3;

	private List<Enemy> aliveEnemies = new ArrayList<>();
	private Random random = new Random();
	private boolean[][] collisionMap = new boolean[columnNumber][rowNumber];

    private BufferedImage staticLayer;
    private Round[] rounds = new Round[5];
    private int roundCounter = 0;

    private int score = 0;
    private boolean lastWave = false;
    private boolean lastRound = false;

    private int frameCounter = 0;
    private int framesPerSecond = 0;

	private final int speedDeBuff = 2;
	private final int speedBuff = 2;
	private final int numberOfEnemiesIncrease = 2;

	public Game()
	{
		init();
		mainFrame = new JFrame();
		mainFrame.add(this);
		mainFrame.pack();
		mainFrame.setTitle(title);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setResizable(false);
		setFocusable(true);
		mainFrame.setVisible(true);
		start();
	}

	public void init()
	{
		setTileManager(new TileManager(this));
		this.setPreferredSize(new Dimension(width, height));
		collisionCheck = new CollisionCheck(this);
		keyInputs = new KeyInputs(this);
		addKeyListener(keyInputs);
		menuUI = new MenuUI(this, width, height, keyInputs);
		ruleSelectionUI = new RuleSelectionUI(this, width, height);
		playingUI = new PlayingUI(this, width, height);
		gameOverUI = new GameOverUI(this, width, height, keyInputs);
		fpsUI = new FPSUI(this);
		leaderBoardUI = new LeaderBoardUI(this, width, height, keyInputs);
		player = new Player(this, keyInputs, playerSpawnX, playerSpawnY, playerSpeed, EntityType.PLAYER, playerMaxHealthPoints);
		allEntities.add(player);
		gameState = GameState.MENU;
		getAllValidSpawnLocations();
		initializeRounds();
	}


	@Override
	public void run()
	{
		long lastTime = System.nanoTime();
		double delta = 0;
		long fpsTimer = System.currentTimeMillis();
		while(running)
		{
            long now = System.nanoTime();
            delta += (now - lastTime) / timePerFrame;
            lastTime = now;

            while (delta >= 1)
            {
                update();
                repaint();
                frameCounter++;
                delta--;
            }

            if(System.currentTimeMillis() - fpsTimer >= 1000)
            {
            	framesPerSecond = frameCounter;
            	frameCounter = 0;
            	fpsTimer += 1000;

            }
		}

	}


	public void start()
	{

		running = true;
		gameThread = new Thread(this);
		gameThread.start();
	}

	public void stop()
	{
		running = false;
		try
		{
	        gameThread.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

    private void update()
    {
    	if(gameState == GameState.PLAYING)
    	{
    		player.update();

    		for(int i = 0; i < aliveEnemies.size(); i++)
    		{
    			aliveEnemies.get(i).update();
    		}

    		checkCollisions();

    		checkEnemyCount();

    		playingUI.update();
    	}

    	if(gameState == GameState.MENU)
    	{
    		menuUI.update();
    	}

    	if(gameState == GameState.RULE_CHOOSING)
    	{
    		ruleSelectionUI.update();
    	}

    	if(gameState == GameState.LEADERBOARD)
    	{
    		leaderBoardUI.update();
    	}

    	if(gameState == GameState.GAME_OVER)
    	{
    		gameOverUI.update();
    	}
    }

    private void checkEnemyCount()
    {
		if(aliveEnemies.size() == 0)
		{
			if(lastWave)
			{

				if(lastRound)
				{
					gameOver();
				}
				else {
					roundCounter++;
    				lastWave = false;
    				deActivateRules();
    				gameState = GameState.RULE_CHOOSING;
    				if(roundCounter == rounds.length) lastRound = true;
				}
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

	}


	private void handleCollision(Entity e1, Entity e2)
    {
    	if(e1 instanceof Player || e2 instanceof Player)
    	{
    		if(e1 instanceof Enemy && !((Enemy) e1).getSpawning() && !((Enemy) e1).isDead())
    		{
    			player.loseHP(enemyAttack);
    		}
    		else if(e2 instanceof Enemy && !((Enemy) e2).getSpawning() && !((Enemy) e2).isDead())
    		{
    			player.loseHP(enemyAttack);
    		}
    	}
    	else {
    		if(((Enemy) e2).getSpawning() || ((Enemy) e1).getSpawning())
    		{
    			return;
    		}
    		else {
    			((Enemy)e1).handleEnemeyCollision(e2);
    		}
    	}
	}



	public void createStaticLayer()
	{
        staticLayer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D drawing = staticLayer.createGraphics();

        getTileManager().draw(drawing);
        playingUI.drawOnce(drawing);

        drawing.dispose();
    }

    public void gameOver()
    {
    	gameState = GameState.GAME_OVER;
    	gameOverUI.setup();
    }

    public void resetGame()
    {
    	allEntities.clear();
    	aliveEnemies.clear();
    	deActivateRules();
    	activeRules.clear();
    	while(roundCounter >= 0)
    	{
    		rounds[roundCounter].setCurrentWaveIndex(0);
    		roundCounter--;
    	}
    	roundCounter = 0;
    	score = 0;
    	lastWave = false;
    	lastRound = false;
    	player.setX(playerSpawnX);
    	player.setY(playerSpawnY);
    	allEntities.add(player);
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


    public void paintComponent(Graphics g)
    {
    	super.paintComponent(g);
    	Graphics2D drawing = (Graphics2D)(g);

    	switch(gameState)
    	{
	    	case MENU:
	    		menuUI.draw(drawing);
	    		break;
			case RULE_CHOOSING:
				getTileManager().draw(drawing);
	        	player.draw(drawing);
	        	player.drawHP(drawing);
	        	ruleSelectionUI.draw(drawing);
				break;
	    	case PLAYING:
	    	    if (staticLayer != null) {
	    	        drawing.drawImage(staticLayer, 0, 0, null);
	    	    }
	        	if(player.isFrozen()) player.drawFrozen(drawing);
	        	else if(player.isStriking()) player.drawStrike(drawing);
	        	else player.draw(drawing);
	        	player.drawHP(drawing);

	    		for(int i = 0; i < aliveEnemies.size(); i++)
	    		{
	    			aliveEnemies.get(i).draw(drawing);
	    			aliveEnemies.get(i).drawHP(drawing);
	    		}
	    		playingUI.draw(drawing);
	    		break;
	    	case PAUSED:
	    		break;
			case LEADERBOARD:
				leaderBoardUI.draw(drawing);
				break;
	    	case GAME_OVER:
	    		gameOverUI.draw(drawing);
	    		break;
    	}
    	if(player.getKeyInputs().isShowingFPS())
    	{
    		fpsUI.draw(drawing);
    	}
    }


    public void spawnNewWave(){
    	Wave currentWave = rounds[roundCounter].getWaves().get(rounds[roundCounter].getCurrentWaveIndex());
    	for(int i = 0; i < currentWave.getNumberOfEnemies(); i++)
    	{
    		int[] spawnpos = generateRandomSpawnPositions();
    		aliveEnemies.add(new Enemy(this, spawnpos[1], spawnpos[0], enemySpeed,EntityType.ENEMY,enemyMaxHealthPoints,getPlayer()));
    		allEntities.add(aliveEnemies.get(aliveEnemies.size() - 1));
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


    public void checkCollisionEnemy()
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

	public Player getPlayer()
	{
		return player;
	}

	public TileManager getTileManager()
	{
		return tileManager;
	}

	public void setTileManager(TileManager tileManager)
	{
		this.tileManager = tileManager;
	}

	public int getTileSize()
	{
		return tileSize;
	}

	public void setTileSize(int tileSize)
	{
		Game.tileSize = tileSize;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public String getTitle()
	{
		return title;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public GameState getGameState()
	{

		return gameState;
	}

	public JFrame getMainFrame()
	{
		return mainFrame;
	}

	public void setGameState(GameState playing)
	{

		gameState = playing;
	}

	public List<Rules> getAllRules()
	{
		return allRules;
	}

	public void setAllRules(List<Rules> allRules)
	{
		this.allRules = allRules;
	}

	public List<Rules> getActiveRules()
	{
		return activeRules;
	}

	public void setActiveRules(List<Rules> activeRules)
	{
		this.activeRules = activeRules;
	}

	public void activateRule(Rules currentRule)
	{
		switch(currentRule)
		{
		case SLOWED:
			player.setSpeed(player.getSpeed() - speedDeBuff);
			break;
		case FREEZE:
			player.setFreeze(true);
			break;
		case ONE_HP:
			player.setHP(1);
			break;
		case REVERSED_INPUTS:
			player.reverseInputs(true);
			break;
		case FASTER_ENEMIES:
			enemySpeed += speedBuff;
			break;
		case DISARMED:
			player.setDisarmed(true);
			break;
		case WEAKENED_DEFENSE:
			enemyAttack += 1;
			break;
		case RANDOM_TELEPORT:
			player.setRandomTeleport(true);
			break;
		case INCREASED_ENEMIES:
			increaseEnemies(numberOfEnemiesIncrease);
			break;
		}
	}

	public void increaseEnemies(int amount)
	{
		List<Wave> waves = rounds[roundCounter].getWaves();
		for(int i = 0; i < waves.size(); i++)
		{
			waves.get(i).setNumberOfEnemies(amount);
		}
	}

	public void deActivateRules()
	{
		for(int i = 0; i < activeRules.size(); i++)
		{
			switch(activeRules.get(i))
			{
			case SLOWED:
				player.setSpeed(player.getSpeed() + speedDeBuff);
				break;
			case FREEZE:
				player.setFreeze(false);
				break;
			case ONE_HP:
				player.setHP(playerMaxHealthPoints);
				break;
			case REVERSED_INPUTS:
				player.reverseInputs(false);
				break;
			case FASTER_ENEMIES:
				enemySpeed -= speedBuff;
				break;
			case DISARMED:
				player.setDisarmed(false);
				break;
			case WEAKENED_DEFENSE:
				enemyAttack -= 1;
				break;
			case RANDOM_TELEPORT:
				player.setRandomTeleport(false);
				break;
			case INCREASED_ENEMIES:
				increaseEnemies(-numberOfEnemiesIncrease);
				break;


			}
		}
	}

	public CollisionCheck getCollisionCheck()
	{
		return collisionCheck;
	}

	public void setCollisionCheck(CollisionCheck collisionCheck)
	{
		this.collisionCheck = collisionCheck;
	}

	public int getColumnNumber()
	{
		return columnNumber;
	}

	public int getRowNumber()
	{
		return rowNumber;
	}

	public PlayingUI getPlayingUI()
	{
		return this.playingUI;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore( int score)
	{
		this.score = score;
	}

	public int getRoundCounter()
	{
		return roundCounter;
	}

	public Round[] getRounds()
	{
		return rounds;
	}

	public List<Enemy> getAliveEnemies()
	{
		return aliveEnemies;
	}


	public boolean isLastWave()
	{
		return lastWave;
	}

	public boolean isLastRound()
	{
		return lastRound;
	}
	public LeaderBoardUI getLeaderBoardUI()
	{
		return leaderBoardUI;
	}

	public MenuUI getMenuUI()
	{
		return menuUI;
	}

	public GameOverUI getGameOverUI()
	{
		return gameOverUI;
	}

	public void setLastWave(boolean b)
	{
		lastWave = b;
	}

	public Random getRandom()
	{
		return random;
	}

	public boolean[][] getCollisionMap()
	{
		return collisionMap;
	}

	public int getFPS()
	{
		return framesPerSecond;
	}
}
