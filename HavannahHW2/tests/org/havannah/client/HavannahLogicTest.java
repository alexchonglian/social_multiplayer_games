package org.havannah.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.havannah.client.GameApi.GameReady;
import org.havannah.client.GameApi.UpdateUI;
import org.havannah.client.GameApi.MakeMove;
import org.havannah.client.GameApi.VerifyMove;
import org.havannah.client.GameApi.VerifyMoveDone;
import org.havannah.client.GameApi.Operation;
import org.havannah.client.GameApi.Set;
import org.havannah.client.GameApi.SetTurn;
import org.havannah.client.GameApi.EndGame;
import org.havannah.client.GameApi.ManipulateState;
import org.havannah.client.GameApi.ManipulationDone;
import org.havannah.client.GameApi.Delete;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@RunWith(JUnit4.class)
public class HavannahLogicTest {
	/** RULE OF THE GAME */
	private static final String W = "W";
	private static final String B = "B";
	private static final String PLAYER_ID = "playerId";
	private final int wId = 0;
	private final int bId = 1;
	
	private final Map<String, Object> wInfo = ImmutableMap.<String, Object> of(
			PLAYER_ID, wId);
	private final Map<String, Object> bInfo = ImmutableMap.<String, Object> of(
			PLAYER_ID, bId);
	private final List<Map<String, Object>> playersInfo = ImmutableList.of(
			wInfo, bInfo);
	
	private final Map<String, Object> emptyState = ImmutableMap
			.<String, Object> of();
	
	private final Map<String, Object> nonEmptyState = ImmutableMap
			.<String, Object> of("k", "v");
	
	private HavannahLogic havannahLogic = new HavannahLogic(5);
	
	private void assertMoveOk(VerifyMove verifyMove) {
		havannahLogic.checkMoveIsLegal(verifyMove);
	}

	private void assertHacker(VerifyMove verifyMove) {
		VerifyMoveDone verifyDone = havannahLogic.verify(verifyMove);
		assertEquals(verifyMove.getLastMovePlayerId(),
				verifyDone.getHackerPlayerId());
	}
	
	private VerifyMove move(int lastMovePlayerId, 
			Map<String, Object> lastState, List<Operation> lastMove) {
		return new VerifyMove(playersInfo, emptyState,lastState,
				lastMove, lastMovePlayerId, ImmutableMap.<Integer, Integer>of());
	}
	
	@Test
	public void testCorrectNumberOfPoints() {
		// size (points) = 3s^2 - 3s + 1
		int bs = havannahLogic.boardSize;
		int expectedSize = (int) (3*Math.pow(bs, 2) - 3*bs + 1);
		assertEquals(havannahLogic.points.size(), expectedSize);
	}
	
	private final List<ImmutableList<Integer>> neiborsOfPointOrigin = 
	  		ImmutableList.<ImmutableList<Integer>>of(// points around [0 0 0]
	  				ImmutableList.<Integer>of(0, 1, -1),
	  				ImmutableList.<Integer>of(0, -1, 1),
	  				ImmutableList.<Integer>of(1, 0, -1),
	  				ImmutableList.<Integer>of(-1, 0, 1),
	  				ImmutableList.<Integer>of(1, -1, 0),
	  				ImmutableList.<Integer>of(-1, 1, 0)
	  				
	  		);
	
	private final List<ImmutableList<Integer>> neiborsOfPointNeighborOrigin =
			ImmutableList.<ImmutableList<Integer>>of(// points around [0 -1 1]
	  				ImmutableList.<Integer>of(0, 0, 0),
	  				ImmutableList.<Integer>of(-1, 0, 1),
	  				ImmutableList.<Integer>of(-1, -1, 2),
	  				ImmutableList.<Integer>of(0, -2, 2),
	  				ImmutableList.<Integer>of(1, -2, 1),
	  				ImmutableList.<Integer>of(1, -1, 0)
	  		);
	
	private final List<ImmutableList<Integer>> neiborsOfPointSide = 
	  		ImmutableList.<ImmutableList<Integer>>of(// points around [4 -1 -3]
	  				ImmutableList.<Integer>of(4, 0, -4),
	  				ImmutableList.<Integer>of(3, 0, -3),
	  				ImmutableList.<Integer>of(3, -1, -2),
	  				ImmutableList.<Integer>of(4, -2, -2)
	  		);
	
	private final List<ImmutableList<Integer>> neiborsOfPointCorner = 
	  		ImmutableList.<ImmutableList<Integer>>of(// points around [4 -4 0]
	  				ImmutableList.<Integer>of(4, -3, -1),
	  				ImmutableList.<Integer>of(3, -3, 0),
	  				ImmutableList.<Integer>of(3, -4, 1)
	  		);
	
	
	
	private final List<Integer> pointOrigin = ImmutableList.<Integer>of(0, 0, 0);
	private final List<Integer> pointNeighborOrigin = ImmutableList.<Integer>of(0, -1, 1);
	private final List<Integer> pointSide = ImmutableList.<Integer>of(4, -1, -3);
	private final List<Integer> pointCorner = ImmutableList.<Integer>of(4, -4, 0);
	
