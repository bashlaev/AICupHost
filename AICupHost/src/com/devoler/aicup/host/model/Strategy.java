package com.devoler.aicup.host.model;

public interface Strategy {
	public static class Result {
		private final long millis;
		private final Move move;

		public Result(final long millis, final Move move) {
			this.millis = millis;
			this.move = move;
		}

		public long getMillis() {
			return millis;
		}

		public Move getMove() {
			return move;
		}
	}

	public Result getMove(Battlefield battlefield, Side yourSide);
}
