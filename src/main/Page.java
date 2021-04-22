package main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

public class Page extends JPanel implements MouseListener {
	
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private Canvas3D canvas;
	private static PickTool pickTool;  
	
	public static void addLights(BranchGroup sceneBG, Color3f clr) {		
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		AmbientLight amLgt = new AmbientLight(new Color3f(0.2f, 0.2f, 0.2f));
		amLgt.setInfluencingBounds(bounds);
		sceneBG.addChild(amLgt);
		Point3f pt  = new Point3f(2.0f, 2.0f, 2.0f);
		Point3f atn = new Point3f(1.0f, 0.0f, 0.0f);
		PointLight ptLight = new PointLight(clr, pt, atn);
		ptLight.setInfluencingBounds(bounds);
		sceneBG.addChild(ptLight);
		Background bg = new Background();	
		bg.setImage(new TextureLoader("images/escape-room.jpeg",null).getImage());		
    	bg.setImageScaleMode(Background.SCALE_FIT_ALL);							
    	bg.setApplicationBounds(bounds);
    	bg.setColor(clr);	

    	sceneBG.addChild(bg);
    	
	}
	
	public static Appearance createAppearance() {
		Appearance Appear = new Appearance();
		
 		TexCoordGeneration tcg = new TexCoordGeneration();
		tcg.setEnable(false);

		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);
		PolygonAttributes polyAttrib = new PolygonAttributes();
		polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
		Appear.setPolygonAttributes(polyAttrib);
		
