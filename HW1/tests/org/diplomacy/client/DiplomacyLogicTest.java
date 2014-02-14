package org.diplomacy.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.diplomacy.client.GameApi.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@RunWith(JUnit4.class)
public class DiplomacyLogicTest {
	/** RULE OF THE GAME */
	/** http://ry4an.org/diptutor/mgp00001.html */
	
	/** The object under test. */
	DiplomacyLogic diplomacyLogic = new DiplomacyLogic();
	
	private static final String MOVEMENT = "MOVEMENT";
	private static final String RETREAT = "RETREAT";
	private static final String ADJUSTMENT = "ADJUSTMENT";
	
	private static final String SPRING = "SPRING";
	private static final String FALL = "FALL";
	private static final String YEAR = "year";
	
	// Disclose orders only after everyone submitted
	private static final String Austria_Submitted = "Austria_Submitted";
	private static final String Turkey_Submitted = "Turkey_Submitted";
	private static final String Italy_Submitted = "Italy_Submitted";
	private static final String France_Submitted = "France_Submitted";
	private static final String England_Submitted = "England_Submitted";
	private static final String Germany_Submitted = "Germany_Submitted";
	private static final String Russia_Submitted = "Russia_Submitted";

	// keep it, may be useful later
	private void assertMoveOk(VerifyMove verifyMove) {
		diplomacyLogic.checkMoveIsLegal(verifyMove);
	}

	private void assertHacker(VerifyMove verifyMove) {
		VerifyMoveDone verifyDone = diplomacyLogic.verify(verifyMove);
		assertEquals(verifyMove.getLastMovePlayerId(),
				verifyDone.getHackerPlayerId());
	}

	
	/* 
	 * Part One: [Make sure the initial configuration is right, no behavior]
	 * 
	 * 1. Map is loaded correctly.
	 * 
	 * 		Number of regions, seas, lands, source centers
	 * 
	 * 		Troops from different nations are initially deployed on correct positions
	 * 
	 * 		The connectivity is right (but we can't cover everything)
	 *  	
	 * 2. Army and Fleet can move accordingly.
	 * 
	 * 		move is valid or not (hold, attack, support, convoy)
	 * 
	 */
	
	
	
	
	/* 
	 * Test region counts
	 */
	@Test
	public void test56Land() {
		//56 lands on map
		assertEquals(56, diplomacyLogic.countLand());
	}
	
	@Test
	public void test19Seas() {
		// 19 seas on map
		assertEquals(19, diplomacyLogic.countSea());
	}
	
	@Test
	public void test34SourceCenters() {
		// 34 source centers on map
		assertEquals(34, diplomacyLogic.countSourceCenter());
	}
	
	
	
	/* 
	 * Test initial troop deployment
	 */
	@Test
	public void testTroopsOfAustriaAssignedOnMap() {
		//Austria has their troops on the right position?	
		//Army in Vienna
		assertEquals(diplomacyLogic.Vie.occupant.nationality, diplomacyLogic.austria);
		assertEquals(diplomacyLogic.Vie.occupant.type, "A");
		
		//Army in Budapest
		assertEquals(diplomacyLogic.Bud.occupant.nationality, diplomacyLogic.austria);
		assertEquals(diplomacyLogic.Bud.occupant.type, "A");
		
		//Fleet in Trieste
		assertEquals(diplomacyLogic.Tri.occupant.nationality, diplomacyLogic.austria);
		assertEquals(diplomacyLogic.Tri.occupant.type, "F");
	}
	
	@Test
	public void testTroopsOfTurkeyAssignedOnMap() {
		//Turkey has their troops on the right position?
		//Army in Consta
		assertEquals(diplomacyLogic.Con.occupant.nationality, diplomacyLogic.turkey);
		assertEquals(diplomacyLogic.Con.occupant.type, "A");
		
		//Army in Ankara
		assertEquals(diplomacyLogic.Bud.occupant.nationality, diplomacyLogic.turkey);
		assertEquals(diplomacyLogic.Bud.occupant.type, "F");
		
		//Army in Smy
		assertEquals(diplomacyLogic.Smy.occupant.nationality, diplomacyLogic.turkey);
		assertEquals(diplomacyLogic.Smy.occupant.type, "A");
	}
	
