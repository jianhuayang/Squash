package ch.squash.simulation.shapes.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.graphic.Shader;
import ch.squash.simulation.graphic.ShaderType;
import ch.squash.simulation.shapes.shapes.DummyShape;

public abstract class AbstractShape {
	// constant
	private final static String TAG = AbstractShape.class.getSimpleName();
	private static final int BYTES_PER_FLOAT = 4;
	
	// data - drawing
	protected FloatBuffer mPositions;
	protected FloatBuffer mColors;
	protected FloatBuffer mNormals;
	private int mDrawMode;
	private int mVertexCount;
	private boolean mVisible = true;
	private boolean isInitialized;
	
	// data - shape
	public final String tag;
	protected final IVector origin;
	protected final IVector location;
	private SolidType mSolidType;
	private Movable mMovable;
	public float temperature = 20;

	private final ShaderType mShaderType;
	
	// matrices
	private float[] mModelMatrix = new float[16];

	public AbstractShape(final String tag, final float x, final float y, final float z, final ShaderType type) {
		this.tag = tag;

		location = new Vector(x, y, z);
		origin = new Vector(x, y, z);

		mShaderType = ShaderType.LIGHT;
	}
	
	@SuppressWarnings("unused")
	private AbstractShape() {
		location = null;
		origin = null;
		tag = null;
		mShaderType = null;
	}

	protected void initialize(final float[] vertices, final float[] color, final float[] normal,
			final int drawMode, final SolidType type, final Movable movable){
		// required info for drawing
		mVertexCount = vertices.length / Shader.POSITION_DATA_SIZE;
		
		// Initialize the buffers.
		mPositions = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mPositions.put(vertices).position(0);

		mColors = ByteBuffer.allocateDirect(color.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mColors.put(color).position(0);

		mNormals = ByteBuffer.allocateDirect(normal.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mNormals.put(normal).position(0);

		mDrawMode = drawMode;
		mSolidType = type;
		mMovable = movable;

		if (movable != null)
			for (final PhysicalVector v : mMovable.vectorArrows)
				v.moveTo(location);
		
		isInitialized = true;
	}

	public void setVisible(final boolean visible) {
		mVisible = visible;
	}
	
	public Movable getMovable() {
		return mMovable;
	}

	public boolean isSolid() {
		return mSolidType != null;
	}

	public SolidType getSolidType(){
		return mSolidType;
	}
	
	public boolean isMovable() {
		return mMovable != null;
	}
	
	public void setNewVertices(final float[] positionData) {
		mVertexCount = positionData.length / Shader.POSITION_DATA_SIZE;
		mPositions = ByteBuffer
				.allocateDirect(positionData.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mPositions.put(positionData).position(0);
	}
	
	public void draw() {
		if (!isInitialized) {
			Log.e(TAG, "Drawing uninitialized shape: " + toString());
			return;
		}
		
		if (!mVisible || mVertexCount == 0 || this instanceof DummyShape)
			return;

		if (isMovable()){
			if (Settings.isDrawForces())
				for (final PhysicalVector v : mMovable.vectorArrows)
					v.draw();
			
			mMovable.trace.draw();
		}

		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.translateM(mModelMatrix, 0, location.getX(), location.getY(), location.getZ());
		
		switch (mShaderType){
		case NO_LIGHT:
			Shader.applyNoLight(mModelMatrix, mPositions, mColors);
			break;
		case LIGHT:
			Shader.applyLight(mModelMatrix, mPositions, mColors, mNormals);
			break;
//		case POINT:
//			break;
		default:
			Log.e(TAG, "Unknown shader type: " + mShaderType);
			break;
		}
		
		final int drawMode = Settings.getDrawMode();
		GLES20.glDrawArrays(drawMode == -1 ? mDrawMode : drawMode, 0,
				mVertexCount);
	}

	public void move(final IVector dv) {
		moveTo(location.add(dv));
	}

	public void moveTo(final IVector dv) {
		location.setDirection(dv.getX(), dv.getY(), dv.getZ());

		if (isMovable()){
			for (final PhysicalVector gs : mMovable.vectorArrows)
				gs.moveTo(dv);
			mMovable.trace.addPoint(dv);
		}
	}

	public static boolean areEqual(final float a, final float b) {
		return areEqual(a, b, 10 * Float.MIN_NORMAL);
	}
	
	public static boolean areEqual(final float a, final float b, final float epsilon) {
		final float absA = Math.abs(a);
		final float absB = Math.abs(b);
		final float diff = Math.abs(a - b);

		boolean result;
		
		if (a == b) { // shortcut, handles infinities
			result = true;
		} else if (a == 0 || b == 0 || diff < Float.MIN_NORMAL) {
			// a or b is zero or both are extremely close to it
			// relative error is less meaningful here
			result = diff < (epsilon * Float.MIN_NORMAL);
		} else { // use relative error
			result = diff / (absA + absB) < epsilon;
		}
		
		return result;
	}

	public static float getPointPointDistance(final float[] p1, final float[] p2) {
		double result = 0;
		
		if (p1.length == p2.length){
			for (int i = 0; i < p1.length; i++)
				result += Math.pow(p1[i] - p2[i], 2);
			
			result = Math.sqrt(result);
		}else{
			Log.e(TAG, "Both points must have the same amount of dimensions");
			result = -1;
		}
		
		return (float) result;
	}
	
	public IVector getLocation(){
		return location;
	}
}