package ch.squash.shapes.common;

import java.util.ArrayList;
import java.util.List;

import ch.squash.main.SquashRenderer;
import ch.squash.shapes.shapes.Quadrilateral;

public class ShapeCollection {
	public static final int OBJECT_COLLECTION_COURT = 0;

	private final List<AbstractShape> mOpaqueObjects = new ArrayList<AbstractShape>();

	private final List<AbstractShape> mTransparentObjects = new ArrayList<AbstractShape>();

	public List<AbstractShape> getOpaqueObjects() {
		return mOpaqueObjects;
	}

	public List<AbstractShape> getTransparentObjects() {
		return mTransparentObjects;
	}

	public List<AbstractShape> getAllShapes() {
		final List<AbstractShape> shapes = new ArrayList<AbstractShape>();
		shapes.addAll(mTransparentObjects);
		shapes.addAll(mOpaqueObjects);

		return shapes;
	}

	public void addObject(final AbstractShape object, final boolean transparent) {
		if (transparent)
			mTransparentObjects.add(object);
		else
			mOpaqueObjects.add(object);
	}

	public void setVisibility(final boolean visible) {
		for (final AbstractShape g : mTransparentObjects)
			g.setVisible(visible);
		for (final AbstractShape g : mOpaqueObjects)
			g.setVisible(visible);
	}

	public ShapeCollection() {
		// instantiates empty collection
	}

	// instantiates collection with one object
	public ShapeCollection(final AbstractShape object, final boolean transparent) {
		if (transparent)
			mTransparentObjects.add(object);
		else
			mOpaqueObjects.add(object);
	}