	@Test
	public void testTroopsOfItalyAssignedOnMap() {
		//Italy has their troops on the right position?
		//Army in Venice
		assertEquals(diplomacyLogic.Ven.occupant.nationality, diplomacyLogic.italy);
		assertEquals(diplomacyLogic.Ven.occupant.type, "A");
		
		//Army in Rome
		assertEquals(diplomacyLogic.Rom.occupant.nationality, diplomacyLogic.italy);
		assertEquals(diplomacyLogic.Rom.occupant.type, "A");
		
		//Fleet in Napolis
		assertEquals(diplomacyLogic.Nap.occupant.nationality, diplomacyLogic.italy);
		assertEquals(diplomacyLogic.Nap.occupant.type, "F");
	}
	
	@Test
	public void testTroopsOfFranceAssignedOnMap() {
		//France has their troops on the right position?
		//Army in Paris
		assertEquals(diplomacyLogic.Par.occupant.nationality, diplomacyLogic.france);
		assertEquals(diplomacyLogic.Par.occupant.type, "A");
		
		//Army in Marsellia
		assertEquals(diplomacyLogic.Mar.occupant.nationality, diplomacyLogic.france);
		assertEquals(diplomacyLogic.Mar.occupant.type, "A");
		
		//Fleet in Brest
		assertEquals(diplomacyLogic.Bre.occupant.nationality, diplomacyLogic.france);
		assertEquals(diplomacyLogic.Bre.occupant.type, "F");
	}
	
	@Test
	public void testTroopsOfEnglandAssignedOnMap() {
		//England has their troops on the right position?
		//Army in Liverpool
		assertEquals(diplomacyLogic.Lvp.occupant.nationality, diplomacyLogic.england);
		assertEquals(diplomacyLogic.Lvp.occupant.type, "A");
		
		//Fleet in Edinburg
		assertEquals(diplomacyLogic.Edi.occupant.nationality, diplomacyLogic.england);
		assertEquals(diplomacyLogic.Edi.occupant.type, "F");
		
		//Fleet in London
		assertEquals(diplomacyLogic.Lon.occupant.nationality, diplomacyLogic.england);
		assertEquals(diplomacyLogic.Lon.occupant.type, "F");
	}
	
	@Test
	public void testTroopsOfGermanyAssignedOnMap() {
		//Germany has their troops on the right position?
		//Army in Munich
		assertEquals(diplomacyLogic.Mun.occupant.nationality, diplomacyLogic.germany);
		assertEquals(diplomacyLogic.Mun.occupant.type, "A");
		
		//Army in Berlin
		assertEquals(diplomacyLogic.Ber.occupant.nationality, diplomacyLogic.germany);
		assertEquals(diplomacyLogic.Ber.occupant.type, "A");
		
		//Fleet in Kiel
		assertEquals(diplomacyLogic.Kie.occupant.nationality, diplomacyLogic.germany);
		assertEquals(diplomacyLogic.Kie.occupant.type, "F");
	}
	
	@Test
	public void testTroopsOfRussiaAssignedOnMap() {
		//Russia has their troops on the right position?
		//Army in Moscow
		assertEquals(diplomacyLogic.Mos.occupant.nationality, diplomacyLogic.russia);
		assertEquals(diplomacyLogic.Mos.occupant.type, "A");
		
		//Army in Warsaw
		assertEquals(diplomacyLogic.War.occupant.nationality, diplomacyLogic.russia);
		assertEquals(diplomacyLogic.War.occupant.type, "A");
		
		//Fleet in Saint Petersberg
		assertEquals(diplomacyLogic.Stp.occupant.nationality, diplomacyLogic.russia);
		assertEquals(diplomacyLogic.Stp.occupant.type, "F");
		
		//Fleet in Sevastopol
		assertEquals(diplomacyLogic.Sev.occupant.nationality, diplomacyLogic.russia);
		assertEquals(diplomacyLogic.Sev.occupant.type, "F");
	}
	
	
	
