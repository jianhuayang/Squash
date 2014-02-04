package ch.squash.simulation.shapes.shapes;

import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;
import android.util.Log;
import ch.squash.simulation.shapes.common.AbstractShape;
import ch.squash.simulation.shapes.common.IVector;
import ch.squash.simulation.shapes.common.Vector;

public class Quadrilateral extends AbstractShape {
	private final static String TAG = Quadrilateral.class.getSimpleName();

	public final float[] edges;

	private static boolean mBothSides;

	/**
	 * Instantiates new quadrilateral
	 * 
	 * @param edges
	 *            edges of quadrilateral, in counter-clockwise order
	 * @param color
	 *            color of quadrilateral
	 */
	public Quadrilateral(final String tag, final float[] edges,
			final float[] color, final boolean bothSides) {
		super(tag, getMiddle(edges)[0], getMiddle(edges)[1],
				getMiddle(edges)[2], getVertices(edges, bothSides), color);

		this.edges = edges.clone();

		initialize(GLES20.GL_TRIANGLES, SolidType.AREA, null);
	}

	private static float[] getMiddle(final float[] edges) {
		return new float[] { edges[0] + (edges[6] - edges[0]) / 2,
				edges[1] + (edges[7] - edges[1]) / 2,
				edges[2] + (edges[8] - edges[2]) / 2 };
	}

	private static float[] getVertices(final float[] edges,
			final boolean bothSides) {
		mBothSides = bothSides;

		final float[] middle = getMiddle(edges);

		final float[] normEdges = new float[] { edges[0] - middle[0],
				edges[1] - middle[1], edges[2] - middle[2],
				edges[3] - middle[0], edges[4] - middle[1],
				edges[5] - middle[2], edges[6] - middle[0],
				edges[7] - middle[1], edges[8] - middle[2],
				edges[9] - middle[0], edges[10] - middle[1],
				edges[11] - middle[2] };

		final float[] result = new float[18 * (bothSides ? 2 : 1)];

		System.arraycopy(normEdges, 0, result, 0, 9);
		System.arraycopy(normEdges, 6, result, 9, 3);
		System.arraycopy(normEdges, 9, result, 12, 3);
		System.arraycopy(normEdges, 0, result, 15, 3);

		if (!bothSides)
			return result;

		System.arraycopy(normEdges, 0, result, 18, 3);
		System.arraycopy(normEdges, 9, result, 21, 3);
		System.arraycopy(normEdges, 6, result, 24, 3);
		System.arraycopy(normEdges, 6, result, 27, 3);
		System.arraycopy(normEdges, 3, result, 30, 3);
		System.arraycopy(normEdges, 0, result, 33, 3);

		return result;
	}

	private IVector getU() {
		return new Vector(edges[3] - edges[0], edges[4] - edges[1], edges[5]
				- edges[2]);
	}

	private IVector getV() {
		return new Vector(edges[9] - edges[0], edges[10] - edges[1], edges[11]
				- edges[2]);
	}

	public IVector getNormalVector() {
		final IVector u = getU();
		final IVector v = getV();
		// normal vector of the quad

		return new Vector(u.getY() * v.getZ() - u.getZ() * v.getY(), u.getZ()
				* v.getX() - u.getX() * v.getZ(), u.getX() * v.getY()
				- u.getY() * v.getX());
	}

	public IVector getNormalizedVector() {
		return getNormalVector().getNormalizedVector();
	}

	public float getDistanceToOrigin() {
		return getDistanceToPoint(new Vector(0, 0, 0));
	}

