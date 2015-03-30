package com.devoler.aicup.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.devoler.aicup.host.model.Battlefield;
import com.devoler.aicup.host.model.LocalStrategy;
import com.devoler.aicup.host.model.Move;
import com.devoler.aicup.host.model.Move.Direction;
import com.devoler.aicup.host.model.Side;
import com.devoler.aicup.host.model.Unit;
import com.devoler.aicup.host.model.util.ImmutableSet;

public class RandomMoveStrategy extends LocalStrategy {
	private final Random rnd = new Random();

	@Override
	public Move getMoveLocally(Battlefield battlefield, Side yourSide) {
		ImmutableSet<Unit> units = battlefield.getUnits();
		List<Unit> myUnits = new ArrayList<>();

		for (Unit unit : units) {
			if (unit.getSide() == yourSide) {
				myUnits.add(unit);
			}
		}
		if (myUnits.isEmpty()) {
			return Move.NO_OP;
		}

		// make a random move
		return new Move.UnitMove(myUnits.get(rnd.nextInt(myUnits.size())), Direction.values()[rnd.nextInt(Direction
				.values().length)]);
	}

}
