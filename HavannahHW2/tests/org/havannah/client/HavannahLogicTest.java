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
	
	HavannahLogic havannahLogic = new HavannahLogic(5);
	
	private void assertMoveOk(VerifyMove verifyMove) {
		havannahLogic.checkMoveIsLegal(verifyMove);
	}

	private void assertHacker(VerifyMove verifyMove) {
		VerifyMoveDone verifyDone = havannahLogic.verify(verifyMove);
		assertEquals(verifyMove.getLastMovePlayerId(),
				verifyDone.getHackerPlayerId());
	}

	
	@Test
	public void testCorrectNumberOfPoints() {
		// len (points) = 3s^2 - 3s + 1
		int bs = havannahLogic.boardSize;
		assertEquals(havannahLogic.points.size(), 3*Math.pow(bs, 2) - 3*bs + 1);
	}
	
	@Test
	public void testdistance() {
		// 19 seas on map
		assertEquals(havannahLogic.distance(null, null), 2);
	}
	
	@Test
	public void testIsNeighbor() {
		// 34 source centers on map
		assertEquals(havannahLogic.isNeighbor(null, null), false);
	}
	
	@Test
	public void testPicardyAdjacentToBelgium() {
		//Picardy and Belgium are adjacent
		ArrayList<Troop> AustrianTroop = havannahLogic.austria.troops;
		HashSet<Troop> setAustrianTroop = new HashSet();
		assertEquals(0, 1);
	}
	
	@Test
	public void testNormandianSeaAdjacentToBlackSea() {
		//NormandianSea and BlackSea are NOT adjacent
		assertEquals(0, 1);
	}
	
	@Test
	public void testArmyCanEnterLandRegion() {
		//Russian has their troops on the right position?
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testArmyCanNotEnterSeaRegion() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetCanEnterSeaRegion() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetCanEnterCoastalRegion() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetCanNotEnterNonCoastalRegion() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetCanNotGoThroughLandRegion() {
		// EnglishChannel -> Spain -> West Med.
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	
	/*	
	 * Part Two: [Make sure users interact in the way I expected ]
	 * 
	 * Mechanism of order submission: 
	 * 
	 * 7 players submitted order and claimed that they submitted
	 * 		>>> set {myOrder: {actions}, visibleTo: me}
	 * 		>>> set me_submitted = true
	 * 
	 * if they submitted they can not make any change. UI will disable submit button.
	 * 		>>> $('input:submit').click(function(){	$(this).attr("disabled", true);});
	 * 
	 * when all submitted then reveal and update states
	 * 		>>> if (7 nations claimed submission) {setVisibilityToALL}
	 * 
	 * submitting blank order is treated as a pass
	 * 
	 * 1. Orders are not revealed until all submitted
	 * 		
	 *  	
	 * 2. Can jump to next stage
	 * 		
	 * 		Year: 1900 => 1901 => 1902
	 * 
	 * 		Season: Spring => Fall
	 * 
	 * 		Stage: Movement => Resolve => Retreat => Adjustment
	 * 
	 */
	
	// create empty map for later use
	
	Map<String, Object> emptyMap = ImmutableMap.<String, Object> builder()
		.put("NAO", null).put("IRI", null).put("ENG", null).put("MAO", null).put("WES", null)
		.put("LYO", null).put("TYS", null).put("ION", null).put("ADR", null).put("AEG", null)
		.put("BLA", null).put("EAS", null).put("BAL", null).put("BOT", null).put("SKA", null)
		.put("NTH", null).put("HEL", null).put("NWG", null).put("BAR", null).put("Cly", null)
		.put("Edi", null).put("Yor", null).put("Lon", null).put("Lvp", null).put("Wal", null)
		.put("Bre", null).put("Gas", null).put("Mar", null).put("Pic", null).put("Par", null)
		.put("Bur", null).put("Por", null).put("Spa", null).put("Naf", null).put("Tun", null)
		.put("Nap", null).put("Rom", null).put("Tus", null).put("Apu", null).put("Ven", null)
		.put("Pie", null).put("Bel", null).put("Hol", null).put("Ruh", null).put("Mun", null)
		.put("Kie", null).put("Ber", null).put("Sil", null).put("Pru", null).put("Tyr", null)
		.put("Tri", null).put("Vie", null).put("Boh", null).put("Gal", null).put("Bud", null)
		.put("Ser", null).put("Alb", null).put("Gre", null).put("Bul", null).put("Rum", null)
		.put("Con", null).put("Ank", null).put("Smy", null).put("Syr", null).put("Arm", null)
		.put("Sev", null).put("Ukr", null).put("War", null).put("Lvn", null).put("Mos", null)
		.put("Stp", null).put("Fin", null).put("Nwy", null).put("Swe", null).put("Den", null)
		.build();

	
	@Test
	public void testArmyBelgiumHolds() {
		List<String> FranceArmy = ImmutableList.<String> of("France", "A");
		Map<String, Object> lastState = ImmutableMap.<String, Object> builder()
				.put("NAO", null).put("IRI", null).put("ENG", null).put("MAO", null).put("WES", null)
				.put("LYO", null).put("TYS", null).put("ION", null).put("ADR", null).put("AEG", null)
				.put("BLA", null).put("EAS", null).put("BAL", null).put("BOT", null).put("SKA", null)
				.put("NTH", null).put("HEL", null).put("NWG", null).put("BAR", null).put("Cly", null)
				.put("Edi", null).put("Yor", null).put("Lon", null).put("Lvp", null).put("Wal", null)
				.put("Bre", null).put("Gas", null).put("Mar", null).put("Pic", null).put("Par", null)
				.put("Bur", null).put("Por", null).put("Spa", null).put("Naf", null).put("Tun", null)
				.put("Nap", null).put("Rom", null).put("Tus", null).put("Apu", null).put("Ven", null)
				.put("Pie", null).put("Bel", FranceArmy).put("Hol", null).put("Ruh", null).put("Mun", null)
				.put("Kie", null).put("Ber", null).put("Sil", null).put("Pru", null).put("Tyr", null)
				.put("Tri", null).put("Vie", null).put("Boh", null).put("Gal", null).put("Bud", null)
				.put("Ser", null).put("Alb", null).put("Gre", null).put("Bul", null).put("Rum", null)
				.put("Con", null).put("Ank", null).put("Smy", null).put("Syr", null).put("Arm", null)
				.put("Sev", null).put("Ukr", null).put("War", null).put("Lvn", null).put("Mos", null)
				.put("Stp", null).put("Fin", null).put("Nwy", null).put("Swe", null).put("Den", null)
				.build();
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testArmyInBurgundyAttacksBelgium() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetInEnglishChannelAttackBelgium() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testArmyInBurgundyCanNotAttackMoscow() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetInEnglishChannelCanNotAttackBlackSea() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testArmyInBurgundyAttacksBelgiumFailWhenDefendantArmyInBelgium() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testBothArmiesInBurgundyAndRuhrAttackBelgiumSucceedEvenDefendantIn() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetFromEnglishChannelSupportArmyInBurgundyAttackBelgium() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testSupportDefence() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testSupportAttackFromForeignArmy() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testSupportDefenceFromForeignArmy() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCuttingSupport() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetInEnglishChannelConvoyArmyInLondonToBelgium() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testConvoyFailedIfArmyWriteWrongOrder() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testConvoyFailedIfArmyWithoutEnoughSupport() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testConvoyFailedWhenFleetDislodged() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCanNotDislodgeToNonAdjacentRegion() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCanNotDisplodgeToNonVacantRegion() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCanNotDislodgeToWhereAttackerCameFrom() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCanNotDislodgeToWhereEmptyForBounce() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCanNotCutSupportAgainstSelf() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void test() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testLegalMoveArmyOnLand() {
	    VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalMoveYYY() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new havannahLogic().verify(verifyMove);
	    assertEquals(verifyMove.getLastMovePlayerId(), verifyDone.getHackerPlayerId());
	}
	
	
	// keep it, may be useful later
	
	private static final String PLAYER_ID = "playerId";
	/*
	 * The entries used in the cheat game are: turn:W/B, isCheater:yes, W, B, M,
	 * claim, C0...C51 When we send operations on these keys, it will always be
	 * in the above order.
	 */
	private static final String TURN = "turn"; // turn of which player (either W
												// or B)
	private static final String W = "W"; // White hand
	private static final String B = "B"; // Black hand
	private static final String M = "M"; // Middle pile
	private static final String CLAIM = "claim"; // a claim has the form:
													// [3cards, rankK]
	private static final String IS_CHEATER = "isCheater"; // we claim we have a
															// cheater
	private static final String YES = "yes"; // we claim we have a cheater
	private final int wId = 41;
	private final int bId = 42;
	private final List<Integer> visibleToW = ImmutableList.of(wId);
	private final List<Integer> visibleToB = ImmutableList.of(bId);
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

	private final Map<String, Object> turnOfWEmptyMiddle = ImmutableMap
			.<String, Object> of(TURN, W, W, getIndicesInRange(0, 10), B,
					getIndicesInRange(11, 51), M, ImmutableList.of());

	Map<String, Object> turnOfBEmptyMiddle = ImmutableMap.<String, Object> of(
			TURN, B, W, getIndicesInRange(0, 10), B, getIndicesInRange(11, 51),
			M, ImmutableList.of());

	// The order of operations: turn, isCheater, W, B, M, claim, C0...C51
	private final List<Operation> claimOfW = ImmutableList.<Operation> of(
			new Set(TURN, B), new Set(W, getIndicesInRange(0, 8)), new Set(M,
					getIndicesInRange(9, 10)),
			new Set(CLAIM, ImmutableList.of("2cards", "rankA")));

	private final List<Operation> claimOfB = ImmutableList.<Operation> of(
			new Set(TURN, W), new Set(B, getIndicesInRange(11, 48)), new Set(M,
					getIndicesInRange(49, 51)),
			new Set(CLAIM, ImmutableList.of("3cards", "rankJ")));

	private final List<Operation> illegalClaimWithWrongCards = ImmutableList
			.<Operation> of(new Set(TURN, B), new Set(W,
					getIndicesInRange(0, 8)),
					new Set(M, getIndicesInRange(9, 10)), new Set(CLAIM,
							ImmutableList.of("3cards", "rankA")));

	private final List<Operation> illegalClaimWithWrongW = ImmutableList
			.<Operation> of(new Set(TURN, B), new Set(W,
					getIndicesInRange(0, 7)),
					new Set(M, getIndicesInRange(9, 10)), new Set(CLAIM,
							ImmutableList.of("2cards", "rankA")));

	private final List<Operation> illegalClaimWithWrongM = ImmutableList
			.<Operation> of(new Set(TURN, B), new Set(W,
					getIndicesInRange(0, 8)),
					new Set(M, getIndicesInRange(8, 10)), new Set(CLAIM,
							ImmutableList.of("2cards", "rankA")));

	private VerifyMove move(int lastMovePlayerId,
			Map<String, Object> lastState, List<Operation> lastMove) {
		return new VerifyMove(wId, playersInfo,
		// in cheat we never need to check the resulting state (the server makes
		// it, and the game
		// doesn't have any hidden decisions such in Battleships)
				emptyState, lastState, lastMove, lastMovePlayerId);
	}

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
	public void testGetInitialOperationsSize() {
		assertEquals(4 + 52 + 1 + 52, havannahLogic.getInitialMove(wId, bId)
				.size());
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
				new Set(TURN, B),
				new Set(W, getIndicesInRange(4, 10)),
				new Set(M, concat(getIndicesInRange(50, 51),
						getIndicesInRange(0, 3))),
				new Set(CLAIM, ImmutableList.of("4cards", "rank" + newRank)));
		return move(wId, state, claimByW);
	}
	
	
	
	
	
}