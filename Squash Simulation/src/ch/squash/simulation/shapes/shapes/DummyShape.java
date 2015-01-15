package ch.squash.simulation.shapes.shapes;

import ch.squash.simulation.shapes.common.AbstractShape;

public class DummyShape extends AbstractShape {
	private final static String TAG = DummyShape.class.getSimpleName();
	
	public DummyShape() {
		super(null, 0, 0, 0, null);
		
		initialize(new float[0], new float[0], new float[0], -1, null, null);
	}

	@Override
	protected String getShapeTag() {
		return TAG;
	}
}
