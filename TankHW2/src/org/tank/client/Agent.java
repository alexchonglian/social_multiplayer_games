package org.tank.client;

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
	public static final int GRAVITY = 2;
	
	public int playerNumber;
	
	
	
	
	//constructor
	public Agent(int x, int y, int playerNumber) {
		this.x = x;
		this.y = y;
		if (playerNumber == 0 || playerNumber == 1) {
			this.playerNumber = playerNumber;
		}
	}
	
	public Agent(int x, int y, int playerNumber, GameEngine gameEngine) {
		this(x, y, playerNumber);
		this.env = gameEngine;
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
		
		if (y < GAME_HEIGHT) { dy += GRAVITY; }
		
		if (dx > 0) {dx -= FRICTION;}
		if (dx < 0) {dx += FRICTION;}
		if (dy > 0) {dy -= FRICTION;}
		if (dy < 0) {dy += FRICTION;}
		
		//bounce on boundary
		if (x < 0) { x = 0; int temp = dx; dx = -temp/2; }
		if (y < 0) { y = 0; int temp = dy; dy = -temp/2;}
		if (x > GAME_WIDTH) { x = GAME_WIDTH; int temp = dx; dx = -temp/2;}
		if (y > GAME_HEIGHT-AGENT_HEIGHT) { y = GAME_HEIGHT-AGENT_HEIGHT; int temp = dy; dy = -temp/2;}
		
	}
	

	
	public Bullet fire(int dx, int dy) {
		// starting point is the same as agent
		Bullet b = new Bullet(this.x+AGENT_WIDTH/2, this.y+AGENT_HEIGHT/2, dx, dy);
		return b;
	}
	
	public void KeyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if (playerNumber == 0) {
			
			switch (key) {
			case KeyEvent.VK_0:
				Bullet m = fire(this.dx, this.dy);
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
			
		} else if (playerNumber == 1) {
			
			switch (key) {
			case KeyEvent.VK_SPACE:
				Bullet m = fire(this.dx, this.dy);
				this.env.bulletList.add(m);
				break;
			case KeyEvent.VK_A: 
				controlLeft = true;
				break;
			case KeyEvent.VK_D: 
				controlRight = true;
				break;
			case KeyEvent.VK_W: 
				controlUp = true;
				break;
			case KeyEvent.VK_S:
				controlDown = true;
				break;
			}
			
		}
	}
	

	public void KeyReleased (KeyEvent e) {
		int key = e.getKeyCode();
		if (playerNumber == 0) {
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
		} else if (playerNumber == 1) {
			switch (key) {
			case KeyEvent.VK_A: 
				controlLeft = false;
				break;
			case KeyEvent.VK_D: 
				controlRight = false;
				break;
			case KeyEvent.VK_W: 
				controlUp = false;
				break;
			case KeyEvent.VK_S:
				controlDown = false;
				break;
			}
		}
	}
	
	public void MouseReleased(MouseEvent event) {
		int velocityX = (event.getX() - this.x+AGENT_WIDTH/2)/10;
		int velocityY = - 20 + (event.getY() - this.y+AGENT_HEIGHT/2)/10;
		Bullet m = fire(velocityX, velocityY);
		this.env.bulletList.add(m);
		
		// for debugging
		//System.out.println("Mouse released at x = " + event.getX() + " y = " + event.getY()); 
	}
}
