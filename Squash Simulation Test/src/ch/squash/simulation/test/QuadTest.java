package ch.squash.simulation.test;

import junit.framework.TestCase;
import ch.squash.simulation.shapes.common.IVector;
import ch.squash.simulation.shapes.common.Vector;
import ch.squash.simulation.shapes.shapes.Quadrilateral;

public class QuadTest extends TestCase {
	// -1/-1 to 1/1 on y=0
	private Quadrilateral q0 = new Quadrilateral("q0", new float[]{-1,0,-1,		1,0,-1,		1,0,1,		-1,0,1}, new float[]{0, 0, 0, 0}, false);
	// -1/-1 to 1/1 on x=0
	private Quadrilateral q1 = new Quadrilateral("q1", new float[]{0,-1,-1,		0,-1,1,		0,1,1,		0,1,-1}, new float[]{0, 0, 0, 0}, false);
	// -2/-2 to 5/-2 to 5/4 to -2/2 on z=0
	private Quadrilateral q2 = new Quadrilateral("q2", new float[]{-2,-2,0,		5,-2,0,		5,4,0,		-2,2,0}, new float[]{0, 0, 0, 0}, false);
	// 3/3 to 7/7 on y=0
	private Quadrilateral q3 = new Quadrilateral("q3", new float[]{3,0,3,		7,0,3,		7,0,7,		3,0,7}, new float[]{0, 0, 0, 0}, false);
	// -5/-5 to -2/-2 on x=0
	private Quadrilateral q4 = new Quadrilateral("q4", new float[]{0,-5,-5,		0,-5,-2,	0,-2,-2,	0,-2,-5}, new float[]{0, 0, 0, 0}, false);
	// -2/2 to -2/5 to -5/7 to -5/2 on z=0
	private Quadrilateral q5 = new Quadrilateral("q5", new float[]{-2,2,0,		-2,5,0,		-5,7,0,		-5,2,0}, new float[]{0, 0, 0, 0}, false);
	// 1/2/0 to -2/0/2
	private Quadrilateral q6 = new Quadrilateral("q6", new float[]{1,2,0,		1,0,2,		-1,0,2,		-1,2,0}, new float[]{0, 0, 0, 0}, false);
	// 0/1/-3 to -2/3/0
	private Quadrilateral q7 = new Quadrilateral("q7", new float[]{0,1,-3,		-2,1,0,		-2,3,0,		0,3,-3}, new float[]{0, 0, 0, 0}, false);
	
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
		assertEquals(new Vector( 0, -4,  0), q0.getNormalVector());
		assertEquals(new Vector(-4,  0,  0), q1.getNormalVector());
		assertEquals(new Vector( 0,  0, 28), q2.getNormalVector());
		assertEquals(new Vector( 0,-16,  0), q3.getNormalVector());
		assertEquals(new Vector(-9,  0,  0), q4.getNormalVector());
		assertEquals(new Vector( 0,  0,  9), q5.getNormalVector());
		assertEquals(new Vector( 0, -4, -4), q6.getNormalVector());
		assertEquals(new Vector(-6,  0, -4), q7.getNormalVector());
	}
	
	public void testGetDistanceToPoint(){
		// points in same plane around rectangle q0
		assertEquals("p within quad",			0f,						q0.getDistanceToPoint(new Vector(0.5f, 0, -0.5f)));
		
		assertEquals("p on vertex 1",			0f,						q0.getDistanceToPoint(new Vector(-1, 0, -1)));
		assertEquals("p on vertex 2",			0f,						q0.getDistanceToPoint(new Vector(1, 0, -1)));
		assertEquals("p on vertex 3",			0f,						q0.getDistanceToPoint(new Vector(1, 0, 1)));
		assertEquals("p on vertex 4",			0f,						q0.getDistanceToPoint(new Vector(-1, 0, 1)));
		
		assertEquals("p on edge 1",				0f,						q0.getDistanceToPoint(new Vector(0.5f, 0, -1)));
		assertEquals("p on edge 2",				0f,						q0.getDistanceToPoint(new Vector(1, 0, 0.4f)));
		assertEquals("p on edge 3",				0f,						q0.getDistanceToPoint(new Vector(-0.3f, 0, 1)));
		assertEquals("p on edge 4",				0f,						q0.getDistanceToPoint(new Vector(-1f, 0, -0.1f)));
		
		assertEquals("p closest to vertex 1",	(float)Math.sqrt(2),	q0.getDistanceToPoint(new Vector(-2, 0, -2)));
		assertEquals("p closest to vertex 2",	(float)Math.sqrt(1.25),	q0.getDistanceToPoint(new Vector(2, 0, -1.5f)));
		assertEquals("p closest to vertex 3",	(float)Math.sqrt(4.5),	q0.getDistanceToPoint(new Vector(2.5f, 0, 2.5f)));
		assertEquals("p closest to vertex 4",	5f,						q0.getDistanceToPoint(new Vector(-5, 0, 4)));
		
		assertEquals("p closest to edge 1"	,	3f,						q0.getDistanceToPoint(new Vector(0.25f, 0, -4)));
		assertEquals("p closest to edge 2"	,	4f,						q0.getDistanceToPoint(new Vector(5, 0, 0.5f)));
		assertEquals("p closest to edge 3"	,	2f,						q0.getDistanceToPoint(new Vector(0, 0, 3)));
		assertEquals("p closest to edge 4"	,	2.5f,					q0.getDistanceToPoint(new Vector(-3.5f, 0, 0)));

		// points in different plane around rectangle q1
		
		// points in same plane around quad q2

		// points in different plane around quad q5

		// points in different plane around quad q6

		// points in different plane around quad q7
		
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
		assertEquals("point on vertex of quad with dvert",			12f,						q2.getDistanceToPoint(v9));
		assertEquals("point near edge of quad with dvert",			(float)Math.sqrt(409),		q2.getDistanceToPoint(v10));
		assertEquals("point near vertex of quad with dvert",		(float)Math.sqrt(194),		q2.getDistanceToPoint(v11));
		assertEquals("point near skewed edge of quad with dvert",	7.889867f,					q2.getDistanceToPoint(v12));
	}
}
