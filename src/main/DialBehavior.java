package main;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.Font3D;
import org.jogamp.java3d.FontExtrusion;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Text3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnAWTEvent;
import org.jogamp.java3d.WakeupOnCollisionEntry;
import org.jogamp.java3d.WakeupOnCollisionExit;
import org.jogamp.vecmath.Point3f;

public class DialBehavior extends Behavior {

	private RotationInterpolator r;
	private int key;
	private int level = 0;
	private int count = 0;
	private boolean win = false;

	private TransformGroup scene;
	private Shape3D text;
	private Transform3D rotZ;
	private int randPos[];
	private int speeds[] = {1500, 1000};
	private CollisionDetectLines c;

	private Boolean paused;
	public static Boolean stopped = false;
	private WakeupOnAWTEvent wEnter;
	private WakeupOnCollisionEntry cEnter;
	private WakeupOnCollisionExit cExit;


	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		wEnter = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
		wakeupOn(wEnter);
	}

	@Override
	public void processStimulus(Iterator<WakeupCriterion> arg0) {
		// TODO Auto-generated method stub
		KeyEvent event = (KeyEvent) wEnter.getAWTEvent() [0];
		if(key == event.getKeyCode()) {
			paused = !paused;			///if paused, unpause, if unpaused, pause
		}
		///pause or play the rotation interpolator r
		if(paused) {
			r.getAlpha().pause();
			stopped = true;
			if(c.collided == true) {
				win = true;
			}
		}
		else if(win == true && level < 4) {
			win = false;
			r.setAlpha(new Alpha(-1, speeds[count]));
			doRotateZ();
			if(count < 1) {
				count++;
			}
		}
		else if(!paused){
			r.getAlpha().resume();
			stopped = false;
		}

		if(level == 4) {
			addText();
			r.setAlpha(new Alpha(-1, 0));
			DoorIndicator.setSun(true);
			DoorIndicator.checkSun();
			DoorIndicator.checkIFcomplete();
		}



		wakeupOn(wEnter);
	}

	public void addText() {
		Font3D font3D = new Font3D(new Font("Arial", Font.PLAIN, 10), new FontExtrusion());
		Appearance appTX = new Appearance();

		String s = String.format("Unlocked!");

		Text3D poly = new Text3D(font3D, s, new Point3f(-21 , 25 , 4));

		text.setGeometry(poly);

	}

	protected void doRotateZ()
	{
		scene.getTransform(rotZ);
		Transform3D toMove = new Transform3D( );
		if(level < 3) {
			toMove.rotZ(Math.PI / 10 * randPos[level]);
		}
		rotZ.mul( toMove );

		level++;
		System.out.println(level);

		updateTransform( );
	}

	protected void updateTransform( )
	{
		scene.setTransform(rotZ);
	}

	public DialBehavior(RotationInterpolator r, int key) {
		this.r = r;
		this.key = key;
		paused = false;
	}

	public DialBehavior(RotationInterpolator r, int key, CollisionDetectLines cd, TransformGroup posts, int random[], Shape3D txt) {
		this.r = r;
		this.key = key;
		scene = posts;
		rotZ = new Transform3D();
		randPos = random;
		c = cd;
		text = txt;

		paused = false;
	}


}