package ch.squash.simulation.shapes.common;

import android.util.Log;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.main.MovementEngine;
import ch.squash.simulation.main.SquashRenderer;
import ch.squash.simulation.shapes.shapes.Ball;

public class Movable {
	private final static String TAG = Movable.class.getSimpleName();

	private final AbstractShape mShape;

	public final PhysicalVector gravitation;
	public final PhysicalVector speed;
	public final PhysicalVector normal;
	public final PhysicalVector airFriction;

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
		airFriction = new PhysicalVector("air_friction", origin.clone(), new float[3], new float[] {
				0.7f, 0.5f, 0, 1 });
		
		vectorArrows = new PhysicalVector[] { gravitation, speed, normal, airFriction };
		
		mTrace = new Trace(shape.tag + "\'s trace", 0, 0, 0, null, new float[]{ 0.35f, 0.35f, 0.35f, 1 });
	}

	public void resetClock() {
		mNextMovement = System.currentTimeMillis() + MovementEngine.DELAY_BETWEEN_MOVEMENTS;
	}

	private int moveSkipCount;
	
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
	private void move(float dt) {
		final float epot = mShape.location.getY() * gravitation.getLength();
		final float ekin = 0.5f * speed.getLength() * speed.getLength();
		Log.w(TAG, "epot=" + epot + ",\tekin= " + ekin +",\tsum=" + (epot+ekin));
		
//		speed.getNormalizedVector().multiply(
//				-((Ball)mShape).frictionConstant * speed.getLength() * speed.getLength() * 10);
//		airFriction.setDirection(totalForce);
		// add other forces
//		totalForce.setDirection(totalForce.add(gravitation)); //.add(normal));
		
		// calculate forces
		// every force is calculated without weight so it equals the acceleration
		final IVector totalForce = gravitation.getVector();
		final IVector acceleration = totalForce;

		// calculate travelling distance s = v0*t + 1/2*a*t^2
		final IVector distance = speed.multiply(dt).add(acceleration.multiply(dt * dt * 0.5f));		
		// calculate new speed v = v0 + a*t
		speed.setDirection(speed.add(acceleration.multiply(dt)));

		
		float ekin2 = 0.5f * (float)Math.pow(speed.getLength(), 2);
		float dekin = ekin2 - ekin;
		float depot = distance.getY() * gravitation.getLength();
		
//		distance = distance.multiply(-depot);
//		
//		ekin2 = 0.5f * (float)Math.pow(speed.getLength(), 2);
//		dekin = ekin2 - ekin;
//		depot = distance.getY() * gravitation.getLength();

		Log.v(TAG, "depot=" + depot + "\tdekin=" + dekin + "\tdiff=" + (depot + dekin));
				
//		Log.d(TAG, "Starting round of collisions. location=" + mShape.location + ", distance=" + distance + ", force=" + totalForce);

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
	
					normal.setDirection(collision.normalForce);

//					final IVector oldSpeed = speed.multiply(1);
					
					// calculate ausfallswinkel (= einfallswinkel, must change that)
					final IVector n = (Vector) collision.solidNormalVector.getNormalizedVector();
					
					final float halfPi = (float) (Math.PI / 2);
					final float angleFactor = collision.collisionAngle / halfPi;
					float speedFactor = angleFactor / MovementEngine.COLLISION_FRICTION_FACTOR;
					
					float refractionFactor = angleFactor / MovementEngine.COLLISION_REFRACTION_FACTOR;
					
					if (speedFactor > 1)
						speedFactor = 1;
					if (speedFactor < 0)
						speedFactor = 0;
					if (refractionFactor > 1)
						refractionFactor = 1;
					if (refractionFactor < 0)
						refractionFactor = 0;

					refractionFactor = 1;
					speedFactor = 1;
					
					final float newSpeedLength = speed.getLength() * speedFactor;
					IVector newSpeed = (speed.add(
							n.multiply(-(1 + refractionFactor) * speed.multiply(n)))).
							getNormalizedVector().multiply(newSpeedLength);	// formula for ausfallswinkel
					newSpeed = speed.multiply(-1);
					
					// adjust speed
					// the smaller the angle, the more the ball slows down, the lower the factor
					// speed * (1 - friction_factor * %)
					Log.e(TAG, "Setting speed from " + speed + " to " + newSpeed);
					speed.setDirection(newSpeed);
					
//					final double outAng = (halfPi - newSpeed.getAngle(((Quadrilateral)solid).getNormalVector())) / Math.PI * 180;
//					Log.e(TAG, "angle=" + (collision.collisionAngle * 180 / Math.PI) + ", speedf=" + speedFactor + ", refractionf=" + refractionFactor);
//					Log.w(TAG, "outang=" + outAng);
					
//					Log.i(TAG, "oldspeed=" + oldSpeed + ", newspeed=" + newSpeed + ", location=" + mShape.location);

					// do rest of the movement
//					while (true){}
					dt = dt * (1 - collision.travelPercentage);
					move(dt);
					return;
				}
			}
		}
		
		if (!collided)
			lastMovementCollision = null;
		
		if (!MovementEngine.isRunning())
			return;
		
		mShape.moveTo(mShape.location.add(distance));
		
		normal.setDirection(0, 0, 0);
	}

	public void reset() {
		speed.setDirection(Settings.getBallStartSpeed());		// watch out with new movables...
		airFriction.setDirection(0, 0, 0);
		mTrace.reset();
		mShape.moveTo(Settings.getBallStartPosition());
	}

}
