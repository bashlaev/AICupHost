package com.devoler.aicup.host.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class Move {
	private static final String JSON_VALUE_SHOOT = "shoot";
	private static final String JSON_VALUE_MOVE = "move";
	private static final String JSON_NAME_TARGET_Y = "targetY";
	private static final String JSON_NAME_TARGET_X = "targetX";
	private static final String JSON_NAME_DIRECTION = "direction";
	private static final String JSON_NAME_ACTION = "action";
	private static final String JSON_NAME_UNIT_X = "unitX";
	private static final String JSON_NAME_UNIT_Y = "unitY";

	public static final Move NO_OP = new Move() {
		@Override
		public String toString() {
			return "NO MOVE";
		}

		@Override
		public JsonElement toJson() {
			return new JsonObject();
		};
	};

	public static enum Direction {
		LEFT, RIGHT, UP, DOWN;
	}

	public static final class UnitMove extends Move {
		private final Unit unit;
		private final Direction direction;

		public UnitMove(final Unit unit, final Direction direction) {
			if ((unit == null) || (direction == null)) {
				throw new NullPointerException();
			}
			this.unit = unit;
			this.direction = direction;
		}

		public Unit getUnit() {
			return unit;
		}

		public Direction getDirection() {
			return direction;
		}

		@Override
		public String toString() {
			return "MOVE, unit [" + unit + "], direction: " + direction.name();
		}

		@Override
		public JsonElement toJson() {
			JsonObject result = new JsonObject();
			result.addProperty(JSON_NAME_ACTION, JSON_VALUE_MOVE);
			result.addProperty(JSON_NAME_UNIT_X, unit.getBounds().getX());
			result.addProperty(JSON_NAME_UNIT_Y, unit.getBounds().getY());
			result.addProperty(JSON_NAME_DIRECTION, direction.name());
			return result;
		}
	}

	public static final class UnitShoot extends Move {
		private final Unit unit;
		private final int x;
		private final int y;

		public UnitShoot(final Unit unit, final int x, final int y) {
			if (unit == null) {
				throw new NullPointerException();
			}
			this.unit = unit;
			this.x = x;
			this.y = y;
		}

		public Unit getUnit() {
			return unit;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		@Override
		public String toString() {
			return "SHOOT, unit [" + unit + "], target: [" + x + ", " + y + "]";
		}

		@Override
		public JsonElement toJson() {
			JsonObject result = new JsonObject();
			result.addProperty(JSON_NAME_ACTION, JSON_VALUE_SHOOT);
			result.addProperty(JSON_NAME_UNIT_X, unit.getBounds().getX());
			result.addProperty(JSON_NAME_UNIT_Y, unit.getBounds().getY());
			result.addProperty(JSON_NAME_TARGET_X, x);
			result.addProperty(JSON_NAME_TARGET_Y, y);
			return result;
		}
	}

	private Move() {
	}

	public abstract JsonElement toJson();

	public static Move fromJson(JsonObject root, Battlefield battlefield) {
		JsonElement actionElement = root.getAsJsonObject().get(JSON_NAME_ACTION);
		if (actionElement == null) {
			return Move.NO_OP;
		}
		String action = actionElement.getAsString();
		if (action.equalsIgnoreCase(JSON_VALUE_MOVE)) {
			int unitX = root.getAsJsonObject().get(JSON_NAME_UNIT_X).getAsInt();
			int unitY = root.getAsJsonObject().get(JSON_NAME_UNIT_Y).getAsInt();
			Unit target = null;
			for (Unit unit : battlefield.getUnits()) {
				if ((unit.getBounds().getX() == unitX) && (unit.getBounds().getY() == unitY)) {
					target = unit;
					break;
				}
			}
			if (target == null) {
				throw new RuntimeException("Unit not found, x: " + unitX + ", y: " + unitY);
			}
			String directionString = root.getAsJsonObject().get(JSON_NAME_DIRECTION).getAsString().toUpperCase();
			Direction direction = Direction.valueOf(directionString);
			if (direction == null) {
				throw new RuntimeException("Invalid direction: " + directionString);
			}

			return new Move.UnitMove(target, direction);
		} else if (action.equalsIgnoreCase(JSON_VALUE_SHOOT)) {
			int unitX = root.getAsJsonObject().get(JSON_NAME_UNIT_X).getAsInt();
			int unitY = root.getAsJsonObject().get(JSON_NAME_UNIT_Y).getAsInt();
			Unit target = null;
			for (Unit unit : battlefield.getUnits()) {
				if ((unit.getBounds().getX() == unitX) && (unit.getBounds().getY() == unitY)) {
					target = unit;
					break;
				}
			}
			if (target == null) {
				throw new RuntimeException("Unit not found, x: " + unitX + ", y: " + unitY);
			}
			int targetX = root.getAsJsonObject().get(JSON_NAME_TARGET_X).getAsInt();
			int targetY = root.getAsJsonObject().get(JSON_NAME_TARGET_Y).getAsInt();
			return new Move.UnitShoot(target, targetX, targetY);
		} else {
			throw new RuntimeException("Unrecognized action: " + action);
		}
	}
}
