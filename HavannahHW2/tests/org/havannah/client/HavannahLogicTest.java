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
	private final int wId = 87;
	private final int bId = 88;
	
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
	  				ImmutableList.<Integer>of(-1, 1, 0),
	  				ImmutableList.<Integer>of(-1, 0, 1),
	  				ImmutableList.<Integer>of(0, -1, 1),
	  				ImmutableList.<Integer>of(1, -1, 0),
	  				ImmutableList.<Integer>of(1, 0, -1)
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
	  				ImmutableList.<Integer>of(4, -4, 0)
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
		assertEquals(havannahLogic.distance(pointNeighborOrigin, pointSide), 6);
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
		assertEquals(havannahLogic.getNeighborsOf(pointOrigin), neiborsOfPointOrigin);
		assertEquals(havannahLogic.getNeighborsOf(pointNeighborOrigin), neiborsOfPointNeighborOrigin);
		assertEquals(havannahLogic.getNeighborsOf(pointSide), neiborsOfPointSide);
		assertEquals(havannahLogic.getNeighborsOf(pointCorner), neiborsOfPointCorner);
	}
	
	@Test
	public void testIsSidePoint() {
		assertEquals(havannahLogic.isSidePoint(pointOrigin), false);
		assertEquals(havannahLogic.isSidePoint(pointNeighborOrigin), false);
		assertEquals(havannahLogic.isSidePoint(pointSide), false);
		assertEquals(havannahLogic.isSidePoint(pointCorner), true);
	}
	
	@Test
	public void testIsCornerPoint() {
		assertEquals(havannahLogic.isSidePoint(pointOrigin), false);
		assertEquals(havannahLogic.isSidePoint(pointNeighborOrigin), false);
		assertEquals(havannahLogic.isSidePoint(pointSide), true);
		assertEquals(havannahLogic.isSidePoint(pointCorner), false);
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
	
	@Test
	public void testInitialMove() {
		assertMoveOk(move(wId, emptyState, getInitialOperations()));
	}

	@Test
	public void testInitialMoveByWrongPlayer() {
		assertHacker(move(bId, emptyState, getInitialOperations()));
	}

	
	private final Map<String, Object> someState = ImmutableMap.<String, Object> of();
	

	@Test
	public void testWhiteAddPieceToEmpty() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(verifyMove.getLastMovePlayerId(), verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testBlackAddPieceToEmpty() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertHacker(move(wId, emptyState, getInitialOperations()));
	}
	
	@Test
	public void testIllegalMoveYYY() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(verifyMove.getLastMovePlayerId(), verifyDone.getHackerPlayerId());
	}



	private final Map<String, Object> turnOfWEmptyMiddle = ImmutableMap
			.<String, Object> of(TURN, W, W, getIndicesInRange(0, 10), B,
					getIndicesInRange(11, 51), M, ImmutableList.of());

	Map<String, Object> turnOfBEmptyMiddle = ImmutableMap.<String, Object> of(
			TURN, B, W, getIndicesInRange(0, 10), B, getIndicesInRange(11, 51),
			M, ImmutableList.of());

	// The order of operations: turn, isCheater, W, B, M, claim, C0...C51
	private final List<Operation> claimOfW = ImmutableList.<Operation> of(
			new SetTurn(bId), new Set(W, getIndicesInRange(0, 8)), new Set(M,
					getIndicesInRange(9, 10)),
			new Set(CLAIM, ImmutableList.of("2cards", "rankA")));

	private final List<Operation> claimOfB = ImmutableList.<Operation> of(
			new SetTurn(wId), new Set(B, getIndicesInRange(11, 48)), new Set(M,
					getIndicesInRange(49, 51)),
			new Set(CLAIM, ImmutableList.of("3cards", "rankJ")));

	private final List<Operation> illegalClaimWithWrongCards = ImmutableList
			.<Operation> of(new SetTurn(bId), new Set(W,
					getIndicesInRange(0, 8)),
					new Set(M, getIndicesInRange(9, 10)), new Set(CLAIM,
							ImmutableList.of("3cards", "rankA")));

	private final List<Operation> illegalClaimWithWrongW = ImmutableList
			.<Operation> of(new SetTurn(bId), new Set(W,
					getIndicesInRange(0, 7)),
					new Set(M, getIndicesInRange(9, 10)), new Set(CLAIM,
							ImmutableList.of("2cards", "rankA")));

	private final List<Operation> illegalClaimWithWrongM = ImmutableList
			.<Operation> of(new SetTurn(bId), new Set(W,
					getIndicesInRange(0, 8)),
					new Set(M, getIndicesInRange(8, 10)), new Set(CLAIM,
							ImmutableList.of("2cards", "rankA")));



	private List<Integer> getIndicesInRange(int fromInclusive, int toInclusive) {
		return havannahLogic.getIndicesInRange(fromInclusive, toInclusive);
	}

	@Test
	public void testGetIndicesInRange() {
		assertEquals(ImmutableList.of(3, 4),
				havannahLogic.getIndicesInRange(3, 4));
	}

	private List<String> getCardsInRange(int fromInclusive, int toInclusive) {
		return havannahLogic.getCardsInRange(fromInclusive, toInclusive);
	}

	@Test
	public void testCardsInRange() {
		assertEquals(ImmutableList.of("C3", "C4"),
				havannahLogic.getCardsInRange(3, 4));
	}

	private <T> List<T> concat(List<T> a, List<T> b) {
		return havannahLogic.concat(a, b);
	}

	@Test
	public void testCardIdToString() {
		assertEquals("2c", havannahLogic.cardIdToString(0));
		assertEquals("2d", havannahLogic.cardIdToString(1));
		assertEquals("2h", havannahLogic.cardIdToString(2));
		assertEquals("2s", havannahLogic.cardIdToString(3));
		assertEquals("As", havannahLogic.cardIdToString(51));
	}

	private List<Operation> getInitialOperations() {
		return havannahLogic.getInitialMove(wId, bId);
	}


	@Test
	public void testInitialMoveFromNonEmptyState() {
		assertHacker(move(wId, nonEmptyState, getInitialOperations()));
	}

	@Test
	public void testInitialMoveWithExtraOperation() {
		List<Operation> initialOperations = getInitialOperations();
		initialOperations.add(new Set(M, ImmutableList.of()));
		assertHacker(move(wId, emptyState, initialOperations));
	}

	@Test
	public void testNormalClaimByWhite() {
		assertMoveOk(move(wId, turnOfWEmptyMiddle, claimOfW));
	}

	@Test
	public void testNormalClaimByBlack() {
		assertMoveOk(move(bId, turnOfBEmptyMiddle, claimOfB));
	}

	@Test
	public void testIllegalClaimByWrongColor() {
		assertHacker(move(bId, turnOfWEmptyMiddle, claimOfW));
		assertHacker(move(wId, turnOfBEmptyMiddle, claimOfB));
		assertHacker(move(wId, turnOfBEmptyMiddle, claimOfW));
		assertHacker(move(bId, turnOfWEmptyMiddle, claimOfB));
		assertHacker(move(bId, turnOfBEmptyMiddle, claimOfW));
		assertHacker(move(wId, turnOfWEmptyMiddle, claimOfB));
	}

	@Test
	public void testClaimWithWrongCards() {
		assertHacker(move(wId, turnOfWEmptyMiddle, illegalClaimWithWrongCards));
	}

	@Test
	public void testClaimWithWrongW() {
		assertHacker(move(wId, turnOfWEmptyMiddle, illegalClaimWithWrongW));
	}

	@Test
	public void testClaimWithWrongM() {
		assertHacker(move(wId, turnOfWEmptyMiddle, illegalClaimWithWrongM));
	}

	// The order of operations: turn, isCheater, W, B, M, claim, C0...C51
	List<Operation> claimCheaterByW = ImmutableList.<Operation> of(new Set(
			TURN, W), new Set(IS_CHEATER, YES), new SetVisibility("C50"),
			new SetVisibility("C51"));

	@Test
	public void testClaimCheaterByWhite() {
		Map<String, Object> state = ImmutableMap.<String, Object> of(TURN, W,
				W, getIndicesInRange(0, 10), B, getIndicesInRange(11, 51), M,
				getIndicesInRange(50, 51), CLAIM,
				ImmutableList.of("2cards", "rankA"));

		assertMoveOk(move(wId, state, claimCheaterByW));
	}

	@Test
	public void testCannotClaimCheaterWhenMiddlePileIsEmpty() {
		assertHacker(move(wId, turnOfWEmptyMiddle, claimCheaterByW));
	}

	@Test
	public void testBlackIsIndeedCheater() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, W).put(IS_CHEATER, YES).put("C50", "Ah")
				.put("C51", "Kh").put(W, getIndicesInRange(0, 10))
				.put(B, getIndicesInRange(11, 49))
				.put(M, getIndicesInRange(50, 51))
				.put(CLAIM, ImmutableList.of("2cards", "rankA")).build();

		// The order of operations: turn, isCheater, W, B, M, claim, C0...C51
		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				B), new Delete(IS_CHEATER), new Set(B,
				getIndicesInRange(11, 51)), new Set(M, ImmutableList.of()),
				new SetVisibility("C50", visibleToB), new SetVisibility("C51",
						visibleToB), new Shuffle(getCardsInRange(11, 51)));

		assertMoveOk(move(wId, state, operations));
		assertHacker(move(bId, state, operations));
		assertHacker(move(wId, emptyState, operations));
		assertHacker(move(wId, turnOfWEmptyMiddle, operations));
	}

	@Test
	public void testBlackWasNotCheating() {
		Map<String, Object> state = ImmutableMap.<String, Object> builder()
				.put(TURN, W).put(IS_CHEATER, YES).put("C50", "Ah")
				.put("C51", "Ah").put(W, getIndicesInRange(0, 10))
				.put(B, getIndicesInRange(11, 49))
				.put(M, getIndicesInRange(50, 51))
				.put(CLAIM, ImmutableList.of("2cards", "rankA")).build();

		List<String> wNewCards = concat(getCardsInRange(0, 10),
				getCardsInRange(50, 51));
		List<Integer> wNewIndices = concat(getIndicesInRange(0, 10),
				getIndicesInRange(50, 51));
		// The order of operations: turn, isCheater, W, B, M, claim, C0...C51
		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				W), new Delete(IS_CHEATER), new Set(W, wNewIndices), new Set(M,
				ImmutableList.of()), new SetVisibility("C50", visibleToW),
				new SetVisibility("C51", visibleToW), new Shuffle(wNewCards));

		assertMoveOk(move(wId, state, operations));
		assertHacker(move(bId, state, operations));
		assertHacker(move(wId, emptyState, operations));
		assertHacker(move(wId, turnOfWEmptyMiddle, operations));
	}

	@Test
	public void testIncreasePreviousClaim() {
		assertMoveOk(getChangePreviousClaim("2"));
	}

	@Test
	public void testDecreasePreviousClaim() {
		assertMoveOk(getChangePreviousClaim("K"));
	}

	@Test
	public void testKeepPreviousClaim() {
		assertMoveOk(getChangePreviousClaim("A"));
	}

	@Test
	public void testIllegalNextClaim() {
		assertHacker(getChangePreviousClaim("Q"));
		assertHacker(getChangePreviousClaim("10"));
		assertHacker(getChangePreviousClaim("3"));
	}

	private VerifyMove getChangePreviousClaim(String newRank) {
		Map<String, Object> state = ImmutableMap.<String, Object> of(TURN, W,
				W, getIndicesInRange(0, 10), B, getIndicesInRange(11, 49), M,
				getIndicesInRange(50, 51), CLAIM,
				ImmutableList.of("2cards", "rankA"));
		// The order of operations: turn, isCheater, W, B, M, claim, C0...C51
		List<Operation> claimByW = ImmutableList.<Operation> of(
				new SetTurn(bId),
				new Set(W, getIndicesInRange(4, 10)),
				new Set(M, concat(getIndicesInRange(50, 51),
						getIndicesInRange(0, 3))),
				new Set(CLAIM, ImmutableList.of("4cards", "rank" + newRank)));
		return move(wId, state, claimByW);
	}
	
	
	
	
	
}