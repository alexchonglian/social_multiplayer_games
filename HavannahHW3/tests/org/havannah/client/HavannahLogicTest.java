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
	private static final String addW = "addW";
	private static final String addB = "addB";
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
	
	/*
	 * Part One - Board Representation
	 * 
	 * 1. testCorrectNumberOfPoints
	 * 2. testDistance
	 * 3. testIsNeighbor
	 * 4. testGetNeighbors
	 * 5. testSidePoint
	 * 6. testCornerPoint
	 */
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
	
	
	
	/*
	 * Part Two - Initial Move
	 * 
	 * 1. testLegalInitialMove
	 * 2. testIllegalInitialMove
	 */
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
	public void testInitialMoveFromNonEmptyState() {
		assertHacker(move(wId, nonEmptyState, getInitialOperations()));
	}
	
	@Test
	public void testInitialMoveWithExtraOperation() {
		List<Operation> initialOperations = getInitialOperations();
		initialOperations.add(new Set(addW, ImmutableList.of()));
		assertHacker(move(wId, emptyState, initialOperations));
	}
	
	
	
	/*
	 * Part Three - When game is in progress
	 * 
	 * 0. can add piece to unoccupied place
	 * 1. can't add piece to points of your own 
	 * 2. can't add piece to points of your opponent
	 */
	private final List<Integer> leftOfOrigin = ImmutableList.<Integer>of(1, 0, -1);
	private final List<Integer> rightOfOrigin = ImmutableList.<Integer>of(-1, 0, 1);
	
	@Test
	public void testOkForWhiteToTakeUnoccupiedPoint() {
		Map<String, Object> onlyOneWhiteAndOneBlackOnBoard = ImmutableMap.<String, Object>builder()
				.put(W, ImmutableList.of(leftOfOrigin))
				.put(B, ImmutableList.of(rightOfOrigin))
				.build();
		List<Operation> whiteTakeOrigin = ImmutableList.of(
				new SetTurn(bId),
				new Set(addW, pointOrigin));
		assertMoveOk(move(wId, onlyOneWhiteAndOneBlackOnBoard, whiteTakeOrigin));
	}
	
	@Test
	public void testOkForBlackToTakeUnoccupiedPoint() {
		Map<String, Object> onlyOneWhiteAndOneBlackOnBoard = ImmutableMap.<String, Object>builder()
				.put(W, ImmutableList.of(leftOfOrigin))
				.put(B, ImmutableList.of(rightOfOrigin))
				.build();
		List<Operation> blackTakeOrigin = ImmutableList.of(
				new SetTurn(wId),
				new Set(addB, pointOrigin));
		assertMoveOk(move(bId, onlyOneWhiteAndOneBlackOnBoard, blackTakeOrigin));
	}
	
	@Test
	public void testWhiteFailToAddPieceToPointOccupiedByBlack() {
		Map<String, Object> onlyOneWhiteAndOneBlackOnBoard = ImmutableMap.<String, Object>builder()
				.put(W, ImmutableList.of(leftOfOrigin))
				.put(B, ImmutableList.of(rightOfOrigin))
				.build();
		List<Operation> whiteTakeRightOfOrigin = ImmutableList.of(
				new SetTurn(bId),
				new Set(addW, rightOfOrigin));
		assertHacker(move(wId, onlyOneWhiteAndOneBlackOnBoard, whiteTakeRightOfOrigin));
	}
	
	@Test
	public void testWhiteFailToAddPieceToPointOccupiedByWhite() {
		Map<String, Object> onlyOneWhiteAndOneBlackOnBoard = ImmutableMap.<String, Object>builder()
				.put(W, ImmutableList.of(leftOfOrigin))
				.put(B, ImmutableList.of(rightOfOrigin))
				.build();
		List<Operation> whiteTakeLeftOfOrigin = ImmutableList.of(
				new SetTurn(bId),
				new Set(addW, leftOfOrigin));
		assertHacker(move(wId, onlyOneWhiteAndOneBlackOnBoard, whiteTakeLeftOfOrigin));
	}
	
	@Test
	public void testBlackFailToAddPieceToPointOccupiedByBlack() {
		Map<String, Object> onlyOneWhiteAndOneBlackOnBoard = ImmutableMap.<String, Object>builder()
				.put(W, ImmutableList.of(leftOfOrigin))
				.put(B, ImmutableList.of(rightOfOrigin))
				.build();
		List<Operation> whiteTakeRightOfOrigin = ImmutableList.of(
				new SetTurn(bId),
				new Set(addW, rightOfOrigin));
		assertHacker(move(bId, onlyOneWhiteAndOneBlackOnBoard, whiteTakeRightOfOrigin));
	}
	
	@Test
	public void testBlackFailToAddPieceToPointOccupiedByWhite() {
		Map<String, Object> onlyOneWhiteAndOneBlackOnBoard = ImmutableMap.<String, Object>builder()
				.put(W, ImmutableList.of(leftOfOrigin))
				.put(B, ImmutableList.of(rightOfOrigin))
				.build();
		List<Operation> whiteTakeLeftOfOrigin = ImmutableList.of(
				new SetTurn(bId),
				new Set(addW, leftOfOrigin));
		assertHacker(move(bId, onlyOneWhiteAndOneBlackOnBoard, whiteTakeLeftOfOrigin));
	}
	
	
	/*
	 * Part Four - HavannahLogic.detect()
	 * 
	 * the way I detect fork, bridge, cycle is to start from a new point
	 */
	@Test
	public void testFork() {
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
		//addPointToStateOf(B/W, newPiece)
		assertTrue(havannahLogic.addPointToStateOf(
				ImmutableList.copyOf(fork), ImmutableList.copyOf(theMissingPiece)));
	}
	
	@Test
	public void testBridge() {
		List<ImmutableList<Integer>> bridge = 
				ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, -4, 0),
		  				ImmutableList.<Integer>of(3, -4, 1),
		  				ImmutableList.<Integer>of(2, -4, 2),
		  				ImmutableList.<Integer>of(1, -3, 2),
		  				ImmutableList.<Integer>of(0, -3, 3),
		  				
		  				ImmutableList.<Integer>of(0, -4, 4)
		  				);
		ImmutableList<Integer> theMissingPiece = ImmutableList.<Integer>of(0, -4, 4);
		assertTrue(havannahLogic.addPointToStateOf(
				ImmutableList.copyOf(bridge), ImmutableList.copyOf(theMissingPiece)));
	}
	
	@Test
	public void testCycle() {
		List<ImmutableList<Integer>> cycle = 
				ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(1, 0, -1),
		  				ImmutableList.<Integer>of(1, -1, 0),
		  				ImmutableList.<Integer>of(0, -1, 1),
		  				ImmutableList.<Integer>of(-1, 0, 1),
		  				ImmutableList.<Integer>of(-1, 1, 0),
		  				ImmutableList.<Integer>of(0, 0, 0),
		  				
		  				ImmutableList.<Integer>of(0, 1, -1)
		  				);
		ImmutableList<Integer> theMissingPiece = ImmutableList.<Integer>of(0, 1, -1);
		assertTrue(havannahLogic.addPointToStateOf(
				ImmutableList.copyOf(cycle), ImmutableList.copyOf(theMissingPiece)));
	}
	
	@Test
	public void testNotFork() {
		List<ImmutableList<Integer>> almostFork = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, -3, -1),
		  				ImmutableList.<Integer>of(3, -3, 0),
		  				ImmutableList.<Integer>of(3, -4, 1),
		  				ImmutableList.<Integer>of(2, -4, 2),
		  				ImmutableList.<Integer>of(1, -3, 2),
		  				ImmutableList.<Integer>of(0, -3, 3),
		  				
		  				ImmutableList.<Integer>of(0, 4, -4)
		  				);
		ImmutableList<Integer> irrelevantMove = ImmutableList.<Integer>of(0, 4, -4);
		assertTrue(!havannahLogic.addPointToStateOf(
				ImmutableList.copyOf(almostFork), ImmutableList.copyOf(irrelevantMove)));
	}
	
	@Test
	public void testNotBridge() {
		List<ImmutableList<Integer>> almostBridge = 
				ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, -4, 0),
		  				ImmutableList.<Integer>of(3, -4, 1),
		  				ImmutableList.<Integer>of(2, -4, 2),
		  				ImmutableList.<Integer>of(1, -3, 2),
		  				ImmutableList.<Integer>of(0, -3, 3),
		  				
		  				ImmutableList.<Integer>of(0, -4, 4)
		  				);
		ImmutableList<Integer> irrelevantMove = ImmutableList.<Integer>of(0, 4, -4);
		assertTrue(!havannahLogic.addPointToStateOf(
				ImmutableList.copyOf(almostBridge), ImmutableList.copyOf(irrelevantMove)));
	}
	
	@Test
	public void testNotCycle() {
		List<ImmutableList<Integer>> almostCycle = 
				ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(1, 0, -1),
		  				ImmutableList.<Integer>of(1, -1, 0),
		  				ImmutableList.<Integer>of(0, -1, 1),
		  				ImmutableList.<Integer>of(-1, 0, 1),
		  				ImmutableList.<Integer>of(-1, 1, 0),
		  				ImmutableList.<Integer>of(0, 0, 0),
		  				
		  				ImmutableList.<Integer>of(0, 4, -4)
		  				);
		ImmutableList<Integer> irrelevantMove = ImmutableList.<Integer>of(0, 4, -4);
		assertTrue(!havannahLogic.addPointToStateOf(
				ImmutableList.copyOf(almostCycle), ImmutableList.copyOf(irrelevantMove)));
	}
	
	
	
	/*
	 * Part Five - End Game
	 * 
	 * {W, B} ends with {fork, bridge, cycle} which is {truthful, fake}
	 * 
	 */
	@Test
	public void testWhiteEndGameWithFork() {
		List<ImmutableList<Integer>> whiteOneStepCloserToFork = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, -3, -1),
		  				ImmutableList.<Integer>of(3, -3, 0),
		  				ImmutableList.<Integer>of(3, -4, 1),
		  				ImmutableList.<Integer>of(2, -4, 2),
		  				ImmutableList.<Integer>of(1, -3, 2),
		  				ImmutableList.<Integer>of(0, -3, 3)
		  				);
		List<ImmutableList<Integer>> blackPoints = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, 0, -4),
		  				ImmutableList.<Integer>of(3, 0, -3),
		  				ImmutableList.<Integer>of(2, 0, -2),
		  				ImmutableList.<Integer>of(1, 0, -1),
		  				ImmutableList.<Integer>of(0, 0, 0),
		  				ImmutableList.<Integer>of(-1, 0, 1)
		  				);
		Map<String, Object> state = ImmutableMap.<String, Object>builder()
				.put(W, whiteOneStepCloserToFork)
				.put(B, blackPoints)
				.build();
		// The order of operations: SetTurn, SetW/B/addW/addB, EndGame
		List<Operation> operations = ImmutableList.of(
				new SetTurn(wId),
				new Set(addW, ImmutableList.<Integer>of(-1, -3, 4)),
				new EndGame(wId));
		assertMoveOk(move(wId, state, operations));
	}
	
	@Test
	public void testWhiteEndGameWithBridge() {
		List<ImmutableList<Integer>> whiteOneStepCloserToBridge = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, -4, 0),
		  				ImmutableList.<Integer>of(3, -4, 1),
		  				ImmutableList.<Integer>of(2, -4, 2),
		  				ImmutableList.<Integer>of(1, -3, 2),
		  				ImmutableList.<Integer>of(0, -3, 3)
		  				);
		List<ImmutableList<Integer>> blackPoints = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, 0, -4),
		  				ImmutableList.<Integer>of(3, 0, -3),
		  				ImmutableList.<Integer>of(2, 0, -2),
		  				ImmutableList.<Integer>of(1, 0, -1),
		  				ImmutableList.<Integer>of(0, 0, 0)
		  				);
		Map<String, Object> state = ImmutableMap.<String, Object>builder()
				.put(W, whiteOneStepCloserToBridge)
				.put(B, blackPoints)
				.build();
		// The order of operations: SetTurn, SetW/B/addW/addB, EndGame
		List<Operation> operations = ImmutableList.of(
				new SetTurn(bId),
				new Set(addB, ImmutableList.<Integer>of(0, -4, 4)),
				new EndGame(wId));
		assertMoveOk(move(wId, state, operations));
	}
	
	@Test
	public void testWhiteEndGameWithCycle() {
		List<ImmutableList<Integer>> whiteOneStepCloserToCycle = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(1, 0, -1),
		  				ImmutableList.<Integer>of(1, -1, 0),
		  				ImmutableList.<Integer>of(0, -1, 1),
		  				ImmutableList.<Integer>of(-1, 0, 1),
		  				ImmutableList.<Integer>of(-1, 1, 0)
		  				);
		List<ImmutableList<Integer>> blackPoints = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, 0, -4),
		  				ImmutableList.<Integer>of(3, 0, -3),
		  				ImmutableList.<Integer>of(2, 0, -2),
		  				ImmutableList.<Integer>of(1, 0, -1),
		  				ImmutableList.<Integer>of(0, 0, 0)
		  				);
		Map<String, Object> state = ImmutableMap.<String, Object>builder()
				.put(W, whiteOneStepCloserToCycle)
				.put(B, blackPoints)
				.build();
		// The order of operations: SetTurn, SetW/B/addW/addB, EndGame
		List<Operation> operations = ImmutableList.of(
				new SetTurn(bId),
				new Set(addW, ImmutableList.<Integer>of(0, 1, -1)),
				new EndGame(wId));
		assertMoveOk(move(wId, state, operations));
	}
	
	@Test
	public void testBlackEndGameWithFork() {
		List<ImmutableList<Integer>> whitePoints = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, 0, -4),
		  				ImmutableList.<Integer>of(3, 0, -3),
		  				ImmutableList.<Integer>of(2, 0, -2),
		  				ImmutableList.<Integer>of(1, 0, -1),
		  				ImmutableList.<Integer>of(0, 0, 0),
		  				ImmutableList.<Integer>of(-1, 0, 1),
		  				ImmutableList.<Integer>of(-2, 0, 2)
		  				);
		List<ImmutableList<Integer>> blackOneStepCloserToFork = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, -3, -1),
		  				ImmutableList.<Integer>of(3, -3, 0),
		  				ImmutableList.<Integer>of(3, -4, 1),
		  				ImmutableList.<Integer>of(2, -4, 2),
		  				ImmutableList.<Integer>of(1, -3, 2),
		  				ImmutableList.<Integer>of(0, -3, 3)
		  				);
		Map<String, Object> state = ImmutableMap.<String, Object>builder()
				.put(W, whitePoints)
				.put(B, blackOneStepCloserToFork)
				.build();
		// The order of operations: SetTurn, SetW/B/addW/addB, EndGame
		List<Operation> operations = ImmutableList.of(
				new SetTurn(wId),
				new Set(addB, ImmutableList.<Integer>of(-1, -3, 4)),
				new EndGame(bId));
		assertMoveOk(move(bId, state, operations));
	}
	
	@Test
	public void testBlackEndGameWithBridge() {
		List<ImmutableList<Integer>> whitePoints = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, 0, -4),
		  				ImmutableList.<Integer>of(3, 0, -3),
		  				ImmutableList.<Integer>of(2, 0, -2),
		  				ImmutableList.<Integer>of(1, 0, -1),
		  				ImmutableList.<Integer>of(0, 0, 0),
		  				ImmutableList.<Integer>of(-1, 0, 1)
		  				);
		List<ImmutableList<Integer>> blackOneStepCloserToBridge = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, -4, 0),
		  				ImmutableList.<Integer>of(3, -4, 1),
		  				ImmutableList.<Integer>of(2, -4, 2),
		  				ImmutableList.<Integer>of(1, -3, 2),
		  				ImmutableList.<Integer>of(0, -3, 3)
		  				);
		
		Map<String, Object> state = ImmutableMap.<String, Object>builder()
				.put(W, whitePoints)
				.put(B, blackOneStepCloserToBridge)
				.build();
		// The order of operations: SetTurn, SetW/B/addW/addB, EndGame
		List<Operation> operations = ImmutableList.of(
				new SetTurn(wId),
				new Set(addB, ImmutableList.<Integer>of(0, -4, 4)),
				new EndGame(bId));
		assertMoveOk(move(bId, state, operations));
	}
	
	@Test
	public void testBlackEndGameWithCycle() {
		List<ImmutableList<Integer>> whitePoints = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, 0, -4),
		  				ImmutableList.<Integer>of(3, 0, -3),
		  				ImmutableList.<Integer>of(2, 0, -2),
		  				ImmutableList.<Integer>of(1, 0, -1),
		  				ImmutableList.<Integer>of(0, 0, 0),
		  				ImmutableList.<Integer>of(-1, 0, 1)
		  				);
		List<ImmutableList<Integer>> blackOneStepCloserToCycle = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(1, 0, -1),
		  				ImmutableList.<Integer>of(1, -1, 0),
		  				ImmutableList.<Integer>of(0, -1, 1),
		  				ImmutableList.<Integer>of(-1, 0, 1),
		  				ImmutableList.<Integer>of(-1, 1, 0)
		  				);
		
		Map<String, Object> state = ImmutableMap.<String, Object>builder()
				.put(W, whitePoints)
				.put(B, blackOneStepCloserToCycle)
				.build();
		// The order of operations: SetTurn, SetW/B/addW/addB, EndGame
		List<Operation> operations = ImmutableList.of(
				new SetTurn(wId),
				new Set(addB, ImmutableList.<Integer>of(0, 1, -1)),
				new EndGame(bId));
		assertMoveOk(move(bId, state, operations));
	}
	
	@Test
	public void testWhiteFakeEndGame() {
		List<ImmutableList<Integer>> whitePoints = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, 0, -4)
		  				);
		List<ImmutableList<Integer>> blackPoints = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(1, 0, -1)
		  				);
		Map<String, Object> state = ImmutableMap.<String, Object>builder()
				.put(W, whitePoints)
				.put(B, blackPoints)
				.build();
		// The order of operations: SetTurn, SetW/B/addW/addB, EndGame
		List<Operation> operations = ImmutableList.of(
				new SetTurn(wId),
				new Set(addB, ImmutableList.<Integer>of(0, 1, -1)),
				new EndGame(bId));
		assertHacker(move(bId, state, operations));
	}
	
	@Test
	public void testBlackFakeEndGame() {
		List<ImmutableList<Integer>> whitePoints = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(4, 0, -4)
		  				);
		List<ImmutableList<Integer>> blackPoints = 
		  		ImmutableList.<ImmutableList<Integer>>of(
		  				ImmutableList.<Integer>of(1, 0, -1)
		  				);
		Map<String, Object> state = ImmutableMap.<String, Object>builder()
				.put(W, whitePoints)
				.put(B, blackPoints)
				.build();
		// The order of operations: SetTurn, SetW/B/addW/addB, EndGame
		List<Operation> operations = ImmutableList.of(
				new SetTurn(bId),
				new Set(addW, ImmutableList.<Integer>of(0, 1, -1)),
				new EndGame(wId));
		assertHacker(move(wId, state, operations));
	}
}