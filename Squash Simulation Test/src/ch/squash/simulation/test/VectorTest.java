package ch.squash.simulation.test;

import junit.framework.TestCase;
import ch.squash.simulation.shapes.common.IVector;
import ch.squash.simulation.shapes.common.Vector;

public class VectorTest extends TestCase {
	public void testGetAngle() {
		assertEquals(Float.NaN, v0.getAngle(v4));
		assertEquals((float)Math.PI/2, v1.getAngle(v2));
		assertEquals((float)Math.PI/4, v2.getAngle(v4));
		assertEquals(1.030376827f, v3.getAngle(v9));
		assertEquals(1.712772813f, v4.getAngle(v8));
		assertEquals(0f, v5.getAngle(v5));
		assertEquals(2.0088851f, v6.getAngle(v8));
		assertEquals(1.41419445f, v7.getAngle(v6));
		assertEquals(0.7156504f, v8.getAngle(v7));
		assertEquals(1.030376827f, v9.getAngle(v3));
	}

	public void testGetLength() {
		assertEquals(0f, v0.getLength());
		assertEquals(1f, v1.getLength());
		assertEquals(1f, v2.getLength());
		assertEquals(1f, v3.getLength());
		assertEquals((float)Math.sqrt(2), v4.getLength());
		assertEquals((float)Math.sqrt(41), v5.getLength());
		assertEquals((float)Math.sqrt(185), v6.getLength());
		assertEquals((float)Math.sqrt(18), v7.getLength());
		assertEquals((float)Math.sqrt(899), v8.getLength());
		assertEquals((float)Math.sqrt(34), v9.getLength());
	}
	
	public void testMultiplyIVector() {
		assertEquals(0f, v0.multiply(v4));
		assertEquals(0f, v1.multiply(v5));
		assertEquals(1f, v2.multiply(v4));
		assertEquals(3f, v3.multiply(v9));
		assertEquals(-6f, v4.multiply(v8));
		assertEquals(41f, v5.multiply(v5));
		assertEquals(-173f, v6.multiply(v8));
		assertEquals(9f, v7.multiply(v6));
		assertEquals(96f, v8.multiply(v7));
		assertEquals(3f, v9.multiply(v3));
	}

	public void testAdd() {
		assertEquals(new Vector(1, 0, 0), v0.add(v1));
		assertEquals(new Vector(18, -23, 9), v1.add(v8));
		assertEquals(new Vector(0, -2, 3), v2.add(v7));
		assertEquals(new Vector(0, 0, 2), v3.add(v3));
		assertEquals(new Vector(-4, 1, 3), v4.add(v9));
		assertEquals(new Vector(1, 4, -5), v5.add(v1));
		assertEquals(new Vector(-6, 7, 11), v6.add(v3));
		assertEquals(new Vector(0, -2, 3), v7.add(v2));
		assertEquals(new Vector(18, -22, 9), v8.add(v4));
		assertEquals(new Vector(-5, 4, -2), v9.add(v5));
	}
	
	public void testMultiplyFloat() {
		assertEquals(new Vector(0, 0, 0), v0.multiply(1));
		assertEquals(new Vector(0, 0, 0), v1.multiply(0));
		assertEquals(new Vector(0, -2, 0), v2.multiply(-2));
		assertEquals(new Vector(0, 0, 2), v3.multiply(2));
		assertEquals(new Vector(5, 5, 0), v4.multiply(5));
		assertEquals(new Vector(0, -12, 15), v5.multiply(-3));
		assertEquals(new Vector(-6, 7, 10), v6.multiply(1));
		assertEquals(new Vector(0, 3, -3), v7.multiply(-1));
		assertEquals(new Vector(42.5f, -57.5f, 22.5f), v8.multiply(2.5f));
		assertEquals(new Vector(7.5f, 0, -4.5f), v9.multiply(-1.5f));
	}