	// They will filter invalid points before calling these functions
	// Invalid points will never appear here
	@Test
	public void testDistance() {
		// distance([0 0 0], [1 0 -1]) => 2
		assertEquals(havannahLogic.distance(pointOrigin, pointOrigin), 0);
		assertEquals(havannahLogic.distance(pointNeighborOrigin, pointNeighborOrigin), 0);
		assertEquals(havannahLogic.distance(pointOrigin, pointNeighborOrigin), 2);
		assertEquals(havannahLogic.distance(pointOrigin, pointSide), 8);
		assertEquals(havannahLogic.distance(pointNeighborOrigin, pointSide), 8);
		assertEquals(havannahLogic.distance(pointSide, pointSide), 0);
		assertEquals(havannahLogic.distance(pointSide, pointCorner), 6);
		// passing identical points as parameters will never happen in reality
		// we check if point held by players before calling functions
	}
	
	@Test
	public void testIsNeighbor() {
		ImmutableList<Integer> p = ImmutableList.of(0, 0, 0);
		ImmutableList<Integer> q = ImmutableList.of(0, -1, -1);
		ImmutableList<Integer> r = ImmutableList.of(4, -1, -3);
		assertEquals(havannahLogic.isNeighbor(pointOrigin, pointOrigin), false);
		// the above never happen in reality
		// we check identity first and then check adjacency
		assertEquals(havannahLogic.isNeighbor(pointOrigin, pointNeighborOrigin), true);
		assertEquals(havannahLogic.isNeighbor(pointNeighborOrigin, pointOrigin), true);
		assertEquals(havannahLogic.isNeighbor(pointOrigin, pointSide), false);
		assertEquals(havannahLogic.isNeighbor(pointSide, pointOrigin), false);
		assertEquals(havannahLogic.isNeighbor(pointSide, pointCorner), false);
		assertEquals(havannahLogic.isNeighbor(pointCorner, pointSide), false);

	}
	
	@Test
	public void testGetNeighborsOf() {
		
		for (ImmutableList<Integer> pt: neiborsOfPointOrigin) {
			assertTrue(havannahLogic.getNeighborsOf(pointOrigin).contains(pt));
		}
		
		for (ImmutableList<Integer> pt: neiborsOfPointNeighborOrigin) {
			assertTrue(havannahLogic.getNeighborsOf(pointNeighborOrigin).contains(pt));
		}
		
		for (ImmutableList<Integer> pt: neiborsOfPointSide) {
			assertTrue(havannahLogic.getNeighborsOf(pointSide).contains(pt));
		}
		
		for (ImmutableList<Integer> pt: neiborsOfPointCorner) {
			assertTrue(havannahLogic.getNeighborsOf(pointCorner).contains(pt));
		}
	}
	
	@Test
	public void testIsSidePoint() {
		assertEquals(havannahLogic.isSidePoint(pointOrigin), false);
		assertEquals(havannahLogic.isSidePoint(pointNeighborOrigin), false);
		assertEquals(havannahLogic.isSidePoint(pointSide), true);
		assertEquals(havannahLogic.isSidePoint(pointCorner), false);
	}
	
	@Test
	public void testIsCornerPoint() {
		assertEquals(havannahLogic.isCornerPoint(pointOrigin), false);
		assertEquals(havannahLogic.isCornerPoint(pointNeighborOrigin), false);
		assertEquals(havannahLogic.isCornerPoint(pointSide), false);
		assertEquals(havannahLogic.isCornerPoint(pointCorner), true);
	}
	
	@Test
	public void testAddPointToPlayer0Neighbor() {
		
	}
	
	@Test
	public void testAddPointToPlayer1Neighbor() {
		
	}
	
	@Test
	public void testAddPointToPlayer2Neighbors() {
		
	}
	
	@Test
	public void testAddPointToPlayer3Neighbors() {
		
	}
	
	@Test
	public void testAddPointToPlayer4Neighbors() {
		
	}
	
	@Test
	public void testAddPointToPlayer5Neighbors() {
		
	}
	
	@Test
	public void testAddPointToPlayer6Neighbors() {
		
	}
	
	private List<Operation> getInitialOperations() {
	    return havannahLogic.getMoveInitial(ImmutableList.of(wId, bId));
	}
	
	@Test
	public void testInitialMove() {
		assertMoveOk(move(wId, emptyState, getInitialOperations()));
	}

	@Test
	public void testInitialMoveByWrongPlayer() {
		assertHacker(move(bId, emptyState, getInitialOperations()));
	}
	
	@Test
	public void testWhat() {
//		ImmutableList<Operation> claimOfB = ImmutableList.<Operation>of(
//			      new SetTurn(wId),
//			      new Set(B, getIndicesInRange(11, 48)),
//			      new Set(M, getIndicesInRange(49, 51)),
//			      new Set(CLAIM, ImmutableList.of("3cards", "rankJ")));
//		
//		ImmutableList<Operation> claimOfB = ImmutableList.<Operation>of(
//			      new SetTurn(wId),
//			      new Set(B, ImmutableList.<ImmutableList<Integer>>of()),
//			      new Set(W, ImmutableList.<ImmutableList<Integer>>of()),
		
	}
	
}