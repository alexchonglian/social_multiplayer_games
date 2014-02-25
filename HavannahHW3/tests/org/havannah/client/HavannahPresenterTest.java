package org.havannah.client;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.havannah.client.HavannahPresenter.View;
import org.havannah.client.GameApi.Container;
import org.havannah.client.GameApi.Operation;
import org.havannah.client.GameApi.SetTurn;
import org.havannah.client.GameApi.UpdateUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


@RunWith(JUnit4.class)
public class HavannahPresenterTest {
	/** The class under test. */
	private HavannahPresenter havannahPresenter;
	private final HavannahLogic havannahLogic = new HavannahLogic();
	private View mockView;
	private Container mockContainer;

	private static final String PLAYER_ID = "playerId";
	private static final String W = "W"; // White
	private static final String B = "B"; // Black
	private static final String addW = "addW"; // add White
	private static final String addB = "addB"; // add Black
	private final int viewerId = GameApi.VIEWER_ID;
	private final int wId = 87;
	private final int bId = 88;
	private final ImmutableList<Integer> playerIds = ImmutableList.of(wId, bId);
	private final ImmutableMap<String, Object> wInfo = ImmutableMap
			.<String, Object> of(PLAYER_ID, wId);
	private final ImmutableMap<String, Object> bInfo = ImmutableMap
			.<String, Object> of(PLAYER_ID, bId);
	private final ImmutableList<Map<String, Object>> playersInfo = ImmutableList
			.<Map<String, Object>> of(wInfo, bInfo);

	private final Optional<Claim> claimFourAces = Optional.of(new Claim(
			Rank.ACE, 4));
	private final Optional<Claim> claimFourKings = Optional.of(new Claim(
			Rank.KING, 4));
	private final Optional<Claim> claimAbsent = Optional.<Claim> absent();

	/* The interesting states that I'll test. */
	private final ImmutableMap<String, Object> emptyState = ImmutableMap
			.<String, Object> of();
	private final ImmutableMap<String, Object> emptyMiddle = createState(10,
			42, false, claimAbsent);
	private final ImmutableMap<String, Object> nonEmptyMiddle = createState(10,
			10, false, claimFourAces);
	private final ImmutableMap<String, Object> mustDeclareHavannaher = createState(
			0, 10, false, claimFourAces);
	private final ImmutableMap<String, Object> declaredHavannaherAndIndeedHavannahed = createState(
			0, 10, true, claimFourKings);
	private final ImmutableMap<String, Object> declaredHavannaherAndDidNotHavannah = createState(
			0, 10, true, claimFourAces);
	private final ImmutableMap<String, Object> gameOver = createState(0, 52,
			false, claimAbsent);

	@Before
	public void runBefore() {
		mockView = Mockito.mock(View.class);
		mockContainer = Mockito.mock(Container.class);
		havannahPresenter = new HavannahPresenter(mockView, mockContainer);
		verify(mockView).setPresenter(havannahPresenter);
	}

	@After
	public void runAfter() {
		// This will ensure I didn't forget to declare any extra interaction the
		// mocks have.
		verifyNoMoreInteractions(mockContainer);
		verifyNoMoreInteractions(mockView);
	}

	@Test
	public void testEmptyStateForW() {
		havannahPresenter.updateUI(createUpdateUI(wId, 0, emptyState));
		verify(mockContainer)
				.sendMakeMove(havannahLogic.getMoveInitial(playerIds));
	}

	@Test
	public void testEmptyStateForB() {
		havannahPresenter.updateUI(createUpdateUI(bId, 0, emptyState));
	}

	@Test
	public void testEmptyStateForViewer() {
		havannahPresenter.updateUI(createUpdateUI(viewerId, 0, emptyState));
	}

	@Test
	public void testEmptyMiddleStateForWTurnOfW() {
		havannahPresenter.updateUI(createUpdateUI(wId, wId, emptyMiddle));
		verify(mockView).setPlayerState(42, 0, getCards(0, 10),
				HavannahMessage.INVISIBLE, claimAbsent);
		verify(mockView).chooseNextCard(ImmutableList.<Card> of(),
				getCards(0, 10));
	}

	@Test
	public void testEmptyMiddleStateForWTurnOfB() {
		havannahPresenter.updateUI(createUpdateUI(wId, bId, emptyMiddle));
		verify(mockView).setPlayerState(42, 0, getCards(0, 10),
				HavannahMessage.INVISIBLE, claimAbsent);
	}