	public float getDistanceToPoint(final IVector p) {
		Log.i(TAG, "Testing whether " + p + " lies in quad " + tag);
		float dVertical = Float.MIN_VALUE; // distance from p to quad area

		// project p onto quad area
		final IVector n = getNormalVector();
		float[] q = new float[2]; // projection of p onto quad area
		float[] e = new float[8]; // edges in 2d (2 relevant components of quad
									// area)

		// ensure there is only one component in normal vector (two 0's)
		// and compute q
		for (int i = 0; i < 3; i++)
			if (n.getDirection()[i] != 0)
				if (Math.abs(n.getDirection()[i]) == n.getLength()) {
					float[] v = p.getDirection();
					dVertical = Math.abs(v[i] - edges[i]);
					v[i] = edges[i];

					if (i == 0)
						q[0] = v[1];
					else
						q[0] = v[0];
					if (i == 2)
						q[1] = v[1];
					else
						q[1] = v[2];

					int index = 0;
					for (int j = 0; j < 4; j++)
						for (int k = 0; k < 3; k++)
							if (k == i)
								continue;
							else
								e[index++] = edges[3 * j + k];

					Log.d(TAG, "q=" + q[0] + "/" + q[1] + ", edges=" + e[0]
							+ "/" + e[1] + ", " + e[2] + "/" + e[3] + ", "
							+ e[4] + "/" + e[5] + ", " + e[6] + "/" + e[7]
							+ ", ");
				} else {
					Log.e(TAG, "Cant handle non-ortogonal quads");
					return -1;
				}

		float m;
		float b;
		final List<Integer> violatedEdges = new ArrayList<Integer>();

		// find violated edges
		for (int i = 0; i < 4; i++) {
			boolean otherEdgeSign;
			boolean qSign;

			// m = (y2 - y1) / (x2 - x1)
			m = (e[(2 * i + 3) % 8] - e[(2 * i + 1) % 8])
					/ (e[(2 * i + 2) % 8] - e[(2 * i + 0) % 8]);
			
			if (areEqual(e[(2 * i + 2) % 8], e[(2 * i + 0) % 8])
					|| Float.isInfinite(m) || Float.isNaN(m)) {
				// can not do it with geometry if slope is infinite
				// sign depends on x-coordinate
				otherEdgeSign = e[(2 * i + 4) % 8] >= e[(2 * i + 2) % 8];
//				qSign = q[0] >= e[(2 * i + 2) % 8];
				qSign = q[0] > e[(2 * i + 2) % 8];
				
				if (q[0] == e[(2 * i + 2) % 8])
					qSign = otherEdgeSign;
			} else {
				// can do it with geometry; continue
				// b = y1 - m * x1
				b = e[(2 * i + 1) % 8] - m * e[(2 * i + 0) % 8];

				// check sign of "opposite" (next) corner
				// y3 - m * x3 - b > 0
				otherEdgeSign = e[(2 * i + 5) % 8] - m * e[(2 * i + 4) % 8] - b >= 0;
				qSign = (q[1] - m * q[0] - b >= 0);
			}
			// edge is violated if sign of point doesnt match
			if (otherEdgeSign != qSign)
				violatedEdges.add(i);
		}

		// nothing more to do if point lies within quad
		if (violatedEdges.size() == 0)
			// point is in quad
			return dVertical;
		
		// ensure amount of violated edges is valid
		if (violatedEdges.size() != 1 && violatedEdges.size() != 2){
			Log.e(TAG, "Invalid amount of violated edges. Expected 0-2, have " + violatedEdges.size());
			return -1;
		}
		
		// calculate intersection of violated edges with normal vector of the edge through q
		float inverseM;
		float inverseB;
		final float[] intersections = new float[2 * violatedEdges.size()];
		for (int i = 0; i < violatedEdges.size(); i++)
			if (areEqual(e[(2 * violatedEdges.get(i)+ 2) % 8], e[(2 * violatedEdges.get(i) + 0) % 8])){
				// dx == 0
				// edge 0 is vertical -> normal vector is horizontal
				intersections[2 * i] = e[(2 * violatedEdges.get(i) + 0) % 8];
				intersections[2*i+1] = q[1];
			}else if (areEqual(e[(2 * violatedEdges.get(i)+ 3) % 8], e[(2 * violatedEdges.get(i) + 1) % 8])){
				// dy == 0
				// edge 0 is horizontal -> normal vector is vertical
				intersections[2 * i] = q[0];
				intersections[2*i+1] = e[(2 * violatedEdges.get(i) + 1) % 8];
			}else{
				// do geometry...
				m = (e[(2 * violatedEdges.get(i) + 3) % 8] - e[(2 * violatedEdges.get(i) + 1) % 8])
						/ (e[(2 * violatedEdges.get(i) + 2) % 8] - e[(2 * violatedEdges.get(i) + 0) % 8]);
				b = e[(2 * violatedEdges.get(i) + 1) % 8] - m * e[(2 * violatedEdges.get(i) + 0) % 8];
				inverseM = -1/m;
				inverseB = q[1] - inverseM * q[0];
				
				intersections[2 * i] = (inverseB - b) / (m - inverseM);
				intersections[2*i+1] = m * intersections[2*i] + b;
			}
		
		// if only one edge was violated, it must be closest to a edge
		// so take distance between q and intersection as vertical distance
		if (violatedEdges.size() == 1)
			return (float)Math.sqrt(Math.pow(dVertical, 2) + Math.pow(AbstractShape.getPointPointDistance(
					new float[]{ intersections[0], intersections[1] }, q), 2));
		
		// if neither intersection is on the edge, take distance between vertex and q
		// else, take distance between intersection and q
		for (int i = 0; i < 2; i++){
			final float dirx = e[(2 * violatedEdges.get(i) + 0) % 8] - e[(2 * violatedEdges.get(i) + 2) % 8];
			final float diry = e[(2 * violatedEdges.get(i) + 1) % 8] - e[(2 * violatedEdges.get(i) + 3) % 8];

			final float lambdax = (e[(2 * violatedEdges.get(i) + 0) % 8] - intersections[2 * i]) / dirx;
			final float lambday = (e[(2 * violatedEdges.get(i) + 1) % 8] - intersections[2*i+1]) / diry;
//			final float lambdax = (q[0] - e[(2 * violatedEdges.get(i) + 0) % 8]) / dirx;
//			final float lambday = (q[1] - e[(2 * violatedEdges.get(i) + 1) % 8]) / diry;
//			final float epsilon = Math.abs(lambdax + lambday) / 2 / 100000;
//			 point is on edge if the lambdas match
			
			// point is on edge if 0 <= lambda <= 1 
			if (lambdax >= 0 && lambdax <= 1 && lambday >= 0 && lambday <= 1){
				return (float)Math.sqrt(Math.pow(dVertical, 2) + Math.pow(AbstractShape.getPointPointDistance(
						new float[]{ intersections[2 * i], intersections[2*i+1] }, q), 2));
			}
		}
		
		// no intersection was on edge, take distance between q and vertex
		// use special indices if the edge is closest to the very first edge
		if (violatedEdges.get(0) == 0 && violatedEdges.get(1) == 3)
			return (float)Math.sqrt(Math.pow(dVertical, 2) + 
					Math.pow(AbstractShape.getPointPointDistance(new float[]{ e[(2 * violatedEdges.get(0)) % 8],  e[(2 * violatedEdges.get(0) + 1) % 8] }, q), 2));
		
		return (float)Math.sqrt(Math.pow(dVertical, 2) + 
				Math.pow(AbstractShape.getPointPointDistance(new float[]{ e[(2 * violatedEdges.get(1)) % 8],  e[(2 * violatedEdges.get(1) + 1) % 8] }, q), 2));
		
		
		
		
//		if (violatedEdges.size() == 1){
//			// point is closest to violated edge
//			
//			// recalculate m, b
//			m = (e[(2 * violatedEdges.get(0) + 3) % 8] - e[(2 * violatedEdges.get(0) + 1) % 8])
//					/ (e[(2 * violatedEdges.get(0) + 2) % 8] - e[(2 * violatedEdges.get(0) + 0) % 8]);
//			if (areEqual(e[(2 * violatedEdges.get(0)+ 2) % 8], e[(2 * violatedEdges.get(0) + 0) % 8]) || Float.isInfinite(m) || Float.isNaN(m)) {
//				// can not do it with geometry if slope is infinite
//				// distance depends on x-coordinate
//				return (float)Math.sqrt(Math.pow(dVertical, 2) + Math.pow(q[0] - e[(2 * violatedEdges.get(0) + 0) % 8], 2));
//			}
//			// can do it with geometry (maybe); continue
//			// b = y1 - m * x1
//			b = e[(2 * violatedEdges.get(0) + 1) % 8] - m * e[(2 * violatedEdges.get(0) + 0) % 8];
//						
//			// calculate inverseM, inverseB
//			final float inverseM = -1/m;
//			if (areEqual(inverseM, 0f) || Float.isInfinite(inverseM) || Float.isNaN(inverseM)) {
//				// can not do it with geometry after all, slope is zero
//				// distance depends on y-coordinate
//				return (float)Math.sqrt(Math.pow(dVertical, 2) + Math.pow(q[1] - e[(2 * violatedEdges.get(0) + 1) % 8], 2));
//			}
//			// can do it with geometry AFTER ALL; continue
//			final float inverseB = q[1] - inverseM * q[0];
//			
//			// intersect 2 lines
//			final float x = (inverseB - b) / (m - inverseM);	// no need to test x for infinity
//			final float y = m * x + b;
//			
//			return (float)Math.sqrt(Math.pow(dVertical, 2) + Math.pow(AbstractShape.getPointPointDistance(new float[]{ x, y }, q), 2));
//		}
//		
//		if (violatedEdges.size() == 2){
//			// point is LIKELY to be closest to corner between two violated edges
//			// but point MAY also be closest to be an edge while also violating a second corner
//
//			
//			
//			// find out whether one of the two corners is further away from the edge
//			float nearestCorner = AbstractShape.getPointPointDistance(
//					new float[]{ e[(2 * violatedEdges.get(0) + 0) % 8], e[(2 * violatedEdges.get(0) + 1) % 8] } , q);
//			float distanceToEdge;
//			
//			if (nearestCorner > AbstractShape.getPointPointDistance(
//					new float[]{ e[(2 * violatedEdges.get(0) + 2) % 8], e[(2 * violatedEdges.get(0) + 3) % 8] } , q))
//				nearestCorner = AbstractShape.getPointPointDistance(
//						new float[]{ e[(2 * violatedEdges.get(0) + 2) % 8], e[(2 * violatedEdges.get(0) + 3) % 8] } , q);
//					
//			// recalculate m, b
//			m = (e[(2 * violatedEdges.get(0) + 3) % 8] - e[(2 * violatedEdges.get(0) + 1) % 8])
//					/ (e[(2 * violatedEdges.get(0) + 2) % 8] - e[(2 * violatedEdges.get(0) + 0) % 8]);
//			if (areEqual(e[(2 * violatedEdges.get(0)+ 2) % 8], e[(2 * violatedEdges.get(0) + 0) % 8]) || Float.isInfinite(m) || Float.isNaN(m)) {
//				// can not do it with geometry if slope is infinite
//				// distance depends on x-coordinate
//				distanceToEdge = Math.abs(q[0] - e[(2 * violatedEdges.get(0) + 0) % 8]);
//			}else{
//				// can do it with geometry (maybe); continue
//				// b = y1 - m * x1
//				b = e[(2 * violatedEdges.get(0) + 1) % 8] - m * e[(2 * violatedEdges.get(0) + 0) % 8];
//							
//				// calculate inverseM, inverseB
//				final float inverseM = -1/m;
//				if (areEqual(inverseM, 0f) || Float.isInfinite(inverseM) || Float.isNaN(inverseM)) {
//					// can not do it with geometry after all, slope is zero
//					// distance depends on y-coordinate
//					distanceToEdge = Math.abs(q[1] - e[(2 * violatedEdges.get(0) + 1) % 8]);
//				}else{
//					// can do it with geometry AFTER ALL; continue
//					final float inverseB = q[1] - inverseM * q[0];
//					
//					// intersect 2 lines
//					final float x = (inverseB - b) / (m - inverseM);	// no need to test x for infinity
//					final float y = m * x + b;
//					
//					distanceToEdge = AbstractShape.getPointPointDistance(new float[]{ x, y }, q);
//				}
//			}
//			
//			// PROBLEM:		distance is measured to infinitely long edge, not to finite edge
//			// SOLUTION:	not only in normal case, calculate intersection with (infinite) edge
//			//				then check whether intersection is on finite edge
//			//				yes	-> point is closest to edge, return that distance
//			//				no	-> point is closest to vertex, return "original" distance
//			// DO ALSO:		refactor that piece of code into a method and call it with 1 violated edge also
//			
//			
//			
//			if (distanceToEdge <= nearestCorner){
//				Log.w(TAG, "Point is (surprisingly) closer to edge than to vertex, fyi...");
//				return (float)Math.sqrt(Math.pow(dVertical, 2) + Math.pow(distanceToEdge, 2));
//			}
//				
//			// use special indices if the edge is closest to the very first edge
//			if (violatedEdges.get(0) == 0 && violatedEdges.get(1) == 3)
//				return (float)Math.sqrt(Math.pow(dVertical, 2) + 
//						Math.pow(AbstractShape.getPointPointDistance(new float[]{ e[(2 * violatedEdges.get(0)) % 8],  e[(2 * violatedEdges.get(0) + 1) % 8] }, q), 2));
//			
//			return (float)Math.sqrt(Math.pow(dVertical, 2) + 
//					Math.pow(AbstractShape.getPointPointDistance(new float[]{ e[(2 * violatedEdges.get(1)) % 8],  e[(2 * violatedEdges.get(1) + 1) % 8] }, q), 2));
//		}
//		
//		Log.e(TAG, "Invalid amount of violated edges: " + violatedEdges.size());
//		return -1;
	}

	public boolean isPointInQuad(final IVector p) {
		return getDistanceToPoint(p) == 0f;
	}

	@Override
	protected float[] getColorData(final float[] color) {
		final float[] result = new float[6 * color.length
				* (mBothSides ? 2 : 1)];

		for (int i = 0; i < 6 * (mBothSides ? 2 : 1); i++)
			System.arraycopy(color, 0, result, i * color.length, color.length);

		return result;
	}
}
