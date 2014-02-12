package ch.squash.simulation.shapes.common;

import android.util.Log;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.main.MovementEngine;
import ch.squash.simulation.main.SquashRenderer;
import ch.squash.simulation.shapes.shapes.Quadrilateral;

public class Movable {
	private final static String TAG = Movable.class.getSimpleName();

	private final AbstractShape mShape;

	public final PhysicalVector gravitation;
	public final PhysicalVector speed;
	public final PhysicalVector normal;

	public final PhysicalVector[] vectorArrows;
	
	public final Trace mTrace;
	public Collision lastMovementCollision;

	private long mNextMovement = System.currentTimeMillis(); // ms

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
				new float[] { 0, 0.8f, 0.8f, 1 });
		vectorArrows = new PhysicalVector[] { gravitation, speed, normal };
		
		mTrace = new Trace(shape.tag + "\'s trace", 0, 0, 0, null, new float[]{ 0.35f, 0.35f, 0.35f, 1 });
	}

	public void resetClock() {
		mNextMovement = System.currentTimeMillis() + MovementEngine.DELAY_BETWEEN_MOVEMENTS;
	}

	public void move() {
		final long now = System.currentTimeMillis();
		long sleep = mNextMovement - now;
		mNextMovement += MovementEngine.DELAY_BETWEEN_MOVEMENTS;
		
		if (sleep > 0)
			try{
				Thread.sleep(sleep);
				} catch (InterruptedException e) {
					Log.e(TAG, "Error while sleeping", e);
				}
		else
			Log.w(TAG, "Calculating last round of movements took too long: " + (MovementEngine.DELAY_BETWEEN_MOVEMENTS - sleep) + "ms, should be under " + MovementEngine.DELAY_BETWEEN_MOVEMENTS + "ms");

		if (!MovementEngine.isRunning())
			return;
		
		move(MovementEngine.DELAY_BETWEEN_MOVEMENTS / 1000f);
//		Log.d(TAG, "sleep=" + sleep + "ms, movementduration=" + (System.currentTimeMillis() - now - sleep) + "ms");
	}
	
	// move in seconds to use Si-units
	private void move(float dt) {
		// add forces
		IVector totalForce = new Vector(gravitation.getDirection());

		normal.setDirection(0, 0, 0);
		for (final AbstractShape s : SquashRenderer.getInstance().courtSolids)
			if (Collision.isOnSolid(mShape, s)){
				final IVector n = ((Quadrilateral)s).getNormalizedVector().multiply(getGravitation().getLength());
				totalForce = totalForce.add(n);
				
				normal.setDirection(n);
			}
		
		// calculate new speed v = v0 + a*t
		speed.setDirection(
				(speed.getDirection()[0] + totalForce.getX() * dt) * MovementEngine.AIR_FRICTION_FACTOR,
				(speed.getDirection()[1] + totalForce.getY() * dt) * MovementEngine.AIR_FRICTION_FACTOR,
				(speed.getDirection()[2] + totalForce.getZ() * dt) * MovementEngine.AIR_FRICTION_FACTOR);

		// calculate travelling distance s = v0*t + 1/2*a*t^2
		// PROBABLY WRONG!!! during the interval, the motion is described as gleichfoermig, not gleichmaessig beschleunigt
		final IVector distance = speed.multiply(dt * MovementEngine.SLOW_FACTOR);
				
		Log.d(TAG, "Starting round of collisions. location=" + mShape.location + ", distance=" + distance + ", force=" + totalForce);
		
		boolean collided = true;
		while (collided){
			collided = false;
			
			for (final AbstractShape solid : SquashRenderer.getInstance().courtSolids) {
				final Collision collision = Collision.getCollision(mShape, distance, solid, lastMovementCollision);
	
				if (collision != null) {
					collided = true;
					lastMovementCollision = collision;
					
					MovementEngine.playSound(solid.tag);
					if (!(solid instanceof Quadrilateral)) {
						Log.wtf(TAG, "Shouldnt collide with unsolid shape!");
						continue;
					}
					
					mShape.moveTo(collision.collisionPoint);
	
					final IVector n = (Vector) collision.solidNormalVector.getNormalizedVector();
					final IVector oldSpeed = speed.multiply(1);	// get a copy of the current speed
					final IVector newSpeed = (speed.add(n.multiply(-2 * speed.multiply(n)))).multiply(MovementEngine.COLLISION_FRICTION_FACTOR);	// formula for ausfallswinkel
					
					speed.setDirection(newSpeed);
					
					Log.i(TAG, "oldspeed=" + oldSpeed + ", newspeed=" + newSpeed + ", location=" + mShape.location);

					dt = dt * (1 - collision.travelPercentage);
					
					move(dt);
					return;
//					break;
				}
			}
		}
		
		if (!collided)
			lastMovementCollision = null;
		
		if (!MovementEngine.isRunning())
			return;
		
		mShape.moveTo(mShape.location.add(distance));
	}

	public void reset() {
		speed.setDirection(Settings.getBallStartSpeed());		// watch out with new movables...
		mTrace.reset();
		mShape.moveTo(Settings.getBallStartPosition());
	}

}
