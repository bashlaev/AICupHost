package com.devoler.aicup.host.run;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.devoler.aicup.host.model.Battlefield;
import com.devoler.aicup.host.model.Move;
import com.devoler.aicup.host.model.Move.Direction;
import com.devoler.aicup.host.model.Side;
import com.devoler.aicup.host.model.Unit;
import com.devoler.aicup.host.model.Unit.Shot;
import com.devoler.aicup.host.model.Unit.Type;
import com.devoler.aicup.host.model.util.ImmutableRectangle;

public class BattlefieldView extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int CELL_SIZE = 30;
	private static final int BORDER_SIZE = 1;
	private static final int EXT_BORDER_SIZE = 3;

	private static final int FLAG_OFFSET_X = 35;
	private static final int FLAG_OFFSET_Y = 16;

	private static final int SOLDIER_FIRE_OFFSET_X = 9;
	private static final int SOLDIER_FIRE_OFFSET_Y = 18;

	private static final int MOVE_ANIM_TICKS = 5;
	private static final int SHOOT_ANIM_TICKS = 5;

	private class Animation {
		private final int ticks;
		private final Move move;
		private final Battlefield nextBattlefield;
		private int currentTick = 0;

		public Animation(final int ticks, final Move move, final Battlefield nextBattlefield) {
			if (ticks <= 0) {
				throw new RuntimeException("Invalid ticks: " + ticks);
			}
			if (move == null || nextBattlefield == null) {
				throw new NullPointerException();
			}
			this.ticks = ticks;
			this.move = move;
			this.nextBattlefield = nextBattlefield;
		}

		public int getTicks() {
			return ticks;
		}

		public int getCurrentTick() {
			return currentTick;
		}

		public void tick() {
			currentTick++;
			if (currentTick == ticks) {
				battlefield = nextBattlefield;
				animation = null;
			}
		}

		public Move getMove() {
			return move;
		}
	}

	private final Image cellImage = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/30x30.png"));
	private final Image baseImage = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/base.png"));
	private final Image redFlag = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/flag_red.png"));
	private final Image blueFlag = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/flag_blue.png"));
	private final Image redTank = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/tank_red_shot_.png"));
	private final Image blueTank = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/tank_blue_shot_.png"));
	private final Image redSoldier = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/soldier_red_shot.png"));
	private final Image blueSoldier = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/soldier_blue_shot.png"));
	private final Image explosion = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/explosion.png"));
	private final Image redSoldierFire = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/soldier_red_fire.png"));
	private final Image blueSoldierFire = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/soldier_blue_fire.png"));
	private final Image redTankFire = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/tank_red_fire.png"));
	private final Image blueTankFire = Toolkit.getDefaultToolkit().createImage(
			BattlefieldView.class.getResource("/img/tank_blue_fire.png"));

	private Animation animation;
	private Battlefield battlefield;

	public BattlefieldView(final Battlefield battlefield) {
		this.battlefield = battlefield;
		setBorder(BorderFactory.createLineBorder(Color.black, EXT_BORDER_SIZE));
		MediaTracker mt = new MediaTracker(this);
		int id = 0;
		mt.addImage(cellImage, id++);
		mt.addImage(baseImage, id++);
		mt.addImage(redFlag, id++);
		mt.addImage(blueFlag, id++);
		mt.addImage(redTank, id++);
		mt.addImage(blueTank, id++);
		mt.addImage(redSoldier, id++);
		mt.addImage(blueSoldier, id++);
		mt.addImage(explosion, id++);
		mt.addImage(redSoldierFire, id++);
		mt.addImage(blueSoldierFire, id++);
		mt.addImage(redTankFire, id++);
		mt.addImage(blueTankFire, id++);
		try {
			mt.waitForAll();
		} catch (InterruptedException e) {
			throw new RuntimeException("Could not load resources");
		}
	}

	@Override
	public synchronized Dimension getMinimumSize() {
		return new Dimension(2 * EXT_BORDER_SIZE + battlefield.getWidth() * (CELL_SIZE + BORDER_SIZE) - 1, 2
				* EXT_BORDER_SIZE + battlefield.getHeight() * (CELL_SIZE + BORDER_SIZE) - 1);
	}

	public synchronized boolean isAnimating() {
		return animation != null;
	}

	public synchronized void tick() {
		if (animation != null) {
			animation.tick();
		}
	}

	public synchronized void update(Move move, Battlefield nextBattlefield) {
		if (animation != null) {
			throw new RuntimeException();
		}
		if (move instanceof Move.UnitMove) {
			// move animation
			animation = new Animation(MOVE_ANIM_TICKS, move, nextBattlefield);
		} else if (move instanceof Move.UnitShoot) {
			// shoot animation
			animation = new Animation(SHOOT_ANIM_TICKS, move, nextBattlefield);
		} else {
			battlefield = nextBattlefield;
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return getMinimumSize();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		synchronized (this) {
			// paint white bg
			g.setColor(new Color(0x304218));
			g.fillRect(0, 0, getWidth(), getHeight());

			// paint cells
			for (int row = 0; row < battlefield.getHeight(); row++) {
				for (int col = 0; col < battlefield.getWidth(); col++) {
					g.drawImage(cellImage, cellToFieldX(col), cellToFieldY(row), null);
				}
			}

			Unit animatedUnit = null;
			if (animation != null) {
				if (animation.getMove() instanceof Move.UnitMove) {
					animatedUnit = ((Move.UnitMove) animation.getMove()).getUnit();
				}
				if (animation.getMove() instanceof Move.UnitShoot) {
					animatedUnit = ((Move.UnitShoot) animation.getMove()).getUnit();
				}
			}

			for (Unit unit : battlefield.getUnits()) {
				if (!unit.equals(animatedUnit)) {
					paintUnit(unit, g, cellToFieldX(unit.getBounds().getX()), cellToFieldY(unit.getBounds().getY()), 2);
				}
			}

			// paint animation
			if (animation != null) {
				if (animation.getMove() instanceof Move.UnitMove) {
					Move.UnitMove unitMove = ((Move.UnitMove) animation.getMove());
					Direction direction = unitMove.getDirection();
					ImmutableRectangle currentPosition = unitMove.getUnit().getBounds();
					ImmutableRectangle nextPosition = unitMove.getUnit().move(direction).getBounds();
					int currentX = cellToFieldX(currentPosition.getX());
					int currentY = cellToFieldY(currentPosition.getY());
					int nextX = cellToFieldX(nextPosition.getX());
					int nextY = cellToFieldY(nextPosition.getY());
					int animX = currentX + (nextX - currentX) * (animation.getCurrentTick() + 1) / animation.getTicks();
					int animY = currentY + (nextY - currentY) * (animation.getCurrentTick() + 1) / animation.getTicks();
					paintUnit(unitMove.getUnit(), g, animX, animY,
							unitMove.getUnit().getType() == Type.TANK ? 2 + (animation.getCurrentTick() % 2)
									: animation.getCurrentTick() % 5);
				}
				if (animation.getMove() instanceof Move.UnitShoot) {
					Move.UnitShoot unitShoot = ((Move.UnitShoot) animation.getMove());
					paintUnit(unitShoot.getUnit(), g, cellToFieldX(unitShoot.getUnit().getBounds().getX()),
							cellToFieldY(unitShoot.getUnit().getBounds().getY()),
							unitShoot.getUnit().getType() == Type.SOLDIER ? 2 : 0);

					int cellW = cellToFieldWidth(1);
					int cellH = cellToFieldHeight(1);

					// paint fire
					if (unitShoot.getUnit().getType() == Type.TANK) {
						int fireX = cellToFieldX(unitShoot.getUnit().getBounds().getX()) + cellW / 2;
						int fireY = cellToFieldY(unitShoot.getUnit().getBounds().getY())
								+ (unitShoot.getUnit().getSide() == Side.BLUE ? (-1 * cellH / 2) : (3 * cellH / 2));
						g.setClip(fireX, fireY, cellW, cellH);
						g.drawImage(unitShoot.getUnit().getSide() == Side.BLUE ? blueTankFire : redTankFire, fireX
								- Math.min(2, animation.getCurrentTick()) * cellW, fireY, null);
					} else if (unitShoot.getUnit().getType() == Type.SOLDIER) {
						int fireX = cellToFieldX(unitShoot.getUnit().getBounds().getX()) + SOLDIER_FIRE_OFFSET_X;
						int fireY = cellToFieldY(unitShoot.getUnit().getBounds().getY())
								+ (unitShoot.getUnit().getSide() == Side.BLUE ? -SOLDIER_FIRE_OFFSET_Y
										: SOLDIER_FIRE_OFFSET_Y);
						g.setClip(fireX, fireY, cellW, cellH);
						g.drawImage(unitShoot.getUnit().getSide() == Side.BLUE ? blueSoldierFire : redSoldierFire,
								fireX - Math.min(2, animation.getCurrentTick()) * cellW, fireY, null);
					}

					// int unitCoreX = cellToFieldX(unitShoot.getUnit().getBounds().getX())
					// + cellToFieldWidth(unitShoot.getUnit().getBounds().getWidth()) / 2;
					// int unitCoreY = cellToFieldY(unitShoot.getUnit().getBounds().getY())
					// + cellToFieldHeight(unitShoot.getUnit().getBounds().getHeight()) / 2;
					// int targetCoreX = cellToFieldX(unitShoot.getX()) + cellToFieldWidth(1) / 2;
					// int targetCoreY = cellToFieldY(unitShoot.getY()) + cellToFieldHeight(1) / 2;
					// int animX = unitCoreX + (targetCoreX - unitCoreX) * (animation.getCurrentTick() + 1)
					// / animation.getTicks();
					// int animY = unitCoreY + (targetCoreY - unitCoreY) * (animation.getCurrentTick() + 1)
					// / animation.getTicks();
					// g.setColor(Color.black);
					// ((Graphics2D) g).setStroke(new BasicStroke(3.0f));
					// g.drawLine(unitCoreX, unitCoreY, animX, animY);
					// ((Graphics2D) g).setStroke(new BasicStroke());

					// paint explosion on all targets
					if (animation.getCurrentTick() >= 2) {
						ImmutableRectangle target = new ImmutableRectangle(unitShoot.getX(), unitShoot.getY(), 1, 1);
						Shot shot = unitShoot.getUnit().getShot();
						for (int col = unitShoot.getX() - shot.getCollateral(); col <= unitShoot.getX()
								+ shot.getCollateral(); col++) {
							for (int row = unitShoot.getY() - shot.getCollateral(); row <= unitShoot.getY()
									+ shot.getCollateral(); row++) {
								// cross-like area
								if (target.getManhattanDistance(col, row) > shot.getCollateral()) {
									continue;
								}
								int explosionX = cellToFieldX(col);
								int explosionY = cellToFieldY(row);
								g.setClip(explosionX, explosionY, cellW, cellH);
								g.drawImage(explosion, explosionX - (animation.getCurrentTick() - 2) * cellW,
										explosionY, null);
							}
						}
					}

					g.setClip(0, 0, getWidth(), getHeight());
				}
			}
		}
	}

	private static int cellToFieldX(int x) {
		return EXT_BORDER_SIZE + x * (CELL_SIZE + BORDER_SIZE);
	}

	private static int cellToFieldY(int y) {
		return EXT_BORDER_SIZE + y * (CELL_SIZE + BORDER_SIZE);
	}

	private static int cellToFieldWidth(int width) {
		return width * (CELL_SIZE + BORDER_SIZE) - BORDER_SIZE;
	}

	private static int cellToFieldHeight(int height) {
		return height * (CELL_SIZE + BORDER_SIZE) - BORDER_SIZE;
	}

	private void paintUnit(Unit unit, Graphics g, int x, int y, int frame) {
		if (unit.getType() == Type.BASE) {
			paintBase(unit, g, x, y);
		} else if (unit.getType() == Type.TANK) {
			paintTank(unit, g, x, y, frame);
		} else if (unit.getType() == Type.SOLDIER) {
			paintSoldier(unit, g, x, y, frame);
		}
		paintHitPoints(unit, g, x, y);
	}

	private void paintHitPoints(Unit unit, Graphics g, int x, int y) {
		int w = cellToFieldWidth(unit.getType().getSize());
		g.setColor(Color.black);
		g.fill3DRect(x + 2, y + 2, w - 4, 4, true);
		g.setColor(Color.red);
		g.fill3DRect(x + 3, y + 3, w - 6, 2, true);
		g.setColor(Color.green);
		int greenWidth = unit.getState().getHitPoints() * (w - 6) / unit.getType().getHitPoints();
		g.fill3DRect(x + 3, y + 3, greenWidth, 2, true);
	}

	private void paintTank(Unit tank, Graphics g, int x, int y, int frame) {
		int w = cellToFieldWidth(tank.getType().getSize());
		int h = cellToFieldHeight(tank.getType().getSize());
		g.setClip(x, y, w, h);
		g.drawImage(tank.getSide() == Side.BLUE ? blueTank : redTank, x - w * frame, y, null);
		g.setClip(0, 0, getWidth(), getHeight());
	}

	private void paintSoldier(Unit soldier, Graphics g, int x, int y, int frame) {
		int w = cellToFieldWidth(soldier.getType().getSize());
		int h = cellToFieldHeight(soldier.getType().getSize());
		g.setClip(x, y, w, h);
		g.drawImage(soldier.getSide() == Side.BLUE ? blueSoldier : redSoldier, x - w * frame, y, null);
		g.setClip(0, 0, getWidth(), getHeight());
	}

	private void paintBase(Unit base, Graphics g, int x, int y) {
		g.drawImage(baseImage, x, y, null);
		g.drawImage(base.getSide() == Side.BLUE ? blueFlag : redFlag, x + FLAG_OFFSET_X, y + FLAG_OFFSET_Y, null);
	}
}
