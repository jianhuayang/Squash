package ch.squash.simulation.shapes.common;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import ch.squash.simulation.main.SquashRenderer;
import ch.squash.simulation.shapes.shapes.Chair;
import ch.squash.simulation.shapes.shapes.Quadrilateral;

public class ShapeCollection {
	// constant
	private final static String TAG = ShapeCollection.class.getSimpleName();
	public final static int OBJECT_COLLECTION_COURT = 0;
	public final static int OBJECT_COLLECTION_ARENA = 1;
	public final static int OBJECT_COLLECTION_CHAIRS = 2;
	
	private final static String FLOOR_LINE = "floor line";
	private final static float[] COLOR_FLOOR = new float[]{ 0.2f, 0, 0.2f, 1 };
	private final static float[] COLOR_STAND_VERTICAL = new float[]{ 0, 0.1f, 0.3f, 1 };
	private final static float[] COLOR_STAND_HORIZONTAL = new float[]{ 0.1f, 0f, 0.2f, 1 };
	private final static float[] COLOR_STAND_OUTSIDE = new float[]{ 0.2f, 0.2f, 0.2f, 1 };
	private final static float[] COLOR_CHAIR = new float[]{ 0.3f, 0, 1, 1 };
	private final static float STAND_STEP_WIDTH = 0.7f;
	private final static float STAND_STEP_HEIGHT = 0.3f;
	private final static float STAND_CHAIR_SIZE = 0.5f;
	private final static int STAND_COUNT_FRONT_SIDE = 7;
	private final static int STAND_COUNT_BACK = 10;

	
	// collections
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

