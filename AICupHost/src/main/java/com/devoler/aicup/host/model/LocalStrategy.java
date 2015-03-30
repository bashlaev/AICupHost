package com.devoler.aicup.host.model;

public abstract class LocalStrategy implements Strategy {

	@Override
	public final Result getMove(Battlefield battlefield, Side yourSide) {
		long startTime = System.currentTimeMillis();
		Move move = getMoveLocally(battlefield, yourSide);
		return new Result(System.currentTimeMillis() - startTime, move);
	}

	public abstract Move getMoveLocally(Battlefield battlefield, Side yourSide);
}