	@Test
	public void testEmptyMiddleStateForBTurnOfB() {
		havannahPresenter.updateUI(createUpdateUI(bId, bId, emptyMiddle));
		verify(mockView).setPlayerState(10, 0, getCards(10, 52),
				HavannahMessage.INVISIBLE, claimAbsent);
		verify(mockView).chooseNextCard(ImmutableList.<Card> of(),
				getCards(10, 52));
	}

	@Test
	public void testEmptyMiddleStateForBTurnOfW() {
		havannahPresenter.updateUI(createUpdateUI(bId, wId, emptyMiddle));
		verify(mockView).setPlayerState(10, 0, getCards(10, 52),
				HavannahMessage.INVISIBLE, claimAbsent);
	}

	@Test
	public void testEmptyMiddleStateForViewerTurnOfW() {
		havannahPresenter.updateUI(createUpdateUI(viewerId, wId, emptyMiddle));
		verify(mockView).setViewerState(10, 42, 0, HavannahMessage.INVISIBLE,
				claimAbsent);
	}

	@Test
	public void testEmptyMiddleStateForViewerTurnOfB() {
		havannahPresenter.updateUI(createUpdateUI(viewerId, bId, emptyMiddle));
		verify(mockView).setViewerState(10, 42, 0, HavannahMessage.INVISIBLE,
				claimAbsent);
	}

	@Test
	public void testNonEmptyMiddleStateForWTurnOfW() {
		havannahPresenter.updateUI(createUpdateUI(wId, wId, nonEmptyMiddle));
		verify(mockView).setPlayerState(10, 32, getCards(0, 10),
				HavannahMessage.IS_OPPONENT_CHEATING, claimFourAces);
		verify(mockView).chooseNextCard(ImmutableList.<Card> of(),
				getCards(0, 10));
	}

	@Test
	public void testNonEmptyMiddleStateForWTurnOfB() {
		havannahPresenter.updateUI(createUpdateUI(wId, bId, nonEmptyMiddle));
		verify(mockView).setPlayerState(10, 32, getCards(0, 10),
				HavannahMessage.INVISIBLE, claimFourAces);
	}

	@Test
	public void testNonEmptyMiddleStateForBTurnOfB() {
		havannahPresenter.updateUI(createUpdateUI(bId, bId, nonEmptyMiddle));
		verify(mockView).setPlayerState(10, 32, getCards(10, 20),
				HavannahMessage.IS_OPPONENT_CHEATING, claimFourAces);
		verify(mockView).chooseNextCard(ImmutableList.<Card> of(),
				getCards(10, 20));
	}

	@Test
	public void testNonEmptyMiddleStateForBTurnOfW() {
		havannahPresenter.updateUI(createUpdateUI(bId, wId, nonEmptyMiddle));
		verify(mockView).setPlayerState(10, 32, getCards(10, 20),
				HavannahMessage.INVISIBLE, claimFourAces);
	}

	@Test
	public void testNonEmptyMiddleStateForViewerTurnOfW() {
		havannahPresenter.updateUI(createUpdateUI(viewerId, wId, nonEmptyMiddle));
		verify(mockView).setViewerState(10, 10, 32, HavannahMessage.INVISIBLE,
				claimFourAces);
	}

	@Test
	public void testMustDeclareHavannaherStateForW() {
		havannahPresenter.updateUI(createUpdateUI(wId, bId, mustDeclareHavannaher));
		verify(mockView).setPlayerState(10, 42, getCards(0, 0),
				HavannahMessage.INVISIBLE, claimFourAces);
	}

	@Test
	public void testMustDeclareHavannaherStateForB() {
		UpdateUI updateUI = createUpdateUI(bId, bId, mustDeclareHavannaher);
		HavannahState havannahState = havannahLogic.gameApiStateToHavannahState(
				updateUI.getState(), Color.B, playerIds);
		havannahPresenter.updateUI(updateUI);
		verify(mockView).setPlayerState(0, 42, getCards(0, 10),
				HavannahMessage.INVISIBLE, claimFourAces);
		// Note that B doesn't have chooseNextCard, because he has to declare
		// havannaher.
		verify(mockContainer).sendMakeMove(
				havannahLogic.getMoveDeclareHavannaher(havannahState));
	}

	@Test
	public void testMustDeclareHavannaherStateForViewer() {
		havannahPresenter.updateUI(createUpdateUI(viewerId, bId,
				mustDeclareHavannaher));
		verify(mockView).setViewerState(0, 10, 42, HavannahMessage.INVISIBLE,
				claimFourAces);
	}