	// instantiates specific collection
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
			mOpaqueObjects.add(new Quadrilateral(FLOOR_LINE,
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
			mOpaqueObjects.add(new Quadrilateral(FLOOR_LINE, new float[] {
					-3.2f, SquashRenderer.ONE_MM,
					SquashRenderer.COURT_LINE_WIDTH / 2f, 3.2f,
					SquashRenderer.ONE_MM,
					SquashRenderer.COURT_LINE_WIDTH / 2f, 3.2f,
					SquashRenderer.ONE_MM,
					-SquashRenderer.COURT_LINE_WIDTH / 2f, -3.2f,
					SquashRenderer.ONE_MM,
					-SquashRenderer.COURT_LINE_WIDTH / 2f }, new float[] { 1f,
					0f, 0f, 1f }, false));
			mOpaqueObjects.add(new Quadrilateral(FLOOR_LINE, new float[] {
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
			mOpaqueObjects.add(new Quadrilateral(FLOOR_LINE, new float[] {
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
			mOpaqueObjects.add(new Quadrilateral(FLOOR_LINE, new float[] {
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
			mOpaqueObjects.add(new Quadrilateral(FLOOR_LINE, new float[] {
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
		} else if (collectionId == OBJECT_COLLECTION_ARENA){
			// floor "up"
			mOpaqueObjects.add(new Quadrilateral("floor_behind", new float[]{
					-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 4.26f, -4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 5.26f,
					4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 5.26f, 4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 4.26f },
					COLOR_FLOOR, false));
			mOpaqueObjects.add(new Quadrilateral("floor_before", new float[]{
					-4.2f, 0, -6.49f, -4.2f, 0, -5.49f, 4.2f, 0, -5.49f, 4.2f, 0, -6.49f },
					COLOR_FLOOR, false));
			mOpaqueObjects.add(new Quadrilateral("floor_left", new float[]{
					-4.2f, 0, -5.49f, -4.2f, 0, 4.26f, -3.2f, 0, 4.26f, -3.2f, 0, -5.49f },
					COLOR_FLOOR, false));
			mOpaqueObjects.add(new Quadrilateral("floor_right", new float[]{
					3.2f, 0, -5.49f, 3.2f, 0, 4.26f, 4.2f, 0, 4.26f, 4.2f, 0, -5.49f },
					COLOR_FLOOR, false));
			// floor "down"
			mOpaqueObjects.add(new Quadrilateral("floor_bottom", new float[]{
					4.26f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 5.26f + STAND_COUNT_BACK * STAND_STEP_WIDTH,
					-4.26f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 5.26f + STAND_COUNT_BACK * STAND_STEP_WIDTH,
					-4.26f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, -6.49f - STAND_COUNT_BACK * STAND_STEP_WIDTH,
					4.26f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, -6.49f - STAND_COUNT_BACK * STAND_STEP_WIDTH,
					}, COLOR_STAND_OUTSIDE, false));
			
			// stands - backwall
			for (int i = 0; i < STAND_COUNT_BACK; i++){
				// inside
				mOpaqueObjects.add(new Quadrilateral("stand_back", new float[]{
						-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, i * STAND_STEP_HEIGHT, 5.26f + i * STAND_STEP_WIDTH,
						-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 5.26f + i * STAND_STEP_WIDTH,
						 4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 5.26f + i * STAND_STEP_WIDTH,
						 4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, i * STAND_STEP_HEIGHT, 5.26f + i * STAND_STEP_WIDTH},
						 COLOR_STAND_VERTICAL, false));
				mOpaqueObjects.add(new Quadrilateral("stand_back", new float[]{
						-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 5.26f + i * STAND_STEP_WIDTH,
						-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 5.26f + (i+1) * STAND_STEP_WIDTH,
						 4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 5.26f + (i+1) * STAND_STEP_WIDTH,
						 4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 5.26f + i * STAND_STEP_WIDTH},
						 COLOR_STAND_HORIZONTAL, false));
				// outside
				mOpaqueObjects.add(new Quadrilateral("stand_back_outside", new float[]{
						-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 5.26f + i * STAND_STEP_WIDTH,
						-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 5.26f + (i+1) * STAND_STEP_WIDTH,
						-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 5.26f + (i+1) * STAND_STEP_WIDTH,
						-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 5.26f + i * STAND_STEP_WIDTH,
						}, COLOR_STAND_OUTSIDE, false));
				mOpaqueObjects.add(new Quadrilateral("stand_back_outside", new float[]{
						4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 5.26f + (i+1) * STAND_STEP_WIDTH,
						4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 5.26f + i * STAND_STEP_WIDTH,
						4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 5.26f + i * STAND_STEP_WIDTH,
						4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 5.26f + (i+1) * STAND_STEP_WIDTH,
						}, COLOR_STAND_OUTSIDE, false));
			}
			// outside
			mOpaqueObjects.add(new Quadrilateral("stand_back_outside", new float[]{
					4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, STAND_COUNT_BACK * STAND_STEP_HEIGHT, 5.26f + STAND_COUNT_BACK * STAND_STEP_WIDTH,
					-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, STAND_COUNT_BACK * STAND_STEP_HEIGHT, 5.26f + STAND_COUNT_BACK * STAND_STEP_WIDTH,
					-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 5.26f + STAND_COUNT_BACK * STAND_STEP_WIDTH,
					4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 5.26f + STAND_COUNT_BACK * STAND_STEP_WIDTH,
					}, COLOR_STAND_OUTSIDE, false));

			// stands - left sidewall
			for (int i = 0; i < STAND_COUNT_FRONT_SIDE; i++){
				mOpaqueObjects.add(new Quadrilateral("stand_left", new float[]{
						-4.2f - i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 4.26f,
						-4.2f - i * STAND_STEP_WIDTH, i * STAND_STEP_HEIGHT, 4.26f,
						-4.2f - i * STAND_STEP_WIDTH, i * STAND_STEP_HEIGHT, -6.49f - i * STAND_STEP_WIDTH,
						-4.2f - i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, -6.49f - i * STAND_STEP_WIDTH},
						COLOR_STAND_VERTICAL, false));
				mOpaqueObjects.add(new Quadrilateral("stand_left", new float[]{
						-4.2f - (i+1) * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 4.26f,
						-4.2f - i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 4.26f,
						-4.2f - i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, -6.49f - i * STAND_STEP_WIDTH,
						-4.2f - (i+1) * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, -6.49f - (i+1) * STAND_STEP_WIDTH},
						COLOR_STAND_HORIZONTAL, false));
				// outside
				mOpaqueObjects.add(new Quadrilateral("stand_left_outside", new float[]{
						-4.2f - (i+1) * STAND_STEP_WIDTH, 0, 4.26f,
						-4.2f - i * STAND_STEP_WIDTH, 0, 4.26f,
						-4.2f - i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 4.26f,
						-4.2f - (i+1) * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 4.26f
						}, COLOR_STAND_OUTSIDE, false));
			}
			// outside
			mOpaqueObjects.add(new Quadrilateral("stand_left_outside", new float[]{
					-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, -6.49f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH,
					-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 4.26f,
					-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, STAND_COUNT_FRONT_SIDE * STAND_STEP_HEIGHT, 4.26f,
					-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, STAND_COUNT_FRONT_SIDE * STAND_STEP_HEIGHT, -6.49f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH,
					}, COLOR_STAND_OUTSIDE, false));

			// stands - frontwall
			for (int i = 0; i < STAND_COUNT_FRONT_SIDE; i++){
				mOpaqueObjects.add(new Quadrilateral("stand_front", new float[]{
						-4.2f - i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, -6.49f - i * STAND_STEP_WIDTH,
						-4.2f - i * STAND_STEP_WIDTH, i * STAND_STEP_HEIGHT, -6.49f - i * STAND_STEP_WIDTH,
						4.2f + i * STAND_STEP_WIDTH, i * STAND_STEP_HEIGHT, -6.49f - i * STAND_STEP_WIDTH,
						4.2f + i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, -6.49f - i * STAND_STEP_WIDTH},
						COLOR_STAND_VERTICAL, false));
				mOpaqueObjects.add(new Quadrilateral("stand_front", new float[]{
						-4.2f - (i+1) * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, -6.49f - (i+1) * STAND_STEP_WIDTH,
						-4.2f - i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, -6.49f - i * STAND_STEP_WIDTH,
						4.2f + i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, -6.49f - i * STAND_STEP_WIDTH,
						4.2f + (i+1) * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, -6.49f - (i+1) * STAND_STEP_WIDTH},
						COLOR_STAND_HORIZONTAL, false));			}
			// outside
			mOpaqueObjects.add(new Quadrilateral("stand_front_outside", new float[]{
					-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, STAND_COUNT_FRONT_SIDE * STAND_STEP_HEIGHT, -6.49f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH,
					4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, STAND_COUNT_FRONT_SIDE * STAND_STEP_HEIGHT, -6.49f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH,
					4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, -6.49f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH,
					-4.2f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, -6.49f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH,
					}, COLOR_STAND_OUTSIDE, false));

			// stands - right sidewall
			for (int i = 0; i < STAND_COUNT_FRONT_SIDE; i++){
				mOpaqueObjects.add(new Quadrilateral("stand_right", new float[]{
						4.2f + i * STAND_STEP_WIDTH, i * STAND_STEP_HEIGHT, 4.26f,
						4.2f + i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 4.26f,
						4.2f + i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, -6.49f - i * STAND_STEP_WIDTH,
						4.2f + i * STAND_STEP_WIDTH, i * STAND_STEP_HEIGHT, -6.49f - i * STAND_STEP_WIDTH},
						COLOR_STAND_VERTICAL, false));
				mOpaqueObjects.add(new Quadrilateral("stand_right", new float[]{
						4.2f + i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 4.26f,
						4.2f + (i+1) * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 4.26f,
						4.2f + (i+1) * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, -6.49f - (i+1) * STAND_STEP_WIDTH,
						4.2f + i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, -6.49f - i * STAND_STEP_WIDTH},
						COLOR_STAND_HORIZONTAL, false));
				// outside
				mOpaqueObjects.add(new Quadrilateral("stand_right_outside", new float[]{
						4.2f + i * STAND_STEP_WIDTH, 0, 4.26f,
						4.2f + (i+1) * STAND_STEP_WIDTH, 0, 4.26f,
						4.2f + (i+1) * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 4.26f,
						4.2f + i * STAND_STEP_WIDTH, (i+1) * STAND_STEP_HEIGHT, 4.26f,
						}, COLOR_STAND_OUTSIDE, false));
			}
			// outside
			mOpaqueObjects.add(new Quadrilateral("stand_right_outside", new float[]{
					4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, 4.26f,
					4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, 0, -6.49f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH,
					4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, STAND_COUNT_FRONT_SIDE * STAND_STEP_HEIGHT, -6.49f - STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH,
					4.2f + STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH, STAND_COUNT_FRONT_SIDE * STAND_STEP_HEIGHT, 4.26f,
					}, COLOR_STAND_OUTSIDE, false));

		} else if (collectionId == OBJECT_COLLECTION_CHAIRS) {
			final float arenaWidth = 2 * STAND_COUNT_FRONT_SIDE * STAND_STEP_WIDTH + 2 + 6.4f;
			
			// back
			float standWidth = arenaWidth;
			float space = standWidth;
			float chairs = 0;
			float startPosition = -arenaWidth / 2;
			while (space > 0.2f){
				chairs++;
				space = (standWidth- chairs * STAND_CHAIR_SIZE) / (chairs - 1);
			}
			chairs--;
			
			for (int j = 0; j < STAND_COUNT_BACK; j++){
				if (j % 2 == 1){
					chairs--;
					space = (standWidth - chairs * STAND_CHAIR_SIZE) / (chairs - 1);
				}else{
					chairs++;
					space = (standWidth - chairs * STAND_CHAIR_SIZE) / (chairs - 1);
				}
				
				for (int i = 0; i < chairs; i++)
					mOpaqueObjects.add(new Chair("chair", startPosition + i * (STAND_CHAIR_SIZE + space) + STAND_CHAIR_SIZE / 2, (j+1) * STAND_STEP_HEIGHT,
							5.26f + (j+1) * STAND_STEP_WIDTH - STAND_CHAIR_SIZE / 2 - SquashRenderer.ONE_CM, STAND_CHAIR_SIZE, STAND_CHAIR_SIZE, STAND_CHAIR_SIZE, COLOR_CHAIR));
			}
			
			// front
			for (int j = 0; j < STAND_COUNT_FRONT_SIDE; j++){
				startPosition = -4.2f - j * STAND_STEP_WIDTH;
				chairs = 0;
				standWidth = 8.4f + 2 * j * STAND_STEP_WIDTH;
				space = standWidth;
				while (space > 0.2f){
					chairs++;
					space = (standWidth - chairs * STAND_CHAIR_SIZE) / (chairs - 1);
				}
				
				if (j % 2 == 1){
					chairs--;
					space = (standWidth - chairs * STAND_CHAIR_SIZE) / (chairs - 1);
				}
				
				for (int i = 0; i < chairs; i++)
					mOpaqueObjects.add(new Chair("chair", startPosition + i * (STAND_CHAIR_SIZE + space) + STAND_CHAIR_SIZE / 2, (j+1) * STAND_STEP_HEIGHT,
							-6.49f - (j+1) * STAND_STEP_WIDTH + STAND_CHAIR_SIZE / 2 + SquashRenderer.ONE_CM, STAND_CHAIR_SIZE, STAND_CHAIR_SIZE, STAND_CHAIR_SIZE, COLOR_CHAIR, 2));
			}	

			// right
			for (int j = 0; j < STAND_COUNT_FRONT_SIDE; j++){
				startPosition = -6.49f - j * STAND_STEP_WIDTH;
				chairs = 0;
				standWidth = 10.75f + j * STAND_STEP_WIDTH;
				space = standWidth;
				while (space > 0.2f){
					chairs++;
					space = (standWidth - chairs * STAND_CHAIR_SIZE) / (chairs - 1);
				}

				if (j % 2 == 1){
					chairs--;
					space = (standWidth - chairs * STAND_CHAIR_SIZE) / (chairs - 1);
				}
				
				for (int i = 0; i < chairs; i++)
					mOpaqueObjects.add(new Chair("chair", 4.2f + (j+1) * STAND_STEP_WIDTH - STAND_CHAIR_SIZE / 2 - SquashRenderer.ONE_CM, (j+1) * STAND_STEP_HEIGHT,
							startPosition + i * (STAND_CHAIR_SIZE + space) + STAND_CHAIR_SIZE / 2, STAND_CHAIR_SIZE, STAND_CHAIR_SIZE, STAND_CHAIR_SIZE, COLOR_CHAIR, 3));
			}

			// left
			for (int j = 0; j < STAND_COUNT_FRONT_SIDE; j++){
				startPosition = -6.49f - j * STAND_STEP_WIDTH;
				chairs = 0;
				standWidth = 10.75f + j * STAND_STEP_WIDTH;
				space = standWidth;
				while (space > 0.2f){
					chairs++;
					space = (standWidth - chairs * STAND_CHAIR_SIZE) / (chairs - 1);
				}

				if (j % 2 == 1){
					chairs--;
					space = (standWidth - chairs * STAND_CHAIR_SIZE) / (chairs - 1);
				}
				
				for (int i = 0; i < chairs; i++)
					mOpaqueObjects.add(new Chair("chair", -4.2f - (j+1) * STAND_STEP_WIDTH + STAND_CHAIR_SIZE / 2 + SquashRenderer.ONE_CM, (j+1) * STAND_STEP_HEIGHT,
							startPosition + i * (STAND_CHAIR_SIZE + space) + STAND_CHAIR_SIZE / 2, STAND_CHAIR_SIZE, STAND_CHAIR_SIZE, STAND_CHAIR_SIZE, COLOR_CHAIR, 1));
			}
		} else {
			Log.e(TAG, "Unknown ShapeCollection ID: " + collectionId);
		}
	}
}
