package ch.squash.simulation.shapes.common;

import android.util.Log;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.main.MovementEngine;
import ch.squash.simulation.main.SquashRenderer;
import ch.squash.simulation.shapes.shapes.Ball;
import ch.squash.simulation.shapes.shapes.Quadrilateral;

public class Movable {
	private final static String TAG = Movable.class.getSimpleName();

	private final AbstractShape mShape;

	public final PhysicalVector gravitation;
	public final PhysicalVector speed;
	public final PhysicalVector airFriction;

	public final PhysicalVector[] vectorArrows;
	
	public final Trace mTrace;
	public Collision lastMovementCollision;

	private long mNextMovement = System.currentTimeMillis(); // ms

	private int moveSkipCount;
	
	public static IVector getGravitation() {
		return new Vector(0, -9.81f, 0);
	}

	public Movable(final AbstractShape shape, final float[] origin) {
		mShape = shape;
		gravitation = new PhysicalVector("force_gravitation", origin.clone(),
				getGravitation().getDirection(), new float[] { 0, 0.5f, 0, 1 });
		speed = new PhysicalVector("speed", origin.clone(), new float[3], new float[] {
				0.5f, 0f, 0.5f, 1 });
		airFriction = new PhysicalVector("air_friction", origin.clone(), new float[3], new float[] {
				0.7f, 0.5f, 0, 1 });
		
		vectorArrows = new PhysicalVector[] { gravitation, speed, airFriction };
		
		mTrace = new Trace(shape.tag + "\'s trace", new float[]{ 0.35f, 0.35f, 0.35f, 1 });
	}

	public void resetClock() {
		mNextMovement = System.currentTimeMillis() + MovementEngine.DELAY_BETWEEN_MOVEMENTS;
	}

	public void move() {
		final long now = System.currentTimeMillis();
		final long sleep = mNextMovement - now;
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
		
		if (sleep < -MovementEngine.DELAY_BETWEEN_MOVEMENTS){
			Log.e(TAG, "Skipping move");
			moveSkipCount++;
		}
		else{
			move((moveSkipCount + 1) * MovementEngine.DELAY_BETWEEN_MOVEMENTS / 1000f);
			moveSkipCount = 0;
		}
	}
	
	// move in seconds to use Si-units
	private void move(final float dt) {
		float epot = mShape.location.getY() * gravitation.getLength();
		float ekin = 0.5f * speed.getLength() * speed.getLength();
		Log.v(TAG, "Energy before move: pot=" + epot + ",\tkin= " + ekin +",\tsum=" + (epot+ekin));

		// set air friction
		airFriction.setDirection(speed.getNormalizedVector().multiply(-((Ball)mShape).frictionConstant * speed.getLength() * speed.getLength()));
		
		// calculate forces
		// every force is calculated without weight so it equals the acceleration
		final IVector totalForce = gravitation.add(airFriction);
		final IVector acceleration = totalForce;

		// calculate travelling distance s = v0*t + 1/2*a*t^2
		final IVector distance = speed.multiply(dt).add(acceleration.multiply(dt * dt * 0.5f));		
		
		boolean collided = true;
		while (collided){
			collided = false;
			
			for (final AbstractShape solid : SquashRenderer.getInstance().courtSolids) {
				final Collision collision = Collision.getCollision(mShape, distance, solid, lastMovementCollision);
	
				if (collision != null) {
					// do miscellaneous stuff
					collided = true;
					lastMovementCollision = collision;
					
					MovementEngine.playSound(solid.tag);
					
					mShape.moveTo(collision.collisionPoint);
	
					final float curDt = dt * collision.travelPercentage;
					final float newDt = dt - curDt;
					
					// calculate new speed v = v0 + a*t
					speed.setDirection(speed.add(acceleration.multiply(curDt)));

					// calculate ausfallswinkel (= einfallswinkel, must change that)
					final IVector n = collision.solidNormalVector.getNormalizedVector();

					final float collisionAngle = (float)(collision.collisionAngle * 180 / Math.PI);
					
					float speedFactor = collisionAngle * 0.0075f + 0.125f;
					float refractionFactor = collisionAngle * 0.005f + 0.55f;

					Log.d(TAG, "Slowing down by " + ((1 - speedFactor) * 100) + "% after a collision at " + collisionAngle);
					
					if (speedFactor > 1)
						speedFactor = 1;
					if (speedFactor < 0)
						speedFactor = 0;
					if (refractionFactor > 1)
						refractionFactor = 1;
					if (refractionFactor < 0)
						refractionFactor = 0;
					
					final float newSpeedLength = speed.getLength() * speedFactor;
					
					final IVector newSpeed = speed.add(
							n.multiply(-(1 + refractionFactor) * speed.multiply(n))).
							getNormalizedVector().multiply(newSpeedLength);	// formula for ausfallswinkel
					speed.setDirection(newSpeed);
					
					final double outAng = (Math.PI / 2 - newSpeed.getAngle(((Quadrilateral)solid).getNormalVector())) / Math.PI * 180;
					Log.d(TAG, "inangle=" + collisionAngle + ", outangle=" + outAng);
					
					final float oldTot = epot + ekin;
					epot = mShape.location.getY() * gravitation.getLength();
					ekin = 0.5f * speed.getLength() * speed.getLength();
					Log.i(TAG, "Energy delta after collision: " + ((ekin + epot) / oldTot * 100) + "%");
					
					move(newDt);
					return;
				}
			}
		}

		// reset variables
		lastMovementCollision = null;
		
		// calculate new speed v = v0 + a*t
		speed.setDirection(speed.add(acceleration.multiply(dt)));
			
		if (!MovementEngine.isRunning())
			return;
		
		mShape.moveTo(mShape.location.add(distance));
		
		epot = mShape.location.getY() * gravitation.getLength();
		ekin = 0.5f * speed.getLength() * speed.getLength();
	}

	public void reset() {
		speed.setDirection(Settings.getBallStartSpeed());		// watch out with new movables...
		airFriction.setDirection(0, 0, 0);
		mTrace.reset();
		mShape.moveTo(Settings.getBallStartPosition());
	}
}
