package ch.squash.shapes.shapes;

import ch.squash.shapes.common.AbstractShape;

public class DummyShape extends AbstractShape {
	public DummyShape() {
		super(null, 0, 0, 0, new float[0], null);
		
		initialize(-1, null, null);
	}

	@Override
	protected float[] getColorData(final float[] color) {
		return new float[0];
	}

}