	@Test
	public void testDeclaredHavannaherAndIndeedHavannahedStateForW() {
		havannahPresenter.updateUI(createUpdateUI(wId, bId,
				declaredHavannaherAndIndeedHavannahed));
		verify(mockView).setPlayerState(10, 42, getCards(0, 0),
				HavannahMessage.WAS_CHEATING, claimFourKings);
	}

	@Test
	public void testDeclaredHavannaherAndIndeedHavannahedStateForB() {
		UpdateUI updateUI = createUpdateUI(bId, bId,
				declaredHavannaherAndIndeedHavannahed);
		HavannahState havannahState = havannahLogic.gameApiStateToHavannahState(
				updateUI.getState(), Color.B, playerIds);
		havannahPresenter.updateUI(updateUI);
		verify(mockView).setPlayerState(0, 42, getCards(0, 10),
				HavannahMessage.WAS_CHEATING, claimFourKings);
		verify(mockContainer).sendMakeMove(
				havannahLogic.getMoveCheckIfHavannahed(havannahState));
	}

	@Test
	public void testDeclaredHavannaherAndIndeedHavannahedStateForViewer() {
		havannahPresenter.updateUI(createUpdateUI(viewerId, bId,
				declaredHavannaherAndIndeedHavannahed));
		verify(mockView).setViewerState(0, 10, 42, HavannahMessage.WAS_CHEATING,
				claimFourKings);
	}

	@Test
	public void testDeclaredHavannaherAndDidNotHavannahStateForW() {
		havannahPresenter.updateUI(createUpdateUI(wId, bId,
				declaredHavannaherAndDidNotHavannah));
		verify(mockView).setPlayerState(10, 42, getCards(0, 0),
				HavannahMessage.WAS_NOT_CHEATING, claimFourAces);
	}

	@Test
	public void testDeclaredHavannaherAndDidNotHavannahStateForB() {
		UpdateUI updateUI = createUpdateUI(bId, bId,
				declaredHavannaherAndDidNotHavannah);
		HavannahState havannahState = havannahLogic.gameApiStateToHavannahState(
				updateUI.getState(), Color.B, playerIds);
		havannahPresenter.updateUI(updateUI);
		verify(mockView).setPlayerState(0, 42, getCards(0, 10),
				HavannahMessage.WAS_NOT_CHEATING, claimFourAces);
		verify(mockContainer).sendMakeMove(
				havannahLogic.getMoveCheckIfHavannahed(havannahState));
	}

	@Test
	public void testDeclaredHavannaherAndDidNotHavannahStateForViewer() {
		havannahPresenter.updateUI(createUpdateUI(viewerId, bId,
				declaredHavannaherAndDidNotHavannah));
		verify(mockView).setViewerState(0, 10, 42,
				HavannahMessage.WAS_NOT_CHEATING, claimFourAces);
	}

	@Test
	public void testGameOverStateForW() {
		havannahPresenter.updateUI(createUpdateUI(wId, bId, gameOver));
		verify(mockView).setPlayerState(52, 0, getCards(0, 0),
				HavannahMessage.INVISIBLE, claimAbsent);
	}

	@Test
	public void testGameOverStateForB() {
		havannahPresenter.updateUI(createUpdateUI(bId, bId, gameOver));
		verify(mockView).setPlayerState(0, 0, getCards(0, 52),
				HavannahMessage.INVISIBLE, claimAbsent);
	}

	@Test
	public void testGameOverStateForViewer() {
		havannahPresenter.updateUI(createUpdateUI(viewerId, bId, gameOver));
		verify(mockView).setViewerState(0, 52, 0, HavannahMessage.INVISIBLE,
				claimAbsent);
	}

	/* Tests for preparing a claim. */
	@Test
	public void testEmptyMiddleStateForWTurnOfWPrepareClaimWithTwoCards() {
		UpdateUI updateUI = createUpdateUI(wId, wId, emptyMiddle);
		HavannahState havannahState = havannahLogic.gameApiStateToHavannahState(
				updateUI.getState(), Color.W, playerIds);
		havannahPresenter.updateUI(updateUI);
		List<Card> myCards = getCards(0, 10);
		havannahPresenter.cardSelected(myCards.get(0));
		havannahPresenter.cardSelected(myCards.get(1));
		havannahPresenter.finishedSelectingCards();
		havannahPresenter.rankSelected(Rank.ACE);
		verify(mockView).setPlayerState(42, 0, myCards,
				HavannahMessage.INVISIBLE, claimAbsent);
		verify(mockView).chooseNextCard(ImmutableList.<Card> of(), myCards);
		verify(mockView).chooseNextCard(getCards(0, 1), getCards(1, 10));
		verify(mockView).chooseNextCard(getCards(0, 2), getCards(2, 10));
		verify(mockView).chooseRankForClaim(Arrays.asList(Rank.values()));
		verify(mockContainer).sendMakeMove(
				havannahLogic.getMoveClaim(havannahState, Rank.ACE,
						ImmutableList.of(0, 1)));
	}

