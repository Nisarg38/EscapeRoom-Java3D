package main;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;




public class lamp extends JPanel  {
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private Canvas3D canvas;
	private static PickTool pickTool;

	public static void addLights(BranchGroup sceneBG, Color3f clr) {
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		AmbientLight amLgt = new AmbientLight(new Color3f(0.2f, 0.2f, 0.2f));
		amLgt.setInfluencingBounds(bounds);
		sceneBG.addChild(amLgt);
		Point3f pt = new Point3f(2.0f, 2.0f, 2.0f);
		Point3f atn = new Point3f(1.0f, 0.0f, 0.0f);
		PointLight ptLight = new PointLight(clr, pt, atn);
		ptLight.setInfluencingBounds(bounds);
		sceneBG.addChild(ptLight);
	}

	private static Appearance setApp(Color3f clr) {
		Appearance app = new Appearance();
		// Decide to write setMaterial in this class as I implemented it differently before 
		app.setMaterial(setMaterial(clr));
		ColoringAttributes colorAtt = new ColoringAttributes();
		colorAtt.setColor(clr);
		app.setColoringAttributes(colorAtt);
		//app.setTexture(textureApp("CowTray"));
		return app;
	}
	
	public static Material setMaterial(Color3f clr) {
		int SH = 128;               // 10
		Material ma = new Material();
		Color3f c = new Color3f(0.6f*clr.x, 0.6f*clr.y, 0.6f*clr.z);
		ma.setAmbientColor(c);
		ma.setDiffuseColor(c);
		ma.setSpecularColor(clr);
		ma.setShininess(SH);
		ma.setLightingEnable(true);

		return ma;
	}
	
	public static BranchGroup loadShape() {
		int flags = ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY;
		ObjectFile f = new ObjectFile(flags);
		// Trying to load the obj, cow object here, to us and setting catch instances 
		// To find out whats wrong if it doesnt work 
		Scene s = null ; 
		try {
			s = f.load("images/lamp-1.obj");
		}catch(FileNotFoundException e){
			System.err.println(e);
			System.exit(1);
		}catch(ParsingErrorException e){
			System.err.println(e);
			System.exit(1);
		}catch(IncorrectFormatException e){
			System.err.println(e);
			System.exit(1);
		}
		// Representing scene onto a BranchGroup so we can add appearance to it 
		// Creating 3d shape and adding appearance property to it 
		BranchGroup objBG = s.getSceneGroup();
		Shape3D obj = (Shape3D) objBG.getChild(0);
		obj.setAppearance(setApp(Commons.Grey));
		return objBG;
	}
	
	public static TransformGroup lamp() {
		
		Transform3D t3D = new Transform3D();
		t3D.rotX(-Math.PI/2);
		TransformGroup tg = new TransformGroup(t3D);
		
		tg.addChild(loadShape());
		/*
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		Point3f atn = new Point3f(0, 0.0f, 1.0f);
		Point3f pt = new Point3f(-1f,-1f,0f);
		PointLight ptLight = new PointLight(Commons.Blue, pt, atn);
		ptLight.setInfluencingBounds(bounds);
		tg.addChild(ptLight);
		*/
		return tg;
		
	}


	public static BranchGroup obj() {
		BranchGroup scene = new BranchGroup();
		TransformGroup tg = new TransformGroup();
		//paintRocket(scene);
		
		tg.addChild(lamp());

		scene.addChild(tg);
		return scene;
	}

	public static BranchGroup createScene() {
		BranchGroup scene = new BranchGroup();

		// Calling the function to check its functionality
		scene.addChild(obj());

		addLights(scene, Commons.White);
		scene.compile(); // optimize scene BG

		return scene;
	}

	public lamp(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);

		SimpleUniverse su = new SimpleUniverse(canvas); // create a SimpleUniverse
		Commons.setEye(new Point3d(0, 0, 3));
		Commons.defineViewer(su); // set the viewer's location

		sceneBG.compile();
		su.addBranchGraph(sceneBG); // attach the scene to SimpleUniverse

		setLayout(new BorderLayout());
		add("Center", canvas);
		frame.setSize(600, 600); // set the size of the JFrame
		frame.setVisible(true);
	}

	/* the main entrance of the application */
	public static void main(String[] args) {
		frame = new JFrame("XY's Assignment 4");
		frame.getContentPane().add(new lamp(createScene()));
	}


}