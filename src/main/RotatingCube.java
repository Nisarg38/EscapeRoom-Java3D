package main;

import java.util.Random;


import java.awt.*;

import javax.swing.JPanel;

import org.jdesktop.j3d.examples.collision.Box;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.Font3D;
import org.jogamp.java3d.FontExtrusion;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Text3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.geometry.ColorCube;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;





public class RotatingCube{
	
	//Positions for color box's and array to save them
	private static Vector3d box_left = new Vector3d(-2.25, -0.25, -3);
	private static Vector3d box_leftMid = new Vector3d(-0.75, -0.25, -3);
	private static Vector3d box_rightMid = new Vector3d(0.75, -0.25, -3);
	private static Vector3d box_right = new Vector3d(2.25, -0.25, -3);
	private static Vector3d boxCoors[] = {box_left, box_leftMid, box_rightMid, box_right};
	
	//to store random numbers
	private static int randomNums[] = new int[4];
	
	
	//set colors for sequence.
	private static Color3f colors[] = {Commons.Red, Commons.Blue, Commons.Green, Commons.Yellow};
	
	
	//This is the stand for the rotating cube
	private static TransformGroup createColumn(double scale, Vector3d pos) {	
		Transform3D transM = new Transform3D();
		transM.set(scale, pos);  // Create base TG with 'scale' and 'position'
		TransformGroup baseTG = new TransformGroup(transM);
		
		Shape3D shape = new Box(2.5, 5, 2.5);
		baseTG.addChild(shape); // Create a column as a box and add to 'baseTG'

		Appearance app = shape.getAppearance();
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(Commons.Grey); // set column's color and make changeable
		app.setColoringAttributes(ca);

		return baseTG;
	}
	
	private static BranchGroup createCube(double scale, Vector3d pos) {
		BranchGroup BG = new BranchGroup();
		//initial value for counter
		int n = 0;
		//Set BG to be able to alter in behavior class
		BG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		BG.setCapability(BranchGroup.ALLOW_DETACH);
		BG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		BG.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		Transform3D transM = new Transform3D();
		Transform3D transWin = new Transform3D();
		//for cube
		transM.set(pos);
		//for result square
		transWin.set(new Vector3d(0,2,-3));
		
		//Create Score text
		Font3D font3D = new Font3D(new Font("Arial", Font.PLAIN, 10), new FontExtrusion());
		Appearance appTX = new Appearance();
		
		String s = String.format("Score: %d", n);
		
		Text3D poly = new Text3D(font3D, s, new Point3f(-100 , 50 ,-3));
		
		Shape3D text = new Shape3D(poly);
		//allow for text to be updated
		text.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		
		Transform3D scaler =  new Transform3D();
		scaler.setScale(0.025);
		TransformGroup sceneTX = new TransformGroup(scaler);
		text.setAppearance(appTX);
		
		sceneTX.addChild(text);
		
		BranchGroup combo = new BranchGroup();
		TransformGroup tg1 = new TransformGroup(transM);
		TransformGroup baseTG = new TransformGroup();
		baseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		baseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		
		//appearance for result square
		Appearance app = new Appearance();
		TransformGroup tg = new TransformGroup(transWin);
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(0.0f, 0.0f, 0.0f);
		app.setColoringAttributes(ca);
		
		//result square
		Shape3D boxWin = new Box(2, 2, 0);
		boxWin.setAppearance(app);
		
		//create first sequence
		combo = createColorCombo(0.5);
		
		//cube for rotation
		ColorCube cube = new ColorCube(scale);
		baseTG.addChild(cube);
		BG.addChild(combo);
		
		//Behavior for sequence, cube and results
		CubeBehavior cb = new CubeBehavior(baseTG, randomNums, boxWin, BG, text);
		cb.setSchedulingBounds(new BoundingSphere());
		tg.addChild(boxWin);
		
		//add it all to the group
		BG.addChild(sceneTX);
		BG.addChild(cb);
		BG.addChild(tg);
		tg1.addChild(baseTG);
		BG.addChild(tg1);
		
		
		return BG;
	}
	
	//for creating the sequence
	public static BranchGroup createColorCombo(double scale) {
		BranchGroup BG = new BranchGroup();
		BG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		BG.setCapability(BranchGroup.ALLOW_DETACH);
		Transform3D trans1 = new Transform3D();
		Transform3D trans2 = new Transform3D();
		Transform3D trans3 = new Transform3D();
		Transform3D trans4 = new Transform3D();
		 
		Transform3D trans[] = {trans1, trans2, trans3, trans4};
		
		//generate random seed and set upper bound to nums will be 0-3
		Random rand =  new Random(System.currentTimeMillis());
		int upper = 4;
		int random_Num;
		
		//set transforms
		for(int i = 0; i < 4; i++) {
			trans[i].set(scale, boxCoors[i]);
		}
		
		//create each color square for sequence
		for(int i = 0; i < 4; i++){
			random_Num = rand.nextInt(upper);
			Appearance app = new Appearance();
			//set translation
			TransformGroup tg = new TransformGroup(trans[i]);
			ColoringAttributes ca = new ColoringAttributes();
			//set appropriate color
			ca.setColor(colors[random_Num]);
			//save number
			randomNums[i] = random_Num;
			app.setColoringAttributes(ca);
			//create box and add it to group
			Shape3D box = new Box(2, 2, 0);
			box.setAppearance(app);
			
			TransformGroup boxTG = new TransformGroup();
			boxTG.addChild(box);
			tg.addChild(boxTG);
			BG.addChild(tg);
			
		}
		
		return BG;
		
	}

	public static BranchGroup createScene() {
		BranchGroup sceneBG = new BranchGroup();		     // create 'objsBG' for content
		TransformGroup sceneTG = new TransformGroup();       // create a TransformGroup (TG)
		sceneBG.addChild(sceneTG);	                         // add TG to the scene BranchGroup
		
		//Alyssa's Appearance Class for AppearanceExtra
		sceneBG.addChild(AppearanceExtra.createBackground("backgroundLight.jpg"));
		sceneTG.addChild(createColumn(0.5, new Vector3d(0,-2.5,0)));
		sceneTG.addChild(createCube(0.5, new Vector3d(0,-0.75,0)));
		
		/*
		 * Use addLights for testing purpose ONLY
		 */
		//AppearanceExtra.addLights(sceneTG);
		
		
		sceneBG.compile(); 		// optimize objsBG
		return sceneBG;
	}
	
	
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				Commons.setEye(new Point3d(0, 2.5, 7.5));
				new Commons.MyGUI(createScene(), "JJ's Rotating Cube");
			}
		});
	}
	

	
}