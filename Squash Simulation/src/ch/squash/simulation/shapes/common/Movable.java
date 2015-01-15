package ch.squash.simulation.shapes.common;

import java.util.Random;

import android.util.Log;
import ch.squash.simulation.common.Settings;
import ch.squash.simulation.graphic.SquashRenderer;
import ch.squash.simulation.main.MovementEngine;
import ch.squash.simulation.shapes.shapes.Ball;
import ch.squash.simulation.shapes.shapes.Quadrilateral;

public class Movable {
	// static
	private final static String TAG = Movable.class.getSimpleName();

	private final static float STATIONARY_THRESHOLD = 0.2f;	// the higher the threshold, the sooner the ball stops completely
	private final static float ROLLING_THRESHOLD = 0.5f;	// the higher the threshold, the sooner the ball starts rolling
	
	// shapes
	private final AbstractShape mShape;
	public final PhysicalVector[] vectorArrows;
	public final PhysicalVector gravitation;
	public final PhysicalVector speed;
	public final PhysicalVector airFriction;
	public final PhysicalVector rollFriction;
	public final Trace trace;

	// data for between movements
	private Collision mLastMovementCollision;
	private long mNextMovement = System.currentTimeMillis(); // ms
	private int mMoveSkipCount;
	private boolean mIsRolling;
	private AbstractShape mRollingShape;
	
	public Movable(final AbstractShape shape, final float[] origin) {
		mShape = shape;
		gravitation = new PhysicalVector("force_gravitation", origin.clone(),
				getGravitation().getDirection(), new float[] { 0, 0.5f, 0, 1 });
		speed = new PhysicalVector("speed", origin.clone(), new float[3],
				new float[] { 0.5f, 0f, 0.5f, 1 });
		airFriction = new PhysicalVector("air_friction", origin.clone(),
				new float[3], new float[] { 0.7f, 0.5f, 0, 1 });
		rollFriction = new PhysicalVector("roll_friction", origin.clone(),
				new float[3], new float[] { 0.5f, 0.7f, 0, 1 });

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
		while (MovementEngine.isRunning()) {
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
				final float dt = (mMoveSkipCount + 1) * MovementEngine.DELAY_BETWEEN_MOVEMENTS
						/ 1000f * Settings.getSpeedFactor();
				if (mIsRolling || isRolling()) {
					moveRolling(dt);
				} else {
					moveFlying(dt);
				}
				mMoveSkipCount = 0;
			}
		}
	}

	// move in seconds to use Si-units
	private void moveRolling(final float dt) {
		final IVector oldSpeed = speed.multiply(1);
		
		if (!mIsRolling) {
			Log.d(TAG, "First frame where the ball is rolling");
			
			mRollingShape = mLastMovementCollision.collidedSolid;
			
			// cheat and move movable straight onto rolling surface
			final IVector newLocation = mShape.location;
			newLocation.setY(mRollingShape.location.getY());
			mShape.moveTo(newLocation);
			
			// correct speed to be zero in y direction
			speed.setY(0);

			// set flag so this is only executed once
			mIsRolling = true;
		}
		
		Log.i(TAG, "Ball is ROLLING on " + mRollingShape);

		//////////////////////////////////////
		// calculating distance travelled
		//////////////////////////////////////
		
		// calculate distance with old speed
		final IVector oldDistance = speed.multiply(dt);

		// set forces
		airFriction.setDirection(speed.getNormalizedVector().multiply(
				-((Ball)mShape).getDragFactor() * speed.getLength() * speed.getLength()));
		rollFriction.setDirection(airFriction.multiply(Settings.getCoefficientOfRollFriction()));
		// calculate acceleration
		final IVector acceleration = airFriction.add(rollFriction); // don't add gravitation since that is being offset by the normalkraft
		
		// update speed: v = v0 + a*t
		speed.setDirection(speed.add(acceleration.multiply(dt)));
		
		// calculate actual distance travelled by taking the average of old and new distance --> WHY??
		final IVector actualDistance = (speed.multiply(dt).add(oldDistance)).multiply(0.5f);
		
		Log.d(TAG, "acc " + acceleration + ", " + speed + ", " + speed);

		//////////////////////////////////////
		// detecting collisions 
		//////////////////////////////////////
		Collision possibleCollision = null;
		for (final AbstractShape solid : SquashRenderer.getCourtSolids()) {
			if (solid == mRollingShape) {
				// no need to check collision with floor when the movable is rolling
				continue;
			}
			
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
			
			if (Math.abs(speed.getX()) + Math.abs(speed.getZ()) < STATIONARY_THRESHOLD) {
				Log.w(TAG, "Ball is not moving properly anymore, stopping...");
				speed.setDirection(0, 0, 0);
				MovementEngine.pause();
			}
		}else{
			Log.e(TAG, "COLLISION WHILE ROLLING!!!!");
			
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
			
			moveRolling(newDt);
			return;
		}
	}
	
	// move in seconds to use Si-units
	private void moveFlying(final float dt) {
		final IVector oldSpeed = speed.multiply(1);
		
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

		// calculate actual distance travelled by taking the average of old and new distance --> WHY??
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
			
			// it is possible that we're rolling now
			if (mIsRolling || isRolling()) {
				moveRolling(newDt);
			} else {
				moveFlying(newDt);
			}
			
			return;
		}
	}
	
	public void reset() {
		airFriction.setDirection(0, 0, 0);
		trace.reset();
		mRollingShape = null;
		mLastMovementCollision = null;
		mIsRolling = false;
		mShape.moveTo(Settings.getBallStartPosition());
		speed.setDirection(Settings.getBallStartSpeed()); // watch out with new
															// movables...
	}
	
	public void setRandomDirection() {
		final Random rnd = new Random();
		// create random speed vector with x in [-10..10], y in [-5..5], z in [-10..10]
		final float x =  Math.round((rnd.nextFloat() - 0.5f) * 200) / 10f;
		final float y =  Math.round((rnd.nextFloat() - 0.5f) * 100) / 10f;
		final float z =  Math.round((rnd.nextFloat() - 0.5f) * 200) / 10f;
		final IVector speed = new Vector(x, y, z);
		
		Settings.setBallStartSpeed(speed);
		
		Log.i(TAG, "Assigned new random speed " + speed + " to movable " + mShape.tag);
	}

	private boolean isRolling() {
		// rolling if a collision with a "floor"-object happened last round and
		// the speed away from the floor is small
		return mLastMovementCollision != null
				&& ((Quadrilateral) mLastMovementCollision.collidedSolid)
						.getNonZeroDimension() == 1 && speed.getY() < ROLLING_THRESHOLD;
	}

	public float getPotentialEnergy(){
		return mShape.location.getY() * gravitation.getLength();
	}
	
	public float getKineticLinearEnergy(){
		return speed.getLength() * speed.getLength() * 0.5f;
	}
}
