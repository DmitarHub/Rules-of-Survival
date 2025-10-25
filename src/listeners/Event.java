package listeners;

import java.awt.Point;

public class Event {
	
	private DifferentEvents code;
	private Point p;
	private int keyeventcode;
	
	public Event(DifferentEvents code, Point p)
	{
		this.code = code;
		this.p = p;
	}
	
	public Event(DifferentEvents code)
	{
		this.code = code;
	}
	
	public Event(DifferentEvents code, int keyeventcode) {
		this.code = code;
	    this.keyeventcode = keyeventcode;
	}

	public DifferentEvents getCode() { return code; }
	public Point getPoint() { return p; }
	public int getKeyEventCode() { return keyeventcode; }
}
