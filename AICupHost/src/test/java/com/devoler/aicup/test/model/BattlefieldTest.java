package com.devoler.aicup.test.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.devoler.aicup.host.model.Battlefield;
import com.devoler.aicup.host.model.Move;
import com.devoler.aicup.host.model.Move.Direction;
import com.devoler.aicup.host.model.Side;
import com.devoler.aicup.host.model.Unit;
import com.devoler.aicup.host.model.Unit.Type;
import com.devoler.aicup.host.model.util.ImmutableRectangle;
import com.google.gson.JsonParser;

public class BattlefieldTest {
	private static final Battlefield BATTLEFIELD = new Battlefield(6, 6)
			.addUnit(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1), new Unit.State(10, 3)))
			.addUnit(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2), new Unit.State(20, 1)))
			.addUnit(new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(0, 0, 3, 3), new Unit.State(100, 0)))
			.addUnit(new Unit(Type.SOLDIER, Side.RED, new ImmutableRectangle(5, 3, 1, 1), new Unit.State(10, 3)))
			.addUnit(new Unit(Type.TANK, Side.RED, new ImmutableRectangle(3, 3, 2, 2), new Unit.State(20, 1)))
			.addUnit(new Unit(Type.BASE, Side.RED, new ImmutableRectangle(0, 3, 3, 3), new Unit.State(100, 0)));

	private static final Battlefield BATTLEFIELD_SHOOT = new Battlefield(6, 6)
			.addUnit(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1), new Unit.State(10, 0)))
			.addUnit(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2), new Unit.State(20, 0)))
			.addUnit(new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(0, 0, 3, 3), new Unit.State(100, 0)))
			.addUnit(new Unit(Type.SOLDIER, Side.RED, new ImmutableRectangle(5, 3, 1, 1), new Unit.State(10, 0)))
			.addUnit(new Unit(Type.TANK, Side.RED, new ImmutableRectangle(3, 3, 2, 2), new Unit.State(20, 0)))
			.addUnit(new Unit(Type.BASE, Side.RED, new ImmutableRectangle(0, 3, 3, 3), new Unit.State(100, 0)));

	private static final Battlefield BATTLEFIELD_NO_MOVE_RED = new Battlefield(6, 6)
			.addUnit(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1), new Unit.State(10, 3)))
			.addUnit(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2), new Unit.State(20, 1)))
			.addUnit(new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(0, 0, 3, 3), new Unit.State(100, 0)))
			.addUnit(new Unit(Type.SOLDIER, Side.RED, new ImmutableRectangle(5, 3, 1, 1), new Unit.State(10, 2)))
			.addUnit(new Unit(Type.TANK, Side.RED, new ImmutableRectangle(3, 3, 2, 2), new Unit.State(20, 0)))
			.addUnit(new Unit(Type.BASE, Side.RED, new ImmutableRectangle(0, 3, 3, 3), new Unit.State(100, 0)));

	private static final Battlefield BATTLEFIELD_NO_MOVE_BLUE = new Battlefield(6, 6)
			.addUnit(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1), new Unit.State(10, 2)))
			.addUnit(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2), new Unit.State(20, 0)))
			.addUnit(new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(0, 0, 3, 3), new Unit.State(100, 0)))
			.addUnit(new Unit(Type.SOLDIER, Side.RED, new ImmutableRectangle(5, 3, 1, 1), new Unit.State(10, 3)))
			.addUnit(new Unit(Type.TANK, Side.RED, new ImmutableRectangle(3, 3, 2, 2), new Unit.State(20, 1)))
			.addUnit(new Unit(Type.BASE, Side.RED, new ImmutableRectangle(0, 3, 3, 3), new Unit.State(100, 0)));

	private static final String BATTLEFIELD_JSON = "{\"width\":6,\"height\":6,\"units\":[{\"type\":\"SOLDIER\",\"side\":\"BLUE\",\"hp\":10,\"cooldown\":3,\"x\":5,\"y\":0},{\"type\":\"TANK\",\"side\":\"BLUE\",\"hp\":20,\"cooldown\":1,\"x\":3,\"y\":0},{\"type\":\"BASE\",\"side\":\"BLUE\",\"hp\":100,\"cooldown\":0,\"x\":0,\"y\":0},{\"type\":\"SOLDIER\",\"side\":\"RED\",\"hp\":10,\"cooldown\":3,\"x\":5,\"y\":3},{\"type\":\"TANK\",\"side\":\"RED\",\"hp\":20,\"cooldown\":1,\"x\":3,\"y\":3},{\"type\":\"BASE\",\"side\":\"RED\",\"hp\":100,\"cooldown\":0,\"x\":0,\"y\":3}]}";

	@Test
	public void testTick() {
		assertEquals(BATTLEFIELD_NO_MOVE_RED, BATTLEFIELD.tick(Move.NO_OP, Side.RED).getBattlefield());
		assertEquals(BATTLEFIELD_NO_MOVE_BLUE, BATTLEFIELD.tick(Move.NO_OP, Side.BLUE).getBattlefield());
		assertEquals(BATTLEFIELD_SHOOT, BATTLEFIELD_SHOOT.tick(Move.NO_OP, Side.RED).getBattlefield());
		assertEquals(BATTLEFIELD_SHOOT, BATTLEFIELD_SHOOT.tick(Move.NO_OP, Side.BLUE).getBattlefield());
	}

	@Test
	public void testMovement() {
		assertEquals(
				new Battlefield(6, 6)
						.addUnit(
								new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1), new Unit.State(
										10, 2)))
						.addUnit(
								new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 1, 2, 2),
										new Unit.State(20, 0)))
						.addUnit(
								new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(0, 0, 3, 3), new Unit.State(100,
										0)))
						.addUnit(
								new Unit(Type.SOLDIER, Side.RED, new ImmutableRectangle(5, 3, 1, 1), new Unit.State(10,
										3)))
						.addUnit(
								new Unit(Type.TANK, Side.RED, new ImmutableRectangle(3, 3, 2, 2), new Unit.State(20, 1)))
						.addUnit(
								new Unit(Type.BASE, Side.RED, new ImmutableRectangle(0, 3, 3, 3),
										new Unit.State(100, 0))),
				BATTLEFIELD.tick(
						new Move.UnitMove(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2),
								new Unit.State(20, 1)), Direction.DOWN), Side.BLUE).getBattlefield());
		assertEquals(
				new Battlefield(6, 6)
						.addUnit(
								new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 1, 1, 1), new Unit.State(
										10, 2)))
						.addUnit(
								new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2),
										new Unit.State(20, 0)))
						.addUnit(
								new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(0, 0, 3, 3), new Unit.State(100,
										0)))
						.addUnit(
								new Unit(Type.SOLDIER, Side.RED, new ImmutableRectangle(5, 3, 1, 1), new Unit.State(10,
										3)))
						.addUnit(
								new Unit(Type.TANK, Side.RED, new ImmutableRectangle(3, 3, 2, 2), new Unit.State(20, 1)))
						.addUnit(
								new Unit(Type.BASE, Side.RED, new ImmutableRectangle(0, 3, 3, 3),
										new Unit.State(100, 0))),
				BATTLEFIELD.tick(
						new Move.UnitMove(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1),
								new Unit.State(10, 3)), Direction.DOWN), Side.BLUE).getBattlefield());
	}

	@Test
	public void testInvalidMove() {
		assertEquals(
				BATTLEFIELD_NO_MOVE_RED,
				BATTLEFIELD.tick(
						new Move.UnitMove(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1),
								new Unit.State(10, 3)), Direction.LEFT), Side.RED).getBattlefield());
		assertEquals(
				Move.NO_OP,
				BATTLEFIELD.tick(
						new Move.UnitMove(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1),
								new Unit.State(10, 3)), Direction.LEFT), Side.RED).getActualMove());
		assertEquals(
				BATTLEFIELD_NO_MOVE_RED,
				BATTLEFIELD.tick(
						new Move.UnitShoot(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1),
								new Unit.State(10, 3)), 4, 1), Side.RED).getBattlefield());
		assertEquals(
				Move.NO_OP,
				BATTLEFIELD.tick(
						new Move.UnitShoot(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1),
								new Unit.State(10, 3)), 4, 1), Side.RED).getActualMove());
	}

	@Test
	public void testSoldierShooting() {
		// out of range
		assertEquals(
				BATTLEFIELD_SHOOT,
				BATTLEFIELD_SHOOT.tick(
						new Move.UnitShoot(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1),
								new Unit.State(10, 0)), 5, 3), Side.BLUE).getBattlefield());
		assertEquals(
				Move.NO_OP,
				BATTLEFIELD_SHOOT.tick(
						new Move.UnitShoot(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1),
								new Unit.State(10, 0)), 5, 3), Side.BLUE).getActualMove());
		// out of turn
		assertEquals(
				BATTLEFIELD_SHOOT,
				BATTLEFIELD_SHOOT.tick(
						new Move.UnitShoot(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1),
								new Unit.State(10, 0)), 5, 3), Side.RED).getBattlefield());
		assertEquals(
				Move.NO_OP,
				BATTLEFIELD_SHOOT.tick(
						new Move.UnitShoot(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1),
								new Unit.State(10, 0)), 5, 3), Side.RED).getActualMove());

		// ok
		assertEquals(
				new Battlefield(6, 6)
						.addUnit(
								new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1), new Unit.State(
										10, 3)))
						.addUnit(
								new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2),
										new Unit.State(17, 0)))
						.addUnit(
								new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(0, 0, 3, 3), new Unit.State(100,
										0)))
						.addUnit(
								new Unit(Type.SOLDIER, Side.RED, new ImmutableRectangle(5, 3, 1, 1), new Unit.State(10,
										0)))
						.addUnit(
								new Unit(Type.TANK, Side.RED, new ImmutableRectangle(3, 3, 2, 2), new Unit.State(20, 0)))
						.addUnit(
								new Unit(Type.BASE, Side.RED, new ImmutableRectangle(0, 3, 3, 3),
										new Unit.State(100, 0))),
				BATTLEFIELD_SHOOT.tick(
						new Move.UnitShoot(new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1),
								new Unit.State(10, 0)), 4, 0), Side.BLUE).getBattlefield());
	}

	@Test
	public void testTankShooting() {
		// out of range
		assertEquals(
				BATTLEFIELD_SHOOT,
				BATTLEFIELD_SHOOT.tick(
						new Move.UnitShoot(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2),
								new Unit.State(20, 0)), 0, 3), Side.BLUE).getBattlefield());
		// ok
		assertEquals(
				new Battlefield(6, 6)
						.addUnit(
								new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1), new Unit.State(
										10, 0)))
						.addUnit(
								new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2),
										new Unit.State(20, 8)))
						.addUnit(
								new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(0, 0, 3, 3), new Unit.State(100,
										0)))
						.addUnit(
								new Unit(Type.SOLDIER, Side.RED, new ImmutableRectangle(5, 3, 1, 1), new Unit.State(10,
										0)))
						.addUnit(
								new Unit(Type.TANK, Side.RED, new ImmutableRectangle(3, 3, 2, 2), new Unit.State(5, 0)))
						.addUnit(
								new Unit(Type.BASE, Side.RED, new ImmutableRectangle(0, 3, 3, 3), new Unit.State(95, 0))),
				BATTLEFIELD_SHOOT.tick(
						new Move.UnitShoot(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2),
								new Unit.State(20, 0)), 3, 4), Side.BLUE).getBattlefield());
		assertEquals(
				new Battlefield(6, 6)
						.addUnit(
								new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1), new Unit.State(5,
										0)))
						.addUnit(
								new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2),
										new Unit.State(15, 8)))
						.addUnit(
								new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(0, 0, 3, 3), new Unit.State(100,
										0)))
						.addUnit(
								new Unit(Type.SOLDIER, Side.RED, new ImmutableRectangle(5, 3, 1, 1), new Unit.State(10,
										0)))
						.addUnit(
								new Unit(Type.TANK, Side.RED, new ImmutableRectangle(3, 3, 2, 2), new Unit.State(20, 0)))
						.addUnit(
								new Unit(Type.BASE, Side.RED, new ImmutableRectangle(0, 3, 3, 3),
										new Unit.State(100, 0))),
				BATTLEFIELD_SHOOT.tick(
						new Move.UnitShoot(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2),
								new Unit.State(20, 0)), 5, 1), Side.BLUE).getBattlefield());
		assertEquals(
				new Battlefield(6, 6)
						.addUnit(
								new Unit(Type.SOLDIER, Side.BLUE, new ImmutableRectangle(5, 0, 1, 1), new Unit.State(
										10, 0)))
						.addUnit(
								new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2),
										new Unit.State(20, 8)))
						.addUnit(
								new Unit(Type.BASE, Side.BLUE, new ImmutableRectangle(0, 0, 3, 3), new Unit.State(100,
										0)))
						.addUnit(
								new Unit(Type.SOLDIER, Side.RED, new ImmutableRectangle(5, 3, 1, 1), new Unit.State(5,
										0)))
						.addUnit(
								new Unit(Type.TANK, Side.RED, new ImmutableRectangle(3, 3, 2, 2), new Unit.State(5, 0)))
						.addUnit(
								new Unit(Type.BASE, Side.RED, new ImmutableRectangle(0, 3, 3, 3),
										new Unit.State(100, 0))),
				BATTLEFIELD_SHOOT.tick(
						new Move.UnitShoot(new Unit(Type.TANK, Side.BLUE, new ImmutableRectangle(3, 0, 2, 2),
								new Unit.State(20, 0)), 4, 3), Side.BLUE).getBattlefield());
	}

	@Test
	public void testToJson() {
		assertEquals(new JsonParser().parse(BATTLEFIELD_JSON), BATTLEFIELD.toJson());
	}

	@Test
	public void testFromJson() {
		assertEquals(BATTLEFIELD, Battlefield.fromJson(new JsonParser().parse(BATTLEFIELD_JSON).getAsJsonObject()));
	}

}
