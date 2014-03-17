package ch.squash.simulation.graphic;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class LightShader extends Shader {
	// static
	private static LightShader mInstance;

	// private data
	private int mMVPMatrixHandle;
	private int mMVMatrixHandle;
	private int mPositionHandle;
	private int mColorHandle;
	private int mLightPosHandle;
	private int mNormalHandle;
	
	// static access
	protected static LightShader getInstance(){
		if (mInstance == null)
			mInstance = new LightShader();
		
		return mInstance;
	}

	// ctor
	private LightShader(){
		super();
		
        // Set program handles. These will later be used to pass in values to the program.
		// maybe they will have to be moved to draw() which is called on every frame
        GLES20.glUseProgram(mProgramHandle);
        
        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix"); 
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal"); 
	}
	
	// access for superclass
	protected void apply(final float[] modelMatrix, final FloatBuffer positionBuffer,
			final FloatBuffer colorBuffer, final FloatBuffer normalBuffer) {
		// Pass in the position information
		positionBuffer.position(0);		
        GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false,
        		0, positionBuffer);        
        GLES20.glEnableVertexAttribArray(mPositionHandle);        
        
        // Pass in the color information
        colorBuffer.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false,
        		0, colorBuffer);        
        GLES20.glEnableVertexAttribArray(mColorHandle);
        
        // Pass in the normal information
        normalBuffer.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, 
        		0, normalBuffer);
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        
		// This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mRenderer.mViewMatrix, 0, modelMatrix, 0);   
        
        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);                
        
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mRenderer.mProjectionMatrix, 0, mMVPMatrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        
        // Pass in the light position in eye space.        
        GLES20.glUniform3f(mLightPosHandle, mRenderer.mLightPosInEyeSpace[0], mRenderer.mLightPosInEyeSpace[1], mRenderer.mLightPosInEyeSpace[2]);
	}
	
	protected static void destroy(){
		mInstance = null;
	}
	
	// implemented methods
	@Override
	protected String[] getAttributes(){
		return new String[] {"a_Position",  "a_Color", "a_Normal"};
	}
	
	
	@Override
	protected String getVertexShader() {
		return 		"uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.
				  + "uniform mat4 u_MVMatrix;       \n"		// A constant representing the combined model/view matrix.
		  			
				  + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
				  + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.
				  + "attribute vec3 a_Normal;       \n"		// Per-vertex normal information we will pass in.
				  
				  + "varying vec3 v_Position;       \n"		// This will be passed into the fragment shader.
				  + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.
				  + "varying vec3 v_Normal;         \n"		// This will be passed into the fragment shader.
				  
				// The entry point for our vertex shader.  
				  + "void main()                                                \n" 	
				  + "{                                                          \n"
				// Transform the vertex into eye space.
				  + "   v_Position = vec3(u_MVMatrix * a_Position);             \n"
				// Pass through the color.
				  + "   v_Color = a_Color;                                      \n"
				// Transform the normal's orientation into eye space.
				  + "   v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));      \n"
				// gl_Position is a special variable used to store the final position.
				// Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
				  + "   gl_Position = u_MVPMatrix * a_Position;                 \n"      		  
				  + "}                                                          \n";      
	}

	@Override
	protected String getFragmentShader() {
		return	"precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a 
														// precision in the fragment shader.
			  + "uniform vec3 u_LightPos;       \n"	    // The position of the light in eye space.
			  
			  + "varying vec3 v_Position;		\n"		// Interpolated position for this fragment.
			  + "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the 
			  											// triangle per fragment.
			  + "varying vec3 v_Normal;         \n"		// Interpolated normal for this fragment.
			  
			// The entry point for our fragment shader.
			  + "void main()                    \n"		
			  + "{                              \n"
			// Will be used for attenuation.
			  + "   float distance = length(u_LightPos - v_Position);                  \n"
			// Get a lighting direction vector from the light to the vertex.
			  + "   vec3 lightVector = normalize(u_LightPos - v_Position);             \n" 	
			// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
			// pointing in the same direction then it will get max illumination.
			  + "   float diffuse = max(dot(v_Normal, lightVector), 0.1);              \n" 	  		  													  
			// Add attenuation. 
			  + "   diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));  \n"
			// Multiply the color by the diffuse illumination level to get final output color.
			  + "   gl_FragColor = v_Color * diffuse;                                  \n"		
			  + "}                                                                     \n";	
	}
}
