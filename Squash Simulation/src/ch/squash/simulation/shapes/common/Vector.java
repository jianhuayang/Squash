package ch.squash.simulation.shapes.common;

import android.util.Log;

public class Vector implements IVector {
	// static
	private final static String TAG = Vector.class.getSimpleName();

	// constant
	private final static int DIMENSION = 3;
	
	// misc
	private final float[] mDirection;

	public Vector() {
		mDirection = new float[DIMENSION];
	}

	public Vector(final float x, final float y, final float z) {
		mDirection = new float[] { x, y, z };
	}

	public Vector(final float[] direction) {
		mDirection = direction.length == DIMENSION ? direction : null;

		if (direction.length != DIMENSION)
			Log.e(TAG, "INVALID DIMENSIONS!!");
	}

	@Override
	public String toString() {
		return "(" + mDirection[0] + "/" + mDirection[1] + "/" + mDirection[2]
				+ ")";
	}

	@Override
	public boolean equals(final Object o) {
		boolean equal = false;

		if (o instanceof Vector) {
			final Vector v = (Vector) o;

			equal = AbstractShape.areEqual(v.getX(), getX())
					&& AbstractShape.areEqual(v.getY(), getY())
					&& AbstractShape.areEqual(v.getZ(), getZ());
		}

		return equal;
	}

	@Override
	public int hashCode() {
		return (int) (Double.doubleToRawLongBits(getX()) ^ (Double
				.doubleToRawLongBits(getY()) ^ Double
				.doubleToRawLongBits(getZ())));
	}
	
	@Override
	public float getX() {
		return mDirection[0];
	}

	@Override
	public float getY() {
		return mDirection[1];
	}

	@Override
	public float getZ() {
		return mDirection[2];
	}

	@Override
	public void setX(final float x) {
		mDirection[0] = x;
	}

	@Override
	public void setY(final float y) {
		mDirection[1] = y;
	}

	@Override
	public void setZ(final float z) {
		mDirection[2] = z;
	}

	@Override
	public float[] getDirection() {
		return mDirection.clone();
	}

	@Override
	public void setDirection(final float x, final float y, final float z) {
		mDirection[0] = x;
		mDirection[1] = y;
		mDirection[2] = z;
	}

	@Override
	public void setDirection(final IVector other){
		setDirection(other.getX(), other.getY(), other.getZ());
	}
	
	@Override
	public IVector add(final IVector other) {
		Vector result = new Vector();
		
		if (other instanceof PhysicalVector)
			result = (Vector)add(((PhysicalVector) other).getVector());
		else{
			for (int i = 0; i < DIMENSION; i++)
				((Vector)result).mDirection[i] = mDirection[i]
						+ ((Vector) other).mDirection[i];
		}
		return result;
	}

	@Override
	public IVector multiply(final float factor) {
		final Vector result = new Vector();

		for (int i = 0; i < DIMENSION; i++)
			result.mDirection[i] = mDirection[i] * factor;

		return result;
	}

	@Override
	public float multiply(final IVector other) {
		float result = 0;
		for (int i = 0; i < DIMENSION; i++)
			result += mDirection[i] * other.getDirection()[i];

		return result;
	}

	@Override
	public float getAngle(final IVector other) {
		if (getLength() * other.getLength() == 0)
			Log.e(TAG, "Dividing by zero while calculating angle between "
					+ this + " and " + other);

		return (float) Math.acos(multiply(other) / other.getLength()
				/ getLength());
	}

	@Override
	public float getLength() {
		float result = 0;

		for (int i = 0; i < DIMENSION; i++)
			result += Math.pow(mDirection[i], 2);

		return (float) Math.sqrt(result);
	}


	@Override
	public IVector getNormalizedVector() {
		return getLength() == 0 ? this : multiply(1 / getLength());
	}
	
	@Override
	public IVector getCrossProduct(final IVector other){
		return new Vector(getY() * other.getZ() - getZ() * other.getY(), getZ()
				* other.getX() - getX() * other.getZ(), getX() * other.getY()
				- getY() * other.getX());
	}
}
