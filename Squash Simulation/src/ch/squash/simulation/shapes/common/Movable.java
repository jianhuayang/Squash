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
				new float[] { 1, 1, 0, 1 });
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

		if (!MovementEngine.isRunning())
			return;
		
		move(MovementEngine.DELAY_BETWEEN_MOVEMENTS / 1000f);
//		Log.d(TAG, "sleep=" + sleep + "ms, movementduration=" + (System.currentTimeMillis() - now - sleep) + "ms");
	}
	
	// move in seconds to use Si-units
	private void move(float dt) {
		// add forces
		IVector totalForce = new Vector(gravitation.getDirection());
		
		// calculate new speed v = v0 + a*t
		speed.setDirection(
				(speed.getDirection()[0] + totalForce.getX() * dt) * MovementEngine.AIR_FRICTION_FACTOR,
				(speed.getDirection()[1] + totalForce.getY() * dt) * MovementEngine.AIR_FRICTION_FACTOR,
				(speed.getDirection()[2] + totalForce.getZ() * dt) * MovementEngine.AIR_FRICTION_FACTOR);

		// calculate travelling distance s = v0*t + 1/2*a*t^2
		// PROBABLY WRONG!!! during the interval, the motion is described as gleichfoermig, not gleichmaessig beschleunigt
		final IVector distance = speed.multiply(dt * MovementEngine.SLOW_FACTOR);
				
		Log.d(TAG, "Starting round of collisions. location=" + mShape.location + ", distance=" + distance);
		
		boolean collided = true;
		while (collided){
			collided = false;
			
			for (final AbstractShape solid : SquashRenderer.getInstance().courtSolids) {
				final Collision collision = Collision.getCollision(mShape, distance, solid);
	
				if (collision != null) {
					collided = true;
					
					MovementEngine.playSound(solid.tag);
					if (!(solid instanceof Quadrilateral)) {
						Log.wtf(TAG, "Shouldnt collide with unsolid shape!");
						continue;
					}
					
//					totalForce = (Vector) totalForce.add(collision.normalForce.multiply(30));
					
//					dt = dt * (1 - collision.travelPercentage);
	
					mShape.moveTo(collision.collisionPoint);
	
					final IVector n = (Vector) collision.solidNormalVector.getNormalizedVector();
					final IVector oldSpeed = speed.multiply(1);	// get a copy of the current speed
					final IVector newSpeed = (Vector) speed.add(n.multiply(-2 * speed.multiply(n))).multiply(MovementEngine.COLLISION_FRICTION_FACTOR);	// formula for ausfallswinkel
					
					speed.setDirection(newSpeed.getX(), newSpeed.getY(), newSpeed.getZ());
					
					Log.i(TAG, "oldspeed=" + oldSpeed + ", newspeed=" + newSpeed + ", normal=" + n + ", location=" + mShape.location);
					
					return;
//					break;
				}
			}
		}
		
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