	// instantiates court
	public ShapeCollection(final int collectionId) {
		if (collectionId == OBJECT_COLLECTION_COURT) {
			// floor (opaque)
			mOpaqueObjects.add(new Quadrilateral("floor inside", new float[] {
					-3.2f, 0f, 4.26f, 3.2f, 0f, 4.26f, 3.2f, 0f, -5.49f, -3.2f,
					0f, -5.49f }, new float[] { 0.90f, 0.83f, 0.47f, 1f },
					false));
			mOpaqueObjects.add(new Quadrilateral("floor outside", new float[] {
					-3.2f, 0f, 4.26f, -3.2f, 0f, -5.49f, 3.2f, 0f, -5.49f,
					3.2f, 0f, 4.26f }, new float[] { 0.5f, 0.5f, 0.5f, 1f },
					false));

			// tin wall (opaque)
			mOpaqueObjects.add(new Quadrilateral("tin wall inside",
					new float[] { -3.2f, 0f, -5.49f, 3.2f, 0f, -5.49f, 3.2f,
							0.43f - SquashRenderer.COURT_LINE_WIDTH, -5.49f,
							-3.2f, 0.43f - SquashRenderer.COURT_LINE_WIDTH,
							-5.49f }, new float[] { 1f, 1f, 1f, 1f }, false));
			mOpaqueObjects.add(new Quadrilateral("tin wall outside",
					new float[] { 3.2f, 0f, -5.49f, -3.2f, 0f, -5.49f, -3.2f,
							0.43f, -5.49f, 3.2f, 0.43f, -5.49f }, new float[] {
							0.5f, 0.5f, 0.5f, 1f }, false));

			// walls (transparent)
			mTransparentObjects.add(new Quadrilateral("front wall inside",
					new float[] { -3.2f, 0.43f, -5.49f, 3.2f, 0.43f, -5.49f,
							3.2f, 4.57f, -5.49f, -3.2f, 4.57f, -5.49f },
					new float[] { 0.2f, 0.4f, 0.6f, 0.8f }, false));
			mTransparentObjects.add(new Quadrilateral("front wall outside",
					new float[] { 3.2f, 4.57f, -5.49f, 3.2f, 0.43f, -5.49f,
							-3.2f, 0.43f, -5.49f, -3.2f, 4.57f, -5.49f },
					new float[] { 0.2f, 0.4f, 0.6f, 0.6f }, false));
			mTransparentObjects.add(new Quadrilateral("left wall inside",
					new float[] { -3.2f, 0f, -5.49f, -3.2f, 4.57f, -5.49f,
							-3.2f, 2.13f, 4.26f, -3.2f, 0f, 4.26f },
					new float[] { 0.2f, 0.4f, 0.6f, 0.7f }, false));
			mTransparentObjects.add(new Quadrilateral("left wall outside",
					new float[] { -3.2f, 0f, -5.49f, -3.2f, 0f, 4.26f, -3.2f,
							2.13f, 4.26f, -3.2f, 4.57f, -5.49f }, new float[] {
							0.2f, 0.4f, 0.6f, 0.4f }, false));
			mTransparentObjects.add(new Quadrilateral("right wall inside",
					new float[] { 3.2f, 0f, -5.49f, 3.2f, 0f, 4.26f, 3.2f,
							2.13f, 4.26f, 3.2f, 4.57f, -5.49f }, new float[] {
							0.2f, 0.4f, 0.6f, 0.7f }, false));
			mTransparentObjects.add(new Quadrilateral("right wall outside",
					new float[] { 3.2f, 0f, -5.49f, 3.2f, 4.57f, -5.49f, 3.2f,
							2.13f, 4.26f, 3.2f, 0f, 4.26f }, new float[] {
							0.2f, 0.4f, 0.6f, 0.4f }, false));
			mTransparentObjects.add(new Quadrilateral("back wall inside",
					new float[] { 3.2f, 2.13f, 4.26f, 3.2f, 0f, 4.26f, -3.2f,
							0f, 4.26f, -3.2f, 2.13f, 4.26f }, new float[] {
							0.2f, 0.4f, 0.6f, 0.35f }, false));
			mTransparentObjects.add(new Quadrilateral("back wall outside",
					new float[] { -3.2f, 0f, 4.26f, 3.2f, 0f, 4.26f, 3.2f,
							2.13f, 4.26f, -3.2f, 2.13f, 4.26f }, new float[] {
							0.2f, 0.4f, 0.6f, 0.2f }, false));

			// lines (opaque)
			mOpaqueObjects.add(new Quadrilateral("tin line", new float[] {
					-3.2f, 0.43f - SquashRenderer.COURT_LINE_WIDTH,
					-5.49f + SquashRenderer.ONE_MM, 3.2f,
					0.43f - SquashRenderer.COURT_LINE_WIDTH,
					-5.49f + SquashRenderer.ONE_MM, 3.2f, 0.43f,
					-5.49f + SquashRenderer.ONE_MM, -3.2f, 0.43f,
					-5.49f + SquashRenderer.ONE_MM }, new float[] { 1f, 0f, 0f,
					1f }, true));
			mOpaqueObjects.add(new Quadrilateral("out line front", new float[] {
					-3.2f, 4.57f - SquashRenderer.COURT_LINE_WIDTH,
					-5.49f + SquashRenderer.ONE_MM, 3.2f,
					4.57f - SquashRenderer.COURT_LINE_WIDTH,
					-5.49f + SquashRenderer.ONE_MM, 3.2f, 4.57f,
					-5.49f + SquashRenderer.ONE_MM, -3.2f, 4.57f,
					-5.49f + SquashRenderer.ONE_MM }, new float[] { 1f, 0f, 0f,
					1f }, true));
			mOpaqueObjects.add(new Quadrilateral("service line front",
					new float[] { -3.2f,
							1.78f - SquashRenderer.COURT_LINE_WIDTH,
							-5.49f + SquashRenderer.ONE_MM, 3.2f,
							1.78f - SquashRenderer.COURT_LINE_WIDTH,
							-5.49f + SquashRenderer.ONE_MM, 3.2f, 1.78f,
							-5.49f + SquashRenderer.ONE_MM, -3.2f, 1.78f,
							-5.49f + SquashRenderer.ONE_MM }, new float[] { 1f,
							0f, 0f, 1f }, true));
			mOpaqueObjects.add(new Quadrilateral("out line left", new float[] {
					-3.2f + SquashRenderer.ONE_MM,
					2.13f - SquashRenderer.COURT_LINE_WIDTH, 4.26f,
					-3.2f + SquashRenderer.ONE_MM, 2.13f, 4.26f,
					-3.2f + SquashRenderer.ONE_MM, 4.57f, -5.49f,
					-3.2f + SquashRenderer.ONE_MM,
					4.57f - SquashRenderer.COURT_LINE_WIDTH, -5.49f },
					new float[] { 1f, 0f, 0f, 1f }, true));
			mOpaqueObjects.add(new Quadrilateral("out line right", new float[] {
					3.2f - SquashRenderer.ONE_MM,
					2.13f - SquashRenderer.COURT_LINE_WIDTH, 4.26f,
					3.2f - SquashRenderer.ONE_MM, 2.13f, 4.26f,
					3.2f - SquashRenderer.ONE_MM, 4.57f, -5.49f,
					3.2f - SquashRenderer.ONE_MM,
					4.57f - SquashRenderer.COURT_LINE_WIDTH, -5.49f },
					new float[] { 1f, 0f, 0f, 1f }, true));
			mOpaqueObjects.add(new Quadrilateral("floor line",
					new float[] { -SquashRenderer.COURT_LINE_WIDTH / 2f,
							SquashRenderer.ONE_MM, 4.26f,
							SquashRenderer.COURT_LINE_WIDTH / 2f,
							SquashRenderer.ONE_MM, 4.26f,
							SquashRenderer.COURT_LINE_WIDTH / 2f,
							SquashRenderer.ONE_MM,
							SquashRenderer.COURT_LINE_WIDTH / 2f,
							-SquashRenderer.COURT_LINE_WIDTH / 2f,
							SquashRenderer.ONE_MM,
							SquashRenderer.COURT_LINE_WIDTH / 2f },
					new float[] { 1f, 0f, 0f, 1f }, false));
			mOpaqueObjects.add(new Quadrilateral("floor line", new float[] {
					-3.2f, SquashRenderer.ONE_MM,
					SquashRenderer.COURT_LINE_WIDTH / 2f, 3.2f,
					SquashRenderer.ONE_MM,
					SquashRenderer.COURT_LINE_WIDTH / 2f, 3.2f,
					SquashRenderer.ONE_MM,
					-SquashRenderer.COURT_LINE_WIDTH / 2f, -3.2f,
					SquashRenderer.ONE_MM,
					-SquashRenderer.COURT_LINE_WIDTH / 2f }, new float[] { 1f,
					0f, 0f, 1f }, false));
			mOpaqueObjects.add(new Quadrilateral("floor line", new float[] {
					-3.2f, SquashRenderer.ONE_MM,
					1.6f + SquashRenderer.COURT_LINE_WIDTH / 2f,
					-1.6f + SquashRenderer.COURT_LINE_WIDTH / 2f,
					SquashRenderer.ONE_MM,
					1.6f + SquashRenderer.COURT_LINE_WIDTH / 2f,
					-1.6f + SquashRenderer.COURT_LINE_WIDTH / 2f,
					SquashRenderer.ONE_MM,
					1.6f + -SquashRenderer.COURT_LINE_WIDTH / 2f, -3.2f,
					SquashRenderer.ONE_MM,
					1.6f + -SquashRenderer.COURT_LINE_WIDTH / 2f },
					new float[] { 1f, 0f, 0f, 1f }, false));
			mOpaqueObjects.add(new Quadrilateral("floor line", new float[] {
					-1.6f - SquashRenderer.COURT_LINE_WIDTH / 2f,
					SquashRenderer.ONE_MM,
					1.6f - SquashRenderer.COURT_LINE_WIDTH / 2f,
					-1.6f + SquashRenderer.COURT_LINE_WIDTH / 2f,
					SquashRenderer.ONE_MM,
					1.6f - SquashRenderer.COURT_LINE_WIDTH / 2f,
					-1.6f + SquashRenderer.COURT_LINE_WIDTH / 2f,
					SquashRenderer.ONE_MM,
					+SquashRenderer.COURT_LINE_WIDTH / 2f,
					-1.6f - SquashRenderer.COURT_LINE_WIDTH / 2f,
					SquashRenderer.ONE_MM,
					+SquashRenderer.COURT_LINE_WIDTH / 2f }, new float[] { 1f,
					0f, 0f, 1f }, false));
			mOpaqueObjects.add(new Quadrilateral("floor line", new float[] {
					1.6f - SquashRenderer.COURT_LINE_WIDTH / 2f,
					SquashRenderer.ONE_MM,
					1.6f + SquashRenderer.COURT_LINE_WIDTH / 2f, 3.2f,
					SquashRenderer.ONE_MM,
					1.6f + SquashRenderer.COURT_LINE_WIDTH / 2f, 3.2f,
					SquashRenderer.ONE_MM,
					1.6f + -SquashRenderer.COURT_LINE_WIDTH / 2f,
					1.6f - SquashRenderer.COURT_LINE_WIDTH / 2f,
					SquashRenderer.ONE_MM,
					1.6f + -SquashRenderer.COURT_LINE_WIDTH / 2f },
					new float[] { 1f, 0f, 0f, 1f }, false));
			mOpaqueObjects.add(new Quadrilateral("floor line", new float[] {
					1.6f - SquashRenderer.COURT_LINE_WIDTH / 2f,
					SquashRenderer.ONE_MM,
					1.6f - SquashRenderer.COURT_LINE_WIDTH / 2f,
					1.6f + SquashRenderer.COURT_LINE_WIDTH / 2f,
					SquashRenderer.ONE_MM,
					1.6f - SquashRenderer.COURT_LINE_WIDTH / 2f,
					1.6f + SquashRenderer.COURT_LINE_WIDTH / 2f,
					SquashRenderer.ONE_MM,
					+SquashRenderer.COURT_LINE_WIDTH / 2f,
					1.6f - SquashRenderer.COURT_LINE_WIDTH / 2f,
					SquashRenderer.ONE_MM,
					+SquashRenderer.COURT_LINE_WIDTH / 2f }, new float[] { 1f,
					0f, 0f, 1f }, false));
		}
	}
}
