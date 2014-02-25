package org.havannah.client;

import java.util.List;

import org.havannah.client.GameApi.Container;
import org.havannah.client.GameApi.Operation;
import org.havannah.client.GameApi.SetTurn;
import org.havannah.client.GameApi.UpdateUI;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


public class HavannahPresenter {

	// public enum HavannahMessage {addW, addB, W, B}

	public interface View {
		
		void setPresenter(HavannahPresenter havannahPresenter);

		void setViewerState(List<ImmutableList<Integer>> white, 
				List<ImmutableList<Integer>> black);

		void setPlayerState(List<ImmutableList<Integer>> white, 
				List<ImmutableList<Integer>> black);

		void choosePoint(List<Integer> selectedPoint);
		
	}

	private final HavannahLogic havannahLogic = new HavannahLogic();
	private final View view;
	private final Container container;
	/** A viewer doesn't have a color. */
	private Optional<Color> myColor;
	private HavannahState havannahState;
	private List<Integer> selectedPoint;

	public HavannahPresenter(View view, Container container) {
		this.view = view;
		this.container = container;
		view.setPresenter(this);
	}

	/** Updates the presenter and the view with the state in updateUI. */
	public void updateUI(UpdateUI updateUI) {
		List<Integer> playerIds = updateUI.getPlayerIds();
		int yourPlayerId = updateUI.getYourPlayerId();
		int yourPlayerIndex = updateUI.getPlayerIndex(yourPlayerId);
		myColor = yourPlayerIndex == 0 ? Optional.of(Color.W)
				: yourPlayerIndex == 1 ? Optional.of(Color.B) : Optional
						.<Color> absent();
				
		selectedPoint = Lists.newArrayList();
		
		if (updateUI.getState().isEmpty()) {
			// The W player sends the initial setup move.
			if (myColor.isPresent() && myColor.get().isWhite()) {
				sendInitialMove(playerIds);
			}
			return;
		}
		
		Color turnOfColor = null;
		for (Operation operation : updateUI.getLastMove()) {
			if (operation instanceof SetTurn) {
				turnOfColor = Color.values()[playerIds.indexOf(((SetTurn) operation).getPlayerId())];
			}
		}
		
		havannahState = havannahLogic.gameApiStateToHavannahState(updateUI.getState(),
				turnOfColor, playerIds);

		//HavannahMessage havannahMessage = getHavannahMessage();
		
		if (updateUI.isViewer()) {
			view.setViewerState(havannahState.getWhite(), havannahState.getBlack());
			return;
		}
		if (updateUI.isAiPlayer()) {
			// TODO: implement AI in a later HW!
			// container.sendMakeMove(..);
			return;
		}
		// Must be a player!
		view.setPlayerState(havannahState.getWhite(), havannahState.getBlack());
		if (isMyTurn()) {
			choosePoint();
		}
	}

	private boolean isMyTurn() {
		return myColor.isPresent() && myColor.get() == havannahState.getTurn();
	}

	private void choosePoint() {
		view.choosePoint(ImmutableList.copyOf(selectedPoint));
		container.sendMakeMove(HavannahLogic.getMove());
	}

	private void check(boolean val) {
		if (!val) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Adds/remove the card from the {@link #selectedCards}. The view can only
	 * call this method if the presenter called {@link View#chooseNextCard}.
	 */
	public void pointSelected(ImmutableList<Integer> pt) {
		check(isMyTurn());
		choosePoint();
	}
	
	private void sendInitialMove(List<Integer> playerIds) {
		container.sendMakeMove(havannahLogic.getMoveInitial(playerIds));
	}
}