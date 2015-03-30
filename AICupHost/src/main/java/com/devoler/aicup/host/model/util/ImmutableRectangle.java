package com.devoler.aicup.host.model.util;


/**
 * An immutable rectangle.
 * 
 * @author homer
 * 
 */
public class ImmutableRectangle {
	private final int x;
	private final int y;
	private final int width;
	private final int height;

	public ImmutableRectangle(final int x, final int y, final int width, final int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ImmutableRectangle moveLeft() {
		return new ImmutableRectangle(x - 1, y, width, height);
	}

	public ImmutableRectangle moveRight() {
		return new ImmutableRectangle(x + 1, y, width, height);
	}

	public ImmutableRectangle moveUp() {
		return new ImmutableRectangle(x, y - 1, width, height);
	}

	public ImmutableRectangle moveDown() {
		return new ImmutableRectangle(x, y + 1, width, height);
	}

	public boolean contains(ImmutableRectangle r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public int getManhattanDistance(int X, int Y) {
		if (contains(X, Y)) {
			return 0;
		}
		int dx = Math.min(Math.abs(x - X), Math.abs(x + width - 1 - X));
		int dy = Math.min(Math.abs(y - Y), Math.abs(y + height - 1 - Y));
		return dx + dy;
	}

	public boolean contains(int X, int Y, int W, int H) {
		int w = this.width;
		int h = this.height;
		if ((w | h | W | H) < 0) {
			// At least one of the dimensions is negative...
			return false;
		}
		// Note: if any dimension is zero, tests below must return false...
		int x = this.x;
		int y = this.y;
		if (X < x || Y < y) {
			return false;
		}
		w += x;
		W += X;
		if (W <= X) {
			// X+W overflowed or W was zero, return false if...
			// either original w or W was zero or
			// x+w did not overflow or
			// the overflowed x+w is smaller than the overflowed X+W
			if (w >= x || W > w)
				return false;
		} else {
			// X+W did not overflow and W was not zero, return false if...
			// original w was zero or
			// x+w did not overflow and x+w is smaller than X+W
			if (w >= x && W > w)
				return false;
		}
		h += y;
		H += Y;
		if (H <= Y) {
			if (h >= y || H > h)
				return false;
		} else {
			if (h >= y && H > h)
				return false;
		}
		return true;
	}

	public boolean intersects(ImmutableRectangle r) {
		int tw = this.width;
		int th = this.height;
		int rw = r.width;
		int rh = r.height;
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}
		int tx = this.x;
		int ty = this.y;
		int rx = r.x;
		int ry = r.y;
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		// overflow || intersect
		return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
	}

	public boolean contains(int X, int Y) {
		int w = this.width;
		int h = this.height;
		if ((w | h) < 0) {
			// At least one of the dimensions is negative...
			return false;
		}
		// Note: if either dimension is zero, tests below must return false...
		int x = this.x;
		int y = this.y;
		if (X < x || Y < y) {
			return false;
		}
		w += x;
		h += y;
		// overflow || intersect
		return ((w < x || w > X) && (h < y || h > Y));
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ImmutableRectangle))
			return false;

		ImmutableRectangle that = (ImmutableRectangle) o;
		return (this.x == that.x) && (this.y == that.y) && (this.width == that.width) && (this.height == that.height);
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + x;
		hashCode = 31 * hashCode + y;
		hashCode = 31 * hashCode + width;
		hashCode = 31 * hashCode + height;
		return hashCode;
	}

}
