package com.devoler.aicup.client;

import com.devoler.aicup.host.model.Battlefield;
import com.devoler.aicup.host.model.Move;
import com.devoler.aicup.host.model.Side;
import com.devoler.aicup.host.model.Strategy;

public class DoNothingStrategy implements Strategy {

	@Override
	public Result getMove(Battlefield battlefield, Side yourSide) {
		return new Result(0, Move.NO_OP);
	}

}
