package main;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JPanel;

import org.jdesktop.j3d.examples.collision.Box;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Cylinder;
import org.jogamp.vecmath.*;




	public class WheelPuzzle extends JPanel {
	private static final long serialVersionUID = 1L;
	private static int count=0;
	public static boolean inCollision = false;
	
	private static int randomNums[] = new int[3];

	private static Point3d pt_zero = new Point3d(0d, 0d, 0d);

	
	private static Shape3D line(Point3f pt) {
		LineArray lineArr = new LineArray(2, LineArray.COLOR_3 | LineArray.COORDINATES);
		///y
		lineArr.setCoordinate(0, pt);
		lineArr.setCoordinate(1, new Point3f(0.0f, 0.0f, 0.0f));
		lineArr.setColor(0, new Color3f(0.0f,0.0f,0.0f));
		lineArr.setColor(1, new Color3f(0.0f,0.0f,0.0f));
		
		Appearance app = new Appearance();
		ColoringAttributes ca = new ColoringAttributes(new Color3f(0.0f,0.0f,0.0f), ColoringAttributes.FASTEST);
		app.setColoringAttributes(ca);
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		
		
		return new Shape3D(lineArr, app);

	}
	
	public static void inCollision(boolean coll) {
		inCollision = coll;
		//System.out.println(inCollision);
	} 
	
	private static TransformGroup createColumn(double scale, Vector3d pos) {	
		Transform3D transM = new Transform3D();
		transM.set(scale, pos);  // Create base TG with 'scale' and 'position'
		TransformGroup baseTG = new TransformGroup(transM);
		
		Shape3D shape = new Box(2.5, 4.5, 1.0);
		baseTG.addChild(shape); // Create a column as a box and add to 'baseTG'
		ColoringAttributes colour = new ColoringAttributes(Commons.Green, ColoringAttributes.FASTEST);

		Appearance app = shape.getAppearance();
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(0.0f, 0.0f, 0.0f); // set column's color and make changeable
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		app.setColoringAttributes(ca);
		
		CollisionDetectLines cd = new CollisionDetectLines(shape, inCollision);
		cd.setSchedulingBounds(new BoundingSphere(pt_zero, 10d));
		
		
		baseTG.addChild(cd);

		return baseTG;
	}
	
	
	
	private static BranchGroup Cylinder(float scale, float zV) {
		BranchGroup BG = new BranchGroup();
		
		String names[] = {
				"Back",
				"Front"
		};
		Color3f colors[] = {
				new Color3f(0.0f,0.0f,1.0f),
				new Color3f(1.0f,0.0f,0.0f)
		};
		
		Appearance app = new Appearance();
		ColoringAttributes ca = new ColoringAttributes(Commons.Grey, ColoringAttributes.FASTEST);
		app.setColoringAttributes(ca);
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		app.setMaterial(AppearanceExtra.setMaterial(colors[count]));
		
		Cylinder cylinder = new Cylinder((1f)*scale, zV, app);
		
		cylinder.setUserData(0);				///original 
		cylinder.setName(names[count++]);		///static variable to change the name based on how many times sphere is called
		
		
		Transform3D rotate = new Transform3D();
		rotate.rotX(Math.PI/2);

		TransformGroup tg1 = new TransformGroup();
		TransformGroup tg2 = new TransformGroup(rotate);
		tg2.addChild(cylinder);
		tg1.addChild(tg2);
		BG.addChild(tg1);
		
		return BG;
		
	}
	public static BranchGroup createMeasure() {
		BranchGroup BG = new BranchGroup();
		//BG.addChild(line(new Point3f( 1.0f,5.0f, 0)));
		//BG.addChild(line(new Point3f( -1.0f,5.0f, 0)));
		//BG.addChild(createColumn(0.5, new Vector3d( 0f,4.35f, 0)));
		BG.addChild(Cylinder(3, 1f));
		return BG;
	}

	
	public static BranchGroup createDial() {
		BranchGroup BG = new BranchGroup();
		
		Transform3D trans = new Transform3D();
		//trans.setTranslation(new Vector3f(0,0,1));
		
		
		Random rand =  new Random(System.currentTimeMillis());
		int upper = 21;
		int random_Num;
		

		
		for(int i = 0; i < 3; i++){
			random_Num = rand.nextInt(upper);
			System.out.println(random_Num);
			randomNums[i] = random_Num;
		}
		

		
		Font3D font3D = new Font3D(new Font("Arial", Font.PLAIN, 10), new FontExtrusion());
		Appearance appTX = new Appearance();
		
		String s = String.format("");
		
		Text3D poly = new Text3D(font3D, s, new Point3f(-21 , 25 , 4));
		
		Shape3D text = new Shape3D(poly);
		//allow for text to be updated
		text.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		
		Transform3D scaler =  new Transform3D();
		scaler.setScale(0.25);
		TransformGroup sceneTX = new TransformGroup(scaler);
		text.setAppearance(appTX);
		
		sceneTX.addChild(text);

		
		TransformGroup tg1 = new TransformGroup(trans);
		TransformGroup tg2 = new TransformGroup();
		TransformGroup lns = new TransformGroup();
		lns.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tg2.addChild(Cylinder(2, 1.5f));
		
		
		TransformGroup Col = new TransformGroup();
		Col.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Col.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		Shape3D shape = line(new Point3f( 0.0f,5.0f, 0.0f));
		
		CollisionDetectLines cd = new CollisionDetectLines(shape, inCollision);
		cd.setSchedulingBounds(new BoundingSphere(pt_zero, 10d)); // detect column's collision
		tg2.addChild(shape);
		RotationInterpolator ri1 = ri(2500, tg2, 'x', new Point3d(0,0,0));
		BG.addChild(ri1);
		///key presses that pause or un-pause the rotations depending on key pressed
		///press z to stop the rotation
		
		
		System.out.println(inCollision);
		DialBehavior sb1 = new DialBehavior(ri1, KeyEvent.VK_Z, cd, lns, randomNums, text);
		sb1.setSchedulingBounds(new BoundingSphere());
		lns.addChild(line(new Point3f( 1.0f, 5.0f, 0)));
		lns.addChild(line(new Point3f( -1.0f ,5.0f, 0)));
		lns.addChild(createColumn(0.5, new Vector3d( 0f,4.35f, 0)));
		BG.addChild(sceneTX);
		BG.addChild(lns);
		BG.addChild(sb1);
				
		tg1.addChild(tg2);
		BG.addChild(tg1);
		
		return BG;
	}
	
	public static RotationInterpolator ri(int rotationnumber, TransformGroup tg, char option, Point3d pos) {
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D axis = new Transform3D();
		switch(option) {
		case 'x':
			axis.rotX(Math.PI/2);
			break;
		case 'z':
			axis.rotZ(Math.PI/2);
			break;
		default:
			///case Y
			axis.rotY(Math.PI/2);
			break;
		}
		
		Alpha a = new Alpha(-1, rotationnumber);
		RotationInterpolator rot = new RotationInterpolator(a, tg, axis, 0.0f, (float) Math.PI*2);
		rot.setSchedulingBounds(new BoundingSphere(pos, 100));
		
		return rot;
	}
	
	public static BranchGroup buildWin() {
		BranchGroup BG = new BranchGroup();
		TransformGroup TG = new TransformGroup();
		if(CollisionDetectLines.collided && DialBehavior.stopped) {
			AppearanceExtra.addptLights(TG, Commons.Green);
			RenderText.letters3D("Unlocked", 1.0d , new Color3f(0.0f,0.0f,0.0f));
		}
		return BG;
	}

	/* a function to create and return the scene BranchGroup */
	public static BranchGroup createScene() {
		BranchGroup sceneBG = new BranchGroup();		     // create 'objsBG' for content
		TransformGroup sceneTG = new TransformGroup();       // create a TransformGroup (TG)
		sceneBG.addChild(sceneTG);	                         // add TG to the scene BranchGroup
		
		sceneBG.addChild(AppearanceExtra.createBackground("backgroundLight.jpg"));

		
		/*
		 * Use adLights() for testing purpose ONLY 
		 */
		//AppearanceExtra.addLights(sceneTG);
		
		sceneBG.addChild(createMeasure());
		sceneBG.addChild(createDial());
		sceneBG.addChild(buildWin());
		sceneBG.compile(); 		// optimize objsBG
		return sceneBG;
	}
	
		/* a function to allow key navigation with the ViewingPlateform */

		/* the main entrance of the application via 'MyGUI()' of "CommonXY.java" */
		public static void main(String[] args) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					//CommonsEK.setEye(new Point3d(0.0, 0.35, 25.0));
					new Commons.MyGUI(createScene(), "Wheel Puzzle");
				}
			});
		}
	}
