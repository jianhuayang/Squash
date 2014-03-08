package ch.squash.simulation.shapes.common;

import android.util.Log;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.main.MovementEngine;
import ch.squash.simulation.main.SquashRenderer;
import ch.squash.simulation.shapes.shapes.Ball;
import ch.squash.simulation.shapes.shapes.Quadrilateral;

public class Movable {
	// static
	private final static String TAG = Movable.class.getSimpleName();

	// shapes
	private final AbstractShape mShape;
	public final PhysicalVector[] vectorArrows;
	public final PhysicalVector gravitation;
	public final PhysicalVector speed;
	public final PhysicalVector airFriction;
	public final Trace mTrace;

	// data for between movements
	private Collision lastMovementCollision;
	private long mNextMovement = System.currentTimeMillis(); // ms
	private int moveSkipCount;

	public Movable(final AbstractShape shape, final float[] origin) {
		mShape = shape;
		gravitation = new PhysicalVector("force_gravitation", origin.clone(),
				getGravitation().getDirection(), new float[] { 0, 0.5f, 0, 1 });
		speed = new PhysicalVector("speed", origin.clone(), new float[3],
				new float[] { 0.5f, 0f, 0.5f, 1 });
		airFriction = new PhysicalVector("air_friction", origin.clone(),
				new float[3], new float[] { 0.7f, 0.5f, 0, 1 });

		vectorArrows = new PhysicalVector[] { gravitation, speed, airFriction };

		mTrace = new Trace(shape.tag + "\'s trace", new float[] { 0.35f, 0.35f,
				0.35f, 1 });
	}

	public static IVector getGravitation() {
		return new Vector(0, -9.81f, 0);
	}

	public void resetClock() {
		mNextMovement = System.currentTimeMillis()
				+ MovementEngine.DELAY_BETWEEN_MOVEMENTS;
	}

	public void move() {
		final long now = System.currentTimeMillis();
		final long sleep = mNextMovement - now;
		mNextMovement += MovementEngine.DELAY_BETWEEN_MOVEMENTS;

		if (sleep > 0)
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error while sleeping", e);
			}
		else
			Log.w(TAG, "Calculating last round of movements took too long: "
					+ (MovementEngine.DELAY_BETWEEN_MOVEMENTS - sleep)
					+ "ms, should be under "
					+ MovementEngine.DELAY_BETWEEN_MOVEMENTS + "ms");

		if (!MovementEngine.isRunning())
			return;

		if (sleep < -MovementEngine.DELAY_BETWEEN_MOVEMENTS) {
			Log.e(TAG, "Skipping move");
			moveSkipCount++;
		} else {
			move((moveSkipCount + 1) * MovementEngine.DELAY_BETWEEN_MOVEMENTS
					/ 1000f * Settings.getSpeedFactor());
			moveSkipCount = 0;
		}
	}

	// move in seconds to use Si-units
	private void move(final float dt) {
		// distance that is travelled if speed is considered to be constant
		// during dt
		final IVector distance = speed.multiply(dt);

		final boolean rolling = isRolling();
		lastMovementCollision = null;

		for (final AbstractShape solid : SquashRenderer.getCourtSolids()) {
			final Collision collision = Collision.getCollision(mShape,
					distance, solid, lastMovementCollision);

			if (collision != null){
				if (collision.travelPercentage < 0)
					Log.e(TAG, "travelperc of " + solid.tag + " and ball is " + collision.travelPercentage);
		
				if (lastMovementCollision == null)
					lastMovementCollision = collision;
				else if (collision.travelPercentage < lastMovementCollision.travelPercentage)
					lastMovementCollision = collision;
			}
		}
		
		// update shape position, forces, speed
		if (lastMovementCollision == null)
			if (rolling)
				doRoll(dt);
			else
				doFly(dt, distance);
		else
			if (rolling)
				doRollCollision(dt);
			else
				doFlyCollision(dt);
	}

	public void reset() {
		airFriction.setDirection(0, 0, 0);
		mTrace.reset();
		mShape.moveTo(Settings.getBallStartPosition());
		speed.setDirection(Settings.getBallStartSpeed()); // watch out with new
															// movables...
	}

	private boolean isRolling() {
		// rolling if a collision with a "floor"-object happened last round and
		// the speed away from the floor is small
		return lastMovementCollision != null
				&& ((Quadrilateral) lastMovementCollision.collidedSolid)
						.getNonZeroDimension() == 1 && speed.getY() < 0.1f;
	}

	private void doRoll(final float dt) {
		// // if (isRolling){
		// // speed.setDirection(new Vector(speed.getX() + acceleration.getX() *
		// dt, 0, speed.getZ() + acceleration.getZ() * dt));
		// // distance.setDirection(distance.getX(), mShape.location.getY(),
		// distance.getZ());
		// //
		// // Log.e(TAG, "rolling");
		// // }else{
		// // reset variables

	}

	private void doRollCollision(final float dt) {

	}

	private void doFly(final float dt, final IVector distance) {
		// move shape
		mShape.moveTo(mShape.location.add(distance));
		
		// set forces
		airFriction.setDirection(speed.getNormalizedVector().multiply(
				-((Ball)mShape).getDragFactor() * speed.getLength() * speed.getLength()));
		
		// calculate acceleration
		final IVector acceleration = airFriction.add(gravitation);
		
		// update speed
		speed.setDirection(speed.add(acceleration.multiply(dt)));
	}

	private void doFlyCollision(final float dt) {
		// go to collision point
		doFly(dt * lastMovementCollision.travelPercentage, lastMovementCollision.collisionPoint.add(mShape.location.multiply(-1)));
		
		// do collision stuff
		MovementEngine.playSound(lastMovementCollision.collidedSolid.tag);
		
		final float curDt = dt * lastMovementCollision.travelPercentage;
		final float newDt = dt - curDt;
		
		// calculate ausfallswinkel (= einfallswinkel, must change that)
		final IVector n = lastMovementCollision.solidNormalVector.getNormalizedVector();
		
		final IVector newSpeed = speed.add(n.multiply(-2 * speed.multiply(n))).multiply(Settings.getCoefficientOfRestitution()); // formula for ausfallswinkel
		
		speed.setDirection(newSpeed);
		
		// do rest of movement
		final IVector newDistance = speed.multiply(newDt);
		
		doFly(newDt, newDistance);
	}


	public float getPotentialEnergy(){
		return mShape.location.getY() * gravitation.getLength();
	}
	
	public float getKineticLinearEnergy(){
		return speed.getLength() * speed.getLength() * 0.5f;
	}

	public float getKineticRotationalEnergy(){
		return 0;
	}
	
	public float getThermicEnergy(){
		return 0;
	}
	
	public float getTotalEnergy(){
		return getPotentialEnergy() + getKineticLinearEnergy() + getKineticRotationalEnergy() + getThermicEnergy();
	}
}
