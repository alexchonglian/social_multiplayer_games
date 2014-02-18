package org.havannah.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.havannah.client.GameApi.Operation;
import org.havannah.client.GameApi.VerifyMove;
import org.havannah.client.GameApi.VerifyMoveDone;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
	
	//won't change it after initialize it
	public List<ImmutableList<Integer>> points;// [[0 0 0],[-1 0 1],[0 -1 1]]
	
	// must be a mutable data structure. keep adding new pieces
	// keep in client side. only send { B: points->clusters, W: points->clusters }
	public Map<ImmutableList<Integer>, Cluster> blackPointClusterMapping;// {[0 0 0]: c1, [-1 0 1]:c2}
	public Map<ImmutableList<Integer>, Cluster> whitePointClusterMapping;// {[0 -1 1]:c3}
	
	public Map<ImmutableList<Integer>, List<ImmutableList<Integer>>> blackPointAdjacency;
	public Map<ImmutableList<Integer>, List<ImmutableList<Integer>>> whitePointAdjacency;
	
	public Map<String, List<ImmutableList<Integer>>> boardState;//{W:[p1, p2], B:[p3, p4]}
	
	private static final String W = "W"; // White hand
	private static final String B = "B"; // Black hand
	
	private final ImmutableMap<String, Object> emptyState = ImmutableMap.<String, Object>of();

	
	/* 
	 * verify(VerifyMove) will filter invalid points before calling these functions below
	 * For them, NO need to check point validity by "points.contains(p)"
	 * 1. distance(p, q)
	 * 2. isNeighbor(p, q)
	 * 3. getNeighborsOf(p)
	 * 4. isCorner(p)
	 * 5. isSide(p)
	 * 
	 */
	public int distance(List<Integer> p, List<Integer> q) {
		// distance([0 0 0], [1 0 -1]) => 2
		int x1 = p.get(0);
		int y1 = p.get(1);
		int z1 = p.get(2);
		int x2 = q.get(0);
		int y2 = q.get(1);
		int z2 = q.get(2);
		return Math.abs(x1-x2) + Math.abs(y1-y2) + Math.abs(z1-z2);
	}
	
	public boolean isNeighbor(List<Integer> p, List<Integer> q) {
		//isNeighbor([0 0 0], [1 0 -1]) => true
		return distance(p, q) == 2;
	}
	
	public List<ImmutableList<Integer>> getNeighborsOf(List<Integer> p) {
		//Invalid points will never appear here. They can't pass the first filter
		
		// normally => [6 points]
		// corner => [3 points]
		// side => [4 points]
		int x = p.get(0);
		int y = p.get(1);
		int z = p.get(2);
		
		int[][] candidates = {{x+1,y-1,z},{x-1,y+1,z}, {x+1,y,z-1},{x-1,y,z+1},
				{x,y+1,z-1},{x,y-1,z+1}};
		
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
	
	public boolean isCornerPoint(List<Integer> p) {
		//this.points.contains(p); // don't check here, check when added to black or white
		return p.contains(this.boardSize - 1) && p.contains(- this.boardSize + 1) && p.contains(0);
	}
	
	public boolean isSidePoint(List<Integer> p) {
		//this.points.contains(p); // don't check here, check when added to black or white
		return p.contains(this.boardSize - 1) ^ p.contains(- this.boardSize + 1);
	}
	

	
	
	public HavannahLogic() {
		this(5);// boardSize = 5 by default
	}
	
	public HavannahLogic(int boardSize) {
		this.boardSize = boardSize;
		this.points = createPoints();
		this.blackPointClusterMapping = new HashMap<ImmutableList<Integer>, Cluster>();
		this.whitePointClusterMapping = new HashMap<ImmutableList<Integer>, Cluster>();
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
	
	public void updatePointClusterMappingAndPointAdjacency () {
		return;
	}
	
	

	public List<ImmutableList<Integer>> findCycleFor(String playerStr, 
			ImmutableList<Integer> newPoint) {
		/*  Call this function when adding new points and think it's likely to be a loop
		 * 
		 *  Definition of cycle in Havannah:
		 *  
		 *  Exists a path [P1, P2, ... Pn] in one player's points such that
		 *  For i in [1,2,3 .. n]:
		 *  	neighbor(pi,p(i+1)mod n) && NOT neighbor(pi,p(i+2)mod n)
		 * 
		 *  This Algorithm uses Depth First Search to detect cycle in one player's pieces
		 *  Use stack!
		 */
		Map<ImmutableList<Integer>, List<ImmutableList<Integer>>> playerPointAdjacency;
		if (playerStr == W) {
			playerPointAdjacency = this.whitePointAdjacency;
		} else if (playerStr == B) {
			playerPointAdjacency = this.blackPointAdjacency;
		} else {
			throw new IllegalArgumentException("playerStr must be W or B");
		}
		// make a copy of playerPointAdjacency
		// this is not the adjacency of all points!!!
		// this is adjacency of only one player's pieces!!!
		Map<ImmutableList<Integer>, List<ImmutableList<Integer>>> neighborLeft = null;
		// neighborLeft = playerPointAdjacency.deepCopy()
		neighborLeft = new HashMap<ImmutableList<Integer>, List<ImmutableList<Integer>>>();
		
		

		List<ImmutableList<Integer>> stack  = new ArrayList<ImmutableList<Integer>>();
		stack.add(newPoint);
		
		while (stack.size() != 0) {// while stack not empty
			// For stack, pop() and push() => so we delete last
			// For neighborLeft, only pop() => we can delete first => which is easier
			// stack[i],stack[i-1] must be neighbor
			// stack[i],stack[i-2] must not be neighbor
			// but java.util.Stack doesn't support peeking the last two element
			// Sad, I have to simulate Stack using List and remove() when pop()
			
			ImmutableList<Integer> top = stack.get(stack.size() - 1);//pop top of stack
			stack.remove(stack.size() - 1);
			
			if (neighborLeft.get(top).size() == 0) {
				// if top has no neighbor left, then pop
				stack.remove(stack.size() - 1);
				
			} else {
				// check top's neighbor, if valid then push to stack
				ImmutableList<Integer> candidate = neighborLeft.get(top).get(1);// pop candidate
				neighborLeft.get(top).remove(candidate);// and delete
				
				if (stack.size() == 1) {
					
				} else {// stack.size() must be greater than 1
					ImmutableList<Integer> previous = stack.get(stack.size() - 2 );
					if (candidate != top && candidate != previous
							&& !playerPointAdjacency.get(candidate).contains(previous)) {
						
						if (stack.contains(candidate)) {
							// if candidate is already in stack[:-2], then cycle detected!
							// better if remove points before the first appearance of candidate
							stack.add(candidate);
						}
						else {stack.add(candidate);}// if not keep searching
						
					}
				}
			}
			
		}
		return null;
	}
	
	// update black and white players' pieces collection after player make move
	// only called after making sure that the position is valid and not occupied by both
	public void addPointToPlayer(ImmutableList<Integer> newPoint, String playerStr)
			throws Exception {
		Map<ImmutableList<Integer>, Cluster> playerCollection;
		if (playerStr == W) {
			playerCollection = this.whitePointClusterMapping;
		} else if (playerStr == B) {
			playerCollection = this.blackPointClusterMapping;
		} else {
			throw new IllegalArgumentException("playerStr must be W or B");
		}
		
		// see if neighbors are in connected component (connected if has cluster label)
		List<ImmutableList<Integer>> neighborsOfNewPoint = getNeighborsOf(newPoint);
		// create map that stores neighbors that has connection (has cluster label)
		Map<ImmutableList<Integer>, Cluster> connectedNeighbors = new HashMap();
		
		for (ImmutableList<Integer> neighborPt: neighborsOfNewPoint) {
			Cluster label = playerCollection.get(neighborPt);
			if ( label != null ) {
				connectedNeighbors.put(neighborPt, label);
			}
		}
		
		int ptIsCorner = this.isCornerPoint(newPoint)? 1:0;
		int ptIsSide = this.isSidePoint(newPoint)? 1:0;
		
		switch (connectedNeighbors.size()) {
		
			case 0:
			/* If you are an island, create a new cluster for you!
			 * 
			 *  0 0      0 0
			 * 0 X 0 => 0 A 0
			 *  0 0      0 0
			 */
				playerCollection.put(newPoint, new Cluster(ptIsCorner, ptIsSide));
				break;
				
				
			case 1:
			/* One neighbor, join him!
			 *  A 0      A 0
			 * 0 X 0 => 0 A 0
			 *  0 0      0 0
			 */
				for (ImmutableList<Integer> pt: connectedNeighbors.keySet()) {
					Cluster cluster1 = playerCollection.get(pt);
					cluster1.addCornerAndSide(ptIsCorner, ptIsSide);
					playerCollection.put(pt, cluster1);
				}
				break;
				
			case 2:
			/* 2 neighbors = { n1 n2 }
			 * if n1.cluster = n2.cluster || not neighbor(n1, n2)  ==> probably a cycle!
			 *  A 0      A 0
			 * 0 X A => 0 A A
			 *  0 0      0 0
			 * 
			 * if n1.cluster = n2.cluster || neighbor(n1, n2)  ==> join them!
			 *  A A      A A
			 * 0 X 0 => 0 A 0
			 *  0 0      0 0
			 *  
			 * if n1.cluster != n2.cluster || not neighbor(n1, n2)  ==> update cluster and player collection
			 *  A 0      C 0
			 * 0 X B => 0 C C
			 *  0 0      0 0
			 *  
			 * if n1.cluster = n2.cluster || not neighbor(n1, n2)  ==> something goes WRONG!
			 *  A B      A B
			 * 0 X 0 => 0 A 0
			 *  0 0      0 0
			 */
				break;
				
			case 3:
				break;
			case 4:
				break;
			case 5:
			/* 5 neighbors must be in same cluster or something goes wrong
			 *  A A      A A
			 * A X 0 => A A 0
			 *  A A      A A
			 */
				break;
			case 6:
			/* 6 neighbors must be in same cluster or something goes wrong
			 *  A A      A A
			 * A X A => A A A
			 *  A A      A A
			 */
				break;
			default:
			// more than 7 neighbor?!
				throw new Exception();
		}

	}
	
	void checkMoveIsLegal(VerifyMove verifyMove) {
		List<Operation> lastMove = verifyMove.getLastMove();
		Map<String, Object> lastState = verifyMove.getLastState();
//		if (lastState = this.boardState || lastMove not in this.boardState) {
//			then legal!
//		}
		
	}
	
	public VerifyMoveDone verify(VerifyMove verifyMove) {
		try {
	    	checkMoveIsLegal(verifyMove);
	    	return new VerifyMoveDone();
	    } catch (Exception e) {
	    	return new VerifyMoveDone(verifyMove.getLastMovePlayerId(), e.getMessage());
	    }
	}
	
	private void check(boolean val, Object... debugArguments) {
		if (!val) {
			throw new RuntimeException("We have a hacker! debugArguments="+ Arrays.toString(debugArguments));
		}
	}
	
//	private VerifyMove move(int lastMovePlayerId, Map<String, Object> state, List<Operation> lastMove) {
//		return new VerifyMove(playersInfo,state,emptyState, lastMove, lastMovePlayerId, ImmutableMap.<Integer, Integer>of());
//	}
//	VerifyMove n = new VerifyMove(List<Map<String, Object>> playersInfo,
//	        Map<String, Object> state,
//	        Map<String, Object> lastState,
//	        List<Operation> lastMove,
//	        int lastMovePlayerId,
//	        Map<Integer, Integer> playerIdToNumberOfTokensInPot);
	
	public static void main(String[] args) {
		HavannahLogic h = new HavannahLogic();
		ImmutableList<Integer> p = ImmutableList.of(4, -1, -3);
		HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
		hash.put(1,10);
		hash.put(2,20);
		hash.put(3,30);
		hash.put(4,40);
	}
}


class Cluster {
	private static int idAccumulator = 0;//make sure unique id
	private int id; 
	private int numCorner;
	private int numSide;
	
	public boolean equals(Cluster c) {
		return this.id == c.id && this.numCorner == c.numCorner && this.numSide == c.numSide;
	}
	
	// Getters and Setters
	public int getId() {
		return id;
	}

	public int getNumCorner() {
		return numCorner;
	}

	public int getNumSide() {
		return numSide;
	}

	public void addCornerAndSide(int extraNumCorner, int extraNumSide) {
		this.numCorner += extraNumCorner;
		this.numSide += extraNumSide;
	}
	
	
	// Contructors
	public Cluster() {
		this(0, 0);
	}
	
	public Cluster (int numCorner, int numSide) {
		this.id = Cluster.idAccumulator++;
		this.numCorner = numCorner;
		this.numSide = numSide;
	}
	
	/*---------------------------------------------------------------------
	 * Illustration of cluster merge
	 *---------------------------------------------------------------------
	 * X is new added point;
	 * A B are cluster labels of 2 neighbors that are held by player;
	 * C is newly created cluster label
	 * 
	 *  A 0      C 0
	 * 0 X B => 0 X C
	 *  0 0      0 0
	 *  
	 *---------------------------------------------------------------------
	 * X is new added point;
	 * A B C are cluster labels of 3 neighbors that are held by player;
	 * C is newly created cluster label
	 * 
	 *  A 0      D 0
	 * 0 X B => 0 X D
	 *  C 0      D 0
	 *  
	 *---------------------------------------------------------------------
	 */
	public Cluster merge(Cluster c) {// merge 2 cluster labels
		int totalNumCorner = this.numCorner + c.numCorner;
		int totalNumSide = this.numSide + c.numSide;
		return new Cluster(totalNumCorner, totalNumSide);
	}
	
	public Cluster merge(Cluster c1, Cluster c2) {// merge 3 cluster labels
		int totalNumCorner = this.numCorner + c1.numCorner + c2.numCorner;
		int totalNumSide = this.numSide + c1.numSide + c2.numSide;
		return new Cluster(totalNumCorner, totalNumSide);
	}
	
}
