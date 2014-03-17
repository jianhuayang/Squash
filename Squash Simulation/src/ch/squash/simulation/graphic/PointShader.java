package ch.squash.simulation.graphic;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class PointShader extends Shader {
	// static
	private static PointShader mInstance;

	// private data
	private int mMVPMatrixHandle;
	private int mPositionHandle;
	
	// static access
	protected static PointShader getInstance(){
		if (mInstance == null)
			mInstance = new PointShader();
		
		return mInstance;
	}

	private PointShader(){
		super();
	}  
	
	// access for superclass
	protected void apply() {    
        GLES20.glUseProgram(mProgramHandle);
        
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");

        // Pass in the position.
		GLES20.glVertexAttrib3f(mPositionHandle, mRenderer.mLightPosInModelSpace[0], mRenderer.mLightPosInModelSpace[1], mRenderer.mLightPosInModelSpace[2]);
	
		// Since we are not using a buffer object, disable vertex arrays for this attribute.
	    GLES20.glDisableVertexAttribArray(mPositionHandle);  
		
		// Pass in the transformation matrix.
		Matrix.multiplyMM(mMVPMatrix, 0, mRenderer.mViewMatrix, 0, mRenderer.mLightModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mRenderer.mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
	}

	@Override
	protected String getVertexShader() {
		return  "uniform mat4 u_MVPMatrix;      \n"		
              +	"attribute vec4 a_Position;     \n"		
              + "void main()                    \n"
              + "{                              \n"
              + "   gl_Position = u_MVPMatrix   \n"
              + "               * a_Position;   \n"
              + "   gl_PointSize = 5.0;         \n"
              + "}                              \n";
	}

	@Override
	protected String getFragmentShader() {
        return	"precision mediump float;       \n"					          
              + "void main()                    \n"
              + "{                              \n"
              + "   gl_FragColor = vec4(1.0,    \n" 
              + "   1.0, 1.0, 1.0);             \n"
              + "}                              \n";
	}

	@Override
	protected String[] getAttributes() {
		return new String[] {"a_Position"};
	}
}
