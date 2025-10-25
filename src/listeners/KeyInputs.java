package listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import game.Game;
import layers.Layer;
import layers.StatsLayer;
import layers.gameover.GameOverLayer;

public class KeyInputs implements KeyListener{

	private boolean up, down, left, right;
	private boolean w, s, a, d;
	private boolean space, enter;

	private StringBuilder typedBuffer = new StringBuilder();
	private boolean backspace = false;
	
	
	
	private Game game;

	public KeyInputs(Game game)
	{
		this.game = game;
	}


	@Override
	public void keyTyped(KeyEvent e)
	{
	    char c = e.getKeyChar();
	    if ((Character.isLetterOrDigit(c) || Character.isSpaceChar(c)) &&  game.getCurrentLayers().get(0) instanceof GameOverLayer) {
	        typedBuffer.append(c);
	    }
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		List<Layer> temp =  game.getCurrentLayers();
		if(e.getKeyCode() == KeyEvent.VK_F && e.isControlDown())
		{
			((StatsLayer)temp.get(temp.size() - 1)).toggleShow();
		}
		else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			for(Layer l : temp)
			{
				l.onEvent(new Event(DifferentEvents.TOGGLEPAUSE, e.getKeyCode()));
			}
		}
		else
		{
			switch(code)
			{
				case KeyEvent.VK_W:
					w = true;
					break;
				case KeyEvent.VK_A:
					a = true;
					break;
				case KeyEvent.VK_S:
					s = true;
					break;
				case KeyEvent.VK_D:
					d = true;
					break;
				case KeyEvent.VK_UP:
					up = true;
					break;
				case KeyEvent.VK_LEFT:
					left = true;
					break;
				case KeyEvent.VK_DOWN:
					down = true;
					break;
				case KeyEvent.VK_RIGHT:
					right = true;
					break;
				case KeyEvent.VK_SPACE:
					space = true;
					break;
				case KeyEvent.VK_ENTER:
					enter = true;
					break;
				case KeyEvent.VK_BACK_SPACE:
					backspace = true;
					break;
				default:
					break;
			}
		}
	}

	
	@Override
	public void keyReleased(KeyEvent e)
	{
		int code = e.getKeyCode();
		switch(code)
		{
			case KeyEvent.VK_W:
				w = false;
				break;
			case KeyEvent.VK_A:
				a = false;
				break;
			case KeyEvent.VK_S:
				s = false;
				break;
			case KeyEvent.VK_D:
				d = false;
				break;
			case KeyEvent.VK_UP:
				up = false;
				break;
			case KeyEvent.VK_LEFT:
				left = false;
				break;
			case KeyEvent.VK_DOWN:
				down = false;
				break;
			case KeyEvent.VK_RIGHT:
				right = false;
				break;
			case KeyEvent.VK_SPACE:
				space = false;
				break;
			case KeyEvent.VK_ENTER:
				enter = false;
				break;
			case KeyEvent.VK_BACK_SPACE:
				backspace = false;
				break;
			default:
				break;
		}
	}

	public String getTypedText()
	{
	    String typed = typedBuffer.toString();
	    typedBuffer.setLength(0);
	    return typed;
	}

	public boolean isBackspace()
	{
	    boolean pressed = backspace;
	    backspace = false;
	    return pressed;
	}
	
	public boolean isUp() { return up;}
	public boolean isDown() { return down; }
	public boolean isLeft() { return left; }
	public boolean isRight() { return right; }
	public boolean isW() { return w;}
	public boolean isS() { return s; }
	public boolean isA() { return a; }
	public boolean isD() { return d; }
	public boolean isSpace() { return space; }
	public boolean isEnter() { return enter; }
	
}
