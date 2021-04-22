package main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;


public class Taskbar extends JPanel implements MouseListener {
	
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
	
	private static TransformGroup txt(String txt, float scl, Color3f clr, Point3f p) {
		
    	Font my2DFont = new Font("New Tegomin", Font.BOLD, 1);
    	FontExtrusion myExtrude = new FontExtrusion();
    	Font3D font3D = new Font3D(my2DFont, myExtrude);
    	Text3D text3D = new Text3D(font3D,txt,p);
  
        Transform3D scalar = new Transform3D();
        scalar.setScale(scl);
        Appearance look = new Appearance();
        look.setMaterial(new Material(clr, clr, clr, clr, 1));
        TransformGroup text = new TransformGroup(scalar);
        Shape3D s2 = new Shape3D(text3D, look);
        text.addChild(s2);
        
        return text;
    }
	
	private static TransformGroup txt1(String txt, float scl, Color3f clr, Point3f p) {
		
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
        text.addChild(s2);
        
        return text;
    }
	
	private static TransformGroup frame(Point3f[] verts, Color3f clr) {
		
    	// Base Quadarray for the front side 
    	QuadArray sqr = new QuadArray(4, GeometryArray.NORMALS| GeometryArray.COORDINATES | GeometryArray.COLOR_3);
    	sqr.setCoordinates(0, verts);
    	// 1/sqrt(3) as there are 3 normals 
    	float normal = (float)(1/Math.sqrt(3));
    	float [] f1 = {normal,normal,normal};
    	Appearance app = new Appearance();
    	TransparencyAttributes T1 = new TransparencyAttributes(TransparencyAttributes.NICEST,0.5f);
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
	
	public static TransformGroup taskboard() {
		TransformGroup tg = new TransformGroup();
		Point3f [] verts = {  new Point3f(-4.05f,2f,0f), new Point3f(-4.05f,0f,0f), new Point3f(-1.4f,0f,0f),new Point3f(-1.4f,2f,0f)};
		tg.addChild(frame(verts,Commons.Grey));
		Point3f pos = new Point3f(-20f,8f,0f);
		tg.addChild(txt("Clues to Escape the Room",0.2f,Commons.White,pos));
		Point3f pos1 = new Point3f(-19.5f,6f,0f);
		tg.addChild(txt1("• Find and complete",0.2f,Commons.White,pos1));
		Point3f pos2 = new Point3f(-19.5f,5f,0f);
		tg.addChild(txt1("  the cube puzzle",0.2f,Commons.White,pos2));
		Point3f pos3 = new Point3f(-19.5f,4f,0f);
		tg.addChild(txt1("• Find and complete",0.2f,Commons.White,pos3));
		Point3f pos4 = new Point3f(-19.5f,3f,0f);
		tg.addChild(txt1("  the Dial Puzzle",0.2f,Commons.White,pos4));
		Point3f pos5 = new Point3f(-19.5f,2f,0f);
		tg.addChild(txt1("• Find the painting of a",0.2f,Commons.White,pos5));
		Point3f pos6 = new Point3f(-19.5f,1f,0f);
		tg.addChild(txt1("  spacex rocket",0.2f,Commons.White,pos6));
		/*
		Point3f pos7 = new Point3f(-19.5f,0f,0f);
		tg.addChild(txt1("(Check the orbes on the door",0.1f,CommonsNP.White,pos7));
		Point3f pos8 = new Point3f(-19.5f,-1f,0f);
		tg.addChild(txt1("to measure your progress)",0.1f,CommonsNP.White,pos8));
		*/
		
		return tg;
	}
	
	public static BranchGroup createScene() {
		BranchGroup scene = new BranchGroup(); 
		               
		// Calling the function to check its functionality
		scene.addChild(taskboard());
		pickTool = new PickTool( scene );                   // allow picking of objs in 'sceneBG'
		pickTool.setMode(PickTool.BOUNDS);	
		//
		//addSound();
		//
		addLights(scene,Commons.White);
		scene.compile(); // optimize scene BG

		return scene;
	}
	
	public Taskbar(BranchGroup sceneBG) {
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
		frame.setSize(1000,700);                              // set the size of the JFrame
		frame.setVisible(true);
	}

	/* the main entrance of the application */
	public static void main(String[] args) {
		frame = new JFrame("Taskbar");
		frame.getContentPane().add(new Taskbar(createScene())); 
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
			
		           
		} 
}
}