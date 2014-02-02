package ch.squash.simulation.shapes.shapes;

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
	public Quadrilateral(final String tag, final float[] edges, final float[] color,
			final boolean bothSides) {
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

	private static float[] getVertices(final float[] edges, final boolean bothSides) {
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
		Log.d(TAG, "Testing whether " + p + " lies in quad " + tag);
		float dVertical = Float.MIN_VALUE; 			// distance from p to quad area

		// project p onto quad area
		final IVector n = getNormalVector();
		float[] q = new float[2];							// projection of p onto quad area
		float[] e = new float[8];					// edges in 2d (2 relevant components of quad area)
		
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

					Log.v(TAG, "q=" + q[0] + "/" + q[1] + ", edges=" + e[0]
							+ "/" + e[1] + ", " + e[2] + "/" + e[3] + ", "
							+ e[4] + "/" + e[5] + ", " + e[6] + "/" + e[7]
							+ ", ");
				} else {
					Log.e(TAG, "Cant handle non-ortogonal quads");
					return -1;
				}	
		
		// determine whether point lies in quad on plane
		// iterate over all edges
		final float[] eq = new float[2];	// current edge to point on plane
		final float[] e1e2 = new float[2];	// edge across from current edge
		final float[] e3e2 = new float[2];	// other edge across from current edge
		final float[] m = new float[3];		// slope  of the 3 vectors
		final float[] b = new float[3];		// y-intercept of 3 vectors
		final float[] s = new float[4];		// 2 intersections of eq and e1e2/e3e2
		for (int i = 0; i < 4; i++) {
			eq[0] = q[0] - e[2 * i];
			eq[1] = q[1] - e[2 * i + 1];
			e1e2[0] = e[(2 * i + 4) % 8] - e[(2 * i + 2) % 8];
			e1e2[1] = e[(2 * i + 5) % 8] - e[(2 * i + 3) % 8];
			e3e2[0] = e[(2 * i + 4) % 8] - e[(2 * i + 6) % 8];
			e3e2[1] = e[(2 * i + 5) % 8] - e[(2 * i + 7) % 8];

			m[0] = eq[1] / eq[0];
			m[1] = e1e2[1] / e1e2[0];
			m[2] = e3e2[1] / e3e2[0];
			b[0] = e[(2*i+1)%8] - m[0] * e[(2*i)%8];
			b[1] = e[(2*i+5)%8] - m[1] * e[(2*i+4)%8];
			b[2] = e[(2*i+5)%8] - m[2] * e[(2*i+4)%8];

			if (Float.isInfinite(m[0]))
				Log.w(TAG, "Slope of eq is infinite");
			
			s[0] = Float.isInfinite(m[1]) ? e[(2*i+4)%8] : (b[1] - b[0]) / (m[0] - m[1]);
			s[1] = m[0] * s[0] + b[0];
			s[2] = Float.isInfinite(m[2]) ? e[(2*i+4)%8] : (b[2] - b[0]) / (m[0] - m[2]);
			s[3] = m[0] * s[2] + b[0];
			
			double lengthEq = Math.sqrt(Math.pow(eq[0], 2) + Math.pow(eq[1], 2));
			double lengthES0 = Math.sqrt(Math.pow(s[0] - e[2*i], 2) + Math.pow(s[1] - e[2*i+1], 2));
			double lengthES1 = Math.sqrt(Math.pow(s[2] - e[2*i], 2) + Math.pow(s[3] - e[2*i+1], 2));

			float lambda11 = e1e2[0] == 0 ? 0 : (s[0] - e[(2 * i + 2) % 8]) / e1e2[0];
			float lambda12 = e1e2[1] == 0 ? 0 : (s[1] - e[(2 * i + 3) % 8]) / e1e2[1];
			float lambda21 = e3e2[0] == 0 ? 0 : (s[2] - e[(2 * i + 6) % 8]) / e3e2[0];
			float lambda22 = e3e2[1] == 0 ? 0 : (s[3] - e[(2 * i + 7) % 8]) / e3e2[1];
			
			// test that 0 <= lambda <= 1
			// if it is, test distance from edge to S and to Q
			if ((lengthEq <= lengthES0 && lambda12 >= 0 && lambda12 <= 1 && lambda11 >= 0 && lambda11 <= 1) ||
					(lengthEq <= lengthES1 &&  lambda22 >= 0 && lambda22 <= 1 && lambda21 >= 0 && lambda21 <= 1)){
				float lambdas00 = eq[0] == 0 ? 0 : (s[0] - e[2 * i    ]) / eq[0];
				float lambdas01 = eq[1] == 0 ? 0 : (s[1] - e[2 * i + 1]) / eq[1];
				float lambdas10 = eq[0] == 0 ? 0 : (s[2] - e[2 * i    ]) / eq[0];
				float lambdas11 = eq[1] == 0 ? 0 : (s[3] - e[2 * i + 1]) / eq[1];
				
				if ((lambdas00 >= 0 && lambdas01 >= 0) ||
						(lambdas10 >= 0 && lambdas11 >= 0)){
					Log.v(TAG, "Point lies in quad. Detected from edge " + e[2*i] + "/" + e[2*i+1] + ", distance of " + tag + " to origin: " + dVertical);
					return dVertical;
				}
			}
		}
		
		// point doesnt lie in quad on plane
		// compute dHorizontal
		float dHorizontal = Float.MIN_VALUE;		// distance from p on quad area to quad
		
		int closestEdgeIndex = 0;
		float closestEdgeDistance = Float.MAX_VALUE;
		
		for (int i = 1; i < 4; i++){
			final float currentDistance = (float)Math.sqrt(Math.pow(e[2*i] - q[0], 2) + Math.pow(e[2*i + 1] - q[1], 2));
			
			if (currentDistance < closestEdgeDistance)
				closestEdgeIndex = i;
		}
		
		Log.i(TAG, "Point doesnt lie in quad. Closest edge to quad is e" + closestEdgeIndex + " (" + e[2*closestEdgeIndex] + "/" + e[2*closestEdgeIndex+1] + ")");
		final float distanceClosestToQ = (float)Math.sqrt(Math.pow(e[2*closestEdgeIndex] - q[0], 2) + Math.pow(e[2*closestEdgeIndex + 1] - q[1], 2));
		final float distanceClosestToNext = (float)Math.sqrt(Math.pow(e[2*closestEdgeIndex] - e[(2*closestEdgeIndex + 2) % 8], 2) 
				+ Math.pow(e[2*closestEdgeIndex + 1] - e[(2*closestEdgeIndex + 3) % 8], 2));
		final float distanceNextToQ = (float)Math.sqrt(Math.pow(e[(2*closestEdgeIndex + 2) % 8] - q[0], 2) + Math.pow(e[(2*closestEdgeIndex + 3) % 8] - q[1], 2));
		final float distanceClosestToPrevious = (float)Math.sqrt(Math.pow(e[2*closestEdgeIndex] - e[(2*closestEdgeIndex + 6) % 8], 2) 
				+ Math.pow(e[2*closestEdgeIndex + 1] - e[(2*closestEdgeIndex + 7) % 8], 2));
		final float distancePreviousToQ = (float)Math.sqrt(Math.pow(e[(2*closestEdgeIndex + 6) % 8] - q[0], 2) + Math.pow(e[(2*closestEdgeIndex + 7) % 8] - q[1], 2));
		
		final float[] vectorToQ = new float[]{ q[0] - e[2*closestEdgeIndex], q[1] - e[2*closestEdgeIndex + 1]};
		
		if (distanceClosestToNext >= distanceNextToQ){
			// calculate distance between edge and q with sin(angle) to next edge
			final float[] vectorToNext = new float[]{ e[(2*closestEdgeIndex) % 8] - e[(2*closestEdgeIndex + 2) % 8], e[(2*closestEdgeIndex + 1) % 8] - e[(2*closestEdgeIndex + 3) % 8]};
			final float angle = (float) Math.acos((vectorToQ[0] * vectorToNext[0] + vectorToQ[1] * vectorToNext[1]) /
					(Math.sqrt((vectorToQ[0] * vectorToQ[0] + vectorToQ[1] * vectorToQ[1]) * (vectorToNext[0] * vectorToNext[0] + 
							vectorToNext[1] * vectorToNext[1]))));
			
			dHorizontal = (float) Math.sin(angle) * distanceClosestToQ;
			Log.d(TAG, "Point is closest to the edge to the next edge");
		}else if (distanceClosestToPrevious >= distancePreviousToQ){
			// calculate distance between edge and q with sin(angle) to previous edge
			final float[] vectorToPrevious = new float[]{ e[(2*closestEdgeIndex) % 8] - e[(2*closestEdgeIndex + 6) % 8], e[(2*closestEdgeIndex + 1) % 8] - e[(2*closestEdgeIndex + 7) % 8]};
			final float angle = (float) Math.acos((vectorToQ[0] * vectorToPrevious[0] + vectorToQ[1] * vectorToPrevious[1]) /
					(Math.sqrt((vectorToQ[0] * vectorToQ[0] + vectorToQ[1] * vectorToQ[1]) * (vectorToPrevious[0] * vectorToPrevious[0] + 
							vectorToPrevious[1] * vectorToPrevious[1]))));
			
			dHorizontal = (float) Math.sin(angle) * distanceClosestToQ;
			Log.d(TAG, "Point is closest to the edge to the previous edge");
		}else{
			// calculate distance from edge to q
			dHorizontal = distanceClosestToQ;
			Log.d(TAG, "Point is closest to the vertex");
		}
		
		Log.w(TAG, "dVert=" + dVertical + ", dhor=" + dHorizontal + ", total=" + Math.sqrt(dVertical * dVertical + dHorizontal * dHorizontal));
		return (float) Math.sqrt(dVertical * dVertical + dHorizontal * dHorizontal);
	}

	public boolean isPointInQuad(final IVector p) {
		return getDistanceToPoint(p) == 0f;
	}

	@Override
	protected float[] getColorData(final float[] color) {
		final float[] result = new float[6 * color.length * (mBothSides ? 2 : 1)];

		for (int i = 0; i < 6 * (mBothSides ? 2 : 1); i++)
			System.arraycopy(color, 0, result, i * color.length, color.length);

		return result;
	}
}
