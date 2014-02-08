package ch.squash.simulation.shapes.common;

import android.util.Log;

public class Vector implements IVector {
	private final static String TAG = Vector.class.getSimpleName();

	private final static int DIMENSION = 3;
	private final float[] mDirection;

	public float getX() {
		return mDirection[0];
	}

	public float getY() {
		return mDirection[1];
	}

	public float getZ() {
		return mDirection[2];
	}

	public float multiply(final IVector other) {
		float result = 0;
		for (int i = 0; i < DIMENSION; i++)
			result += mDirection[i] * ((Vector) other).mDirection[i];

		return result;
	}

	public float getAngle(final IVector other) {
		if (getLength() * other.getLength() == 0)
			Log.e(TAG, "Dividing by zero while calculating angle between "
					+ this + " and " + other);

		return (float) Math.acos(multiply(other) / other.getLength()
				/ getLength());
	}

	public float getLength() {
		float result = 0;

		for (int i = 0; i < DIMENSION; i++)
			result += Math.pow(mDirection[i], 2);

		return (float) Math.sqrt(result);
	}

	public Vector() {
		mDirection = new float[DIMENSION];
	}

	public Vector(final float x, final float y, final float z) {
		mDirection = new float[] { x, y, z };
	}

	@Override
	public String toString() {
		return "(" + mDirection[0] + "/" + mDirection[1] + "/" + mDirection[2]
				+ ")";
	}

	public Vector(final float[] direction) {
		mDirection = direction.length == DIMENSION ? direction : null;

		if (direction.length != DIMENSION)
			Log.e(TAG, "INVALID DIMENSIONS!!");
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
	public IVector add(final IVector other) {
		if (other instanceof PhysicalVector)
			return add(((PhysicalVector) other).getVector());

		final Vector result = new Vector();

		for (int i = 0; i < DIMENSION; i++)
			result.mDirection[i] = mDirection[i]
					+ ((Vector) other).mDirection[i];

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
	public IVector getNormalizedVector() {
		return getLength() == 0 ? null : multiply(1 / getLength());
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Vector))
			return false;

		final Vector v = (Vector) o;

		return AbstractShape.areEqual(v.getX(), getX()) && 
				AbstractShape.areEqual(v.getY(), getY()) &&
				AbstractShape.areEqual(v.getZ(), getZ());
	}

	@Override
	public int hashCode() {
		return (int) (Double.doubleToRawLongBits(getX()) ^ (Double
				.doubleToRawLongBits(getY()) ^ Double
				.doubleToRawLongBits(getZ())));
	}
	
	@Override
	public IVector getCrossProduct(final IVector other){
		return new Vector(getY() * other.getZ() - getZ() * other.getY(), getZ()
				* other.getX() - getX() * other.getZ(), getX() * other.getY()
				- getY() * other.getX());
	}
	
	@Override
	public void setDirection(final IVector other){
		setDirection(other.getX(), other.getY(), other.getZ());
	}
}
