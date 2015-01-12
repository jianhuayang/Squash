package ch.squash.simulation.shapes.common;

import android.util.Log;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.graphic.SquashRenderer;
import ch.squash.simulation.main.MovementEngine;
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
	public final Trace trace;

	// data for between movements
	private Collision mLastMovementCollision;
	private long mNextMovement = System.currentTimeMillis(); // ms
	private int mMoveSkipCount;

	public Movable(final AbstractShape shape, final float[] origin) {
		mShape = shape;
		gravitation = new PhysicalVector("force_gravitation", origin.clone(),
				getGravitation().getDirection(), new float[] { 0, 0.5f, 0, 1 });
		speed = new PhysicalVector("speed", origin.clone(), new float[3],
				new float[] { 0.5f, 0f, 0.5f, 1 });
		airFriction = new PhysicalVector("air_friction", origin.clone(),
				new float[3], new float[] { 0.7f, 0.5f, 0, 1 });

		vectorArrows = new PhysicalVector[] { gravitation, speed, airFriction };

		trace = new Trace(shape.tag + "\'s trace", new float[] { 0.35f, 0.35f,
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
			mMoveSkipCount++;
		} else {
			move((mMoveSkipCount + 1) * MovementEngine.DELAY_BETWEEN_MOVEMENTS
					/ 1000f * Settings.getSpeedFactor());
			mMoveSkipCount = 0;
		}
	}

	// move in seconds to use Si-units
	private void move(final float dt) {
		final IVector oldSpeed = speed.multiply(1);
		
		if (isRolling()){
//			mLastMovementCollision = null;
			// TODO
			Log.w(TAG, "Ball is ROLLING which is NOT IMPLEMENTED");
		}else{
			//////////////////////////////////////
			// calculating distance travelled
			//////////////////////////////////////
			// calculate distance with old speed
			final IVector oldDistance = speed.multiply(dt);

			// set forces
			airFriction.setDirection(speed.getNormalizedVector().multiply(
					-((Ball)mShape).getDragFactor() * speed.getLength() * speed.getLength()));
			
			// calculate acceleration
			final IVector acceleration = airFriction.add(gravitation);

			// update speed: v = v0 + a*t
			speed.setDirection(speed.add(acceleration.multiply(dt)));

			// calculate actual distance travelled by taking the average of old and new distance
			final IVector actualDistance = (speed.multiply(dt).add(oldDistance)).multiply(0.5f);

			//////////////////////////////////////
			// detecting collisions 
			//////////////////////////////////////
			Collision possibleCollision = null;
			for (final AbstractShape solid : SquashRenderer.getCourtSolids()) {
				final Collision collision = Collision.getCollision(mShape,
						actualDistance, solid, mLastMovementCollision);

				if (collision != null){
					if (collision.travelPercentage < 0)
						Log.e(TAG, "travelperc of " + solid.tag + " and ball is " + collision.travelPercentage);
			
					if (possibleCollision == null)
						possibleCollision = collision;
					else if (collision.travelPercentage < possibleCollision.travelPercentage)
						possibleCollision = collision;
				}
			}
			mLastMovementCollision = possibleCollision;
			
			//////////////////////////////////////
			// reacting to collision
			//////////////////////////////////////
			if (mLastMovementCollision == null){
				// complete movement "as planned
				// move shape
				mShape.move(actualDistance);
			}else{
				final float curDt = dt * mLastMovementCollision.travelPercentage;
				final float newDt = dt - curDt;
				
				// MUST NO LONGER USE ACTUALDISTANCE!!
				
				// adjust speed update
				speed.setDirection(oldSpeed.add(acceleration.multiply(curDt)));
				// move shape
				mShape.moveTo(mLastMovementCollision.collisionPoint);
				
				// do collision stuff
				MovementEngine.playSound(mLastMovementCollision.collidedSolid.tag);
				
				// calculate ausfallswinkel (= einfallswinkel, must change that)
				final IVector n = mLastMovementCollision.solidNormalVector.getNormalizedVector();
				
				final IVector newSpeed = speed.add(n.multiply(-2 * speed.multiply(n))).multiply(Settings.getCoefficientOfRestitution()); // formula for ausfallswinkel
				
				speed.setDirection(newSpeed);
				
				move(newDt);
				return;
			}
		}
	}

	public void reset() {
		airFriction.setDirection(0, 0, 0);
		trace.reset();
		mShape.moveTo(Settings.getBallStartPosition());
		speed.setDirection(Settings.getBallStartSpeed()); // watch out with new
															// movables...
	}

	private boolean isRolling() {
		// rolling if a collision with a "floor"-object happened last round and
		// the speed away from the floor is small
		return mLastMovementCollision != null
				&& ((Quadrilateral) mLastMovementCollision.collidedSolid)
						.getNonZeroDimension() == 1 && speed.getY() < 0.1f;
	}

	public float getPotentialEnergy(){
		return mShape.location.getY() * gravitation.getLength();
	}
	
	public float getKineticLinearEnergy(){
		return speed.getLength() * speed.getLength() * 0.5f;
	}
}
