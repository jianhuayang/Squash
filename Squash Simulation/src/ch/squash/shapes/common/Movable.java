package ch.squash.shapes.common;

import android.util.Log;
import ch.squash.main.MovementEngine;
import ch.squash.main.SquashRenderer;
import ch.squash.shapes.shapes.Quadrilateral;

public class Movable {
	private final static String TAG = Movable.class.getSimpleName();

	private final AbstractShape mShape;

	public final PhysicalVector gravitation;
	public final PhysicalVector speed;
	public final PhysicalVector normal;

	public final PhysicalVector[] vectorArrows;
	
	private long mLastMovement;

	public static IVector getGravitation() {
		return new Vector(0, -9.81f, 0);
	}

	public Movable(final AbstractShape shape, final float[] origin) {
		mShape = shape;
		gravitation = new PhysicalVector("force_gravitation", origin.clone(),
				getGravitation().getDirection(), new float[] { 0, 0.5f, 0, 1 });
		speed = new PhysicalVector("speed", origin.clone(), new float[3], new float[] {
				0.5f, 0f, 0.5f, 1 });
		normal = new PhysicalVector("force_normal", origin.clone(), new float[3],
				new float[] { 1, 1, 0, 1 });
		vectorArrows = new PhysicalVector[] { gravitation, speed, normal };
	}

	public void resetClock() {
		mLastMovement = System.currentTimeMillis();
	}

	public void move() {
		final long now = System.currentTimeMillis(); // ms
		final float dt = (now - mLastMovement) / 1000f; // s
		mLastMovement = now;

		move(dt);
	}

	private void move(final float dt) {
		if (MovementEngine.DELAY_BETWEEN_MOVEMENTS > 0) {
			try {
				Thread.sleep(MovementEngine.DELAY_BETWEEN_MOVEMENTS);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error while sleeping", e);
			}
			mLastMovement += MovementEngine.DELAY_BETWEEN_MOVEMENTS;
		}

		// add forces
		IVector totalForce = new Vector(gravitation.getDirection()).multiply(1 / MovementEngine.SLOW_FACTOR);

		// calculate new speed v = v0 + a*t
		speed.setDirection((speed.getDirection()[0] + totalForce.getX() * dt)
				* MovementEngine.AIR_FRICTION_FACTOR,
				(speed.getDirection()[1] + totalForce.getY() * dt)
						* MovementEngine.AIR_FRICTION_FACTOR,
				(speed.getDirection()[2] + totalForce.getZ() * dt)
						* MovementEngine.AIR_FRICTION_FACTOR);

		// calculate travelling distance s = v0*t + 1/2*a*t^2
		float[] distance = new float[3];
		for (int i = 0; i < 3; i++)
			distance[i] = (speed.getDirection()[i] * dt + 0.5f
					* totalForce.getDirection()[i] * dt * dt) / MovementEngine.SLOW_FACTOR;
		
				
		// Collision floorCollision = null;
		boolean collided = false;
		
		Log.w(TAG, "Starting new round of collisions");
		
		for (final AbstractShape solid : SquashRenderer.getInstance().courtSolids) {
			// Log.i(TAG, "Checking collision with " + solid.tag);
			final Collision collision = Collision.getCollision(mShape,
					speed.multiply(dt), solid);

			if (collision != null) {
				MovementEngine.playBounceSound();
				collided = true;
				if (!(solid instanceof Quadrilateral)) {
					Log.wtf(TAG, "Shouldnt collide with unsolid shape!");
					continue;
				}

				totalForce = (Vector) totalForce.add(collision.normalForce);
				
				final float newDt = dt * (1 - collision.timePercentage);

				mShape.moveTo(collision.collisionPoint);

				final IVector n = (Vector) collision.solidNormalVector.getNormalizedVector();
				final IVector oldSpeed = speed.multiply(1);	// get a copy of the current speed
				final IVector newSpeed = (Vector) speed.add(n.multiply(-2 * speed.multiply(n)));	// formula for ausfallswinkel
				
				speed.setDirection(newSpeed.getX(), newSpeed.getY(), newSpeed.getZ());
				
				Log.i(TAG, "oldsp=" + oldSpeed + ", newspeed=" + newSpeed + ", normal=" + n);

				if (speed.getLength() > 1) {
					move(newDt);
				}
				return;
			}
		}

		if (!collided) {
			float[] destination = new float[3];
			for (int i = 0; i < 3; i++)
				destination[i] = mShape.location.getDirection()[i]
						+ distance[i];

			mShape.moveTo(new Vector(destination));
		}
	}

	public void reset() {
		speed.setDirection(0, 0, 0);
		mShape.moveTo(mShape.origin);
	}

}
