package ch.squash.simulation.shapes.shapes;

import android.opengl.GLES20;
import ch.squash.simulation.shapes.common.AbstractShape;

public class DottedLine extends AbstractShape {
	// only to be used during initialization!!!
	private static int mIterations;

	// constants
	private static final int COORDS_PER_ITERATION = 18;

	public DottedLine(final String tag, final float startx, final float starty,
			final float startz, final float endx, final float endy,
			final float endz, final float dotInterval, final float[] color) {
		super(tag, 0, 0, 0, getVertices(startx, starty, startz, endx, endy,
				endz, dotInterval), color);

		initialize(GLES20.GL_LINES, SolidType.NONE, null);
	}

	private static float[] getVertices(final float startx, final float starty,
			final float startz, final float endx, final float endy, final float endz, final float dotInterval) {
		final float dx = endx - startx;
		final float dy = endy - starty;
		final float dz = endz - startz;

		mIterations = (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) / dotInterval);

		float[] vertices = new float[COORDS_PER_ITERATION * (mIterations + 1) - 6];

		int index = 0;

		for (int i = 0; i <= mIterations; i++) {
			if (i > 0) {
				vertices[index++] = startx + i * dx / mIterations;
				vertices[index++] = starty + i * dy / mIterations;
				vertices[index++] = startz + i * dz / mIterations;
			}

			vertices[index++] = startx + i * dx / mIterations;
			vertices[index++] = starty + i * dy / mIterations;
			vertices[index++] = startz + i * dz / mIterations;

			vertices[index++] = startx + i * dx / mIterations + 0.1f;
			vertices[index++] = starty + i * dy / mIterations + 0.1f;
			vertices[index++] = startz + i * dz / mIterations + 0.0f;

			vertices[index++] = startx + i * dx / mIterations;
			vertices[index++] = starty + i * dy / mIterations;
			vertices[index++] = startz + i * dz / mIterations;

			vertices[index++] = startx + i * dx / mIterations - 0.0f;
			vertices[index++] = starty + i * dy / mIterations - 0.1f;
			vertices[index++] = startz + i * dz / mIterations - 0.1f;

			if (i < mIterations) {
				vertices[index++] = startx + i * dx / mIterations;
				vertices[index++] = starty + i * dy / mIterations;
				vertices[index++] = startz + i * dz / mIterations;
			}
		}

		return vertices;
	}

	@Override
	protected float[] getColorData(final float[] color) {
		final float[] result = new float[(COORDS_PER_ITERATION
				* (mIterations + 1) - 6)
				* color.length];

		for (int i = 0; i < COORDS_PER_ITERATION * (mIterations + 1) - 6; i++)
			System.arraycopy(color, 0, result, i * color.length, color.length);

		return result;
	}
}
