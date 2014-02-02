package ch.squash.simulation.shapes.common;

import ch.squash.simulation.shapes.shapes.Arrow;

public class PhysicalVector implements IVector {
	public final static float ARROW_LENGTH_DIVISOR = 10;

	private final Arrow mArrow;
	private final IVector mVector;
	
	public final String VectorTag;

	public IVector getVector(){
		return mVector;
	}

	private void setNewCoordinates() {
		 mArrow.setNewVertices(Arrow.getVertices(0, 0, 0, mVector.getX()
		 / ARROW_LENGTH_DIVISOR, mVector.getY() / ARROW_LENGTH_DIVISOR,
		 mVector.getZ() / ARROW_LENGTH_DIVISOR));

//		mArrow.setNewVertices(Arrow.getVertices(mArrow.location[0],
//				mArrow.location[1], mArrow.location[2],
//				(mArrow.location[0] + mVector.getX()) / ARROW_LENGTH_DIVISOR,
//				(mArrow.location[0] + mVector.getY()) / ARROW_LENGTH_DIVISOR,
//				(mArrow.location[0] + mVector.getZ()) / ARROW_LENGTH_DIVISOR));
	}

	@Override
	public String toString() {
		return mVector.toString();
	}

	public PhysicalVector(final String tag, final float[] origin, final float[] direction,
			final float[] color) {
		mArrow = new Arrow(tag, origin[0], origin[1], origin[2], origin[0]
				+ direction[0] / ARROW_LENGTH_DIVISOR, origin[1] + direction[1]
				/ ARROW_LENGTH_DIVISOR, origin[2] + direction[2]
				/ ARROW_LENGTH_DIVISOR, color);
		mVector = new Vector(direction[0], direction[1], direction[2]);

		VectorTag = tag;
	}

	public void moveTo(final IVector fs) {
		mArrow.moveTo(fs);
	}

	public void draw() {
		mArrow.draw();
	}

	public void move(final IVector dv) {
		mArrow.move(dv);
	}

	@Override
	public float getX() {
		return mVector.getX();
	}

	@Override
	public float getY() {
		return mVector.getY();
	}

	@Override
	public float getZ() {
		return mVector.getZ();
	}

	@Override
	public float multiply(final IVector other) {
		return mVector.multiply(other);
	}

	@Override
	public float getAngle(final IVector other) {
		return mVector.getAngle(other);
	}

	@Override
	public float getLength() {
		return mVector.getLength();
	}

	@Override
	public float[] getDirection() {
		return mVector.getDirection();
	}

	@Override
	public void setDirection(final float x, final float y, final float z) {
		mVector.setDirection(x, y, z);
		setNewCoordinates();
	}

	@Override
	public IVector add(final IVector other) {
		return mVector.add(other);
	}

	@Override
	public IVector multiply(final float factor) {
		return mVector.multiply(factor);
	}

	@Override
	public IVector getNormalizedVector() {
		return mVector.getNormalizedVector();
	}
}
