package ch.squash.simulation.graphic;

import java.nio.FloatBuffer;
import android.opengl.GLES20;
import android.util.Log;

public abstract class Shader {
	// FINAL STATIC
	private final static String TAG = Shader.class.getSimpleName();
	
	protected final static SquashRenderer mRenderer = SquashRenderer.getInstance();

	public final static int POSITION_DATA_SIZE = 3;
	public final static int COLOR_DATA_SIZE = 4;
	public final static int NORMAL_DATA_SIZE = 3;

	// instance variables - variables that each shader needs
	protected final int mProgramHandle;
	protected final float[] mMVPMatrix = new float[16];
	

	// ctor
	protected Shader(){
		final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, getVertexShader());
		final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader());
		mProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, getAttributes());
	}
	
	// public access
	public static void applyNoLight(final float[] modelMatrix, final FloatBuffer positionBuffer, final FloatBuffer colorBuffer){
		NoLightShader.getInstance().apply(modelMatrix, positionBuffer, colorBuffer);
	}
	
	public static void applyLight(final float[] modelMatrix, final FloatBuffer positionBuffer,
			final FloatBuffer colorBuffer, final FloatBuffer normalBuffer){
		LightShader.getInstance().apply(modelMatrix, positionBuffer, colorBuffer, normalBuffer);
	}
	
	public static void destroyShaders(){
		NoLightShader.destroy();
	}
	
	// abstract methods
	protected abstract String getVertexShader();
	
	protected abstract String getFragmentShader();
	
	protected abstract String[] getAttributes();

	
	/** 
	 * Helper function to compile a shader.
	 * 
	 * @param shaderType The shader type.
	 * @param shaderSource The shader source code.
	 * @return An OpenGL handle to the shader.
	 */
	protected static int compileShader(final int shaderType, final String shaderSource) 
	{
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle != 0) 
		{
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);

			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) 
			{
				Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0)
		{			
			throw new RuntimeException("Error creating shader.");
		}
		
		return shaderHandle;
	}
	
	/**
	 * Helper function to compile and link a program.
	 * 
	 * @param vertexShaderHandle An OpenGL handle to an already-compiled vertex shader.
	 * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
	 * @param attributes Attributes that need to be bound to the program.
	 * @return An OpenGL handle to the program.
	 */
	protected static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) 
	{
		int programHandle = GLES20.glCreateProgram();
		
		if (programHandle != 0) 
		{
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);			

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			
			// Bind attributes
			if (attributes != null)
			{
				final int size = attributes.length;
				for (int i = 0; i < size; i++)
				{
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}						
			}
			
			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);

			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			// If the link failed, delete the program.
			if (linkStatus[0] == 0) 
			{				
				Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}
		
		if (programHandle == 0)
		{
			throw new RuntimeException("Error creating program.");
		}
		
		return programHandle;
	}
}
