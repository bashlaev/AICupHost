package com.devoler.aicup.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.devoler.aicup.host.model.Battlefield;
import com.devoler.aicup.host.model.LocalStrategy;
import com.devoler.aicup.host.model.Move;
import com.devoler.aicup.host.model.Side;
import com.devoler.aicup.host.model.Strategy;
import com.devoler.aicup.host.model.Unit;
import com.devoler.aicup.host.model.Unit.Type;
import com.devoler.aicup.host.model.util.ImmutableSet;

public class StayAndDefendStrategy extends LocalStrategy {
	Strategy endgameStrategy = new ShootOrMoveToBaseStrategy();

	@Override
	public Move getMoveLocally(Battlefield battlefield, Side yourSide) {
		ImmutableSet<Unit> units = battlefield.getUnits();
		List<Unit> myUnits = new ArrayList<>();

		boolean enemyUnitsLeft = false;
		for (Unit unit : units) {
			if (unit.getSide() == yourSide) {
				myUnits.add(unit);
			} else if (unit.getType() != Type.BASE) {
				enemyUnitsLeft = true;
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

		if (!enemyUnitsLeft) {
			return endgameStrategy.getMove(battlefield, yourSide).getMove();
		}

		// make a random move
		return Move.NO_OP;
	}
}
