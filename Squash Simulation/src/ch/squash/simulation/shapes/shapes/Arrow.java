package ch.squash.simulation.shapes.shapes;

import android.opengl.GLES20;
import ch.squash.simulation.shapes.common.AbstractShape;
import ch.squash.simulation.shapes.common.IVector;
import ch.squash.simulation.shapes.common.Vector;

public class Arrow extends AbstractShape {
//	private final static String TAG = Arrow.class.getSimpleName();

	public Arrow(final String tag, final float startx, final float starty, final float startz, final float endx,
			final float endy, final float endz, final float[] color) {
		super(tag, startx, starty, startz, getVertices(0, 0, 0, endx - startx, endy
				- starty, endz - startz), color);

		initialize(GLES20.GL_LINES, SolidType.NONE, null);
	}
	
	public static float[] getVertices(final float startx, final float starty,
			final float startz, final float endx, final float endy, final float endz) {
		final float[] vertices = new float[10 * 3];

		final IVector v = new Vector(endx - startx, endy - starty, endz - startz).multiply(0.1f);
		
		final IVector[] o = new IVector[4];
		
		// idea for code: find 2 components with highest absolute value
		// if both are greater than null
		//		-> use formula to calculate orthos
		// else
		//		-> not sure yet...
		
		
		final IVector o0 = new Vector(v.getY(), v.getX(), -(2 * v.getX() * v.getY()) / v.getZ());
		
		
		final float mx = endx - 2 * v.getX();
		final float my = endy - 2 * v.getY();
		final float mz = endz - 2 * v.getZ();
		
		vertices[ 0] = startx;
		vertices[ 1] = starty;
		vertices[ 2] = startz;
		vertices[ 3] = endx;
		vertices[ 4] = endy;
		vertices[ 5] = endz;

		vertices[ 6] = mx;
		vertices[ 7] = my;
		vertices[ 8] = mz;
		vertices[ 9] = mx + o0.getX();
		vertices[10] = my + o0.getY();
		vertices[11] = mz + o0.getZ();
//
//		vertices[12] = endx;
//		vertices[13] = endy;
//		vertices[14] = endz;
//		vertices[15] = mx - o1.getX();
//		vertices[16] = my - o1.getY();
//		vertices[17] = mz - o1.getZ();
//
//		vertices[18] = endx;
//		vertices[19] = endy;
//		vertices[20] = endz;
//		vertices[21] = mx - o2.getX();
//		vertices[22] = my - o2.getY();
//		vertices[23] = mz - o2.getZ();
//
//		vertices[24] = endx;
//		vertices[25] = endy;
//		vertices[26] = endz;
//		vertices[27] = mx - o3.getX();
//		vertices[28] = my - o3.getY();
//		vertices[29] = mz - o3.getZ();

		
		return vertices;
	}
	
	public static float[] ggetVertices(final float startx, final float starty,
			final float startz, final float endx, final float endy, final float endz) {
		IVector dir = new Vector(endx - startx, endy - starty, endz - startz);
		
		if (dir.getX() != 0 && dir.getY() != 0 || dir.getX() != 0 && dir.getZ() != 0 || dir.getY() != 0 && dir.getZ() != 0 || startx == endx && starty == endy && startz == endz)
			return new float[] { startx, starty, startz, endx, endy, endz };

		final float dirLength = dir.getLength();
		dir = dir.getNormalizedVector();

		float parx1;
		float pary1;
		float parz1;
		float parx2;
		float pary2;
		float parz2;

		if (dir.getZ() == 0){
			parx1 = dir.getY();
			pary1 = dir.getX();
			parz1 = dir.getZ();
		}else{
			parx1 = dir.getX();
			pary1 = dir.getZ();
			parz1 = dir.getY();
		}
		if (dir.getY() == 0){
			parx2 = dir.getZ();
			pary2 = dir.getY();
			parz2 = dir.getX();
		}else{
			parx2 = dir.getX();
			pary2 = dir.getZ();
			parz2 = dir.getY();
		}
		
//		parx1 = dirz == 0 ? diry : dirx;
//		pary1 = dirz == 0 ? dirx : dirz;
//		parz1 = dirz == 0 ? dirz : diry;
//		parx2 = diry == 0 ? dirz : dirx;
//		pary2 = diry == 0 ? diry : dirz;
//		parz2 = diry == 0 ? dirx : diry;
		
//		if (diry == 0 && dirz == 0) {			// x != 0
//			parx1 = diry;
//			pary1 = dirx;
//			parz1 = dirz;
//			parx2 = dirz;
//			pary2 = diry;
//			parz2 = dirx;
//		} else if (dirx == 0 && dirz == 0) {	// y != 0
//			parx1 = diry;
//			pary1 = dirx;
//			parz1 = dirz;
//			parx2 = dirx;
//			pary2 = dirz;
//			parz2 = diry;
//		} else if (dirx == 0 && diry == 0) {	// z != 0
//			parx1 = dirx;
//			pary1 = dirz;
//			parz1 = diry;
//			parx2 = dirz;
//			pary2 = diry;
//			parz2 = dirx;
//		} else {
//			return new float[] { startx, starty, startz, endx, endy, endz };
//		}

		final float[] vertices = new float[10 * 3];

		final float arrowLength = dirLength / 8;
		final float arrowDistance = dirLength / 16;

		final float arrowx = endx - dir.getX() * arrowLength;
		final float arrowy = endy - dir.getY() * arrowLength;
		final float arrowz = endz - dir.getZ() * arrowLength;
		
		vertices[0] = startx;
		vertices[1] = starty;
		vertices[2] = startz;
		vertices[3] = endx;
		vertices[4] = endy;
		vertices[5] = endz;
		vertices[6] = arrowx + parx1 * arrowDistance;
		vertices[7] = arrowy + pary1 * arrowDistance;
		vertices[8] = arrowz + parz1 * arrowDistance;
		vertices[9] = endx;
		vertices[10] = endy;
		vertices[11] = endz;
		vertices[12] = arrowx - parx1 * arrowDistance;
		vertices[13] = arrowy - pary1 * arrowDistance;
		vertices[14] = arrowz - parz1 * arrowDistance;
		vertices[15] = endx;
		vertices[16] = endy;
		vertices[17] = endz;
		vertices[18] = arrowx + parx2 * arrowDistance;
		vertices[19] = arrowy + pary2 * arrowDistance;
		vertices[20] = arrowz + parz2 * arrowDistance;
		vertices[21] = endx;
		vertices[22] = endy;
		vertices[23] = endz;
		vertices[24] = arrowx - parx2 * arrowDistance;
		vertices[25] = arrowy - pary2 * arrowDistance;
		vertices[26] = arrowz - parz2 * arrowDistance;
		vertices[27] = endx;
		vertices[28] = endy;
		vertices[29] = endz;

		return vertices;
	}

	@Override
	protected float[] getColorData(final float[] color) {
		final float[] result = new float[10 * 3 * color.length];

		for (int i = 0; i < result.length / color.length; i++)
			System.arraycopy(color, 0, result, i * color.length, color.length);
		return result;
	}
}
