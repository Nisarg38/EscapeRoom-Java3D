/* *********************************************************
 * For use by students to work on assignments and project.

 * Permission required material. Contact: xyuan@uwindsor.ca 
 **********************************************************/
package main;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.ColorCube;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

public class Commons extends JPanel {
	private static final long serialVersionUID = 1L;
	public final static Color3f Red = new Color3f(1.0f, 0.0f, 0.0f);
	public final static Color3f Green = new Color3f(0.0f, 1.0f, 0.0f);
	public final static Color3f Blue = new Color3f(0.0f, 0.0f, 1.0f);
	public final static Color3f Yellow = new Color3f(1.0f, 1.0f, 0.0f);
	public final static Color3f Cyan = new Color3f(0.0f, 1.0f, 1.0f);
	public final static Color3f Orange = new Color3f(1.0f, 0.5f, 0.0f);
	public final static Color3f Magenta = new Color3f(1.0f, 0.0f, 1.0f);
	public final static Color3f White = new Color3f(1.0f, 1.0f, 1.0f);
	public final static Color3f Grey = new Color3f(0.5f, 0.5f, 0.5f);
	public final static Color3f None = new Color3f(0f, 0f, 0f);
	public final static Color3f[] Clrs = {Blue, Green, Red, Yellow, 
			Cyan, Orange, Magenta, Grey};
	public final static int clr_num = 8;

	private static JFrame frame;
	private static Point3d eye = new Point3d(1.35, 0.35, 2.0);

	/* a function to create a rotation behavior and refer it to 'my_TG' */
	public static RotationInterpolator rotateBehavior(int r_num, TransformGroup my_TG) {

		my_TG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D yAxis = new Transform3D();
		Alpha rotationAlpha = new Alpha(-1, r_num);
		RotationInterpolator rot_beh = new RotationInterpolator(
				rotationAlpha, my_TG, yAxis, 0.0f, (float) Math.PI * 2.0f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		rot_beh.setSchedulingBounds(bounds);
		return rot_beh;
	}
	
	/* a function to position viewer to 'eye' location */
	public static void defineViewer(SimpleUniverse su) {

	    TransformGroup viewTransform = su.getViewingPlatform().getViewPlatformTransform();
		Point3d center = new Point3d(0, 0, 0);               // define the point where the eye looks at
		Vector3d up = new Vector3d(0, 1, 0);                 // define camera's up direction
		Transform3D view_TM = new Transform3D();
		view_TM.lookAt(eye, center, up);
		view_TM.invert();  
	    viewTransform.setTransform(view_TM);                 // set the TransformGroup of ViewingPlatform
	}

	/* a function to build the content branch and attach to 'scene' */
	private static BranchGroup createScene() {
		BranchGroup scene = new BranchGroup();
		
		TransformGroup content_TG = new TransformGroup();    // create a TransformGroup (TG)
		content_TG.addChild(new ColorCube(0.4f));
		scene.addChild(content_TG);	                         // add TG to the scene BranchGroup
		scene.addChild(rotateBehavior(10000, content_TG));   // make TG continuously rotating 
		
		return scene;
	}
	
	public static void setEye(Point3d eye_position) {
		eye = eye_position;
	}

	/* a constructor to set up and run the application */
	public Commons(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas_3D = new Canvas3D(config);
		SimpleUniverse su = new SimpleUniverse(canvas_3D);   // create a SimpleUniverse
		defineViewer(su);                                    // set the viewer's location

		sceneBG.compile();
		su.addBranchGraph(sceneBG);                          // attach the scene to SimpleUniverse
		
		setLayout(new BorderLayout());
		add("Center", canvas_3D);		
		frame.setSize(600, 600);                             // set the size of the JFrame
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		frame = new JFrame("XY's Commons");                  // call constructor with 'createScene()'
		frame.getContentPane().add(new Commons(createScene()));    
	}
	
	public static class MyGUI extends JFrame {
		private static final long serialVersionUID = 1L;
		public MyGUI(BranchGroup branchGroup, String title) {
			frame = new JFrame(title);                       // call constructor with 'branchGroup' 
			frame.getContentPane().add(new Commons(branchGroup));
			pack();
		}		
	}

}