	/* 
	 * Test initial troop deployment
	 */
	@Test
	public void testPicardyAdjacentToBelgium() {
		//Picardy and Belgium are adjacent
		ArrayList<Troop> AustrianTroop = diplomacyLogic.austria.troops;
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
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testArmyCanNotEnterSeaRegion() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetCanEnterSeaRegion() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetCanEnterCoastalRegion() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetCanNotEnterNonCoastalRegion() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetCanNotGoThroughLandRegion() {
		// EnglishChannel -> Spain -> West Med.
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	
	/*	
	 * Part Two: [Make sure users interact in the way I expected ]
	 * 
	 * 1. Orders are not revealed until all submitted
	 * 
	 * 		!!! I need to come up with a mechanism to support this op
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
	
	@Test
	public void testArmyBelgiumHolds() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testArmyInBurgundyAttacksBelgium() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetInEnglishChannelAttackBelgium() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testArmyInBurgundyCanNotAttackMoscow() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetInEnglishChannelCanNotAttackBlackSea() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testArmyInBurgundyAttacksBelgiumFailWhenDefendantArmyInBelgium() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testBothArmiesInBurgundyAndRuhrAttackBelgiumSucceedEvenDefendantIn() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetFromEnglishChannelSupportArmyInBurgundyAttackBelgium() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testSupportDefence() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testSupportAttackFromForeignArmy() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testSupportDefenceFromForeignArmy() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCuttingSupport() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testFleetInEnglishChannelConvoyArmyInLondonToBelgium() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testConvoyFailedIfArmyWriteWrongOrder() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testConvoyFailedIfArmyWithoutEnoughSupport() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testConvoyFailedWhenFleetDislodged() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCanNotDislodgeToNonAdjacentRegion() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCanNotDisplodgeToNonVacantRegion() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCanNotDislodgeToWhereAttackerCameFrom() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCanNotDislodgeToWhereEmptyForBounce() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCanNotCutSupportAgainstSelf() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void test() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testLegalMoveArmyOnLand() {
	    VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
	    assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalMoveYYY() {
		VerifyMove verifyMove = new VerifyMove(bId, playersInfo, bInfo, bInfo, null, bId);
	    VerifyMoveDone verifyDone = new DiplomacyLogic().verify(verifyMove);
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
		return diplomacyLogic.getIndicesInRange(fromInclusive, toInclusive);
	}

	@Test
	public void testGetIndicesInRange() {
		assertEquals(ImmutableList.of(3, 4),
				diplomacyLogic.getIndicesInRange(3, 4));
	}

	private List<String> getCardsInRange(int fromInclusive, int toInclusive) {
		return diplomacyLogic.getCardsInRange(fromInclusive, toInclusive);
	}

	@Test
	public void testCardsInRange() {
		assertEquals(ImmutableList.of("C3", "C4"),
				diplomacyLogic.getCardsInRange(3, 4));
	}

	private <T> List<T> concat(List<T> a, List<T> b) {
		return diplomacyLogic.concat(a, b);
	}

	@Test
	public void testCardIdToString() {
		assertEquals("2c", diplomacyLogic.cardIdToString(0));
		assertEquals("2d", diplomacyLogic.cardIdToString(1));
		assertEquals("2h", diplomacyLogic.cardIdToString(2));
		assertEquals("2s", diplomacyLogic.cardIdToString(3));
		assertEquals("As", diplomacyLogic.cardIdToString(51));
	}

	private List<Operation> getInitialOperations() {
		return diplomacyLogic.getInitialMove(wId, bId);
	}

	@Test
	public void testGetInitialOperationsSize() {
		assertEquals(4 + 52 + 1 + 52, diplomacyLogic.getInitialMove(wId, bId)
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