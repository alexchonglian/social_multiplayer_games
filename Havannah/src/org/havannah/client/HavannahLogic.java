package org.havannah.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.havannah.client.GameApi.Operation;
import org.havannah.client.GameApi.VerifyMove;
import org.havannah.client.GameApi.VerifyMoveDone;
import org.havannah.client.GameApi.Set;
import org.havannah.client.GameApi.SetTurn;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class HavannahLogic {
	
	/*
	 * 
	 * @author alexchonglian@gmail.com
	 * 
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
	 * = { p,q in points2 | distance(p,q) = 2}
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
	
	public Map<String, List<ImmutableList<Integer>>> boardState;//{W:[p1, p2], B:[p3, p4]}
	
	private static final String W = "W"; // in the UI White => Red, Black => Blue
	private static final String B = "B"; 
	private static final String addW = "addW";
	private static final String addB = "addB";
	
	private boolean forkFound = false;
	private boolean bridgeFound = false;
	private boolean cycleFound = false;
	
	private final ImmutableMap<String, Object> emptyState = ImmutableMap.<String, Object>of();

	
	/* 
	 * verify(VerifyMove) will filter invalid points before calling these functions below
	 * No need to check point validity by "points.contains(p)"
	 * 1. distance(p, q)
	 * 2. isNeighbor(p, q)
	 * 3. getNeighborsOf(p)
	 * 4. isCorner(p)
	 * 5. isSide(p)
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
	
	
	// when calculate how many unique sides that the connected component contains
	// i need to test if i can add newPoint to uniqueSides = [p1, p2, p3]
	// isUnique = true
	// for p in uniqueSides:
	//   if isOnSameSide(p, newPoint)
	//     isUnique = false;
	//     break
	public boolean isOnSameSide(List<Integer> p, List<Integer> q) {
		int firstProduct = p.get(0) * q.get(0);
		int secondProduct = p.get(1) * q.get(1);
		int thirdProduct = p.get(2) * q.get(2);
		if (firstProduct == 16 || secondProduct == 16 || thirdProduct == 16) {
			return true;
		}
		return false;
	}
	
	public HavannahLogic() {
		this(5);// boardSize = 5 by default
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
	
		

	
	// !!remember to make of copy of the parameters before calling it
	public boolean addPointToStateOf(List<ImmutableList<Integer>> playerPieces, 
			ImmutableList<Integer> newPoint) {
		
		// House Keeping Stuff
		// create numOfCorners = 0
		// uniqueSides = []
		// neighborLeftHashMap = [p, [p's neighbors]]
		int numOfCorners = 0;
		
		List<ImmutableList<Integer>> uniqueSides = new ArrayList<ImmutableList<Integer>>();
		
		Map<ImmutableList<Integer>, Stack<ImmutableList<Integer>>> neighborLeftHashMap = 
				new HashMap<ImmutableList<Integer>, Stack<ImmutableList<Integer>>>();
		
		for (ImmutableList<Integer> p: playerPieces) {
			Stack<ImmutableList<Integer>> pAdj = new Stack<ImmutableList<Integer>>();
			for (ImmutableList<Integer> q: playerPieces) {
				if (!p.equals(q) && isNeighbor(p, q)) {
					//add to adjacency list if its not itself
					pAdj.push(q);
				}
				neighborLeftHashMap.put(p, pAdj);
			}
		}
		
		for (ImmutableList<Integer> p: neighborLeftHashMap.keySet()) {
			System.out.println(p.get(0) + " " + p.get(1) + " " + p.get(2));
			for (ImmutableList<Integer> q: neighborLeftHashMap.get(p)) {
				System.out.println("   " + q.get(0) + " " + q.get(1) + " " + q.get(2));
			}
		}
		
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
		Stack<ImmutableList<Integer>> stack1  = new Stack<ImmutableList<Integer>>();
		stack1.push(newPoint);

		

		Stack<ImmutableList<Integer>> stack2  = new Stack<ImmutableList<Integer>>();
		stack2.push(newPoint);
		
		while (stack2.empty()) {// while stack not empty
			// Find Cycle with Depth First Search!
			// stack[i],stack[i-1] must be neighbor
			// stack[i],stack[i-2] must not be neighbor
			
			ImmutableList<Integer> top = stack2.pop();
			
			if (neighborLeftHashMap.get(top).empty()) {
				// if top has no neighbor left, then keep popping
				stack2.pop();
				
			} else {
				// check top's neighbor, if valid then push to stack
				ImmutableList<Integer> candidate = neighborLeftHashMap.get(top).pop();
				
				if (stack2.size() == 1) {
					
				} else {// stack.size() must be greater than 1
					
					ImmutableList<Integer> previous = stack2.get(stack2.size() - 2 );
					
					if (candidate != top && candidate != previous
							&& !neighborLeftHashMap.get(candidate).contains(previous)) {
						
						if (stack2.contains(candidate)) {
							// if candidate is already in stack[:-2], then cycle detected!
							// better if remove points before the first appearance of candidate
							stack2.push(candidate);
							return true;
						}
						else {stack2.push(candidate);}// if not keep searching
						
					}
				}
			}
		}
		return false;
		
	}

	
	<T> List<T> concat(List<T> a, List<T> b) {
	    return Lists.newArrayList(Iterables.concat(a, b));
	}
	
	public VerifyMoveDone verify(VerifyMove verifyMove) {
		try {
	    	checkMoveIsLegal(verifyMove);
	    	return new VerifyMoveDone();
	    } catch (Exception e) {
	    	return new VerifyMoveDone(verifyMove.getLastMovePlayerId(), e.getMessage());
	    }
	}
	
	void checkMoveIsLegal(VerifyMove verifyMove) {
	    // Checking the operations are as expected.
	    List<Operation> expectedOperations = getExpectedOperations(verifyMove);
	    List<Operation> lastMove = verifyMove.getLastMove();
	    check(expectedOperations.equals(lastMove), expectedOperations, lastMove);
	    // We use SetTurn, so we don't need to check that the correct player did the move.
	    // However, we do need to check the first move is done by the white player (and then in the
	    // first MakeMove we'll send SetTurn which will guarantee the correct player send MakeMove).
	    if (verifyMove.getLastState().isEmpty()) {
	      check(verifyMove.getLastMovePlayerId() == verifyMove.getPlayerIds().get(0));
	    }
	}
	
//	void checkMoveIsLegal(VerifyMove verifyMove) {
//	    // the move is legal if (the new point a valid point) and (it is not occupied)
//		List<Operation> lastMove = verifyMove.getLastMove();
//	    Map<String, Object> lastState = verifyMove.getLastState();
//	    List<Integer> playerIds = verifyMove.getPlayerIds();
//	    if (verifyMove.getLastState().isEmpty()) {
//	    	check(verifyMove.getLastMovePlayerId() == verifyMove.getPlayerIds().get(0));
//	    } else {
//	    	// check the new point is valid (in the HavannahLogic.points collection)
//		    ImmutableList<Integer> newPoint
//	    	= (ImmutableList<Integer>) ((Set) lastMove.get(1)).getValue();
//		    
//		    check(this.points.contains(newPoint));
//		    
//		    List<Object> whitePieces = ((List<Object>) lastState.get(W));
//		    List<Object> blackPieces = ((List<Object>) lastState.get(B));
//		    
//		    check(!whitePieces.contains(newPoint) && !blackPieces.contains(newPoint));
//	    }
//	}
	  
	private void check(boolean val, Object... debugArguments) {
		if (!val) {
			throw new RuntimeException("We have a hacker! debugArguments="+ Arrays.toString(debugArguments));
		}
	}
	
	/**
	* Returns the expected move, which is one of:
	* getMoveInitial, getMoveDeclareCheater, getMoveCheckIfCheated, getMoveClaim.
	*/
	@SuppressWarnings("unchecked")
	List<Operation> getExpectedOperations(VerifyMove verifyMove) {
		List<Operation> lastMove = verifyMove.getLastMove();
		Map<String, Object> lastApiState = verifyMove.getLastState();
		List<Integer> playerIds = verifyMove.getPlayerIds();
	    if (lastApiState.isEmpty()) {
	      return getMoveInitial(playerIds);
	    }
	    // else the operation must be addW or addB
	    int lastMovePlayerId = verifyMove.getLastMovePlayerId();
	    HavannahState lastState = gameApiStateToHavannahState(lastApiState,
	        Color.values()[playerIds.indexOf(lastMovePlayerId)], playerIds);
	    ImmutableList<Integer> newPoint
    	= (ImmutableList<Integer>) ((Set) lastMove.get(1)).getValue();
	    
	    check(this.points.contains(newPoint));
	    
	    List<Object> whitePieces = ((List<Object>) lastApiState.get(W));
	    List<Object> blackPieces = ((List<Object>) lastApiState.get(B));
	    
	    check(!whitePieces.contains(newPoint) && !blackPieces.contains(newPoint));
	    // 3) checking if we had a cheater (then we have Delete(isCheater)).
//	    if (lastMove.contains(new Set(IS_CHEATER, YES))) {
//	      return getMoveDeclareCheater(lastState);
//
//	    } else if (lastMove.contains(new Delete(IS_CHEATER))) {
//	      return getMoveCheckIfCheated(lastState);
//
//	    } else {
//	      List<Integer> lastM = lastState.getMiddle();
//	      Set setM = (Set) lastMove.get(2);
//	      List<Integer> newM = (List<Integer>) setM.getValue();
//	      List<Integer> diffM = subtract(newM, lastM);
//	      Set setClaim = (Set) lastMove.get(3);
//	      Claim claim =
//	          checkNotNull(Claim.fromClaimEntryInGameState((List<String>) setClaim.getValue()));
//	      return getMoveClaim(lastState, claim.getCardRank(), diffM);
//	    }
	    return null;
	  }

	List<Operation> getMoveInitial(List<Integer> playerIds) {
	    int whitePlayerId = playerIds.get(0);
	    int blackPlayerId = playerIds.get(1);
	    List<Operation> operations = Lists.newArrayList();
	    operations.add(new SetTurn(whitePlayerId));
	    operations.add(new Set(W, new ArrayList<ImmutableList<Integer>>()));
	    operations.add(new Set(B, new ArrayList<ImmutableList<Integer>>()));
	    return operations;
	}
	
	public HavannahState gameApiStateToHavannahState(Map<String, Object> state,
			Color turnOfColor, List<Integer> playerIds) {
		return null;
	}
	
	public void printSide(int n) {
		for (int x = 1; x < n; x++) {
			System.out.println(x + " " + (n-x) + " " + (-n));
			ImmutableList<Integer> point = ImmutableList.of(x, n-x, -n);
		}
		System.out.println();
		
		for (int x = 1; x < n; x++) {
			System.out.println((-n+x) + " " + n + " " + (-x));
			ImmutableList<Integer> point = ImmutableList.of(x, n-x, -n);
		}
		System.out.println();
		
		for (int x = 1; x < n; x++) {
			System.out.println(-n + " " + (n-x) + " " + x);
			ImmutableList<Integer> point = ImmutableList.of(x, n-x, -n);
		}
		System.out.println();
		
		for (int x = 1; x < n; x++) {
			System.out.println(-x + " " + (-n+x) + " " + n);
			ImmutableList<Integer> point = ImmutableList.of(x, n-x, -n);
		}
		System.out.println();
		
		for (int x = 1; x < n; x++) {
			System.out.println((n-x) + " " + (-n) + " " + x);
			ImmutableList<Integer> point = ImmutableList.of(x, n-x, -n);
		}
		System.out.println();
		
		for (int x = 1; x < n; x++) {
			System.out.println(n + " " + (x-n) + " " + (-x));
			ImmutableList<Integer> point = ImmutableList.of(x, n-x, -n);
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		HavannahLogic havana = new HavannahLogic();
		ImmutableList<Integer> p = ImmutableList.of(4, -1, -3);
		HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
		hash.put(1,10);
		hash.put(2,20);
		hash.put(3,30);
		hash.put(4,40);
		int n = 4;
		// havana.printSide(n);
		// System.out.println(ImmutableList.<Integer>of(1, -3, 2).equals(ImmutableList.<Integer>of(1, -3, 2)));
		
		List<ImmutableList<Integer>> fork = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, -3, -1),
		  				ImmutableList.<Integer>of(3, -3, 0),
		  				ImmutableList.<Integer>of(3, -4, 1),
		  				ImmutableList.<Integer>of(2, -4, 2),
		  				ImmutableList.<Integer>of(1, -3, 2),
		  				ImmutableList.<Integer>of(0, -3, 3),
		  				ImmutableList.<Integer>of(-1, -3, 4)
		  				);
		ImmutableList<Integer> theMissingPiece = ImmutableList.<Integer>of(-1, -3, 4);
		havana.addPointToStateOf(fork, theMissingPiece);

	}

	public static List<Operation> getMove() {
		// TODO Auto-generated method stub
		return null;
	}




}



