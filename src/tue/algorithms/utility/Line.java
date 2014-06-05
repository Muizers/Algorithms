package tue.algorithms.utility;

import static java.lang.Math.abs;

import java.awt.geom.Line2D;

/**
 * <p>
 * Utility class that represents a line from one point to another point.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 * @author Martijn
 */
public class Line {
	/**
	 * The width of the line. This is used to determine whether the line intersects another line or point.
	 * TODO: Evaluate whether this arbitrarily chosen value makes sense.
	 */
	final static float LINE_WIDTH = 0.0001f;
	
	/* -- START Private final fields -- */
	
	/**
	 * The x-coordinate of the point the line starts at.
	 */
	protected final float x1;
	/**
	 * The y-coordinate of the point the line starts at.
	 */
	protected final float y1;
	/**
	 * The x-coordinate of the point the line ends at.
	 */
	protected final float x2;
	/**
	 * The y-coordinate of the point the line ends at.
	 */
	protected final float y2;

	/**
	 * Normalized coordinates of the line.
	 * If the line is vertical, then *Left is the top-most point.
	 */
	protected final float xLeft;
	protected final float yLeft;
	protected final float xRight;
	protected final float yRight;
	
	/* -- END Private final fields -- */
	
	/* -- START Constructors -- */
	
	/**
	 * Create a line from the point (x1, y1) to the point (x2, y2).
	 * @param x1 The x-coordinate of the point the line starts at.
	 * @param y1 The y-coordinate of the point the line starts at.
	 * @param x2 The x-coordinate of the point the line ends at.
	 * @param y2 The y-coordinate of the point the line ends at.
	 */
	public Line(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		if (x1 < x2 || (x1 == x2 && y1 < y2)) {
			this.xLeft = x1;
			this.yLeft = y1;
			this.xRight = x2;
			this.yRight = y2;
		} else {
			this.xLeft = x2;
			this.yLeft = y2;
			this.xRight = x1;
			this.yRight = y1;
		}
	}
	
	/**
	 * Create a line from point1 to point2.
	 * @param point1 The point the line starts at.
	 * @param point2 The point the line ends at.
	 */
	public Line(Point point1, Point point2) {
		this(point1.getX(), point1.getY(), point2.getX(), point2.getY());
	}
	
	/* -- END Constructors -- */
	
	/* -- START Public getters for private fields -- */
	
	/**
	 * Get the x-coordinate of the point the line starts at.
	 * @return The x-coordinate as an float.
	 */
	public float getX1() {
		return x1;
	}
	
	/**
	 * Get the y-coordinate of the point the line starts at.
	 * @return The y-coordinate as an float.
	 */
	public float getY1() {
		return y1;
	}
	
	/**
	 * Get the x-coordinate of the point the line ends at.
	 * @return The x-coordinate as an float.
	 */
	public float getX2() {
		return x2;
	}
	
	/**
	 * Get the y-coordinate of the point the line ends at.
	 * @return The y-coordinate as an float.
	 */
	public float getY2() {
		return y2;
	}

	/**
	 * @return The minimal X-coordinate of this line.
	 */
	public float getMinX() {
		return x1 < x2 ? x1 : x2;
	}

	/**
	 * @return The maximal X-coordinate of this line.
	 */
	public float getMaxX() {
		return x1 < x2 ? x2 : x1;
	}

	/**
	 * @return The minimal Y-coordinate of this line.
	 */
	public float getMinY() {
		return y1 < y2 ? y1 : y2;
	}

	/**
	 * @return The maximal Y-coordinate of this line.
	 */
	public float getMaxY() {
		return y1 < y2 ? y2 : y1;
	}
	
	/* -- END Public getters for private fields -- */
	
	/* -- START Getters for point representations of private fields -- */
	
	/**
	 * Get the point the line starts at.
	 * @return The point.
	 */
	public Point getPoint1() {
		return new Point(getX1(), getY1());
	}
	
	/**
	 * Get the point the line ends at.
	 * @return The point.
	 */
	public Point getPoint2() {
		return new Point(getX2(), getY2());
	}
	
	/**
	 * Get the vector of this line, which is the point (x2-x1, y2-y1).
	 * @return The vector as a point.
	 */
	public Point getVector() {
		return getPoint2().subtract(getPoint1());
	}
	
	/* -- END Getters for point representations of private fields -- */
	
	/* -- START Getters for useful information -- */
	
	/**
	 * Get the length of the line.
	 * @return The length as a float.
	 */
	public float length() {
		return getVector().length();
	}
        
        /**
	 * Get the manhattan distance of the line.
	 * @return The distance as a float.
	 */
	public float manhattanDistance() {
		return abs(this.x1-this.x2)+abs(this.y1-this.y2);
	}
	
	/**
	 * Get the angle of the line.
	 * An angle of 0 means this line points to the right, and an angle of Math.PI/2 means this line points upwards.
	 * @return The angle as a double.
	 */
	public double getAngle() {
		return getVector().getAngle();
	}

	/**
	 * Get the angle of {@code other} relative to this line, as if 
	 * this line were the positive X-axis of some coordinate system,
	 * with (x1, y1) as origin.
	 *
	 * @return The angle in radians in the range [-Math.PI, Math.PI]
	 */
	public double getAngleOf(Point other) {
		double otherAbsoluteAngle = other.subtract(getPoint1()).getAngle();
		double thisAbsoluteAngle = getAngle();
		double relativeAngle = otherAbsoluteAngle - thisAbsoluteAngle;
		if (relativeAngle <= -Math.PI) relativeAngle += 2 * Math.PI;
		else if (relativeAngle >= Math.PI) relativeAngle -= 2 * Math.PI;
		return relativeAngle;
	}
	
