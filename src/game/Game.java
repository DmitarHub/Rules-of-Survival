package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import listeners.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import layers.Layer;
import layers.StatsLayer;
import layers.gameover.GameOverLayer;
import layers.ingame.InGameLayer;
import layers.ingame.PausedLayer;
import layers.ingame.RuleChoosingLayer;
import layers.mainmenu.MainMenuLayer;
import listeners.KeyInputs;
import tiles.ScaleTool;


public class Game extends JPanel implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Game gameInstance;
	private JFrame mainFrame;
	
	private Thread gameThread;
	private volatile boolean running;

	private static final int fps = 60;
	private static final double timePerFrame = 1000000000.0 / fps;
    private int frameCounter = 0;
    private int framesPerSecond = 0;
    
	private List<Layer> currentLayers = new CopyOnWriteArrayList<>();
	
	private final String title = "RulesOfSurvival";
	private Image icon;
	
	private int positionChange = 0;
	private boolean awaitingTransition = false;
	private Class<? extends Layer> layerClass;
	private MouseInfo mi = new MouseInfo(this);
	
	private int numberOfPlayers = 1;
	
	private KeyInputs keyInputs = new KeyInputs(this);
	
	private int finalScore = 0;
	
	private Game()
	{
		init();
		start();
	}
	
	public void init()
	{
		this.setPreferredSize(new Dimension(864, 864));
		
		icon = loadIcon("/icon.png");
		mainFrame = new JFrame();
		mainFrame.add(this);
		mainFrame.pack();
		mainFrame.setTitle(title);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setResizable(true);
		mainFrame.setIconImage(icon);
		setFocusable(true);
		mainFrame.setVisible(true);

		addMouseMotionListener(mi);
		addMouseListener(mi);
		addKeyListener(keyInputs);
		currentLayers.add(new MainMenuLayer());
		currentLayers.add(new StatsLayer());
	}
	
    public static BufferedImage loadIcon(String path)
    {
    	try {
    		ScaleTool u = new ScaleTool();

    		BufferedImage icon = ImageIO.read(Game.class.getResourceAsStream(path));
    		return u.scaleImage(icon, 100, 100);
    	} catch (IOException e) {
    		System.out.println("Greska u citanju fajla");
    		return null;
    	}
    }
	
	public static Game Get()
	{
		if(gameInstance == null)
		{
			gameInstance = new Game();
		}
		return gameInstance;
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
            	((StatsLayer)currentLayers.get(currentLayers.size() - 1)).setFPS(framesPerSecond);
            	frameCounter = 0;
            	fpsTimer += 1000;

            }
		}

	}

	public int getFPS() { return framesPerSecond; }

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

	public void update()
    {
		if(awaitingTransition)
		{
			try {
				currentLayers.set(positionChange, layerClass.getDeclaredConstructor().newInstance());
				Layer layer0 = currentLayers.get(0);
				if(layer0 instanceof InGameLayer)
				{
					Layer stats = currentLayers.remove(currentLayers.size() - 1);
					currentLayers.add(new RuleChoosingLayer());
					currentLayers.add(new PausedLayer());
					currentLayers.add(stats);
				}
				else if(layer0 instanceof GameOverLayer || (layer0 instanceof MainMenuLayer && currentLayers.size() == 4))
				{
					Layer stats = currentLayers.remove(currentLayers.size() - 1);
					currentLayers.remove(currentLayers.size() - 1);
					currentLayers.remove(currentLayers.size() - 1);
					currentLayers.add(stats);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
			awaitingTransition = false;
		}
		for(Layer layer : currentLayers) layer.onUpdate();
    }

    public void paintComponent(Graphics g)
    {
    	super.paintComponent(g);
    	Graphics2D graphics = (Graphics2D)(g);
    	for(Layer layer : currentLayers) layer.onRender(graphics);
    }
    
    public List<Layer> getCurrentLayers() { return currentLayers; }

	public <T extends Layer> void queueTransition(Class<T> layerClass, int position) {
		positionChange = position;
		awaitingTransition = true;
		this.layerClass = layerClass;
	}

	public void setNumPlayers(int num) { numberOfPlayers = num; }
	public int getNumberOfPlayers() { return numberOfPlayers; }
	public KeyInputs getKeyInputs() { return keyInputs; }
	public int getFinalScore() { return finalScore; }
	public void setFinalScore(int score) { finalScore = score; }
}
