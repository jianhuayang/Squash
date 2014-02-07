package ch.squash.simulation.shapes.common;

import android.util.Log;
import ch.squash.simulation.shapes.common.AbstractShape.SolidType;
import ch.squash.simulation.shapes.shapes.Ball;
import ch.squash.simulation.shapes.shapes.Quadrilateral;

public final class Collision {
	private final static String TAG = Collision.class.getSimpleName();

	public final IVector collisionPoint;
	public final float travelPercentage;
	public final IVector normalForce;
	public final IVector solidNormalVector;
	
	private Collision(final IVector collisionPoint, final float travelPercentage, final IVector normalForce, final IVector solidNormalVector) {
		this.collisionPoint = collisionPoint;
		this.travelPercentage = travelPercentage;
		this.normalForce = normalForce;
		this.solidNormalVector = solidNormalVector;
	}

	public static Collision getCollision(final AbstractShape moving, final IVector travelled, final AbstractShape stationary) {
		String error = null;

		// ensure we got valid parameters
		if (moving.getSolidType() == SolidType.NONE
				|| stationary.getSolidType() == SolidType.NONE)
			error = "Checking collision of unsolid shapes";
		if (!moving.isMovable())
			error = "Moving shape is not moving";
		if (moving.getMovable().speed.getLength() == 0)
			error = "Moving shape is not moving";
		if (stationary.isMovable()
				&& stationary.getMovable().speed.getLength() != 0)
			error = "Stationary shape is moving";
		if (moving.getSolidType() != SolidType.SPHERE
				|| stationary.getSolidType() != SolidType.AREA)
			error = "Checking collision of unknown types";

		if (error != null) {
			Log.e(TAG, error);
			return null;
		}

		if (moving instanceof Ball && stationary instanceof Quadrilateral)
			return getBallQuadrilateralCollision((Ball) moving, travelled,
					(Quadrilateral) stationary);

		Log.e(TAG, "Handling collision between " + moving + " and "
				+ stationary + " not implemented");
		return null;
	}
	
	private static Collision getBallQuadrilateralCollision(final Ball ball, final IVector travelled,
			final Quadrilateral quad) {
//		Log.i(TAG, "Detecting collision with " + quad.tag);
		
		// TODO: Not treat ball as a point anymore (but as a sphere)		
		// get distance to quad
		final float distanceToQuad = quad.getDistanceToPoint(ball.location);
		
		if (AbstractShape.areEqual(0, distanceToQuad))
			return null;
		
		// if the quad is too far away, return
		if (distanceToQuad > travelled.getLength())
			return null;
		
		final IVector intersection = 
				quad.getIntersectionWithPlane(ball.location, travelled);
//					.add(quad.getNormalVector().multiply(-0.5f / quad.getNormalVector().getLength()));
		
		final float lambdax = (intersection.getX() - ball.location.getX()) / travelled.getX();
		final float lambday = (intersection.getY() - ball.location.getY()) / travelled.getY();
		final float lambdaz = (intersection.getZ() - ball.location.getZ()) / travelled.getZ();

		if (!AbstractShape.areEqual(travelled.getX(), 0)
				&& (lambdax < 0 || lambdax > 1)) {
			Log.i(TAG, "lx=" + lambdax);
			return null;
		}
		if (!AbstractShape.areEqual(travelled.getY(), 0)
				&& (lambday < 0 || lambday > 1)) {
			Log.i(TAG, "ly=" + lambday);
			return null;
		}
		if (!AbstractShape.areEqual(travelled.getZ(), 0)
				&& (lambdaz < 0 || lambdaz > 1)) {
			Log.i(TAG, "lz=" + lambdaz);
			return null;
		}
//		IVector newInters = null;
//		if (Math.abs(lambdax) < Math.abs(lambday)){
//			if (Math.abs(lambdax) < Math.abs(lambdaz))
//				newInters = new Vector(	ball.location.getX() + travelled.getX() * 0.9f * lambdax, 
//										ball.location.getY() + travelled.getY() * 0.9f * lambdax, 
//										ball.location.getZ() + travelled.getZ() * 0.9f * lambdax);
//			else
//				newInters = new Vector(	ball.location.getX() + travelled.getX() * 0.9f * lambdaz, 
//						ball.location.getY() + travelled.getY() * 0.9f * lambdaz, 
//						ball.location.getZ() + travelled.getZ() * 0.9f * lambdaz);
//		}else if (Math.abs(lambday) < Math.abs(lambdaz))
//				newInters = new Vector(	ball.location.getX() + travelled.getX() * 0.9f * lambday, 
//										ball.location.getY() + travelled.getY() * 0.9f * lambday, 
//										ball.location.getZ() + travelled.getZ() * 0.9f * lambday);
//		else
//			newInters = new Vector(	ball.location.getX() + travelled.getX() * 0.9f * lambdaz, 
//									ball.location.getY() + travelled.getY() * 0.9f * lambdaz, 
//									ball.location.getZ() + travelled.getZ() * 0.9f * lambdaz);
		
		// if ball does not cross quad, return null
		if (!AbstractShape.areEqual(quad.getDistanceToPoint(intersection), 0)){
			Log.i(TAG, "distancetointers=" + quad.getDistanceToPoint(intersection) + ", inters=" + intersection);
			return null;
		}
		
		// collision happens, return collision object	
		// TODO: check necessity of parameters for collision ctor
		Log.w(TAG, "Collision with " + quad.tag + " after " + distanceToQuad + "m from " + travelled.getLength() + "m (" + (distanceToQuad/travelled.getLength() * 100) + "%)");
		Log.w(TAG, "Ball is at " + ball.location + ", travelling " + travelled + " to inters=" + intersection);
//		Log.i(TAG, "lx=" + lambdax + ", ly=" + lambday + ", lz=" + lambdaz);
		return new Collision(intersection, distanceToQuad / travelled.getLength(), getNormalForce(quad.getNormalVector()), quad.getNormalVector());
	}

	private static IVector getNormalForce(final IVector areaNormal){
		final IVector a = new Vector(0, 1, 0);
		final float angle = (float) Math.acos(areaNormal.multiply(a) / areaNormal.getLength() / a.getLength());
		
		return Movable.getGravitation().multiply(-(float)Math.cos(angle));
	}
}
