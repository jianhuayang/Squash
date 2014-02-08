package ch.squash.simulation.shapes.common;

public interface IVector {
	float getX();

	float getY();

	float getZ();
	
	float[] getDirection();

	IVector multiply(final float factor);
	
	float multiply(final IVector other);

	float getAngle(final IVector other);

	IVector add(final IVector other);
	
	float getLength();
	
	void setDirection(final float x, final float y, final float z);
	
	void setDirection(final IVector other);
	
	IVector getNormalizedVector();
	
	IVector getCrossProduct(IVector other);
}
