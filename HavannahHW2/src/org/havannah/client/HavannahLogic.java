package org.havannah.client;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Range;
import com.google.common.collect.ImmutableList;

public class HavannahLogic {
	
	
	/*	
	 * Board representation
	 * 	
	 *    0 0 0 0
	 *   0 0 0 0 0
	 *  0 0 0 0 0 0
	 * 0 0 0 0 0 0 0
	 *  0 0 0 0 0 0
	 *   0 0 0 0 0
	 *    0 0 0 0
	 * 
	 * 		
	 * 
	 * def  points
	 * = { (x,y,z) in { 1-size,..,size-1 }^3  |  x + y + z = 0}
	 * 
	 * // Visualize a 2D plane that cut through the middle of six of the ribs of a cube(3D) 
	 * http://mathworld.wolfram.com/images/eps-gif/CubeHexagon1_800.gif
	 * 
	 * def  distance ((x1,y1,z1) in points, (x2,y2,z2) in points)
	 * = ( |x1-x2| + |y1-y2| + |z1-z2| )/2
	 * 
	 * 
	 * def  neighbour (p,q)
	 * = { p,q in points2 | distance(p,q) = 1}
	 * 
	 * 
	 * def  neighbours_of (p in points)
	 * = { q  in points | (p,q) in neighbour }
	 * 
	 * 
	 * def  corner_points (p)
	 * =  { p in points | count(neighbours_of(p)) = 3 }
	 * 
	 * 
	 * def  side_points (p)
	 * =  { p in points | count(neighbours_of(p)) = 4 }
	 * 
	 */
	
	
	public int boardSize = 5;
	public List<ImmutableList<Integer>> points;
	
	public int distance(ImmutableList<Integer> p, ImmutableList<Integer> q) {
		return 1;
	}
	
	public boolean isNeighbor(ImmutableList<Integer> p, ImmutableList<Integer> q) {
		return true;
	}
	
	public List<ImmutableList<Integer>> NeighborsOf(ImmutableList<Integer> p) {
		return points;
	}
	
	public boolean isCornerPoints(ImmutableList<Integer> p) {
		return true;
	}
	
	public boolean isSidePoints(ImmutableList<Integer> p) {
		return true;
	}
	
	public HavannahLogic(int boardSize) {
		this.boardSize = boardSize;
		this.points = createPoints();
	}
	
	public List<ImmutableList<Integer>> createPoints() {
		
		List<ImmutableList<Integer>> validPoints = new ArrayList<ImmutableList<Integer>>();
		
		for (int x = 1-boardSize; x < boardSize; x++) {
			for (int y = 1-boardSize; y < boardSize; y++) {
				for (int z = 1-boardSize; z < boardSize; z++) {
					if (x+y+z == 0) {
						ImmutableList<Integer> p = ImmutableList.of(x, y, z);
						validPoints.add(p);
					}
				}
			}
		}  
		
		return validPoints;
	}
	
	public void checkMoveIsLegal() {
		return;
	}
	
	public static void main(String[] args) {
		return;
	}

}
