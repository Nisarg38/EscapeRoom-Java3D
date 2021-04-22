package main;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;

import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.*;


public class DoorIndicator extends JPanel {
	
	public int selection = 0;
	
	private static NetEscapeRoom thisFBF;
	private static int pid;
	
	private static final long serialVersionUID = 1L;
	
	static boolean sunAttained = false;
	static boolean moonAttained = true;
	static boolean jupiterAttained = false;
	static boolean neptuneAttained = false;
	
	private static Sphere sun = createSphere(0.25f, 80, Commons.Orange, "galaxy.jpg");
	private static Sphere jupiter = createSphere(0.25f, 80, Commons.Orange, "galaxy.jpg");
	private static Sphere moon = createSphere(0.25f, 80, Commons.Green, "galaxy.jpg");
	private static Sphere neptune = createSphere(0.25f, 80, Commons.Orange, "galaxy.jpg");
	
	/**
	 * Method which creates a material to be applied to the faces of a cube
	 * @return material to be used
	 */
	private static Material setMaterial(Color3f diffuseColor) {
		
		int shine = 10;
		
		Material material = new Material();
		
		material.setAmbientColor(new Color3f(0.6f, 0.6f, 0.6f));
		material.setEmissiveColor(new Color3f(0.0f, 0.0f, 0.0f));
		material.setDiffuseColor(diffuseColor);
		material.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f));
		
		material.setShininess(shine);
		material.setLightingEnable(true);
		
		return material;
		
	}
	
	/**
	 * The following function is responsible for loading the specified texture given by the String path
	 * @return Texture2D object containing texture
	 */
	static Texture setTexture(String filename) {
		
		String path = "images/" + filename;
		TextureLoader tl = new TextureLoader(path, null);
		ImageComponent2D image = tl.getImage();
		
		if (image == null) {
			System.out.println("Load failed for texture: " + path);
		}
		
		Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image);
		
		return texture;
		
	}
	
	/**
	 * Method creates a sphere with specified parameters
	 * @param radius the radius of the sphere
	 * @param divisions number of divisions
	 * @param color the color of the sphere
	 * @return created Sphere object
	 */
	private static Sphere createSphere(float radius, int divisions, Color3f color, String texture) {
		
		Appearance app = new Appearance();
		
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		app.setPolygonAttributes(pa);
		
		TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.5f);
		app.setTransparencyAttributes(ta);
		
		app.setMaterial(setMaterial(color));
		app.setTexture(setTexture(texture));

		return new Sphere(radius, Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS | Sphere.ENABLE_APPEARANCE_MODIFY, divisions, app);
		
	}
	
	static Appearance setAppearance(Color3f attributesColor, Color3f materialColor, String texture) {

		Appearance app = new Appearance();
		
		//Creating and setting PolygonAttributes
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		app.setPolygonAttributes(pa);
		
		//Creating and setting ColoringAttributes
		ColoringAttributes ca = new ColoringAttributes(attributesColor, ColoringAttributes.NICEST);
		app.setColoringAttributes(ca);
		
		//Setting the material of the Appearance object
		app.setMaterial(setMaterial(materialColor)); //Setting the material previously created to the Appearance object 
		app.setTexture(setTexture(texture));
		
		return app;
		
	}
	
	/**
	 * @param rotationNum the duration
	 * @param axisNum axis number signifying x or z
	 * @param pos position of BoundingSphere for schedulingBounds purposes
	 * @param tg the target TransformGroup 
	 * @return corresponding RotationInterpolator
	 */
	private static RotationInterpolator rotationInterpolator (int rotationNum, int axisNum, Point3d pos, TransformGroup tg) {
		
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		Transform3D axis = new Transform3D();
		
		//Determining whether rotating x or z (rotY is default rotation behavior)
		if (axisNum == 0) {
			axis.rotZ(Math.PI / 2);
		}
		
		if (axisNum == -1) {
			axis.rotZ(Math.PI / -2);
		}
		
		if (axisNum == 2) {
			axis.rotX(Math.PI / 2);
		}
		
		if (axisNum == -2) {
			axis.rotX(Math.PI / -2);
		}
		
		if (axisNum == 3) {
			axis.rotY(Math.PI / -2);
		}
		
		Alpha a = new Alpha(-1, rotationNum);
		RotationInterpolator ri = new RotationInterpolator(a, tg, axis, 0.0f, (float) Math.PI * 2.0f);
		BoundingSphere bounds = new BoundingSphere(pos, 100);
		ri.setSchedulingBounds(bounds);
		
		return ri;
		
	}
	
	private static Box doorBlock() {
		
		Appearance app = setAppearance(Commons.White, Commons.Blue, "doorStone.jpg");
		return new Box(0.5f, 1f, 0.1f, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, app);
	
	}
	
	private static TransformGroup keyHole() { 
		
		Appearance app = setAppearance(Commons.White, Commons.Blue, "keyHole.png");
		Box keyHole = new Box(0.15f, 0.2f, 0.20f, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, app);
		
		Transform3D translator = new Transform3D();
		translator.setTranslation(new Vector3f(0.25f, 0, 0));
		
		TransformGroup keyBlock = new TransformGroup(translator);
		keyBlock.addChild(keyHole);
		
		return keyBlock;
		
	}
	
	private static void createIndicators(TransformGroup sceneTG) {
		
		//First indicator creating and positioning
			Transform3D translate1 = new Transform3D();
			Vector3d vec1 = new Vector3d(0.25f, 0.60f, 0.0f);
			translate1.setTranslation(vec1);
		
			TransformGroup indicate1 = new TransformGroup(translate1);
			TransformGroup indicate1Rotate = new TransformGroup();
			
			indicate1Rotate.addChild(moon);
			indicate1.addChild(indicate1Rotate);
			
			indicate1.addChild(rotationInterpolator(1000, 0, new Point3d(vec1), indicate1Rotate));
			sceneTG.addChild(indicate1);
			
		//Second indicator creating and positioning
			Transform3D translate2 = new Transform3D();
			Vector3d vec2 = new Vector3d(0.25f, -0.60f, 0.0f);
			translate2.setTranslation(vec2);
		
			TransformGroup indicate2 = new TransformGroup(translate2);
			TransformGroup indicate2Rotate = new TransformGroup();
			
			indicate2Rotate.addChild(sun);
			indicate2.addChild(indicate2Rotate);
			
			indicate2.addChild(rotationInterpolator(1000, -1, new Point3d(vec2), indicate2Rotate));
			sceneTG.addChild(indicate2);
		
		//Third indicator creating and positioning
			Transform3D translate3 = new Transform3D();
			Vector3d vec3 = new Vector3d(-0.25f, 0.3f, 0.0f);
			translate3.setTranslation(vec3);
		
			TransformGroup indicate3 = new TransformGroup(translate3);
			TransformGroup indicate3Rotate = new TransformGroup();
			
			indicate3Rotate.addChild(neptune);
			indicate3.addChild(indicate3Rotate);
			
			indicate3.addChild(rotationInterpolator(1000, 3, new Point3d(vec3), indicate3Rotate));
			sceneTG.addChild(indicate3);
			
		//Four indicator creating and positioning
			Transform3D translate4 = new Transform3D();
			Vector3d vec4 = new Vector3d(-0.25f, -0.3f, 0.0f);
			translate4.setTranslation(vec4);
		
			TransformGroup indicate4 = new TransformGroup(translate4);
			TransformGroup indicate4Rotate = new TransformGroup();
			
			indicate4Rotate.addChild(jupiter);
			indicate4.addChild(indicate4Rotate);
			
			indicate4.addChild(rotationInterpolator(1000, 1, new Point3d(vec3), indicate4Rotate));
			sceneTG.addChild(indicate4);
		
	}
	
	static TransformGroup doorModel (float scl, Vector3f vec) {
		
		Transform3D scaler = new Transform3D();
		scaler.set(scl, vec);
		
		TransformGroup door = new TransformGroup(scaler);
		door.addChild(doorBlock());
		door.addChild(keyHole());
		
		createIndicators(door);
		
		return door;
		
	}
	
	static void setJupiter(boolean value) {
		jupiterAttained = value;
	}
	
	static void setNeptune(boolean value) {
		neptuneAttained = value;
	}
	
	static void setSun(boolean value) {
		sunAttained = value;
	}
	
	static void checkJupiter() {
	
		if (jupiterAttained) {
			Appearance app = new Appearance();
			app.setTexture(setTexture("jupiter.jpg"));
			jupiter.setAppearance(app);
		}
		
	}
	
	static void checkNeptune() {
		
		if (neptuneAttained) {
			Appearance app = new Appearance();
			app.setTexture(setTexture("neptune.jpg"));
			neptune.setAppearance(app);
		}
		
	}
	
	static void checkSun() {
		
		if (sunAttained) {
			Appearance app = new Appearance();
			app.setTexture(setTexture("sun.jpg"));
			sun.setAppearance(app);
		}
		
	}
	
	static void checkMoon() {
		
		if (moonAttained) {
			Appearance app = new Appearance();
			app.setTexture(setTexture("moon.jpg"));
			moon.setAppearance(app);
		}
		
	}
	
	static void checkIFcomplete() {
		if(jupiterAttained && neptuneAttained && sunAttained && moonAttained) {
			thisFBF.gameWon(pid);
			//thisFBF.setStatus("Winner!");
		}
	}
	
	/* A function to position viewer to 'eye' location */
	private void defineViewer(SimpleUniverse simple_U, Point3d eye) {

	    TransformGroup viewTransform = simple_U.getViewingPlatform().getViewPlatformTransform();
		Point3d center = new Point3d(0, 0, 0);               // define the point where the eye looks at
		Vector3d up = new Vector3d(0, 1, 0);                 // define camera's up direction
		Transform3D view_TM = new Transform3D();
		view_TM.lookAt(eye, center, up);
		view_TM.invert();  
	    viewTransform.setTransform(view_TM);                 // set the TransformGroup of ViewingPlatform
	    
	}
	
	/* A function to allow key navigation with the ViewingPlateform */
	private KeyNavigatorBehavior keyNavigation(SimpleUniverse simple_U) {

		ViewingPlatform view_platfm = simple_U.getViewingPlatform();
		TransformGroup view_TG = view_platfm.getViewPlatformTransform();
		KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(view_TG);
		BoundingSphere view_bounds = new BoundingSphere(new Point3d(), 20.0);
		keyNavBeh.setSchedulingBounds(view_bounds);
		return keyNavBeh;
		
	}
	
	/* A function to add ambient light and a point light to 'sceneBG' */
	static void addLights(BranchGroup sceneBG, Color3f clr) {		
		
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		
		AmbientLight amLgt = new AmbientLight(new Color3f(0.2f, 0.2f, 0.2f));
		amLgt.setInfluencingBounds(bounds);
	
		Point3f pt  = new Point3f(2.0f, 2.0f, 2.0f);
		Point3f atn = new Point3f(1.0f, 0.0f, 0.0f);
		
		PointLight ptLight = new PointLight(clr, pt, atn);
		ptLight.setInfluencingBounds(bounds);
		
		sceneBG.addChild(amLgt);
		sceneBG.addChild(ptLight);
		
	}
	
	/* A function to build the content branch and attach to 'scene' */
	static BranchGroup createScene(BranchGroup sceneBG) {
		
		TransformGroup scenePG = new TransformGroup();    // create a TransformGroup (TG)
		scenePG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		scenePG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		sceneBG.addChild(scenePG);	                         // add TG to the scene BranchGroup
		//sceneBG.addChild(CodeAssign1.axisFrame(CommonsEK.Blue, 1.0f));
		
		
		/*
		 * Use addLight() for testing purpose ONLY
		 */
		//addLights(sceneBG, Commons.White);
		
		scenePG.addChild(doorModel(2, new Vector3f(0, 0, 0)));
		
		checkJupiter();
		checkNeptune();
		checkSun();
		checkMoon();
		
		return sceneBG;
	
	}
	
	/* A constructor to set up and run the application */
	public DoorIndicator(BranchGroup sceneBG, NetEscapeRoom fbf, int playerID) {
		
		createScene(sceneBG);
		
		thisFBF = fbf;
		pid = playerID;
		
		/*
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas_3D = new Canvas3D(config);
		SimpleUniverse su = new SimpleUniverse(canvas_3D);   // create a SimpleUniverse                                    
		defineViewer(su, new Point3d(2, 2, 6));    // set the viewer's location
		
		BranchGroup scene = new BranchGroup();			
		createScene(scene);                           // add contents to the scene branch
		scene.addChild(keyNavigation(su));                   // allow key navigation
		
		scene.compile();		                             // optimize the BranchGroup
		su.addBranchGraph(scene);                            // attach the scene to SimpleUniverse
		
		setLayout(new BorderLayout());
		add("Center", canvas_3D);		
		setVisible(true);
		*/
		
	}

	/*
	public static void main(String[] args) {
		JFrame frame = new JFrame("Door Indicator Model"); 
		frame.getContentPane().add(new DoorIndicator());         // create an instance of the class
		frame.setSize(600, 600);                             // set the size of the JFrame
		frame.setVisible(true);
	}
	*/
	
}