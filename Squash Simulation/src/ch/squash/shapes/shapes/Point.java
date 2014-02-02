package ch.squash.shapes.shapes;

import android.opengl.GLES20;
import ch.squash.shapes.common.AbstractShape;

public class Point extends AbstractShape {
	@Override
	protected float[] getColorData(final float[] color) {
		return new float[] {1, 1, 1, 1};
	}

	public Point(final String tag, final float x, final float y, final float z) {
		super(tag, x, y, z, new float[] { 0, 0, 0 }, null);

		initialize(GLES20.GL_POINTS, SolidType.NONE, null);
	}
}
