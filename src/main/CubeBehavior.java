package main;

import java.awt.AWTEvent;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Random;

import org.jdesktop.j3d.examples.collision.Box;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.Font3D;
import org.jogamp.java3d.FontExtrusion;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Text3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.TransparencyAttributes;
import org.jogamp.java3d.WakeupCondition;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnAWTEvent;
import org.jogamp.java3d.WakeupOnCollisionEntry;
import org.jogamp.java3d.WakeupOnCollisionExit;
import org.jogamp.java3d.WakeupOr;
import org.jogamp.java3d.utils.geometry.ColorCube;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;

public class CubeBehavior extends Behavior {

	//same as main program, used for in reset program
	private static Vector3d box_left = new Vector3d(-2.25, -0.25, -3);
	private static Vector3d box_leftMid = new Vector3d(-0.75, -0.25, -3);
	private static Vector3d box_rightMid = new Vector3d(0.75, -0.25, -3);
	private static Vector3d box_right = new Vector3d(2.25, -0.25, -3);
	private static Vector3d boxCoors[] = {box_left, box_leftMid, box_rightMid, box_right};
	
	private TransformGroup tg;
	private BranchGroup branch;
	private BranchGroup branch2;
	private Transform3D axis;
	private int combination = 0;
	private int currBox = 1;
	private int order[];
	private int correctCount = 0;
	private static int currScore;
	private static int randomNums[] = new int[4];
	private int childSave = 0;
	private Shape3D s;
	private Shape3D text;
	
	
	private boolean inMain;
	private boolean done = false;
	private boolean correct = false;
	private boolean inCorrect = false;
	
	protected WakeupCondition keyCriterion;
	
	private static Color3f colors[] = {Commons.Red, Commons.Blue, Commons.Green, Commons.Yellow};
	private Appearance shapeAppearance;
	private ColoringAttributes shapeColoring;

	
	//Controls
	//Q rotates left
	//E rotates right
	//Enter confirms choice
	//R resets game once combination is correct
	private int left = KeyEvent.VK_Q;
	private int right = KeyEvent.VK_E;
	private int enter = KeyEvent.VK_ENTER;
	private int reset = KeyEvent.VK_R;
	
	//For main program
	public CubeBehavior(TransformGroup sceneTG, int nums[], Shape3D result, BranchGroup BG, Shape3D scoreTX) {
		this.tg = sceneTG;
		this.order = nums;
		branch = BG;
		axis = new Transform3D();
		s=result;
		text = scoreTX;
		inMain = true;
		shapeAppearance = s.getAppearance();
		shapeAppearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		shapeColoring = shapeAppearance.getColoringAttributes();
	}
	
	//for reset program as not everything needs to be monitored and modified with new sequences
	public CubeBehavior(TransformGroup sceneTG, int nums[], Shape3D result, BranchGroup BG) {
		this.tg = sceneTG;
		this.order = nums;
		branch = BG;
		axis = new Transform3D();
		s=result;
		shapeAppearance = s.getAppearance();
		shapeAppearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		shapeColoring = shapeAppearance.getColoringAttributes();
	}
	
	
	
	
	@Override
	//basic control initialize
	public void initialize( )
	{
		WakeupCriterion[] keyEvents = new WakeupCriterion[2];
		keyEvents[0] = new WakeupOnAWTEvent( KeyEvent.KEY_PRESSED );
		keyEvents[1] = new WakeupOnAWTEvent( KeyEvent.KEY_RELEASED );
		keyCriterion = new WakeupOr( keyEvents );
		wakeupOn( keyCriterion );
	}

