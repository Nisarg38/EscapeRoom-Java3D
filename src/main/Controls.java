package main;

import java.awt.event.MouseEvent;
import java.util.Iterator;

import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.WakeupOnAWTEvent;
import org.jogamp.vecmath.Vector3f;
import org.jogamp.java3d.WakeupCriterion;

public class Controls extends Behavior{
	
	private TransformGroup targetTG;
	private WakeupOnAWTEvent wEnter;
	private float transY = 0.0f;
	private Transform3D trans = new Transform3D();

	public Controls(TransformGroup targetTG) {
		this.targetTG = targetTG;
	}
	
	//Set initial wakeup condition
	public void initialize() {
		wEnter = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
		wakeupOn(wEnter);
	}

	@Override
	public void processStimulus(Iterator<WakeupCriterion> criteria) {
		
		if (++transY > 6.0f) {
			transY = -2.0f;
		}
		
		trans.set(new Vector3f(0.0f, transY/10.0f, 0.0f));
		targetTG.setTransform(trans);
		wakeupOn(wEnter);	
		
	}
	
}