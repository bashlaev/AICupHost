package com.devoler.aicup.test.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.devoler.aicup.host.model.Move.Direction;
import com.devoler.aicup.host.model.Side;
import com.devoler.aicup.host.model.Unit;
import com.devoler.aicup.host.model.Unit.Type;
import com.devoler.aicup.host.model.util.ImmutableRectangle;
import com.google.gson.JsonParser;

public class UnitTest {
	private static final Unit SOLDIER = new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(10, 10, 1, 1),
			new Unit.State(10, 3));
	private static final Unit TANK = new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(10, 10, 2, 2),
			new Unit.State(20, 1));
	private static final Unit BASE = new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(10, 10, 3, 3),
			new Unit.State(100, 0));

	private static final String SOLDIER_JSON = "{\"type\":\"SOLDIER\",\"side\":\"BLUE\",\"hp\":10,\"cooldown\":3,\"x\":10,\"y\":10}";
	private static final String TANK_JSON = "{\"type\":\"TANK\",\"side\":\"BLUE\",\"hp\":20,\"cooldown\":1,\"x\":10,\"y\":10}";
	private static final String BASE_JSON = "{\"type\":\"BASE\",\"side\":\"BLUE\",\"hp\":100,\"cooldown\":0,\"x\":10,\"y\":10}";

	@Test
	public void testShoot() {
		assertEquals(SOLDIER, SOLDIER.shoot());
		assertEquals(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(10, 10, 1, 1), new Unit.State(10, SOLDIER
				.getShot().getCooldownPeriod())), SOLDIER.tick().tick().tick().shoot().tick());
		assertEquals(TANK, TANK.shoot());
		assertEquals(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(10, 10, 2, 2), new Unit.State(20, TANK
				.getShot().getCooldownPeriod())), TANK.tick().shoot().tick());
		assertEquals(BASE, BASE.shoot());
	}

	@Test
	public void testShotAt() {
		assertEquals(SOLDIER, SOLDIER.shotAt(null));
		assertEquals(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(10, 10, 1, 1), new Unit.State(7, 3)),
				SOLDIER.shotAt(SOLDIER.getShot()));
		assertEquals(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(10, 10, 1, 1), new Unit.State(4, 3)),
				SOLDIER.shotAt(SOLDIER.getShot()).shotAt(SOLDIER.getShot()));
		assertEquals(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(10, 10, 1, 1), new Unit.State(1, 3)),
				SOLDIER.shotAt(SOLDIER.getShot()).shotAt(SOLDIER.getShot()).shotAt(SOLDIER.getShot()));
		assertEquals(null, SOLDIER.shotAt(SOLDIER.getShot()).shotAt(SOLDIER.getShot()).shotAt(SOLDIER.getShot())
				.shotAt(SOLDIER.getShot()));
		assertEquals(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(10, 10, 1, 1), new Unit.State(5, 3)),
				SOLDIER.shotAt(TANK.getShot()));
		assertEquals(null, SOLDIER.shotAt(TANK.getShot()).shotAt(TANK.getShot()));
		assertEquals(TANK, TANK.shotAt(null));
		assertEquals(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(10, 10, 2, 2), new Unit.State(17, 1)),
				TANK.shotAt(SOLDIER.getShot()));
		assertEquals(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(10, 10, 2, 2), new Unit.State(15, 1)),
				TANK.shotAt(TANK.getShot()));
		assertEquals(BASE, BASE.shotAt(null));
		assertEquals(new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(10, 10, 3, 3), new Unit.State(97, 0)),
				BASE.shotAt(SOLDIER.getShot()));
		assertEquals(new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(10, 10, 3, 3), new Unit.State(95, 0)),
				BASE.shotAt(TANK.getShot()));
	}

	@Test
	public void testTick() {
		assertEquals(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(10, 10, 1, 1), new Unit.State(10, 2)),
				SOLDIER.tick());
		assertEquals(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(10, 10, 1, 1), new Unit.State(10, 1)),
				SOLDIER.tick().tick());
		assertEquals(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(10, 10, 1, 1), new Unit.State(10, 0)),
				SOLDIER.tick().tick().tick());
		assertEquals(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(10, 10, 2, 2), new Unit.State(20, 0)),
				TANK.tick());
		assertEquals(BASE, BASE.tick());
	}

	@Test
	public void testMove() {
		assertEquals(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(9, 10, 1, 1), new Unit.State(10, 3)),
				SOLDIER.move(Direction.LEFT));
		assertEquals(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(11, 10, 1, 1), new Unit.State(10, 3)),
				SOLDIER.move(Direction.RIGHT));
		assertEquals(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(10, 9, 1, 1), new Unit.State(10, 3)),
				SOLDIER.move(Direction.UP));
		assertEquals(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(10, 11, 1, 1), new Unit.State(10, 3)),
				SOLDIER.move(Direction.DOWN));
		assertEquals(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(9, 10, 2, 2), new Unit.State(20, 1)),
				TANK.move(Direction.LEFT));
		assertEquals(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(11, 10, 2, 2), new Unit.State(20, 1)),
				TANK.move(Direction.RIGHT));
		assertEquals(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(10, 9, 2, 2), new Unit.State(20, 1)),
				TANK.move(Direction.UP));
		assertEquals(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(10, 11, 2, 2), new Unit.State(20, 1)),
				TANK.move(Direction.DOWN));
		for (Direction direction : Direction.values()) {
			assertEquals(BASE, BASE.move(direction));
		}
	}

	@Test
	public void testToJson() {
		assertEquals(new JsonParser().parse(SOLDIER_JSON), SOLDIER.toJson());
		assertEquals(new JsonParser().parse(TANK_JSON), TANK.toJson());
		assertEquals(new JsonParser().parse(BASE_JSON), BASE.toJson());
	}

	@Test
	public void testFromJson() {
		assertEquals(SOLDIER, Unit.fromJson(new JsonParser().parse(SOLDIER_JSON).getAsJsonObject()));
		assertEquals(TANK, Unit.fromJson(new JsonParser().parse(TANK_JSON).getAsJsonObject()));
		assertEquals(BASE, Unit.fromJson(new JsonParser().parse(BASE_JSON).getAsJsonObject()));
	}

}
