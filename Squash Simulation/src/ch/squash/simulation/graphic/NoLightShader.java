package ch.squash.simulation.graphic;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

final class NoLightShader extends Shader {
	// static
	private static NoLightShader mInstance;

	// private data
	private int mMVPMatrixHandle;
	private int mPositionHandle;
	private int mColorHandle;
	
	// static access
	protected static NoLightShader getInstance(){
		if (mInstance == null)
			mInstance = new NoLightShader();
		
		return mInstance;
	}

	// ctor
	private NoLightShader(){
		super();
		
        // Set program handles. These will later be used to pass in values to the program.
		// maybe they will have to be moved to draw() which is called on every frame
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");        
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");        
	}
	
	// access for superclass
	protected void apply(final float[] modelMatrix, final FloatBuffer positionBuffer, final FloatBuffer colorBuffer) {
        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(mProgramHandle);   

		// Pass in the position information
        positionBuffer.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle,
				POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, positionBuffer);
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Pass in the color information
		colorBuffer.position(0);
		GLES20.glVertexAttribPointer(mColorHandle,
				COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, 0, colorBuffer);
		GLES20.glEnableVertexAttribArray(mColorHandle);
		

		// This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mRenderer.mViewMatrix, 0, modelMatrix, 0);
        
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mRenderer.mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
	}
	
	protected static void destroy(){
		mInstance = null;
	}
	
	// implemented methods
	@Override
	protected String[] getAttributes(){
		return new String[] { "a_Position", "a_Color" };
	}

	@Override
	protected String getVertexShader(){
		return "uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.
				
				  + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
				  + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.			  
				  
				  + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.
				  
				  + "void main()                    \n"		// The entry point for our vertex shader.
				  + "{                              \n"
				  + "   v_Color = a_Color;          \n"		// Pass the color through to the fragment shader. 
				  											// It will be interpolated across the triangle.
				  + "   gl_Position = u_MVPMatrix   \n" 	// gl_Position is a special variable used to store the final position.
				  + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in 			                                            			 
				  + "}                              \n";    // normalized screen coordinates.
	}
	
	@Override
	protected String getFragmentShader(){
		return "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a 
								// precision in the fragment shader.				
				+ "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the 
									// triangle per fragment.			  
				+ "void main()                    \n"		// The entry point for our fragment shader.
				+ "{                              \n"
				+ "   gl_FragColor = v_Color;     \n"		// Pass the color directly through the pipeline.		  
				+ "}                              \n";		
	}
}
