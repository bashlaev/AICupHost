package com.devoler.aicup.client;

import com.devoler.aicup.host.model.Battlefield;
import com.devoler.aicup.host.model.Side;
import com.devoler.aicup.host.model.Unit;
import com.devoler.aicup.host.model.Unit.Type;

public final class SimpleMinimaxStrategy extends MinimaxStrategy {

	@Override
	public long evaluateBattlefield(Battlefield battlefield, Side side) {
		// locate bases
		Unit enemyBase = null;
		Unit ownBase = null;
		for (Unit unit : battlefield.getUnits()) {
			if (unit.getType() == Type.BASE) {
				if (unit.getSide() == side) {
					ownBase = unit;
				} else {
					enemyBase = unit;
				}
			}
		}

		// max value if enemy base destroyed
		if (enemyBase == null) {
			return Long.MAX_VALUE;
		}
		// min value if own base destroyed
		if (ownBase == null) {
			return Long.MIN_VALUE;
		}

		long score = 0;

		// a lot of positive value for each hp off enemy base
		score += 1000000 * (Type.BASE.getHitPoints() - enemyBase.getState().getHitPoints());

		// a lot of negative value for each hp off own base
		score -= 1000000 * (Type.BASE.getHitPoints() - ownBase.getState().getHitPoints());

		// heavily penalize unprotected area within 3 cells of own base
		for (int col = ownBase.getBounds().getX() - 3; col <= ownBase.getBounds().getX() + Type.BASE.getSize() + 3; col++) {
			for (int row = ownBase.getBounds().getY() - 3; row <= ownBase.getBounds().getY() + Type.BASE.getSize() + 3; row++) {
				if ((battlefield.isInField(col, row)) && (ownBase.getBounds().getManhattanDistance(col, row) <= 3)) {
					// check if any of own units protects this cell
					boolean cellProtected = false;
					for (Unit unit : battlefield.getUnits()) {
						if (unit.getType() == Type.BASE) {
							continue;
						}
						if ((unit.getSide() == side)
								&& (unit.getBounds().getManhattanDistance(col, row) <= unit.getShot().getRange()
										+ unit.getShot().getCollateral())) {
							cellProtected = true;
							break;
						}
					}
					if (!cellProtected) {
						score -= 10000;
					}
				}
			}
		}

		// less positive value for each hp on own units and negative value for each hp on enemy units
		for (Unit unit : battlefield.getUnits()) {
			if (unit.getType() == Type.BASE) {
				continue;
			}
			if (unit.getSide() == side) {
				score += (100000 + 10000 * unit.getState().getHitPoints());
			} else {
				score -= (100000 + 10000 * unit.getState().getHitPoints());
			}
		}

		// value unit's proximity to the base in a polynomial manner
		for (Unit unit : battlefield.getUnits()) {
			if (unit.getType() == Type.BASE) {
				continue;
			}
			if (unit.getSide() == side) {
				int distanceToOppositeBase = unit.getBounds().getManhattanDistance(
						enemyBase.getBounds().getX() + Type.BASE.getSize() / 2,
						enemyBase.getBounds().getY() + Type.BASE.getSize() / 2);
				int unitScore = 24 - distanceToOppositeBase;
				if (distanceToOppositeBase <= unit.getShot().getRange()) {
					unitScore *= unitScore;
				}
				score += unitScore;
			} else {
				int distanceToOppositeBase = unit.getBounds().getManhattanDistance(
						ownBase.getBounds().getX() + Type.BASE.getSize() / 2,
						ownBase.getBounds().getY() + Type.BASE.getSize() / 2);
				int unitScore = 24 - distanceToOppositeBase;
				if (distanceToOppositeBase <= unit.getShot().getRange()) {
					unitScore *= unitScore;
				}
				score -= unitScore;
			}
		}

		return score;
	}

}