	/**
	 * Get the slope of the line.
	 * @return The slope as a float.
	 */
	public float getSlope() {
		return getSlope(x2 - x1, y2 - y1);
	}

	/**
	 * Get the slope of the line, relative to the left point.
	 * @return The slope as a float.
	 */
	public float getNormalizedSlope() {
		return getSlope(xRight - xLeft, yRight - yLeft);
	}

	private static float getSlope(float dx, float dy) {
        if (dx == 0) {
        	if (dy > 0) {
        		return Integer.MAX_VALUE;
        	}
        	if (dy < 0) {
        		return Integer.MIN_VALUE;
        	}
        	return 0;
        }
		return dy / dx;
	}
	
	/* -- END Getters for useful information -- */
	
	/* -- START Manipulation method to invert line -- */
	
	/**
	 * Get a line with the direction inverted: the created line will start where this line ends, and end where this line starts.
	 * @return The line with inverted direction.
	 */
	public Line invertDirection() {
		return new Line(getX2(), getY2(), getX1(), getY1());
	}
	
	/* -- END Manipulation method to invert line -- */
	
	/* -- START Manipulation methods for adding and subtracting points -- */
	
	/**
	 * Get a line that is translated by the vector of the given point.
	 * @param point The translation vector.
	 * @return The resulting line.
	 */
	public Line add(Point point) {
		return add(point.getX(), point.getY());
	}
	
	/**
	 * Get a line that is translated by the vector (x, y).
	 * @param x The x-coordinate of the translation.
	 * @param y The y-coordinate of the translation.
	 * @return The resulting line.
	 */
	public Line add(float x, float y) {
		return new Line(getX1() + x, getY1() + y, getX2() + x, getY2() + y);
	}
	
	/**
	 * Get a line that is inversely translated by the vector of the given point.
	 * @param point The translation vector to be inversely translated over.
	 * @return The resulting line.
	 */
	public Line subtract(Point point) {
		return subtract(point.getX(), point.getY());
	}
	
	/**
	 * Get a line that is inversely translated by the vector (x, y).
	 * @param x The x-coordinate of the translation vector to be inversely translated over.
	 * @param y The y-coordinate of the translation vector to be inversely translated over.
	 * @return The resulting line.
	 */
	public Line subtract(float x, float y) {
		return add(-x, -y);
	}
	
	/* -- END Manipulation methods for adding and subtracting points -- */
	
	/* -- START Method to check for intersection -- */
	
	public boolean intersectsWith(Line other) {
		Line2D line1 = new Line2D.Float(getX1(), getY1(), getX2(), getY2());
		Line2D line2 = new Line2D.Float(other.getX1(), other.getY1(), other.getX2(), other.getY2());
		if (line2.intersectsLine(line1)) {
			// Tests for touching lines
			if (xLeft == other.xLeft && yLeft == other.yLeft || xRight == other.xRight && yRight == other.yRight) {
				// Both lines start or end in the same point. If the slopes are equal, then they overlap.
				return getNormalizedSlope() == other.getNormalizedSlope();
			}
			if (xLeft == other.xRight && yLeft == other.yRight || xRight == other.xLeft && yRight == other.yLeft) {
				// One line ends in the start point of the other line. They will never intersect, because the
				// normalized coordinates already ensure that if they overlap, then the previous branch should
				// have been taken.
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * @return Whether a point lies somewhere on the line.
	 */
	public boolean intersectsWith(Point point) {
		float x = point.getX();
		float y = point.getY();
		float dx = xRight - xLeft;
		float dy = yRight - yLeft;

		if (Math.abs(dx) < LINE_WIDTH) {
			// Current line is vertical
			return y > getMinY() && y < getMaxY() && LINE_WIDTH > Math.abs(x - xLeft);
		} else if (Math.abs(dy) < LINE_WIDTH) {
			// Current line is horizontal
			return x > getMinX() && x < getMaxX() && LINE_WIDTH > Math.abs(y - yLeft);
		} else {
			// Neither horizontal nor vertical.
			return x > getMinX() && x < getMaxX() && LINE_WIDTH > (dy / dx) * (x - xLeft) - (y - yLeft);
		}
	}
	
	/* -- END Method to check for intersection -- */
	
	/* -- START Override equals(), hashCode() and toString() -- */
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Line) {
			Line other = (Line) obj;
			return (other.getX1() == getX1() &&
					other.getY1() == getY1() &&
					other.getX2() == getX2() &&
					other.getY2() == getY2());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) ((getX1() * 10000)
			+ (getY1() * 10000) * (getY1() * 10000)
			+ (getX2() * 10000) * (getX2() * 10000) * (getX2() * 10000)
			+ (getY2() * 10000) * (getY2() * 10000) * (getY2() * 10000) * (getY2() * 10000));
	}

	@Override
	public String toString() {
		return super.toString() + "["
			+ "x1=" + getX1() + ", "
			+ "y1=" + getY1() + ", "
			+ "x2=" + getX2() + ", "
			+ "y2=" + getY2()
			+ "]";
	}

	/* -- END Override equals(), hashCode() and toString() -- */

}
