package ch.squash.simulation.shapes.common;

import android.opengl.GLES20;
import android.util.Log;

public class Trace extends AbstractShape {
	private final static String TAG = Trace.class.getSimpleName();
	public final static float MIN_DISTANCE_OF_POINTS = 0.02f;
	public final static int MAX_TRACE_SEGMENTS = 50;
	private final IVector[] mPoints = new IVector[MAX_TRACE_SEGMENTS + 1];
	
	private Trace mNextTrace;
	
	private final float[] mColor;
	
	private int mTraceIndex = 0;
	
	public void addPoint(IVector point){		
		if (mNextTrace != null){
			mNextTrace.addPoint(point);
			return;
		}
		
		if (mTraceIndex == 0){
			// always add new point into empty list
			mPoints[mTraceIndex++] = point;
			return;
		}
		else if (mTraceIndex == mPoints.length){
			// start new trace if current list is full
			mNextTrace = new Trace(tag, 0, 0, 0, null, mColor);
			mNextTrace.addPoint(point);
			Log.w(TAG, "Adding new trace to " + tag);
		}
		else if (AbstractShape.getPointPointDistance(mPoints[mTraceIndex - 1].getDirection(), point.getDirection()) >= MIN_DISTANCE_OF_POINTS)
			// add new point to current list if it's enough far away from last point in list
			mPoints[mTraceIndex] = point;
		else
			// return if nothing else must be done
			return;
		
		final float[] data = new float[]{ mPoints[mTraceIndex -1].getX(), mPoints[mTraceIndex -1].getY(),
				mPoints[mTraceIndex -1].getZ(), point.getX(), point.getY(), point.getZ() };
		
		mPositions.position(6 * (mTraceIndex++));
		mPositions.put(data);
		mPositions.position(0);
	}

	public Trace(String tag, float x, float y, float z, float[] mVertexData,
			float[] color) {
		super(tag, 0, 0, 0, new float[MAX_TRACE_SEGMENTS * 2 * 3 * 2], color);
		
		mColor = color;
		
		initialize(GLES20.GL_LINES, SolidType.NONE, null);
	}

	@Override
	protected float[] getColorData(float[] color) {
		final float[] result = new float[(MAX_TRACE_SEGMENTS + 2) * 2 * color.length];

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
