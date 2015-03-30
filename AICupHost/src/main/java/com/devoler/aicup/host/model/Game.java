package com.devoler.aicup.host.model;

import static com.devoler.aicup.host.model.Side.BLUE;
import static com.devoler.aicup.host.model.Side.RED;

import java.util.EnumMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.devoler.aicup.host.model.Strategy.Result;
import com.devoler.aicup.host.model.Unit.Type;
import com.devoler.aicup.host.model.util.ImmutableSet;

public class Game {
	private static final int FIELD_WIDTH = 24;
	private static final int FIELD_HEIGHT = 24;

	private static final long TOTAL_MILLIS = TimeUnit.SECONDS.toMillis(110);

	public static final Side FIRST_MOVE_TEAM = RED;

	public static enum State {
		RUNNING("RUNNING"), VICTORY_BLUE("BLUE WINS"), VICTORY_RED("RED WINS"), DRAW("DRAW");

		private final String description;

		private State(final String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	public static Battlefield initBattlefield() {
		return new Battlefield(FIELD_WIDTH, FIELD_HEIGHT)
				.addUnit(new Unit(Type.BASE, RED, 0, 0))
				.addUnit(new Unit(Type.TANK, RED, 4, 4))
				.addUnit(new Unit(Type.TANK, RED, 1, 4))
				.addUnit(new Unit(Type.TANK, RED, 4, 1))
				.addUnit(new Unit(Type.SOLDIER, RED, 0, 7))
				.addUnit(new Unit(Type.SOLDIER, RED, 2, 7))
				.addUnit(new Unit(Type.SOLDIER, RED, 4, 7))
				.addUnit(new Unit(Type.SOLDIER, RED, 6, 7))
				.addUnit(new Unit(Type.SOLDIER, RED, 0, 9))
				.addUnit(new Unit(Type.SOLDIER, RED, 2, 9))
				.addUnit(new Unit(Type.SOLDIER, RED, 4, 9))
				.addUnit(new Unit(Type.SOLDIER, RED, 6, 9))
				.addUnit(new Unit(Type.SOLDIER, RED, 7, 6))
				.addUnit(new Unit(Type.SOLDIER, RED, 7, 4))
				.addUnit(new Unit(Type.SOLDIER, RED, 7, 2))
				.addUnit(new Unit(Type.SOLDIER, RED, 7, 0))
				.addUnit(new Unit(Type.SOLDIER, RED, 9, 6))
				.addUnit(new Unit(Type.SOLDIER, RED, 9, 4))
				.addUnit(new Unit(Type.SOLDIER, RED, 9, 2))
				.addUnit(new Unit(Type.SOLDIER, RED, 9, 0))
				.addUnit(
						new Unit(Type.BASE, BLUE, FIELD_WIDTH - Type.BASE.getSize(), FIELD_HEIGHT - Type.BASE.getSize()))
				.addUnit(
						new Unit(Type.TANK, BLUE, FIELD_WIDTH - Type.TANK.getSize() - 4, FIELD_HEIGHT
								- Type.TANK.getSize() - 4))
				.addUnit(
						new Unit(Type.TANK, BLUE, FIELD_WIDTH - Type.TANK.getSize() - 1, FIELD_HEIGHT
								- Type.TANK.getSize() - 4))
				.addUnit(
						new Unit(Type.TANK, BLUE, FIELD_WIDTH - Type.TANK.getSize() - 4, FIELD_HEIGHT
								- Type.TANK.getSize() - 1))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 0, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 7))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 2, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 7))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 4, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 7))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 6, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 7))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 0, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 9))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 2, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 9))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 4, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 9))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 6, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 9))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 7, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 6))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 7, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 4))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 7, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 2))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 7, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 0))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 9, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 6))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 9, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 4))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 9, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 2))
				.addUnit(
						new Unit(Type.SOLDIER, BLUE, FIELD_WIDTH - Type.SOLDIER.getSize() - 9, FIELD_HEIGHT
								- Type.SOLDIER.getSize() - 0));
	}

	public static class Update {
		private final Move move;
		private final String comment;

		public Update(final Move move, final String comment) {
			this.move = move;
			this.comment = comment;
		}

		public Move getMove() {
			return move;
		}

		public String getComment() {
			return comment;
		}
	}

	private final EnumMap<Side, Strategy> strategies = new EnumMap<>(Side.class);
	private final EnumMap<Side, AtomicLong> timeBanks = new EnumMap<>(Side.class);

	private Side activePlayer;

	private State state = State.RUNNING;

	private Battlefield battlefield;

	private int ticks = 0;
	private final int maxTicks;

	public Game(final Strategy strategyRed, final Strategy strategyBlue, final int maxTicks) {
		if ((strategyRed == null) || (strategyBlue == null)) {
			throw new NullPointerException();
		}
		strategies.put(RED, strategyRed);
		strategies.put(BLUE, strategyBlue);
		timeBanks.put(RED, new AtomicLong(TOTAL_MILLIS));
		timeBanks.put(BLUE, new AtomicLong(TOTAL_MILLIS));
		this.maxTicks = maxTicks;

		battlefield = initBattlefield();

		activePlayer = FIRST_MOVE_TEAM;
	}

	public Battlefield getBattlefield() {
		return battlefield;
	}

	public Update tick() {
		if (state != State.RUNNING) {
			return null;
		}
		if (ticks == maxTicks) {
			state = State.DRAW;
			return null;
		}

		String comment = null;
		Strategy strategy = strategies.get(activePlayer);
		AtomicLong timeBank = timeBanks.get(activePlayer);
		Move move;
		if (timeBank.get() > 0) {
			long startTime = System.currentTimeMillis();
			try {
				Result result = strategy.getMove(battlefield, activePlayer);
				move = result.getMove();
				timeBank.addAndGet(-1 * result.getMillis());
				if (move == null) {
					comment = "Could not obtain move";
					move = Move.NO_OP;
				}
			} catch (Throwable t) {
				comment = "Could not obtain move, cause: " + t.getMessage();
				move = Move.NO_OP;
				timeBank.addAndGet(System.currentTimeMillis() - startTime);
			}
		} else {
			comment = "Time bank empty, move skipped";
			move = Move.NO_OP;
		}
		Battlefield.Update update = battlefield.tick(move, activePlayer);
		battlefield = update.getBattlefield();
		if (!move.equals(update.getActualMove())) {
			comment = "Invalid move: " + move + ", replaced by " + update.getActualMove();
		}
		checkVictoryConditions();
		ticks++;
		switchActivePlayer();
		return new Update(move, comment);
	}

	private void checkVictoryConditions() {
		// if a player has no base - he lost
		ImmutableSet<Unit> units = battlefield.getUnits();
		boolean redHasBase = false;
		boolean blueHasBase = false;
		for (Unit unit : units) {
			if (unit.getType() == Type.BASE) {
				if (unit.getSide() == BLUE) {
					blueHasBase = true;
				} else {
					redHasBase = true;
				}
			}
		}
		if (!blueHasBase) {
			state = State.VICTORY_RED;
		} else if (!redHasBase) {
			state = State.VICTORY_BLUE;
		}
	}

	public int getTicks() {
		return ticks;
	}

	public State getState() {
		return state;
	}

	private void switchActivePlayer() {
		activePlayer = (activePlayer == RED ? BLUE : RED);
	}

	public long getTimeBank(Side side) {
		return timeBanks.get(side).get();
	}

}
