package listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.Game;
import game.GameState;

public class KeyInputs implements KeyListener{

	private boolean up, down, left, right, escape, enter, showFPS;
	private boolean reversed = false;

	private StringBuilder typedBuffer = new StringBuilder();
	private boolean backspace = false;

	private Game game;

	public KeyInputs(Game game)
	{
		this.game = game;
	}

	public void reverse(boolean r)
	{
		reversed = r;
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	    char c = e.getKeyChar();
	    if (Character.isLetterOrDigit(c) || Character.isSpaceChar(c) && game.getGameState() == GameState.GAME_OVER) {
	        typedBuffer.append(c);
	    }
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_F && (e.isControlDown() || e.isMetaDown()))
		{
			showFPS = !showFPS;
		}
		switch(code)
		{
			case KeyEvent.VK_W:
				if(!reversed) up = true;
				else down = true;
				break;
			case KeyEvent.VK_A:
				if(!reversed) left = true;
				else right = true;
				break;
			case KeyEvent.VK_S:
				if(!reversed) down = true;
				else up = true;
				break;
			case KeyEvent.VK_D:
				if(!reversed) right = true;
				else left = true;
				break;
			case KeyEvent.VK_ESCAPE:
				escape = true;
				break;
			case KeyEvent.VK_ENTER:
				enter = true;
				break;
			case KeyEvent.VK_BACK_SPACE:
				if(game.getGameState() == GameState.GAME_OVER) backspace = true;
				break;
			default:
				break;
		}

	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		int code = e.getKeyCode();
		switch(code)
		{
			case KeyEvent.VK_W:
				if(!reversed) up = false;
				else down = false;
				break;
			case KeyEvent.VK_A:
				if(!reversed) left = false;
				else right = false;
				break;
			case KeyEvent.VK_S:
				if(!reversed) down = false;
				else up = false;
				break;
			case KeyEvent.VK_D:
				if(!reversed) right = false;
				else left = false;
				break;
			case KeyEvent.VK_ESCAPE:
				escape = false;
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

	public boolean isUp() {
		return up;
	}

	public boolean isDown() {
		return down;
	}

	public boolean isLeft() {
		return left;
	}

	public boolean isRight() {
		return right;
	}

	public boolean isEscape() {
		return escape;
	}

	public boolean isEnter() {
		return enter;
	}

	public boolean isShowingFPS()
	{
		return showFPS;
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
}
