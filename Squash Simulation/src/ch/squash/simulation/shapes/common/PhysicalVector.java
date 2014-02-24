package ch.squash.simulation.shapes.common;

import ch.squash.simulation.shapes.shapes.Arrow;

public class PhysicalVector implements IVector {
	// Constant
	private final static float ARROW_LENGTH_DIVISOR = 10;

	// public
	public final String VectorTag;

	// private
	private final Arrow mArrow;
	private final IVector mVector;
	
	public PhysicalVector(final String tag, final float[] origin, final float[] direction,
			final float[] color) {
		mArrow = new Arrow(tag, origin[0], origin[1], origin[2], origin[0]
				+ direction[0] / ARROW_LENGTH_DIVISOR, origin[1] + direction[1]
				/ ARROW_LENGTH_DIVISOR, origin[2] + direction[2]
				/ ARROW_LENGTH_DIVISOR, color);
		mVector = new Vector(direction[0], direction[1], direction[2]);

		VectorTag = tag;
	}

	@Override
	public String toString() {
		return VectorTag + " " + mVector.toString();
	}

	public IVector getVector(){
		return mVector;
	}

	private void setNewCoordinates() {
		 mArrow.setNewVertices(Arrow.getVertices(0, 0, 0, mVector.getX()
		 / ARROW_LENGTH_DIVISOR, mVector.getY() / ARROW_LENGTH_DIVISOR,
		 mVector.getZ() / ARROW_LENGTH_DIVISOR));
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
	
	@Override
	public IVector getCrossProduct(final IVector other) {
		return mVector.getCrossProduct(other);
	}
	
	@Override
	public void setDirection(final IVector other){
		setDirection(other.getX(), other.getY(), other.getZ());
	}
}
