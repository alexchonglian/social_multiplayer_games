package org.havannah.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * Representation of the cheat game state. The game state uses these keys:
 * isCheater, W, B, M, claim, C1...C54 which are mapped to these fields:
 * isCheater, white, black, middle, claim, cards
 */
public class HavannahState {
	private final Color turn;
	private final ImmutableList<Integer> playerIds;

	private final ImmutableList<ImmutableList<Integer>> white;
	private final ImmutableList<ImmutableList<Integer>> black;

	public HavannahState(Color turn, ImmutableList<Integer> playerIds, 
			ImmutableList<ImmutableList<Integer>> white,
			ImmutableList<ImmutableList<Integer>> black) {
		super();
		this.turn = checkNotNull(turn);
		this.playerIds = checkNotNull(playerIds);
		this.white = checkNotNull(white);
		this.black = checkNotNull(black);
	}

	public Color getTurn() {
		return turn;
	}

	public ImmutableList<Integer> getPlayerIds() {
		return playerIds;
	}

	public int getPlayerId(Color color) {
		return playerIds.get(color.ordinal());
	}

	public ImmutableList<ImmutableList<Integer>> getWhite() {
		return white;
	}

	public ImmutableList<ImmutableList<Integer>> getBlack() {
		return black;
	}

	public ImmutableList<ImmutableList<Integer>> getWhiteOrBlack(Color color) {
		return color.isWhite() ? white : black;
	}

}