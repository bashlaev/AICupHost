package com.devoler.aicup.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.devoler.aicup.host.model.Battlefield;
import com.devoler.aicup.host.model.LocalStrategy;
import com.devoler.aicup.host.model.Move;
import com.devoler.aicup.host.model.Move.Direction;
import com.devoler.aicup.host.model.Side;
import com.devoler.aicup.host.model.Unit;
import com.devoler.aicup.host.model.Unit.Type;
import com.devoler.aicup.host.model.util.ImmutableSet;

public class ShootOrMoveToBaseStrategy extends LocalStrategy {
	@Override
	public Move getMoveLocally(Battlefield battlefield, Side yourSide) {
		ImmutableSet<Unit> units = battlefield.getUnits();
		List<Unit> myUnits = new ArrayList<>();

		Unit enemyBase = null;

		for (Unit unit : units) {
			if (unit.getSide() == yourSide) {
				myUnits.add(unit);
			} else if (unit.getType() == Type.BASE) {
				enemyBase = unit;
			}
		}
		if (myUnits.isEmpty()) {
			return Move.NO_OP;
		}

		// shoot if possible
		List<Move> shots = new ArrayList<>();
		for (Unit unit : myUnits) {
			if (unit.getShot() == null) {
				continue;
			}
			if (unit.getState().getCooldownPeriod() > 0) {
				continue;
			}
			for (Unit target : units) {
				if (target.getSide() == yourSide) {
					continue;
				}
				for (int x = target.getBounds().getX(); x < target.getBounds().getX() + target.getBounds().getWidth(); x++) {
					for (int y = target.getBounds().getY(); y < target.getBounds().getY()
							+ target.getBounds().getHeight(); y++) {
						if (unit.getBounds().getManhattanDistance(x, y) <= unit.getShot().getRange()) {
							shots.add(new Move.UnitShoot(unit, x, y));
						}
					}
				}
			}
		}
		// if possible, make a random shot
		if (!shots.isEmpty()) {
			return shots.get(ThreadLocalRandom.current().nextInt(shots.size()));
		}

		// move a random unit towards enemy base
		List<Move> moves = new ArrayList<>();
		for (Unit unit : myUnits) {
			if (unit.getType().isImmovable()) {
				continue;
			}

			// check if enemy base is within range
			boolean enemyBaseWithinRange = false;
			for (int x = enemyBase.getBounds().getX(); x < enemyBase.getBounds().getX()
					+ enemyBase.getBounds().getWidth(); x++) {
				for (int y = enemyBase.getBounds().getY(); y < enemyBase.getBounds().getY()
						+ enemyBase.getBounds().getHeight(); y++) {
					if (unit.getBounds().getManhattanDistance(x, y) <= unit.getShot().getRange()) {
						enemyBaseWithinRange = true;
						break;
					}
				}
			}

			if (!enemyBaseWithinRange) {
				// get moves that make unit come closer to enemy base
				if (2 * unit.getBounds().getX() + unit.getBounds().getWidth() < 2 * enemyBase.getBounds().getX()
						+ enemyBase.getBounds().getWidth()) {
					moves.add(new Move.UnitMove(unit, Direction.RIGHT));
				}
				if (2 * unit.getBounds().getX() + unit.getBounds().getWidth() > 2 * enemyBase.getBounds().getX()
						+ enemyBase.getBounds().getWidth()) {
					moves.add(new Move.UnitMove(unit, Direction.LEFT));
				}
				if (2 * unit.getBounds().getY() + unit.getBounds().getHeight() < 2 * enemyBase.getBounds().getY()
						+ enemyBase.getBounds().getHeight()) {
					moves.add(new Move.UnitMove(unit, Direction.DOWN));
				}
				if (2 * unit.getBounds().getY() + unit.getBounds().getHeight() > 2 * enemyBase.getBounds().getY()
						+ enemyBase.getBounds().getHeight()) {
					moves.add(new Move.UnitMove(unit, Direction.UP));
				}
			}
		}

		if (!moves.isEmpty()) {
			// choose a random move
			return moves.get(ThreadLocalRandom.current().nextInt(moves.size()));
		}

		return Move.NO_OP;
	}
}
