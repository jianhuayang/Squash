package ch.squash.simulation.test;

import junit.framework.TestCase;
import ch.squash.simulation.shapes.common.AbstractShape;

public class AbstractShapeTest extends TestCase {
	
	public void testAreEqual(){
		assertEquals(true,	AbstractShape.areEqual(0, 0));
		assertEquals(true,	AbstractShape.areEqual(0, -0));
		assertEquals(false,	AbstractShape.areEqual(0, 1));
		assertEquals(true,	AbstractShape.areEqual(1, 1));
		assertEquals(false,	AbstractShape.areEqual(1, 2));
		assertEquals(false,	AbstractShape.areEqual(1, 1.5f));
		assertEquals(true,	AbstractShape.areEqual(1.5f, 1.5f));
		assertEquals(true,	AbstractShape.areEqual(1.00001f, 1.00001f));
		assertEquals(false,	AbstractShape.areEqual(1.00001f, 1.00000f));
		assertEquals(true,	AbstractShape.areEqual(1.000001f, 1.000001f));
		assertEquals(false,	AbstractShape.areEqual(1.000001f, 1.000000f));
		assertEquals(true,	AbstractShape.areEqual(1.0000001f, 1.0000001f));
		assertEquals(false,	AbstractShape.areEqual(1.0000001f, 1.0000000f));
	}
	
	public void testGetPointPointDistance(){
		assertEquals(0f,	AbstractShape.getPointPointDistance(new float[0], new float[0]));
		assertEquals(-1f,	AbstractShape.getPointPointDistance(new float[1], new float[0]));
		assertEquals(0f,	AbstractShape.getPointPointDistance(new float[]{ 1 }, new float[]{ 1 }));
		assertEquals(1f,	AbstractShape.getPointPointDistance(new float[]{ 1 }, new float[]{ 0 }));
		assertEquals(5f,	AbstractShape.getPointPointDistance(new float[]{ 0, 0 }, new float[]{ 4, -3 }));
		assertEquals(0f,	AbstractShape.getPointPointDistance(new float[]{ 1, 1 }, new float[]{ 1, 1 }));
		assertEquals(5f,	AbstractShape.getPointPointDistance(new float[]{ 1, 1 }, new float[]{ 4, -3 }));
		assertEquals((float)Math.sqrt(269),	AbstractShape.getPointPointDistance(new float[]{ 0, 0, 0 }, new float[]{ 6, 8, -13 }));
		assertEquals((float)Math.sqrt(89),	AbstractShape.getPointPointDistance(new float[]{ 1, 1, 1 }, new float[]{ -2, 5, -7 }));
			
	}
}
