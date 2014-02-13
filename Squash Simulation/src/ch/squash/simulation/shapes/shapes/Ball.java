package ch.squash.simulation.shapes.shapes;

import android.opengl.GLES20;
import android.util.Log;
import ch.squash.simulation.shapes.common.AbstractShape;
import ch.squash.simulation.shapes.common.IVector;
import ch.squash.simulation.shapes.common.Movable;
import ch.squash.simulation.shapes.common.PhysicalVector;
import ch.squash.simulation.shapes.common.Vector;

public class Ball extends AbstractShape {
	private final static String TAG = Ball.class.getSimpleName();

	private final float mRadius;

	private static int mEdges;
	private static int mLevels;

	public final float FRICTION_CONSTANT;
	private final static float C_W = 0.45f;
	
	public float getRadius() {
		return mRadius;
	}

	public Ball(final String tag, final float x, final float y, final float z,
			final float radius, final int edges, final float[] color) {
		super(tag, x, y, z, getVertices(radius, edges), color);

		mRadius = radius;
		
		// air friction: F = 1/2 * rho * c_w * A * v^2
		// constant: 1/2 * rho * c_w * A
		FRICTION_CONSTANT = (float) (0.5 * 1.293 * C_W * Math.PI * radius * radius);

		initialize(GLES20.GL_TRIANGLES, SolidType.SPHERE, new Movable(this,
				new float[] { x, y, z }));

		final IVector position = new Vector(x, y, z);
		for (final PhysicalVector v : mMovable.vectorArrows) {
			v.moveTo(position);
		}
	}

	private static void prepareEdgeCount() {
		mLevels = mEdges / 4;
		int vertexCount = 2 * (mLevels - 1) * mEdges * 6 + 6 * mEdges;

		// ensure that there won't be too many vertices
		while (vertexCount > Short.MAX_VALUE) {
			mEdges /= 2;
			mLevels = mEdges / 4;
			vertexCount = 2 * (mLevels - 1) * mEdges * 6 + 6 * mEdges;
			Log.w(TAG, "Edges halved");
		}

		Log.d(TAG, "edges: " + mEdges + ", levels: " + mLevels);
	}

	private static float getRad(final float radius, final int i,
			final float dz, final float zOffset, final boolean reversing) {
		if (reversing)
			return (float) Math.sqrt(radius * radius
					- Math.pow((mLevels - i) * dz + zOffset, 2));
		else
			return (float) Math.sqrt(radius * radius
					- Math.pow((i) * dz + zOffset, 2));
	}

