package com.devoler.aicup.host.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.devoler.aicup.host.model.Unit.Shot;
import com.devoler.aicup.host.model.util.ImmutableRectangle;
import com.devoler.aicup.host.model.util.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Battlefield is a matrix of cells that may be occupied by players' units. It will often be a square, but may be a
 * rectangle as well.
 * 
 * @author homer
 */
public final class Battlefield {
	private static final String JSON_NAME_UNITS = "units";
	private static final String JSON_NAME_HEIGHT = "height";
	private static final String JSON_NAME_WIDTH = "width";

	public static class Update {
		private final Battlefield battlefield;
		private final Move actualMove;

		public Update(final Battlefield battlefield, final Move actualMove) {
			this.battlefield = battlefield;
			this.actualMove = actualMove;
		}

		public Move getActualMove() {
			return actualMove;
		}

		public Battlefield getBattlefield() {
			return battlefield;
		}
	}

	private final int width;
	private final int height;

	private final ImmutableRectangle fieldRectangle;

	private final ImmutableSet<Unit> units;

	public Battlefield(final int width, final int height) {
		this(width, height, new ImmutableSet<Unit>());
	}

	private Battlefield(final int width, final int height, final ImmutableSet<Unit> units) {
		this.width = width;
		this.height = height;
		fieldRectangle = new ImmutableRectangle(0, 0, width, height);
		this.units = units;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isInField(int x, int y) {
		return fieldRectangle.contains(x, y);
	}

	public Battlefield addUnit(Unit unit) {
		if (unit == null) {
			throw new NullPointerException();
		}
		if (!canPlaceUnit(unit)) {
			throw new RuntimeException("Can't place unit, battlefield:\r\n" + this + "\r\nunit: " + unit);
		}

		return updateUnits(units.add(unit));
	}

	public Update tick(final Move originalMove, final Side activePlayer) {
		Move actualMove = originalMove;

		ImmutableSet<Unit> currentUnits = units;
		if (originalMove instanceof Move.UnitMove) {
			// move unit
			Move.UnitMove unitMove = (Move.UnitMove) originalMove;
			Unit unit = unitMove.getUnit();
			if ((units.contains(unit)) && (unit.getSide() == activePlayer)) {
				final Unit newUnit = unit.move(unitMove.getDirection());
				if (canPlaceUnit(newUnit, unit)) {
					currentUnits = currentUnits.replace(unit, newUnit);
				} else {
					// System.out.println("Can't move unit");
					actualMove = Move.NO_OP;
				}
			} else {
				// System.out.println("invalid unit, unit: " + unit +
				// ", active player: " + activePlayer + ", ==: "
				// + (unit.getTeam() == activePlayer) + ", units: " + units);
				actualMove = Move.NO_OP;
			}
		} else if (originalMove instanceof Move.UnitShoot) {
			// shoot
			Move.UnitShoot unitShoot = (Move.UnitShoot) originalMove;
			Unit shooter = unitShoot.getUnit();
			if ((units.contains(shooter)) && (shooter.getSide() == activePlayer)) {
				Shot shot = shooter.getShot();
				int x = unitShoot.getX();
				int y = unitShoot.getY();
				int distance = shooter.getBounds().getManhattanDistance(x, y);
				if ((distance <= 0) || (distance > shot.getRange()) || (shooter.getState().getCooldownPeriod() > 0)) {
					// invalid shot
					actualMove = Move.NO_OP;
				} else {
					// apply to shooter
					currentUnits = currentUnits.replace(shooter, shooter.shoot());

					// apply to targets
					ImmutableRectangle target = new ImmutableRectangle(x, y, 1, 1);
					for (int col = x - shot.getCollateral(); col <= x + shot.getCollateral(); col++) {
						for (int row = y - shot.getCollateral(); row <= y + shot.getCollateral(); row++) {
							// cross-like area
							if (target.getManhattanDistance(col, row) > shot.getCollateral()) {
								continue;
							}
							for (Iterator<Unit> i = currentUnits.iterator(); i.hasNext();) {
								Unit unit = i.next();
								if (unit.getBounds().contains(col, row)) {
									currentUnits = currentUnits.replace(unit, unit.shotAt(shot));
									break;
								}
							}
						}
					}
				}
			} else {
				// shooter unit not found
				actualMove = Move.NO_OP;
			}
		}
		for (Iterator<Unit> i = currentUnits.iterator(); i.hasNext();) {
			Unit unit = i.next();
			if (unit.getSide() == activePlayer) {
				currentUnits = currentUnits.replace(unit, unit.tick());
			}
		}
		return new Update(updateUnits(currentUnits), actualMove);
	}

	private Battlefield updateUnits(ImmutableSet<Unit> units) {
		if (this.units.equals(units)) {
			return this;
		}
		return new Battlefield(width, height, units);
	}

	public ImmutableSet<Unit> getUnits() {
		return units;
	}

	public boolean canPlaceUnit(Unit unit) {
		return canPlaceUnit(unit, null);
	}

	public boolean canPlaceUnit(Unit unit, Unit insteadOf) {
		if (!fieldRectangle.contains(unit.getBounds())) {
			return false;
		}
		// check for collision with other units
		for (Unit otherUnit : units) {
			if (!otherUnit.equals(insteadOf)) {
				if (unit.getBounds().intersects(otherUnit.getBounds())) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Battlefield " + width + "x" + height + ", units: " + units;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Battlefield))
			return false;

		Battlefield that = (Battlefield) o;
		return (this.width == that.width) && (this.height == that.height) && (this.units.equals(that.units));
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + width;
		hashCode = 31 * hashCode + height;
		hashCode = 31 * hashCode + units.hashCode();
		return hashCode;
	}

	public String print() {
		StringBuilder b = new StringBuilder();
		b.append('\u250F');
		for (int col = 0; col < width; col++) {
			b.append('\u2501');
		}
		b.append('\u2513').append("\r\n");
		for (int row = 0; row < height; row++) {
			b.append('\u2503');
			for (int col = 0; col < width; col++) {
				boolean cellOccupied = false;
				for (Unit unit : units) {
					if (unit.getBounds().contains(col, row)) {
						b.append((char) ((unit.getSide() == Side.BLUE ? 'B' : 'R') + unit.getType().ordinal()));
						cellOccupied = true;
						break;
					}
				}
				if (!cellOccupied) {
					b.append(' ');
				}
			}
			b.append('\u2503').append("\r\n");
		}
		b.append('\u2517');
		for (int col = 0; col < width; col++) {
			b.append('\u2501');
		}
		b.append('\u251B').append("\r\n");
		return b.toString();
	}

	public JsonElement toJson() {
		JsonObject result = new JsonObject();
		result.addProperty(JSON_NAME_WIDTH, width);
		result.addProperty(JSON_NAME_HEIGHT, height);
		JsonArray unitArray = new JsonArray();
		for (Unit unit : units) {
			unitArray.add(unit.toJson());
		}
		result.add(JSON_NAME_UNITS, unitArray);
		return result;
	}

	public static Battlefield fromJson(JsonObject jsonObject) {
		int width = jsonObject.get(JSON_NAME_WIDTH).getAsInt();
		int height = jsonObject.get(JSON_NAME_HEIGHT).getAsInt();
		JsonArray jsonArray = jsonObject.get(JSON_NAME_UNITS).getAsJsonArray();
		List<Unit> unitList = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			unitList.add(Unit.fromJson(jsonArray.get(i).getAsJsonObject()));
		}
		return new Battlefield(width, height, new ImmutableSet<>(unitList));
	}
}