	@Override
	//basic control stimulus
	public void processStimulus( Iterator<WakeupCriterion> criteria)
	{
		WakeupCriterion wakeup;
		AWTEvent[] event;
		
		Color3f hilightClr1 = Commons.Green;
		Color3f hilightClr2 = Commons.Red;
		ColoringAttributes highlight1 = new ColoringAttributes(hilightClr1, ColoringAttributes.SHADE_GOURAUD);
		ColoringAttributes highlight2 = new ColoringAttributes(hilightClr2, ColoringAttributes.SHADE_GOURAUD);
		

		while(  criteria.hasNext())
		{
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
		
		
		
		//if full combination is correct
		if(correct == true) {
			//set result square to highlight green
			shapeAppearance.setColoringAttributes(highlight1);
			//reset tracker for correct
			correct = false;
			//sequence is done
			done = true;
			//only if we are in the main sequence edit score otherwise there will be overlap
			if(inMain) {
				currScore++;
				//used to update text
				updateText(currScore);
				if(currScore == 3) {
					DoorIndicator.setNeptune(true);
					DoorIndicator.checkNeptune();
					DoorIndicator.checkIFcomplete();
				}
			}
			//if any confirmed entry does not match seq
		}else if(combination <= 3 && inCorrect == true) {
			//set result square to red
			shapeAppearance.setColoringAttributes(highlight2);
			//reset color cube to start
			for(int i = 0; i < combination; i++) {
				rotLeftReset();
			}
			
			//reset all trackers
			correctCount = 0;
			combination = 0;
			correct = false;
			inCorrect = false;
			currBox = 1;
			
			//if in main then update score otherwise there will be overlap
			if(inMain) {
				currScore = 0;
				updateText(currScore);
			}
		}
		
		wakeupOn( keyCriterion );
	}
	
	//process the key clicks
	protected void processKeyEvent( KeyEvent event )
	{
		int keycode = event.getKeyCode( );
		
		//if enter is pressed, check if current combination number is the same as seq
		if(keycode == enter) {
			//check if correct
			checkCorrectness();
			//make sure no overflow
			if(currBox < 4) {
				currBox++;
			}	
		}
		//if r is pressed and the combination was fully correct
		else if(keycode == reset && done == true) {
			//reset tracker
			done = false;
			//reset the sequence to new numbers and colors
			resetCombo();
		}
		//if q or e then rotate accordingly
		else if(keycode == left || keycode == right){
			standardRot( keycode );
		}
		
		
		
	}
	
	//check if correct
	private void checkCorrectness(){
		
		if(combination <= 3 ) {
			//if current entry is the same as the current color square
			if(combination == order[currBox-1]) {
				//a correct color was entered
				correctCount++;
				//for debugging
				System.out.println("Correct");
				//reset coloring in case of previous failure or success 
				shapeAppearance.setColoringAttributes(shapeColoring);
				//if current entry is not the same as current color square
			}else if (combination != order[currBox-1]){
				//set tracker for checking incorrectness to true
				inCorrect = true;
				//for debugging
				System.out.println("False");
			}
		}
		
		//if all entry's are correct
		if(correctCount == 4) {
			//set tacker to true
			correct = true;
			//reset tracker
			correctCount = 0;
			//for debugging
			System.out.println("All correct!");
		}
		
		
	}	
	
	//for rotation
	private void standardRot( int keycode )
	{
		//if right/e
		if(keycode == right) {
			//monitor current rotation and account for going backwards and forwards
			if(combination < 3) {
				combination++;
			}else if(combination == 3){
				combination = 0;
			}
			//complete transform
			rotRight();
			
			
		}
		//if left/q
		else if(keycode == left) {
			//monitor overflow
			if(combination >  0 && combination <= 3) {
				combination--;
			}else if(combination == 0){
				combination = 3;
			}
			//complete transform
			rotLeft();
			
		}
		
		
	}
	
	//final update for rotating cube
	protected void updateTransform( )
	{
		tg.setTransform(axis);
	}
	
	//reset the colors and sequence
	protected void resetCombo() {
		
		//first reset cube to starting position
		for(int i = 0; i < combination; i++) {
			rotLeftReset();
		}
		
		//reset trackers
		correctCount = 0;
		combination = 0;
		inCorrect = false;
		correct = false;
		currBox = 1;
		
		//reset result square
		shapeAppearance.setColoringAttributes(shapeColoring);
		//remove the proper child from the group witch has been saved but the first one is 0 as it is the first item added to BG in RotatingCube in createCube
		branch.removeChild(childSave);
		//create new method
		branch2 = createCube(0.5, new Vector3d(0,-0.75,0));
		//add method
		branch.addChild(branch2);
		//save index of new child
		childSave = branch.indexOfChild(branch2);
		
		//set the new random numbers generated in the new branch to the order tracker
		order = randomNums;
	}
	
	//rotate the cube
	protected void doRotateY( double radians )
	{
		tg.getTransform(axis);
		Transform3D toMove = new Transform3D( );
		toMove.rotY(radians);
		axis.mul( toMove );
		updateTransform( );
	}
	
	//for right rotation
	protected void rotRight()
	{
		doRotateY(getRotateRightAmount());
	}

	//for left rotation
	protected void rotLeft( )
	{
		doRotateY(getRotateLeftAmount( ));
	}
	
	//for reset
	protected void rotLeftReset( )
	{
		doRotateY(getRotateLeftAmount( ));
	}
	
	//each direction has a different value
	protected double getRotateLeftAmount( )
	{
		return Math.PI/2;
	}

	protected double getRotateRightAmount( )
	{
		return -Math.PI/2;
	}
	
	//for new child added at reset
	private static BranchGroup createCube(double scale, Vector3d pos) {
		BranchGroup BG = new BranchGroup();
		
		
		BG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		BG.setCapability(BranchGroup.ALLOW_DETACH);
		BG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		BG.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		Transform3D transM = new Transform3D();
		Transform3D transWin = new Transform3D();
		transM.set(pos);
		transWin.set(new Vector3d(0,2,-3));
	
		
		BranchGroup combo = new BranchGroup();
		TransformGroup tg1 = new TransformGroup(transM);
		TransformGroup baseTG = new TransformGroup();
		baseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		baseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		
		Appearance app = new Appearance();
		TransformGroup tg = new TransformGroup(transWin);
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(0.0f, 0.0f, 0.0f);
		app.setColoringAttributes(ca);
		
		
		Shape3D boxWin = new Box(2, 0.5, 0);
		boxWin.setAppearance(app);
		
		
		combo = createColorCombo(0.5);
		
		ColorCube cube = new ColorCube(scale);
		baseTG.addChild(cube);
		BG.addChild(combo);
		
		System.out.println(BG.indexOfChild(combo));
		
		CubeBehavior cb = new CubeBehavior(baseTG, randomNums, boxWin, BG);
		cb.setSchedulingBounds(new BoundingSphere());
		
		BG.addChild(cb);
		tg1.addChild(tg);
		tg1.addChild(baseTG);
		BG.addChild(tg1);
		
		return BG;
	}
	
	public static BranchGroup createColorCombo(double scale) {
		BranchGroup BG = new BranchGroup();
		BG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		BG.setCapability(BranchGroup.ALLOW_DETACH);
		Transform3D trans1 = new Transform3D();
		Transform3D trans2 = new Transform3D();
		Transform3D trans3 = new Transform3D();
		Transform3D trans4 = new Transform3D();
		Transform3D trans[] = {trans1, trans2, trans3, trans4};
		int seq[] = new int[4];
		
		Random rand =  new Random(System.currentTimeMillis());
		int upper = 4;
		int random_Num;
		

		for(int i = 0; i < 4; i++) {
			trans[i].set(scale, boxCoors[i]);
		}
		
		for(int i = 0; i < 4; i++) {
			random_Num = rand.nextInt(upper);
			System.out.println(random_Num);
			Appearance app = new Appearance();
			TransformGroup tg = new TransformGroup(trans[i]);
			ColoringAttributes ca = new ColoringAttributes();
			ca.setColor(colors[random_Num]);
			randomNums[i] = random_Num;
			app.setColoringAttributes(ca);
			Shape3D box = new Box(2, 2, 0);
			box.setAppearance(app);
			
			TransformGroup boxTG = new TransformGroup();
			boxTG.addChild(box);
			tg.addChild(boxTG);
			BG.addChild(tg);
			
		}
		
		
		return BG;
	}
	
	//update the score
	private void updateText(int currScore) {
		
		int n = currScore;
		Font3D font3D = new Font3D(new Font("Arial", Font.PLAIN, 10), new FontExtrusion());
		
		String s = String.format("Score: %d", n);
		
		Text3D poly = new Text3D(font3D, s, new Point3f(-100 , 50 ,-3));
		
		text.setGeometry(poly);
		
		
	}
	
	
}
