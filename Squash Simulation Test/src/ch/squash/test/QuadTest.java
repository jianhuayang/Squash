package ch.squash.test;

import junit.framework.TestCase;
import android.util.Log;
import ch.squash.shapes.common.IVector;
import ch.squash.shapes.common.Vector;
import ch.squash.shapes.shapes.Quadrilateral;

public class QuadTest extends TestCase {
	// -1/-1 to 1/1 on y=0
	private Quadrilateral q0 = new Quadrilateral("q0", new float[]{-1,0,-1,  1,0,-1,  1,0,1,  -1,0,1}, new float[]{0, 0, 0, 0}, false);
	// -1/-1 to 1/1 on x=0
	private Quadrilateral q1 = new Quadrilateral("q1", new float[]{0,-1,-1,  0,-1,1,  0,1,1,  0,1,-1}, new float[]{0, 0, 0, 0}, false);
	// -2/-2 to 5/-2 to 5/4 to -2/2 on z=0
	private Quadrilateral q2 = new Quadrilateral("q2", new float[]{-2,-2,0,  5,-2,0,  5,4,0,  -2,2,0}, new float[]{0, 0, 0, 0}, false);
	
	private IVector v0 = new Vector(0, 0, 0);
	private IVector v1 = new Vector(1, 0, 1);
	private IVector v2 = new Vector(1, 0, 0);
	private IVector v3 = new Vector(0, 1, 0);
	private IVector v4 = new Vector(1, 1, 1);
	private IVector v5 = new Vector(1, 1, 0);
	private IVector v6 = new Vector(0, -1, 0);
	private IVector v7 = new Vector(1, -1, 1);
	private IVector v8 = new Vector(1, -1, 0);
	private IVector v9 = new Vector(5, -2, -12);
	private IVector v10 = new Vector(-1, -5, -20);
	private IVector v11 = new Vector(-5, -6, 13);
	private IVector v12 = new Vector(0.5f, 6.5f, 7);
	
	public void testGetNormalVector() {
		assertEquals(new Vector(0, -4, 0), q0.getNormalVector());
		assertEquals(new Vector(-4, 0, 0), q1.getNormalVector());
		assertEquals(new Vector(0, 0, 28), q2.getNormalVector());
	}
	
	public void testGetDistanceToPoint(){
		// q0
		assertEquals("point in middle of quad", 0f, q0.getDistanceToPoint(v0));
		assertEquals("point on vertex of quad",  0f, q0.getDistanceToPoint(v1));
		assertEquals("point on edge of quad",  0f, q0.getDistanceToPoint(v2));
		assertEquals("point in middle of quad but with vert offset", 1f, q0.getDistanceToPoint(v3));
		assertEquals("point on vertex of quad but with vert offset",  1f, q0.getDistanceToPoint(v4));
		assertEquals("point on edge of quad but with vert offset",  1f, q0.getDistanceToPoint(v5));
		assertEquals("point in middle of quad but with -vert offset", 1f, q0.getDistanceToPoint(v6));
		assertEquals("point on vertex of quad but with -vert offset",  1f, q0.getDistanceToPoint(v7));
		assertEquals("point on edge of quad but with -vert offset",  1f, q0.getDistanceToPoint(v8));
		
		// q1
		assertEquals(0f, q1.getDistanceToPoint(v0));
		assertEquals(1f, q1.getDistanceToPoint(v1));
		assertEquals(1f, q1.getDistanceToPoint(v2));
		assertEquals(0f, q1.getDistanceToPoint(v3));
		assertEquals(1f, q1.getDistanceToPoint(v4));
		assertEquals(1f, q1.getDistanceToPoint(v5));
		assertEquals(0f, q1.getDistanceToPoint(v6));
		assertEquals(1f, q1.getDistanceToPoint(v7));
		assertEquals(1f, q1.getDistanceToPoint(v8));
		
		// q2
		assertEquals("point on vertex of quad with dvert", 12f, q2.getDistanceToPoint(v9));
		assertEquals("point near edge of quad with dvert", (float)Math.sqrt(409), q2.getDistanceToPoint(v10));
		assertEquals("point near vertex of quad with dvert", (float)Math.sqrt(169+25), q2.getDistanceToPoint(v11));
		assertEquals("point near skewed edge of quad with dvert", 7.889867f, q2.getDistanceToPoint(v12));
	}
}
