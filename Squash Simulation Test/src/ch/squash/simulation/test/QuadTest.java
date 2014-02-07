package ch.squash.simulation.test;

import junit.framework.TestCase;
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
	private Quadrilateral q6 = new Quadrilateral("q6", new float[]{1,2,0,		1,0,2,		-1,0,2,		-1,3,-1}, new float[]{0, 0, 0, 0}, false);
	// 0/1/-3 to -2/3/0
	private Quadrilateral q7 = new Quadrilateral("q7", new float[]{0,1,-3,		-2,1,0,		-2,3,0,		0,3,-3}, new float[]{0, 0, 0, 0}, false);

	public void testGetIntersectionWithPlane() {
		assertEquals(new Vector(0,	0,	0 ), q0.getIntersectionWithPlane(new Vector(0, 0, 0), new Vector(1, 1, 1)));
		assertEquals(new Vector(0,	0,	0 ), q0.getIntersectionWithPlane(new Vector(0, 0, 0), new Vector(-21, 3, 0.75f)));
		assertEquals(new Vector(2,	0,	2 ), q0.getIntersectionWithPlane(new Vector(2, 0, 2), new Vector(-21, 3, 0.75f)));
		assertEquals(new Vector(-2,	0,	-2), q0.getIntersectionWithPlane(new Vector(-2, 0, -2), new Vector(-21, 3, 0.75f)));
		assertEquals(null				   , q0.getIntersectionWithPlane(new Vector(0, 1, 0), new Vector(1, 0, 1)));
		assertEquals(new Vector(1,	0,	1 ), q0.getIntersectionWithPlane(new Vector(2, 1, 2), new Vector(1, 1, 1)));
		assertEquals(new Vector(-1.5f,0,-2.5f), q0.getIntersectionWithPlane(new Vector(0, -1, 0), new Vector(3, -2, 5)));
	}
	
	public void testgetDistanceFromQuadPlaneToPoint() {
		assertEquals(0f, q0.getDistanceFromQuadPlaneToPoint(new Vector(0,	0,	0)));
		assertEquals(0f, q0.getDistanceFromQuadPlaneToPoint(new Vector(-1,	0,	0)));
		assertEquals(0f, q0.getDistanceFromQuadPlaneToPoint(new Vector(-4,	0,	3)));
		assertEquals(1f, q0.getDistanceFromQuadPlaneToPoint(new Vector(0,	1,	0)));
		assertEquals(1f, q0.getDistanceFromQuadPlaneToPoint(new Vector(3,	-1,	7)));
		
		assertEquals(0f, q5.getDistanceFromQuadPlaneToPoint(new Vector(-3,	3,	0)));
		assertEquals(0f, q5.getDistanceFromQuadPlaneToPoint(new Vector(-9,	4.5f,0)));
		assertEquals(0f, q5.getDistanceFromQuadPlaneToPoint(new Vector(1,	-1,	0)));
		assertEquals(1.5f, q5.getDistanceFromQuadPlaneToPoint(new Vector(-2,	2,	-1.5f)));
		assertEquals(2.75f, q5.getDistanceFromQuadPlaneToPoint(new Vector(10, 10.5f, 2.75f)));
	}
	
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
		
		assertEquals("p on vertex 0",			0f,						q0.getDistanceToPoint(new Vector(-1, 0, -1)));
		assertEquals("p on vertex 1",			0f,						q0.getDistanceToPoint(new Vector(1, 0, -1)));
		assertEquals("p on vertex 2",			0f,						q0.getDistanceToPoint(new Vector(1, 0, 1)));
		assertEquals("p on vertex 3",			0f,						q0.getDistanceToPoint(new Vector(-1, 0, 1)));
		
		assertEquals("p on edge 0",				0f,						q0.getDistanceToPoint(new Vector(0.5f, 0, -1)));
		assertEquals("p on edge 1",				0f,						q0.getDistanceToPoint(new Vector(1, 0, 0.4f)));
		assertEquals("p on edge 2",				0f,						q0.getDistanceToPoint(new Vector(-0.3f, 0, 1)));
		assertEquals("p on edge 3",				0f,						q0.getDistanceToPoint(new Vector(-1f, 0, -0.1f)));
		
		assertEquals("p closest to vertex 0",	(float)Math.sqrt(2),	q0.getDistanceToPoint(new Vector(-2, 0, -2)));
		assertEquals("p closest to vertex 1",	(float)Math.sqrt(1.25),	q0.getDistanceToPoint(new Vector(2, 0, -1.5f)));
		assertEquals("p closest to vertex 2",	(float)Math.sqrt(4.5),	q0.getDistanceToPoint(new Vector(2.5f, 0, 2.5f)));
		assertEquals("p closest to vertex 3",	5f,						q0.getDistanceToPoint(new Vector(-5, 0, 4)));
		
		assertEquals("p closest to edge 0"	,	3f,						q0.getDistanceToPoint(new Vector(0.25f, 0, -4)));
		assertEquals("p closest to edge 1"	,	4f,						q0.getDistanceToPoint(new Vector(5, 0, 0.5f)));
		assertEquals("p closest to edge 2"	,	2f,						q0.getDistanceToPoint(new Vector(0, 0, 3)));
		assertEquals("p closest to edge 3"	,	2.5f,					q0.getDistanceToPoint(new Vector(-3.5f, 0, 0)));

		// points in same plane around quad q2
		assertEquals("p within quad",			0f,						q2.getDistanceToPoint(new Vector(-1, 1, 0)));
		
		assertEquals("p on vertex 0",			0f,						q2.getDistanceToPoint(new Vector(-2, -2, 0)));
		assertEquals("p on vertex 1",			0f,						q2.getDistanceToPoint(new Vector(5, -2, 0)));
		assertEquals("p on vertex 2",			0f,						q2.getDistanceToPoint(new Vector(5, 4, 0)));
		assertEquals("p on vertex 3",			0f,						q2.getDistanceToPoint(new Vector(-2, 2, 0)));
		
		assertEquals("p on edge 0",				0f,						q2.getDistanceToPoint(new Vector(1.5f, -2, 0)));
		assertEquals("p on edge 1",				0f,						q2.getDistanceToPoint(new Vector(5, 3.5f, 0)));
		assertEquals("p on edge 2",				0f,						q2.getDistanceToPoint(new Vector(1.5f, 3, 0)));		// inaccurate
		assertEquals("p on edge 3",				0f,						q2.getDistanceToPoint(new Vector(-2, 1.5f, 0)));
		
		assertEquals("p closest to vertex 0",	(float)Math.sqrt(2.5),	q2.getDistanceToPoint(new Vector(-3.5f, -2.5f, 0)));
		assertEquals("p closest to vertex 1",	(float)Math.sqrt(3.25),	q2.getDistanceToPoint(new Vector(6, -3.5f, 0)));
		assertEquals("p closest to vertex 2",	(float)Math.sqrt(4.5),	q2.getDistanceToPoint(new Vector(6.5f, 5.5f, 0)));
		assertEquals("p closest to vertex 3",	5f,						q2.getDistanceToPoint(new Vector(-6, 5, 0)));
		
		assertEquals("p closest to edge 0"	,	1.5f,					q2.getDistanceToPoint(new Vector(0.5f, -3.5f, 0)));
		assertEquals("p closest to edge 1"	,	1.5f,					q2.getDistanceToPoint(new Vector(6.5f, 0, 0)));
		assertEquals("p closest to edge 2"	,	(float)Math.sqrt(212),	q2.getDistanceToPoint(new Vector(-2.5f, 17, 0)));
		assertEquals("p closest to edge 3"	,	1.5f,					q2.getDistanceToPoint(new Vector(-3.5f, 0, 0)));

		// points in different plane around quad q5
		assertEquals("p within quad",			2.5f,					q5.getDistanceToPoint(new Vector(-3.5f, 3.5f, 2.5f)));
		
		assertEquals("p on vertex 0",			1.5f,					q5.getDistanceToPoint(new Vector(-2, 2, -1.5f)));
		assertEquals("p on vertex 1",			2.5f,					q5.getDistanceToPoint(new Vector(-2, 5, -2.5f)));
		assertEquals("p on vertex 2",			3.5f,					q5.getDistanceToPoint(new Vector(-5, 7, 3.5f)));
		assertEquals("p on vertex 3",			5.5f,					q5.getDistanceToPoint(new Vector(-5, 2, 5.5f)));
		
		assertEquals("p on edge 0",				2f,						q5.getDistanceToPoint(new Vector(-2, 2.5f, -2)));
		assertEquals("p on edge 1",				3.5f,					q5.getDistanceToPoint(new Vector(-3.5f, 6f, 3.5f)));
		assertEquals("p on edge 2",				3f,						q5.getDistanceToPoint(new Vector(-5, 6.5f, -3)));
		assertEquals("p on edge 3",				2f,						q5.getDistanceToPoint(new Vector(-4.5f, 2, -2)));
		
		assertEquals("p closest to vertex 0",	(float)Math.sqrt(51.5),	q5.getDistanceToPoint(new Vector(3.5f, -2.5f, -1)));
		assertEquals("p closest to vertex 1",	(float)Math.sqrt(13.25),q5.getDistanceToPoint(new Vector(1, 7, 0.5f)));
		assertEquals("p closest to vertex 2",	(float)Math.sqrt(7.5),	q5.getDistanceToPoint(new Vector(-5.5f, 8, 2.5f)));
		assertEquals("p closest to vertex 3",	(float)Math.sqrt(3),	q5.getDistanceToPoint(new Vector(-6, 1, 1)));
		
		assertEquals("p closest to edge 0"	,	(float)Math.sqrt(3.25),	q5.getDistanceToPoint(new Vector(-1, 3.5f, 1.5f)));
		assertEquals("p closest to edge 1"	,	(float)Math.sqrt(13.25),q5.getDistanceToPoint(new Vector(-1.5f, 9, -0.5f)));
		assertEquals("p closest to edge 2"	,	(float)Math.sqrt(8.5),	q5.getDistanceToPoint(new Vector(-6.5f, 3, -2.5f)));
		assertEquals("p closest to edge 3"	,	(float)Math.sqrt(10.25),q5.getDistanceToPoint(new Vector(-3.5f, 0, -2.5f)));
	}
}
