package ch.squash.simulation.shapes.shapes;

import android.opengl.GLES20;
import ch.squash.simulation.graphic.ShaderType;
import ch.squash.simulation.shapes.common.AbstractShape;
import ch.squash.simulation.shapes.common.SolidType;

public class Point extends AbstractShape {
	private final static String TAG = Point.class.getSimpleName();
	
	public Point(final String tag, final float x, final float y, final float z) {
		super(tag, x, y, z, ShaderType.POINT);

		initialize(new float[] { 0, 0, 0 }, new float[0], new float[0], GLES20.GL_POINTS, SolidType.NONE, null);
	}

	@Override
	protected String getShapeTag() {
		return TAG;
	}
}
