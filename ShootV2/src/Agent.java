import java.awt.*;
import java.awt.event.*;

public class Agent {
	
	// reference to GameEngine environment so that we can add bullets onto it
	GameEngine env;
	private int x;
	private int y;
	private int dx = 0;
	private int dy = 0;
	
	// control signals
	private boolean controlLeft = false;
	private boolean controlRight = false;
	private boolean controlUp = false;
	private boolean controlDown = false;
	
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	
	public static final int AGENT_WIDTH = 20;
	public static final int AGENT_HEIGHT = 20;
	
	public static final int SPEED = 20;
	public static final int ACCELERATION = 10;
	public static final int FRICTION = 1;
	
	
	//constructor
	public Agent(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Agent(int x, int y, GameEngine ge){
		this(x, y);
		this.env = ge;
	}

	
	public void draw(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.WHITE);
		g.fillRect(x, y, AGENT_WIDTH, AGENT_HEIGHT);
		g.setColor(c);

		this.move();
	}
	
	public void move() {
		x += dx;
		y += dy;
		if (controlLeft == true) { dx -= ACCELERATION; }
		if (controlRight == true) { dx += ACCELERATION; }
		if (controlUp == true) { dy -= ACCELERATION; }
		if (controlDown == true) { dy += ACCELERATION; }
		
		
		if (dx > 0) {dx -= FRICTION;}
		if (dx < 0) {dx += FRICTION;}
		if (dy > 0) {dy -= FRICTION;}
		if (dy < 0) {dy += FRICTION;}
		
		//bounce on boundary
		if (x < 0) { x = 0; int temp = dx; dx = -temp; }
		if (y < 0) { y = 0; int temp = dy; dy = -temp;}
		if (x > GAME_WIDTH) { x = GAME_WIDTH; int temp = dx; dx = -temp;}
		if (y > GAME_HEIGHT) { y = GAME_HEIGHT; int temp = dy; dy = -temp;}
		
	}
	

	
	public Bullet fire() {
		Bullet b = new Bullet(this.x+AGENT_WIDTH/2, this.y+AGENT_HEIGHT/2, this.dx, this.dy);
		return b;
	}
	
	public void KeyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		switch (key) {
		case KeyEvent.VK_SPACE:
			Bullet m = fire();
			this.env.bulletList.add(m);
			break;
		case KeyEvent.VK_LEFT: 
			controlLeft = true;
			break;
		case KeyEvent.VK_RIGHT: 
			controlRight = true;
			break;
		case KeyEvent.VK_UP: 
			controlUp = true;
			break;
		case KeyEvent.VK_DOWN:
			controlDown = true;
			break;
		}
	}
	

	public void KeyReleased (KeyEvent e) {
		int key = e.getKeyCode();
		
		switch (key) {
		case KeyEvent.VK_LEFT: 
			controlLeft = false;
			break;
		case KeyEvent.VK_RIGHT: 
			controlRight = false;
			break;
		case KeyEvent.VK_UP: 
			controlUp = false;
			break;
		case KeyEvent.VK_DOWN:
			controlDown = false;
			break;
		}
	}
}