	public void testGetNormalizedVector() {
		assertEquals(null, v0.getNormalizedVector());
		assertEquals(new Vector(1, 0, 0), v1.getNormalizedVector());
		assertEquals(new Vector(0, 1, 0), v2.getNormalizedVector());
		assertEquals(new Vector(0, 0, 1), v3.getNormalizedVector());
		assertEquals(new Vector(0.7071067812f, 0.7071067812f, 0), v4.getNormalizedVector());
		assertEquals(new Vector(0, 0.6246950476f, -0.7808688094f), v5.getNormalizedVector());
		assertEquals(new Vector(-0.44112873f, 0.5146502f, 0.7352146f), v6.getNormalizedVector());
		assertEquals(new Vector(0, -0.7071068f, 0.7071068f), v7.getNormalizedVector());
		assertEquals(new Vector(0.5669818f, -0.76709294f, 0.30016682f), v8.getNormalizedVector());
		assertEquals(new Vector(-0.857493f, 0, 0.5144958f), v9.getNormalizedVector());
	}

	public void testGetCrossProduct() {
		assertEquals(new Vector(0, 0, 0), v0.getCrossProduct(v0));
		assertEquals(new Vector(0, 0, 0), v0.getCrossProduct(v1));
		assertEquals(new Vector(0, 0, 0), v0.getCrossProduct(v3));
		assertEquals(new Vector(0, 0, 0), v0.getCrossProduct(v5));
		assertEquals(new Vector(0, 0, 0), v0.getCrossProduct(v7));
		assertEquals(new Vector(0, 0, 0), v0.getCrossProduct(v9));
		assertEquals(new Vector(0, 0, 0), v1.getCrossProduct(v1));
		assertEquals(new Vector(0, 0, 0), v1.getCrossProduct(v0));
		assertEquals(new Vector(0, 0, 1), v1.getCrossProduct(v2));
		assertEquals(new Vector(0, 0, 1), v1.getCrossProduct(v4));
		assertEquals(new Vector(0, -10, 7), v1.getCrossProduct(v6));
		assertEquals(new Vector(0, -9, -23), v1.getCrossProduct(v8));
		assertEquals(new Vector(0, 0, 0), v4.getCrossProduct(v4));
		assertEquals(new Vector(0, 0, 1), v4.getCrossProduct(v1));
		assertEquals(new Vector(1, -1, 0), v4.getCrossProduct(v3));
		assertEquals(new Vector(-5, 5, 4), v4.getCrossProduct(v5));
		assertEquals(new Vector(3, -3, -3), v4.getCrossProduct(v7));
		assertEquals(new Vector(3, -3, 5), v4.getCrossProduct(v9));
		assertEquals(new Vector(0, 0, 0), v5.getCrossProduct(v5));
		assertEquals(new Vector(0, 0, 0), v5.getCrossProduct(v0));
		assertEquals(new Vector(0, 0, 0), v5.getCrossProduct(v2));
		assertEquals(new Vector(5, -5, 4), v5.getCrossProduct(v4));
		assertEquals(new Vector(75, 30, 24), v5.getCrossProduct(v6));
		assertEquals(new Vector(-79, -85, -68), v5.getCrossProduct(v8));
		assertEquals(new Vector(0, 0, 0), v8.getCrossProduct(v8));
		assertEquals(new Vector(0, 9, 23), v8.getCrossProduct(v1));
		assertEquals(new Vector(-23, -17, 0), v8.getCrossProduct(v3));
		assertEquals(new Vector(79, 85, 68), v8.getCrossProduct(v5));
		assertEquals(new Vector(-42, -51, -51), v8.getCrossProduct(v7));
		assertEquals(new Vector(-69, -96, -115), v8.getCrossProduct(v9));
		assertEquals(new Vector(0, 0, 0), v9.getCrossProduct(v9));
		assertEquals(new Vector(0, 0, 0), v9.getCrossProduct(v0));
		assertEquals(new Vector(-3, 0, -5), v9.getCrossProduct(v2));
		assertEquals(new Vector(-3, 3, -5), v9.getCrossProduct(v4));
		assertEquals(new Vector(-21, 32, -35), v9.getCrossProduct(v6));
		assertEquals(new Vector(69, 96, 115), v9.getCrossProduct(v8));
	}
	private IVector v0 = new Vector(0, 0, 0);
	private IVector v1 = new Vector(1, 0, 0);
	private IVector v2 = new Vector(0, 1, 0);
	private IVector v3 = new Vector(0, 0, 1);
	private IVector v4 = new Vector(1, 1, 0);
	private IVector v5 = new Vector(0, 4, -5);
	private IVector v6 = new Vector(-6, 7, 10);
	private IVector v7 = new Vector(0, -3, 3);
	private IVector v8 = new Vector(17, -23, 9);
	private IVector v9 = new Vector(-5, 0, 3);
}
