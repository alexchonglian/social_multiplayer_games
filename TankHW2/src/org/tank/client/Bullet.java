package org.tank.client;

import java.awt.*;


public class Bullet {
	private int x;
	private int y;
	private int dx;
	private int dy;
	
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	public static final int GRAVITY = 2;
	
	public Bullet(int x, int y, int dx, int dy) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
	public void draw(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.RED);
		g.fillOval(x, y, 10, 10);
		
		this.move();
	}
	
	private void move() {
		x += dx;
		y += dy;
		
		if (y < GAME_HEIGHT) { dy += GRAVITY; }
		
		//bounce on boundary
		if (x < 0) { x = 0; int temp = dx; dx = -temp/2; }
		if (y < 0) { y = 0; int temp = dy; dy = -temp/2;}
		if (x > GAME_WIDTH) { x = GAME_WIDTH; int temp = dx; dx = -temp/2;}
		if (y > GAME_HEIGHT) { y = GAME_HEIGHT; dy = 0; }//y = GAME_HEIGHT; int temp = dy; dy = -temp/2;}
	}
	
}
