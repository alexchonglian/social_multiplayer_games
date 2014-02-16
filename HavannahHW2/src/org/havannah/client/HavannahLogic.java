package org.havannah.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.havannah.client.GameApi.Operation;
import org.havannah.client.GameApi.VerifyMove;
import org.havannah.client.GameApi.VerifyMoveDone;

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
	 * def  points
	 * = { (x,y,z) in { 1-size,..,size-1 }^3  |  x + y + z = 0}
	 * 
	 * Visualize a 2D plane that cut through the middle of six of the ribs of a cube(3D) 
	 * 
	 * >> http://mathworld.wolfram.com/images/eps-gif/CubeHexagon1_800.gif
	 * 
	 * 
	 *    (0 1 -1)--(-1 1 0)
	 *     /   \      /   \
	 *    /     \    /     \
	 * (1 0 -1)-(0 0 0)-(-1 0 1)
	 *    \     /    \     /
	 *     \   /      \   /
	 *    (1 -1 0)--(0 -1 1)
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
	 * = { inc/dec x,y,z and eliminate ones out of boundary(-5 or +5)} // Faster
	 * 
	 * 
	 * def  corner_points (p)
	 * = { p in points | count(neighbours_of(p)) = 3 }
	 * = { set(p) = set(-4, 4, 0)} 
	 * = { 4 in p and -4 in p } // Faster
	 * 
	 * 
	 * def  side_points (p)
	 * = { p in points | count(neighbours_of(p)) = 4 }
	 * = { 4 in p xor -4 in p } // Faster
	 * 
	 * 
	 */
	
	
	public int boardSize;
	
	public List<ImmutableList<Integer>> points;// [[0 0 0],[-1 0 1],[0 -1 1]]
	
	public Map<ImmutableList<Integer>, ClusterLabel> black;// {[0 0 0]: c1, [-1 0 1]:c2}
	
	public Map<ImmutableList<Integer>, ClusterLabel> white;// {[0 -1 1]:c3}
	
	private static final String W = "W"; // White hand
	private static final String B = "B"; // Black hand
	
	public int distance(ImmutableList<Integer> p, ImmutableList<Integer> q) {
		//distance([0 0 0], [1 0 -1]) => 2
		//this.points.contains(p); 
		//this.points.contains(q); 
		// don't check here, check when added to black or white
		int x1 = p.get(0);
		int y1 = p.get(1);
		int z1 = p.get(2);
		int x2 = q.get(0);
		int y2 = q.get(1);
		int z2 = q.get(2);
		return Math.abs(x1-x2) + Math.abs(y1-y2) + Math.abs(z1-z2);
	}
	
	public boolean isNeighbor(ImmutableList<Integer> p, ImmutableList<Integer> q) {
		//isNeighbor([0 0 0], [1 0 -1]) => true
		//this.points.contains(p); // don't check here, check when added to black or white
		return distance(p, q) == 2;
	}
	
	public List<ImmutableList<Integer>> getNeighborsOf(ImmutableList<Integer> p) {
		// normally => [6 points]
		// corner => [3 points]
		// side => [4 points]
		int x = p.get(0);
		int y = p.get(1);
		int z = p.get(2);
		
		int[][] candidates = {{x+1,y-1,z},{x-1,y+1,z},
				{x+1,y,z-1},{x-1,y,z+1},
				{x,y+1,z-1},{x,y-1,z+1},};
		
		List<ImmutableList<Integer>> neighbors = new ArrayList<ImmutableList<Integer>>();
		
		for (int i=0; i<candidates.length; i++) {
			int a = candidates[i][0];
			int b = candidates[i][1];
			int c = candidates[i][2];
			int s = this.boardSize;
			if (a<s && a>-s && b<s && b>-s && c<s && c>-s) {
				ImmutableList<Integer> validPoint = ImmutableList.of(a, b, c);
				neighbors.add(validPoint);
			}
		}
		return neighbors;
	}
	
	public boolean isCornerPoint(ImmutableList<Integer> p) {
		//this.points.contains(p); // don't check here, check when added to black or white
		return p.contains(4) && p.contains(-4) && p.contains(0);
	}
	
	public boolean isSidePoint(ImmutableList<Integer> p) {
		//this.points.contains(p); // don't check here, check when added to black or white
		return p.contains(4) ^ p.contains(-4);
	}
	

	
	

	public HavannahLogic() {
		this.boardSize = 5;// by default
		this.points = createPoints();
	}
	
	public HavannahLogic(int boardSize) {
		this.boardSize = boardSize;
		this.points = createPoints();
	}
	
	public List<ImmutableList<Integer>> createPoints() {
		
		List<ImmutableList<Integer>> points = new ArrayList<ImmutableList<Integer>>();
		
		for (int x = 1-boardSize; x < boardSize; x++) {
			for (int y = 1-boardSize; y < boardSize; y++) {
				for (int z = 1-boardSize; z < boardSize; z++) {
					if (x+y+z == 0) {
						ImmutableList<Integer> point = ImmutableList.of(x, y, z);
						points.add(point);
					}
				}
			}
		}
		
		return points;
	}
	
	void checkMoveIsLegal(VerifyMove verifyMove) {
		List<Operation> lastMove = verifyMove.getLastMove();
		Map<String, Object> lastState = verifyMove.getLastState();
		// Check the operations are as expected.
		List<Operation> expectedOperations = getExpectedOperations(
				lastState, lastMove, verifyMove.getPlayerIds(), verifyMove.getLastMovePlayerId());
		check(expectedOperations.equals(lastMove), expectedOperations, lastMove);
		// We use SetTurn, so we don't need to check that the correct player did the move.
		// However, we do need to check the first move is done by the white player (and then in the
		// first MakeMove we'll send SetTurn which will guarantee the correct player send MakeMove).
		if (lastState.isEmpty()) {
			check(verifyMove.getLastMovePlayerId() == verifyMove.getPlayerIds().get(0));
		}
	}
	
	public VerifyMoveDone verify(VerifyMove verifyMove) {
		return new VerifyMoveDone();
	}
	
	public static void main(String[] args) {
		HavannahLogic h = new HavannahLogic();
		ImmutableList<Integer> p = ImmutableList.of(4, -1, -3);
		h.getNeighborsOf(p);
	}
	


}


class ClusterLabel {
	private static int idAccumulator = 0;//make sure unique id
	public int id; 
	int numCorner;
	int numSide;
	
	public ClusterLabel() {
		this.id = ClusterLabel.idAccumulator++;
		this.numCorner = 0;
		this.numSide = 0;
	}
	
	public ClusterLabel (int numCorner, int numSide) {
		this.id = ClusterLabel.idAccumulator++;
		this.numCorner = numCorner;
		this.numSide = numSide;
	}
	
	public ClusterLabel merge(ClusterLabel c) {
		int totalNumCorner = this.numCorner + c.numCorner;
		int totalNumSide = this.numSide + c.numSide;
		return new ClusterLabel(totalNumCorner, totalNumSide);
	}
	
	public ClusterLabel merge(ClusterLabel c1, ClusterLabel c2) {
		int totalNumCorner = this.numCorner + c1.numCorner + c2.numCorner;
		int totalNumSide = this.numSide + c1.numSide + c2.numSide;
		return new ClusterLabel(totalNumCorner, totalNumSide);
	}
	
}
