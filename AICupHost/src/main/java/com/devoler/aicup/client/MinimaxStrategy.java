package com.devoler.aicup.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.devoler.aicup.host.model.Battlefield;
import com.devoler.aicup.host.model.LocalStrategy;
import com.devoler.aicup.host.model.Move;
import com.devoler.aicup.host.model.Move.Direction;
import com.devoler.aicup.host.model.Side;
import com.devoler.aicup.host.model.Unit;
import com.devoler.aicup.host.model.util.ImmutableSet;

public abstract class MinimaxStrategy extends LocalStrategy {
	private static class NodeResult {
		private final Move move;
		private final long value;

		public NodeResult(final Move move, final long value) {
			this.move = move;
			this.value = value;
		}
	}

	private static final List<Move> getAllMoves(Battlefield battlefield, Side side) {
		List<Move> moves = new ArrayList<>();
		ImmutableSet<Unit> units = battlefield.getUnits();
		List<Unit> myUnits = new ArrayList<>();

		for (Unit unit : units) {
			if (unit.getSide() == side) {
				myUnits.add(unit);
			}
		}
		if (myUnits.isEmpty()) {
			return moves;
		}

		for (Unit unit : myUnits) {
			// add moves
			if (!unit.getType().isImmovable()) {
				for (Direction direction : Direction.values()) {
					Unit moved = unit.move(direction);
					if (battlefield.canPlaceUnit(moved, unit)) {
						moves.add(new Move.UnitMove(unit, direction));
					}
				}
			}
			// add shots
			if (unit.getShot() == null) {
				continue;
			}
			if (unit.getState().getCooldownPeriod() > 0) {
				continue;
			}
			for (int col = unit.getBounds().getX() - unit.getShot().getRange(); col <= unit.getBounds().getX()
					+ unit.getShot().getRange(); col++) {
				for (int row = unit.getBounds().getY() - unit.getShot().getRange(); row <= unit.getBounds().getY()
						+ unit.getShot().getRange(); row++) {
					if ((battlefield.isInField(col, row))
							&& (unit.getBounds().getManhattanDistance(col, row) <= unit.getShot().getRange())) {
						// check whether it hits at least 1 enemy
						boolean hitsEnemy = false;
						for (Unit target : battlefield.getUnits()) {
							if (target.getSide() != side) {
								if (target.getBounds().getManhattanDistance(col, row) <= unit.getShot().getCollateral()) {
									hitsEnemy = true;
									break;
								}
							}
						}
						if (hitsEnemy) {
							moves.add(new Move.UnitShoot(unit, col, row));
						}
					}
				}
			}
		}
		if (moves.isEmpty()) {
			moves.add(Move.NO_OP);
		}
		return moves;
	}

	private Side switchSide(Side side) {
		return Side.values()[Side.values().length - side.ordinal() - 1];
	}

	private NodeResult minimax(final Battlefield battlefield, final Side side, final int depth, final boolean maximize) {
		if (depth == 0) {
			return new NodeResult(null, evaluateBattlefield(battlefield, side));
		}
		List<Move> moves = getAllMoves(battlefield, side);
		Collections.shuffle(moves);
		if (maximize) {
			long bestValue = Long.MIN_VALUE;
			Move bestMove = null;
			for (Move move : moves) {
				NodeResult result = minimax(battlefield.tick(move, side).getBattlefield(), switchSide(side), depth - 1,
						false);
				if (result.value >= bestValue) {
					bestValue = result.value;
					bestMove = move;
				}
			}
			return new NodeResult(bestMove, bestValue);
		} else {
			long bestValue = Long.MAX_VALUE;
			Move bestMove = null;
			for (Move move : moves) {
				NodeResult result = minimax(battlefield.tick(move, side).getBattlefield(), switchSide(side), depth - 1,
						true);
				if (result.value <= bestValue) {
					bestValue = result.value;
					bestMove = move;
				}
			}
			return new NodeResult(bestMove, bestValue);
		}
	}

	@Override
	public Move getMoveLocally(Battlefield battlefield, Side yourSide) {
		return minimax(battlefield, yourSide, 2, true).move;
	}

	public abstract long evaluateBattlefield(Battlefield battlefield, Side side);
}
