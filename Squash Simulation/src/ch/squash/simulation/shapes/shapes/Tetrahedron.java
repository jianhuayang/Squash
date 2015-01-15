package ch.squash.simulation.shapes.shapes;

import android.opengl.GLES20;
import ch.squash.simulation.graphic.ShaderType;
import ch.squash.simulation.shapes.common.AbstractShape;
import ch.squash.simulation.shapes.common.SolidType;

public class Tetrahedron extends AbstractShape {

	private final static String TAG = Tetrahedron.class.getSimpleName();
	
	public Tetrahedron(final String tag, final float x, final float y, final float z, final float edge){
		super(tag, x, y, z, ShaderType.LIGHT);

		initialize(getVertices(edge), getColorData(), getNormalData(), GLES20.GL_TRIANGLES, SolidType.NONE, null);
	}
	
	private float[] getVertices(final float edge){
		final float halfEdge = edge / 2;
		final float radius = (float) (Math.sqrt(3) / 3 * edge);
		final float bottomHalf = (float) (Math.sqrt(Math.pow(radius, 2) - Math.pow(halfEdge, 2)));
		final float topHalf = radius;
		
		// X, Y, Z
		return new float[]{
				// Front face
				0 - halfEdge, 0 - bottomHalf, 0 + bottomHalf,
				0 + halfEdge, 0 - bottomHalf, 0 + bottomHalf,
				0 + 0,		  0 + topHalf, 0 + 0,
				
				// Right face
				0 + halfEdge, 0 - bottomHalf, 0 + bottomHalf,
				0 + 0,		  0 - bottomHalf, 0 - topHalf,
				0 + 0, 		  0 + topHalf, 0 + 0,

				// Left face
				0 + 0,		  0 + topHalf, 0 + 0,
				0 + 0,		  0 - bottomHalf, 0 - topHalf,
				0 - halfEdge, 0 - bottomHalf, 0 + bottomHalf,
				
				// Bottom face
				0 + halfEdge, 0 - bottomHalf, 0 + bottomHalf,
				0 - halfEdge, 0 - bottomHalf, 0 + bottomHalf,
				0 + 0,		  0 - bottomHalf, 0 - topHalf,
		};
	}

	private float[] getColorData() {
	// R, G, B, A
	return new float[] 	{				
			// Front face (red)
			1.0f, 0.0f, 0.0f, 1.0f,				
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			
			// Right face (green)
			0.0f, 1.0f, 0.0f, 1.0f,				
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			
			// Left face (blue)
			0.0f, 0.0f, 1.0f, 1.0f,				
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			
			// Bottom face (white)
			1.0f, 1.0f, 1.0f, 1.0f,				
			1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f, 1.0f
		};
	}
	
	private float[] getNormalData(){
		return new float[] {
			// Front face
			0.000f, 0.316f, 0.949f,
			0.000f, 0.316f, 0.949f,
			0.000f, 0.316f, 0.949f,
				
			// Right face
			0.739f, 0.213f, -0.640f,
			0.739f, 0.213f, -0.640f,
			0.739f, 0.213f, -0.640f,
			
			// Left face
			-0.822f, 0.316f,- 0.474f,
			-0.822f, 0.316f, -0.474f,
			-0.822f, 0.316f, -0.474f,
				
			// Bottom face
			0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f,				
		};
	}

	@Override
	protected String getShapeTag() {
		return TAG;
	}
}