		return Appear;
	}

	public void mouseEntered(MouseEvent arg0) { }
	public void mouseExited(MouseEvent arg0) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	
	private static TransformGroup txt1(String txt, float scl, Color3f clr, Point3f p, String click) {
    	Font my2DFont = new Font("New Tegomin", Font.PLAIN, 1);
    	FontExtrusion myExtrude = new FontExtrusion();
    	Font3D font3D = new Font3D(my2DFont, myExtrude);
    	Text3D text3D = new Text3D(font3D,txt,p);
  
        Transform3D scalar = new Transform3D();
        scalar.setScale(scl);
        Appearance look = new Appearance();
        look.setMaterial(new Material(clr, clr, clr, clr, 1));
        TransformGroup text = new TransformGroup(scalar);
        Shape3D s2 = new Shape3D(text3D, look);
        s2.setUserData(0);
        s2.setName(click);
        text.addChild(s2);
        
        return text;
    }
	
	private static TransformGroup button(Point3f[] verts, Color3f clr) {
    	// Base Quadarray for the front side 
    	QuadArray sqr = new QuadArray(4, GeometryArray.NORMALS| GeometryArray.COORDINATES | GeometryArray.COLOR_3);
    	sqr.setCoordinates(0, verts);
    	// 1/sqrt(3) as there are 3 normals 
    	float normal = (float)(1/Math.sqrt(3));
    	float [] f1 = {normal,normal,normal};
    	Appearance app = new Appearance();
    	TransparencyAttributes T1 = new TransparencyAttributes(TransparencyAttributes.NICEST,0.9f);
    	for(int i =0; i<4;i++) {
    		sqr.setColor(i, clr);
    		sqr.setNormal(i, f1);
    	}
    	
    	Shape3D FrontF = new Shape3D(sqr);
    	app.setTransparencyAttributes(T1);
    	FrontF.setAppearance(app);
    	TransformGroup tg = new TransformGroup();
    	tg.addChild(FrontF);
    	Transform3D scence = new Transform3D();
    	TransformGroup stg = null;
    	scence.setTranslation(new Vector3f(0,0,0.4f));
    	stg = new TransformGroup(scence);
    	
    	tg.addChild(stg);
    	return tg;
    	
    }
	
	
	private static TransformGroup front() {
    	
    	TransformGroup stg = new TransformGroup();
    	//Writing Instruction 
    	// With txt() we are inputing the text string, scale of the text, color of the text,
    	// Point3f of the text and "no-effect" is for mouse click action, to identify the shape
    	Point3f [] verts = {  new Point3f(-1.5f,-1.5f,0f), new Point3f(-1.5f,-2f,0f), new Point3f(-0.5f,-2f,0f),new Point3f(-0.5f,-1.5f,0f)};
		stg.addChild(button(verts,Commons.Red));
    	Point3f b1 = new Point3f((float) -5,(float) -6,0);
    	stg.addChild(txt1("START",0.3f,Commons.Red,b1,"start"));
    	
    	
    	Point3f [] verts1 = {   new Point3f(0.5f,-1.5f,0f), new Point3f(0.5f,-2f,0f), new Point3f(1.5f,-2f,0f),new Point3f(1.5f,-1.5f,0f)};
		stg.addChild(button(verts1,Commons.Red));
    	Point3f b2 = new Point3f((float) 2,(float) -6,0);
    	stg.addChild(txt1("EXIT",0.3f,Commons.Red,b2,"stop"));

    	return stg;
    }

	
	/*
	public static void initialSound() {
		soundJOAL = new SoundUtilityJOAL();
		if (!soundJOAL.load("intro4", 0f, 0f, 10f, true))
			System.out.println("Could not load " + "intro");	
		}

	public static void playSound(int key) {
		String snd_pt = "intro4";
		if (key > 1)
			snd_pt = "intro4";
		soundJOAL.play(snd_pt);
		try {
			Thread.sleep(500); // sleep for 0.5 secs
		} catch (InterruptedException ex) {}
		soundJOAL.stop(snd_pt);
	}
	*/
	
	public static BranchGroup createScene() {
		BranchGroup scene = new BranchGroup(); 
		               
		// Calling the function to check its functionality
		scene.addChild(front());
		pickTool = new PickTool( scene );                   // allow picking of objs in 'sceneBG'
		pickTool.setMode(PickTool.BOUNDS);	

		//soundJOAL = new SoundUtilityJOAL();
		//soundJOAL.load("cow", 0f, 0f, 10f, true);    // fix 'snd_pt' at cow location
		//soundJOAL.play("cow");
		
		addLights(scene,Commons.White);
		scene.compile(); // optimize scene BG

		return scene;
	}
	
	public Page(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);                        
		canvas.addMouseListener(this);                        // NOTE: enable mouse clicking 

		SimpleUniverse su = new SimpleUniverse(canvas);       // create a SimpleUniverse
		Commons.setEye(new Point3d(0, 0, 10.0));
		Commons.defineViewer(su);                           // set the viewer's location
	
		
		sceneBG.compile();
		su.addBranchGraph(sceneBG);                           // attach the scene to SimpleUniverse
		
		setLayout(new BorderLayout());
		add("Center", canvas);		
		frame.setSize(1000, 700);                              // set the size of the JFrame
		frame.setVisible(true);
	}

	/* the main entrance of the application */
	public static void main(String[] args) {
		frame = new JFrame("XY's Assignment 4");
		frame.getContentPane().add(new Page(createScene())); 
	}
	
	public void mouseClicked(MouseEvent event) {
		int x = event.getX(); int y = event.getY();           // mouse coordinates
		Point3d point3d = new Point3d(), center = new Point3d();
		canvas.getPixelLocationInImagePlate(x, y, point3d);   // obtain AWT pixel in ImagePlate coordinates
		canvas.getCenterEyeInImagePlate(center);              // obtain eye's position in IP coordinates
		
		Transform3D transform3D = new Transform3D();          // matrix to relate ImagePlate coordinates~
		canvas.getImagePlateToVworld(transform3D);            // to Virtual World coordinates
		transform3D.transform(point3d);                       // transform 'point3d' with 'transform3D'
		transform3D.transform(center);                        // transform 'center' with 'transform3D'

		Vector3d mouseVec = new Vector3d();
		mouseVec.sub(point3d, center);
		mouseVec.normalize();
		pickTool.setShapeRay(point3d, mouseVec);              // send a PickRay for intersection
		
		if (pickTool.pickClosest() != null) {
			PickResult pickResult = pickTool.pickClosest();   // obtain the closest hit
			Shape3D shape = (Shape3D)pickResult.getNode(PickResult.SHAPE3D);
			if ((int) shape.getUserData() == 0) {               // retrieve 'UserData'
				// Change according to when the mouse is clicked or which sphere its clicked on 
				// We differentiate based on the name 
				if(shape.getName() == "start"){ 
					 // Using System.out to keep track of the actions of the program and check for breakpoints
					System.out.println("START");
				}else if(shape.getName() == "stop"){ 
					 // Using System.out to keep track of the actions of the program and check for breakpoints
					System.out.println("STOP");
					System.exit(0);
				}
			}
		           
		} 
}
}