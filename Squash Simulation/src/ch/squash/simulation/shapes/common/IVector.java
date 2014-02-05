package ch.squash.simulation.shapes.common;

public interface IVector {
	float getX();

	float getY();

	float getZ();
	
	float[] getDirection();

	IVector multiply(float factor);
	
	float multiply(IVector other);

	float getAngle(IVector other);

	IVector add(IVector other);
	
	float getLength();
	
	void setDirection(float x, float y, float z);
	
	IVector getNormalizedVector();
	
	IVector getCrossProduct(IVector other);
}
