package ch.squash.shapes.common;

import android.util.Log;
import ch.squash.shapes.common.AbstractShape.SolidType;
import ch.squash.shapes.shapes.Ball;
import ch.squash.shapes.shapes.Quadrilateral;

public final class Collision {
	private final static String TAG = Collision.class.getSimpleName();
	private final static float EPSILON = (float) Math.pow(10, -6);

	public final IVector collisionPoint;
	public final float lambda;
	public final float timePercentage;
	public final IVector normalForce;
	public final IVector solidNormalVector;
	
	private Collision(final IVector collisionPoint, final float lambda, final float timePercentage, final IVector normalForce, final IVector solidNormalVector) {
		this.collisionPoint = collisionPoint;
		this.lambda = lambda;
		this.timePercentage = timePercentage;
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
		Log.i(TAG, "Detecting collision with " + quad.tag);
		// prepare variables
		final IVector n = quad.getNormalVector();

		// distance of quad to origin
		final float b = quad.getDistanceToOrigin();
		
		// reachable sphere
		// distance middle of sphere to area:
		final float d = (n.getX() * ball.location.getX() + n.getY() * ball.location.getY() + n.getZ() * ball.location.getZ() - b) / n.getLength(); 
		
//		if (d < 0){
//			Log.d(TAG, "n=" + n + ", ball=" + ball.location);
//		}
		
		// ball wont touch solid, return
		if (d > travelled.getLength()){
			return null;
		}
		
		final IVector r = travelled;
		
		// factor of direction of ball to area
		final float lambda = (b - ball.location.getX() * n.getX() - ball.location.getY() * n.getY() - ball.location.getZ() * n.getZ())
				/ (r.getX() * n.getX() + r.getY() * n.getY() + r.getZ() * n.getZ());
		
		if (lambda < 0 || lambda > 1)
			return null;

		Log.v(TAG, "Location=" + ball.location + ", speed=" + travelled);
		Log.v(TAG, "b=" + b + ", d=" + d + ", Lambda=" + lambda);
		
		// intersection of ball and area
		IVector s = new Vector(ball.location.getX() + lambda * r.getX(), ball.location.getY() + lambda * r.getY(), ball.location.getZ() + lambda * r.getZ());
		if (!quad.isPointInQuad(s)){
			IVector nNorm = n.multiply(1 / n.getLength());
			
			for (int i = 0; i < 5; i++)
				if (nNorm.getLength() == 1)
					break;
				else
					nNorm = nNorm.multiply(1 / nNorm.getLength());
			
			if (nNorm.getLength() != 1)
				Log.e(TAG, "Failed to normalize vector");
			
			final float dStoQuad = -(nNorm.getX() * s.getX() + nNorm.getY() * s.getY() + nNorm.getZ() * s.getZ() - b); 
			
			s = s.add(nNorm.multiply(dStoQuad));
			Log.w(TAG, "New S=" + s + ", nNorm=" + nNorm);
		}
		
		// check x
		if (quad.edges[0] < quad.edges[6]) {
			if (s.getX() + EPSILON < quad.edges[0]
					|| s.getX() > quad.edges[6] + EPSILON)
				return null;
		} else if (s.getX() > quad.edges[0] + EPSILON
				|| s.getX() + EPSILON < quad.edges[6])
			return null;

		// check y
		if (quad.edges[1] < quad.edges[7]) {
			if (s.getY() + EPSILON < quad.edges[1]
					|| s.getY() > quad.edges[7] + EPSILON)
				return null;
		} else if (s.getY() > quad.edges[1] + EPSILON
				|| s.getY() + EPSILON < quad.edges[7])
			return null;

		// check z
		if (quad.edges[2] < quad.edges[8]) {
			if (s.getZ() + EPSILON < quad.edges[2]
					|| s.getZ() > quad.edges[8] + EPSILON)
				return null;
		} else if (s.getZ() > quad.edges[2] + EPSILON
				|| s.getZ() + EPSILON < quad.edges[8])
			return null;
		
		Log.e(TAG, "Collision with " + quad.tag + " after " + d + "m from " + travelled.getLength() + "m (" + (d/travelled.getLength() * 100) + "%)");
		Log.e(TAG, "Collision point=" + s + " is in quad=" + quad.isPointInQuad(s));
		return new Collision((Vector)s, lambda, d/travelled.getLength(), getNormalForce((Vector)n), (Vector)n);
	}

	
	private static IVector getNormalForce(final IVector areaNormal){
		final IVector a = new Vector(0, 1, 0);
		final float angle = (float) Math.acos(areaNormal.multiply(a) / areaNormal.getLength() / a.getLength());
		
		Log.v(TAG, "a=" + a + ", angle=" + (angle * 180 / Math.PI) + ", cos=" + Math.cos(angle));
		
		return Movable.getGravitation().multiply(-(float)Math.cos(angle));
	}
}