	@Test
	public void testEmptyMiddleStateForWTurnOfWPrepareClaimAndUnselectOneCard() {
		UpdateUI updateUI = createUpdateUI(wId, wId, emptyMiddle);
		HavannahState havannahState = havannahLogic.gameApiStateToHavannahState(
				updateUI.getState(), Color.W, playerIds);
		havannahPresenter.updateUI(updateUI);
		List<Card> myCards = getCards(0, 10);
		havannahPresenter.cardSelected(myCards.get(0));
		havannahPresenter.cardSelected(myCards.get(1));
		havannahPresenter.cardSelected(myCards.get(1)); // remove card 1
		havannahPresenter.finishedSelectingCards();
		havannahPresenter.rankSelected(Rank.ACE);
		verify(mockView).setPlayerState(42, 0, myCards,
				HavannahMessage.INVISIBLE, claimAbsent);
		verify(mockView).chooseNextCard(ImmutableList.<Card> of(), myCards);
		verify(mockView, times(2)).chooseNextCard(getCards(0, 1),
				getCards(1, 10));
		verify(mockView).chooseNextCard(getCards(0, 2), getCards(2, 10));
		verify(mockView).chooseRankForClaim(Arrays.asList(Rank.values()));
		verify(mockContainer).sendMakeMove(
				havannahLogic.getMoveClaim(havannahState, Rank.ACE,
						ImmutableList.of(0)));
	}

	@Test
	public void testNonEmptyMiddleStateForWTurnOfWDeclareHavannaher() {
		UpdateUI updateUI = createUpdateUI(wId, wId, nonEmptyMiddle);
		HavannahState havannahState = havannahLogic.gameApiStateToHavannahState(
				updateUI.getState(), Color.W, playerIds);
		havannahPresenter.updateUI(updateUI);
		havannahPresenter.declaredHavannaher();
		verify(mockView).setPlayerState(10, 32, getCards(0, 10),
				HavannahMessage.IS_OPPONENT_CHEATING, claimFourAces);
		verify(mockView).chooseNextCard(ImmutableList.<Card> of(),
				getCards(0, 10));
		verify(mockContainer).sendMakeMove(
				havannahLogic.getMoveDeclareHavannaher(havannahState));
	}

	private List<Card> getCards(int fromInclusive, int toExclusive) {
		List<Card> cards = Lists.newArrayList();
		for (int i = fromInclusive; i < toExclusive; i++) {
			Rank rank = Rank.values()[i / 4];
			Suit suit = Suit.values()[i % 4];
			cards.add(new Card(suit, rank));
		}
		return cards;
	}

	private ImmutableMap<String, Object> createState(int numberOfWhiteCards,
			int numberOfBlackCards, boolean isHavannaher, Optional<Claim> claim) {
		Map<String, Object> state = Maps.newHashMap();
		state.put(W, havannahLogic.getIndicesInRange(0, numberOfWhiteCards - 1));
		state.put(B, havannahLogic.getIndicesInRange(numberOfWhiteCards,
				numberOfWhiteCards + numberOfBlackCards - 1));
		state.put(
				M,
				havannahLogic.getIndicesInRange(numberOfWhiteCards
						+ numberOfBlackCards, 51));
		if (isHavannaher) {
			state.put(IS_CHEATER, YES);
		}
		if (claim.isPresent()) {
			state.put(CLAIM, Claim.toClaimEntryInGameState(claim.get()));
		}
		// We just reveal all the cards (hidden cards are not relevant for our
		// testing).
		int i = 0;
		for (Card card : getCards(0, 52)) {
			state.put(C + (i++), card.getRank().getFirstLetter()
					+ card.getSuit().getFirstLetterLowerCase());
		}
		return ImmutableMap.copyOf(state);
	}

	private UpdateUI createUpdateUI(int yourPlayerId, int turnOfPlayerId,
			Map<String, Object> state) {
		// Our UI only looks at the current state
		// (we ignore: lastState, lastMovePlayerId,
		// playerIdToNumberOfTokensInPot)
		return new UpdateUI(yourPlayerId, playersInfo, state,
				emptyState, // we ignore lastState
				ImmutableList.<Operation> of(new SetTurn(turnOfPlayerId)), 0,
				ImmutableMap.<Integer, Integer> of());
	}
}