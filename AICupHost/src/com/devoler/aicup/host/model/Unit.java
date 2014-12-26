package com.devoler.aicup.host.model;

import com.devoler.aicup.host.model.Move.Direction;
import com.devoler.aicup.host.model.util.ImmutableRectangle;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Unit {
	private static final String JSON_NAME_Y = "y";
	private static final String JSON_NAME_X = "x";
	private static final String JSON_NAME_COOLDOWN = "cooldown";
	private static final String JSON_NAME_HP = "hp";
	private static final String JSON_NAME_SIDE = "side";
	private static final String JSON_NAME_TYPE = "type";

	public static class Shot {
		private final int range;
		private final int damage;
		private final int collateral;
		private final int cooldownPeriod;

		public Shot(final int range, final int damage, final int collateral, final int cooldownPeriod) {
			this.range = range;
			this.damage = damage;
			this.collateral = collateral;
			this.cooldownPeriod = cooldownPeriod;
		}

		public int getRange() {
			return range;
		}

		public int getDamage() {
			return damage;
		}

		public int getCollateral() {
			return collateral;
		}

		public int getCooldownPeriod() {
			return cooldownPeriod;
		}
	}

	public static enum Type {
		BASE(true, 3, 100, null), SOLDIER(false, 1, 15, new Shot(2, 3, 0, 3)), TANK(false, 2, 40, new Shot(4, 5, 1, 8));

		private final boolean isImmovable;
		private final int size;
		private final int hitPoints;
		private final Shot shot;

		Type(final boolean isImmovable, final int size, final int hitPoints, final Shot shot) {
			this.isImmovable = isImmovable;
			this.size = size;
			this.hitPoints = hitPoints;
			this.shot = shot;
		}

		public int getSize() {
			return size;
		}

		public int getHitPoints() {
			return hitPoints;
		}

		public boolean isImmovable() {
			return isImmovable;
		}
	}

	public static class State {
		private final int hitPoints;
		private final int cooldownPeriod;

		public State(final int hitPoints, final int cooldownPeriod) {
			this.hitPoints = hitPoints;
			this.cooldownPeriod = cooldownPeriod;
		}

		public int getHitPoints() {
			return hitPoints;
		}

		public int getCooldownPeriod() {
			return cooldownPeriod;
		}

		public State tick() {
			if (cooldownPeriod > 0) {
				return new State(hitPoints, cooldownPeriod - 1);
			}
			return this;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof State))
				return false;

			State that = (State) o;
			return (this.hitPoints == that.hitPoints) && (this.cooldownPeriod == that.cooldownPeriod);
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			hashCode = 31 * hashCode + hitPoints;
			hashCode = 31 * hashCode + cooldownPeriod;
			return hashCode;
		}

		@Override
		public String toString() {
			return "HP: " + getHitPoints() + ", cooldown: " + cooldownPeriod;
		}
	}

	private final Type type;
	private final Side side;

	private final ImmutableRectangle bounds;

	private final State state;

	public Unit(final Type type, final Side side, final int x, final int y) {
		this(type, side, new ImmutableRectangle(x, y, type.getSize(), type.getSize()),
				new State(type.getHitPoints(), 0));
	}

	public Unit(final Type type, final Side side, final ImmutableRectangle bounds, final State state) {
		if ((type == null) || (side == null) || (bounds == null) || (state == null)) {
			throw new NullPointerException();
		}
		if ((bounds.getWidth() != type.getSize()) || (bounds.getHeight() != type.getSize())) {
			throw new IllegalArgumentException("Invalid unit bounds: " + bounds);
		}
		if (state.getHitPoints() > type.getHitPoints()) {
			throw new IllegalArgumentException("Invalid hit points: " + state.getHitPoints());
		}
		int maxCooldown = 0;
		if (type.shot != null) {
			maxCooldown = type.shot.getCooldownPeriod() + 1;
		}
		if ((state.getCooldownPeriod() < 0) || (state.getCooldownPeriod() > maxCooldown)) {
			throw new IllegalArgumentException("Invalid cooldown period: " + state.getCooldownPeriod());
		}
		this.type = type;
		this.side = side;
		this.state = state;
		this.bounds = bounds;
	}

	public Type getType() {
		return type;
	}

	public Side getSide() {
		return side;
	}

	public ImmutableRectangle getBounds() {
		return bounds;
	}

	public State getState() {
		return state;
	}

	public Shot getShot() {
		return type.shot;
	}

	public Unit shoot() {
		if (type.shot == null) {
			return this;
		}
		if (state.getCooldownPeriod() > 0) {
			return this;
		}
		return new Unit(type, side, bounds, new State(state.getHitPoints(), type.shot.getCooldownPeriod() + 1));
	}

	public Unit shotAt(Shot shot) {
		if (shot == null) {
			return this;
		}
		int newHitPoints = state.getHitPoints() - shot.getDamage();
		if (newHitPoints <= 0) {
			// unit is dead
			return null;
		}
		return new Unit(type, side, bounds, new State(newHitPoints, state.getCooldownPeriod()));
	}

	public Unit tick() {
		State newState = state.tick();
		if (newState != state) {
			return new Unit(type, side, bounds, newState);
		}
		return this;
	}

	public Unit move(Direction direction) {
		if (type.isImmovable()) {
			return this;
		}
		final ImmutableRectangle newBounds;
		switch (direction) {
		case DOWN:
			newBounds = bounds.moveDown();
			break;
		case UP:
			newBounds = bounds.moveUp();
			break;
		case LEFT:
			newBounds = bounds.moveLeft();
			break;
		case RIGHT:
			newBounds = bounds.moveRight();
			break;
		default:
			throw new RuntimeException("Direction not recognized: " + direction);
		}
		return new Unit(type, side, newBounds, state);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Unit))
			return false;

		Unit that = (Unit) o;
		return (this.type == that.type) && (this.side == that.side) && (this.state.equals(that.state))
				&& (this.bounds.equals(that.bounds));
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + type.hashCode();
		hashCode = 31 * hashCode + side.hashCode();
		hashCode = 31 * hashCode + state.hashCode();
		hashCode = 31 * hashCode + bounds.hashCode();
		return hashCode;
	}

	@Override
	public String toString() {
		return type.name() + " of " + side.name() + " at [" + bounds.getX() + "," + bounds.getY() + "], state: "
				+ state;
	}

	public JsonElement toJson() {
		JsonObject result = new JsonObject();
		result.addProperty(JSON_NAME_TYPE, type.name());
		result.addProperty(JSON_NAME_SIDE, side.name());
		result.addProperty(JSON_NAME_HP, state.getHitPoints());
		result.addProperty(JSON_NAME_COOLDOWN, state.getCooldownPeriod());
		result.addProperty(JSON_NAME_X, bounds.getX());
		result.addProperty(JSON_NAME_Y, bounds.getY());
		return result;
	}

	public static Unit fromJson(JsonObject jsonObject) {
		Type type = Type.valueOf(jsonObject.get(JSON_NAME_TYPE).getAsString().toUpperCase());
		Side team = Side.valueOf(jsonObject.get(JSON_NAME_SIDE).getAsString().toUpperCase());
		int hp = jsonObject.get(JSON_NAME_HP).getAsInt();
		int cooldown = jsonObject.get(JSON_NAME_COOLDOWN).getAsInt();
		int x = jsonObject.get(JSON_NAME_X).getAsInt();
		int y = jsonObject.get(JSON_NAME_Y).getAsInt();
		return new Unit(type, team, new ImmutableRectangle(x, y, type.getSize(), type.getSize()), new State(hp,
				cooldown));
	}
}
