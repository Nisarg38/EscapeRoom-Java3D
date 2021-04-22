package main;

import java.awt.AWTEvent;

import java.awt.event.KeyEvent;
import java.util.Iterator;

import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.WakeupCondition;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnAWTEvent;
import org.jogamp.java3d.WakeupOr;
import org.jogamp.vecmath.Vector3d;

/*
 * In your create scene method, write
 * 
 * KeyMovement km = new KeyMovement(sceneTG); 	
 * km.setSchedulingBounds(new BoundingSphere());
 * sceneBG.addChild(km);
 * 
 * where sceneTG is TransformGroup and sceneBG is BranchGroup
 * 
 * 
 * To change keys outside class call km.set______(your key or movement);
 * 
 * 
 */

public class KeyMovement extends Behavior {
	
	protected static final double NORMAL = 1.0;
	protected static final double SLOW = 0.5;

	protected TransformGroup trans;
	protected Transform3D trans3D;
	protected WakeupCondition keyCriterion;

	private double rot = Math.PI / 16.0;
	
	private double move = 1;
	private double speed = NORMAL;

	private final double forwardScale = 0.5;
	private final double backwardScale = 0.25;

	private int fw = KeyEvent.VK_UP;
	private int bk = KeyEvent.VK_DOWN;
	private int lf = KeyEvent.VK_LEFT;
	private int rt = KeyEvent.VK_RIGHT;

	public KeyMovement( TransformGroup scene ) {
		trans = scene;
		trans3D = new Transform3D( );
	}

	public void initialize( ) {
		WakeupCriterion[] keyEvents = new WakeupCriterion[2];
		keyEvents[0] = new WakeupOnAWTEvent( KeyEvent.KEY_PRESSED );
		keyEvents[1] = new WakeupOnAWTEvent( KeyEvent.KEY_RELEASED );
		keyCriterion = new WakeupOr( keyEvents );

		wakeupOn( keyCriterion );
	}

	public void processStimulus( Iterator<WakeupCriterion> criteria) {
		
		WakeupCriterion wakeup;
		AWTEvent[] event;

		while(criteria.hasNext()) {
			
			wakeup = (WakeupCriterion) criteria.next();

			if( !(wakeup instanceof WakeupOnAWTEvent) )	
				continue;

			event = ((WakeupOnAWTEvent)wakeup).getAWTEvent( );

			for( int i = 0; i < event.length; i++ )
			{
				if( event[i].getID( ) == KeyEvent.KEY_PRESSED )
				{
					processKeyEvent( (KeyEvent)event[i] );
				}
			}
		}

		wakeupOn( keyCriterion );
	}

	protected void processKeyEvent( KeyEvent event )
	{
		int keycode = event.getKeyCode( );
		
		//If shift is pressed, slow down otherwise normal
		if(event.isShiftDown( )) 
			speed = SLOW;
		else 
			speed = NORMAL;
		
		standardMove( keycode );
	}

	//Moves forward backward or rotates left right
	private void standardMove( int keycode )
	{
		if(keycode == fw)
			moveForward( );
		else if(keycode == bk)
			moveBackward( );
		else if(keycode == lf)
			rotLeft();
		else if(keycode == rt)
			rotRight( );
	}

	//Buffer methods to set movement positions
	private void moveForward( )
	{
		doMove( new Vector3d( 0.0,0.0, forwardScale * speed ) );
	}

	private void moveBackward( )
	{
		doMove( new Vector3d( 0.0,0.0, -backwardScale * speed ) );
	}

	protected void rotRight( )
	{
		doRotateY( getRotateRightAmount( ) );
	}


	protected void rotLeft( )
	{
		doRotateY( getRotateLeftAmount( ) );
	}

	protected void updateTransform( )
	{
		trans.setTransform( trans3D );
	}
	
	//Transforms the scene either rotation or movement
	protected void doRotateY( double radians )
	{
		trans.getTransform( trans3D );
		Transform3D toMove = new Transform3D( );
		toMove.rotY( radians );
		trans3D.mul( toMove );
		updateTransform( );
	}

	protected void doMove( Vector3d theMove )
	{
		trans.getTransform( trans3D );
		Transform3D toMove = new Transform3D( );
		toMove.setTranslation( theMove );
		trans3D.mul( toMove );
		updateTransform();
	}

	//Returns the proper values of movement, rotation left and right rates
	protected double getMovementRate( )
	{
		return move * speed;
	}

	protected double getRotateLeftAmount( )
	{
		return rot * speed;
	}

	protected double getRotateRightAmount( )
	{
		return -rot * speed;
	}

	//Allows programmer to change values on the fly in their create scene method
	public void setRotateYAmount( double radians )
	{
		rot = radians;
	}

	public void setMovementRate( double meters )
	{
		move = meters; // Travel rate in meters/frame
	}

	public void setForwardKey( int key )
	{
		fw = key;
	}

	public void setBackKey( int key )
	{
		bk = key;
	}

	public void setLeftKey( int key )
	{
		lf = key;
	}
	
	public void setRightKey( int key )
	{
		rt = key;
	}
	
}