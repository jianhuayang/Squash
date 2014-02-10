package ch.squash.simulation.shapes.common;

import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;

public class Trace extends AbstractShape {
	public final static int MIN_DISTANCE_OF_POINTS = 3;
	public final static int MAX_POINTS_PER_TRACE = 10;
	private final List<IVector> mPoints = new ArrayList<IVector>();
	private Trace mNextTrace;
	
	private final float mX;
	private final float mY;
	private final float mZ;
	private final float[] mColor;
	
	public void addPoint(final IVector point){
		if (mPoints.size() == 0)
			// always add new point into empty list
			mPoints.add(point);
		else if (mPoints.size() == MAX_POINTS_PER_TRACE){
			// start new trace if current list is full
			mNextTrace = new Trace(tag, mX, mY, mZ, null, mColor);
			mNextTrace.addPoint(point);
		}
		else if (AbstractShape.getPointPointDistance(mPoints.get(mPoints.size() - 1).getDirection(), point.getDirection()) >= MIN_DISTANCE_OF_POINTS)
			// add new point to current list if it's enough far away from last point in list
			mPoints.add(point);
	}

	public Trace(String tag, float x, float y, float z, float[] mVertexData,
			float[] color) {
		super(tag, x, y, z, new float[0], color);
		
		mX = x;
		mY = y;
		mZ = z;
		mColor = color;
		// TODO get points from list to vertexdata somehow

		initialize(GLES20.GL_LINES, SolidType.NONE, null);
	}

	@Override
	protected float[] getColorData(float[] color) {
		final float[] result = new float[MAX_POINTS_PER_TRACE * 3 * color.length];

		for (int i = 0; i < result.length / color.length; i++)
			System.arraycopy(color, 0, result, i * color.length, color.length);
		return result;
	}
	
	@Override
	public void draw() {
		super.draw();
		
		if (mNextTrace != null)
			mNextTrace.draw();
	}
}