	private static float[] getVertices(final float radius, final int edges) {
		mEdges = edges;
		prepareEdgeCount();
		// prepare variables
		float[] vertices = new float[(2 * (mLevels - 1) * mEdges * 6 + 6 * mEdges) * 3];

		final float degrees = 360f / mEdges;
		final float dz = radius / mLevels;
		final float zOffset = -radius;

		int vertIndex = 0;
		boolean reversing = false;

		float rad = 0;
		float lastRad = 0;

		// loop over levels
		for (int i = 1; i <= mLevels; i++) {
			lastRad = rad;
			// calculate this level's radius
			rad = getRad(radius, i, dz, zOffset, reversing);

			// loop over edges on current level
			for (int j = 0; j < mEdges; j++) {
				if (reversing) {
					vertices[vertIndex++] = 0 + (float) (rad * Math
							.cos((degrees / 2 + (j - 0) * degrees) / 180
									* Math.PI));
					vertices[vertIndex++] = 0 + (float) (rad * Math
							.sin((degrees / 2 + (j - 0) * degrees) / 180
									* Math.PI));
					vertices[vertIndex++] = 0 + i * dz;

					vertices[vertIndex++] = 0 + (float) (rad * Math
							.cos((degrees / 2 + (j - 1) * degrees) / 180
									* Math.PI));
					vertices[vertIndex++] = 0 + (float) (rad * Math
							.sin((degrees / 2 + (j - 1) * degrees) / 180
									* Math.PI));
					vertices[vertIndex++] = 0 + i * dz;

					vertices[vertIndex++] = 0 + (float) (lastRad * Math
							.cos((degrees / 2 + (j - 1) * degrees) / 180
									* Math.PI));
					vertices[vertIndex++] = 0 + (float) (lastRad * Math
							.sin((degrees / 2 + (j - 1) * degrees) / 180
									* Math.PI));
					vertices[vertIndex++] = 0 + (i - 1) * dz;

					vertices[vertIndex++] = 0 + (float) (lastRad * Math
							.cos((degrees / 2 + (j - 1) * degrees) / 180
									* Math.PI));
					vertices[vertIndex++] = 0 + (float) (lastRad * Math
							.sin((degrees / 2 + (j - 1) * degrees) / 180
									* Math.PI));
					vertices[vertIndex++] = 0 + (i - 1) * dz;

					vertices[vertIndex++] = 0 + (float) (lastRad * Math
							.cos((degrees / 2 + (j - 0) * degrees) / 180
									* Math.PI));
					vertices[vertIndex++] = 0 + (float) (lastRad * Math
							.sin((degrees / 2 + (j - 0) * degrees) / 180
									* Math.PI));
					vertices[vertIndex++] = 0 + (i - 1) * dz;

					vertices[vertIndex++] = 0 + (float) (rad * Math
							.cos((degrees / 2 + (j - 0) * degrees) / 180
									* Math.PI));
					vertices[vertIndex++] = 0 + (float) (rad * Math
							.sin((degrees / 2 + (j - 0) * degrees) / 180
									* Math.PI));
					vertices[vertIndex++] = 0 + i * dz;

					if (i == mLevels - 1) {
						vertices[vertIndex++] = 0;
						vertices[vertIndex++] = 0;
						vertices[vertIndex++] = 0 - zOffset;

						vertices[vertIndex++] = 0 + (float) (rad * Math
								.cos((degrees / 2 + j * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (float) (rad * Math
								.sin((degrees / 2 + j * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + i * dz;

						vertices[vertIndex++] = 0 + (float) (rad * Math
								.cos((degrees / 2 + (j + 1) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (float) (rad * Math
								.sin((degrees / 2 + (j + 1) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + i * dz;
					}
				} else {
					if (i == 1) {
						vertices[vertIndex++] = 0;
						vertices[vertIndex++] = 0;
						vertices[vertIndex++] = 0 + zOffset;

						vertices[vertIndex++] = 0 + (float) (rad * Math
								.cos((degrees / 2 + (j + 1) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (float) (rad * Math
								.sin((degrees / 2 + (j + 1) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + i * dz + zOffset;

						vertices[vertIndex++] = 0 + (float) (rad * Math
								.cos((degrees / 2 + j * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (float) (rad * Math
								.sin((degrees / 2 + j * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + i * dz + zOffset;
					} else {
						vertices[vertIndex++] = 0 + (float) (rad * Math
								.cos((degrees / 2 + (j - 0) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (float) (rad * Math
								.sin((degrees / 2 + (j - 0) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + i * dz + zOffset;

						vertices[vertIndex++] = 0 + (float) (rad * Math
								.cos((degrees / 2 + (j - 1) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (float) (rad * Math
								.sin((degrees / 2 + (j - 1) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + i * dz + zOffset;

						vertices[vertIndex++] = 0 + (float) (lastRad * Math
								.cos((degrees / 2 + (j - 1) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (float) (lastRad * Math
								.sin((degrees / 2 + (j - 1) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (i - 1) * dz + zOffset;

						vertices[vertIndex++] = 0 + (float) (lastRad * Math
								.cos((degrees / 2 + (j - 1) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (float) (lastRad * Math
								.sin((degrees / 2 + (j - 1) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (i - 1) * dz + zOffset;

						vertices[vertIndex++] = 0 + (float) (lastRad * Math
								.cos((degrees / 2 + (j - 0) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (float) (lastRad * Math
								.sin((degrees / 2 + (j - 0) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (i - 1) * dz + zOffset;

						vertices[vertIndex++] = 0 + (float) (rad * Math
								.cos((degrees / 2 + (j - 0) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + (float) (rad * Math
								.sin((degrees / 2 + (j - 0) * degrees) / 180
										* Math.PI));
						vertices[vertIndex++] = 0 + i * dz + zOffset;
					}
				}
			}
			// did we reach the middle?
			if (i == mLevels && !reversing) {
				i = 0;
				reversing = true;
			}

			// are we done?
			if (i == mLevels - 1 && reversing) {
				break;
			}
		}

		Log.d(TAG, "vertices: " + (vertIndex / 3f));
		return vertices;
	}

	@Override
	protected float[] getColorData(final float[] color) {
		final int vertices = 2 * (mEdges / 4 - 1) * mEdges * 6 + 6 * mEdges;
		final float[] result = new float[vertices * color.length];

		for (int i = 0; i < vertices; i++)
			System.arraycopy(color, 0, result, i * color.length, color.length);

		return result;
	}
}
