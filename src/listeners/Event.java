package listeners;

import java.awt.Point;

public class Event {
	
	private DifferentEvents code;
	private Point p;
	private int keyeventcode;
	private int type;
	
	public Event(DifferentEvents code, Point p, int type)
	{
		this.type = type;
		this.code = code;
		this.p = p;
	}
	
	public Event(DifferentEvents code)
	{
		this.code = code;
	}
	
	public Event(DifferentEvents code, int keyeventcode, int type) {
		this.type = type;
		this.code = code;
	    this.keyeventcode = keyeventcode;
	}

	public DifferentEvents getCode() { return code; }
	public Point getPoint() { return p; }
	public int getKeyEventCode() { return keyeventcode; }
	public int getType() { return type; }
}
