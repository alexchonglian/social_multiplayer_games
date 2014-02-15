package org.tank.client;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GameEngine extends Frame{
	
	Agent jet0 = new Agent(200, 300, 0, this);
	Agent jet1 = new Agent(600, 300, 1, this);
	ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
	
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	public static final int RATE = 50;
	
	Image imgBuffer = null;

	public void paint(Graphics g) {
		
		jet0.draw(g);
		jet1.draw(g);
		
		for (Bullet b: bulletList) {
			b.draw(g);
		}
	}
 
	public void update(Graphics g) {
		if (imgBuffer == null) {
			imgBuffer = this.createImage(GAME_WIDTH, GAME_HEIGHT); 
		}
		Graphics graphBuffer = imgBuffer.getGraphics();
		Color c = graphBuffer.getColor();
		graphBuffer.setColor(Color.DARK_GRAY);
		graphBuffer.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		g.setColor(c);
		paint(graphBuffer);
		g.drawImage(imgBuffer, 0, 0, null);
	}
	
	public void launchFrame() {
		this.setLocation(400, 300);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setResizable(false);
		this.setTitle("Shooter!");
		this.setBackground(Color.DARK_GRAY);
		this.setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		this.addKeyListener(new KeyTrigger());
		this.addMouseListener(new MouseTrigger());
		
		new Thread(new PaintThread()).start();
	}
	
	public static void main(String[] args) { 
		GameEngine ge = new GameEngine();
		ge.launchFrame();
	}
	
	private class PaintThread implements Runnable {
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(RATE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class KeyTrigger extends KeyAdapter {
		
		public void keyReleased(KeyEvent e) {
			jet0.KeyReleased(e);
			jet1.KeyReleased(e);
		}

		public void keyPressed(KeyEvent e) {
			jet0.KeyPressed(e);
			jet1.KeyPressed(e);
		}
	}
	
	private class MouseTrigger extends MouseAdapter {
		public void mouseReleased(MouseEvent e) { 
			jet0.MouseReleased(e);
			jet1.MouseReleased(e);
		} 
	}
	
}
