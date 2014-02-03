package ch.squash.simulation.test;

import junit.framework.TestCase;
import ch.squash.simulation.shapes.common.AbstractShape;

public class AbstractShapeTest extends TestCase {
	
	public void testAreEqual(){
		assertEquals(true,	AbstractShape.areEqual(0, 0));
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
}
