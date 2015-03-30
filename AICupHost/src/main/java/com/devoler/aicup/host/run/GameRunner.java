package com.devoler.aicup.host.run;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.devoler.aicup.host.model.Game;
import com.devoler.aicup.host.model.Game.State;
import com.devoler.aicup.host.model.Game.Update;
import com.devoler.aicup.host.model.Side;
import com.devoler.aicup.host.model.Strategy;

class GameRunner implements Callable<GameRunner.Result> {
	public static class Result {
		private final Game.State finalState;
		private final long timeBankRed;
		private final long timeBankBlue;
		private final List<Update> history;

		public Result(final Game.State finalState, final List<Update> history, final long timeBankRed,
				final long timeBankBlue) {
			this.finalState = finalState;
			this.history = history;
			this.timeBankRed = timeBankRed;
			this.timeBankBlue = timeBankBlue;
		}

		public Game.State getFinalState() {
			return finalState;
		}

		// not guarded, for internal use
		public List<Update> getHistory() {
			return history;
		}

		public long getTimeBankRed() {
			return timeBankRed;
		}

		public long getTimeBankBlue() {
			return timeBankBlue;
		}

		@Override
		public String toString() {
			return finalState + " after " + history.size() + " ticks";
		}
	}

	private static final int MAX_TICKS = 2000;

	private final Strategy strategyRed;
	private final Strategy strategyBlue;

	public GameRunner(final Strategy strategyRed, final Strategy strategyBlue) {
		this.strategyRed = strategyRed;
		this.strategyBlue = strategyBlue;
	}

	@Override
	public Result call() {
		Game game = new Game(strategyRed, strategyBlue, MAX_TICKS);
		List<Update> history = new ArrayList<>();
		while (game.getState() == State.RUNNING) {
			Update update = game.tick();
			if (update != null) {
				history.add(update);
			}
		}
		return new Result(game.getState(), history, game.getTimeBank(Side.RED), game.getTimeBank(Side.BLUE));
	}
}
